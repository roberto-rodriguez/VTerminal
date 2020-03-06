package com.voltcash.vterminal.interfaces;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by roberto.rodriguez on 2/19/2020.
 */

public interface AuthConnector {

    public void connectTerminal(String activationCode, ServiceCallback callback)throws Exception;

    public void login(String email, String password, ServiceCallback callback)throws Exception;
}
