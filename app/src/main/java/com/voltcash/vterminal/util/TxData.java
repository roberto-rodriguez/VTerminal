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

    public static Map data = new HashMap<String, Object>();

    public static BarCodeFoundEvent BARCODE_EVENT = null;

    public static Image getImage(String fieldName){
        return (Image)data.get(fieldName);
    }

    public static void put(String fieldName, Object value) {
        data.put(fieldName, value);
    }

    public static void remove(String fieldName) {
        data.remove(fieldName);
    }

    public static boolean contains(String fieldName) {
        return data.containsKey(fieldName);
    }

    public static void clear() { data.clear();}

    public static void take(Map other, String... fields){
        for (String field : fields) {
            put(field, other.get(field));
        }
    }
    public static String getString(String fieldName){
        Object obj = data.get(fieldName);

        if(obj != null){
           return obj.toString();
        }
        return "";
    }

    //TODO fotmat amount
    public static String getAmount(String fieldName){
        Object obj = data.get(fieldName);

        if(obj != null){
            return "$ " + obj + ".00";
        }
        return "$ 0.00";
    }

    public static Double getDouble(String fieldName){
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

    public static Boolean getBoolean(String fieldName){
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

