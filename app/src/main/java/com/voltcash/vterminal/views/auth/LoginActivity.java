package com.voltcash.vterminal.views.auth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.voltcash.vterminal.R;
import static com.voltcash.vterminal.VTerminal.DRIVER_MANAGER;
import com.voltcash.vterminal.views.MainActivity;
import com.voltcash.vterminal.views.home.HomeActivity;
import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.services.AuthService;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.PreferenceUtil;
import com.voltcash.vterminal.views.lab.util.DialogUtils;
import com.zcs.sdk.Printer;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.print.PrnStrFormat;
import com.zcs.sdk.print.PrnTextFont;
import com.zcs.sdk.print.PrnTextStyle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private TextView emailTextView;
    private TextView passwordTextView;

    private Printer PRINTER;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        emailTextView = (TextView)findViewById(R.id.login_email);
        emailTextView.getBackground().setColorFilter(R.color.VOLTCASH_GREEN, PorterDuff.Mode.SRC_ATOP);
        emailTextView.setText("roberto@girocheck.com");

        passwordTextView = (TextView)findViewById(R.id.login_password);
        passwordTextView.getBackground().setColorFilter(R.color.VOLTCASH_GREEN, PorterDuff.Mode.SRC_ATOP);
        passwordTextView.setText("a");

        String serialNumber = PreferenceUtil.read(Field.AUTH.TERMINAL_SERIAL_NUMBER);

        if(serialNumber == null){
            //If it gets here is because there was an error, need to restart the app
            Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainActivity);
        }

        PRINTER = DRIVER_MANAGER.getPrinter();
    }

//    public void onLogin(View view) {
//        printMatrixText();
//    }

    private void printMatrixText() {
        final Activity _this = this;
        new Thread(new Runnable() {
            @Override
            public void run() {

                int printStatus = PRINTER.getPrinterStatus();
                if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                     runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogUtils.show(_this, "Printer is out of paper");
                        }
                    });
                } else {

                    PrnStrFormat format = new PrnStrFormat();
                    format.setTextSize(25);
                    format.setAli(Layout.Alignment.ALIGN_CENTER);
                    format.setStyle(PrnTextStyle.BOLD);
                    format.setFont(PrnTextFont.DEFAULT);

                    PRINTER.setPrintAppendString("Header", format);
                    format.setTextSize(22);
                    format.setStyle(PrnTextStyle.NORMAL);
                    format.setAli(Layout.Alignment.ALIGN_NORMAL);
                    PRINTER.setPrintAppendString(" ", format);
                    PRINTER.setPrintAppendString("Simple Text", format);
                    PRINTER.setPrintAppendString(" ", format);
                    PRINTER.setPrintAppendString(" ", format);
                    PRINTER.setPrintAppendString(" ", format);
                    PRINTER.setPrintAppendString(" ", format);
                    printStatus = PRINTER.setPrintStart();
                    if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                        _this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogUtils.show(_this, "Printer is out of paper");
                            }
                        });
                    }
                }
            }
        }).start();
    }

    public void onLogin(View view) {
            String serialNumber = PreferenceUtil.read(Field.AUTH.TERMINAL_SERIAL_NUMBER);
            String terminalUsername = PreferenceUtil.read(Field.AUTH.TERMINAL_USERNAME);
            String terminalPassword = PreferenceUtil.read(Field.AUTH.TERMINAL_PASSWORD);

            String email = emailTextView.getText().toString();
            String password = passwordTextView.getText().toString();

            Log.i("LoginActivity", "email = " + email);
            Log.i("LoginActivity", "password = " + password);

            AuthService.login(serialNumber, terminalUsername, terminalPassword, email, password, new ServiceCallback(this) {
                @Override
                public void onSuccess(Map response) {
                    PreferenceUtil.write(getCtx(), response,
                            Field.AUTH.CLERK_FIRST_NAME,
                            Field.AUTH.CLERK_LAST_NAME,
                            Field.AUTH.CLERK_ID,
                            Field.AUTH.SESSION_TOKEN
                    );

                    Intent homeView = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(homeView);
                }
            });
    }
}
