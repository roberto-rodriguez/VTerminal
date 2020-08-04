package com.voltcash.vterminal.services;

import com.voltcash.vterminal.interfaces.AuthConnector;
import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.services.impl.AuthConnectorImpl;
import com.voltcash.vterminal.services.stub.AuthConnectorStub;
import com.voltcash.vterminal.util.Constants;
import com.voltcash.vterminal.util.Settings;

/**
 * Created by roberto.rodriguez on 2/25/2020.
 */

public class AuthService {

    private static AuthConnector connector;

    static{
        if(Settings.ENV == Constants.ENV_LOCAL){
            connector = new AuthConnectorStub();
        }else{
            connector = new AuthConnectorImpl();
        }
    }

    public static void connectTerminal(String activationCode, ServiceCallback callback) {
        try {
            callback.startProgressDialog();

            connector.connectTerminal(activationCode, callback);
        } catch (Exception e) {
            callback.onFailure(null, e);
        }
    }

    public static void login(String serialNumber, String terminalUsername, String terminalPassword, String email, String password, ServiceCallback callback) {
        try {
            callback.startProgressDialog();

            connector.login( serialNumber, terminalUsername, terminalPassword,email, password, callback);
        } catch (Exception e) {
            callback.onFailure(null, e);
        }
    }

    public static void logOut(String sessionToken, ServiceCallback callback) {
        try {
            connector.logOut(sessionToken, callback);
        } catch (Exception e) {
            callback.onFailure(null, e);
        }
    }

    public static void changePassword(String currentPassword, String newPassword, ServiceCallback callback) {
        try {
            connector.changePassword(currentPassword, newPassword, callback);
        } catch (Exception e) {
            callback.onFailure(null, e);
        }
    }

    public static void notifyIssue(String serialNumber, String clerkId, String functionality,  String errorMessage){
        try {
            connector.notifyIssue(serialNumber, clerkId, functionality, errorMessage);
        } catch (Exception e) {}
    }
}
