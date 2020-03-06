package com.voltcash.vterminal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.voltcash.vterminal.auth.AuthTerminalActivity;
import com.voltcash.vterminal.auth.LoginActivity;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.PreferenceUtil;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        String serialNumber = PreferenceUtil.read(getApplicationContext(), Field.AUTH.TERMINAL_SERIAL_NUMBER);

        Intent activity;

        if(serialNumber == null){
            activity = new Intent(getApplicationContext(), AuthTerminalActivity.class);
        }else{
            activity = new Intent(getApplicationContext(), LoginActivity.class);
        }

        startActivity(activity);
    }
}
