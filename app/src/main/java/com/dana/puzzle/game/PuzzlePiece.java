package com.dana.puzzle.game;

import android.content.Context;

public class PuzzlePiece extends androidx.appcompat.widget.AppCompatImageView {
    public int xCoord;
    public int yCoord;
    public int pieceWidth;
    public int pieceHeight;
    public boolean canMove = true;
    private int who;

    public int getWho() {
        return who;
    }

    public void setWho(int who) {
        this.who = who;
    }

    public PuzzlePiece(Context context) {
        super(context);
    }
}