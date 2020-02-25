package com.voltcash.vterminal.interfaces;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by roberto.rodriguez on 2/24/2020.
 */

public abstract class ServiceCallerActivity extends AppCompatActivity {

    public abstract void onServiceCallback(String serviceType, Boolean success);
}
