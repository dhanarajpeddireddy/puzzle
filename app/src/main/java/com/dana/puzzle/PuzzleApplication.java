package com.dana.puzzle;

import android.app.Application;
import android.content.Context;


public class PuzzleApplication extends Application  {

    private static PuzzleApplication instance;


    public static Context getContext() {
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();

        instance=this;


    }



}
