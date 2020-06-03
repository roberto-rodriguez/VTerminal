package com.voltcash.vterminal.views.tx;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.TextView;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.services.TxService;
import com.voltcash.vterminal.util.Constants;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.PreferenceUtil;
import com.voltcash.vterminal.util.TxData;
import com.voltcash.vterminal.util.ViewUtil;
import com.voltcash.vterminal.util.cardReader.FragmentWithCardReader;
import com.voltcash.vterminal.views.receipt.ReceiptBuilder;
import com.voltcash.vterminal.views.receipt.ReceiptView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TxBalanceFragment extends FragmentWithCardReader
        implements
        ActivityCompat.OnRequestPermissionsResultCallback{


    @Override
    public void onViewCreated(View view,  Bundle savedInstanceState) {
        setTitle("Balance Inquiry");
        super.onViewCreated(view, savedInstanceState);
    }

    protected int getLayoutId(){
        return R.layout.activity_tx_balance;
    }

    public void onCalculateFees(View view){
        final TxBalanceFragment _this = this;

        final String cardNumber = getCardNumber();

        if(cardNumber == null || cardNumber.isEmpty()){
            ViewUtil.showError(getActivity(), "Invalid Input", "Please enter card number");
            return;
        }
        TxData.put(Field.TX.CARD_NUMBER, cardNumber);

        TxService.balanceInquiry(new ServiceCallback(this.getActivity()) {
            @Override
            public void onSuccess(Map response) {

                if(response == null){
                    ViewUtil.showError(getCtx(), "Server Error", "Error trying to check balance. Please contact Customer Support");
                    return;
                }

                String balance   = response.get(Field.TX.BALANCE) + "";

                ((TextView)findViewById(R.id.tx_balance_result        )).setText("Balance: $" + balance);

                String merchant = PreferenceUtil.read(Field.AUTH.MERCHANT_NAME);

                List<String> receiptLines = new ArrayList<>();
                ReceiptBuilder.addTitle(receiptLines,"Balance Inquiry");
                ReceiptBuilder.addDateTimeLines(receiptLines);
                receiptLines.add("Location Name -> "    + merchant);
                receiptLines.add("Card Number -> **** " + getCardField().getText());
                receiptLines.add("Balance -> $" + balance);

                ReceiptView.show(_this.getActivity(), receiptLines);
            }
        });
    }
}
