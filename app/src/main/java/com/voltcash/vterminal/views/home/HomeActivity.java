package com.voltcash.vterminal.views.home;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.voltcash.vterminal.R;
import com.voltcash.vterminal.util.ViewUtil;
import com.voltcash.vterminal.views.settings.ClerkSettingsActivity;
import com.voltcash.vterminal.views.settings.TerminalSettingsActivity;
import com.voltcash.vterminal.views.tx.TxActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setTitle("Voltcash Terminal");

    }

    protected void onProcessCheck(View view){
        Intent txActivity = new Intent(getApplicationContext(), TxActivity.class);
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
            default:
        }

        startActivity(intent);
        return true;
    }
}
