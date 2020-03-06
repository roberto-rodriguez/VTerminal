package com.voltcash.vterminal.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Map;

/**
 * Created by roberto.rodriguez on 3/6/2020.
 */

public class PreferenceUtil {

    public static void write(Context ctx, Map data, String... fields){
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();

            for (String field : fields) {
                String value = data.get(field) + "";
                editor.putString(field, value);
            }
            editor.commit();
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    public static String read(Context ctx, String field){
        return PreferenceManager.getDefaultSharedPreferences(ctx).getString(field, null);
    }
}
