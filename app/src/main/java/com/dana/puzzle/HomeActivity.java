package com.dana.puzzle;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.dana.puzzle.databinding.ActivityHomeBinding;
import com.dana.puzzle.game.SoloPlayerImageListActivity;
import com.dana.puzzle.history.HistoryActivity;
import com.dana.puzzle.multiplay.MatchPlayerActivity;
import com.dana.puzzle.multiplay.SelectionPeicesActivity;
import com.dana.puzzle.tool.OnClickListner;
import com.dana.puzzle.tool.PreferenceUtills;
import com.dana.puzzle.tool.Utility;


public class HomeActivity extends BaseActivity implements OnClickListner {


    ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding= DataBindingUtil.setContentView(this,R.layout.activity_home);
      binding.setOnclick(this);
      init();

    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private void setMusicIcon() {
        if (PreferenceUtills.getInstance(this).getBoolean(Constants.music))
        {
            binding.ivMusic.setImageDrawable(getResources().getDrawable(R.drawable.ic_music));
        }else
        {
            binding.ivMusic.setImageDrawable(getResources().getDrawable(R.drawable.ic_mute));
        }
    }


    private void init() {

    }


    @Override
    protected void onResume() {
        showBannerAd();
        setMusicIcon();
        super.onResume();
    }



    @Override
    public void onClick(View view) {

        if (view.getId()==R.id.cv_multi)
        {
            Utility.bounce(binding.ivOnline,null);
            if (Utility.isOnline())
            startActivity(new Intent(this, SelectionPeicesActivity.class));
            else
                Toast.makeText(getApplicationContext(),getString(R.string.no_net),Toast.LENGTH_SHORT).show();
        }else if (view.getId()==R.id.cv_solo)
        {
            Utility.bounce(binding.ivSolo,null);
            startActivity(new Intent(this, SoloPlayerImageListActivity.class));
        }

        else if (view.getId()==R.id.iv_share)
        {
            Utility.bounce(view,null);
            Utility.shareApp(this,"");
        }
        else  if (view.getId()==R.id.iv_history)
        {
            Utility.bounce(view,null);
            startActivity(new Intent(this, HistoryActivity.class));
        }

        else  if (view.getId()==R.id.iv_feedback)
        {
            Utility.bounce(view,null);
            Utility.ContactsUs(this);
        } else if (view.getId()==R.id.iv_music)
        {
            Utility.bounce(view,null);
            if (PreferenceUtills.getInstance(this).getBoolean(Constants.music))
            {
                PreferenceUtills.getInstance(this).setboolean(Constants.music,false);
                stopService();

            }else
            {
                PreferenceUtills.getInstance(this).setboolean(Constants.music,true);
                startService();

            }
            setMusicIcon();


        }
    }



    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.clickAgainBack), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }




}