package com.voltcash.vterminal.views.lab.util;

import android.content.Context;
import android.support.annotation.StringRes;

import com.voltcash.vterminal.R;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.card.CardInfoEntity;

/**
 * Created by yyzz on 2018/5/19.
 */

public class SDK_Result {

    public static String obtainCardInfo(Context context, CardInfoEntity... cardInfoEntitys) {
        StringBuilder stringBuilder = new StringBuilder();
        for (CardInfoEntity entity : cardInfoEntitys) {
            stringBuilder.append(obtainCardInfo(context, entity)).append("\n");
        }
        return stringBuilder.toString();
    }

    public static String obtainCardInfo(Context context, CardInfoEntity cardInfoEntity) {
        if (cardInfoEntity == null)
            return null;
        StringBuilder sb = new StringBuilder();
        sb.append("Resultcode:\t" + cardInfoEntity.getResultcode() + "\n")
                .append(cardInfoEntity.getCardExistslot() == null ? "" : "Card type:\t" + cardInfoEntity.getCardExistslot().name() + "\n")
                .append(cardInfoEntity.getCardNo() == null ? "" : "Card no:\t" + cardInfoEntity.getCardNo() + "\n")
                .append(cardInfoEntity.getRfCardType() == 0 ? "" : "Rf card type:\t" + cardInfoEntity.getRfCardType() + "\n")
                .append(cardInfoEntity.getRFuid() == null ? "" : "RFUid:\t" + new String(cardInfoEntity.getRFuid()) + "\n")
                .append(cardInfoEntity.getAtr() == null ? "" : "Atr:\t" + cardInfoEntity.getAtr() + "\n")
                .append(cardInfoEntity.getTk1() == null ? "" : "Track1:\t" + cardInfoEntity.getTk1() + "\n")
                .append(cardInfoEntity.getTk2() == null ? "" : "Track2:\t" + cardInfoEntity.getTk2() + "\n")
                .append(cardInfoEntity.getTk3() == null ? "" : "Track3:\t" + cardInfoEntity.getTk3() + "\n")
                .append(cardInfoEntity.getExpiredDate() == null ? "" : "expiredDate:\t" + cardInfoEntity.getExpiredDate() + "\n")
                .append(cardInfoEntity.getServiceCode() == null ? "" : "serviceCode:\t" + cardInfoEntity.getServiceCode());
        return sb.toString();
    }

    public static String obtainMsg(Context context, int resCode) {
        String msg = null;

        msg = appendMsg(  resCode, "Some msg here");

        return msg;
    }

    public static String appendMsg(Context context, int code, @StringRes int id) {
        return appendMsg(code, context.getString(id));
    }

    public static String appendMsg(int code, String msg) {
        return appendMsg("Code", code + "", "Msg", msg);
    }

    public static String appendMsg(String... msg) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < msg.length; i += 2) {
            if (i == msg.length - 2) {
                sb.append(append(msg[i], msg[i + 1]));
                continue;
            }
            if (i == msg.length - 1) {
                sb.append(msg[i]);
                continue;
            }
            sb.append(append(msg[i], msg[i + 1]))
                    .append("\n");
        }
        return sb.toString();
    }

    public static String append(String title, String content) {
        return title + ":\t" + content;
    }
}
