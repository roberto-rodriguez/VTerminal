package com.voltcash.vterminal.services.impl;

import android.app.Activity;
import android.content.Intent;

import com.voltcash.vterminal.interfaces.AuthConnector;
import com.voltcash.vterminal.interfaces.AuthServiceAPI;
import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.util.ClientBuilder;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.PreferenceUtil;
import com.voltcash.vterminal.views.MainActivity;

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

    public void changePassword(String currentPasswordStr, String newPasswordStr, ServiceCallback callback)throws Exception {
        RequestBody sessionToken = getSessionToken(callback.getCtx());
        String clerkIdStr = PreferenceUtil.read(Field.AUTH.CLERK_ID);

        RequestBody clerkID =  buildStringBody(clerkIdStr);
        RequestBody currentPassword =  buildStringBody(currentPasswordStr);
        RequestBody newPassword =  buildStringBody(newPasswordStr);

        try {
            Call<Map> call = getAPI().changePassword(sessionToken, clerkID, currentPassword, newPassword);

            call.enqueue(callback);

        } catch (Exception e) {
            callback.onFailure(null, e);
        }
    }


    private static RequestBody getSessionToken(Activity activity) throws Exception{
        String token = PreferenceUtil.read(Field.AUTH.SESSION_TOKEN);

        if(token == null){
            //If it gets here is because there was an error, need to restart the app
            Intent mainActivity = new Intent(activity.getApplicationContext(), MainActivity.class);
            activity.startActivity(mainActivity);
            return null;
        }
        return buildStringBody(token);
    }

}
