package com.voltcash.vterminal.views.receipt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.util.Constants;
import com.voltcash.vterminal.util.TxData;
import com.voltcash.vterminal.util.ViewUtil;
import com.voltcash.vterminal.util.thread.AppThreadPool;
import com.voltcash.vterminal.views.lab.util.DialogUtils;
import com.zcs.sdk.Printer;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.print.PrnStrFormat;
import com.zcs.sdk.print.PrnTextFont;
import com.zcs.sdk.print.PrnTextStyle;

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

         //   String receipt = (String)getIntent().getExtras().get(Constants.RECEIPT);

            List<String> receiptLines = (List<String>)getIntent().getSerializableExtra("receiptLines");

            String receipt =  ReceiptBuilder.build(receiptLines, PRINTER);

            Button m_Back = (Button)findViewById(R.id.payment_receipt_back);
            Button m_Print = (Button)findViewById(R.id.payment_receipt_print);
            m_Back.setOnClickListener(this);
            m_Print.setOnClickListener(this);

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

//    public void print() {
//        try {
//
//            final AppCompatActivity _this = this;
//            Log.i("iii", "print-------------------------------------------------------------------");
//            WebView vebView = (WebView) findViewById(R.id.payment_receipt);
//
//            //receipt_scroll_view
//            //  final Bitmap resultBitmap = PrintUtil.shotWebView(vebView);
//              final Bitmap resultBitmap = PrintUtil.createBitmapFromView(vebView);
//            Log.i("iii", "width---" + resultBitmap.getWidth());
//            printMerchantCopy(new Runnable() {
//                @Override
//                public void run() {
//
//                }
//            }, resultBitmap);
//
//        }catch(Exception e){
//            e.printStackTrace();
//
//            ViewUtil.showError(this, "Error in print()" , e.getMessage());
//        }
//    }

//    private void printMerchantCopy(final Runnable printFinish, final Bitmap bitmap) {
//        final AppCompatActivity _this = this;
//        final ProcessingDialog processingDialog = new ProcessingDialog(new ProgressDialog(ReceiptView.this));
//        processingDialog.start("Printing", false);
//        AppThreadPool.getInstance().runInBackground(new Runnable() {
//            @Override
//            public void run() {
//
////                POSLinkPrinter.getInstance(ReceiptView.this).print(bitmap, POSLinkPrinter.CutMode.FULL_PAPER_CUT, new POSLinkPrinter.PrintListener() {
////                    @Override
////                    public void onSuccess() {
////                        dismissDialog(processingDialog, printFinish);
////                    }
////
////                    @Override
////                    public void onError(ProcessResult processResult) {
////                        dismissDialog(processingDialog, printFinish);
////                        //toastError(processResult.getMessage());
////                    }
////                });
//            }
//        });
//    }
//
//    private static void dismissDialog(final ProcessingDialog processingDialog, final Runnable printFinish) {
//        AppThreadPool.getInstance().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                processingDialog.dismiss();
//                printFinish.run();
//            }
//        });
//    }
//
//    private void toastError(final String ret) {
//        AppThreadPool.getInstance().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(ReceiptView.this, "Print Error---" + ret, Toast.LENGTH_LONG).show();
//            }
//        });
//    }

 }
