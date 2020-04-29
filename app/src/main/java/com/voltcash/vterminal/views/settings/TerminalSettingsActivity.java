package com.voltcash.vterminal.views.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.PreferenceUtil;
import com.voltcash.vterminal.util.Settings;
import com.voltcash.vterminal.views.auth.AuthTerminalActivity;
import com.voltcash.vterminal.R;
import java.util.HashMap;

public class TerminalSettingsActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private TextView checkResolutionLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal_settings);

        setTitle("Terminal Settings");

        String merchant = PreferenceUtil.read( Field.AUTH.MERCHANT_NAME);
        TextView terminalMerchantField = (TextView)findViewById(R.id.settings_terminal_merchant);
        terminalMerchantField.setText("Merchant: " + merchant);

        String serialNumber = PreferenceUtil.read( Field.AUTH.TERMINAL_SERIAL_NUMBER);
        TextView terminalSerialField = (TextView)findViewById(R.id.settings_terminal_serial);
        terminalSerialField.setText(serialNumber);

        checkResolutionLabel = findViewById(R.id.check_resolution_label);
        setCheckResolutionLabel();

        SeekBar checkResolutionSeekBar=(SeekBar) findViewById(R.id.check_image_resolution);
        checkResolutionSeekBar.setProgress(Settings.CHECK_RESOLUTION);
        checkResolutionSeekBar.setMax(400);
        checkResolutionSeekBar.setOnSeekBarChangeListener(this);

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

    public void onProgressChanged (SeekBar seekBar, int progresValue, boolean fromUser) {
        Settings.CHECK_RESOLUTION = progresValue;
        setCheckResolutionLabel();
    }

    private void setCheckResolutionLabel(){
        checkResolutionLabel.setText("Check Image Resolution: " + Settings.CHECK_RESOLUTION + " dpi");
    }

    public void onStartTrackingTouch(SeekBar var1){};

    public void onStopTrackingTouch(SeekBar var1){};
}
