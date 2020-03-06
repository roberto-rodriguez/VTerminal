package com.voltcash.vterminal.tx;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
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

public class CaptureActivity extends AppCompatActivity
        implements  CameraInitializationListener, ImageCapturedListener, CameraInitializationFailedListener {

    private static final String TAG = CaptureActivity.class.getSimpleName();

    private String field;

    private boolean mTorchFlag = false;
    private FloatingActionButton mFabTorch;

    private ImageCaptureView mImageCaptureView;
    private FloatingActionButton mForceCapture;
    private DocumentCaptureExperience mDocumentCaptureExperience;

    public ProgressDialog mProgressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.field = (String)getIntent().getExtras().get( Field.TX.TX_FIELD);
        setUp();
    }



    private void setUp() {
        setContentView(R.layout.activity_capture);

        mImageCaptureView = (ImageCaptureView) findViewById(R.id.view_capture);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mImageCaptureView.addCameraInitializationListener(this);

        SettingsHelperClass.DeviceDeclinationResult declinationPitchRes = SettingsHelperClass.getDeviceDeclinationPitch(this);
        if (declinationPitchRes.result) mImageCaptureView.setDeviceDeclinationPitch(declinationPitchRes.value);

        SettingsHelperClass.DeviceDeclinationResult declinationRollRes = SettingsHelperClass.getDeviceDeclinationRoll(this);
        if (declinationRollRes.result) mImageCaptureView.setDeviceDeclinationRoll(declinationRollRes.value);

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
                mImageCaptureView.forceTakePicture();
            }
        });

        if (Constants.IS_TORCH_SUPPORTED) {
            mFabTorch = (FloatingActionButton) findViewById(R.id.fab_torch);

            mFabTorch.setVisibility(View.VISIBLE);

            mFabTorch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFabTorch.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), (mTorchFlag) ? R.drawable.torchoff : R.drawable.torchon));
                    mImageCaptureView.setFlash((mTorchFlag) ? Flash.OFF : Flash.TORCH);
                    mTorchFlag = !mTorchFlag;
                }
            });
        }

        FloatingActionButton fabGallery = (FloatingActionButton) findViewById(R.id.fab_gallery);

        if (SettingsHelperClass.isGalleryEnabled(this)) {
            fabGallery.setVisibility(View.VISIBLE);
            fabGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, Constants.GALLERY_IMPORT_REQUEST_ID);
            }
            });
        } else {
            fabGallery.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mForceCapture.setVisibility(View.GONE);

        int manualCaptureTimeVal = SettingsHelperClass.getManualCaptureTime(this);
        if (manualCaptureTimeVal == -1) manualCaptureTimeVal = Constants.DEFAULT_MANUAL_CAPTURE_TIME;

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mForceCapture.setVisibility(View.VISIBLE);
            }
        }, manualCaptureTimeVal*1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDocumentCaptureExperience != null) {
            mDocumentCaptureExperience.removeOnImageCapturedListener(this);
            mDocumentCaptureExperience.destroy();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(resultCode){
             case Constants.PROCESSED_IMAGE_ACCEPT_RESPONSE_ID:
                setResult(Constants.PROCESSED_IMAGE_ACCEPT_RESPONSE_ID);
                finish();
                break;
        }

    }

    private Image decodeImageFromIntent(Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        if (selectedImage == null) return null;

        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

        if (cursor == null) return null;

        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);

        Log.i("sendImage", "filePath:: " +filePath);
        cursor.close();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        if(bitmap == null) {
            return null;
        }


        return new Image(bitmap);
    }


    @Override
    public void onCameraInitialized(CameraInitializationEvent arg0) {
        mImageCaptureView.setUseVideoFrame(true);
        mImageCaptureView.setFlash(Flash.OFF);

        if (mProgressDialog != null && mProgressDialog.isShowing()) mProgressDialog.dismiss();
    }

    @Override
    public void onImageCaptured(final ImageCapturedEvent imageCapturedEvent) {
        Log.i("onImageCaptured",   " ----------");
        if (imageCapturedEvent != null) {
            if (imageCapturedEvent.getImage() != null) {
              //  Constants.RESULT_IMAGE = imageCapturedEvent.getImage();
                TxData.put(field, imageCapturedEvent.getImage());

                Intent intent = new Intent(getApplicationContext(), com.voltcash.vterminal.tx.PreviewActivity.class);
                intent.putExtra( Field.TX.TX_FIELD , field);
                startActivityForResult(intent, Constants.PROCESSED_IMAGE_REQUEST_ID);
            } else {
                onBackPressed();
            }
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


}
