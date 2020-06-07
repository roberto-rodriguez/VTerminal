package com.voltcash.vterminal.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.voltcash.vterminal.views.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by roberto.rodriguez on 3/6/2020.
 */

public class PreferenceUtil {

    private static final String FILE_NAME = "vterminal_settings";

    public static final Map<String, String> PREFERENCES = new HashMap<>();

    private static String[] FIELDS = new String[]{
            Field.AUTH.TERMINAL_USERNAME,
            Field.AUTH.TERMINAL_PASSWORD ,
            Field.AUTH.TERMINAL_SERIAL_NUMBER,
            Field.AUTH.CLERK_FIRST_NAME,
            Field.AUTH.CLERK_LAST_NAME ,
            Field.AUTH.CLERK_ID        ,
            Field.AUTH.SESSION_TOKEN   ,
            Field.AUTH.MERCHANT_NAME
    };

    public static String loadFromFile(AppCompatActivity view){
        Context context = view.getApplicationContext();

        try {
            File file = new File(context.getFilesDir(), FILE_NAME);

            StringBuilder content = new StringBuilder();

            if (file.exists()) {

                FileInputStream fis = context.openFileInput(FILE_NAME);
                InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
                StringBuilder stringBuilder = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                    String line = reader.readLine();
                    while (line != null) {
                        content.append(line);
                        if (!line.isEmpty()) {
                            String[] parts = line.split("=");
                            if (parts.length == 2) {
                                PREFERENCES.put(parts[0], parts[1]);
                            }
                        }
                        line = reader.readLine();
                    }
                }
            }

        }catch (IOException e) {
           ViewUtil.showError(view, "Error loading preferences", e.getMessage());
        }

        return PREFERENCES.toString();
    }

    public static String read( String field){
        return PREFERENCES.get(field);
    }

    public static String getSerialNumber(Activity activity){
        String serialNumber = PreferenceUtil.read(Field.AUTH.TERMINAL_SERIAL_NUMBER);

        if(serialNumber == null){
            //If it gets here is because there was an error, need to restart the app
            Intent mainActivity = new Intent(activity.getApplicationContext(), MainActivity.class);
            activity.startActivity(mainActivity);
        }

        return serialNumber;
    }

    public static void write(Activity context, Map data, String... fields)  {
     try {
        for (String field : fields) {
            String value = data.get(field) + "";
            PREFERENCES.put(field, value);
        }


        File file = new File(context.getFilesDir(), FILE_NAME);

        if (!file.exists()) {
            file.createNewFile();
        }

        StringBuilder sb = new StringBuilder();

        for (String field : FIELDS) {
            sb.append(field).append("=").append(PREFERENCES.get(field)).append('\n');
        }

            try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
                fos.write(sb.toString().getBytes());
            }
        } catch (Exception e) {
         ViewUtil.showError(context, "Error writing preferences", e.getMessage());
        }
    }

}
