package com.voltcash.vterminal.views.lab;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.voltcash.vterminal.R;
import com.voltcash.vterminal.VTerminal;
import com.voltcash.vterminal.util.ViewUtil;
import com.voltcash.vterminal.views.lab.util.DialogUtils;
import com.voltcash.vterminal.views.lab.util.SDK_Result;
import com.zcs.sdk.DriverManager;
import com.zcs.sdk.SdkData;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.card.CardInfoEntity;
import com.zcs.sdk.card.CardReaderManager;
import com.zcs.sdk.card.CardReaderTypeEnum;
import com.zcs.sdk.card.CardSlotNoEnum;
import com.zcs.sdk.card.ICCard;
import com.zcs.sdk.card.MagCard;
import com.zcs.sdk.card.RfCard;
import com.zcs.sdk.listener.OnSearchCardListener;
import com.zcs.sdk.util.StringUtils;

import java.lang.ref.WeakReference;


/**
 * Created by yyzz on 2018/5/24.
 */

public class CardFragment extends PreferenceFragment {

    private static final String TAG = "CardFragment";

    private static final int READ_TIMEOUT = 60 * 1000;
    private static final int MSG_CARD_OK = 2001;
    private static final int MSG_CARD_ERROR = 2002;
    private static final int MSG_CARD_APDU = 2003;
    private static final int MSG_RF_CARD_APDU = 2007;
    private static final int MSG_CARD_M1 = 2004;
    private static final int MSG_CARD_MF_PLUS = 2005;
    private static final int MSG_CARD_FELICA = 2006;

    public static final byte[] APDU_SEND_IC = {0x00, (byte) 0xA4, 0x04, 0x00, 0x0E, 0x31, 0x50, 0x41, 0x59, 0x2E, 0x53, 0x59, 0x53, 0x2E, 0x44, 0x44, 0x46, 0x30, 0x31, 0X00};
    public static final byte[] APDU_SEND_RF = {0x00, (byte) 0xA4, 0x04, 0x00, 0x0E, 0x32, 0x50, 0x41, 0x59, 0x2E, 0x53, 0x59, 0x53, 0x2E, 0x44, 0x44, 0x46, 0x30, 0x31, 0x00};
    public static final byte[] APDU_SEND_RANDOM = {0x00, (byte) 0x84, 0x00, 0x00, 0x08};
    // 10 06 01 2E 45 76 BA C5 45 2B 01 09 00 01 80 00
    public static final byte[] APDU_SEND_FELICA = {0x10, 0x06, 0x01, 0x2E, 0x45, 0x76, (byte) 0xBA, (byte) 0xC5, 0x45, 0x2B, 0x01, 0x09, 0x00, 0x01, (byte) 0x80, 0x00};
    private static final String KEY_APDU = "APDU";
    private static final String KEY_RF_CARD_TYPE = "RF_CARD_TYPE";
    private static final byte SLOT_USERCARD = 0x00;
    private static final byte SLOT_PSAM1 = 0x01;
    private static final byte SLOT_PSAM2 = 0x02;

    private DriverManager mDriverManager = VTerminal.DRIVER_MANAGER;
    //private CardHandler mHandler;
    private CardReaderManager mCardReadManager;
    private ICCard mICCard;
    private RfCard mRfCard;
    private MagCard mMagCard;

    private ProgressDialog mProgressDialog;
    private Dialog mCardInfoDialog;

    private Handler mHandler;


    boolean ifSearch = true;
    boolean isM1 = false;
    boolean isMfPlus = false;
    boolean isNtag = false;
    String keyNtag = "FFFFFFFF";

    String keyM1 = "FFFFFFFFFFFF";
    byte keyType = 0x00; // 0x00 typeA, 0x01 typeB
    boolean hasSetM1 = false;

    String keyMfPlus = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
    byte[] addressMfPlus = {0x40, 0x00};
    boolean hasSetMf = false;

