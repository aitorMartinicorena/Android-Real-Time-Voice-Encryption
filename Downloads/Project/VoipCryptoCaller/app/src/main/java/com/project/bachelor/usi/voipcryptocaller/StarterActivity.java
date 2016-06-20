package com.project.bachelor.usi.voipcryptocaller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class StarterActivity extends AppCompatActivity {
    protected final static String DATA1 = "com.project.bachelor.usi.voipcryptocaller.DATA1";
    protected final static String DATA2 = "com.project.bachelor.usi.voipcryptocaller.DATA2";
    protected final static String DATA3 = "com.project.bachelor.usi.voipcryptocaller.DATA3";
    protected final static String EXTRA_ERR = "com.project.bachelor.usi.voipcryptocaller.ERR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter);

        Button buttonNumber = (Button) findViewById(R.id.toNumber);
        buttonNumber.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                changeActivityNumber(v);
            }
        });

        Button buttonList = (Button) findViewById(R.id.toList);
        buttonList.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                changeActivityList(v);
            }
        });

        Button buttonGet = (Button) findViewById(R.id.toGetCall);
        buttonGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivityGet(v);
            }
        });

        String err;
        err = getIntent().getStringExtra(EXTRA_ERR);
        if(err != null)
            showError(err);
        Toast.makeText(getApplicationContext(),"Welcome",Toast.LENGTH_LONG).show();
    }

    protected void changeActivityList(View view){
        final String data2 = getIntent().getStringExtra(DATA2);
        Intent i = new Intent(getBaseContext(),ListActivity.class);
        i.putExtra(DATA2,data2);
        startActivity(i);
    }

    protected void changeActivityNumber(View view){
        final String data2 = getIntent().getStringExtra(DATA2);
        Intent i = new Intent(getBaseContext(),NumberActivity.class);
        i.putExtra(DATA2,data2);
        startActivity(i);
    }

    protected void changeActivityGet(View view){
        final String data2 = getIntent().getStringExtra(DATA2);
        Intent i = new Intent(getBaseContext(),GetCallActivity.class);
        i.putExtra(DATA2,data2);
        startActivity(i);
    }

    private void showError(String errorName){
        Toast.makeText(getApplicationContext(),errorName,Toast.LENGTH_LONG).show();
    }
}