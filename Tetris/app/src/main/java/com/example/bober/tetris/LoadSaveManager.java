package com.example.bober.tetris;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Bober on 5/1/2017.
 */

public class LoadSaveManager
{
    private Context context;

    public LoadSaveManager(Context context) {
        this.context = context;
    }

    public void saveHighScore(int highscore, int mode)
    {
        SharedPreferences pref = context.getSharedPreferences("save", Context.MODE_PRIVATE);
        pref.edit().putInt("highscore" + mode, highscore).apply();
    }

    public int loadHighScore(int mode)
    {
        SharedPreferences pref = context.getSharedPreferences("save", Context.MODE_PRIVATE);
        return pref.getInt("highscore" + mode, 0);
    }
}
