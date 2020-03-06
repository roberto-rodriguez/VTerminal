package com.voltcash.vterminal.services.impl;

import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.interfaces.TxServiceAPI;
import com.voltcash.vterminal.interfaces.TxConnector;
import com.voltcash.vterminal.util.ClientBuilder;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.RequestBuilder;
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
            RequestBody amount           = buildStringBody(Field.TX.AMOUNT);
            RequestBody cardNumber       = buildStringBody(Field.TX.CARD_NUMBER);
            RequestBody operation        = buildStringBody(Field.TX.OPERATION);

            Call<Map> call = getAPI().checkAuthLocationConfig(
                    cardNumber,
                    amount,
                    operation
            );

            call.enqueue(callback);
    }

    public void tx(final ServiceCallback callback) throws Exception{
                final List<File> filesToDelete = new ArrayList<>();

                MultipartBody.Part checkFront= RequestBuilder.buildMultipartBody(Field.TX.CHECK_FRONT, filesToDelete);
                MultipartBody.Part checkBack = RequestBuilder.buildMultipartBody(Field.TX.CHECK_BACK, filesToDelete);
                RequestBody amount           = buildStringBody(Field.TX.AMOUNT);
                RequestBody cardNumber       = buildStringBody(Field.TX.CARD_NUMBER);
                RequestBody operation        = buildStringBody(Field.TX.OPERATION);

                MultipartBody.Part idFront   = null;
                MultipartBody.Part idBack    = null;
                RequestBody dlDataScan       = null;
                RequestBody phone            = null;
                RequestBody ssn              = null;

                if(!TxData.getBoolean(Field.TX.CARD_EXIST)){
                    idFront   = RequestBuilder.buildMultipartBody(Field.TX.ID_FRONT, filesToDelete);
                    idBack    = RequestBuilder.buildMultipartBody(Field.TX.ID_BACK, filesToDelete);
                    dlDataScan= buildStringBody(Field.TX.DL_DATA_SCAN);
                    phone     = buildStringBody(Field.TX.PHONE);
                    ssn       = buildStringBody(Field.TX.SSN);
                }

                Call<Map> call = getAPI().tx(
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
}
