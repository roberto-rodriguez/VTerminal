package com.voltcash.vterminal.views.tx.imageCapture;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.voltcash.vterminal.R;
import com.voltcash.vterminal.util.Constants;
import com.voltcash.vterminal.util.IDScanBarcodeParser;

import net.idscan.components.android.scanpdf417.PDF417ScanActivity;

public class CaptureIDScanActivity extends PDF417ScanActivity {

    @Override
    protected View getViewFinder(LayoutInflater inflater) {
        View v = inflater.inflate(R.layout.idscan_viewfinder, null);

        View old_vf = super.getViewFinder(inflater);
        if (old_vf != null) {
            FrameLayout old_vf_layout = v.findViewById(R.id.old_vf);
            ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            old_vf_layout.addView(old_vf, p);
        }

        v.findViewById(R.id.fab_torch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFlashState(!getFlashState());
            }
        });

        return v;
    }

    @Override
    protected void onData(@NonNull PDF417Data result) {
        try{
            IDScanBarcodeParser.parseData(this.getApplicationContext(), result.barcodeData);
            setResult(Constants.PROCESSED_IMAGE_ACCEPT_RESPONSE_ID);
            finish();
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
