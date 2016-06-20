package com.project.bachelor.usi.voipcryptocaller.call;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.project.bachelor.usi.voipcryptocaller.security.Encryptor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Talker {
    private AudioRecord audioRecord = null;
    private DatagramSocket socket;
    private String address;
    private int port;
    boolean recording;
    int samplerate;
    int bufferSizeInBytes, encBufferSizeInBytes;
    private Encryptor enc;

    public Talker(DatagramSocket socket, int samplerate, String address, int port, byte[] clear, byte[] ivBytes){
        try {
            recording = true;
            this.socket = socket;
            this.samplerate = samplerate;
            this.address = address;
            this.port = port;
            bufferSizeInBytes = 11296;
            encBufferSizeInBytes = bufferSizeInBytes + 16;
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    samplerate,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSizeInBytes);
            enc = new Encryptor(); enc.init(clear, ivBytes, "AES/CBC/PKCS5PADDING");
            audioRecord.startRecording();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void startRecording() {
        byte[] audioData = new byte[bufferSizeInBytes];
        byte[] audioDataEnc;
        try {
            DatagramPacket packet;
                audioRecord.read(audioData, 0, bufferSizeInBytes);
                audioDataEnc = enc.enc(audioData);
                packet = new DatagramPacket(audioDataEnc, 0, audioDataEnc.length, InetAddress.getByName(address), port);
                socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void end(){
        audioRecord.stop();
        audioRecord.release();
        socket.close();
    }
}