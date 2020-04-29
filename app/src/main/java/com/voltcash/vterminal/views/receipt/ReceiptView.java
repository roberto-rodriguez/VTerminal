package com.voltcash.vterminal.views.receipt;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
        Log.i("ReceiptView", "onCreate -------------------------------------------------------------------");
        setTitle("Receipt");
      //  getActionBar().hide();

        try{
            setContentView(R.layout.receipt_activity);

            String receipt = (String)getIntent().getExtras().get(Constants.RECEIPT);

            Log.i("ReceiptView", "receipt -------------------------------------------------------------------");

            Button m_Back = (Button)findViewById(R.id.payment_receipt_back);
            Button m_Print = (Button)findViewById(R.id.payment_receipt_print);
            m_Back.setOnClickListener(this);
            m_Print.setOnClickListener(this);


            Log.i("ReceiptView", "after setOnClickListener");

            final WebView v = (WebView)findViewById(R.id.payment_receipt);

//            Log.i("ReceiptView", "after m_Receipt");
//            m_Receipt.loadDataWithBaseURL(null, receipt, "text/html", "utf-8", null);
//
//            Log.i("ReceiptView", "after loadDataWithBaseURL");

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
         //   v.setBackgroundColor(Color.TRANSPARENT);
            //v.getSettings().setJavaScriptEnabled(true);
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
                 ReceiptView.this.finish();
                 break;
            case R.id.payment_receipt_print:
                 print();
                break;
        }
    }

    public void print() {
        try {

            final AppCompatActivity _this = this;
            Log.i("iii", "print-------------------------------------------------------------------");
            WebView vebView = (WebView) findViewById(R.id.payment_receipt);

            //receipt_scroll_view
            //  final Bitmap resultBitmap = PrintUtil.shotWebView(vebView);
              final Bitmap resultBitmap = PrintUtil.createBitmapFromView(vebView);
            Log.i("iii", "width---" + resultBitmap.getWidth());
            printMerchantCopy(new Runnable() {
                @Override
                public void run() {

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
                        //toastError(processResult.getMessage());
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
