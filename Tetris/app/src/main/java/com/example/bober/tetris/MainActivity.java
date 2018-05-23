package com.example.bober.tetris;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class MainActivity extends Activity
{
    private Game game;
    private final double game_speed = 0.3;
    private GameResult last_result;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        game = (Game)findViewById(R.id.gameview);
        final int mode = getIntent().getIntExtra("mode", 1);
        game.setMode(mode);

        Thread game_thread = new Thread(new Runnable() {
            boolean game_ended = false;

            @Override
            public void run() {
                Handler h = new Handler(Looper.getMainLooper());
                long ticks = 0;
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        //Log.i("ha", "invalidate!");
                        game.invalidate();
                        if (game_ended)
                            openMenuScreen();
                    }
                };
                long m;

                while (true) {
                    //Log.i("kup", "Thread next iteration start");
                    m = System.currentTimeMillis();
                    boolean ended_in_session = false;
                    for (int i = 0; i < ticks; i++)
                    {
                        //Log.i("kup", "Thread tick start");
                        last_result = game.game_logic();
                        if (last_result.finished)
                            ended_in_session = true;
                        //Log.i("kup", "Thread tick end");
                    }
                    game_ended = ended_in_session;

                    //Log.i("kup", "Thread sleep start");
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //Log.i("kup", "Thread sleep end");
                    h.post(runnable);
                    ticks = (int)((System.currentTimeMillis() - m)*game_speed);
                    //Log.i("kup", "Thread next iteration end");
                    if (game_ended)
                        break;
                }
            }
        });
        game_thread.start();

        findViewById(R.id.button_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.moveLeft();
            }
        });

        findViewById(R.id.button_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.moveRight();
            }
        });

        findViewById(R.id.button_rotate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.rotateRight();
            }
        });

        float button_size = getResources().getDimensionPixelSize(R.dimen.button_size);
        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams)findViewById(R.id.button_down).getLayoutParams();
        p.topMargin = (int)game.getButtonY(button_size);
        p.leftMargin = (int)game.getButtonX(button_size);
        findViewById(R.id.button_down).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.throwDown();
            }
        });
    }

    private void openMenuScreen()
    {
        Intent intent = new Intent();
        intent.putExtra("points", last_result.points);
        setResult(RESULT_OK, intent);
        finish();
    }

}
