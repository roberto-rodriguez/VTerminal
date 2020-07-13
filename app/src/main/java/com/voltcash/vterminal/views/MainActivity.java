package com.voltcash.vterminal.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.views.auth.AuthTerminalActivity;
import com.voltcash.vterminal.views.auth.LoginActivity;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.PreferenceUtil;


public class MainActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        PreferenceUtil.loadFromFile(this);

        String serialNumber = PreferenceUtil.read(Field.AUTH.TERMINAL_SERIAL_NUMBER);

        Intent activity;

        Log.i("MainActivity", "serialNumber = " + serialNumber );

        if(serialNumber == null){
            activity = new Intent(getApplicationContext(), AuthTerminalActivity.class);
        }else{
            activity = new Intent(getApplicationContext(), LoginActivity.class);
        }

        startActivity(activity);
    }


}
