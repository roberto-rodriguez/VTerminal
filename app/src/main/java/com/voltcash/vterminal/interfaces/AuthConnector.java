package com.voltcash.vterminal.interfaces;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by roberto.rodriguez on 2/19/2020.
 */

public interface AuthConnector {

    public void connectTerminal(String activationCode, ServiceCallback callback)throws Exception;

    public void login(String serialNumber, String terminalUsername, String terminalPassword, String clerkEmail, String clerkPassword, ServiceCallback callback)throws Exception;

    public void logOut(String sessionToken, ServiceCallback callback)throws Exception;

    public void changePassword(String currentPassword, String newPassword, ServiceCallback callback) throws Exception;

    public void notifyIssue(String serialNumber, String clerkId, String functionality,  String errorMessage) throws Exception;

    public void subscribeAlerts(ServiceCallback callback) throws Exception;
}
