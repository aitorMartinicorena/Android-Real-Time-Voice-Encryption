package com.project.bachelor.usi.voipcryptocaller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

public class LoginActivity extends AppCompatActivity implements AsyncResponse {

    private static final String PREFS_NAME = "PrefsFileVOIPCC";
    EditText auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!getSharedPreferences(PREFS_NAME,0).contains("ident")) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            settings.edit().clear().commit();
            startActivity(new Intent(getBaseContext(),RegisterActivity.class));
        }
        else {
            setContentView(R.layout.activity_login);

            auth = (EditText) findViewById(R.id.getPass);
            auth.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

            Button buttonCall = (Button) findViewById(R.id.toCheck);
            buttonCall.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String text = auth.getText().toString();
                    if (text != null)
                        connectToServer(auth.getText().toString());
                    else
                        showMessage("No password inserted");
                }
            });
        }
    }

    private void connectToServer(String s){
        try {
            ServerConnectionTask task = new ServerConnectionTask(this);
            MessageDigest dg = MessageDigest.getInstance("MD5");
            if (getOwnPhoneNumber().equals("!!ERROR!!")){
                showMessage("Error: missing value; try registering again");
            }
            String data1 = bytesToHex(dg.digest(getOwnPhoneNumber().getBytes("UTF-8")));
            s = bytesToHex(dg.digest(s.getBytes("UTF-8")));
            String data3 = getOwnIPAddress();
            String[] data = new String[]{data1,s,data3,"login"};
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
        if((output.equals("OK login without up")) || (output.equals("OK login without up")) || (output.equals("OK login WITH update"))) {
            Intent i = new Intent(getBaseContext(),StarterActivity.class);
            i.putExtra(StarterActivity.DATA2,auth.getText().toString());
            startActivity(i);
        }
        else {
            auth.getText().clear();
            if (output.equals("Error in connection")) {
                showMessage("Connection error. Try again in a few moments");
            }
            else {
                showMessage("Bad parameters");
            }
        }
    }

    private String getOwnPhoneNumber(){
        return getSharedPreferences(PREFS_NAME,0).getString("ident","!!ERROR!!");
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

    private void showMessage(String s) {
        Toast.makeText(getApplicationContext(), s,
                Toast.LENGTH_LONG).show();
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

