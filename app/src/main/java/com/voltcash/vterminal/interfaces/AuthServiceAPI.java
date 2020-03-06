package com.voltcash.vterminal.interfaces;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by roberto.rodriguez on 2/25/2020.
 */

public interface AuthServiceAPI {
    @Multipart
    @POST("FrontTerminal/v1/auth/connect")
    Call<Map> connectTerminal(
            @Part("activationCode") RequestBody activationCode
    );

    @Multipart
    @POST("FrontTerminal/v1/auth/login")
    Call<Map> login(
            @Part("email") RequestBody email,
            @Part("password") RequestBody password
    );
}