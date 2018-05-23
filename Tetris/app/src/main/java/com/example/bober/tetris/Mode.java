package com.example.bober.tetris;

/**
 * Created by Bober on 5/2/2017.
 */

public enum Mode
{
    EASY, NORMAL, HARD;
    static int parseInt(Mode m)
    {
        switch(m)
        {
            case EASY: return 0;
            case NORMAL: return 1;
            case HARD: return 2;
            default: return -1;
        }
    }
}
