package com.voltcash.vterminal.services;

import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.interfaces.TxConnector;
import com.voltcash.vterminal.services.impl.TxConnectorImpl;
import com.voltcash.vterminal.services.stub.TxConnectorStub;
import com.voltcash.vterminal.util.Constants;
import com.voltcash.vterminal.util.Settings;

/**
 * Created by roberto.rodriguez on 2/25/2020.
 */

public class TxService {

    private static TxConnector connector;

    static{
        if(Settings.ENV == Constants.ENV_LOCAL){
            connector = new TxConnectorStub();
        }else{
            connector = new TxConnectorImpl();
        }
    }

    public static void checkAuthLocationConfig(ServiceCallback callback) {
        try {
            connector.checkAuthLocationConfig(callback);
        } catch (Exception e) {
            callback.onFailure(null, e);
        }
    }

    public static void tx(ServiceCallback callback) {
        try {
            connector.tx(callback);
        } catch (Exception e) {
            callback.onFailure(null, e);
        }
    }
}
