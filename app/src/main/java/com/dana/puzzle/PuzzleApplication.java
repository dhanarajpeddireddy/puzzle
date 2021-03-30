package com.dana.puzzle;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.util.Arrays;
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


      /*  if (BuildConfig.DEBUG)
        {
            List<String> testDeviceIds = Arrays.asList("610A68859DA35B7A2A173EB7BE8BACEC");
            RequestConfiguration configuration =
                    new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
            MobileAds.setRequestConfiguration(configuration);
        }*/

    }


}
