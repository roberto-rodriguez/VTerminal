package com.voltcash.vterminal.views.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.voltcash.vterminal.R;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.PreferenceUtil;
import com.voltcash.vterminal.views.tx.TxActivity;

public class ClerkSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setTitle("Clerk Settings");

        String firstName = PreferenceUtil.read(getApplicationContext(), Field.AUTH.CLERK_FIRST_NAME);
        String lastName = PreferenceUtil.read(getApplicationContext(), Field.AUTH.CLERK_LAST_NAME);

        TextView clerkNameField = (TextView)findViewById(R.id.settings_clerk_name);
        clerkNameField.setText(firstName + " " + lastName);
    }


    protected void onProcessCheck(View view){
        Intent txActivity = new Intent(getApplicationContext(), TxActivity.class);
        startActivity(txActivity);
    }

}
