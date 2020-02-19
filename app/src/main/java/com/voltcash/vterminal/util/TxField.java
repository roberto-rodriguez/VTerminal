package com.voltcash.vterminal.util;

/**
 * Created by roberto.rodriguez on 2/17/2020.
 */

public enum TxField {
    TX_FIELD("txField"),
    CHECK_FRONT("checkFront"),
    CHECK_BACK("checkBack"),
    ID_FRONT("idFront"),
    ID_BACK("idBack");

    private String name;

    private TxField(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    };
}
