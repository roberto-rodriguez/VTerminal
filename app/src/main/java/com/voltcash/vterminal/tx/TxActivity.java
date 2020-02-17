package com.voltcash.vterminal.tx;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kofax.kmc.ken.engines.data.Image;
import com.kofax.kmc.kui.uicontrols.ImgReviewEditCntrl;
import com.kofax.kmc.kut.utilities.error.KmcException;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.util.TxData;
import com.voltcash.vterminal.util.TxField;

import static com.voltcash.vterminal.util.Constants.PROCESSED_IMAGE_RETAKE_RESPONSE_ID;

public class TxActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;

    public static final int CAPTURE_CHECK_FRONT_ACTIVITY_ID = 1001;
    public static final int CAPTURE_CHECK_BACK_ACTIVITY_ID = 1002;

    private ImgReviewEditCntrl checkFrontImgReviewEditCntrl;
    private ImgReviewEditCntrl checkBackImgReviewEditCntrl;

    private Integer activeImgActivityId = null;
    private TxField activeImgField = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tx);
        getSupportActionBar().hide();

        checkFrontImgReviewEditCntrl = (ImgReviewEditCntrl) findViewById(R.id.view_check_front_image);
        checkBackImgReviewEditCntrl = (ImgReviewEditCntrl) findViewById(R.id.view_check_back_image);
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
            Log.i("onCaptureClick", "Creating PreviewActivity... ");
            intent = new Intent(this, PreviewActivity.class);
        }else{
            Log.i("onCaptureClick", "Creating CaptureActivity... ");
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

}
