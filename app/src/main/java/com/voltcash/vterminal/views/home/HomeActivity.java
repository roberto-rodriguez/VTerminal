package com.voltcash.vterminal.views.home;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.voltcash.vterminal.R;
import com.voltcash.vterminal.views.tx.TxActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().hide();
    }

    protected void onProcessCheck(View view){
        Intent txActivity = new Intent(getApplicationContext(), TxActivity.class);
        startActivity(txActivity);
    }
}
