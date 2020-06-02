package com.voltcash.vterminal.views.home;

import android.app.Fragment;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.voltcash.vterminal.R;
import com.voltcash.vterminal.util.Constants;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.ViewUtil;
import com.voltcash.vterminal.util.cardReader.FragmentWithCardReader;
import com.voltcash.vterminal.views.report.ActivityReportActivity;
import com.voltcash.vterminal.views.settings.ClerkSettingsActivity;
import com.voltcash.vterminal.views.settings.TerminalSettingsActivity;
import com.voltcash.vterminal.views.tx.TxActivity;
import com.voltcash.vterminal.views.tx.TxBalanceActivity;
import com.voltcash.vterminal.views.tx.TxBalanceFragment;
import com.voltcash.vterminal.views.tx.TxCardToBankActivity;
import com.voltcash.vterminal.views.tx.TxCardToBankFragment;
import com.voltcash.vterminal.views.tx.TxFragment;

public class HomeActivity extends AppCompatActivity {

    private ConstraintLayout homeMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeMenu = findViewById(R.id.home_menu_container);

        setTitle("Voltcash Terminal");
    }

    private void openFragment(FragmentWithCardReader fragment){
        homeMenu.setVisibility(View.INVISIBLE);

        getFragmentManager().beginTransaction().add(R.id.frame_container, fragment).commit();
    }

    private void openTxFragment(String operation){
        Bundle bundle = new Bundle();
        bundle.putString(Field.TX.OPERATION, operation);

        TxFragment fragment = new TxFragment();
        fragment.setArguments(bundle);
        openFragment(fragment);
    }

//    @Override
//    public void onBackPressed() {}

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.menu_clerk:
                intent = new Intent(getApplicationContext(), ClerkSettingsActivity.class);
                break;
            case R.id.menu_terminal:
                intent = new Intent(getApplicationContext(), TerminalSettingsActivity.class);
                break;
            case R.id.home:
                getFragmentManager().popBackStack();
                break;
            default:
        }

        startActivity(intent);
        return true;
    }
}
