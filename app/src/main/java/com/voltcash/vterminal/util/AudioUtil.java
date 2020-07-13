package com.voltcash.vterminal.util;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.voltcash.vterminal.R;

public class AudioUtil {

    public static void playBellSound(Activity ctx){
        playSound(ctx, "raw/bell.mp3");
    }

    private static void playSound(Activity ctx, String audio){

        try {
            MediaPlayer mediaPlayer = MediaPlayer.create(ctx, R.raw.bell);

          //  mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            String m = e.getMessage();

            e.printStackTrace();
        }
    }
}
