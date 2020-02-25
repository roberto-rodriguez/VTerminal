package com.voltcash.vterminal.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

import com.voltcash.vterminal.R;

/**
 * Created by roberto.rodriguez on 2/24/2020.
 */

public class ViewUtil {

    public static void showError(AppCompatActivity activity, String title, String msg) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, null)
                .setCancelable(true)
                .setIcon(R.drawable.error)
                .show();
    }

    public static ProgressDialog buildProgressDialog(AppCompatActivity caller, String title, String msg) {
        ProgressDialog mProgressDialog = new ProgressDialog(caller);
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(msg);
        mProgressDialog.show();
        return mProgressDialog;
    }
}