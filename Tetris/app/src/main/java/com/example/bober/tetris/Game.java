package com.example.bober.tetris;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.Random;

/**
 * Created by Bober on 4/28/2017.
 */

public class Game extends View {
    Context context;
    Paint paint;
    Random random;

    int screen_x;
    int screen_y;
    int game_x = 640; //800
    int game_y = 1136;//600
    double scale_x;
    double scale_y;
    Matrix scale_matrix;

    boolean game_started = false;
    int level = 1;
    int points;
    int mode;

    int container_x = 10;
    int container_y = 20;
    Block[][] container = new Block[container_x][container_y];

    Tetromino current;
    Tetromino next;

    int clock_tick;
    double clock_time;

    public Game(Context context, AttributeSet set) {
        super(context, set);
        this.context = context;
        random = new Random();
        paint = new Paint();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            ((Activity) context).getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        }
        else
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int status_bar_height = 0;
        //int res = getResources().getIdentifier("status_bar_height", "dimen", "android");
        //if (res > 0)
        //    status_bar_height = getResources().getDimensionPixelOffset(res);

        screen_x = displayMetrics.widthPixels;
        screen_y = displayMetrics.heightPixels - status_bar_height;
        scale_x = (double) screen_x / game_x;
        scale_y = (double) screen_y / game_y;
        //buffer_bitmap = Bitmap.createBitmap(game_x, game_y, Bitmap.Config.ARGB_8888);
        //buffer = new Canvas(buffer_bitmap);
        scale_matrix = new Matrix();
        if (scale_x > scale_y) {
            scale_matrix.setScale((float) scale_y, (float) scale_y);
            scale_matrix.postTranslate((float) ((screen_x - game_x * scale_y) / 2), 0);
        } else {
            scale_matrix.setScale((float) scale_x, (float) scale_x);
            scale_matrix.postTranslate(0, (float) ((screen_y - game_y * scale_x) / 2));
        }

