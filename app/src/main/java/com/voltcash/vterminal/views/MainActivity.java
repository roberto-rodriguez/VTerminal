package com.voltcash.vterminal.views;

import android.app.Fragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.views.auth.AuthTerminalActivity;
import com.voltcash.vterminal.views.auth.LoginActivity;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.PreferenceUtil;
import com.voltcash.vterminal.views.lab.CardFragment;
import com.voltcash.vterminal.views.tx.TxBalanceFragment;
import com.voltcash.vterminal.views.tx.TxFragment;


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

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main_lab);
//
//     // Fragment fragment = new TxFragment();
//        //       Fragment fragment = new CardFragment();
//        Fragment fragment = new TxBalanceFragment();
//
//        getFragmentManager().beginTransaction().add(R.id.frame_container, fragment).commit();
//    }
}
