package com.voltcash.vterminal.views.auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.util.Settings;
import com.voltcash.vterminal.views.home.HomeActivity;
import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.services.AuthService;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.PreferenceUtil;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private TextView emailTextView;
    private TextView passwordTextView;


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // setContentView(R.layout.content_progress_dialog);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        emailTextView = (TextView)findViewById(R.id.login_email);
        emailTextView.getBackground().setColorFilter(R.color.VOLTCASH_GREEN, PorterDuff.Mode.SRC_ATOP);
        emailTextView.setText("roberto@girocheck.com");

        passwordTextView = (TextView)findViewById(R.id.login_password);
        passwordTextView.getBackground().setColorFilter(R.color.VOLTCASH_GREEN, PorterDuff.Mode.SRC_ATOP);
        passwordTextView.setText("a");

        //Check if not have Serial Number go to MainActivity
        PreferenceUtil.getSerialNumber(this);

        ((TextView)findViewById(R.id.version_text)).setText(Settings.VERSION);

    }

    public void onLogin(View view) {
            String serialNumber = PreferenceUtil.getSerialNumber(this);

            if(serialNumber == null){return;}

            String terminalUsername = PreferenceUtil.read(Field.AUTH.TERMINAL_USERNAME);
            String terminalPassword = PreferenceUtil.read(Field.AUTH.TERMINAL_PASSWORD);

            String email = emailTextView.getText().toString();
            String password = passwordTextView.getText().toString();

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

    public void onDisconnect(View view){
        //Clean all these fields in Preferences
        PreferenceUtil.write(this, new HashMap(),
                Field.AUTH.CLERK_FIRST_NAME,
                Field.AUTH.CLERK_LAST_NAME ,
                Field.AUTH.CLERK_ID        ,
                Field.AUTH.SESSION_TOKEN);

        Intent intent = new Intent(getApplicationContext(), AuthTerminalActivity.class);
        startActivity(intent);
    }

}
