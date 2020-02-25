package com.voltcash.vterminal.interfaces;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by roberto.rodriguez on 2/25/2020.
 */

public interface TxServiceAPI {
    @Multipart
    @POST("FrontTerminal/v1/tx/checkAuth")
    Call<ResponseBody> upload(
            @Part MultipartBody.Part checkFront,
            @Part MultipartBody.Part checkBack,
            @Part("username") RequestBody params
    );

    @Multipart
    @POST("FrontTerminal/v1/tx/checkAuthLocationConfig")
    Call<ResponseBody> checkAuthLocationConfig(
            @Part("cardNumber") RequestBody cardNumber,
            @Part("amount")     RequestBody amount,
            @Part("operation")  RequestBody operation
    );
}