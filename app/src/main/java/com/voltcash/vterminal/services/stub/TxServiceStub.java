package com.voltcash.vterminal.services.stub;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.voltcash.vterminal.interfaces.ServiceCallerActivity;
import com.voltcash.vterminal.interfaces.TxService;
import com.voltcash.vterminal.interfaces.TxServiceAPI;
import com.voltcash.vterminal.util.RequestBuilder;
import com.voltcash.vterminal.util.TxData;
import com.voltcash.vterminal.util.TxField;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.voltcash.vterminal.util.RequestBuilder.buildStringBody;
import static com.voltcash.vterminal.util.ViewUtil.buildProgressDialog;
import static com.voltcash.vterminal.util.ViewUtil.showError;

/**
 * Created by roberto.rodriguez on 2/19/2020.
 */

public class TxServiceStub implements TxService{


    public void checkAuthLocationConfig(final ServiceCallerActivity caller, String cardNumber, String amount, String operation) throws Exception{
        Log.i("TxServiceStub", "checkAuthLocationConfig");

        final ProgressDialog mProgressDialog = buildProgressDialog((AppCompatActivity)caller, "Calculating Fees", "Please wait...");

        //      TODO Validator
//
//        if(!StringUtil.hasValue(card) || !StringUtil.hasValue(amount)){
//            ViewUtil.showError(this, "Invalid Input", "Values are required");
//            return;
//        }

        try {
            Double amountD = Double.parseDouble(amount);

            //---- TODO remove this --
            Thread.sleep(2000);
            mProgressDialog.dismiss();
            Map response = new HashMap();
            response.put(TxField.CARD_LOAD_FEE, (amountD * 2.75 / 100 ) + "");
            response.put(TxField.ACTIVATION_FEE,  "5.00");
            response.put(TxField.CARD_EXIST,  "1".equals(cardNumber));
            //---- TODO remove this --


            TxData.take(response, TxField.CARD_LOAD_FEE, TxField.ACTIVATION_FEE, TxField.CARD_EXIST);

            caller.onServiceCallback(CHECK_AUTH_LOCATION_CONFIG, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void submitTx(final ServiceCallerActivity caller) throws Exception{
        Log.i("TxServiceStub", "submitTx");
            final ProgressDialog mProgressDialog =  buildProgressDialog((AppCompatActivity)caller, "Sending Transaction", "Please wait...");

            Thread.sleep(2000);
            mProgressDialog.dismiss();

            caller.onServiceCallback(CHECK_AUTH , true);
        }
}
