package com.project.bachelor.usi.voipcryptocaller;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.project.bachelor.usi.voipcryptocaller.call.DataListenerTask;
import com.project.bachelor.usi.voipcryptocaller.call.DataTalkerTask;

public class CallingActivity extends AppCompatActivity implements AsyncResponse{

    private static final String PREFS_NAME = "PrefsFileVOIPCC";
    private static int receiverPort = 9100;
    DataListenerTask listener;
    DataTalkerTask talker;
    ClientTask clientTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);
        Button hangUpButton = (Button) findViewById(R.id.hangUpCalling);
        hangUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hangUp();
            }
        });

        Intent intent = getIntent();
        String data1 = getSharedPreferences(PREFS_NAME,0).getString("ident","!!ERROR!!");
        String data2 = intent.getStringExtra(StarterActivity.DATA2);
        String data3 = intent.getStringExtra(StarterActivity.DATA3);

        Toast toast = Toast.makeText(getApplicationContext(), "Calling " + data3 + "...", Toast.LENGTH_LONG);
        toast.show();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            data1 = LoginActivity.bytesToHex(md.digest(data1.getBytes("UTF-8")));
            data2 = LoginActivity.bytesToHex(md.digest(data2.getBytes("UTF-8")));
            data3 = LoginActivity.bytesToHex(md.digest(data3.getBytes("UTF-8")));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String[] data = new String[]{data1,data2,data3,"communicate"};
        ServerConnectionTask connectionTask = new ServerConnectionTask(this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
            connectionTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,data);
        } else {
            connectionTask.execute(data);
        }
    }

    @Override
    public void processFinish(String output) {
        if(output.contains("Callee: ")){
            try {
                startConnection(output.substring("Callee: ".length()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            manageError(output);
        }
    }

    private void manageError(String error){
        String message = getIntent().getStringExtra(StarterActivity.DATA2);
        Intent intent = new Intent(this, StarterActivity.class);
        intent.putExtra(StarterActivity.EXTRA_ERR,error);
        intent.putExtra(StarterActivity.DATA2,message);
        startService(intent);
    }

    private void startConnection(String result){
        clientTask = new ClientTask(result,getSharedPreferences(PREFS_NAME,0).getString("ident","!!ERROR!!"),this);
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
            clientTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            clientTask.execute();
        }
    }

    protected void obtainResult(byte[] result, String address, Socket socket){
        try {
            if(new String(result,"UTF-8").equals("NoConnection")){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String something = getIntent().getStringExtra(StarterActivity.DATA2);
                Intent intent = new Intent(getBaseContext(),StarterActivity.class);
                intent.putExtra(StarterActivity.DATA2,something);
                intent.putExtra(StarterActivity.EXTRA_ERR,"Unable to establish call");
                startActivity(intent);
            }
            else {
                initiateCall(result, address);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
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
            listener = new DataListenerTask(receive,initVector, key);
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

    @Override
    protected void onDestroy(){
        listener.cancel(true); talker.cancel(true);
        super.onDestroy();
    }

    private void hangUp() {
        String data2 = getIntent().getStringExtra(StarterActivity.DATA2);
        Intent intent = new Intent(getBaseContext(),StarterActivity.class);
        intent.putExtra(StarterActivity.DATA2,data2);
        startActivity(intent);
    }
}