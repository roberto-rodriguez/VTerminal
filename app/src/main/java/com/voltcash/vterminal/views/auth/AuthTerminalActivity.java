package com.voltcash.vterminal.views.auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.services.AuthService;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.PreferenceUtil;
import com.voltcash.vterminal.util.Settings;

import java.util.Map;

public class AuthTerminalActivity extends AppCompatActivity {

    private TextView accessCodeTextView;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_auth_terminal);
        setTitle("Change Password");
        getSupportActionBar().hide();

        accessCodeTextView = (TextView)findViewById(R.id.access_code);
        accessCodeTextView.getBackground().setColorFilter(R.color.VOLTCASH_GREEN, PorterDuff.Mode.SRC_ATOP);

        ((TextView)findViewById(R.id.version_text)).setText(Settings.VERSION);
    }

    public void onAuth(View view){
        String accessCode = accessCodeTextView.getText().toString();



        AuthService.connectTerminal(accessCode, new ServiceCallback(this) {
            @Override
            public void onSuccess(Map response) {
                Log.i("AuthTerminalActivity", "onSuccess"  );

                PreferenceUtil.write(getCtx(), response,
                        Field.AUTH.TERMINAL_USERNAME,
                        Field.AUTH.TERMINAL_PASSWORD,
                        Field.AUTH.TERMINAL_SERIAL_NUMBER,
                        Field.AUTH.MERCHANT_NAME
                );

                Intent loginView = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginView);
            }
        });
    }
}
