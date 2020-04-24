package com.voltcash.vterminal.services.stub;

import android.util.Log;
import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.interfaces.TxConnector;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.TxData;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by roberto.rodriguez on 2/19/2020.
 */

public class TxConnectorStub implements TxConnector {


    public void checkAuthLocationConfig( final ServiceCallback callback) throws Exception{
        Log.i("TxServiceStub", "checkAuthLocationConfig");
        String amount = TxData.getString(Field.TX.AMOUNT);
        String cardNumber = TxData.getString(Field.TX.CARD_NUMBER);

        try {
            Double amountD = Double.parseDouble(amount);

            //---- TODO remove this --
            Thread.sleep(2000);
            Map response = new HashMap();
            response.put(Field.TX.BALANCE,   "100.0");
            response.put(Field.TX.ACTIVATION_FEE,  "0.00");
            response.put(Field.TX.CARD_EXIST,  "1".equals(cardNumber));
            //---- TODO remove this --


            TxData.take(response, Field.TX.CARD_LOAD_FEE, Field.TX.ACTIVATION_FEE, Field.TX.CARD_EXIST);

            callback.onSuccess(response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tx(  final ServiceCallback callback) throws Exception{
        Log.i("TxServiceStub", "submitTx");

            callback.onSuccess( new HashMap());
        }

    public void balanceInquiry(final ServiceCallback callback) throws Exception{
        Map response = new HashMap();
        response.put(Field.TX.BALANCE,   "100.0");

        callback.onSuccess(response);
    }

    public void cardToBank(final ServiceCallback callback) throws Exception{
        Map response = new HashMap();
        response.put(Field.TX.REQUEST_ID,   "1");
        response.put(Field.TX.EXISTACH,     true);
        response.put(Field.TX.MERCHANT_NAME,"Bass Brothers");
        response.put(Field.TX.CUSTUMER_ADDRESS,"9840 Palmetto Club Dr, Miami, FL, 33157");
        response.put(Field.TX.CUSTUMER_NAME,   "Vanessa Rodriguez");
        response.put(Field.TX.BANK_NAME,       "Chase");
        response.put(Field.TX.ROUTING_BANK_NUMBER,   "12345");
        response.put(Field.TX.ACCOUNT_NUMBER,  "9988776655");

        callback.onSuccess(response);
    }

}
