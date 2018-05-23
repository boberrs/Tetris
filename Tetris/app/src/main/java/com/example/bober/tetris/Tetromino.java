package com.example.bober.tetris;

/**
 * Created by Bober on 5/1/2017.
 */

public class Tetromino
{
    enum Type
    {
        I, J, L, O, S, T, Z;
        static Type parseInt(int x)
        {
            switch (x)
            {
                case 1: return I;
                case 2: return J;
                case 3: return L;
                case 4: return O;
                case 5: return S;
                case 6: return T;
                case 7: return Z;
                default: return null;
            }
        }
    };
    static int container_x = 10;

    public boolean[][] map;
    public int size_x;
    public int size_y;

    public int position_x; //position of top-left corner in container
    public int position_y;
    public int orientation; //1, 2, 3, 4 - top, right, down, left

    public Tetromino(Tetromino t)
    {
        this.size_x = t.size_x;
        this.size_y = t.size_y;
        this.position_x = t.position_x;
        this.position_y = t.position_y;
        this.orientation = t.orientation;
        this.map = new boolean[size_x][size_y];
        for (int i = 0; i < size_x; i++)
            for (int j = 0; j < size_y; j++)
                this.map[i][j] = t.map[i][j];
    }

    public Tetromino(Type type)
    {
        switch (type)
        {
            case I:
                map = new boolean[][]{{true, true, true, true}};
                size_x = 1;
                size_y = 4;
                break;
            case J:
                map = new boolean[][]{{false, false, true}, {true, true, true}};
                size_x = 2;
                size_y = 3;
                break;
            case L:
                map = new boolean[][]{{true, true, true}, {false, false, true}};
                size_x = 2;
                size_y = 3;
                break;
            case O:
                map = new boolean[][]{{true, true}, {true, true}};
                size_x = 2;
                size_y = 2;
                break;
            case S:
                map = new boolean[][]{{false, true}, {true, true}, {true, false}};
                size_x = 3;
                size_y = 2;
                break;
            case T:
                map = new boolean[][]{{true, false}, {true, true}, {true, false}};
                size_x = 3;
                size_y = 2;
                break;
            case Z:
                map = new boolean[][]{{true, false}, {true, true}, {false, true}};
                size_x = 3;
                size_y = 2;
                break;
        }
        orientation = 1;
        position_x = container_x/2 - size_x/2;
        position_y = 0;
    }

    public void rotate_right()
    {
        boolean[][] new_map = new boolean[size_y][size_x];
        for (int i = 0; i < size_y; i++)
            for (int j = 0; j < size_x; j++)
                new_map[i][j] = map[j][size_y - 1 - i];
        map = new_map;
        int temp = size_x;
        size_x = size_y;
        size_y = temp;
        orientation = orientation % 4 + 1;
    }

    public void rotate_left()
    {
        boolean[][] new_map = new boolean[size_y][size_x];
        for (int i = 0; i < size_y; i++)
            for (int j = 0; j < size_x; j++)
                new_map[i][j] = map[size_x - 1 - j][i];
        map = new_map;
        int temp = size_x;
        size_x = size_y;
        size_y = temp;
        orientation = orientation % 4 + 1;
    }
}
