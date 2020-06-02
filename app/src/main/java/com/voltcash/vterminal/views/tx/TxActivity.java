package com.voltcash.vterminal.views.tx;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import com.kofax.kmc.ken.engines.data.Image;
import com.kofax.kmc.kui.uicontrols.BarCodeFoundEvent;
import com.kofax.kmc.kui.uicontrols.ImgReviewEditCntrl;
import com.kofax.kmc.kut.utilities.AppContextProvider;
import com.kofax.kmc.kut.utilities.Licensing;
import com.kofax.samples.common.License;
import com.kofax.samples.common.PermissionsManager;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.VTerminal;
import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.services.TxService;
import com.voltcash.vterminal.util.Constants;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.PreferenceUtil;
import com.voltcash.vterminal.util.StringUtil;
import com.voltcash.vterminal.util.TxData;
import com.voltcash.vterminal.util.ViewUtil;
import com.voltcash.vterminal.views.lab.util.DialogUtils;
import com.voltcash.vterminal.views.receipt.ReceiptBuilder;
import com.voltcash.vterminal.views.receipt.ReceiptView;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.card.CardInfoEntity;
import com.zcs.sdk.card.CardReaderManager;
import com.zcs.sdk.card.CardReaderTypeEnum;
import com.zcs.sdk.card.MagCard;
import com.zcs.sdk.listener.OnSearchCardListener;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.voltcash.vterminal.views.receipt.ReceiptBuilder.buildCardToBankReceipt;

