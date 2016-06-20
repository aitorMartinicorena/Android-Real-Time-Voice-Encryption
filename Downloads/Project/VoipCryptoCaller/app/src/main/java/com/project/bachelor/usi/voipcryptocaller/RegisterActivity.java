package com.project.bachelor.usi.voipcryptocaller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

public class RegisterActivity extends AppCompatActivity implements AsyncResponse {

    private static final String PREFS_NAME = "PrefsFileVOIPCC";
    EditText ed1, ed2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ed1 = (EditText) findViewById(R.id.newPhone);
        ed2 = (EditText) findViewById(R.id.newPass);
        ed2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        Button button1 = (Button) findViewById(R.id.toReg);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s1 = ed1.getText().toString();
                String s2 = ed2.getText().toString();
                if((s1 == null) || (s1.equals("") || (s2 == null) || (s2.equals("")))) {
                    showMessage("Parameters left");
                }
                else {
                    connectToServer(s1,s2);
                }
            }
        });
    }

    private void connectToServer(String newInfo1, String newInfo2) {
        try {
            ServerConnectionTask task = new ServerConnectionTask(this);
            MessageDigest dg = MessageDigest.getInstance("MD5");
            newInfo1 = bytesToHex(dg.digest(newInfo1.getBytes("UTF-8")));
            newInfo2 = bytesToHex(dg.digest(newInfo2.getBytes("UTF-8")));
            String data3 = getOwnIPAddress();
            String[] data = new String[]{newInfo1,newInfo2,data3,"register"};
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data);
            } else {
                task.execute(data);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processFinish(String output) {
        if(output.equals("OK register")) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("ident",ed1.getText().toString());
            editor.commit();
            Intent i = new Intent(getBaseContext(),StarterActivity.class);
            i.putExtra(StarterActivity.DATA2,ed2.getText().toString());
            startActivity(i);
        }
        else {
            ed1.getText().clear();
            ed2.getText().clear();
            if (output.equals("Error in connection")) {
                showMessage("Connection error. Try again in a few moments");
            }
            else {
                showMessage("Bad parameters");
            }
        }
    }

    private void showMessage(String s) {
        Toast.makeText(getApplicationContext(), s,
                Toast.LENGTH_LONG).show();
    }

    private String getOwnIPAddress(){
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
