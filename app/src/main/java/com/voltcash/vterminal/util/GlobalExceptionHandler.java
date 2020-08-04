package com.voltcash.vterminal.util;

import android.app.Activity;
import android.content.Intent;
import com.voltcash.vterminal.views.MainActivity;

public class GlobalExceptionHandler {

    public static void catchException(final Activity activity, String functionality, Exception e){

        Intent mainActivity = new Intent(activity.getApplicationContext(), MainActivity.class);
        mainActivity.putExtra("isError", true);
        mainActivity.putExtra("functionality", functionality);
        mainActivity.putExtra("errorMessage", e.getMessage());
        activity.startActivity(mainActivity);
    }
}
