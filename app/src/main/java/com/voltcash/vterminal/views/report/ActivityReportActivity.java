package com.voltcash.vterminal.views.report;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import com.kofax.kmc.kut.utilities.AppContextProvider;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.services.TxService;
import com.voltcash.vterminal.util.Constants;
import com.voltcash.vterminal.util.StringUtil;
import com.voltcash.vterminal.util.TxData;
import com.voltcash.vterminal.util.ViewUtil;
import com.voltcash.vterminal.views.receipt.ReceiptBuilder;
import com.voltcash.vterminal.views.receipt.ReceiptView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ActivityReportActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener {
    private DatePickerDialog picker;
    private EditText startDateField = null;
    private EditText endDateField = null;

    private String startDate = "";
    private String endDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        AppContextProvider.setContext(getApplicationContext());
        TxData.clear();

        setTitle("Activity Report");

        startDateField = ((EditText)findViewById(R.id.activity_report_start));
        endDateField = ((EditText)findViewById(R.id.activity_report_end));

        startDateField.setInputType(InputType.TYPE_NULL);
        endDateField.setInputType(InputType.TYPE_NULL);

        startDateField.setOnClickListener(this);
        endDateField.setOnClickListener(this);

        String date = getDate("MM-dd-yyyy");
        String dateDisplay = getDate("MM/dd/yyyy");

        startDateField.setText(dateDisplay);
        endDateField.setText(dateDisplay);

        startDate = date;
        endDate = date;
    }


    public void onGetActivityReport(View view){
        final ActivityReportActivity _this = this;

        final String startDateDisplay = startDateField.getText().toString();
        final String endDateDisplay = endDateField.getText().toString();

        if(startDate.isEmpty() || endDate.isEmpty()){
            ViewUtil.showError(this, "Invalid Input", "Need to enter Date Range");
        }

        TxService.activityReport(startDate, endDate, new ServiceCallback(this) {
            @Override
            public void onSuccess(Map response) {

                if(response == null){
                    ViewUtil.showError(getCtx(), "Server Error", "Error. Please contact Customer Support");
                    return;
                }

                Double TOTAL_ROWS = (Double)response.get("TOTAL_ROWS");

                List<String> receiptLines = ReceiptBuilder.dateTimeLines();

                receiptLines.add("From: "+ startDateDisplay + " TO: " + endDateDisplay );

                if(TOTAL_ROWS == 0.0){
                    receiptLines.add("<br/>");
                    receiptLines.add("No activity for the required period");

                }else{
                    addReceiptSection("Check to Card", "CHECK2CARD", response, receiptLines) ;
                    addReceiptSection("Cash to Card", "CASH2CARD", response, receiptLines);
                    addReceiptSection("Card to Merchant", "CARD2MERCHANT", response, receiptLines);

                    receiptLines.add("<br/>");

                    receiptLines.add("Total Cash In -> " + getFormattedAmount(response, "CASH_IN"));
                    receiptLines.add("Total Cash Out -> "+ getFormattedAmount(response, "CASH_OUT"));
                    receiptLines.add("Net Cash Flow -> " + getFormattedAmount(response, "NET_CASH"));
                }

                String receiptContent = ReceiptBuilder.build("Activity Report", receiptLines);

                Intent intent = new Intent(_this, ReceiptView.class);
                intent.putExtra(Constants.RECEIPT, receiptContent);

                startActivity(intent);
            }
        });
    }

    public void onClick(final View v) {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        picker = new DatePickerDialog(ActivityReportActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = (monthOfYear + 1) + "-" +  dayOfMonth + "-" +  year;
                        String displaySate = (monthOfYear + 1) + "/" + dayOfMonth + "/" + year;

                        switch (v.getId()){
                            case R.id.activity_report_start:
                                startDateField.setText(displaySate);
                                startDate = date;
                                break;
                            case R.id.activity_report_end:
                                endDateField.setText(displaySate);
                                endDate = date;
                                break;
                        }
                    }
                }, year, month, day);
        picker.show();
    }

    private String getFormattedAmount(Map response, String name){
        Double d = (Double)response.get(name);
        return StringUtil.formatCurrency(d);
    }

    private Integer doubleToInt(Map response, String name){
        Double d = (Double)response.get(name);
        return d.intValue();
    }

    private String getDate(String format){
            DateFormat df = new SimpleDateFormat(format);
            return df.format(new Date());
    }

    private boolean addReceiptSection(String title, String section, Map response, List<String> receiptLines){
        String COUNT = doubleToInt(response, section + "_COUNT") + "";
        String TOTAL = response.get(section + "_TOTAL") + "";
        List<Map> TRANSACTIONS = (List<Map>)response.get(section + "_TRANSACTIONS");

        receiptLines.add("<br/>");
        receiptLines.add(title);
        receiptLines.add("<br/>");
        receiptLines.add("<b>Date/Time</b> -> <b>Trans $</b>");

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

        return !"0.0".equals(COUNT);
    }

}
