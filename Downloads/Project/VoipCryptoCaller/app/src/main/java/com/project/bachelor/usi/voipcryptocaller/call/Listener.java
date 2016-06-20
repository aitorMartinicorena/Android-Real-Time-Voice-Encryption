package com.project.bachelor.usi.voipcryptocaller.call;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import com.project.bachelor.usi.voipcryptocaller.security.Decryptor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Listener {
    AudioTrack audioTrack = null;
    DatagramSocket socket;
    boolean recording;
    int samplerate;
    int bufferSizeInBytes, encBufferSizeInBytes;
    private Decryptor dec;

    byte[] audioData;
    DatagramPacket packet;
    public Listener(DatagramSocket socket, int samplerate, byte[] clear, byte[] ivBytes){
        try {
            recording = true;
            this.socket = socket;
            this.samplerate = samplerate;
            bufferSizeInBytes = 11296;
            encBufferSizeInBytes = bufferSizeInBytes + 16;
            audioTrack = new AudioTrack(
                    AudioManager.STREAM_VOICE_CALL,
                    samplerate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSizeInBytes,
                    AudioTrack.MODE_STREAM);
            dec = new Decryptor(); dec.init(clear, ivBytes, "AES/CBC/PKCS5PADDING");
            audioData = new byte[encBufferSizeInBytes];
            packet = new DatagramPacket(audioData,encBufferSizeInBytes);
            audioTrack.play();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void playRecord(){
        byte[] audiodec = null;
        try {
                socket.receive(packet);
                audiodec = packet.getData();
                if(audiodec.length % 16 != 0){
                    //Error
                }
                else {
                    audiodec = dec.dec(audiodec);
                    audioTrack.write(audiodec,0,audiodec.length);
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    protected void end(){
        audioTrack.stop();
        audioTrack.release();
        socket.close();
    }
}