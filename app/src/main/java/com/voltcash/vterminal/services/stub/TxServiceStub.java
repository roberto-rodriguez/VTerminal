package com.voltcash.vterminal.services.stub;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.interfaces.TxService;
import com.voltcash.vterminal.util.TxData;
import com.voltcash.vterminal.util.TxField;
import java.util.HashMap;
import java.util.Map;
import static com.voltcash.vterminal.util.ViewUtil.buildProgressDialog;

/**
 * Created by roberto.rodriguez on 2/19/2020.
 */

public class TxServiceStub implements TxService{


    public void checkAuthLocationConfig(final AppCompatActivity caller, final ServiceCallback callback) throws Exception{
        Log.i("TxServiceStub", "checkAuthLocationConfig");

        //      TODO Validator
//
//        if(!StringUtil.hasValue(card) || !StringUtil.hasValue(amount)){
//            ViewUtil.showError(this, "Invalid Input", "Values are required");
//            return;
//        }

        String amount = TxData.getString(TxField.AMOUNT);
        String cardNumber = TxData.getString(TxField.CARD_NUMBER);

        try {
            Double amountD = Double.parseDouble(amount);

            //---- TODO remove this --
            Thread.sleep(2000);
            Map response = new HashMap();
            response.put(TxField.CARD_LOAD_FEE,   "3.0");
            response.put(TxField.ACTIVATION_FEE,  "0.00");
            response.put(TxField.CARD_EXIST,  "1".equals(cardNumber));
            //---- TODO remove this --


            TxData.take(response, TxField.CARD_LOAD_FEE, TxField.ACTIVATION_FEE, TxField.CARD_EXIST);

            callback.call(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tx(final AppCompatActivity caller, final ServiceCallback callback) throws Exception{
        Log.i("TxServiceStub", "submitTx");

        //    Thread.sleep(2000);
            callback.call( true);
        }
}
