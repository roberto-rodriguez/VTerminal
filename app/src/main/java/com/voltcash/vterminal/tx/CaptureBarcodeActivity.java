package com.voltcash.vterminal.tx;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.kofax.kmc.ken.engines.data.DocumentDetectionSettings;
import com.kofax.kmc.ken.engines.data.Image;
import com.kofax.kmc.kui.uicontrols.BarCodeCaptureView;
import com.kofax.kmc.kui.uicontrols.BarCodeFoundEvent;
import com.kofax.kmc.kui.uicontrols.BarCodeFoundListener;
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
import com.kofax.kmc.kui.uicontrols.data.GuidingLine;
import com.kofax.kmc.kui.uicontrols.data.Symbology;
import com.kofax.kmc.kut.utilities.AppContextProvider;
import com.kofax.kmc.kut.utilities.Licensing;
import com.kofax.samples.common.License;
import com.kofax.samples.common.PermissionsManager;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.util.Constants;
import com.voltcash.vterminal.util.SettingsHelperClass;
import com.voltcash.vterminal.util.TxData;
import com.voltcash.vterminal.util.TxField;

import java.util.ArrayList;
import java.util.Date;

public class CaptureBarcodeActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback, CameraInitializationListener, BarCodeFoundListener, /*ImageCapturedListener,*/ CameraInitializationFailedListener {


    private final PermissionsManager mPermissionsManager = new PermissionsManager(this);

    private boolean mTorchFlag = false;
    private FloatingActionButton mFabTorch;

    private BarCodeCaptureView mBarcodeCaptureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("CaptureBarcodeActivity", "onCreate - 1 ");
        super.onCreate(savedInstanceState);

        Log.i("CaptureBarcodeActivity", "onCreate - 2 ");

            setUp();
    }


    private void setUp() {
        Log.i("CaptureBarcodeActivity", "setUp - 1 ");
        setContentView(R.layout.activity_capture_barcode);
        Log.i("CaptureBarcodeActivity", "setUp - 2");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.i("CaptureBarcodeActivity", "setUp - 3 ");
        mBarcodeCaptureView = (BarCodeCaptureView) findViewById(R.id.barcode_capture);
        Log.i("CaptureBarcodeActivity", "setUp - 4 ");
        mBarcodeCaptureView.addCameraInitializationListener(this);
        Log.i("CaptureBarcodeActivity", "setUp - 5 ");
        mBarcodeCaptureView.addBarCodeFoundEventListener(this);
        Log.i("CaptureBarcodeActivity", "setUp - 6 ");
        mBarcodeCaptureView.setGuidingLine(GuidingLine.LANDSCAPE);
        Log.i("CaptureBarcodeActivity", "setUp - 7");

        ArrayList<Symbology> symbsList = new ArrayList<>();
         symbsList.add(Symbology.PDF417);

        Log.i("CaptureBarcodeActivity", "setUp - 8 ");

        Symbology[] symbs = new Symbology[symbsList.size()];
        symbs = symbsList.toArray(symbs);

        Log.i("CaptureBarcodeActivity", "setUp - 9 ");

        mBarcodeCaptureView.setSymbologies(symbs);
        mBarcodeCaptureView.readBarcode();

        Log.i("CaptureBarcodeActivity", "setUp - 10 ");
        if (Constants.IS_TORCH_SUPPORTED) {
            mFabTorch = (FloatingActionButton) findViewById(R.id.fab_torch);

            mFabTorch.setVisibility(View.VISIBLE);

            mFabTorch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFabTorch.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), (mTorchFlag) ? R.drawable.torchoff : R.drawable.torchon));
                    mBarcodeCaptureView.setFlash((mTorchFlag) ? Flash.OFF : Flash.TORCH);
                    mTorchFlag = !mTorchFlag;
                }
            });
        }

        Log.i("CaptureBarcodeActivity", "setUp - 11 ");
    }

    @Override
    protected void onResume() {
        Log.i("CaptureBarcodeActivity", "onResume ");
        super.onResume();
        mBarcodeCaptureView.readBarcode();
    }

    @Override
    public void onCameraInitialized(CameraInitializationEvent arg0) {
        Log.i("CaptureBarcodeActivity", "onCameraInitialized ");
        mBarcodeCaptureView.setUseVideoFrame(true);
        mBarcodeCaptureView.setFlash(Flash.OFF);

        Toast.makeText( this, "Camera was initialized successfuly", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCameraInitializationFailed(CameraInitializationFailedEvent event) {
        Log.i("CaptureBarcodeActivity", "onCameraInitializationFailed -> ");
        String message = event.getCause().getMessage();
        Log.i("CaptureBarcodeActivity", "onCameraInitializationFailed -> " + message);
        if (message == null || message.equals("")) {
            message = getResources().getString(R.string.camera_unavailable);
        }
        Toast.makeText( this, message, Toast.LENGTH_LONG).show();
       // onBackPressed();
    }

    @Override
    public void barCodeFound(BarCodeFoundEvent event) {
        Log.i("CaptureBarcodeActivity", "barCodeFound  ");
        TxData.BARCODE_EVENT = event;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.contains("pref_key_shots_count") || !prefs.contains("pref_key_last_usage_date")) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("pref_key_shots_count", 0);
            editor.putLong("pref_key_last_usage_date", new Date().getTime());
            editor.apply();
        } else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("pref_key_shots_count", (prefs.getInt("pref_key_shots_count", -1) + 1));
            editor.putLong("pref_key_last_usage_date", new Date().getTime());
            editor.apply();
        }

        setResult(Constants.PROCESSED_IMAGE_ACCEPT_RESPONSE_ID);
        finish();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.i("CaptureBarcodeActivity", "onActivityResult  ");
//        super.onActivityResult(requestCode, resultCode, data);
//
//        switch (requestCode) {
//            case Constants.BARCODE_FOUND_REQUEST_ID:
//                if (resultCode == RESULT_OK || resultCode == Constants.PROCESSED_IMAGE_EMAIL_IS_SENT_RESPONSE_ID) {
//                    finish();
//                }
//                if (resultCode == RESULT_OK || resultCode == Constants.PROCESSED_IMAGE_RETAKE_RESPONSE_ID) {
//                  //  Constants.BARCODE_EVENT = null;
//                }
//                break;
//        }
//    }

    @Override
    public void onBackPressed() {
        Log.i("CaptureBarcodeActivity", "onBackPressed  ");
        super.onBackPressed();
        finish();
    }
}
