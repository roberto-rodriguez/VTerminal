package com.voltcash.vterminal.util;

/**
 * Created by roberto.rodriguez on 2/17/2020.
 */

public enum TxField {
    TX_FIELD("txField"),
    CHECK_FRONT("checkFront"),
    CHECK_BACK("checkBack"),
    ID_FRONT("idFront"),
    ID_BACK("idBack"),
    AMOUNT("amount"),
    CARD_NUMBER("cardNumber"),
    OPERATION("operation"),
    DL_DATA_SCAN("dlDataScan"),
    SSN("ssn"),
    PHONE("phone"),

    //------- response ------
    CARD_LOAD_FEE("CRDLDF"),
    ACTIVATION_FEE("ACTIVATION_FEE"),
    CARD_EXIST("CARD_EXIST");

    private String name;

    private TxField(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    };
}
