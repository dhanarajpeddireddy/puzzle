package com.dana.puzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.ads.AdView;

public class SelectionPeicesActivity extends AppCompatActivity {

    Ads inappAds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_peices);

        init();
    }

    private void init() {
        inappAds=new Ads();
    }


    @Override
    protected void onResume() {
        AdView adView=findViewById(R.id.adView_banner);
        inappAds.googleBannerAd(adView);
        super.onResume();
    }
}