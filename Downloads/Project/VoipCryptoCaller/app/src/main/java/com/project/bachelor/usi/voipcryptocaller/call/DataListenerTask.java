package com.project.bachelor.usi.voipcryptocaller.call;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.AsyncTask;

import java.net.DatagramSocket;
import java.security.MessageDigest;

public class DataListenerTask extends AsyncTask<Void, Void, Void> {

    boolean listening = true;
    private DatagramSocket s;
    private Listener listener;
    private static byte[] ivBytes;
    private byte[] clear;
    private int samplerate = getBestSampleRate();

    public DataListenerTask(DatagramSocket socket, byte[] iv){
        s = socket;
        this.ivBytes = iv;
    }

    public DataListenerTask(DatagramSocket socket, String iv, String clear){
        s = socket;
        this.ivBytes = iv.getBytes();
        this.clear = clear.getBytes();
    }

    public DataListenerTask(DatagramSocket socket, byte[] iv, byte[] clear){
        s = socket;
        this.ivBytes = iv;
        this.clear = clear;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        listener = new Listener(s,samplerate,clear,ivBytes);
        while(!isCancelled()) {
            listener.playRecord();
        }
        return null;
    }

    @Override
    protected void onCancelled() {
        listener.end();
        super.onCancelled();
    }

    public static byte[] sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            System.out.println("Hash " + hash + " with length " + hash.length);
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