public class TxActivity extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback,
        CompoundButton.OnCheckedChangeListener,
        View.OnClickListener  {

    private final PermissionsManager mPermissionsManager = new PermissionsManager(this);
    private ProgressDialog mProgressDialog;

//    private CardReaderManager mCardReadManager;
//    private MagCard mMagCard;

    private ImgReviewEditCntrl checkFrontImgReviewEditCntrl;
    private ImgReviewEditCntrl checkBackImgReviewEditCntrl;
    private ImgReviewEditCntrl idFrontImgReviewEditCntrl;
    private ImgReviewEditCntrl idBackImgReviewEditCntrl;

    private String activeImgField = null;
    private ImgReviewEditCntrl activeImgCmp = null;

    private String operation;
    String operationName;

    private EditText cashBackField = null;
    private Switch cashBackSwitch = null;
    private Button submitButton;

    private static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private List<Integer> showIfCardNotExist = Arrays.asList(
            R.id.tx_id_front_wrapper,
            R.id.tx_id_back_wrapper,
            R.id.tx_id_ssn_input,
            R.id.tx_id_phone_input
    );
    private List<Integer> showIfCheck = Arrays.asList(
            R.id.tx_check_front_wrapper,
            R.id.tx_check_back_wrapper
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tx);


        checkFrontImgReviewEditCntrl = (ImgReviewEditCntrl) findViewById(R.id.tx_check_front_image);
        checkBackImgReviewEditCntrl = (ImgReviewEditCntrl) findViewById(R.id.tx_check_back_image);
        idFrontImgReviewEditCntrl = (ImgReviewEditCntrl) findViewById(R.id.tx_id_front_image);
        idBackImgReviewEditCntrl = (ImgReviewEditCntrl) findViewById(R.id.tx_id_back_image);

        if (!mPermissionsManager.isGranted(PERMISSIONS)) {
            mPermissionsManager.request(PERMISSIONS);
        }

        AppContextProvider.setContext(getApplicationContext());
        Licensing.setMobileSDKLicense(License.PROCESS_PAGE_SDK_LICENSE);

        TxData.clear();

        this.operation = (String) getIntent().getExtras().get(Field.TX.OPERATION);
        this.operationName = Constants.OPERATION.isCheck(this.operation) ? "Check" : "Cash";
        TxData.put(Field.TX.OPERATION, this.operation);

        setTitle("Deposit " + operationName);

        EditText cardField = ((EditText) findViewById(R.id.tx_card_field));
        cardField.setText("4111111111111112");
        cardField.setOnClickListener(this);
        ((EditText) findViewById(R.id.tx_amount_input)).setText("1000");

        cashBackField = (EditText) findViewById(R.id.cash_back_amount);
        submitButton = findViewById(R.id.tx_submit_button);

        if(submitButton != null){
            submitButton.setOnClickListener(this);
        }

        ((Button) findViewById(R.id.tx_calculate_fee_button)).setOnClickListener(this);

        cashBackSwitch = ((Switch) findViewById(R.id.cash_back_checkbox));
        cashBackSwitch.setOnCheckedChangeListener(this);

//        mCardReadManager = VTerminal.DRIVER_MANAGER.getCardReadManager();
//        mMagCard = mCardReadManager.getMAGCard();

        //Provissional
        ((Button) findViewById(R.id.tx_calculate_fee_button)).setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.tx_calculate_fee_button:
                onCalculateFees(view);
                break;

            case R.id.tx_submit_button:
                onSubmit(view);
                break;
        }
    }

    public void onCalculateFees(View view) {
        final TxActivity _this = this;
        Log.v("onCalculateFees", "start");
        final String cardNumber = ((EditText) findViewById(R.id.tx_card_field)).getText().toString();
        final String amount = ((EditText) findViewById(R.id.tx_amount_input)).getText().toString();

        TxData.put(Field.TX.CARD_NUMBER, cardNumber);
        TxData.put(Field.TX.AMOUNT, amount);

        ConstraintLayout mainLayout = mainLayout = (ConstraintLayout) findViewById(R.id.tx_container_layout);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);

        TxService.checkAuthLocationConfig(new ServiceCallback(this) {
            @Override
            public void onSuccess(Map response) {

                if (response == null) {
                    ViewUtil.showError(getCtx(), "Server Error", "Error trying to calculate fees. Please contact Customer Support");
                    return;
                }

                Boolean cardExist = (Boolean) response.get(Field.TX.CARD_EXIST);
                String cardLoadFee = response.get(Field.TX.CARD_LOAD_FEE) + "";
                String activationFee = response.get(Field.TX.ACTIVATION_FEE) + "";

                TxData.put(Field.TX.CARD_LOAD_FEE, cardLoadFee);
                TxData.put(Field.TX.ACTIVATION_FEE, activationFee);
                TxData.put(Field.TX.CARD_EXIST, cardExist);


                findViewById(R.id.tx_calculate_fees_layout).setVisibility(View.GONE);
                findViewById(R.id.tx_fees_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.tx_grid_layout).setVisibility(View.VISIBLE);

                if (!cardExist) {
                    for (int id : showIfCardNotExist) {
                        findViewById(id).setVisibility(View.VISIBLE);
                    }
                }

                if (Constants.OPERATION.isCheck(_this.operation)) {
                    for (int id : showIfCheck) {
                        findViewById(id).setVisibility(View.VISIBLE);
                    }
                }

                ((TextView) findViewById(R.id.tx_amount_text)).setText("Amount: $" + amount);
                ((TextView) findViewById(R.id.tx_fee_text)).setText("Transaction Fee: $" + cardLoadFee);
                ((TextView) findViewById(R.id.tx_activation_fee_text)).setText("Activation Fee: $" + activationFee);

                submitButton.setVisibility(View.VISIBLE);
                if (Constants.OPERATION.CHECK.equals(operation)) {
                    cashBackSwitch.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void onSubmit(View view) {

        final TxActivity _this = this;
        final String ssn = ((EditText) findViewById(R.id.tx_id_ssn_input)).getText().toString();
        final String phone = ((EditText) findViewById(R.id.tx_id_phone_input)).getText().toString();

        TxData.put(Field.TX.SSN, ssn);
        TxData.put(Field.TX.PHONE, phone);

        String cashBackString = cashBackField.getText().toString().trim();
        boolean hasCashBack = cashBackString != null && !cashBackString.isEmpty();

        if (hasCashBack && !cashBackString.matches("\\d+(?:\\.\\d+)?")) {
            ViewUtil.showError(this, "Error", "Invalid Cashback Amount");
            return;
        }

        final Double cashBack = hasCashBack ? Double.parseDouble(cashBackString) : null;


        TxService.tx(new ServiceCallback(this) {
            @Override
            public void onSuccess(Map response) {
                final Double amount = TxData.getDouble(Field.TX.AMOUNT);
                Double fee = TxData.getDouble(Field.TX.CARD_LOAD_FEE);
                Double payout = amount - fee;
                String card = TxData.getString(Field.TX.CARD_NUMBER);
                String merchant = PreferenceUtil.read(Field.AUTH.MERCHANT_NAME);
                String requestId = StringUtil.formatRequestId(response);

                if (card != null && card.length() > 4) {
                    card = card.substring(card.length() - 4, card.length());
                }

                final List<String> receiptLines = ReceiptBuilder.dateTimeLines();
                receiptLines.add(_this.operationName + " Loading Fee -> $ " + StringUtil.formatCurrency(fee));
                receiptLines.add("Amount Loaded -> $ " + StringUtil.formatCurrency(payout));
                receiptLines.add("Location Name -> " + merchant);
                receiptLines.add("Transaction # -> " + requestId);
                receiptLines.add("Card Number -> **** " + card);

                final String receiptContent = ReceiptBuilder.build(_this.operationName + " Load", receiptLines);

                if (cashBack == null) {
                    showReceipt(receiptContent);
                } else {
                    TxData.put(Field.TX.AMOUNT, cashBack + "");

                    TxService.cardToBank(null, new ServiceCallback(_this) {
                        @Override
                        public void onSuccess(Map response) {
                            String c2bReceiptContent = buildCardToBankReceipt(response, amount, null, null);

                            String doubleReceipt = ReceiptBuilder.div(receiptContent + "<br/>" + c2bReceiptContent);
                            showReceipt(doubleReceipt);
                        }

                        @Override
                        public void onError(Map map) {
                            new AlertDialog.Builder(_this)
                                    .setTitle("Error Processing Cash Back")
                                    .setMessage((String) map.get("errorMessage") + ". \n\nCheck transaction was processed successfully.")
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();

                                            showReceipt(receiptContent);
                                        }
                                    })
                                    .setCancelable(true)
                                    .setIcon(R.drawable.error)
                                    .show();
                        }
                    });
                }
            }
        });
    }

    private void showReceipt(String receiptContent) {
        TxData.clear();
        Intent intent = new Intent(this, ReceiptView.class);
        intent.putExtra(Constants.RECEIPT, receiptContent);
        startActivity(intent);
    }

    public void onClickCheckFront(View view) {
        Log.i("TxActivity", "onClickCheckFront - 1");
        onCaptureClick(Field.TX.CHECK_FRONT, checkFrontImgReviewEditCntrl, CaptureActivity.class);
    }

    public void onClickCheckBack(View view) {
        onCaptureClick(Field.TX.CHECK_BACK, checkBackImgReviewEditCntrl, CaptureActivity.class);
    }

    public void onClickIdFront(View view) {
        onCaptureClick(Field.TX.ID_FRONT, idFrontImgReviewEditCntrl, CaptureActivity.class);
    }

    public void onClickIdBack(View view) {
        onCaptureClick(Field.TX.ID_BACK, idBackImgReviewEditCntrl, CaptureBarcodeActivity.class);
    }

    protected void onCaptureClick(String field, ImgReviewEditCntrl imgCmp, Class captureClazz) {
        activeImgField = field;
        activeImgCmp = imgCmp;

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Please wait");
        mProgressDialog.setMessage("Initializing...");
        mProgressDialog.show();

        Intent intent = null;

        if (!Field.TX.ID_BACK.equals(activeImgField) && TxData.contains(activeImgField)) {
            intent = new Intent(this, PreviewActivity.class);
        } else {
            intent = new Intent(this, captureClazz);
        }

        intent.putExtra(Field.TX.TX_FIELD, activeImgField);
        startActivityForResult(intent, Constants.TAKE_IMAGE_REQUEST_ID);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mProgressDialog != null && mProgressDialog.isShowing()) mProgressDialog.dismiss();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("TxActivity", "onActivityResult() requestCode = " + requestCode + " , resultCode = " + resultCode);

        try {

            switch (resultCode) {
                case Constants.PROCESSED_IMAGE_ACCEPT_RESPONSE_ID:

                    Image image = null;
                    BarCodeFoundEvent barCodeFoundEvent = null;

                    if (activeImgField == Field.TX.ID_BACK) {
                        barCodeFoundEvent = TxData.BARCODE_EVENT;
                        image = barCodeFoundEvent.getImage();
                    } else {
                        image = TxData.getImage(activeImgField);
                    }

                    activeImgCmp.setImage(image);


                    if (activeImgField == Field.TX.ID_BACK) {
                        TxData.put(Field.TX.ID_BACK, barCodeFoundEvent.getImage());
                        TxData.put(Field.TX.DL_DATA_SCAN, barCodeFoundEvent.getBarCode().getValue());
                    }
                    break;

                case Constants.PROCESSED_IMAGE_RETAKE_RESPONSE_ID:
                    Intent intent = new Intent(this, CaptureActivity.class);
                    intent.putExtra(Field.TX.TX_FIELD, activeImgField);
                    startActivityForResult(intent, Constants.TAKE_IMAGE_REQUEST_ID);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        cashBackField.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
        cashBackField.setText("");
    }

    //--------------- Card Reader -----------------


    void searchBankCard() {

      showSearchCardDialog("Waiting", "Please swipe card");

//        mCardReadManager.cancelSearchCard();
//        mCardReadManager.searchCard(CardReaderTypeEnum.MAG_CARD, 60 * 1000, mListener);
    }

    OnSearchCardListener mListener = new OnSearchCardListener() {
        @Override
        public void onCardInfo(CardInfoEntity cardInfoEntity) {
            mProgressDialog.dismiss();


            ViewUtil.showMsg(getApplicationContext(), "onCardInfo", "onCardInfo");
            CardReaderTypeEnum cardType = cardInfoEntity.getCardExistslot();
            switch (cardType) {

                case MAG_CARD:
                    //readMagCard();
                    break;

            }
        }

        @Override
        public void onError(int i) {
            mProgressDialog.dismiss();
            ViewUtil.showMsg(getApplicationContext(), "onError", "onError");
           String a = "Error";
        }

        @Override
        public void onNoCard(CardReaderTypeEnum cardReaderTypeEnum, boolean b) {
            mProgressDialog.dismiss();
            ViewUtil.showMsg(getApplicationContext(), "onNoCard", "onNoCard");
            String a = "onNoCard";
        }
    };



    private void showSearchCardDialog(@StringRes int title, @StringRes int msg) {
        showSearchCardDialog( getString(title), getString(msg));
    }

    private void showSearchCardDialog(String title, String msg) {
        mProgressDialog = (ProgressDialog) DialogUtils.showProgress(this, title, msg, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
//                mCardReadManager.cancelSearchCard();
            }
        });
    }


