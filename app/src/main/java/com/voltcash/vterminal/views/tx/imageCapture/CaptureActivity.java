package com.voltcash.vterminal.views.tx.imageCapture;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import com.kofax.kmc.ken.engines.data.DocumentDetectionSettings;
import com.kofax.kmc.ken.engines.data.Image;
import com.kofax.kmc.kui.uicontrols.CameraInitializationEvent;
import com.kofax.kmc.kui.uicontrols.CameraInitializationFailedEvent;
import com.kofax.kmc.kui.uicontrols.CameraInitializationFailedListener;
import com.kofax.kmc.kui.uicontrols.CameraInitializationListener;
import com.kofax.kmc.kui.uicontrols.ImageCaptureView;
import com.kofax.kmc.kui.uicontrols.ImageCapturedEvent;
import com.kofax.kmc.kui.uicontrols.ImageCapturedListener;
import com.kofax.kmc.kui.uicontrols.captureanimations.DocumentCaptureExperience;
import com.kofax.kmc.kui.uicontrols.captureanimations.DocumentCaptureExperienceCriteriaHolder;
import com.kofax.kmc.kui.uicontrols.data.Flash;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.util.*;
import com.voltcash.vterminal.views.MainActivity;


public class CaptureActivity extends AppCompatActivity
        implements  CameraInitializationListener, ImageCapturedListener, CameraInitializationFailedListener {

    private static final String TAG = CaptureActivity.class.getSimpleName();

    private String field;

    private boolean mTorchFlag = true;
    private FloatingActionButton mFabTorch;

    private ImageCaptureView mImageCaptureView;
    private FloatingActionButton mForceCapture;
    private DocumentCaptureExperience mDocumentCaptureExperience;

    public ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.field = (String)getIntent().getExtras().get( Field.TX.TX_FIELD);
        setUp();
    }



    private void setUp() {
        try {
            Constants.RAW_IMAGE = null;
            final CaptureActivity _this = this;

            setContentView(R.layout.activity_capture);

            mImageCaptureView = (ImageCaptureView) findViewById(R.id.view_capture);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            mImageCaptureView.addCameraInitializationListener(this);

            SettingsHelperClass.DeviceDeclinationResult declinationPitchRes = SettingsHelperClass.getDeviceDeclinationPitch(this);
            if (declinationPitchRes.result)
                mImageCaptureView.setDeviceDeclinationPitch(declinationPitchRes.value);

            SettingsHelperClass.DeviceDeclinationResult declinationRollRes = SettingsHelperClass.getDeviceDeclinationRoll(this);
            if (declinationRollRes.result)
                mImageCaptureView.setDeviceDeclinationRoll(declinationRollRes.value);

            mDocumentCaptureExperience = new DocumentCaptureExperience(mImageCaptureView);
            DocumentCaptureExperienceCriteriaHolder criteriaHolder = new DocumentCaptureExperienceCriteriaHolder();

            DocumentDetectionSettings settings = new DocumentDetectionSettings();
            settings.setTargetFramePaddingPercent(8.0);
            criteriaHolder.setDetectionSettings(settings);
            mDocumentCaptureExperience.setCaptureCriteria(criteriaHolder);

            mDocumentCaptureExperience.addOnImageCapturedListener(this);

            mDocumentCaptureExperience.takePicture();

            mForceCapture = (FloatingActionButton) findViewById(R.id.fab_force_capture);
            mForceCapture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog = (ProgressDialog) DialogUtils.showProgress(_this, "Processing Image", "Please wait...", new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            _this.onBackPressed();
                        }
                    });
                    mImageCaptureView.forceTakePicture();
                }
            });

            mFabTorch = (FloatingActionButton) findViewById(R.id.fab_torch);

            mFabTorch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTorchClick();
                }
            });

        }catch(Exception e){
           GlobalExceptionHandler.catchException(this, "CaptureActivity.setup()", e);
        }
    }

    private void onTorchClick(){
        mFabTorch.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), (mTorchFlag) ? R.drawable.torchoff : R.drawable.torchon));
        mImageCaptureView.setFlash((mTorchFlag) ? Flash.OFF : Flash.TORCH);
        mTorchFlag = !mTorchFlag;
    }

    @Override
    protected void onResume() {
        super.onResume();

        dismissDialog();

        int manualCaptureTimeVal = 0;

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, manualCaptureTimeVal*1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        dismissDialog();
        Constants.RAW_IMAGE = null;

        switch(resultCode){
             case Constants.PROCESSED_IMAGE_ACCEPT_RESPONSE_ID:
                setResult(Constants.PROCESSED_IMAGE_ACCEPT_RESPONSE_ID);
                finish();
                break;
        }
    }


    @Override
    public void onCameraInitialized(CameraInitializationEvent arg0) {
        mImageCaptureView.setUseVideoFrame(true);
      //  mImageCaptureView.setFlash(Flash.OFF);

        dismissDialog();

        onTorchClick();
    }

    private void dismissDialog(){
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
    }

    @Override
    public void onImageCaptured(final ImageCapturedEvent imageCapturedEvent) {
        final CaptureActivity _this = this;

        try{
            dismissDialog();

            progressDialog = (ProgressDialog) DialogUtils.showProgress(_this, "Capturing Image", "Please wait...", new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    _this.onBackPressed();
                }
            });

            Log.i("onImageCaptured",   " ----------");
            if (imageCapturedEvent != null) {
                if (imageCapturedEvent.getImage() != null) {
                    Image image = imageCapturedEvent.getImage();

                    Intent intent = new Intent(getApplicationContext(), PreviewActivity.class);
                    intent.putExtra( Field.TX.TX_FIELD , field);
                    Constants.RAW_IMAGE = image;
                    startActivityForResult(intent, Constants.PROCESSED_IMAGE_REQUEST_ID);

                } else {
                    dismissDialog();
                    Log.i("onImageCaptured",   "imageCapturedEvent = null");
                  //  onBackPressed();
                }
        }else{
            dismissDialog();
            Log.i("onImageCaptured",   "imageCapturedEvent = null");
        }

    }catch(Exception e){
        GlobalExceptionHandler.catchException(this, "CaptureActivity.onImageCaptured()", e);
    }
    }

    @Override
    public void onCameraInitializationFailed(CameraInitializationFailedEvent event) {
        String message = event.getCause().getMessage();
        if (message == null || message.equals("")) {
            message = getResources().getString(R.string.camera_unavailable);
        }
        onBackPressed();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDocumentCaptureExperience != null) {
            mDocumentCaptureExperience.removeOnImageCapturedListener(this);
            mDocumentCaptureExperience.destroy();
        }
    }

}
