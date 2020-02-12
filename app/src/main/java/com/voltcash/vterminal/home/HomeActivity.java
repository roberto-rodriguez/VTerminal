package com.voltcash.vterminal.home;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.voltcash.vterminal.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setTitle("Voltcash Terminal");
    }
}
