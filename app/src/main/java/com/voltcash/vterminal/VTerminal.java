package com.voltcash.vterminal;

import android.app.Application;
import android.util.Log;

import com.pax.poslink.POSLinkAndroid;

/**
 * Created by roberto.rodriguez on 3/18/2020.
 */

public class VTerminal extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("AAAAAAAAAAAAAAAAAAAAAA", "VTerminal -> onCreate");

     POSLinkAndroid.init(getApplicationContext());
    }

}