//    private void readMagCard() {
//        int a = 3;
//        a = 4;
//        // use `getMagReadData` to get mag track data and parse data. if it is bank card, then parse exp and card no
//        // use `getMagTrackData` to get origin track data
//        CardInfoEntity cardInfo = mMagCard.getMagReadData();
//
////        Log.d(TAG, "cardInfo.getResultcode():" + cardInfo.getResultcode());
//        if (cardInfo.getResultcode() == SdkResult.SDK_OK) {
////            //String exp = cardInfo.getExpiredDate();
//            String cardNo = cardInfo.getCardNo();
////            //String tk1 = cardInfo.getTk1();
//            String tk2 = cardInfo.getTk2();
////            //String tk3 = cardInfo.getTk3();
////            Message msg = Message.obtain();
////            msg.what = MSG_CARD_OK;
////            msg.arg1 = cardInfo.getResultcode();
////            msg.obj = cardInfo;
////            mHandler.sendMessage(msg);
//        } else {
//            String k = "no card";
//        }
//        mMagCard.magCardClose();
//
//        mProgressDialog.dismiss();
////        // search again
//        //      mCardReadManager.searchCard(mCardType, READ_TIMEOUT, mListener);
//    }


    void closeSearch() {

        //mCardReadManager.cancelSearchCard();
    }

    @Override
    public void onDestroy() {
        closeSearch();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
     //   mCardReadManager.closeCard();
        super.onDestroy();
    }
}