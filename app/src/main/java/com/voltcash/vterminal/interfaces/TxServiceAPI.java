package com.voltcash.vterminal.interfaces;

import java.util.Map;

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
    @POST("FrontTerminal/v1/tx/tx")
    Call<Map> tx(
            @Part MultipartBody.Part checkFront,
            @Part MultipartBody.Part checkBack,
            @Part MultipartBody.Part idFrontImage,
            @Part MultipartBody.Part idBackImage,
            @Part("amount") RequestBody amount,
            @Part("cardNumber") RequestBody  cardNumber,
            @Part("phone") RequestBody  phone,
            @Part("ssn") RequestBody ssn,
            @Part("operation") RequestBody operation,
            @Part("dlDataScan") RequestBody dlDataScan
    );

    @Multipart
    @POST("FrontTerminal/v1/tx/checkAuthLocationConfig")
    Call<Map> checkAuthLocationConfig(
            @Part("cardNumber") RequestBody cardNumber,
            @Part("amount")     RequestBody amount,
            @Part("operation")  RequestBody operation
    );
}