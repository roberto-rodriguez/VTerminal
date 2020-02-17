package com.voltcash.vterminal.util;

import com.kofax.kmc.ken.engines.data.Image;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by roberto.rodriguez on 2/17/2020.
 */

public class TxData{

//    public static Image CHECK_FRONT_IMAGEX = null;

    public static Map data = new HashMap<TxField, Object>();

    public static Image getImage(TxField fieldName){
        return (Image)data.get(fieldName);
    }

    public static void put(TxField fieldName, Object value) {
        data.put(fieldName, value);
    }

    public static void remove(TxField fieldName) {
        data.remove(fieldName);
    }

    public static boolean contains(TxField fieldName) {
        return data.containsKey(fieldName);
    }

}

