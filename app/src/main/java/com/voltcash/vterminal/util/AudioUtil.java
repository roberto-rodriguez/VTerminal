package com.voltcash.vterminal.util;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;

public class AudioUtil {

    public static void playBellSound(Activity ctx){
        playSound(ctx, "raw/bell.mp3");
    }

    private static void playSound(Activity ctx, String audio){

        try {
            MediaPlayer mediaPlayer = new MediaPlayer();

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            AssetFileDescriptor afd = ctx.getAssets().openFd(audio);
            mediaPlayer.setDataSource(afd.getFileDescriptor());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
