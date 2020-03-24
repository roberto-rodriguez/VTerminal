package com.voltcash.vterminal.views.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.PreferenceUtil;
import com.voltcash.vterminal.views.auth.AuthTerminalActivity;

import java.util.HashMap;

public class TerminalSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal_settings);

        setTitle("Terminal Settings");

        String serialNumber = PreferenceUtil.read( Field.AUTH.TERMINAL_SERIAL_NUMBER);
        TextView terminalSerialField = (TextView)findViewById(R.id.settings_terminal_serial);
        terminalSerialField.setText("Serial Number: " + serialNumber);
    }


    protected void onDisconnect(View view){
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
