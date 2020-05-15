package com.voltcash.vterminal.views.tx;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import com.kofax.kmc.kut.utilities.AppContextProvider;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.services.TxService;
import com.voltcash.vterminal.util.Constants;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.PreferenceUtil;
import com.voltcash.vterminal.util.StringUtil;
import com.voltcash.vterminal.util.TxData;
import com.voltcash.vterminal.util.ViewUtil;
import com.voltcash.vterminal.views.receipt.ReceiptBuilder;
import com.voltcash.vterminal.views.receipt.ReceiptView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.voltcash.vterminal.util.Constants.OPERATION.CARD2BANK_WITH_FEE;
import static com.voltcash.vterminal.views.receipt.ReceiptBuilder.buildCardToBankReceipt;

public class TxCardToBankActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private GridLayout calculateFeesLayout;
    private GridLayout feesLayout;
    private GridLayout contentLayout;


    private EditText amountField;
    private TextView cardField;
    private Button submitButton;

    private TextView feeText;
    private TextView payoutText;
    private TextView amountText;

    private Double fee;
    private Double payout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tx_cardtobank);

        AppContextProvider.setContext(getApplicationContext());
        TxData.clear();

        calculateFeesLayout = (GridLayout)findViewById(R.id.c2b_calculate_fees_layout);
        feesLayout          = (GridLayout)findViewById(R.id.c2b_fees_layout);
        contentLayout       = (GridLayout)findViewById(R.id.c2b_content);

        amountField= (EditText)findViewById(R.id.tx_amount_input);
        cardField  = (TextView)findViewById(R.id.c2b_card);
        submitButton = (Button)findViewById(R.id.tx_submit_button);

        feeText =  (TextView)findViewById(R.id.tx_fee_text);
        payoutText =  (TextView)findViewById(R.id.tx_payout_text);
        amountText =  (TextView)findViewById(R.id.tx_amount_text);

        setTitle("Card to Bank");
        amountField.setText("12.88");
    }

    public void onCalculateFee(View view){
        Double amountDouble = null;
        try{
               amountDouble = Double.parseDouble(amountField.getText() + "");
        }catch(Exception e){
            ViewUtil.showError(this, "Invalid Input", "Amount has to be a numeric value");
            return;
        }
        final Double amount = amountDouble;

        TxService.calculateFee(CARD2BANK_WITH_FEE, amount + "", new ServiceCallback(this) {
            @Override
            public void onSuccess(Map response) {
                calculateFeesLayout.setVisibility(View.GONE);
                feesLayout.setVisibility(View.VISIBLE);
                contentLayout.setVisibility(View.VISIBLE);
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


    public void onCardToBank(View view){
        final TxCardToBankActivity _this = this;

        final String cardNumber = cardField.getText().toString();
        final String amount = amountField.getText().toString().trim();

        if(!amount.matches("\\d+(?:\\.\\d+)?")){
            ViewUtil.showError(this, "Error", "Invalid Cashback Amount");
            return;
        }
        final Double amt = Double.parseDouble(amount);

        TxData.put(Field.TX.CARD_NUMBER, cardNumber);
        TxData.put(Field.TX.AMOUNT, amount);

        TxService.cardToBank(Constants.OPERATION.CARD2BANK_WITH_FEE, new ServiceCallback(this) {
            @Override
            public void onSuccess(Map response) {

                if(response == null){
                    ViewUtil.showError(getCtx(), "Server Error", "Error. Please contact Customer Support");
                    return;
                }

                String receiptContent = buildCardToBankReceipt(response, amt, fee, payout);

                TxData.clear();
                Intent intent = new Intent(_this, ReceiptView.class);
                intent.putExtra(Constants.RECEIPT, receiptContent);

                startActivity(intent);
            }
        });
    }

}
