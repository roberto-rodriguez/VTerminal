package com.voltcash.vterminal.views.tx;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import com.kofax.kmc.kui.uicontrols.BarCodeCaptureView;
import com.kofax.kmc.kui.uicontrols.BarCodeFoundEvent;
import com.kofax.kmc.kui.uicontrols.BarCodeFoundListener;
import com.kofax.kmc.kui.uicontrols.CameraInitializationEvent;
import com.kofax.kmc.kui.uicontrols.CameraInitializationFailedEvent;
import com.kofax.kmc.kui.uicontrols.CameraInitializationFailedListener;
import com.kofax.kmc.kui.uicontrols.CameraInitializationListener;
import com.kofax.kmc.kui.uicontrols.data.Flash;
import com.kofax.kmc.kui.uicontrols.data.GuidingLine;
import com.kofax.kmc.kui.uicontrols.data.Symbology;
import com.kofax.samples.common.PermissionsManager;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.util.Constants;
import com.voltcash.vterminal.util.TxData;
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
        super.onCreate(savedInstanceState);
        setUp();
    }


    private void setUp() {
        setContentView(R.layout.activity_capture_barcode);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mBarcodeCaptureView = (BarCodeCaptureView) findViewById(R.id.barcode_capture);
        mBarcodeCaptureView.addCameraInitializationListener(this);
        mBarcodeCaptureView.addBarCodeFoundEventListener(this);
        mBarcodeCaptureView.setGuidingLine(GuidingLine.LANDSCAPE);

        ArrayList<Symbology> symbsList = new ArrayList<>();
         symbsList.add(Symbology.PDF417);


        Symbology[] symbs = new Symbology[symbsList.size()];
        symbs = symbsList.toArray(symbs);


        mBarcodeCaptureView.setSymbologies(symbs);
        mBarcodeCaptureView.readBarcode();

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

    }

    @Override
    protected void onResume() {
        super.onResume();
        mBarcodeCaptureView.readBarcode();
    }

    @Override
    public void onCameraInitialized(CameraInitializationEvent arg0) {
        mBarcodeCaptureView.setUseVideoFrame(true);
        mBarcodeCaptureView.setFlash(Flash.OFF);
    }

    @Override
    public void onCameraInitializationFailed(CameraInitializationFailedEvent event) {
        String message = event.getCause().getMessage();
        if (message == null || message.equals("")) {
            message = getResources().getString(R.string.camera_unavailable);
        }
    }

    @Override
    public void barCodeFound(BarCodeFoundEvent event) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
