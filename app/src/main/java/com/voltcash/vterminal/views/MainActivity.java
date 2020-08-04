package com.voltcash.vterminal.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.services.AuthService;
import com.voltcash.vterminal.util.ViewUtil;
import com.voltcash.vterminal.views.auth.AuthTerminalActivity;
import com.voltcash.vterminal.views.auth.LoginActivity;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.PreferenceUtil;
import com.voltcash.vterminal.views.home.HomeActivity;


public class MainActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        PreferenceUtil.loadFromFile(this);

        String serialNumber = PreferenceUtil.read(Field.AUTH.TERMINAL_SERIAL_NUMBER);

        Boolean isError =  getIntent().getBooleanExtra("isError", false);

        Intent activity;

        Log.i("MainActivity", "serialNumber = " + serialNumber );

        if(isError){
            String clerkId = PreferenceUtil.read(Field.AUTH.CLERK_ID);
            String functionality= getIntent().getStringExtra("functionality");
            String errorMessage = getIntent().getStringExtra("errorMessage");

            AuthService.notifyIssue(serialNumber, clerkId, functionality, errorMessage);

            activity = new Intent(getApplicationContext(), HomeActivity.class);
            activity.putExtra("isError", true);
        }else{
            if(serialNumber == null){
                activity = new Intent(getApplicationContext(), AuthTerminalActivity.class);
            }else{
                activity = new Intent(getApplicationContext(), LoginActivity.class);
            }
        }

        startActivity(activity);
    }

    @Override
    public void onStop () {
//do your stuff here
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
