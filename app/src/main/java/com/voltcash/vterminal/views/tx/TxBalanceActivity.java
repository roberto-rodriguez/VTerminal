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
import com.voltcash.vterminal.util.StringUtil;
import com.voltcash.vterminal.util.TxData;
import com.voltcash.vterminal.util.ViewUtil;
import java.util.Map;

public class TxBalanceActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tx_balance);

        AppContextProvider.setContext(getApplicationContext());
        TxData.clear();

        setTitle("Balance Inquiry");

        ((EditText)findViewById(R.id.tx_card_field)).setText("4111111111111112");
    }


    public void onCheckBalance(View view){
        final TxBalanceActivity _this = this;

        final String cardNumber = ((EditText)findViewById(R.id.tx_card_field)).getText().toString();
        TxData.put(Field.TX.CARD_NUMBER, cardNumber);

        TxService.balanceInquiry(new ServiceCallback(this) {
            @Override
            public void onSuccess(Map response) {

                if(response == null){
                    ViewUtil.showError(getCtx(), "Server Error", "Error trying to check balance. Please contact Customer Support");
                    return;
                }

                String balance   = response.get(Field.TX.BALANCE) + "";

                ((TextView)findViewById(R.id.tx_balance_result        )).setText("Balance: $" + balance);

                String card     = TxData.getString(Field.TX.CARD_NUMBER);
                String merchant = PreferenceUtil.read(Field.AUTH.MERCHANT_NAME);
                String requestId= response.get("REQUEST_ID") + "";

                if(card != null && card.length() > 4){
                    card = card.substring(card.length() - 4, card.length());
                }

                StringBuilder receiptLines = new StringBuilder();
                receiptLines.append("Location Name -> "    + merchant);
                receiptLines.append("@@Transaction # -> " + requestId);
                receiptLines.append("@@Card Number -> **** **** **** " + card);
                receiptLines.append("@@Balance -> " + balance);

                TxData.clear();

                Intent intent = new Intent(_this, ReceiptActivity.class);
                intent.putExtra(Constants.RECEIPT_LINES, receiptLines.toString());
                intent.putExtra(Constants.RECEIPT_TITLE, "Balance Inquiry");
                startActivity(intent);
            }
        });
    }

}
