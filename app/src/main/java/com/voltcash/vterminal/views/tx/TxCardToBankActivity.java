package com.voltcash.vterminal.views.tx;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
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

                String customerName = (String)response.get(Field.TX.CUSTUMER_NAME);

                List<String> dateTimeLines = ReceiptBuilder.dateTimeLines();

                List<String> receiptLines = new ArrayList();
                receiptLines.add("Merchant Name -> "+ response.get(Field.TX.MERCHANT_NAME));
                receiptLines.add("Funds Settlement Information");
                receiptLines.add("Bank Name -> "+ response.get(Field.TX.BANK_NAME));
                receiptLines.add("Customer Name -> "+ customerName);
                receiptLines.add("Address -> "+ response.get(Field.TX.CUSTUMER_ADDRESS));
                receiptLines.add("Routing # -> "+ response.get(Field.TX.ROUTING_BANK_NUMBER));
                receiptLines.add("Account # -> "+ response.get(Field.TX.ACCOUNT_NUMBER));

                receiptLines.add(ReceiptBuilder.achDisclaimer(customerName));
                receiptLines.add("<br/>");
                receiptLines.add("______________________________");
                receiptLines.add("Customer Signature");
                receiptLines.add(dateTimeLines.get(0).replace(" ->", ":"));

                String achReceiptContent = ReceiptBuilder.build("ACH Authorization Form", receiptLines);

                String card     = TxData.getString(Field.TX.CARD_NUMBER);
                String merchant = PreferenceUtil.read(Field.AUTH.MERCHANT_NAME);
                String requestId= StringUtil.formatRequestId(response);;

                if(card != null && card.length() > 4){
                    card = card.substring(card.length() - 4, card.length());
                }


                receiptLines = dateTimeLines;
                receiptLines.add("Location Name -> "    + merchant);
                receiptLines.add("Card Number -> **** " + card);
                receiptLines.add("Amount to Transfer -> " + amount);
                receiptLines.add("Account to Transfer -> " + response.get(Field.TX.ACCOUNT_NUMBER));
                receiptLines.add("Transaction # -> " + requestId);

                String txReceiptContent = ReceiptBuilder.build("ACH Transfer", receiptLines);

                String receiptContent = ReceiptBuilder.div(achReceiptContent + "<br/>" + txReceiptContent);

                TxData.clear();
                Intent intent = new Intent(_this, ReceiptView.class);
                intent.putExtra(Constants.RECEIPT, receiptContent);

                startActivity(intent);
            }
        });
    }

}
