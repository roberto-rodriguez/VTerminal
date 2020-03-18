package com.voltcash.vterminal.interfaces;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.voltcash.vterminal.util.Field;

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
    private AppCompatActivity caller;

    ProgressDialog mProgressDialog;

    public ServiceCallback(AppCompatActivity caller){
        this.caller = caller;
    }

    public void startProgressDialog(){
        mProgressDialog = buildProgressDialog(caller, "Processing", "Please wait...");
    }

    @Override
    public void onResponse(Call<Map> call,
                           Response<Map> res) {
        try{
            if (mProgressDialog != null && mProgressDialog.isShowing()){
                mProgressDialog.dismiss();
            }

            Map response = res.body();

            if(response == null){
                throw new Exception("Unexpected Server Error");
            }

            Log.i("RESPONSE", response.toString());

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
        t.printStackTrace();

        if (mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }

        showError(caller, "Unexpected Error", t.getMessage());
    }

    public abstract void onSuccess(Map map);

    public void onError( Map map){
        showError(caller, "Error", (String)map.get("errorMessage"));
    }
}
