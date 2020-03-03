package com.voltcash.vterminal.util;

import com.kofax.kmc.ken.engines.data.Image;
import com.kofax.kmc.kui.uicontrols.BarCodeFoundEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by roberto.rodriguez on 2/17/2020.
 */

public class TxData{

//    public static Image CHECK_FRONT_IMAGEX = null;

    public static Map data = new HashMap<TxField, Object>();

    public static BarCodeFoundEvent BARCODE_EVENT = null;

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

    public static void clear() { data.clear();}

    public static void take(Map other, TxField... fields){
        for (TxField field : fields) {
            put(field, other.get(field));
        }
    }
    public static String getString(TxField fieldName){
        Object obj = data.get(fieldName);

        if(obj != null){
           return obj.toString();
        }
        return "";
    }

    //TODO fotmat amount
    public static String getAmount(TxField fieldName){
        Object obj = data.get(fieldName);

        if(obj != null){
            return "$ " + obj + ".00";
        }
        return "$ 0.00";
    }

    public static Double getDouble(TxField fieldName){
        Object obj = data.get(fieldName);
        Double d = 0.0;
        try{
            if(obj != null && obj instanceof Double){
                d = (Double)obj;
            }else{
                d = Double.parseDouble(obj + "");
                data.put(fieldName, d);
            }
        }catch(Exception e){}
        return d;
    }

    public static Boolean getBoolean(TxField fieldName){
        Object obj = data.get(fieldName);

        Boolean bool = false;

        if(obj != null){
            if(obj instanceof String){
                bool = Boolean.parseBoolean(obj.toString());
                data.put(fieldName, bool);
            }else{
                if(obj instanceof Boolean){
                    bool = (Boolean)obj;
                }
            }
        }
        return bool;
    }

}

