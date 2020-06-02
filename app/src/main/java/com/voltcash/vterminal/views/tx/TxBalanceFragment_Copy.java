package com.voltcash.vterminal.views.tx;

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kofax.kmc.kut.utilities.AppContextProvider;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.VTerminal;
import com.voltcash.vterminal.interfaces.ServiceCallback;
import com.voltcash.vterminal.services.TxService;
import com.voltcash.vterminal.util.Constants;
import com.voltcash.vterminal.util.Field;
import com.voltcash.vterminal.util.PreferenceUtil;
import com.voltcash.vterminal.util.TxData;
import com.voltcash.vterminal.util.ViewUtil;
import com.voltcash.vterminal.views.lab.CardFragment;
import com.voltcash.vterminal.views.lab.util.DialogUtils;
import com.voltcash.vterminal.views.receipt.ReceiptBuilder;
import com.voltcash.vterminal.views.receipt.ReceiptView;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.card.CardInfoEntity;
import com.zcs.sdk.card.CardReaderManager;
import com.zcs.sdk.card.CardReaderTypeEnum;
import com.zcs.sdk.card.MagCard;
import com.zcs.sdk.listener.OnSearchCardListener;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

public class TxBalanceFragment_Copy extends Fragment
        implements
        ActivityCompat.OnRequestPermissionsResultCallback,
        View.OnClickListener{

    private Handler mHandler;
    private CardReaderManager mCardReadManager;
    private MagCard mMagCard;
    private ProgressDialog mProgressDialog;
    private Dialog mCardInfoDialog;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new TxBalanceFragment_Copy.CardHandler(this);


    }

    public final <T extends View> T findViewById(int id) {
        return getActivity().findViewById(id);
    }

    public Context getApplicationContext() {return getActivity().getApplicationContext();}

    public void setTitle(CharSequence title) {getActivity().setTitle(title);}

    void closeSearch() {
        mCardReadManager.cancelSearchCard();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.activity_tx_balance, parent, false);
    }

    @Override
    public void onViewCreated(View view,  Bundle savedInstanceState) {
        AppContextProvider.setContext(getActivity().getApplicationContext());
        TxData.clear();

        setTitle("Balance Inquiry");

        ((EditText)findViewById(R.id.tx_card_field)).setText("4111111111111112");

        ((Button)findViewById(R.id.tx_calculate_fee_button)).setOnClickListener(this);

        mCardReadManager = VTerminal.DRIVER_MANAGER.getCardReadManager();
        mMagCard = mCardReadManager.getMAGCard();

        searchBankCard();
    }

    void searchBankCard() {

        showSearchCardDialog("Waiting", "Please swipe card");

        mCardReadManager.cancelSearchCard();
        mCardReadManager.searchCard(CardReaderTypeEnum.MAG_CARD, 60 * 1000, mListener);
    }

    private void showSearchCardDialog(String title, String msg) {
        try{
            mProgressDialog = (ProgressDialog) DialogUtils.showProgress(this.getActivity(), title, msg, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mCardReadManager.cancelSearchCard();
                }
            });
        }catch(Exception e){
            e.printStackTrace();

            ViewUtil.showError(this.getActivity(), "Exception", e.getMessage());


        }

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

            String a = "Error";
        }

        @Override
        public void onNoCard(CardReaderTypeEnum cardReaderTypeEnum, boolean b) {
            String a = "onNoCard";
        }
    };


    private void readMagCard() {
        CardInfoEntity cardInfo = mMagCard.getMagReadData();

        if (cardInfo.getResultcode() == SdkResult.SDK_OK) {
            final String cardNo = cardInfo.getCardNo();


            Message msg = Message.obtain();
            msg.what = 2001;
            msg.arg1 = cardInfo.getResultcode();
            msg.obj = cardNo;// cardInfo;
            mHandler.sendMessage(msg);
        } else {
            String k = "no card";
        }
        mMagCard.magCardClose();
        mProgressDialog.dismiss();
    }

    public void onCheckBalance(View view){

        final TxBalanceFragment_Copy _this = this;

        final String cardNumber = ((EditText)findViewById(R.id.tx_card_field)).getText().toString();
        TxData.put(Field.TX.CARD_NUMBER, cardNumber);

        TxService.balanceInquiry(new ServiceCallback(this.getActivity()) {
            @Override
            public void onSuccess(Map response) {

                if(response == null){
                    ViewUtil.showError(getCtx(), "Server Error", "Error trying to check balance. Please contact Customer Support");
                    return;
                }

                String balance   = response.get(Field.TX.BALANCE) + "";

                ((TextView)findViewById(R.id.tx_balance_result        )).setText("Balance: $" + balance);

                String card     = TxData.getString(Field.TX.CARD_NUMBER);
                String merchant = PreferenceUtil.read(Field.AUTH.MERCHANT_NAME);
                String requestId= response.get("REQUEST_ID") + "";

                if(card != null && card.length() > 4){
                    card = card.substring(card.length() - 4, card.length());
                }

                List<String> receiptLines = ReceiptBuilder.dateTimeLines();
                receiptLines.add("Location Name -> "    + merchant);
                receiptLines.add("Card Number -> **** " + card);
                receiptLines.add("Balance -> $" + balance);

                String receiptContent = ReceiptBuilder.build("Balance Inquiry", receiptLines);

                TxData.clear();

                Intent intent = new Intent(_this.getActivity(), ReceiptView.class);
                intent.putExtra(Constants.RECEIPT, receiptContent);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.tx_calculate_fee_button:
                onCheckBalance(view);
                break;
        }
    }


    class CardHandler extends Handler implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener {
        WeakReference<Fragment> mFragment;

        CardHandler(Fragment fragment) {

            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            TxBalanceFragment_Copy fragment = (TxBalanceFragment_Copy) mFragment.get();
            if (fragment == null || !fragment.isAdded())
                return;
            if (fragment.mCardInfoDialog != null) {
                mCardInfoDialog.dismiss();
            }
            if (fragment.mProgressDialog != null) {
                fragment.mProgressDialog.dismiss();
            }
            //    CardInfoEntity cardInfoEntity = (CardInfoEntity) msg.obj;

            String card= (String) msg.obj;

            try{
                EditText cardField = ((EditText) getActivity().findViewById(R.id.tx_card_field));
                cardField.setText(card); //"4111111111111112"
            }catch(Exception e){
                mCardInfoDialog = DialogUtils.show(fragment.getActivity(),
                        "Exeption", e.getMessage(),
                        "OK", this, this);
            }



//        mCardInfoDialog = DialogUtils.show(fragment.getActivity(),
//                "Card Number", SDK_Result.obtainCardInfo(fragment.getActivity(), cardInfoEntity),
//                "OK", this, this);
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            CardFragment fragment = (CardFragment) mFragment.get();
            if (fragment != null && fragment.isAdded()) {
//            fragment.ifSearch = false;
                closeSearch();
            }
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            CardFragment fragment = (CardFragment) mFragment.get();
            if (fragment != null && fragment.isAdded()) {
//            fragment.ifSearch = false;
                closeSearch();
            }
        }
    }
}
