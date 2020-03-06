package com.voltcash.vterminal.views.tx;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.kofax.kmc.ken.engines.ImageProcessor;
import com.kofax.kmc.ken.engines.data.Image;
import com.kofax.kmc.ken.engines.processing.ColorDepth;
import com.kofax.kmc.ken.engines.processing.ImageProcessorConfiguration;
import com.kofax.kmc.kui.uicontrols.ImgReviewEditCntrl;
import com.kofax.kmc.kut.utilities.error.ErrorInfo;
import com.kofax.kmc.kut.utilities.error.KmcException;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.util.Constants;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.SettingsHelperClass;
import com.voltcash.vterminal.util.TxData;
import com.voltcash.vterminal.util.ViewUtil;

import java.util.Date;

public class PreviewActivity extends AppCompatActivity{

    private ImgReviewEditCntrl mImgReviewEditCntrl;

    private FloatingActionButton mfabRetake;
    private FloatingActionButton mfabGoToProcessing;
    private ProgressDialog mProgressDialog;
    private boolean isProgressDialog;

    private String field;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        this.field = (String)getIntent().getExtras().get(Field.TX.TX_FIELD);



        mImgReviewEditCntrl = (ImgReviewEditCntrl) findViewById(R.id.view_review1);

        mfabRetake = (FloatingActionButton) findViewById(R.id.fab_retake);
        mfabRetake.setVisibility(View.GONE);
        mfabRetake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("PreviewActivity",   "PreviewActivity.super.onBackPressed();" );
                setResult(Constants.PROCESSED_IMAGE_RETAKE_RESPONSE_ID);
                PreviewActivity.super.onBackPressed();
            }
        });

        mfabGoToProcessing = (FloatingActionButton) findViewById(R.id.fab_go_to_processing);
        mfabGoToProcessing.setVisibility(View.GONE);
        mfabGoToProcessing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
         //   Constants.RESULT_IMAGE = mImgReviewEditCntrl.getImage();
                TxData.put( field, mImgReviewEditCntrl.getImage());

                Log.i("PreviewActivity",   "setResult(Constants.PROCESSED_IMAGE_ACCEPT_RESPONSE_ID);" );

            setResult(Constants.PROCESSED_IMAGE_ACCEPT_RESPONSE_ID);
            finish();
            }
        });

        showImage(TxData.getImage(field));

//        if (mProgressDialog == null) {
//            isProgressDialog = true;
//            mProgressDialog = new ProgressDialog(this);
//            mProgressDialog.setTitle("Please wait");
//            mProgressDialog.setMessage("Image is processing...");
//            mProgressDialog.setCanceledOnTouchOutside(false);
//            mProgressDialog.setCancelable(false);
//            mProgressDialog.show();
//        } else {
//            if (isProgressDialog && !mProgressDialog.isShowing()) mProgressDialog.show();
//        }
    }

    protected void onResume() {
        super.onResume();
        if (mProgressDialog != null && isProgressDialog && !mProgressDialog.isShowing()) mProgressDialog.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        setResult(Constants.PROCESSED_IMAGE_RETAKE_RESPONSE_ID);
        finish();
    }

    private void showImage(Image srcImage)  {
        try{
            mImgReviewEditCntrl.setImage(srcImage);

            mfabRetake.setVisibility(View.VISIBLE);
            mfabGoToProcessing.setVisibility(View.VISIBLE);

            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                isProgressDialog = false;
            }
        }catch(Exception e){
            e.printStackTrace();

            ViewUtil.showError(this, "Error Showing Image", e.getMessage());
        }
    }
}
