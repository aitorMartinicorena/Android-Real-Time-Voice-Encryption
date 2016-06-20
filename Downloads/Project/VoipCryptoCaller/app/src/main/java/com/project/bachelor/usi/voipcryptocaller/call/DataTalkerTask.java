package com.project.bachelor.usi.voipcryptocaller.call;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.AsyncTask;

import java.net.DatagramSocket;
import java.security.MessageDigest;

public class DataTalkerTask extends AsyncTask<Void, Void, Void> {

    private DatagramSocket socket;
    private String address;
    private int port;
    private Talker talker;
    private static byte[] ivBytes;
    private static byte[] clear;
    private int samplerate = getBestSampleRate();

    public DataTalkerTask(DatagramSocket socket, String address, int port, byte[] iv){
        this.socket = socket;
        this.address = address;
        this.port = port;
        this.ivBytes = iv;
    }

    public DataTalkerTask(DatagramSocket socket, String address, int port, String iv, String clear){
        this.socket = socket;
        this.address = address;
        this.port = port;
        this.ivBytes = iv.getBytes();
        this.clear = clear.getBytes();
    }

    public DataTalkerTask(DatagramSocket socket, String address, int port, byte[] iv, byte[] clear){
        this.socket = socket;
        this.address = address;
        this.port = port;
        this.ivBytes = iv;
        this.clear = clear;
    }

    @Override
    protected Void doInBackground(Void... params) {
        talker = new Talker(socket,samplerate,address,port,clear, ivBytes);
        while(!isCancelled()) {
            talker.startRecording();
        }
        return null;
    }

    @Override
    protected void onCancelled() {
        talker.end();
        super.onCancelled();
    }

    public static byte[] sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            return hash;
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    private int getBestSampleRate() {
        int r=44100;
        for (int rate : new int[] {
                44100,
                22050,
                16000,
                11025,
                8000
        }) {  // add the rates you wish to check against
            int bufferSize = AudioRecord.getMinBufferSize(rate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
            if (bufferSize > 0) {
                r= rate;
            }
        }
        return r;
    }
}

