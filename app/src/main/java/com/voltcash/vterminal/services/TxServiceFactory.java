package com.voltcash.vterminal.services;

import com.voltcash.vterminal.interfaces.ServiceCallerActivity;
import com.voltcash.vterminal.interfaces.TxService;
import com.voltcash.vterminal.services.impl.TxServiceImpl;
import com.voltcash.vterminal.services.stub.TxServiceStub;
import com.voltcash.vterminal.util.Constants;
import com.voltcash.vterminal.util.Settings;
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

    public static void checkAuthLocationConfig(ServiceCallerActivity caller, String cardNumber, String amount, String operation) {
        try {
            service.checkAuthLocationConfig(caller, cardNumber, amount, operation);
        }catch(Exception e){
            ViewUtil.showError(caller, "Error", e.getMessage());
        }
    }

    public static void submitTx(ServiceCallerActivity caller) {
        try {
            service.submitTx(caller);
        }catch(Exception e){
            ViewUtil.showError(caller, "Error", e.getMessage());
        }
    }
}
