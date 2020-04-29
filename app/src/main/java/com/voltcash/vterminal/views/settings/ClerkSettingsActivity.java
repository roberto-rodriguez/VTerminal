package com.voltcash.vterminal.views.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.services.AuthService;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.PreferenceUtil;
import com.voltcash.vterminal.util.ViewUtil;
import com.voltcash.vterminal.views.auth.LoginActivity;
import com.voltcash.vterminal.views.tx.TxActivity;

import java.util.HashMap;
import java.util.Map;

public class ClerkSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clerk_settings);

        setTitle("Clerk Settings");

        try{
            String firstName = PreferenceUtil.read( Field.AUTH.CLERK_FIRST_NAME);
            String lastName  = PreferenceUtil.read( Field.AUTH.CLERK_LAST_NAME);

            TextView clerkNameField = (TextView)findViewById(R.id.settings_clerk_name);
            clerkNameField.setText(firstName + " " + lastName);
        }catch(Exception e){
            ViewUtil.showError(this, "Error reading preference", e.getMessage());
        }
    }


    public void onChangePassword(View view){
        Intent txActivity = new Intent(getApplicationContext(), TxActivity.class);
        startActivity(txActivity);
    }

    public void onLogOut(View view){
        String sessionToken = PreferenceUtil.read(Field.AUTH.SESSION_TOKEN);
        //Clean all these fields in Preferences
        PreferenceUtil.write(this, new HashMap(),
                Field.AUTH.CLERK_FIRST_NAME,
                Field.AUTH.CLERK_LAST_NAME ,
                Field.AUTH.CLERK_ID        ,
                Field.AUTH.SESSION_TOKEN);

        AuthService.logOut(sessionToken, new ServiceCallback(this) {
            @Override
            public void onSuccess(Map response) {
                Intent loginView = new Intent(getCtx(), LoginActivity.class);
                startActivity(loginView);
            }
        });

    }
}
