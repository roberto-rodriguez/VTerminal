package com.voltcash.vterminal.auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.home.HomeActivity;
import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.services.AuthService;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.PreferenceUtil;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private TextView emailTextView;
    private TextView passwordTextView;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        emailTextView = (TextView)findViewById(R.id.login_email);
        emailTextView.getBackground().setColorFilter(R.color.VOLTCASH_GREEN, PorterDuff.Mode.SRC_ATOP);

        passwordTextView = (TextView)findViewById(R.id.login_password);
        passwordTextView.getBackground().setColorFilter(R.color.VOLTCASH_GREEN, PorterDuff.Mode.SRC_ATOP);
    }

    protected void onLogin(View view){
        String email = emailTextView.getText().toString();
        String password = passwordTextView.getText().toString();

        AuthService.login(email, password, new ServiceCallback(this) {
            @Override
            public void onSuccess(Map response) {
                PreferenceUtil.write(getApplicationContext(), response,
                        Field.AUTH.CLERK_FIRST_NAME,
                        Field.AUTH.CLERK_LAST_NAME ,
                        Field.AUTH.CLERK_ID        ,
                        Field.AUTH.SESSION_TOKEN
                );

                Intent homeView = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(homeView);
            }
        });


    }
}
