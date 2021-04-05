package com.dana.puzzle;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.dana.puzzle.databinding.ActivityHomeBinding;
import com.dana.puzzle.game.MultiPuzzleActivity;
import com.google.android.gms.ads.AdView;


public class HomeActivity extends AppCompatActivity implements OnClickListner {

    Ads inappAds;

    ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding= DataBindingUtil.setContentView(this,R.layout.activity_home);
      binding.setOnclick(this);
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


    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.cv_multi)
        {
            Utility.bounce(binding.ivOnline,null);
            startActivity(new Intent(this, MultiPuzzleActivity.class));
        }else if (view.getId()==R.id.cv_solo)
        {
            Utility.bounce(binding.ivSolo,null);
            startActivity(new Intent(this,MainActivity.class));
        }
    }


}