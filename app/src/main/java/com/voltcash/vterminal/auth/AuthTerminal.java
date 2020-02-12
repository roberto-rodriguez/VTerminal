package com.voltcash.vterminal.auth;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.home.HomeActivity;

public class AuthTerminal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_terminal);

        getSupportActionBar().hide();

        TextView accessCodeTextView = (TextView)findViewById(R.id.access_code);
        accessCodeTextView.getBackground().setColorFilter(R.color.VOLTCASH_GREEN, PorterDuff.Mode.SRC_ATOP);
    }

    protected void onAuth(View view){
        Intent homeView = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(homeView);
    }
}
