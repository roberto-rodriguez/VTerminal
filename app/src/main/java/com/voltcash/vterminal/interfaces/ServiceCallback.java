package com.voltcash.vterminal.interfaces;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.util.Log;

import com.voltcash.vterminal.util.AudioUtil;
import com.voltcash.vterminal.util.DialogUtils;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.TxData;

import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.voltcash.vterminal.util.ViewUtil.buildProgressDialog;
import static com.voltcash.vterminal.util.ViewUtil.showError;

/**
 * Created by roberto.rodriguez on 2/24/2020.
 */

public abstract class ServiceCallback implements Callback<Map> {
    protected Activity caller;

    protected ProgressDialog mProgressDialog;

    public ServiceCallback(Activity caller){
        this.caller = caller;
    }

    public void startProgressDialog(){
        startProgressDialog("Please wait...");
    }

    public void startNonCancellableProgressDialog(String msg){
        //  mProgressDialog = buildProgressDialog(caller, "Processing", msg);
        mProgressDialog = (ProgressDialog) DialogUtils.showNonCancellableProgress(caller, "Processing", msg, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
    }

    public void startProgressDialog(String msg){
        mProgressDialog = (ProgressDialog) DialogUtils.showProgress(caller, "Processing", msg, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
    }

    @Override
    public void onResponse(Call<Map> call,
                           Response<Map> res) {
        Map response = null;
        try{
            if (mProgressDialog != null && mProgressDialog.isShowing()){
                mProgressDialog.dismiss();
            }

              response = res.body();

            if(response == null){
                throw new Exception("Unexpected Server Error");
            }

            Log.i("RESPONSE", response.toString());

            TxData.put(Field.TX.CARD_ID, response.get(Field.TX.CARD_ID));
            TxData.put(Field.TX.EXCLUDE_SMS, response.get(Field.TX.EXCLUDE_SMS));

            if(response.containsKey(Field.ERROR_MESSAGE)){
              onError(response);
            }else{
              onSuccess(response);
            }
        }catch(Exception e){
            e.printStackTrace();

            onFailure(call, e);
        }

    }

    @Override
    public void onFailure(Call<Map> call, Throwable t) {
        AudioUtil.playBellSound(caller);

         t.printStackTrace();

        dismissDialog();

        showError(caller, "Unexpected Error", t.getMessage());
    }

    protected void dismissDialog(){
        if (mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }

    public abstract void onSuccess(Map map);

    public void onError( Map map){
        AudioUtil.playBellSound(caller);
        showError(caller, "Unexpected Error", (String)map.get("errorMessage"));
    }

    public Activity getCtx(){
        return caller;
    }
}
