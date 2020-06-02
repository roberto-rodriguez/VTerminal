package com.voltcash.vterminal.util.cardReader;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by roberto.rodriguez on 6/1/2020.
 */

public class CardReaderHandler extends Handler implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener {
        WeakReference<Fragment> mFragment;

        CardReaderHandler(Fragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            FragmentWithCardReader fragment = (FragmentWithCardReader) mFragment.get();
            if (fragment == null || !fragment.isAdded())
                return;

            if (fragment.getProgressDialog() != null) {
                fragment.getProgressDialog().dismiss();
            }

            String card= (String) msg.obj;

            fragment.setCardNumber(card);
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            FragmentWithCardReader fragment = (FragmentWithCardReader) mFragment.get();
            if (fragment != null && fragment.isAdded()) {
                fragment.closeSearch();
            }
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            onCancel(dialog);
    }
}
