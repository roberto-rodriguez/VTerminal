package com.voltcash.vterminal.views.receipt;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;
import com.pax.poslink.peripheries.POSLinkPrinter;
import com.pax.poslink.peripheries.ProcessResult;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.util.Constants;
import com.voltcash.vterminal.util.ViewUtil;
import com.voltcash.vterminal.util.thread.AppThreadPool;

/**
 * Created by roberto.rodriguez on 2/25/2020.
 */

public class ReceiptView extends AppCompatActivity implements View.OnClickListener  {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);

        setTitle("Receipt");

        try{
            setContentView(R.layout.receipt_activity);

            String receipt = (String)getIntent().getExtras().get(Constants.RECEIPT);

            Button m_Back = (Button)findViewById(R.id.payment_receipt_back);
            m_Back.setOnClickListener(this);

            WebView m_Receipt = (WebView)findViewById(R.id.payment_receipt);
            m_Receipt.loadDataWithBaseURL(null, receipt, "text/html", "utf-8", null);
        }catch(Exception e){
            ViewUtil.showError(this, "Creating ReceiptView", e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.payment_receipt_back:
                print();
               // ReceiptView.this.finish();
                break;
        }
    }

    public void print() {
        try {

            final AppCompatActivity _this = this;
            Log.i("iii", "print-------------------------------------------------------------------");
            WebView vebView = (WebView) findViewById(R.id.payment_receipt);
            final Bitmap resultBitmap = PrintUtil.shotWebView(vebView);
            Log.i("iii", "width---" + resultBitmap.getWidth());
            printMerchantCopy(new Runnable() {
                @Override
                public void run() {
                    ViewUtil.showError(_this, "print", "Printed Successfully");
                }
            }, resultBitmap);

        }catch(Exception e){
            e.printStackTrace();

            ViewUtil.showError(this, "Error in print()" , e.getMessage());
        }
    }

    private void printMerchantCopy(final Runnable printFinish, final Bitmap bitmap) {
        final AppCompatActivity _this = this;
        final ProcessingDialog processingDialog = new ProcessingDialog(new ProgressDialog(ReceiptView.this));
        processingDialog.start("Printing", false);
        AppThreadPool.getInstance().runInBackground(new Runnable() {
            @Override
            public void run() {

                POSLinkPrinter.getInstance(ReceiptView.this).print(bitmap, POSLinkPrinter.CutMode.FULL_PAPER_CUT, new POSLinkPrinter.PrintListener() {
                    @Override
                    public void onSuccess() {
                        dismissDialog(processingDialog, printFinish);
                    }

                    @Override
                    public void onError(ProcessResult processResult) {
                        dismissDialog(processingDialog, printFinish);
                        toastError(processResult.getMessage());
                    }
                });
            }
        });
    }

    private static void dismissDialog(final ProcessingDialog processingDialog, final Runnable printFinish) {
        AppThreadPool.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                processingDialog.dismiss();
                printFinish.run();
            }
        });
    }

    private void toastError(final String ret) {
        AppThreadPool.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ReceiptView.this, "Print Error---" + ret, Toast.LENGTH_LONG).show();
            }
        });
    }

 }
