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

    private static TxConnector connector = new TxConnectorImpl();
    private static TxConnector stubConnector = new TxConnectorStub();

    private static TxConnector getConnector(){
        if(Settings.ENV == Constants.ENV_LOCAL){
            return stubConnector;
        }else{
            return connector;
        }
    }

    static{
        if(Settings.ENV == Constants.ENV_LOCAL){
            connector = new TxConnectorStub();
        }else{
            connector = new TxConnectorImpl();
        }
    }

    public static void checkAuthLocationConfig(ServiceCallback callback) {
        try {
            callback.startProgressDialog();

            getConnector().checkAuthLocationConfig(callback);
        } catch (Exception e) {
            callback.onFailure(null, e);
        }
    }

    public static void tx(ServiceCallback callback) {
        try {
            getConnector().tx(callback);
        } catch (Exception e) {
            callback.onFailure(null, e);
        }
    }

    public static void balanceInquiry(ServiceCallback callback) {
        try {
            callback.startProgressDialog();

            getConnector().balanceInquiry(callback);
        } catch (Exception e) {
            callback.onFailure(null, e);
        }
    }

    public static void cardToBank(String operation, ServiceCallback callback) {
        try {
            callback.startNonCancellableProgressDialog("Please Wait...");

            getConnector().cardToBank(operation, callback);
        } catch (Exception e) {
            callback.onFailure(null, e);
        }
    }

    public static void activityReport(String startDate, String endDate, ServiceCallback callback) {
        try {
            callback.startProgressDialog();

            getConnector().activityReport(startDate, endDate, callback);
        } catch (Exception e) {
            callback.onFailure(null, e);
        }
    }

    public static void calculateFee(String operation, String amount, String card, final ServiceCallback callback){
        try {
            callback.startProgressDialog();

            getConnector().calculateFee(operation, amount, card, callback);
        } catch (Exception e) {
            callback.onFailure(null, e);
        }
    }
}
