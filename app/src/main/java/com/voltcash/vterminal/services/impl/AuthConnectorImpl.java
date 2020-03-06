package com.voltcash.vterminal.services.impl;

import com.voltcash.vterminal.interfaces.AuthConnector;
import com.voltcash.vterminal.interfaces.AuthServiceAPI;
import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.util.ClientBuilder;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;

import static com.voltcash.vterminal.util.RequestBuilder.buildStringBody;

/**
 * Created by roberto.rodriguez on 2/19/2020.
 */

public class AuthConnectorImpl implements AuthConnector {
    private static AuthServiceAPI api = null;

    public static AuthServiceAPI getAPI(){
        if(api == null){
            api = ClientBuilder.build().create(AuthServiceAPI.class);
        }
        return api;
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


    public void login(String email, String password, ServiceCallback callback)throws Exception{
        try{
            RequestBody emailBody = buildStringBody(email);
            RequestBody passwordBody = buildStringBody(password);

            Call<Map> call = getAPI().login(emailBody, passwordBody);

            call.enqueue(callback);

        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(null, e);
        }
    }

}
