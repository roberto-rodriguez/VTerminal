package com.voltcash.vterminal.tx;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

import com.voltcash.vterminal.R;
import com.voltcash.vterminal.util.RequestBuilder;
import com.voltcash.vterminal.util.TxField;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

import static com.voltcash.vterminal.util.RequestBuilder.buildStringBody;

/**
 * Created by roberto.rodriguez on 2/19/2020.
 */

public class TxService {
    private static TxAPIService api = null;

    public static TxAPIService getAPI(){
        if(api == null){
            OkHttpClient client = new OkHttpClient.Builder().build();
            api = new Retrofit.Builder().baseUrl("http://149.97.166.38:8085/").client(client).build().create(TxAPIService.class);
        }
        return api;
    }

    public interface TxAPIService {
        @Multipart
        @POST("FrontTerminal/v1/tx/checkAuth")
        Call<ResponseBody> upload(@Part MultipartBody.Part checkFront, @Part MultipartBody.Part checkBack, @Part("username") RequestBody params);  //, @Body RequestBody params
    }



    public static void submit(final AppCompatActivity caller) {
            final ProgressDialog mProgressDialog = new ProgressDialog(caller);
            mProgressDialog.setTitle("Sending Transaction");
            mProgressDialog.setMessage("Please wait... ( from service)");
            mProgressDialog.show();

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

                        new AlertDialog.Builder(caller)
                                .setTitle("Error")
                                .setMessage(t.getMessage())
                                .setPositiveButton(android.R.string.ok, null)
                                .setCancelable(true)
                                .setIcon(R.drawable.error)
                                .show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}
