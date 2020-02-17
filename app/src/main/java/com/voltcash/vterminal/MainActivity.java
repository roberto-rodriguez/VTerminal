package com.voltcash.vterminal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.voltcash.vterminal.tx.TxActivity;


public class MainActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();


        Intent txActivity = new Intent(getApplicationContext(), TxActivity.class);
        startActivity(txActivity);

    }

//    protected void onCaptureClick(View view){
//        mProgressDialog = new ProgressDialog(this);
//        mProgressDialog.setTitle("Please wait");
//        mProgressDialog.setMessage("Initializing...");
//        mProgressDialog.show();
//
//        Intent intent = new Intent(this, CaptureActivity.class);
//        startActivity(intent);
//    }

    protected void onTerminalAuth(View view){
        Intent txActivity = new Intent(getApplicationContext(), TxActivity.class);
        startActivity(txActivity);
    }


}
