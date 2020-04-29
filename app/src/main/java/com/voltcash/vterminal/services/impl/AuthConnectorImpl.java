package com.voltcash.vterminal.services.impl;

import com.voltcash.vterminal.interfaces.AuthConnector;
import com.voltcash.vterminal.interfaces.AuthServiceAPI;
import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.util.ClientBuilder;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Part;

import static com.voltcash.vterminal.util.RequestBuilder.buildStringBody;

/**
 * Created by roberto.rodriguez on 2/19/2020.
 */

public class AuthConnectorImpl implements AuthConnector {

    public static AuthServiceAPI getAPI(){
        return ClientBuilder.build().create(AuthServiceAPI.class);
    }



    public void connectTerminal(String activationCode, final ServiceCallback callback) {

        try {
            RequestBody activationCodeBody = buildStringBody(activationCode);

            Call<Map> call = getAPI().connectTerminal(activationCodeBody);

            call.enqueue(callback);

        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(null, e);
        }
    }


    public void login(String serialNumber, String terminalUser , String terminalPassw , String email, String password, ServiceCallback callback)throws Exception{
        try{ 
            RequestBody terminalSerialNumber = buildStringBody(serialNumber);
            RequestBody terminalUsername = buildStringBody(terminalUser);
            RequestBody terminalPassword = buildStringBody(terminalPassw);
            RequestBody emailBody        = buildStringBody(email);
            RequestBody passwordBody     = buildStringBody(password);

            Call<Map> call = getAPI().login(terminalSerialNumber, terminalUsername, terminalPassword, emailBody, passwordBody);

            call.enqueue(callback);

        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(null, e);
        }
    }

    public void logOut(String sessionToken, ServiceCallback callback)throws Exception{
        try{
            RequestBody token = buildStringBody(sessionToken);

            Call<Map> call = getAPI().logOut(token);

            call.enqueue(callback);

        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(null, e);
        }
    }

}
