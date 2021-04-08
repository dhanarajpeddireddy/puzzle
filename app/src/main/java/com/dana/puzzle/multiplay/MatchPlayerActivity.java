package com.dana.puzzle.multiplay;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationUtils;

import androidx.databinding.DataBindingUtil;

import com.dana.puzzle.BaseActivity;
import com.dana.puzzle.Constants;
import com.dana.puzzle.R;
import com.dana.puzzle.databinding.ActivityMatchPlayerBinding;
import com.dana.puzzle.tool.Utility;

import java.util.Random;

public class MatchPlayerActivity extends BaseActivity {

    ActivityMatchPlayerBinding binding;
    Handler nameHandler;
    Runnable runnable;
    String opponentName ;
    int peiceSize;
    int times=0,totaltimes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_match_player);

        peiceSize = getIntent().getIntExtra(Constants.PUZZLE_PEICE_SIZE, Constants.DEFAULT_PEICENUMBER);

       // binding.ivPuzzle.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate) );

        Utility.showBlinkAnimation(binding.lyOpponet);

        totaltimes=getTimes();

        nameHandler=new Handler();

        runnable=new Runnable() {
            @Override
            public void run() {
               setName();
            }
        };


        nameHandler.postDelayed(runnable,500);



    }

    private void setName() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                opponentName=getName();
                binding.tvOpponent.setText(opponentName);
                if (times==totaltimes)
                {
                    startActivity(new Intent(MatchPlayerActivity.this,MultiGameActivity.class)
                            .putExtra(Constants.PUZZLE_PEICE_SIZE,peiceSize)
                            .putExtra(Constants.OPPONENT_PLAYER_NAME, opponentName));

                    finish();
                }

                else
                    nameHandler.postDelayed(runnable,1000);

                times++;
            }
        });
    }

    private String getName() {

       return "G"+ new Random().nextInt((12345 - 1) + 1);
    }

    private int getTimes() {

        return  new Random().nextInt((15 - 10) + 10);
    }

    @Override
    protected void onDestroy() {
        nameHandler.removeCallbacks(runnable);
        runnable=null;
        nameHandler=null;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        showBannerAd();
        super.onResume();
    }
}