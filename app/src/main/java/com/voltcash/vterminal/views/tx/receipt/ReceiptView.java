package com.voltcash.vterminal.views.tx.receipt;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.voltcash.vterminal.R;
import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.services.AuthService;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.ReceiptBuilder;
import com.voltcash.vterminal.util.TxData;
import com.voltcash.vterminal.util.ViewUtil;
import com.voltcash.vterminal.util.DialogUtils;
import com.voltcash.vterminal.views.home.HomeActivity;
import com.zcs.sdk.Printer;
import com.zcs.sdk.SdkResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static com.voltcash.vterminal.VTerminal.DRIVER_MANAGER;

/**
 * Created by roberto.rodriguez on 2/25/2020.
 */

public class ReceiptView extends AppCompatActivity implements View.OnClickListener  {
    private Printer PRINTER;
    private List<String> receiptLines;
    private Boolean addBarcode;

    public static void show(Activity originActivity, List<String> receiptLines){
        show(originActivity, receiptLines, true);
    }

    public static void show(Activity originActivity, List<String> receiptLines, boolean addBarcode){
        Intent intent = new Intent(originActivity, ReceiptView.class);

        intent.putExtra("receiptLines", (Serializable) receiptLines);
        intent.putExtra("addBarcode", addBarcode);
        originActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setTitle("Receipt");
        setContentView(R.layout.receipt_activity);

        try{
            PRINTER = DRIVER_MANAGER.getPrinter();

            receiptLines = (List<String>)getIntent().getSerializableExtra("receiptLines");
            addBarcode = getIntent().getBooleanExtra("addBarcode", true);

            String receipt =  ReceiptBuilder.build(receiptLines, null);

            Button m_Back = (Button)findViewById(R.id.payment_receipt_back);
            Button m_Print = (Button)findViewById(R.id.payment_receipt_print);
            m_Back.setOnClickListener(this);
            m_Print.setOnClickListener(this);

            final WebView v = (WebView)findViewById(R.id.payment_receipt);

            v.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);

                    v.postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            Bitmap bitmap = createBitmapFromView(v);
//                            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
//                            imageView.setImageDrawable(drawable);
//                            imageView.setVisibility(VISIBLE);
//                            removeAllViews();
//                            if (listener != null) listener.onComplete();
                        }
                    }, 500);
                }
            });

            v.loadDataWithBaseURL("", receipt, "text/html", "utf-8", "");
            v.setHorizontalScrollBarEnabled(false);
            v.setVerticalScrollBarEnabled(false);

            //opt_in_label
            TextView optInLabel = (TextView)findViewById(R.id.opt_in_label);

            Integer clientID = (Integer)TxData.getInteger(Field.TX.CLIENT_ID);
            Boolean excludeSMS = (Boolean)TxData.getBoolean(Field.TX.EXCLUDE_SMS);

             if(clientID != null && excludeSMS == true){
                 toggleOptInView(true);
             }

        }catch(Exception e){
            ViewUtil.showError(this, "Creating ReceiptView", e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.payment_receipt_back:
                finish();
                Intent intent = new Intent( ReceiptView.this, HomeActivity.class);
                startActivity(intent);
                 break;
            case R.id.payment_receipt_print:
                 print();
                 break;
        }
    }

    public void onSubscribeYes(View view){
        AuthService.subscribeSMS(new ServiceCallback(this) {
            @Override
            public void onSuccess(Map map) {
                Toast.makeText(getApplicationContext(), "User was subscribed to SMS Notifications", Toast.LENGTH_LONG);
                toggleOptInView(false);
            }

            @Override
            public void onError(Map map) {
                Toast.makeText(getApplicationContext(), "Error subscribing user", Toast.LENGTH_LONG);
                toggleOptInView(false);
            }
        });
    }

    public void onSubscribeNo(View view){
        toggleOptInView(false);
    }


    private void toggleOptInView(boolean show){
        LinearLayout optInView = (LinearLayout)findViewById(R.id.opt_in);
        optInView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void print() {
        ReceiptBuilder.build(receiptLines, PRINTER);

        if(addBarcode){
            Bitmap barcode = loadBarcode();

            if(barcode != null){
                PRINTER.setPrintAppendString("DOWNLOAD", ReceiptBuilder.CENTERED_LINE_FORMAT);
                PRINTER.setPrintAppendString("The Voltcash App", ReceiptBuilder.CENTERED_LINE_FORMAT);
                PRINTER.setPrintAppendString("", ReceiptBuilder.LINE_FORMAT);
                PRINTER.setPrintAppendBitmap(barcode, Layout.Alignment.ALIGN_CENTER);
                PRINTER.setPrintAppendString("", ReceiptBuilder.LINE_FORMAT);
                PRINTER.setPrintAppendString("SCAN ME", ReceiptBuilder.CENTERED_LINE_FORMAT);
                PRINTER.setPrintAppendString("", ReceiptBuilder.LINE_FORMAT);
                PRINTER.setPrintAppendString(" 1. Download the app", ReceiptBuilder.LINE_FORMAT);
                PRINTER.setPrintAppendString(" 2. Sign in or create an account", ReceiptBuilder.LINE_FORMAT);
                PRINTER.setPrintAppendString(" 1. Receive a credit of $5 to your card", ReceiptBuilder.LINE_FORMAT);
                PRINTER.setPrintAppendString("", ReceiptBuilder.LINE_FORMAT);
                PRINTER.setPrintAppendString("", ReceiptBuilder.LINE_FORMAT);
                PRINTER.setPrintAppendString(".", ReceiptBuilder.LINE_FORMAT);
            }
        }


        final Activity _this = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int printStatus = PRINTER.setPrintStart();

                if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                    _this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogUtils.show(_this, "Printer is out of paper");
                        }
                    });
                }
            }
        }).start();
    }

    private Bitmap loadBarcode(){
        try {
            InputStream bitmap=getAssets().open("barcode.gif");
            return BitmapFactory.decodeStream(bitmap);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return null;
        }
    }
 }
