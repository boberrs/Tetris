package com.example.bober.tetris;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {

    LoadSaveManager manager;
    private static int REQ_CODE = 100;
    Mode mode;

    private void openGame(int mode)
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("mode", mode);
        startActivityForResult(intent, REQ_CODE);
    }

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
        setContentView(R.layout.activity_menu);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        manager = new LoadSaveManager(this);

        findViewById(R.id.button_easy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGame(0);
                findViewById(R.id.score_table).setVisibility(View.INVISIBLE);
                mode = Mode.EASY;
            }
        });

        findViewById(R.id.button_normal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGame(1);
                findViewById(R.id.score_table).setVisibility(View.INVISIBLE);
                mode = Mode.NORMAL;
            }
        });

        findViewById(R.id.button_hard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGame(2);
                findViewById(R.id.score_table).setVisibility(View.INVISIBLE);
                mode = Mode.HARD;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null)
        {
            int highscore = manager.loadHighScore(Mode.parseInt(mode));
            int points = data.getIntExtra("points", 0);
            if (points > highscore) {
                manager.saveHighScore(points, Mode.parseInt(mode));
                highscore = points;
            }
            findViewById(R.id.score_table).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.mode_text)).setText("Mode: " + mode.toString());
            ((TextView)findViewById(R.id.score_text)).setText("Score: " + points);
            ((TextView)findViewById(R.id.highscore_text)).setText("Highscore: " + highscore);
        }
    }
}