    byte mRfCardType = 0;
    CardReaderTypeEnum mCardType = CardReaderTypeEnum.MAG_IC_RF_CARD;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_card);

        mHandler = new CardHandler(this);

        mCardReadManager = mDriverManager.getCardReadManager();
        mICCard = mCardReadManager.getICCard();
        mRfCard = mCardReadManager.getRFCard();
        mMagCard = mCardReadManager.getMAGCard();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // search card and read, just wait a moment


        // read mag card
        findPreference("Magnetic stripe card").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                searchBankCard(CardReaderTypeEnum.MAG_CARD);
                return true;
            }
        });

    }


    void searchBankCard(CardReaderTypeEnum cardType) {
        mCardType = cardType;
        mRfCardType = SdkData.RF_TYPE_A | SdkData.RF_TYPE_B;

        showSearchCardDialog("Waiting", "Please swipe card");

        mCardReadManager.cancelSearchCard();
        mCardReadManager.searchCard(cardType, READ_TIMEOUT, mListener);
    }

    OnSearchCardListener mListener = new OnSearchCardListener() {
        @Override
        public void onCardInfo(CardInfoEntity cardInfoEntity) {
            CardReaderTypeEnum cardType = cardInfoEntity.getCardExistslot();
            switch (cardType) {

                case MAG_CARD:
                    readMagCard();
                    break;

            }
        }

        @Override
        public void onError(int i) {
            isM1 = false;
            isMfPlus = false;
            isNtag = false;
        }

        @Override
        public void onNoCard(CardReaderTypeEnum cardReaderTypeEnum, boolean b) {

        }
    };



    private void showSearchCardDialog(@StringRes int title, @StringRes int msg) {
        showSearchCardDialog( getString(title), getString(msg));
    }

    private void showSearchCardDialog(String title, String msg) {
        mProgressDialog = (ProgressDialog) DialogUtils.showProgress(getActivity(), title, msg, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mCardReadManager.cancelSearchCard();
            }
        });
    }


    private void readMagCard() {
       int a = 3;
        mProgressDialog.dismiss();

        // use `getMagReadData` to get mag track data and parse data. if it is bank card, then parse exp and card no
        // use `getMagTrackData` to get origin track data
       CardInfoEntity cardInfo = mMagCard.getMagReadData();

//        Log.d(TAG, "cardInfo.getResultcode():" + cardInfo.getResultcode());
        if (cardInfo.getResultcode() == SdkResult.SDK_OK) {
//            //String exp = cardInfo.getExpiredDate();
              String cardNo = cardInfo.getCardNo();
//            //String tk1 = cardInfo.getTk1();
              String tk2 = cardInfo.getTk2();
//            //String tk3 = cardInfo.getTk3();
//            Message msg = Message.obtain();
//            msg.what = MSG_CARD_OK;
//            msg.arg1 = cardInfo.getResultcode();
//            msg.obj = cardInfo;
//            mHandler.sendMessage(msg);

            Message msg = Message.obtain();
            msg.what = MSG_CARD_OK;
            msg.arg1 = cardInfo.getResultcode();
            msg.obj = cardInfo;
            mHandler.sendMessage(msg);
        } else {
            String k = "no card";
         }
   //     mMagCard.magCardClose();
//        // search again
    //    mCardReadManager.searchCard(mCardType, READ_TIMEOUT, mListener);
    }


    void closeSearch() {
        Log.i(TAG, "closeSearch");
        isNtag = false;
        isM1 = false;
        isMfPlus = false;
        // stop to detect card
        ifSearch = false;
        mCardReadManager.cancelSearchCard();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        closeSearch();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mCardReadManager.closeCard();
        super.onDestroy();
    }




    class CardHandler extends Handler implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener {
        WeakReference<Fragment> mFragment;


        CardHandler(Fragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            CardFragment fragment = (CardFragment) mFragment.get();
            if (fragment == null || !fragment.isAdded())
                return;
            if (mCardInfoDialog != null) {
                mCardInfoDialog.dismiss();
            }
            if (fragment.mProgressDialog != null) {
                fragment.mProgressDialog.dismiss();
            }
            CardInfoEntity cardInfoEntity = (CardInfoEntity) msg.obj;

            mCardInfoDialog = DialogUtils.show(fragment.getActivity(),
                    "Card Number", SDK_Result.obtainCardInfo(fragment.getActivity(), cardInfoEntity),
                    "OK", this, this);
        }

        private String handleRfCardType(byte rfCardType) {
            String type = "";
            switch (rfCardType) {
                case SdkData.RF_TYPE_A:
                    type = "RF_TYPE_A";
                    break;
                case SdkData.RF_TYPE_B:
                    type = "RF_TYPE_B";
                    break;
                case SdkData.RF_TYPE_MEMORY_A:
                    type = "RF_TYPE_MEMORY_A";
                    break;
                case SdkData.RF_TYPE_FELICA:
                    type = "RF_TYPE_FELICA";
                    break;
                case SdkData.RF_TYPE_MEMORY_B:
                    type = "RF_TYPE_MEMORY_B";
                    break;
            }
            return type;
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            CardFragment fragment = (CardFragment) mFragment.get();
            if (fragment != null && fragment.isAdded()) {
                fragment.ifSearch = false;
                closeSearch();
            }
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            CardFragment fragment = (CardFragment) mFragment.get();
            if (fragment != null && fragment.isAdded()) {
                fragment.ifSearch = false;
                closeSearch();
            }
        }
    }
}


