package com.voltcash.vterminal.interfaces;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

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

public interface TxService {
    public static final String TX = "tx";
    public static final String CHECK_AUTH_LOCATION_CONFIG = "checkAuthLocationConfig";


    public void checkAuthLocationConfig(final AppCompatActivity caller, ServiceCallback callback)throws Exception;

    public void tx(final AppCompatActivity caller, final ServiceCallback callback) throws Exception;
}
