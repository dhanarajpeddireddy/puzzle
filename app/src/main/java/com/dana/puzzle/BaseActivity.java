package com.dana.puzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.dana.puzzle.tool.PreferenceUtills;
import com.google.android.gms.ads.AdView;

public class BaseActivity extends AppCompatActivity {
public Ads inappAds=new Ads();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void showBannerAd()
    {
        AdView adView=findViewById(R.id.adView_banner);
        inappAds.googleBannerAd(adView);
    }

    @Override
    protected void onDestroy() {
        stopService();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        startService();
        super.onStart();
    }

    @Override
    protected void onPause() {
        stopService();
        super.onPause();
    }


    public void startService() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (PreferenceUtills.getInstance(getApplicationContext()).getBoolean(Constants.music))
                    startService(new Intent(getBaseContext(), MediaPlayerService.class));
            }
        },1000);

    }


    public void stopService() {
        stopService(new Intent(getBaseContext(), MediaPlayerService.class));
    }
}