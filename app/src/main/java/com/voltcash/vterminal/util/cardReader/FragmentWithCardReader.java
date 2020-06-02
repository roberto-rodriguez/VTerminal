package com.voltcash.vterminal.util.cardReader;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import com.kofax.kmc.kut.utilities.AppContextProvider;
import com.voltcash.vterminal.R;
import com.voltcash.vterminal.VTerminal;
import com.voltcash.vterminal.util.TxData;
import com.voltcash.vterminal.views.lab.util.DialogUtils;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.card.CardInfoEntity;
import com.zcs.sdk.card.CardReaderManager;
import com.zcs.sdk.card.CardReaderTypeEnum;
import com.zcs.sdk.card.MagCard;
import com.zcs.sdk.listener.OnSearchCardListener;

/**
 * Created by roberto.rodriguez on 6/1/2020.
 */

public abstract class FragmentWithCardReader extends Fragment
        implements
        View.OnClickListener,
        OnSearchCardListener {

    protected Handler mHandler;
    protected CardReaderManager mCardReadManager;
    protected MagCard mMagCard;

    protected ProgressDialog progressDialog;

    protected TextView cardField;
    protected String cardNumber;

    protected Button submitButton;
    protected GridLayout calculateFeesLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     //   getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        mHandler = new CardReaderHandler(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(getLayoutId(), parent, false);
    }

    //Call this after the children execute
    @Override
    public void onViewCreated(View view,  Bundle savedInstanceState) {
//        setTitle("Balance Inquiry");
//        ((Button)findViewById(R.id.tx_calculate_fee_button)).setOnClickListener(this);

        AppContextProvider.setContext(getActivity().getApplicationContext());
        TxData.clear();

        cardField = (TextView)findViewById(R.id.tx_card_field);

        cardField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBankCard();
            }
        });

        submitButton = (Button)findViewById(R.id.tx_submit_button);

        if(submitButton != null){
            submitButton.setOnClickListener(this);
        }

        ((Button) findViewById(R.id.tx_calculate_fee_button)).setOnClickListener(this);

        calculateFeesLayout = (GridLayout)findViewById(R.id.tx_calculate_fees_layout);

        mCardReadManager = VTerminal.DRIVER_MANAGER.getCardReadManager();
        mMagCard = mCardReadManager.getMAGCard();

        searchBankCard();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.tx_calculate_fee_button:
                onCalculateFees(view);
                break;

            case R.id.tx_submit_button:
                onSubmit(view);
                break;
        }
    }

    protected abstract int getLayoutId();

    protected abstract void onCalculateFees(View view);

    protected void onSubmit(View view){};

    void searchBankCard() {
        progressDialog = (ProgressDialog) DialogUtils.showProgress(this.getActivity(), "Waiting", "Please swipe card", new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mCardReadManager.cancelSearchCard();
            }
        });

        mCardReadManager.cancelSearchCard();
        mCardReadManager.searchCard(CardReaderTypeEnum.MAG_CARD, 60 * 1000, this);
    }

    @Override
    public void onCardInfo(CardInfoEntity cardInfoEntity) {
        CardInfoEntity cardInfo = mMagCard.getMagReadData();

        String card = "FAILED";

        if (cardInfo.getResultcode() == SdkResult.SDK_OK) {
            card = cardInfo.getCardNo();
        }

        sendMsg(cardInfo.getResultcode(), card);

        mMagCard.magCardClose();
        progressDialog.dismiss();
    }

    @Override
    public void onError(int i) {
        sendMsg(0, "Error");
    }

    @Override
    public void onNoCard(CardReaderTypeEnum cardReaderTypeEnum, boolean b) {
        String msg = "No Card";
       // sendMsg(0, "No Card");
    }

    private void sendMsg(int resultCode, String text){
        Message msg = Message.obtain();
        msg.what = 2001;
        msg.arg1 = resultCode;
        msg.obj = text;// cardInfo;
        mHandler.sendMessage(msg);
    }

    void closeSearch() {
        mCardReadManager.cancelSearchCard();
    }

    protected final <T extends View> T findViewById(int id) {
        return getActivity().findViewById(id);
    }

    protected Context getApplicationContext() {return getActivity().getApplicationContext();}

    protected void setTitle(CharSequence title) {getActivity().setTitle(title);}

    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public TextView getCardField() {
        return cardField;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;

        if(cardNumber != null && cardNumber.length() > 4){
            cardField.setText("**** **** **** " + cardNumber.substring(cardNumber.length() - 4));
        }
    }

    public String getCardNumber() {
        return cardNumber;
    }
}
