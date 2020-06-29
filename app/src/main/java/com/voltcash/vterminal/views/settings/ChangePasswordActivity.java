package com.voltcash.vterminal.views.settings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.services.AuthService;
import com.voltcash.vterminal.util.ViewUtil;
import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity implements  View.OnClickListener{

    private TextView currentPasswordTextView;
    private TextView newPasswordTextView;
    private TextView reNewPasswordTextView;
    private Button changePasswordButton;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_password);

        currentPasswordTextView = (TextView)findViewById(R.id.current_password);
        newPasswordTextView = (TextView)findViewById(R.id.new_password);
        reNewPasswordTextView = (TextView)findViewById(R.id.re_new_password);

        changePasswordButton = (Button)findViewById(R.id.change_password_button);

        changePasswordButton.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
       final ChangePasswordActivity _this = this;

        String currentPassword = currentPasswordTextView.getText().toString();
        String newPassword = newPasswordTextView.getText().toString();
        String reNewPassword = reNewPasswordTextView.getText().toString();

        if(currentPassword == null || currentPassword.isEmpty() ||
                newPassword == null || newPassword.isEmpty() ||
                reNewPassword == null || reNewPassword.isEmpty()
        ){
            ViewUtil.showError(this, "Invalid Input", "All fields are required");
            return;
        }

        if(!newPassword.equals(reNewPassword)){
            ViewUtil.showError(this, "Invalid Input", "New password does not match");
            return;
        }

        AuthService.changePassword(currentPassword, newPassword, new ServiceCallback(this) {
            @Override
            public void onSuccess(Map map) {

                new AlertDialog.Builder(_this)
                        .setTitle("Success")
                        .setMessage("Your password was changed successfully")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
                           public void onClick(DialogInterface dialog, int which){
                               _this.finish();
                            }
                        })
                        .setCancelable(true)
                        .show();
            }
        });
    }
}
