package com.voltcash.vterminal.services.impl;

import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.interfaces.TxServiceAPI;
import com.voltcash.vterminal.interfaces.TxConnector;
import com.voltcash.vterminal.util.ClientBuilder;
import com.voltcash.vterminal.util.Field;
import static com.voltcash.vterminal.util.RequestBuilder.*;

import com.voltcash.vterminal.util.PreferenceUtil;
import com.voltcash.vterminal.util.TxData;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.voltcash.vterminal.util.RequestBuilder.buildStringBody;

/**
 * Created by roberto.rodriguez on 2/19/2020.
 */

public class TxConnectorImpl implements TxConnector {
    private static TxServiceAPI api = null;

    public static TxServiceAPI getAPI(){
        if(api == null){
            api = ClientBuilder.build().create(TxServiceAPI.class);
        }
        return api;
    }



    public void checkAuthLocationConfig(final ServiceCallback callback) throws Exception{
        //      TODO Validator
            RequestBody amount           = buildStringBodyFromTxData(Field.TX.AMOUNT);
            RequestBody cardNumber       = buildStringBodyFromTxData(Field.TX.CARD_NUMBER);
            RequestBody operation        = buildStringBodyFromTxData(Field.TX.OPERATION);

            Call<Map> call = getAPI().checkAuthLocationConfig(
                    getSessionToken(),
                    cardNumber,
                    amount,
                    operation
            );

            call.enqueue(callback);
    }

    public void tx(final ServiceCallback callback) throws Exception{
                final List<File> filesToDelete = new ArrayList<>();

                MultipartBody.Part checkFront= buildMultipartBody(Field.TX.CHECK_FRONT, filesToDelete);
                MultipartBody.Part checkBack = buildMultipartBody(Field.TX.CHECK_BACK, filesToDelete);
                RequestBody amount           = buildStringBodyFromTxData(Field.TX.AMOUNT);
                RequestBody cardNumber       = buildStringBodyFromTxData(Field.TX.CARD_NUMBER);
                RequestBody operation        = buildStringBodyFromTxData(Field.TX.OPERATION);

                MultipartBody.Part idFront   = null;
                MultipartBody.Part idBack    = null;
                RequestBody dlDataScan       = null;
                RequestBody phone            = null;
                RequestBody ssn              = null;

                if(!TxData.getBoolean(Field.TX.CARD_EXIST)){
                    idFront   = buildMultipartBody(Field.TX.ID_FRONT, filesToDelete);
                    idBack    = buildMultipartBody(Field.TX.ID_BACK, filesToDelete);
                    dlDataScan= buildStringBodyFromTxData(Field.TX.DL_DATA_SCAN);
                    phone     = buildStringBodyFromTxData(Field.TX.PHONE);
                    ssn       = buildStringBodyFromTxData(Field.TX.SSN);
                }

                Call<Map> call = getAPI().tx(
                        getSessionToken(),
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
                    public void onResponse(Call<Map> call, Response<Map> res) {

                        for (File file : filesToDelete) {
                            file.delete();
                        }

                        callback.onResponse(call, res);
                    }

                    @Override
                    public void onFailure(Call<Map> call, Throwable t) {
                        callback.onFailure(call, t);
                    }
                });
        }

        private RequestBody getSessionToken() throws Exception{
           return buildStringBody(PreferenceUtil.read(Field.AUTH.SESSION_TOKEN));
        }
}
