package com.dana.puzzle;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.databinding.library.baseAdapters.BuildConfig;

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
            List<String> testDeviceIds
                    = new ArrayList<>();
            testDeviceIds.add("4F143BBED9284D317B7B2F766D42A231");
            RequestConfiguration configuration =
                    new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
            MobileAds.setRequestConfiguration(configuration);
        }

    }


}
