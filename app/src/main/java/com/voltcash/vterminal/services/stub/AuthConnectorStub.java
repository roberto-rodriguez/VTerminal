package com.voltcash.vterminal.services.stub;

import com.voltcash.vterminal.interfaces.AuthConnector;
import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.util.Field;
import java.util.HashMap;
import java.util.Map;

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

}
