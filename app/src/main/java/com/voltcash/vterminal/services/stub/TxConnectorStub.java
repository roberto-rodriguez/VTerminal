package com.voltcash.vterminal.services.stub;

import android.util.Log;
import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.interfaces.TxConnector;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.TxData;
import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;

import static com.voltcash.vterminal.util.RequestBuilder.buildStringBody;

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
            response.put(Field.TX.CARD_LOAD_FEE,  "3.50");
            response.put(Field.TX.ACTIVATION_FEE,  "0.00");
            response.put(Field.TX.CARD_EXIST,  false);

            //---- TODO remove this --
            TxData.take(response, Field.TX.CARD_LOAD_FEE, Field.TX.ACTIVATION_FEE, Field.TX.CARD_EXIST);

            callback.stubSuccess(response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tx(  final ServiceCallback callback) throws Exception{
        Log.i("TxServiceStub", "submitTx");


        Map response = new HashMap();
        response.put("REQUEST_ID",   "123.0");

        response.put(Field.TX.EXCLUDE_SMS, true);
        response.put(Field.TX.CARD_ID,  1);

        TxData.take(response, Field.TX.EXCLUDE_SMS, Field.TX.CARD_ID);

        callback.stubSuccess(response);
    }

    public void balanceInquiry(final ServiceCallback callback) throws Exception{
        Map response = new HashMap();
        response.put(Field.TX.BALANCE,   "100.0");

        callback.stubSuccess(response);
    }

    public void cardToBank(String operation, final ServiceCallback callback) throws Exception{
        Map response = new HashMap();
        response.put(Field.TX.REQUEST_ID,   "1");
        response.put(Field.TX.EXISTACH,     true);
        response.put(Field.TX.MERCHANT_NAME,"Demo Merchant");
        response.put(Field.TX.CUSTUMER_ADDRESS,"123 Main Street, Miami, FL, 33157");
        response.put(Field.TX.CUSTUMER_NAME,   "John Smith");
        response.put(Field.TX.BANK_NAME,       "Chase");
        response.put(Field.TX.ROUTING_BANK_NUMBER,   "12345");
        response.put(Field.TX.ACCOUNT_NUMBER,  "9988776655");

        callback.stubSuccess(response);
    }


    public void activityReport(String startDate, String endDate, final ServiceCallback callback) throws Exception{
        Map response = new HashMap();

        response.put("TOTAL_ROWS", 0.0);

        callback.stubSuccess(response);
    }

    public void calculateFee(String operation, String amount, String card, final ServiceCallback callback) throws Exception{
        Map response = new HashMap(); 
        response.put(Field.TX.C2B_FEE,     3.0);

        callback.stubSuccess(response);
    }
}
