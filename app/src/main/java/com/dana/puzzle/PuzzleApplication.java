package com.dana.puzzle;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class PuzzleApplication extends Application  {

    private static PuzzleApplication instance;


    public static Context getContext() {
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();

        instance=this;

        if (BuildConfig.DEBUG)
        {
            Log.e("DEBUG","in");
            List<String> testDeviceIds
                    = new ArrayList<>();
            testDeviceIds.add("73E806C0999E082CB23FBB8568F1A6D3");

            RequestConfiguration configuration =
                    new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();

            MobileAds.setRequestConfiguration(configuration);
        }

    }


}
