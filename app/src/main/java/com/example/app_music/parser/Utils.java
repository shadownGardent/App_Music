package com.example.app_music.parser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.example.app_music.R;

public class Utils {
    public static final String EXTRA_SONG_INDEX = "EXTRA_SONG_INDEX";

    @SuppressLint("UseCompatLoadingForDrawables")
    public static int getDrawableId(Context context, String drawableName) {
        @SuppressLint("DiscouragedApi")
        int id = context.getResources().getIdentifier(
                drawableName.split("[.]")[0],
                "drawable", context.getPackageName()
        );
        if (id != 0) {
            return id;
        } else {
            return R.drawable.da_da_da;
        }
    }

}
