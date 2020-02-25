package com.voltcash.vterminal.services.impl;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

import com.voltcash.vterminal.interfaces.ServiceCallerActivity;
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

public class TxServiceImpl implements TxService{
    private static TxServiceAPI api = null;

    public static TxServiceAPI getAPI(){
        if(api == null){
            OkHttpClient client = new OkHttpClient.Builder().build();
            api = new Retrofit.Builder().baseUrl("http://149.97.166.38:8085/").client(client).build().create(TxServiceAPI.class);
        }
        return api;
    }



    public void checkAuthLocationConfig(final ServiceCallerActivity caller, String cardNumber, String amount, String operation) {
        final ProgressDialog mProgressDialog = buildProgressDialog(caller, "Calculating Fees", "Please wait...");

        //      TODO Validator
//
//        if(!StringUtil.hasValue(card) || !StringUtil.hasValue(amount)){
//            ViewUtil.showError(this, "Invalid Input", "Values are required");
//            return;
//        }

        try {
            RequestBody cardNumberBody = buildStringBody(cardNumber);
            RequestBody amountBody = buildStringBody(amount);
            RequestBody coperationBody = buildStringBody(operation);

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



//            Call<ResponseBody> call = getAPI().checkAuthLocationConfig(
//                    cardNumberBody,
//                    amountBody,
//                    coperationBody
//            );
//
//            call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call,
//                                       Response<ResponseBody> response) {
//
//
//                    if (mProgressDialog != null && mProgressDialog.isShowing())
//                        mProgressDialog.dismiss();
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    if (mProgressDialog != null && mProgressDialog.isShowing())
//                        mProgressDialog.dismiss();
//
//                    showError(caller, "Error", t.getMessage());
//                }
//            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void submitTx(final ServiceCallerActivity caller) {
            final ProgressDialog mProgressDialog =  buildProgressDialog(caller, "Sending Transaction", "Please wait...");

            try {
                final List<File> filesToDelete = new ArrayList<>();

                MultipartBody.Part checkFront = RequestBuilder.buildMultipartBody(TxField.CHECK_FRONT, filesToDelete);
                MultipartBody.Part checkBack = RequestBuilder.buildMultipartBody(TxField.CHECK_BACK, filesToDelete);
                RequestBody username = buildStringBody("Tito Robe");

                Call<ResponseBody> call = getAPI().upload(
                        checkFront,
                        checkBack,
                        username
                );

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           Response<ResponseBody> response) {

                        for (File file : filesToDelete) {
                            file.delete();
                        }

                        if (mProgressDialog != null && mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        if (mProgressDialog != null && mProgressDialog.isShowing())
                            mProgressDialog.dismiss();

                        showError(caller, "Error", t.getMessage());
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}
