package com.voltcash.vterminal.services;

import com.voltcash.vterminal.interfaces.AuthConnector;
import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.services.impl.AuthConnectorImpl;
import com.voltcash.vterminal.services.stub.AuthConnectorStub;
import com.voltcash.vterminal.util.Constants;
import com.voltcash.vterminal.util.Settings;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by roberto.rodriguez on 2/25/2020.
 */

public class AuthService {

    private static AuthConnector connector = new AuthConnectorImpl();
    private static AuthConnector stubConnector = new AuthConnectorStub();

    private static AuthConnector getConnector(){
        if(Settings.ENV == Constants.ENV_LOCAL){
            return stubConnector;
        }else{
            return connector;
        }
    }


    public static void connectTerminal(String activationCode, ServiceCallback callback) {
        try {
            callback.startProgressDialog();

            if(Settings.DEMO_ACCESS_CODE.equals(activationCode)){
                Settings.ENV = Constants.ENV_LOCAL;
            }else{
                Settings.ENV = Constants.ENV_PROD;
            }

            getConnector().connectTerminal(activationCode, callback);
        } catch (Exception e) {
            callback.onFailure(null, e);
        }
    }

    public static void login(String serialNumber, String terminalUsername, String terminalPassword, String email, String password, ServiceCallback callback) {
        try {
            callback.startProgressDialog();

            getConnector().login( serialNumber, terminalUsername, terminalPassword,email, password, callback);
        } catch (Exception e) {
            callback.onFailure(null, e);
        }
    }

    public static void logOut(String sessionToken, ServiceCallback callback) {
        try {
            getConnector().logOut(sessionToken, callback);
        } catch (Exception e) {
            callback.onFailure(null, e);
        }
    }

    public static void changePassword(String currentPassword, String newPassword, ServiceCallback callback) {
        try {
            getConnector().changePassword(currentPassword, newPassword, callback);
        } catch (Exception e) {
            callback.onFailure(null, e);
        }
    }

    public static void notifyIssue(String serialNumber, String clerkId, String functionality,  String errorMessage){
        try {
            getConnector().notifyIssue(serialNumber, clerkId, functionality, errorMessage);
        } catch (Exception e) {}
    }

    public static void subscribeAlerts(ServiceCallback callback){
        try {
            getConnector().subscribeAlerts(callback);
        } catch (Exception e) {}
    }
}
