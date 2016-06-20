package com.project.bachelor.usi.voipcryptocaller;

import android.os.AsyncTask;

import com.project.bachelor.usi.voipcryptocaller.security.ECDHManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyPair;

public class ClientTask extends AsyncTask<Void, Void, byte[]> {

    private final String address;
    private final String ident;
    private OutputStream out;
    private InputStream in;
    private Socket s;
    private CallingActivity ca;

    ClientTask(String address, String ident, CallingActivity activity) {
        this.address = address;
        this.ident = ident;
        this.ca = activity;
    }

    @Override
    protected byte[] doInBackground(Void... params) {
        byte[] key = null;
        try {
            InetAddress local = InetAddress.getByName(address);
            int server_port = 9100;
            s = new Socket(local,server_port);

            out = s.getOutputStream();
            in = s.getInputStream();

            byte[] buffer = new byte[40];
            out.write(ident.getBytes("UTF-8"));
            out.flush();
            in.read(buffer);
            String received = new String(buffer,"UTF-8").substring(0,"ConnectionOK".length());
            if(received.equals("ConnectionOK")){
                ECDHManager m = new ECDHManager();

                final KeyPair kp = m.initiate();
                byte[] pk1 = m.savePublicKey(kp.getPublic());

                byte[] pk2 = new byte[pk1.length];

                out.write(pk1);
                while(in.available() <= 0) {}
                int count = in.read(pk2);
                key = m.doECDH("Client: ",m.savePrivateKey(kp.getPrivate()),pk2);
            }
            else {
                key = "NoConnection".getBytes("UTF-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return key;
    }

    @Override
    protected void onPostExecute(byte[] key){
        ca.obtainResult(key,address,s);
        super.onPostExecute(key);
    }
}