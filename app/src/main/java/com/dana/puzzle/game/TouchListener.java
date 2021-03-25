package com.dana.puzzle.game;

import android.annotation.SuppressLint;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.StrictMath.abs;

public class TouchListener implements View.OnTouchListener {

    interface  IlistnerBack
    {
        void pieceMatched();
    }

    private final int  screenWidth;
    private final int screenHeight;
    private float lastX = 0;
    private float lastY = 0;


    IlistnerBack ilistnerBack;

    public TouchListener(PuzzleActivity activity,IlistnerBack ilistnerBack)
    {
         this.ilistnerBack=ilistnerBack;
         DisplayMetrics displayMetrics = new DisplayMetrics();
         activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
         screenHeight = displayMetrics.heightPixels;
         screenWidth = displayMetrics.widthPixels;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent event) {

        float x = event.getRawX();
        float y = event.getRawY();

        final double illuseSize = sqrt(pow(view.getWidth(), 2) + pow(view.getHeight(), 2)) / 10;

        PuzzlePiece piece = (PuzzlePiece) view;
        if (!piece.canMove) {
            return true;
        }



        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                lastX = x - lParams.leftMargin;
                lastY = y - lParams.topMargin;
                piece.bringToFront();
                break;
            }
            case MotionEvent.ACTION_MOVE: {

                lParams.leftMargin = (int) (x - lastX);
                lParams.topMargin = (int) (y - lastY);
                lParams.rightMargin=-250;
                lParams.bottomMargin=-250;
                view.setLayoutParams(lParams);

                break;
            }
            case MotionEvent.ACTION_UP:
                int xDiff = abs(piece.xCoord - view.getLeft());
                int yDiff = abs(piece.yCoord - view.getTop());
                if (xDiff <= illuseSize && yDiff <= illuseSize) {
                    lParams.leftMargin = piece.xCoord;
                    lParams.topMargin = piece.yCoord;
                    piece.setLayoutParams(lParams);
                    piece.canMove = false;

                    if (ilistnerBack!=null)
                        ilistnerBack.pieceMatched();
                }
                break;
        }
        return true;
    }

}



