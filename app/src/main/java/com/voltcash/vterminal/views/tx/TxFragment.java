package com.voltcash.vterminal.views.tx;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.kofax.kmc.ken.engines.data.Image;
import com.kofax.kmc.kui.uicontrols.BarCodeFoundEvent;
import com.kofax.kmc.kui.uicontrols.ImgReviewEditCntrl;
import com.kofax.kmc.kut.utilities.Licensing;
import com.kofax.samples.common.License;
import com.kofax.samples.common.PermissionsManager;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.services.TxService;
import com.voltcash.vterminal.util.Constants;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.PreferenceUtil;
import com.voltcash.vterminal.util.ReceiptBuilder;
import com.voltcash.vterminal.util.StringUtil;
import com.voltcash.vterminal.util.TxData;
import com.voltcash.vterminal.util.ViewUtil;
import com.voltcash.vterminal.util.cardReader.FragmentWithCardReader;
import com.voltcash.vterminal.views.auth.AuthTerminalActivity;
import com.voltcash.vterminal.views.home.HomeActivity;
import com.voltcash.vterminal.views.tx.receipt.ReceiptView;
import com.voltcash.vterminal.views.tx.imageCapture.CaptureActivity;
import com.voltcash.vterminal.views.tx.imageCapture.CaptureBarcodeActivity;
import com.voltcash.vterminal.views.tx.imageCapture.PreviewActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class TxFragment extends FragmentWithCardReader implements
        ActivityCompat.OnRequestPermissionsResultCallback,
        CompoundButton.OnCheckedChangeListener{

    private PermissionsManager mPermissionsManager;

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

    private ConstraintLayout txProgressDialog = null;
    private Button submitBtn;

    private Boolean cardExist = true;

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

    protected int getLayoutId(){
        return R.layout.activity_tx;
    }

    @Override
    public void onViewCreated(View view,  Bundle savedInstanceState) {
        mPermissionsManager = new PermissionsManager(this.getActivity());

        checkFrontImgReviewEditCntrl = (ImgReviewEditCntrl) findViewById(R.id.tx_check_front_image);
        checkBackImgReviewEditCntrl = (ImgReviewEditCntrl) findViewById(R.id.tx_check_back_image);
        idFrontImgReviewEditCntrl = (ImgReviewEditCntrl) findViewById(R.id.tx_id_front_image);
        idBackImgReviewEditCntrl = (ImgReviewEditCntrl) findViewById(R.id.tx_id_back_image);

        txProgressDialog = (ConstraintLayout)findViewById(R.id.tx_progress_dialog);
        submitBtn = (Button)findViewById(R.id.tx_submit_button);

        if (!mPermissionsManager.isGranted(PERMISSIONS)) {
            mPermissionsManager.request(PERMISSIONS);
        }

        Licensing.setMobileSDKLicense(License.PROCESS_PAGE_SDK_LICENSE);

        this.operation =  getArguments().getString(Field.TX.OPERATION);
        this.operationName = Constants.OPERATION.isCheck(this.operation) ? "Check" : "Cash";

        getActivity().setTitle("Deposit " + operationName);

        cashBackField = (EditText) findViewById(R.id.cash_back_amount);

        cashBackSwitch = ((Switch) findViewById(R.id.cash_back_checkbox));
        cashBackSwitch.setOnCheckedChangeListener(this);

        super.onViewCreated(view, savedInstanceState);

        TxData.put(Field.TX.OPERATION, this.operation);

        addImageCaptureListeners();
    }

    private void addImageCaptureListeners(){
        addImageCaptureListener(R.id.tx_check_front_wrapper, Field.TX.CHECK_FRONT, checkFrontImgReviewEditCntrl, CaptureActivity.class);
        addImageCaptureListener(R.id.tx_check_back_wrapper , Field.TX.CHECK_BACK , checkBackImgReviewEditCntrl , CaptureActivity.class);
        addImageCaptureListener(R.id.tx_id_front_wrapper   , Field.TX.ID_FRONT   , idFrontImgReviewEditCntrl   , CaptureActivity.class);
        addImageCaptureListener(R.id.tx_id_back_wrapper    , Field.TX.ID_BACK    , idBackImgReviewEditCntrl    , CaptureBarcodeActivity.class);
    }

    private void addImageCaptureListener(int cmpId, final String fieldName, final ImgReviewEditCntrl imgReviewEditCntrl, final Class clz){
        ((LinearLayout)findViewById(cmpId)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCaptureClick(fieldName, imgReviewEditCntrl, clz);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        if (!mPermissionsManager.isGranted(PERMISSIONS)) {
            new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.permissions_rationale)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().finish();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }


    public void onCalculateFees(View view) {
        final TxFragment _this = this;
        final String cardNumber = getCardNumber();

        if(cardNumber == null){
            return;
        }

        final Double amount = getAmount();

        if(amount == 0D) return;

        TxData.put(Field.TX.CARD_NUMBER, cardNumber);
        TxData.put(Field.TX.AMOUNT, amount);

        ConstraintLayout mainLayout = (ConstraintLayout) findViewById(R.id.tx_container_layout);

        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);

        TxService.checkAuthLocationConfig(new ServiceCallback((AppCompatActivity)getActivity()) {
            @Override
            public void onSuccess(Map response) {

                if (response == null) {
                    ViewUtil.showError(getCtx(), "Server Error", "Error trying to calculate fees. Please contact Customer Support");
                    return;
                }

                 cardExist = (Boolean) response.get(Field.TX.CARD_EXIST);
                String cardLoadFee = response.get(Field.TX.CARD_LOAD_FEE) + "";
                String activationFee = response.get(Field.TX.ACTIVATION_FEE) + "";

                TxData.put(Field.TX.CARD_LOAD_FEE, cardLoadFee);
                TxData.put(Field.TX.ACTIVATION_FEE, activationFee);
                TxData.put(Field.TX.CARD_EXIST, cardExist);


                calculateFeesLayout.setVisibility(View.GONE);
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

                ((TextView) findViewById(R.id.tx_amount_text)).setText("Amount: $" + StringUtil.formatCurrency(amount));
                ((TextView) findViewById(R.id.tx_fee_text)).setText("Fee: $" + StringUtil.formatCurrency(cardLoadFee));
                ((TextView) findViewById(R.id.tx_activation_fee_text)).setText("Activation Fee: $" + StringUtil.formatCurrency(activationFee));

                submitButton.setVisibility(View.VISIBLE);
                if (Constants.OPERATION.CHECK.equals(operation)) {
                    cashBackSwitch.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void onSubmit(final View view) {
        final TxFragment _this = this;
        final String ssn = ((EditText) findViewById(R.id.tx_id_ssn_input)).getText().toString();
        final String phone = ((EditText) findViewById(R.id.tx_id_phone_input)).getText().toString();

        if(!cardExist && (ssn == null || ssn.isEmpty())){
            ViewUtil.showError(getActivity(), "Error", "Social Security Number is required");
            return;
        }

        if(!cardExist && (phone == null || phone.isEmpty())){
            ViewUtil.showError(getActivity(), "Error", "Phone Number is required");
            return;
        }

        TxData.put(Field.TX.SSN, ssn);
        TxData.put(Field.TX.PHONE, phone);

        String cashBackString = cashBackField.getText().toString().trim();
        boolean hasCashBack = cashBackString != null && !cashBackString.isEmpty();

        if (hasCashBack && !cashBackString.matches("\\d+(?:\\.\\d+)?")) {
            ViewUtil.showError(getActivity(), "Error", "Invalid Cashback Amount");
            return;
        }

        final Double cashBack = hasCashBack ? Double.parseDouble(cashBackString) : null;

        progressDialog(true);

        String merchant = PreferenceUtil.read(Field.AUTH.MERCHANT_NAME);

        final List<String> receiptLines = new ArrayList<>();
        ReceiptBuilder.addTitle(receiptLines, _this.operationName + " Load");
        ReceiptBuilder.addDateTimeLines(receiptLines);

        receiptLines.add("Location Name -> " + merchant);
        receiptLines.add("Card Number -> " + getCardField().getText());

        TxService.tx(new ServiceCallback(this.getActivity()) {
            @Override
            public void onSuccess(Map response) {
                final Double amount = TxData.getDouble(Field.TX.AMOUNT);
                Double fee = TxData.getDouble(Field.TX.CARD_LOAD_FEE);
                Double payout = amount - fee;
                receiptLines.add(_this.operationName + " Loading Fee -> $ " + StringUtil.formatCurrency(fee));
                receiptLines.add("Amount Loaded -> $ " + StringUtil.formatCurrency(payout));
                String requestId = StringUtil.formatRequestId(response);
                receiptLines.add("Transaction # -> " + requestId);

                if (cashBack == null) {
                    showReceipt(receiptLines);
                } else {
                    TxData.put(Field.TX.AMOUNT, cashBack + "");

                    TxService.cardToBank(null, new ServiceCallback(_this.getActivity()) {
                        @Override
                        public void onSuccess(Map response) {
                            List<String> c2bReceiptLines = ReceiptBuilder.buildCardToBankReceiptLines(response, cashBack, null, null, true);

                            receiptLines.addAll(c2bReceiptLines);

                            showReceipt(receiptLines);
                        }

                        @Override
                        public void onError(Map response) {
                            List<String> c2bReceiptLines = ReceiptBuilder.buildCardToBankReceiptLines(response, cashBack, null, null, true);
                            receiptLines.addAll(c2bReceiptLines);

                            showError("Error Processing Cash Back", response.get("errorMessage") + ". \n\nCheck transaction was processed successfully.", receiptLines);
                        }

                        @Override
                        public void onFailure(Call<Map> call, Throwable t) {
                            showError("Error Processing Cash Back", t.getMessage() + ". \n\nCheck transaction was processed successfully.", receiptLines);
                        }
                    });
                }
            }


            @Override
            public void onError(Map response) {
                showError("Unexpected Error", (String)response.get("errorMessage"),  receiptLines);
            }

            @Override
            public void onFailure(Call<Map> call, Throwable t) {
                showError("Unexpected Error", t.getMessage(),  receiptLines);
            }
        });
    }

    private void progressDialog(boolean show){
        ((HomeActivity)getActivity()).setEnableOnBack(!show);
        txProgressDialog.setVisibility(show ? View.VISIBLE : View.GONE);
        submitBtn.setVisibility(show ? View.GONE :  View.VISIBLE);
    }

    private void showError(final String title, final String message, final List<String> receiptLines){
        final Activity _this = this.getActivity();

        progressDialog(false);

        Constants.receiptLines = receiptLines;

        new AlertDialog.Builder(this.getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Print Receipt", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        receiptLines.add("Result Message -> " + message);

                        ReceiptView.show(_this , receiptLines);
                    }
                })
                .setCancelable(true)
                .setIcon(R.drawable.error)
                .show();
    }

    private void showReceipt(List<String> receiptLines) {
        progressDialog(false);

        Constants.receiptLines = receiptLines;

        ReceiptView.show(this.getActivity() , receiptLines);
    }

    protected void onCaptureClick(String field, ImgReviewEditCntrl imgCmp, Class captureClazz) {
        activeImgField = field;
        activeImgCmp = imgCmp;

        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Initializing...");
        progressDialog.show();

        Intent intent = null;

        if (!Field.TX.ID_BACK.equals(activeImgField) && TxData.contains(activeImgField)) {
            intent = new Intent(this.getActivity(), PreviewActivity.class);
            intent.putExtra("SHOW_PROCESSED_IMAGE", true);
        } else {
            intent = new Intent(this.getActivity(), captureClazz);
        }

        intent.putExtra(Field.TX.TX_FIELD, activeImgField);
        startActivityForResult(intent, Constants.TAKE_IMAGE_REQUEST_ID);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if(progressDialog != null){
                progressDialog.dismiss();
            }

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
                    Intent intent = new Intent(this.getActivity(), CaptureActivity.class);
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
 }
