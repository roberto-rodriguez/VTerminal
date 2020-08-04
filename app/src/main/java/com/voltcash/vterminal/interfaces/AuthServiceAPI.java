package com.voltcash.vterminal.interfaces;

import java.util.Map;

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
    @POST("FrontTerminal/v1/clerk/login")
    Call<Map> login(
            @Part("terminalSerialNumber") RequestBody terminalSerialNumber,
            @Part("terminalUsername") RequestBody terminalUsername,
            @Part("terminalPassword") RequestBody terminalPassword,
            @Part("clerkEmail")       RequestBody email,
            @Part("clerkPassword")    RequestBody password
    );

    @Multipart
    @POST("FrontTerminal/v1/clerk/logOut")
    Call<Map> logOut(
            @Part("sessionToken") RequestBody sessionToken
    );

    @Multipart
    @POST("FrontTerminal/v1/clerk/changePassword")
    Call<Map> changePassword(
            @Part("sessionToken")    RequestBody sessionToken,
            @Part("clerkId")         RequestBody clerkId,
            @Part("currentPassword") RequestBody currentPassword,
            @Part("newPassword")     RequestBody newPassword
    );

    @Multipart
    @POST("FrontTerminal/v1/clerk/notifyIssue")
    Call<Map> notifyIssue(
            @Part("serialNumber")  RequestBody serialNumber,
            @Part("clerkId")       RequestBody clerkId,
            @Part("functionality") RequestBody functionality,
            @Part("errorMessage")  RequestBody errorMessage
    );

}