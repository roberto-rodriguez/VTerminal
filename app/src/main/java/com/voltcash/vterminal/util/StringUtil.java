package com.voltcash.vterminal.util;

import java.text.DecimalFormat;

/**
 * Created by roberto.rodriguez on 2/24/2020.
 */

public class StringUtil {

    public static boolean hasValue(String str){
        return str != null && !str.isEmpty();
    }

    public static String formatCurrency(Double doubleVal) {
        if(doubleVal == null)return "";
        return new DecimalFormat("##,##,##0.00").format(doubleVal);
    }
}
