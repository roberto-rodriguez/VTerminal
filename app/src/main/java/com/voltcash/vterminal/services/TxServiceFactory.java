package com.voltcash.vterminal.services;

import android.support.v7.app.AppCompatActivity;

import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.interfaces.TxService;
import com.voltcash.vterminal.services.impl.TxServiceImpl;
import com.voltcash.vterminal.services.stub.TxServiceStub;
import com.voltcash.vterminal.util.Constants;
import com.voltcash.vterminal.util.Settings;
import com.voltcash.vterminal.util.TxData;
import com.voltcash.vterminal.util.TxField;
import com.voltcash.vterminal.util.ViewUtil;

/**
 * Created by roberto.rodriguez on 2/25/2020.
 */

public class TxServiceFactory{

    private static TxService service;

    static{
        if(Settings.ENV == Constants.ENV_LOCAL){
            service = new TxServiceStub();
        }else{
            service = new TxServiceImpl();
        }
    }

    public static void checkAuthLocationConfig(AppCompatActivity caller, ServiceCallback callback) {
        try {
            service.checkAuthLocationConfig(caller, callback);
        }catch(Exception e){
            ViewUtil.showError(caller, "Error", e.getMessage());
        }
    }

    public static void tx(AppCompatActivity caller, ServiceCallback callback) {
        try {
            service.tx(caller, callback);
        }catch(Exception e){
            ViewUtil.showError(caller, "Error", e.getMessage());
        }
    }
}
