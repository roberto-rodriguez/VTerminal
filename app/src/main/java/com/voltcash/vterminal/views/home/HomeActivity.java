package com.voltcash.vterminal.views.home;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.voltcash.vterminal.R;
import com.voltcash.vterminal.util.Constants;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.Settings;
import com.voltcash.vterminal.util.cardReader.FragmentWithCardReader;
import com.voltcash.vterminal.views.report.ActivityReportActivity;
import com.voltcash.vterminal.views.settings.ClerkSettingsActivity;
import com.voltcash.vterminal.views.settings.TerminalSettingsActivity;
import com.voltcash.vterminal.views.tx.TxBalanceFragment;
import com.voltcash.vterminal.views.tx.TxCardToBankFragment;
import com.voltcash.vterminal.views.tx.TxFragment;
import com.voltcash.vterminal.views.tx.receipt.ReceiptView;

public class HomeActivity extends AppCompatActivity {

    private ConstraintLayout homeMenu;

    private Fragment openFragment = null;

    private boolean onBackEnabled = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeMenu = findViewById(R.id.home_menu_container);

        resetHome();

        Boolean isError =  getIntent().getBooleanExtra("isError", false);

        if(isError){
            Toast.makeText(this, "Unexpected Error. A notification was automatically sent to Voltcash with details of the issue. Please try again.", Toast.LENGTH_LONG).show();
        }

        if(Settings.ENV == Constants.ENV_LOCAL){
            LinearLayout demoLabel = (LinearLayout)findViewById(R.id.button_home_demo_mode);
            demoLabel.setVisibility(View.VISIBLE);
        }
    }

    private void resetHome(){
        setTitle("Voltcash Terminal");

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    private void openFragment(FragmentWithCardReader fragment){
        this.openFragment = fragment;
        homeMenu.setVisibility(View.INVISIBLE);

        getFragmentManager().beginTransaction().add(R.id.frame_container, fragment).commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void openTxFragment(String operation){
        Bundle bundle = new Bundle();
        bundle.putString(Field.TX.OPERATION, operation);

        TxFragment fragment = new TxFragment();
        fragment.setArguments(bundle);
        openFragment(fragment);
    }


    public void onTxCheck(View view){
       openTxFragment("01");
    }

    public void onTxCash(View view){
        openTxFragment("02");
    }

    public void onTxBalanceInquiry(View view){
        openFragment(new TxBalanceFragment());
    }

    public void onTxCardToBank(View view){
        openFragment(new TxCardToBankFragment());
    }

    public void onPrintLastReceipt(View view){
        if(Constants.receiptLines != null){
            ReceiptView.show(this , Constants.receiptLines);
        }else{
            Toast.makeText(this, "No receipt to print", Toast.LENGTH_LONG).show();
        }
    }


    public void onActivityReportActivity(View view){
        Intent txActivity = new Intent(getApplicationContext(), ActivityReportActivity.class);
        startActivity(txActivity);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    public void setEnableOnBack(boolean onBackEnabled){
        this.onBackEnabled = onBackEnabled;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(!onBackEnabled)return true;

        Intent intent = null;

        switch (item.getItemId()) {
            case R.id.menu_clerk:
                intent = new Intent(getApplicationContext(), ClerkSettingsActivity.class);
                break;
            case R.id.menu_terminal:
                intent = new Intent(getApplicationContext(), TerminalSettingsActivity.class);
                break;
            default:
                onBackPressed();
                break;
        }

        if(intent != null){
            startActivity(intent);
        }

        return true;
    }

    @Override
    public void onBackPressed(){
        if(!onBackEnabled)return;

        if(this.openFragment != null){
            resetHome();
            getFragmentManager().beginTransaction().remove(openFragment).commit();
            homeMenu.setVisibility(View.VISIBLE);
            this.openFragment = null;
        }
    }

}
