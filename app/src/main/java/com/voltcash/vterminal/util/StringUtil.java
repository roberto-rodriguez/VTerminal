package com.voltcash.vterminal.util;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * Created by roberto.rodriguez on 2/24/2020.
 */

public class StringUtil {

    public static boolean hasValue(String str){
        return str != null && !str.isEmpty();
    }

    public static String formatCurrency(String value) {
       try{
           return formatCurrency(Double.parseDouble(value));
       }catch(Exception e){
           return "";
       }
    }

    public static String formatCurrency(Double doubleVal) {
        if(doubleVal == null)return "";
        return new DecimalFormat("##,##,##0.00").format(doubleVal);
    }

    public static String formatRequestId(Map response) {
        String str = response.get("REQUEST_ID") + "";

        if(str != null){
            try{
                Double d = Double.parseDouble(str);
                return d.intValue() + "";
            }catch(Exception e){}
        }
        return "";
    }
}
