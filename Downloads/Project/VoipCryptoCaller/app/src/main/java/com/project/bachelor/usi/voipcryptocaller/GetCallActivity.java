package com.project.bachelor.usi.voipcryptocaller;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.project.bachelor.usi.voipcryptocaller.call.DataListenerTask;
import com.project.bachelor.usi.voipcryptocaller.call.DataTalkerTask;
import com.project.bachelor.usi.voipcryptocaller.security.ECDHManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Arrays;

public class GetCallActivity extends AppCompatActivity {

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    private int receiverPort = 9100;
    private DataTalkerTask talker = null;
    private DataListenerTask listener = null;
    protected TextView tv1;
    ServerTask server;
    private String data2;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int num = msg.what;
            if(num == 1){
                showCaller((String) msg.obj);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data2 = getIntent().getStringExtra(StarterActivity.DATA2);
        setContentView(R.layout.activity_get_call);
        tv1 = (TextView) findViewById(R.id.numberView);
        Button buttonHangUp = (Button) findViewById(R.id.hangupGet);
        buttonHangUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hangUp();
            }
        });
        server = new ServerTask();
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
            server.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            server.execute();
        }
    }

    private void initiateCall(byte[] key, String address) {
        try {
            DatagramSocket send = new DatagramSocket();
            DatagramSocket receive = new DatagramSocket(receiverPort);
            //--------------------------------------------------------------------------------------
            byte[] initVector = "RandomInitVector".getBytes();
            try {
                MessageDigest dg = MessageDigest.getInstance("SHA1");
                initVector = Arrays.copyOf(dg.digest(key),16);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            //--------------------------------------------------------------------------------------
            listener = new DataListenerTask(receive, initVector, key);
            talker = new DataTalkerTask(send,address,receiverPort, initVector, key);
            if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
                listener.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                talker.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                listener.execute(); talker.execute();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public class ServerTask extends AsyncTask<Void, Void, Void> {

        ServerSocket s;
        Socket socket;
        InputStream in;
        OutputStream out;

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                int server_port = 9100;
                s = new ServerSocket(server_port);
                socket = s.accept();
                out = socket.getOutputStream();
                in = socket.getInputStream();

                byte[] buffer = new byte[40];
                in.read(buffer);

                Message msg = Message.obtain();
                msg.arg1 = 100;
                msg.arg2 = 200;
                msg.what = 1;
                msg.obj = new String(buffer,"UTF-8");
                Bundle bundle = new Bundle();
                bundle.putString("foo", "bar");
                msg.setData(bundle);
                handler.sendMessage(msg);

                out.write("ConnectionOK".getBytes("UTF-8"));
                out.flush();
                byte[] key = executeProtocolServer(socket);
                initiateCall(key,socket.getInetAddress().getHostAddress());
            }
            catch(IOException e){
                e.printStackTrace();
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            super.onCancelled();
        }

        private byte[] executeProtocolServer(Socket socket){
            ECDHManager m = new ECDHManager();
            byte[] key = null;
            try {
                KeyPair kp = m.initiate();
                byte[] pk1 = m.savePublicKey(kp.getPublic());
                byte[] pk2 = new byte[pk1.length];

                //Receive
                in = socket.getInputStream();
                in.read(pk2);

                //Send
                out = socket.getOutputStream();

                out.write(pk1);

                key = m.doECDH("Server: ",m.savePrivateKey(kp.getPrivate()),pk2);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return key;
        }
    }

    private void showCaller(String number) {
        tv1.setText(number);
    }

    @Override
    protected void onDestroy(){
        listener.cancel(true); talker.cancel(true);
        super.onDestroy();
    }

    private void hangUp() {
        Intent intent = new Intent(getBaseContext(),StarterActivity.class);
        intent.putExtra(StarterActivity.DATA2,data2);
        startActivity(intent);
    }
}
