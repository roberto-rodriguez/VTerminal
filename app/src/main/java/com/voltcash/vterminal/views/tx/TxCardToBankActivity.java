package com.voltcash.vterminal.views.tx;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kofax.kmc.kut.utilities.AppContextProvider;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.services.TxService;
import com.voltcash.vterminal.util.Constants;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.PreferenceUtil;
import com.voltcash.vterminal.util.ReceiptActivity;
import com.voltcash.vterminal.util.ReceiptCardToBankActivity;
import com.voltcash.vterminal.util.StringUtil;
import com.voltcash.vterminal.util.TxData;
import com.voltcash.vterminal.util.ViewUtil;

import java.util.Map;

public class TxCardToBankActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tx_cardtobank);

        AppContextProvider.setContext(getApplicationContext());
        TxData.clear();

        setTitle("ACH Transfer");

        ((EditText)findViewById(R.id.tx_card_field)).setText("4111111111111112");
        ((EditText)findViewById(R.id.tx_amount_input)).setText("12.88");
    }


    public void onCardToBank(View view){
        final TxCardToBankActivity _this = this;

        final String cardNumber = ((EditText)findViewById(R.id.tx_card_field)).getText().toString();
        final String amount = ((EditText)findViewById(R.id.tx_amount_input)).getText().toString();

        TxData.put(Field.TX.CARD_NUMBER, cardNumber);
        TxData.put(Field.TX.AMOUNT, amount);

        TxService.cardToBank(new ServiceCallback(this) {
            @Override
            public void onSuccess(Map response) {

                if(response == null){
                    ViewUtil.showError(getCtx(), "Server Error", "Error. Please contact Customer Support");
                    return;
                }

                StringBuilder receiptAchLines = new StringBuilder();
                receiptAchLines.append("  Merhant Name -> "+ response.get(Field.TX.MERCHANT_NAME));
                receiptAchLines.append("@@Bank Name -> "+ response.get(Field.TX.BANK_NAME));
                receiptAchLines.append("@@Customer Name -> "+ response.get(Field.TX.CUSTUMER_NAME));
                receiptAchLines.append("@@Address -> "+ response.get(Field.TX.CUSTUMER_ADDRESS));
                receiptAchLines.append("@@Routing # -> "+ response.get(Field.TX.ROUTING_BANK_NUMBER));
                receiptAchLines.append("@@Account # -> "+ response.get(Field.TX.ACCOUNT_NUMBER));

                String card     = TxData.getString(Field.TX.CARD_NUMBER);
                String merchant = PreferenceUtil.read(Field.AUTH.MERCHANT_NAME);
                String requestId= response.get("REQUEST_ID") + "";

                if(card != null && card.length() > 4){
                    card = card.substring(card.length() - 4, card.length());
                } 

                StringBuilder receiptLines = new StringBuilder();
                receiptLines.append(" Location Name -> "    + merchant);
                receiptLines.append("@@Card Number -> **** **** **** " + card);
                receiptLines.append("@@Amount to Transfer -> " + amount);
                receiptLines.append("@@Account to Transfer -> " + response.get(Field.TX.ACCOUNT_NUMBER));
                receiptLines.append("@@Transaction # -> " + requestId);


                TxData.clear();

                Intent intent = new Intent(_this, ReceiptActivity.class);
                intent.putExtra(Constants.RECEIPT_TITLE, "ACH Transfer");
                intent.putExtra(Constants.RECEIPT_LINES, receiptLines.toString());
          //      intent.putExtra(Constants.RECEIPT_ACH_LINES, receiptAchLines.toString());

                startActivity(intent);
            }
        });
    }

}
