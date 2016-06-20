package com.project.bachelor.usi.voipcryptocaller;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NumberActivity extends AppCompatActivity {
    protected EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        text = (EditText) findViewById(R.id.numberText);
        text.setRawInputType(Configuration.KEYBOARD_12KEY);

        Button buttonCall = (Button) findViewById(R.id.toCall);
        buttonCall.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeActivityCall(v);
            }
        });
    }

    protected void changeActivityCall(View view){
        Intent intent = new Intent(this, CallingActivity.class);
        String message = text.getText().toString();
        intent.putExtra(StarterActivity.DATA3,message);
        message = getIntent().getStringExtra(StarterActivity.DATA2);
        intent.putExtra(StarterActivity.DATA2,message);
        startActivity(intent);
    }
}
