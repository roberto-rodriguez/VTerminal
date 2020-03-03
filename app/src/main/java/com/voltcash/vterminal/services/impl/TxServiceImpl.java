package com.voltcash.vterminal.services.impl;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.interfaces.TxServiceAPI;
import com.voltcash.vterminal.interfaces.TxService;
import com.voltcash.vterminal.util.RequestBuilder;
import com.voltcash.vterminal.util.TxData;
import com.voltcash.vterminal.util.TxField;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.voltcash.vterminal.util.RequestBuilder.buildStringBody;
import static com.voltcash.vterminal.util.ViewUtil.buildProgressDialog;
import static com.voltcash.vterminal.util.ViewUtil.showError;

/**
 * Created by roberto.rodriguez on 2/19/2020.
 */

public class TxServiceImpl implements TxService{
    private static TxServiceAPI api = null;

    public static TxServiceAPI getAPI(){
        if(api == null){
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(3, TimeUnit.MINUTES)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            api = new Retrofit
                    .Builder()
                    .baseUrl("http://149.97.166.38:8085/")

                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
                    .create(TxServiceAPI.class);
        }
        return api;
    }



    public void checkAuthLocationConfig(final AppCompatActivity caller, final ServiceCallback callback) {
        //      TODO Validator
//
//        if(!StringUtil.hasValue(card) || !StringUtil.hasValue(amount)){
//            ViewUtil.showError(this, "Invalid Input", "Values are required");
//            return;
//        }

        try {
            RequestBody amount           = buildStringBody(TxField.AMOUNT);
            RequestBody cardNumber       = buildStringBody(TxField.CARD_NUMBER);
            RequestBody operation        = buildStringBody(TxField.OPERATION);

            Call<Map> call = getAPI().checkAuthLocationConfig(
                    cardNumber,
                    amount,
                    operation
            );

            call.enqueue(new Callback<Map>() {
                @Override
                public void onResponse(Call<Map> call,
                                       Response<Map> res) {
                   try{
                      Map response = res.body();

                       Log.i("RESPONSE", response.toString());

                       TxData.put(TxField.CARD_LOAD_FEE , response.get(TxField.CARD_LOAD_FEE.getName()));
                       TxData.put(TxField.ACTIVATION_FEE, response.get(TxField.ACTIVATION_FEE.getName()));
                       TxData.put(TxField.CARD_EXIST    , response.get(TxField.CARD_EXIST.getName()));

                   }catch(Exception e){
                       e.printStackTrace();
                   }

                    callback.call(true);
                }

                @Override
                public void onFailure(Call<Map> call, Throwable t) {
                    showError(caller, "Error", t.getMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tx(final AppCompatActivity caller, final ServiceCallback callback) {


            try {
                final List<File> filesToDelete = new ArrayList<>();

                MultipartBody.Part checkFront= RequestBuilder.buildMultipartBody(TxField.CHECK_FRONT, filesToDelete);
                MultipartBody.Part checkBack = RequestBuilder.buildMultipartBody(TxField.CHECK_BACK, filesToDelete);
                RequestBody amount           = buildStringBody(TxField.AMOUNT);
                RequestBody cardNumber       = buildStringBody(TxField.CARD_NUMBER);
                RequestBody operation        = buildStringBody(TxField.OPERATION);

                MultipartBody.Part idFront   = null;
                MultipartBody.Part idBack    = null;
                RequestBody dlDataScan       = null;
                RequestBody phone            = null;
                RequestBody ssn              = null;

                if(!TxData.getBoolean(TxField.ID_BACK.CARD_EXIST)){
                    idFront   = RequestBuilder.buildMultipartBody(TxField.ID_FRONT, filesToDelete);
                    idBack    = RequestBuilder.buildMultipartBody(TxField.ID_BACK, filesToDelete);
                    dlDataScan= buildStringBody(TxField.DL_DATA_SCAN);
                    phone     = buildStringBody(TxField.PHONE);
                    ssn       = buildStringBody(TxField.SSN);
                }


                Call<Map> call = getAPI().tx(
                        checkFront,
                        checkBack,
                        idFront,
                        idBack,
                        amount,
                        cardNumber,
                        phone,
                        ssn,
                        operation,
                        dlDataScan
                );

                call.enqueue(new Callback<Map>() {
                    @Override
                    public void onResponse(Call<Map> call,
                                           Response<Map> res) {

                        for (File file : filesToDelete) {
                            file.delete();
                        }

                        Map response = res.body();
                        Log.i("RESPONSE - TX", response.toString());

                        callback.call( true);
                    }

                    @Override
                    public void onFailure(Call<Map> call, Throwable t) {
                        showError(caller, "Error", t.getMessage());
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}
