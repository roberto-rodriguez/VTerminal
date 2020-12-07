package com.voltcash.vterminal.services.stub;

import com.voltcash.vterminal.interfaces.AuthConnector;
import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.PreferenceUtil;
import com.voltcash.vterminal.util.TxData;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;

import static com.voltcash.vterminal.util.RequestBuilder.buildStringBody;

/**
 * Created by roberto.rodriguez on 2/19/2020.
 */

public class AuthConnectorStub implements AuthConnector {

    public void connectTerminal(String activationCode, final ServiceCallback callback) {
        boolean success = "asd".equalsIgnoreCase(activationCode);
        Map response = new HashMap();

        if(success){
           response.put(Field.AUTH.TERMINAL_USERNAME, "mock_username");
           response.put(Field.AUTH.TERMINAL_PASSWORD, "mock_passworda");
           response.put(Field.AUTH.TERMINAL_SERIAL_NUMBER, "mock_serial_number");

           callback.onSuccess(response);
        }else{
            response.put(Field.ERROR_MESSAGE, "Invalid Activation Code");
            callback.onError(response);
        }
    }


    public void login(String serialNumber, String terminalUsername, String terminalPassword, String email, String password, ServiceCallback callback)throws Exception{
         boolean success = "a".equalsIgnoreCase(email) && "a".equalsIgnoreCase(password);

        Map response = new HashMap();

         if(success){

             response.put(Field.AUTH.CLERK_FIRST_NAME, "Roberto");
             response.put(Field.AUTH.CLERK_LAST_NAME, "Rodriguez");
             response.put(Field.AUTH.CLERK_ID, 1);
             response.put(Field.AUTH.SESSION_TOKEN, "asd");

             callback.onSuccess(response);
         }else{
             response.put(Field.ERROR_MESSAGE, "Invalid login credentials");
             callback.onError(response);
         }
    }

    public void logOut(String sessionToken, ServiceCallback callback)throws Exception{
        callback.onSuccess(new HashMap());
    }

    public void changePassword(String currentPasswordStr, String newPasswordStr, ServiceCallback callback)throws Exception {
        callback.onSuccess(new HashMap());
    }

    public void notifyIssue(String serialNumberStr, String clerkIdStr, String functionalityStr,  String errorMessageStr)throws Exception {}

    public void subscribeAlerts(ServiceCallback callback)throws Exception {}
}
