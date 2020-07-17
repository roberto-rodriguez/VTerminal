package com.voltcash.vterminal.views.tx.imageCapture;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.kofax.kmc.ken.engines.ImageProcessor;
import com.kofax.kmc.ken.engines.data.Image;
import com.kofax.kmc.ken.engines.processing.ColorDepth;
import com.kofax.kmc.ken.engines.processing.ImageProcessorConfiguration;
import com.kofax.kmc.ken.engines.processing.RotateType;
import com.kofax.kmc.kui.uicontrols.ImgReviewEditCntrl;
import com.kofax.kmc.kut.utilities.error.ErrorInfo;
import com.kofax.kmc.kut.utilities.error.KmcException;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.util.Constants;
import com.voltcash.vterminal.util.DialogUtils;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.Settings;
import com.voltcash.vterminal.util.SettingsHelperClass;
import com.voltcash.vterminal.util.TxData;
import com.voltcash.vterminal.util.ViewUtil;


public class PreviewActivity extends AppCompatActivity implements ImageProcessor.ImageOutListener {

    private ImgReviewEditCntrl mImgReviewEditCntrl;

    private FloatingActionButton mfabRetake;
    private FloatingActionButton mfabGoToProcessing;
    private ProgressDialog progressDialog;
    private boolean isProgressDialog;

    private String field;

    private boolean isProcessedImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v("PreviewActivity", "onCreate - 1");
        setContentView(R.layout.activity_preview);

        this.field = (String)getIntent().getExtras().get(Field.TX.TX_FIELD);



        mImgReviewEditCntrl = (ImgReviewEditCntrl) findViewById(R.id.view_review1);

        mfabRetake = (FloatingActionButton) findViewById(R.id.fab_retake);
       // mfabRetake.setVisibility(View.GONE);
        mfabRetake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("PreviewActivity",   "PreviewActivity.super.onBackPressed();" );
                dismissDialog();
                setResult(Constants.PROCESSED_IMAGE_RETAKE_RESPONSE_ID);
                PreviewActivity.super.onBackPressed();
            }
        });

        mfabGoToProcessing = (FloatingActionButton) findViewById(R.id.fab_go_to_processing);
     //   mfabGoToProcessing.setVisibility(View.GONE);

        final PreviewActivity _this = this;

        mfabGoToProcessing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isProcessedImage){
                    TxData.put( field, mImgReviewEditCntrl.getImage());

                    Log.i("PreviewActivity",   "setResult(Constants.PROCESSED_IMAGE_ACCEPT_RESPONSE_ID);" );

                    setResult(Constants.PROCESSED_IMAGE_ACCEPT_RESPONSE_ID);
                    finish();
                }else{
                    progressDialog = (ProgressDialog) DialogUtils.showProgress(_this, "Processing Image", "Please wait...", new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            _this.onBackPressed();
                        }
                    });

                    processImage( mImgReviewEditCntrl.getImage());
                }


            }
        });

        Boolean SHOW_PROCESSED_IMAGE = (Boolean)getIntent().getExtras().get("SHOW_PROCESSED_IMAGE");

        Image image = null;

        if(SHOW_PROCESSED_IMAGE != null && SHOW_PROCESSED_IMAGE == true){
            image = TxData.getImage(this.field); //Coming from TxFragment
            isProcessedImage = true;
        }else{
            image = Constants.RAW_IMAGE; //Coming from image capture
        }

        if(image == null){
            ViewUtil.showError(this, "Image is null", "Preview Activity received a null image");
        }else{
            showImage(image);
        }

      //  showImage(TxData.getImage(field));

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
        dismissDialog();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        dismissDialog();
        setResult(Constants.PROCESSED_IMAGE_RETAKE_RESPONSE_ID);
        finish();
    }

    private void showImage(Image srcImage)  {
        try{
            mImgReviewEditCntrl.setImage(srcImage);

//            mfabRetake.setVisibility(View.VISIBLE);
//            mfabGoToProcessing.setVisibility(View.VISIBLE);

           dismissDialog();
        }catch(Exception e){
            e.printStackTrace();

            ViewUtil.showError(this, "Error Showing Image", e.getMessage());
        }
    }

    private void processImage(Image srcImage) {
        ImageProcessor imageProcessor = new ImageProcessor();
        imageProcessor.addImageOutEventListener(this);
        ImageProcessorConfiguration imageProcessingConfiguration = SettingsHelperClass.getImageProcessorConfiguration(this);

        switch (field){
            case Field.TX.CHECK_BACK:
                //  imageProcessingConfiguration.outputDPI = 130;
                imageProcessingConfiguration.rotateType = RotateType.ROTATE_270;
                //   imageProcessingConfiguration.documentDimensions = new DocumentDimensions(200F, 500F);
          //      break;

            case Field.TX.ID_FRONT:
            case Field.TX.CHECK_FRONT:
                imageProcessingConfiguration.outputColorDepth = ColorDepth.COLOR;
                break;
        }

        if(Field.TX.CHECK_FRONT.equals(field)){
            imageProcessingConfiguration.outputDPI = Settings.CHECK_RESOLUTION;
        }

        try {
            imageProcessor.processImage(srcImage, imageProcessingConfiguration);
        } catch (KmcException e) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage( "Image processing failed" )
                    .setPositiveButton(android.R.string.ok, null)
                    .setCancelable(true)
                    .setIcon(R.drawable.error)
                    .show();
        }
    }

    @Override
    public void imageOut(ImageProcessor.ImageOutEvent event) {
        if (event.getStatus() == ErrorInfo.KMC_SUCCESS) {
            try {
                dismissDialog();

                showImage(event.getImage());

                isProcessedImage = true;
            } catch (Exception e) {
                new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage( "Image processing failed" )
                        .setPositiveButton(android.R.string.ok, null)
                        .setCancelable(true)
                        .setIcon(R.drawable.error)
                        .show();
            }
        }
    }

    private void dismissDialog(){
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
    }
}