        restart_game();
    }

    public float getButtonX(float buttonSize)
    {
        float brick_size = 800/(container_y+2);
        float container_left = brick_size;
        float result = (game_x + (container_left + brick_size*(container_x+2)))/2;
        return (scale_x > scale_y) ? (float)scale_y*result - buttonSize/2 + (float) (screen_x - game_x * scale_y) / 2
                : (float)scale_x*result - buttonSize/2;

    }

    public float getButtonY(float buttonSize)
    {
        float brick_size = 800/(container_y + 2);
        float result = brick_size*(4 + container_y);
        return (scale_x > scale_y) ? (float)scale_y*result - buttonSize :
                (float)scale_x*result +(float) ((screen_y - game_y * scale_x) / 2) - buttonSize;
    }

    public void setMode(int mode)
    {
        this.mode = mode;
        switch (mode)
        {
            case 0: clock_time = 180; break;
            case 1: clock_time = 120; break;
            case 2: clock_time = 60; break;
        }
    }

    private void restart_game()
    {
        switch (mode)
        {
            case 0: clock_time = 180; break;
            case 1: clock_time = 120; break;
            case 2: clock_time = 60; break;
        }
        clock_tick = (int)clock_time;
        current = new Tetromino(Tetromino.Type.parseInt(random.nextInt(7) + 1));
        next = new Tetromino(Tetromino.Type.parseInt(random.nextInt(7) + 1));
        game_started = false;
    }

    //returns true if succeeded
    private boolean moveDown()
    {
        //Log.i("lol", "moveDown started");
        if (current.position_y + current.size_y >= container_y)
            return false;
        for (int i = 0; i < current.size_x; i++)
            for (int j = 0; j < current.size_y; j++)
                if (current.map[i][j] && container[current.position_x + i][current.position_y + j + 1] != null)
                    return false;

        current.position_y++;
        //Log.i("lol", "moveDown ended");
        return true;
    }

    //returns true if succeeded
    public boolean moveLeft()
    {
        //Log.i("lol", "moveLeft started");
        if (current.position_x == 0)
            return false;
        for (int i = 0; i < current.size_x; i++)
            for (int j = 0; j < current.size_y; j++)
                if (current.map[i][j] && container[current.position_x + i - 1][current.position_y + j] != null)
                    return false;

        current.position_x--;
        //Log.i("lol", "moveLeft ended");
        return true;
    }

    //returns true if succeeded
    public boolean moveRight()
    {
        //Log.i("lol", "moveRight started");
        if (current.position_x + current.size_x >= container_x)
            return false;
        for (int i = 0; i < current.size_x; i++)
            for (int j = 0; j < current.size_y; j++)
                if (current.map[i][j] && container[current.position_x + i + 1][current.position_y + j] != null)
                    return false;

        current.position_x++;
        //Log.i("lol", "moveRight ended");
        return true;
    }

    public void throwDown()
    {
        while(moveDown());
    }

    public boolean rotateRight()
    {
        //Log.i("lol", "Rotate started");
        //Tetromino copy = new Tetromino(current);
        current.rotate_right();

        if (current.position_y + current.size_y >= container_y)
        {
            current.rotate_left();
            return false;
        }

        int shift_left = 0;
        if (current.position_x + current.size_x >= container_x)
            shift_left = current.position_x + current.size_x - container_x;
        current.position_x -= shift_left;

        for (int i = 0; i < current.size_x; i++)
            for (int j = 0; j < current.size_y; j++)
                if (container[current.position_x + i][current.position_y + j] != null && current.map[i][j])
                {
                    current.rotate_left();
                    current.position_x += shift_left;
                    return false;
                }
        //Log.i("lol", "Rotate ended");
        return true;
    }

    public void settle()
    {
        //Log.i("lol", "Settle started");
        int old_points = points;
        points+=40;

        for (int i = 0; i < current.size_x; i++)
            for (int j = 0; j < current.size_y; j++)
                if (current.map[i][j])
                    container[current.position_x + i][current.position_y + j] = new Block(Color.rgb(220, 200, 10));

        int num_rows = 0;
        for (int i = current.position_y; i < container_y; i++)
        {
            //Log.i("lol", "row " + i + " started");
            boolean is_full = true;
            for (int j = 0; j < container_x; j++)
                if (container[j][i] == null)
                {
                    is_full = false;
                    break;
                }

            if (is_full)
            {
                num_rows++;
                points+=100*num_rows;

                for (int j = 0; j < container_x; j++)
                    container[j][i] = null;

                for (int j = 0; j < container_x; j++)
                {
                    int shift_down = 1;
                    while (i + shift_down < container_y && container[j][i + shift_down] == null)
                        shift_down++;
                    for (int k = i - 1; k >= 0; k--)
                        container[j][k+shift_down] = container[j][k];
                }

                //Log.i("lol", "row " + i + " ended");
                i--;
            }
            //Log.i("lol", "row " + i + " ended");
        }

        switch(mode)
        {
            case 0: clock_time = 20+4960.0/(points/1000 + 31); break;
            case 1: clock_time = 20+1900.0/(points/1000 + 19); break;
            case 2: clock_time = 20+280.0/(points/1000 + 7); break;
        }
        //if (clock_time > 20)
        //    clock_time -= 5*(points/1000 - old_points/1000);

        //Log.i("lol", "Settle ended");

    }

    private boolean isCurrentOK()
    {
        if (current.position_x < 0 || current.position_y < 0 || current.position_x + current.size_x >= container_x
                || current.position_y + current.size_y >= container_y)
                return false;
        for (int i = 0; i < current.size_x; i++)
            for (int j = 0; j < current.size_y; j++)
                if (current.map[i][j] && container[current.position_x + i][current.position_y+j] != null)
                    return false;
        return true;
    }

    //returns false if game ended
    public GameResult game_logic()
    {
        clock_tick--;
        if (clock_tick == 0)
        {
            clock_tick = (int)clock_time;
            //Log.i("lol", "iteration start");

            if (!moveDown())
            {
                settle();
                current = next;
                next = new Tetromino(Tetromino.Type.parseInt(random.nextInt(7) + 1));
                if (!isCurrentOK())
                    return new GameResult(points, true);
            }
            //Log.i("lol", "iteration end");
        }
        return new GameResult(points, false);
    }


    public void allow_start_game()
    {
        if (!game_started) {
            game_started = true;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Log.i("lol", "draw started");

        paint.setColor(Color.rgb(0, 0, 0));
        canvas.drawRect(0, 0, screen_x, screen_y, paint);

        canvas.concat(scale_matrix);

        paint.setColor(Color.rgb(40, 40, 40));
        canvas.drawRect(0, 0, game_x, game_y, paint);

        //container
        float brick_size = 800/(container_y+2);
        //float container_left = game_x/2 - brick_size*((float)container_x/2 + 1);
        float container_left = brick_size;
        float container_top = 3*brick_size;
        paint.setColor(Color.rgb(60, 60, 60));
        canvas.drawRect(container_left, container_top,
                container_left + brick_size, container_top + brick_size*(container_y+1), paint);
        canvas.drawRect(container_left, container_top + brick_size*(container_y),
                container_left + brick_size*(container_x + 2), container_top + brick_size*(container_y+1), paint);
        canvas.drawRect(container_left + brick_size*(container_x + 1), container_top,
                container_left + brick_size*(container_x+2), container_top + brick_size*(container_y+1), paint);

        //blocks
        for (int i = 0; i < container_x; i++)
            for (int j = 0; j < container_y; j++)
                if (container[i][j] != null)
                {
                    int c = container[i][j].color;
                    paint.setColor(Color.rgb((int)(Color.red(c)*0.4), (int)(Color.green(c)*0.4), (int)(Color.blue(c)*0.4)));
                    canvas.drawRect(container_left + brick_size*(1+i), container_top + brick_size*j,
                            container_left+brick_size*(i+2), container_top + brick_size*(j+1), paint);
                    paint.setColor(c);
                    canvas.drawRect(container_left + brick_size*(1+i) + 2, container_top + brick_size*j + 2,
                            container_left+brick_size*(i+2) - 2, container_top + brick_size*(j+1) - 2, paint);
                }

        //current
        for (int i = 0; i < current.size_x; i++)
            for (int j = 0; j < current.size_y; j++)
                if (current.map[i][j])
                {
                    int c = Color.rgb(220, 200, 100);
                    paint.setColor(Color.rgb((int)(Color.red(c)*0.4), (int)(Color.green(c)*0.4), (int)(Color.blue(c)*0.4)));
                    canvas.drawRect(container_left + brick_size*(current.position_x + i + 1),
                            container_top + brick_size*(current.position_y + j),
                            container_left+brick_size*(current.position_x + i+2),
                            container_top + brick_size*(current.position_y + j+1), paint);
                    paint.setColor(c);
                    canvas.drawRect(container_left + brick_size*(current.position_x + i + 1) + 2,
                            container_top + brick_size*(current.position_y + j) + 2,
                            container_left+brick_size*(current.position_x + i+2) - 2,
                            container_top + brick_size*(current.position_y + j+1) - 2, paint);
                }

        //score
        paint.setColor(Color.rgb(240, 240, 240));
        paint.setTextSize(50);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setFakeBoldText(true);
        canvas.drawText(String.valueOf(points), game_x/2, 2*brick_size, paint);

        //next
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(40);
        canvas.drawText("Next", (game_x + (container_left + brick_size*(container_x+2)))/2, container_top + 30, paint);
        float element_left = (game_x + (container_left + brick_size*(container_x+2)))/2 - brick_size*next.size_x/2;
        for (int i = 0; i < next.size_x; i++)
            for (int j = 0; j < next.size_y; j++)
                if (next.map[i][j])
                {
                    int c = Color.rgb(220, 200, 100);
                    paint.setColor(Color.rgb((int)(Color.red(c)*0.4), (int)(Color.green(c)*0.4), (int)(Color.blue(c)*0.4)));
                    canvas.drawRect(element_left + brick_size*i,
                            container_top + brick_size*(j)+60,
                            element_left + brick_size*(i+ 1),
                            container_top + brick_size*(j+1) + 60, paint);
                    paint.setColor(c);
                    canvas.drawRect(element_left + brick_size*i + 2,
                            container_top + brick_size*(j)+60 + 2,
                            element_left + brick_size*(i+ 1) - 2,
                            container_top + brick_size*(j+1) + 60 - 2, paint);
                }

        //Log.i("lol", "draw ended");
    }
}
