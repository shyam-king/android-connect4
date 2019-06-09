package com.github.shyamking.connectfour;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class GameContainer extends View {

    private String TAG = "SHYAMDEBUG";
    private Paint paint;
    private Paint textPaint;
    private boolean init = false;
    private int width, height;
    private OnTouchListener touchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int[] vPos = new int[2];
                v.getLocationOnScreen(vPos);
                float x = event.getRawX() - vPos[0];
                float y = event.getRawY() - vPos[1];
                Log.d(TAG, "Column: " + (int)(x / cellW));
                if(!gameOver)
                if (putBall((int)(x/cellW), val[turn])) {
                    invalidate();
                    turn = 1 - turn;
                }
                return true;
            }
            return false;
        }
    };

    private int[][] matrix;
    private int cellW, cellH;
    private RectF rect = new RectF();
    private int[] val = {1, 5};
    public int turn = 0;
    private int nCols = 7;
    private int nRows = 6;
    private int lastX = -1, lastY = -1;
    private boolean gameOver = false;

    private ResultListener resultListener;

    public GameContainer(Context context) {
        super(context);
    }

    public GameContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameContainer(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!init) {
            init = true;
            paint = new Paint();
            textPaint = new Paint(Paint.UNDERLINE_TEXT_FLAG);
            setOnTouchListener(touchListener);
            matrix = new int[nCols][nRows];
            for (int i = 0; i < nCols; i++)
                for(int j = 0; j < nRows; j++)
                    matrix[i][j] = 0;
            cellH = height / nRows;
            cellW = width / nCols;
            turn = 0;
            lastX = lastY = -1;
            gameOver = false;
        }

        canvas.drawColor(Color.rgb(100, 100, 100));
        paint.setColor(Color.rgb(0,0,0));
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);

        for (int i=1; i<nCols; i++) {
            canvas.drawLine(i * cellW, 0, i * cellW, height, paint);
        }

        for (int i = 1; i < nRows; i++) {
            canvas.drawLine(0, i *cellH, width, i*cellH, paint);
        }

        for (int i = 0; i < nCols; i++)
            for (int  j = 0; j < nRows; j++) {
                if (matrix[i][j] != 0) {
                    if(matrix[i][j] == val[0])
                        paint.setColor(Color.rgb(0,0,100));
                    else
                        paint.setColor(Color.rgb(0, 100, 0));
                    paint.setStyle(Paint.Style.FILL_AND_STROKE);

                    int pad = 10;

                    rect.left = i * cellW + pad;
                    rect.right = (i+1) * cellW - pad;
                    rect.top = j * cellH + pad;
                    rect.bottom = (j+1) * cellH - pad;
                    canvas.drawArc(rect, 0, 360, true, paint);
                }
            }

        rect.top = rect.left = 0;
        rect.bottom = height;
        rect.right = width;

        paint.setStyle(Paint.Style.STROKE);
        if(turn == 0)
            paint.setColor(Color.rgb(0,0,255));
        else
            paint.setColor(Color.rgb(0,255,0));

        if(gameOver)
            paint.setColor(Color.rgb(0,0,0));

        canvas.drawRect(rect, paint);
    }

    private boolean putBall(int c, int v) {
        for (int i = nRows -1; i >= 0; i--) {
            if (matrix[c][i] == 0) {
                matrix[c][i] = v;

                if(checkWin(c, i, v)) {
                    if (v == val[0])
                        resultListener.onResult("Blue");
                    else
                        resultListener.onResult("Green");

                    gameOver = true;
                }
                lastX = c;
                lastY = i;
                return true ;
            }
        }
        return false;
    }

    private boolean checkWin(int x, int y, int v) {
        if (countBalls(x,y,1,0, v) + countBalls(x-1,y,-1,0, v) >= 4
        || countBalls(x,y,0,1,v) + countBalls(x,y-1,0,-1, v) >= 4
        || countBalls(x,y,1,1,v) + countBalls(x-1, y-1, -1, -1,v) >= 4
        || countBalls(x,y,1,-1,v) + countBalls(x-1, y+1, -1, 1, v) >= 4) {
            return true;
        }
        return false;
    }

    private int countBalls(int x, int y, int speedX, int speedY, int v) {
        int count = 0;
        for (; x < nCols && x >= 0 && y < nRows && y >= 0 && matrix[x][y] == v; x+= speedX, y += speedY, count ++);
        return count;
    }

    public void undo() {
        if (lastX >= 0) {
            matrix[lastX][lastY] = 0;
            lastX = lastY = -1;
            turn = 1 - turn;
            invalidate();
        }
    }

    public void reset() {
        init = false;
        invalidate();
    }

    public void setResultListener(ResultListener resultListener) {
        this.resultListener = resultListener;
    }
}
