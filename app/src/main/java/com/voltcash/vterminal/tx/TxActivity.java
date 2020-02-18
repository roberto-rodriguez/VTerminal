package com.voltcash.vterminal.tx;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.kofax.kmc.ken.engines.data.Image;
import com.kofax.kmc.kui.uicontrols.ImgReviewEditCntrl;
import com.kofax.kmc.kut.utilities.error.KmcException;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.util.RequestBuilder;
import com.voltcash.vterminal.util.TxData;
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
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import static com.voltcash.vterminal.util.Constants.PROCESSED_IMAGE_RETAKE_RESPONSE_ID;
import static com.voltcash.vterminal.util.RequestBuilder.buildStringBody;

public class TxActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;

    public static final int CAPTURE_CHECK_FRONT_ACTIVITY_ID = 1001;
    public static final int CAPTURE_CHECK_BACK_ACTIVITY_ID = 1002;

    private ImgReviewEditCntrl checkFrontImgReviewEditCntrl;
    private ImgReviewEditCntrl checkBackImgReviewEditCntrl;

    private Integer activeImgActivityId = null;
    private TxField activeImgField = null;

    private static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    APIService api;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tx);
        getSupportActionBar().hide();

        checkFrontImgReviewEditCntrl = (ImgReviewEditCntrl) findViewById(R.id.view_check_front_image);
        checkBackImgReviewEditCntrl = (ImgReviewEditCntrl) findViewById(R.id.view_check_back_image);

        OkHttpClient client = new OkHttpClient.Builder().build();
        api = new Retrofit.Builder().baseUrl("http://149.97.166.38:8085/").client(client).build().create(APIService.class);
    }

    public void onSubmit(View view){
        try{
            submit();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void onClickCheckFront(View view){
        activeImgActivityId = CAPTURE_CHECK_FRONT_ACTIVITY_ID;
        activeImgField = TxField.CHECK_FRONT;
        onCaptureClick();
    }

    public void onClickCheckBack(View view){
        activeImgActivityId = CAPTURE_CHECK_BACK_ACTIVITY_ID;
        activeImgField = TxField.CHECK_BACK;
        onCaptureClick();
    }



    protected void onCaptureClick(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Please wait");
        mProgressDialog.setMessage("Initializing...");
        mProgressDialog.show();

        Intent intent = null;

        if(TxData.contains(activeImgField)){
            intent = new Intent(this, PreviewActivity.class);
        }else{
            intent = new Intent(this, CaptureActivity.class);
        }

        intent.putExtra(TxField.TX_FIELD.getName(), activeImgField);
        startActivityForResult(intent, activeImgActivityId);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("onActivityResult", "onActivityResult() requestCode = " + requestCode);


        if (mProgressDialog != null && mProgressDialog.isShowing()) mProgressDialog.dismiss();

        if(resultCode == PROCESSED_IMAGE_RETAKE_RESPONSE_ID){
           Intent intent = new Intent(this, CaptureActivity.class);
            intent.putExtra(TxField.TX_FIELD.getName(), activeImgField);
            startActivityForResult(intent, activeImgActivityId);
        }else{

            switch(requestCode){
                case CAPTURE_CHECK_FRONT_ACTIVITY_ID:
                    Log.i("onActivityResult", "CAPTURE_CHECK_FRONT_ACTIVITY_ID -> calling refreshImage()");
                    refreshImage(checkFrontImgReviewEditCntrl, TxField.CHECK_FRONT);
                    break;
                case CAPTURE_CHECK_BACK_ACTIVITY_ID:
                    Log.i("onActivityResult", "CAPTURE_CHECK_BACK_ACTIVITY_ID -> calling refreshImage()");
                    refreshImage(checkBackImgReviewEditCntrl, TxField.CHECK_BACK);
                    break;
            }
        }


    }

    private void refreshImage(ImgReviewEditCntrl imgCmp, final TxField field){
        Log.i("refreshImage", "refreshImage()");
        try {
            Image image = TxData.getImage(field);
            Log.i("Image = ", "refreshImage() = " + (image != null));

            if(image != null){
                imgCmp.setImage(image);
            }
        } catch (KmcException e) {
            e.printStackTrace();

            new android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage( "Image cannot be shown" )
                    .setPositiveButton(android.R.string.ok, null)
                    .setCancelable(true)
                    .setIcon(R.drawable.error)
                    .show();
        }
    }



//------- TODO move this to another class


    public interface APIService {
        @Multipart
        @POST("FrontTerminal/v1/tx/checkAuth")
        Call<ResponseBody> upload(@Part MultipartBody.Part checkFront, @Part MultipartBody.Part checkBack, @Part("username") RequestBody params);  //, @Body RequestBody params
    }



    public void submit() throws IOException {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Sending Transaction");
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();

        try{
            final List<File> filesToDelete = new ArrayList<>();

            MultipartBody.Part checkFront = RequestBuilder.buildMultipartBody( TxField.CHECK_FRONT, filesToDelete);
            MultipartBody.Part checkBack = RequestBuilder.buildMultipartBody( TxField.CHECK_BACK, filesToDelete);
            RequestBody username = buildStringBody("Tito Robe");

            Call<ResponseBody> call = api.upload(
                    checkFront,
                    checkBack,
                    username
            );

           final TxActivity _this = this;

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {

                for (File file: filesToDelete){
                    file.delete();
                }

                if (mProgressDialog != null && mProgressDialog.isShowing()) mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) mProgressDialog.dismiss();

                new AlertDialog.Builder(_this)
                        .setTitle("Error")
                        .setMessage( t.getMessage() )
                        .setPositiveButton(android.R.string.ok, null)
                        .setCancelable(true)
                        .setIcon(R.drawable.error)
                        .show();
            }
        });

        }catch(Exception e){
            e.printStackTrace();
        }
      }



}
