package com.voltcash.vterminal;

import android.app.Application;
import android.util.Log;

import com.zcs.sdk.DriverManager;

/**
 * Created by roberto.rodriguez on 3/18/2020.
 */

public class VTerminal extends Application {
   public static DriverManager DRIVER_MANAGER;

    @Override
    public void onCreate() {
        super.onCreate();

       DRIVER_MANAGER = DriverManager.getInstance();
    }

}
