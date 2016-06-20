package com.project.bachelor.usi.voipcryptocaller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;


public class ListActivity extends AppCompatActivity {
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;
    Button bt;

    String[] projection = new String[] {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
            Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                    ContactsContract.Contacts.DISPLAY_NAME
    };

    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION =
            {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.LOOKUP_KEY,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER,
                    ContactsContract.CommonDataKinds.Phone._ID,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                    Build.VERSION.SDK_INT
                            >= Build.VERSION_CODES.HONEYCOMB ?
                            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                            ContactsContract.Contacts.DISPLAY_NAME

            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        LinearLayout layout = (LinearLayout) findViewById(R.id.layoutList);
        tryPermission();
        for(String[] contact : getContactArray()){
            bt = new Button(this);
            bt.setText(contact[0] + ", Phone: " + contact[1]);
            bt.setHint(contact[1]);
            bt.setWidth(layout.getWidth());
            bt.setBackgroundColor(layout.getDrawingCacheBackgroundColor());
            bt.setOnClickListener(new Button.OnClickListener(){
                String number = bt.getHint().toString();
                public void onClick(View v){
                    changeActivityCall(v, number);
                }
            });
            layout.addView(bt);
        }
    }

    private ArrayList<String[]> getContactArray(){
        tryPermission();
        ArrayList<String[]> contactList = new ArrayList<String[]>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(
                ContactsContract.Contacts.CONTENT_URI,
                projection,
                null,
                null,
                null
        );
        if(cur.getCount() > 0){
            while(cur.moveToNext()){
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if(Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0){
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id},null);
                    while(pCur.moveToNext()){
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactList.add(new String[]{name, phoneNo});
                    }
                    pCur.close();
                }
            }
        }
        return contactList;
    }

    private void tryPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        READ_CONTACTS_PERMISSIONS_REQUEST);
            }
        }
    }

    protected void changeActivityCall(View view, String number){
        String message = getIntent().getStringExtra(StarterActivity.DATA2);
        Intent intent = new Intent(this, CallingActivity.class);
        intent.putExtra(StarterActivity.DATA3,number);
        intent.putExtra(StarterActivity.DATA2,message);
        startService(intent);
    }
}