package com.voltcash.vterminal.views.tx;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.services.TxService;
import com.voltcash.vterminal.util.Constants;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.ReceiptBuilder;
import com.voltcash.vterminal.util.StringUtil;
import com.voltcash.vterminal.util.TxData;
import com.voltcash.vterminal.util.ViewUtil;
import com.voltcash.vterminal.util.cardReader.FragmentWithCardReader;
import com.voltcash.vterminal.views.tx.receipt.ReceiptView;
import java.util.List;
import java.util.Map;

import static com.voltcash.vterminal.util.Constants.OPERATION.CARD2BANK_WITH_FEE;

public class TxCardToBankFragment extends FragmentWithCardReader
        implements
        ActivityCompat.OnRequestPermissionsResultCallback {

    private GridLayout feesLayout;

    private EditText amountField;

    private TextView feeText;
    private TextView payoutText;
    private TextView amountText;

    private Double fee;
    private Double payout;

    @Override
    public void onViewCreated(View view,  Bundle savedInstanceState) {
        setTitle("Card to Bank");

        feesLayout  = (GridLayout)findViewById(R.id.c2b_fees_layout);
        amountField = (EditText)findViewById(R.id.tx_amount_input);

        feeText =  (TextView)findViewById(R.id.tx_fee_text);
        payoutText =  (TextView)findViewById(R.id.tx_payout_text);
        amountText =  (TextView)findViewById(R.id.tx_amount_text);

        super.onViewCreated(view, savedInstanceState);
    }

    protected int getLayoutId(){
        return R.layout.activity_tx_cardtobank;
    }

    public void onCalculateFees(View view){
        Double amountDouble = null;
        try{
               amountDouble = Double.parseDouble(amountField.getText() + "");
        }catch(Exception e){
            ViewUtil.showError(this.getActivity(), "Invalid Input", "Amount has to be a numeric value");
            return;
        }
        final Double amount = amountDouble;

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
        final String amount = amountField.getText().toString().trim();

        if(!amount.matches("\\d+(?:\\.\\d+)?")){
            ViewUtil.showError(this.getActivity(), "Error", "Invalid Cashback Amount");
            return;
        }
        final Double amt = Double.parseDouble(amount);

        TxData.put(Field.TX.CARD_NUMBER, cardNumber);
        TxData.put(Field.TX.AMOUNT, amount);

        TxService.cardToBank(Constants.OPERATION.CARD2BANK_WITH_FEE, new ServiceCallback(this.getActivity()) {
            @Override
            public void onSuccess(Map response) {

                if(response == null){
                    ViewUtil.showError(getCtx(), "Server Error", "Error. Please contact Customer Support");
                    return;
                }

                List<String> receiptLines = ReceiptBuilder.buildCardToBankReceiptLines(response, amt, fee, payout);

                ReceiptView.show(_this.getActivity(), receiptLines);
            }
        });
    }

}
