package com.voltcash.vterminal.views.tx;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.services.TxService;
import com.voltcash.vterminal.util.AudioUtil;
import com.voltcash.vterminal.util.Constants;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.GlobalExceptionHandler;
import com.voltcash.vterminal.util.ReceiptBuilder;
import com.voltcash.vterminal.util.StringUtil;
import com.voltcash.vterminal.util.TxData;
import com.voltcash.vterminal.util.ViewUtil;
import com.voltcash.vterminal.util.cardReader.FragmentWithCardReader;
import com.voltcash.vterminal.views.tx.receipt.ReceiptView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

import static com.voltcash.vterminal.util.Constants.OPERATION.CARD2BANK_WITH_FEE;

public class TxCardToBankFragment extends FragmentWithCardReader
        implements
        ActivityCompat.OnRequestPermissionsResultCallback {

    private GridLayout feesLayout;



    private TextView feeText;
    private TextView payoutText;
    private TextView amountText;

    private Double fee;
    private Double payout;

    @Override
    public void onViewCreated(View view,  Bundle savedInstanceState) {
        try{
            setTitle("Cash Back");

            feesLayout  = (GridLayout)findViewById(R.id.c2b_fees_layout);

            feeText =  (TextView)findViewById(R.id.tx_fee_text);
            payoutText =  (TextView)findViewById(R.id.tx_payout_text);
            amountText =  (TextView)findViewById(R.id.tx_amount_text);

            super.onViewCreated(view, savedInstanceState);

        }catch(Exception e){
            GlobalExceptionHandler.catchException(this.getActivity(), "TxCardToBankFragment.onViewCreated()", e);
        }
    }

    protected int getLayoutId(){
        return R.layout.activity_tx_cardtobank;
    }

    public void onCalculateFees(View view){
        final Double amount = getAmount();

        if(amount == 0D) return;

        ConstraintLayout mainLayout = (ConstraintLayout) findViewById(R.id.tx_container_layout);
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);

        TxService.calculateFee(CARD2BANK_WITH_FEE, amount + "", new ServiceCallback(this.getActivity()) {
            @Override
            public void onSuccess(Map response) {
                calculateFeesLayout.setVisibility(View.GONE);
                feesLayout.setVisibility(View.VISIBLE);
                cardField.setVisibility(View.VISIBLE);
                submitButton.setVisibility(View.VISIBLE);

                  fee = (Double)response.get(Field.TX.C2B_FEE);
                  payout = amount - fee;

                amountText.setText("Amount: " + StringUtil.formatCurrency(amount));
                feeText.setText("Fee: " +  StringUtil.formatCurrency(fee));
                payoutText.setText("Payout: " +  StringUtil.formatCurrency(payout));
            }
        });

    }


    public void onSubmit(View view){
        final TxCardToBankFragment _this = this;

        final String cardNumber = getCardNumber();

        if(cardNumber == null){
            return;
        }

        final Double amount = getAmount();

        if(amount == 0D) return;

        TxData.put(Field.TX.CARD_NUMBER, cardNumber);
        TxData.put(Field.TX.AMOUNT, amount);

        TxService.cardToBank(Constants.OPERATION.CARD2BANK_WITH_FEE, new ServiceCallback(this.getActivity()) {
            @Override
            public void onSuccess(Map response) {
                AudioUtil.playBellSound(_this.getActivity());

                if(response == null){
                    ViewUtil.showError(getCtx(), "Server Error", "Error. Please contact Customer Support");
                    return;
                }

                List<String> receiptLines = ReceiptBuilder.buildCardToBankReceiptLines(response, amount, fee, payout, true);

                Constants.receiptLines = receiptLines;

                ReceiptView.show(_this.getActivity(), receiptLines);
            }

            @Override
            public void onError(Map response) {
                dismissDialog();

                List<String> receiptLines = ReceiptBuilder.buildCardToBankReceiptLines(new HashMap(), amount, fee, payout, false);
                showError("Unexpected Error", response.get("errorMessage") + "",  receiptLines);
            }

            @Override
            public void onFailure(Call<Map> call, Throwable t) {
                dismissDialog();

                List<String> receiptLines = ReceiptBuilder.buildCardToBankReceiptLines(new HashMap(), amount, fee, payout, false);
                showError("Unexpected Error", t.getMessage(),  receiptLines);
            }
        });
    }

    private void showError(final String title, final String message, final List<String> receiptLines){
        AudioUtil.playBellSound(this.getActivity());

        final Activity _this = this.getActivity();

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

}
