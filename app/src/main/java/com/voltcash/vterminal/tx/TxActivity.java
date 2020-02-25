package com.voltcash.vterminal.tx;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kofax.kmc.ken.engines.data.Image;
import com.kofax.kmc.kui.uicontrols.BarCodeFoundEvent;
import com.kofax.kmc.kui.uicontrols.ImgReviewEditCntrl;
import com.kofax.kmc.kut.utilities.AppContextProvider;
import com.kofax.kmc.kut.utilities.Licensing;
import com.kofax.samples.common.License;
import com.kofax.samples.common.PermissionsManager;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.interfaces.ServiceCallerActivity;
import com.voltcash.vterminal.services.TxServiceFactory;
import com.voltcash.vterminal.services.impl.TxServiceImpl;
import com.voltcash.vterminal.util.Constants;
import com.voltcash.vterminal.util.TxData;
import com.voltcash.vterminal.util.TxField;
import com.voltcash.vterminal.util.ViewUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TxActivity extends ServiceCallerActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private final PermissionsManager mPermissionsManager = new PermissionsManager(this);
    private ProgressDialog mProgressDialog;

    private ImgReviewEditCntrl checkFrontImgReviewEditCntrl;
    private ImgReviewEditCntrl checkBackImgReviewEditCntrl;
    private ImgReviewEditCntrl idFrontImgReviewEditCntrl;
    private ImgReviewEditCntrl idBackImgReviewEditCntrl;

    private TxField activeImgField = null;
    private ImgReviewEditCntrl activeImgCmp = null;

    private static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private List<Integer> showIfCardNotExist = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tx);

        setTitle("Check Transaction");

        checkFrontImgReviewEditCntrl= (ImgReviewEditCntrl) findViewById(R.id.tx_check_front_image);
        checkBackImgReviewEditCntrl = (ImgReviewEditCntrl) findViewById(R.id.tx_check_back_image);
        idFrontImgReviewEditCntrl    = (ImgReviewEditCntrl) findViewById(R.id.tx_id_front_image);
        idBackImgReviewEditCntrl    = (ImgReviewEditCntrl) findViewById(R.id.tx_id_back_image);

        if (!mPermissionsManager.isGranted(PERMISSIONS)) {
            mPermissionsManager.request(PERMISSIONS);
        }

        AppContextProvider.setContext(getApplicationContext());
        Licensing.setMobileSDKLicense(License.PROCESS_PAGE_SDK_LICENSE);

        showIfCardNotExist = Arrays.asList(
                R.id.tx_id_front_wrapper,
                R.id.tx_id_back_wrapper,
                R.id.tx_id_ssn_input,
                R.id.tx_id_phone_input
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        if (!mPermissionsManager.isGranted(PERMISSIONS)) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.permissions_rationale)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }


    public void onCalculateFees(View view){
        String card = ((EditText)findViewById(R.id.tx_card_field)).getText().toString();
        String amount = ((EditText)findViewById(R.id.tx_amount_input)).getText().toString();

        TxData.put(TxField.CARD_NUMBER, card);
        TxData.put(TxField.AMOUNT, amount);

        TxServiceFactory.checkAuthLocationConfig(this, card, amount, "01");
    }



    public void onSubmit(View view){
        TxServiceFactory.submitTx(this);
    }

    public void onClickCheckFront(View view){
        onCaptureClick(TxField.CHECK_FRONT, checkFrontImgReviewEditCntrl, CaptureActivity.class);
    }

    public void onClickCheckBack(View view){
        onCaptureClick(TxField.CHECK_BACK, checkBackImgReviewEditCntrl, CaptureActivity.class);
    }

    public void onClickIdFront(View view){
        onCaptureClick(TxField.ID_FRONT, idFrontImgReviewEditCntrl, CaptureActivity.class);
    }

    public void onClickIdBack(View view){
        onCaptureClick(TxField.ID_BACK, idBackImgReviewEditCntrl, CaptureBarcodeActivity.class);
    }

    protected void onCaptureClick(TxField field, ImgReviewEditCntrl imgCmp, Class captureClazz){
        activeImgField = field;
        activeImgCmp = imgCmp;

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Please wait");
        mProgressDialog.setMessage("Initializing...");
        mProgressDialog.show();

        Intent intent = null;

        if(activeImgField != TxField.ID_BACK && TxData.contains(activeImgField)){
            intent = new Intent(this,   PreviewActivity.class);
        }else{
            intent = new Intent(this, captureClazz );
        }

        intent.putExtra(TxField.TX_FIELD.getName(), activeImgField);
        startActivityForResult(intent, Constants.TAKE_IMAGE_REQUEST_ID);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mProgressDialog != null && mProgressDialog.isShowing()) mProgressDialog.dismiss();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("TxActivity", "onActivityResult() requestCode = " + requestCode + " , resultCode = " + resultCode);

        try{

            switch(resultCode){
                case Constants.PROCESSED_IMAGE_ACCEPT_RESPONSE_ID:

                    Image image = null;
                    BarCodeFoundEvent barCodeFoundEvent = null;

                    if(activeImgField == TxField.ID_BACK){
                        barCodeFoundEvent = TxData.BARCODE_EVENT;
                        image = barCodeFoundEvent.getImage();
                    }else{
                        image = TxData.getImage(activeImgField);
                    }

                        activeImgCmp.setImage(image);


                    if(activeImgField == TxField.ID_BACK){
                        ViewUtil.showError(this, "Success", barCodeFoundEvent.getBarCode().getValue() );
                     }
                    break;

                case Constants.PROCESSED_IMAGE_RETAKE_RESPONSE_ID:
                    Intent intent = new Intent(this, CaptureActivity.class);
                    intent.putExtra(TxField.TX_FIELD.getName(), activeImgField);
                    startActivityForResult(intent, Constants.TAKE_IMAGE_REQUEST_ID);
                    break;
            }

        }catch(Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public void onServiceCallback(String serviceType, Boolean success) {

        switch(serviceType ){
            case TxServiceImpl.CHECK_AUTH_LOCATION_CONFIG:
                findViewById(R.id.tx_calculate_fees_layout).setVisibility(View.GONE);
                findViewById(R.id.tx_fees_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.tx_grid_layout).setVisibility(View.VISIBLE);

                Boolean cardExist    = TxData.getBoolean(TxField.CARD_EXIST);
                String cardLoadFee   = TxData.getString(TxField.CARD_LOAD_FEE);
                String activationFee   = TxData.getString(TxField.ACTIVATION_FEE);

                if(!cardExist){
                    for(int id : showIfCardNotExist){
                        findViewById(id).setVisibility(View.VISIBLE);
                    }
                }

                ((TextView)findViewById(R.id.tx_amount_text)).setText("Amount: " + TxData.getAmount(TxField.AMOUNT));
                ((TextView)findViewById(R.id.tx_fee_text)).setText("Transaction Fee: " + cardLoadFee);
                ((TextView)findViewById(R.id.tx_activation_fee_text)).setText("Activation Fee: " + activationFee);
                break;
            case TxServiceImpl.CHECK_AUTH:

        }
    }
}
