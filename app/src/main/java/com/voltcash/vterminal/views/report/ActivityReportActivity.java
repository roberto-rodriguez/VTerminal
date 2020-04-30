package com.voltcash.vterminal.views.report;

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

public class ActivityReportActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        AppContextProvider.setContext(getApplicationContext());
        TxData.clear();

        setTitle("Activity Report");

        ((EditText)findViewById(R.id.activity_report_start)).setText("03-01-2020");
        ((EditText)findViewById(R.id.activity_report_end)).setText("04-31-2020");
    }


    public void onGetActivityReport(View view){
        final ActivityReportActivity _this = this;

        final String startDate = ((EditText)findViewById(R.id.activity_report_start)).getText().toString();
        final String endDate = ((EditText)findViewById(R.id.activity_report_end)).getText().toString();



        TxService.activityReport(startDate, endDate, new ServiceCallback(this) {
            @Override
            public void onSuccess(Map response) {

                if(response == null){
                    ViewUtil.showError(getCtx(), "Server Error", "Error. Please contact Customer Support");
                    return;
                }

                Integer TOTAL_ROWS = (Integer)response.get("TOTAL_ROWS");

                Integer CHECK2CARD_COUNT = (Integer)response.get("CHECK2CARD_COUNT");
                Integer CARD2MERCHANT_COUNT = (Integer)response.get("CARD2MERCHANT_COUNT");
                Integer CASH2CARD_COUNT = (Integer)response.get("CASH2CARD_COUNT");

                String CASH2CARD_TOTAL = response.get("CASH2CARD_TOTAL") + "";
                String CARD2MERCHANT_TOTAL = (Double)response.get("CARD2MERCHANT_TOTAL")+ "";
                String CHECK2CARD_TOTAL = response.get("CHECK2CARD_TOTAL") + "";

                List<Map> CHECK2CARD_TRANSACTIONS = (List<Map>)response.get("CHECK2CARD_TRANSACTIONS");
                List<Map> CASH2CARD_TRANSACTIONS = (List<Map>)response.get("CASH2CARD_TRANSACTIONS");
                List<Map> CARD2MERCHANT_TRANSACTIONS = (List<Map>)response.get("CARD2MERCHANT_TRANSACTIONS");

                String CASH_IN = response.get("CASH_IN") + "";
                String CASH_OUT = response.get("CASH_OUT") + "";
                String NET_CASH = response.get("NET_CASH") + "";

                List<String> receiptLines = ReceiptBuilder.dateTimeLines();

                receiptLines.add("Date Range -> "+ startDate + " TO: " + endDate);
                receiptLines.add("<br/>");

                addReceiptSection("Check to Card", "CHECK2CARD", response, receiptLines);
                addReceiptSection("Cash to Card", "CASH2CARD", response, receiptLines);
                addReceiptSection("Card to Merchant", "CARD2MERCHANT", response, receiptLines);

                receiptLines.add("Total Cash In -> " + response.get("CASH_IN"));
                receiptLines.add("Total Cash Out -> "+ response.get("CASH_OUT"));
                receiptLines.add("Net Cash Flow -> "+ response.get("NET_CASH"));

                String receiptContent = ReceiptBuilder.build("Activity Report", receiptLines);

                Intent intent = new Intent(_this, ReceiptView.class);
                intent.putExtra(Constants.RECEIPT, receiptContent);

                startActivity(intent);
            }
        });
    }

    private void addReceiptSection(String title, String section, Map response, List<String> receiptLines){
        Integer COUNT = (Integer)response.get(section + "_COUNT");
        String TOTAL = response.get(section + "_TOTAL") + "";
        List<Map> TRANSACTIONS = (List<Map>)response.get(section + "_TRANSACTIONS");

        receiptLines.add("<br/>");
        receiptLines.add(title);
        receiptLines.add("<br/>");
        receiptLines.add("Date/Time -> Trans $");

        if(TRANSACTIONS != null){
            for (Map tx : TRANSACTIONS){
                String amount = (String)tx.get("amount");
                String dateTime = (String)tx.get("dateTime");

                if(dateTime != null){
                    dateTime = dateTime.replace("T", " ");
                }
                receiptLines.add(dateTime + " -> " + amount);
            }
        }
        receiptLines.add("<br/>");
        receiptLines.add( "Total Trans -> " + COUNT );
        receiptLines.add( "Total Trans $ -> " + TOTAL);
    }

}
