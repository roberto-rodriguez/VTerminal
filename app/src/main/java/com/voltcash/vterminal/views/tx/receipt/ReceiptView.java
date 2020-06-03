package com.voltcash.vterminal.views.tx.receipt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.util.ReceiptBuilder;
import com.voltcash.vterminal.util.TxData;
import com.voltcash.vterminal.util.ViewUtil;
import com.voltcash.vterminal.util.DialogUtils;
import com.voltcash.vterminal.views.home.HomeActivity;
import com.zcs.sdk.Printer;
import com.zcs.sdk.SdkResult;
import java.io.Serializable;
import java.util.List;

import static com.voltcash.vterminal.VTerminal.DRIVER_MANAGER;

/**
 * Created by roberto.rodriguez on 2/25/2020.
 */

public class ReceiptView extends AppCompatActivity implements View.OnClickListener  {
    private Printer PRINTER;

    public static void show(Activity originActivity, List<String> receiptLines){
        TxData.clear();
        Intent intent = new Intent(originActivity, ReceiptView.class);

        intent.putExtra("receiptLines", (Serializable) receiptLines);
        originActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setTitle("Receipt");
        setContentView(R.layout.receipt_activity);

        try{
            PRINTER = DRIVER_MANAGER.getPrinter();

            List<String> receiptLines = (List<String>)getIntent().getSerializableExtra("receiptLines");

            String receipt =  ReceiptBuilder.build(receiptLines, PRINTER);

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

    public void print() {
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

 }
