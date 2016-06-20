package com.project.bachelor.usi.voipcryptocaller;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

class ServerConnectionTask extends AsyncTask<String[], Void, String> {

    public AsyncResponse delegate = null;
    private String response;
    //private final byte[] goalHost = {(byte) 192,(byte) 168,(byte) 151,(byte) 197};
    private final byte[] goalHost = {(byte) 10, (byte) 63, (byte) 162, (byte) 173};

    ServerConnectionTask(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String[]... params) {

        if(params[0].length != 4){
            return "Bad request";
        }
        response = "";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            System.out.println("Not prepared for this distribution -- Incapable of testing");
        }
        else {
            HttpClient httpClient = new DefaultHttpClient();
            String address = null;
            try {
                address = InetAddress.getByAddress(goalHost).getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            System.out.println("Address: " + address);
            System.out.println("Action: " + params[0][3]);
            HttpPost httpPost = new HttpPost("http://"+address+"/index.php");
            try {
                List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
                nameValuePair.add(new BasicNameValuePair("data1", params[0][0]));
                nameValuePair.add(new BasicNameValuePair("data2", params[0][1]));
                nameValuePair.add(new BasicNameValuePair("data3", params[0][2]));
                nameValuePair.add(new BasicNameValuePair("data4", params[0][3]));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

            } catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
            try {
                HttpResponse httpResponse = httpClient.execute(httpPost);
                response = EntityUtils.toString(httpResponse.getEntity());
            } catch (ClientProtocolException e) {
                // Log exception
                e.printStackTrace();
                response = "Error in connection";
            } catch (IOException e) {
                // Log exception
                e.printStackTrace();
                response = "Error in connection";
            }
        }
        return response;
    }

    @Override
    protected void onPostExecute(final String result) {
        delegate.processFinish(result);
    }
}