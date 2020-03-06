package com.voltcash.vterminal.views.tx;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.services.TxService;
import com.voltcash.vterminal.util.Constants;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.ReceiptActivity;
import com.voltcash.vterminal.util.TxData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.voltcash.vterminal.util.ViewUtil.buildProgressDialog;

public class TxActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private final PermissionsManager mPermissionsManager = new PermissionsManager(this);
    private ProgressDialog mProgressDialog;

    private ImgReviewEditCntrl checkFrontImgReviewEditCntrl;
    private ImgReviewEditCntrl checkBackImgReviewEditCntrl;
    private ImgReviewEditCntrl idFrontImgReviewEditCntrl;
    private ImgReviewEditCntrl idBackImgReviewEditCntrl;

    private String activeImgField = null;
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
        idFrontImgReviewEditCntrl   = (ImgReviewEditCntrl) findViewById(R.id.tx_id_front_image);
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

        TxData.clear();
        TxData.put(Field.TX.OPERATION, "01");
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
        final ProgressDialog mProgressDialog = buildProgressDialog(this, "Calculating Fees", "Please wait...");

        final String cardNumber = ((EditText)findViewById(R.id.tx_card_field)).getText().toString();
        final String amount = ((EditText)findViewById(R.id.tx_amount_input)).getText().toString();

        TxData.put(Field.TX.CARD_NUMBER, cardNumber);
        TxData.put(Field.TX.AMOUNT, amount);

        ConstraintLayout mainLayout =  mainLayout = (ConstraintLayout)findViewById(R.id.tx_container_layout);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);

        TxService.checkAuthLocationConfig(new ServiceCallback(this) {
            @Override
            public void onSuccess(Map response) {
                Boolean cardExist    = TxData.getBoolean(Field.TX.CARD_EXIST);
                String cardLoadFee   = TxData.getString(Field.TX.CARD_LOAD_FEE);
                String activationFee = TxData.getString(Field.TX.ACTIVATION_FEE);

                TxData.put(Field.TX.CARD_LOAD_FEE , cardLoadFee);
                TxData.put(Field.TX.ACTIVATION_FEE, activationFee);
                TxData.put(Field.TX.CARD_EXIST    , cardExist);


                findViewById(R.id.tx_calculate_fees_layout).setVisibility(View.GONE);
                findViewById(R.id.tx_fees_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.tx_grid_layout).setVisibility(View.VISIBLE);

                if(!cardExist){
                    for(int id : showIfCardNotExist){
                        findViewById(id).setVisibility(View.VISIBLE);
                    }
                }

                ((TextView)findViewById(R.id.tx_amount_text)).setText("Amount: $" + amount);
                ((TextView)findViewById(R.id.tx_fee_text   )).setText("Transaction Fee: $" + cardLoadFee);
                ((TextView)findViewById(R.id.tx_activation_fee_text)).setText("Activation Fee: $" + activationFee);

                if (mProgressDialog != null && mProgressDialog.isShowing()){
                    mProgressDialog.dismiss();
                }
            }
        });
    }

    public void onSubmit(View view){
        final AppCompatActivity _this = this;
        final ProgressDialog mProgressDialog =  buildProgressDialog(this, "Sending Transaction", "Please wait...");

        final String ssn = ((EditText)findViewById(R.id.tx_id_ssn_input)).getText().toString();
        final String phone = ((EditText)findViewById(R.id.tx_id_phone_input)).getText().toString();

        TxData.put(Field.TX.SSN, ssn);
        TxData.put(Field.TX.PHONE, phone);



        TxService.tx(new ServiceCallback(this){
            @Override
            public void onSuccess(Map response) {
                if (mProgressDialog != null && mProgressDialog.isShowing()){
                    mProgressDialog.dismiss();
                }

                Intent intent = new Intent(_this, ReceiptActivity.class);

                Double amount = TxData.getDouble(Field.TX.AMOUNT);
                Double fee = TxData.getDouble(Field.TX.CARD_LOAD_FEE);
                Double payout = amount - fee;
                String card = TxData.getString(Field.TX.CARD_NUMBER);

                Log.i("onSubmit", "amount = " + amount);
                Log.i("onSubmit", "fee = " + fee);
                Log.i("onSubmit", "payout = " + payout);

                if(card != null && card.length() > 4){
                    card = card.substring(card.length() - 4, card.length());
                }

                ArrayList<String> receiptLines = new ArrayList<>();
                receiptLines.add("Card -> **** **** **** " + card);
                receiptLines.add("Deposit Amount -> $ " + amount);
                receiptLines.add("Transaction Fee -> $ " + fee);
                receiptLines.add("Payout Amount -> $ " + payout);

                TxData.clear();

                intent.putStringArrayListExtra(Constants.RECEIPT_LINES, receiptLines);
                startActivity(intent);
            }
        });
    }

    public void onClickCheckFront(View view){
        onCaptureClick(Field.TX.CHECK_FRONT, checkFrontImgReviewEditCntrl, CaptureActivity.class);
    }

    public void onClickCheckBack(View view){
        onCaptureClick(Field.TX.CHECK_BACK, checkBackImgReviewEditCntrl, CaptureActivity.class);
    }

    public void onClickIdFront(View view){
        onCaptureClick(Field.TX.ID_FRONT, idFrontImgReviewEditCntrl, CaptureActivity.class);
    }

    public void onClickIdBack(View view){
        onCaptureClick(Field.TX.ID_BACK, idBackImgReviewEditCntrl, CaptureBarcodeActivity.class);
    }

    protected void onCaptureClick(String field, ImgReviewEditCntrl imgCmp, Class captureClazz){
        activeImgField = field;
        activeImgCmp = imgCmp;

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Please wait");
        mProgressDialog.setMessage("Initializing...");
        mProgressDialog.show();

        Intent intent = null;

        if(activeImgField != Field.TX.ID_BACK && TxData.contains(activeImgField)){
            intent = new Intent(this,   PreviewActivity.class);
        }else{
            intent = new Intent(this, captureClazz );
        }

        intent.putExtra(Field.TX.TX_FIELD , activeImgField);
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

                    if(activeImgField == Field.TX.ID_BACK){
                        barCodeFoundEvent = TxData.BARCODE_EVENT;
                        image = barCodeFoundEvent.getImage();
                    }else{
                        image = TxData.getImage(activeImgField);
                    }

                        activeImgCmp.setImage(image);


                    if(activeImgField == Field.TX.ID_BACK){
                        TxData.put(Field.TX.ID_BACK, barCodeFoundEvent.getImage());
                        TxData.put(Field.TX.DL_DATA_SCAN, barCodeFoundEvent.getBarCode().getValue());
                     }
                    break;

                case Constants.PROCESSED_IMAGE_RETAKE_RESPONSE_ID:
                    Intent intent = new Intent(this, CaptureActivity.class);
                    intent.putExtra(Field.TX.TX_FIELD , activeImgField);
                    startActivityForResult(intent, Constants.TAKE_IMAGE_REQUEST_ID);
                    break;
            }

        }catch(Exception e){
            e.printStackTrace();
        }


    }

}
