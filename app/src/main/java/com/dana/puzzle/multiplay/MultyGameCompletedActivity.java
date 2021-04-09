package com.dana.puzzle.multiplay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.databinding.DataBindingUtil;
import com.dana.puzzle.BaseActivity;
import com.dana.puzzle.Constants;
import com.dana.puzzle.R;
import com.dana.puzzle.databinding.ActivityMultyGameCompletedBinding;
import com.dana.puzzle.tool.OnClickListner;
import com.dana.puzzle.tool.Utility;

public class MultyGameCompletedActivity extends BaseActivity implements OnClickListner {

    ActivityMultyGameCompletedBinding binding;

    int yourScore,opponentScoe,peiceSize;
    String opponentName,gameTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding= DataBindingUtil.setContentView(this,R.layout.activity_multy_game_completed);
      binding.setOnclick(this);


      getIntentData();

      binding.tvOpponent.setText(opponentName);
      binding.tvTimer.setText(gameTime);
      binding.tvPiecesNumberOpponent.setText(String.valueOf(opponentScoe));
      binding.tvPiecesNumber.setText(String.valueOf(yourScore));
      if (peiceSize!=-1)
          binding.tvPeiceSize.setText(String.valueOf(peiceSize));

      showWinner();

    }

    private void showWinner() {

        if (yourScore!=-1 && opponentScoe!=-1)
        {
            if (yourScore>=opponentScoe)
            {
                binding.ivYouWin.setVisibility(View.VISIBLE);
                Utility.showBlinkAnimation(binding.ivYouWin);

            }else
            {
                binding.ivOpponentWin.setVisibility(View.VISIBLE);
                Utility.showBlinkAnimation(binding.ivOpponentWin);
            }

        }

    }

    private void getIntentData() {
        opponentName=getIntent().getStringExtra(Constants.OPPONENT_PLAYER_NAME);
        gameTime=getIntent().getStringExtra(Constants.GAME_TIME);
        yourScore=getIntent().getIntExtra(Constants.YOUR_SCORE,-1);
        opponentScoe=getIntent().getIntExtra(Constants.OPPONENT_SCORE,-1);
        peiceSize=getIntent().getIntExtra(Constants.PUZZLE_PEICE_SIZE,-1);

    }



    @Override
    protected void onResume() {
        showBannerAd();
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.tv_playAgain)
        {
            startActivity(new Intent(this,SelectionPeicesActivity.class));
            finish();
        }
    }
}