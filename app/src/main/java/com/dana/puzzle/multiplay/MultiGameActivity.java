package com.dana.puzzle.multiplay;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dana.puzzle.BaseActivity;
import com.dana.puzzle.Constants;
import com.dana.puzzle.R;
import com.dana.puzzle.databinding.ActivityMultyPuzzleBinding;
import com.dana.puzzle.game.PuzzlePiece;
import com.dana.puzzle.game.TouchListener;
import com.dana.puzzle.tool.NetworkStateReceiver;
import com.dana.puzzle.tool.OnClickListner;
import com.dana.puzzle.tool.PreferenceUtills;
import com.dana.puzzle.tool.Utility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MultiGameActivity extends BaseActivity implements TouchListener.IlistnerBack, RequestListener<Drawable>, View.OnClickListener, OnClickListner, NetworkStateReceiver.NetworkStateReceiverListener {
    ArrayList<PuzzlePiece> pieces;
    String assetName;
    String opponentName;
    int peiceSize;
    Bitmap scaledBitmap;
    boolean widthCheck = true;
    int widthFinal, heightFinal;
    TouchListener touchListener;
    ActivityMultyPuzzleBinding binding;

    Handler opponentPlayHandler,nonetHandler;
    Runnable oppnetPlayRunnable,noNetRunnable;

    int yourScore=0;
    int opponetScore=0;

    TextView tv_your_score_incremet,tv_opponent_score_increment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding= DataBindingUtil.setContentView(this,R.layout.activity_multy_puzzle);
      binding.setOnclick(this);
        getIntentData();
        init();

        binding.gameLayout.imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onGlobalLayout() {
                if (widthCheck) {
                    widthFinal =  binding.gameLayout.imageView.getWidth();
                    heightFinal =  binding.gameLayout.imageView.getHeight();
                    setImage();
                    widthCheck = false;
                }
            }
        });

    }


    private void setImage() {

        String[] files=Utility.getAssetFiles();

        if (files!=null)
        {
            assetName=files[new Random().nextInt(files.length)];
        }


        String path = null;
        if (assetName != null) {
            path = "file:///android_asset/" + Constants.ASSET_FOLDER_NAME + "/" + assetName;

        }
        if (path != null) {

            Log.e("setImage", "in : " + path);
            Glide.with(this)
                    .load(Uri.parse(path))
                    .placeholder(R.drawable.spalsh2)
                    .listener(this)
                    .into( binding.gameLayout.imageView);
        }

    }

    private void init() {
        touchListener = new TouchListener(this);
        binding.lyOpponent.tvOpponentName.setText(opponentName);
        setPeicesRemaining(peiceSize);
        tv_your_score_incremet=new TextView(this);
        tv_your_score_incremet.setTextColor(getResources().getColor(R.color.light_blue_600));
        tv_your_score_incremet.setTextSize(30);
        tv_your_score_incremet.setText("+");

        tv_opponent_score_increment=new TextView(this);
        tv_opponent_score_increment.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_opponent_score_increment.setTextSize(30);
        tv_opponent_score_increment.setText("+");
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setPieceForOpponent() {
        if (pieces!=null && pieces.size()>0)
        {
            PuzzlePiece puzzlePiece=pieces.get(new Random().nextInt(pieces.size()));
            puzzlePiece.setWho(Constants.OPPONENT);
            pieceMatched(puzzlePiece);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setMusicIcon() {
        binding.menuLayout.ivMusic.setImageDrawable(PreferenceUtills.getInstance(this).getBoolean(Constants.music)
                ?getResources().getDrawable(R.drawable.ic_music):getResources().getDrawable(R.drawable.ic_mute) );
    }


    private void getIntentData() {
        Intent intent = getIntent();
        peiceSize = intent.getIntExtra(Constants.PUZZLE_PEICE_SIZE, Constants.DEFAULT_PEICENUMBER);
        opponentName = intent.getStringExtra(Constants.OPPONENT_PLAYER_NAME);
    }


    private boolean isGameOver() {
        for (PuzzlePiece piece : pieces) {
            if (piece.canMove) {
                return false;
            }
        }
        return true;
    }


    @Override
    public void pieceMatched(PuzzlePiece puzzlePiece) {
        setPeiceStatusToBack(puzzlePiece);
        Utility.vibrate(100);
        Utility.bounce(puzzlePiece,null);
        changeScoreBoard(puzzlePiece);
        if (isGameOver()) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MultiGameActivity.this, MultyGameCompletedActivity.class)
                    .putExtra(Constants.OPPONENT_PLAYER_NAME,opponentName)
                    .putExtra(Constants.PUZZLE_PEICE_SIZE,peiceSize)
                    .putExtra(Constants.YOUR_SCORE,yourScore)
                    .putExtra(Constants.OPPONENT_SCORE,opponetScore));

                    finish();
                }
            }, 1000);

        }
    }

    private void changeScoreBoard(PuzzlePiece puzzlePiece) {
        switch (puzzlePiece.getWho())
        {
            case Constants.YOU:
                yourScore++;
                binding.lyYou.tvYourScore.setText(String.valueOf(yourScore));
                Utility.bounce(binding.lyYou.tvYourScore,null);
                break;

            case Constants.OPPONENT:
                opponetScore++;
                binding.lyOpponent.tvScore.setText(String.valueOf(opponetScore));
                Utility.bounce(binding.lyOpponent.tvScore,null);

            break;
            default:
                throw new IllegalStateException("Unexpected value: " + puzzlePiece.getWho());
        }

    }

    private void setPeiceStatusToBack(PuzzlePiece puzzlePiece) {
        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) puzzlePiece.getLayoutParams();
        lParams.leftMargin = puzzlePiece.xCoord;
        lParams.topMargin = puzzlePiece.yCoord;
        puzzlePiece.setLayoutParams(lParams);

        if (puzzlePiece.getWho()==Constants.YOU)
        {

            tv_your_score_incremet.setLayoutParams(lParams);
            tv_your_score_incremet.bringToFront();

            tv_your_score_incremet.setHeight(puzzlePiece.getHeight());
            tv_your_score_incremet.setWidth(puzzlePiece.getWidth());



            if (tv_your_score_incremet.getParent() != null) {
                ((ViewGroup) tv_your_score_incremet.getParent()).removeView(tv_your_score_incremet);
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {


                    if (tv_your_score_incremet.getParent() != null) {
                        ((ViewGroup) tv_your_score_incremet.getParent()).removeView(tv_your_score_incremet);
                    }

                }
            },1500);

            binding.gameLayout.layout.addView(tv_your_score_incremet);



        }else
        {
            tv_opponent_score_increment.setLayoutParams(lParams);
            tv_opponent_score_increment.bringToFront();

            tv_opponent_score_increment.setHeight(puzzlePiece.getHeight());
            tv_opponent_score_increment.setWidth(puzzlePiece.getWidth());


            if (tv_opponent_score_increment.getParent() != null) {
                ((ViewGroup) tv_opponent_score_increment.getParent()).removeView(tv_opponent_score_increment);
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {


                    if (tv_opponent_score_increment.getParent() != null) {
                        ((ViewGroup) tv_opponent_score_increment.getParent()).removeView(tv_opponent_score_increment);
                    }

                }
            },1500);

            binding.gameLayout.layout.addView(tv_opponent_score_increment);
        }

        puzzlePiece.canMove = false;
        pieces.remove(puzzlePiece);
        setPeicesRemaining(pieces.size());
    }

    private void setPeicesRemaining(int size) {
        binding.tvPeices.setText(String.valueOf(size));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void pieceTouched() {

        if (binding.gameLayout.imageView.getVisibility() == View.VISIBLE) {
            binding.menuLayout.ivPreview.setImageDrawable(getResources().getDrawable(R.drawable.ic_visibility_off));
            binding.gameLayout.imageView.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
        Log.e("setimageglide", "fail");
        if (e != null) {
            Log.e("getPuzzles", e.getLocalizedMessage());
        }
        return false;
    }

    @Override
    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
        Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
        getPuzzles(bitmap);
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void getPuzzles(Bitmap re) {

        if (re != null) {

            scaledBitmap = Bitmap.createScaledBitmap(re, widthFinal, heightFinal, true);

            pieces = Utility.splitImage(MultiGameActivity.this, binding.gameLayout.imageView
                    , scaledBitmap, peiceSize);

            widthCheck = false;

            shuffle();
        } else {
            Log.e("getPuzzles", "null");
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void shuffle() {

        Collections.shuffle(pieces);

        for (PuzzlePiece piece : pieces) {

            piece.setOnTouchListener(touchListener);

            if (piece.canMove) {
                if (piece.getParent() != null) {
                    ((ViewGroup) piece.getParent()).removeView(piece);
                }

                Log.e("peicex : ", piece.pieceHeight + " : " + piece.pieceWidth
                        + " : " + piece.xCoord + " : " + piece.yCoord);
                binding.gameLayout.layout.addView(piece);
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) piece.getLayoutParams();
                if (pieces.indexOf(piece)<=pieces.size()/2)
                lParams.leftMargin = (binding.gameLayout.layout.getWidth() - piece.pieceWidth);
                else
                    lParams.leftMargin = 5;

                lParams.topMargin = new Random().nextInt(binding.gameLayout.layout.getHeight()
                        - piece.pieceHeight);
                piece.setLayoutParams(lParams);
            }
        }

         startPlayForOpponet();
    }

    private  void startPlayForOpponet()
    {
        if (Utility.isOnline())
        {
            if (pieces!=null && pieces.size()>0)
            {
                opponentPlayHandler=new Handler();

                oppnetPlayRunnable=new Runnable() {
                    @Override
                    public void run() {
                        setPieceForOpponent();
                        playForOpponent();
                    }
                };

                playForOpponent();
            }
        }

    }
    private void playForOpponent() {
        if (opponentPlayHandler != null) {
            opponentPlayHandler.postDelayed(oppnetPlayRunnable,
                    Constants.OPPONET_PLAT_TIME[new Random().nextInt(Constants.OPPONET_PLAT_TIME.length)]);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onClick(View view) {

        if (view.getId()==R.id.iv_menu)
        {
            Utility.bounce(view,null);
            binding.menuLayout.drawerLayout.setVisibility(View.VISIBLE);
            binding.menuLayout.drawerLayout.animate().setDuration(160).start();
        } else if (view.getId()==R.id.iv_back)
        {
            Utility.bounce(view,null);
            binding.menuLayout.drawerLayout.setVisibility(View.GONE);
        }
       else if (view.getId() == R.id.iv_preview) {
            Utility.bounce(view, null);


            if (binding.gameLayout.imageView.getVisibility() == View.VISIBLE) {
                binding.menuLayout.ivPreview.setImageDrawable(getResources().getDrawable(R.drawable.ic_visibility_off));
                binding.gameLayout.imageView.setVisibility(View.INVISIBLE);
            } else {
                binding.menuLayout.ivPreview.setImageDrawable(getResources().getDrawable(R.drawable.ic_visible));
                binding.gameLayout.imageView.setVisibility(View.VISIBLE);
            }
        }


       else if (view.getId() == R.id.iv_shuffle) {
            Utility.bounce(view,null);
            shuffle();
        }

        else if (view.getId()==R.id.iv_music)
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

    @Override
    protected void onResume() {
        showBannerAd();
        setMusicIcon();
        startNetworkBroadcastReceiver();
        startPlayForOpponet();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
       stopPlayForOpponent();
        super.onDestroy();
    }

    private void stopPlayForOpponent() {
        if (opponentPlayHandler!=null)
        {
            opponentPlayHandler.removeCallbacks(oppnetPlayRunnable);
            oppnetPlayRunnable=null;
            opponentPlayHandler=null;
        }
    }


    @Override
    protected void onPause() {
        stopPlayForOpponent();
        unregisterNetworkBroadcastReceiver();
        super.onPause();
    }

    private NetworkStateReceiver networkStateReceiver;


    public void registerNetworkBroadcastReceiver() {
        registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public void unregisterNetworkBroadcastReceiver() {
        unregisterReceiver(networkStateReceiver);
    }

    public void startNetworkBroadcastReceiver() {
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener((this));
        registerNetworkBroadcastReceiver();
    }

    @Override
    public void networkAvailable() {

        if (alertDialog!=null && alertDialog.isShowing())
            alertDialog.dismiss();

        if (nonetHandler!=null)
        {
           removeNonetHandler();
        }

        startPlayForOpponet();

    }


    AlertDialog alertDialog;

    @Override
    public void networkUnavailable() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.no_net_title));
        builder.setMessage(getString(R.string.no_net));
         alertDialog = builder.create();
        alertDialog.setCancelable(false);
        if (!this.isFinishing())
            alertDialog.show();

         startNetwarkHandler();
         stopPlayForOpponent();



    }

    private void startNetwarkHandler() {
        nonetHandler=new Handler();

        noNetRunnable=new Runnable() {
            @Override
            public void run() {
                finish();
            }
        };
        nonetHandler.postDelayed(noNetRunnable,30000);
    }

    private void removeNonetHandler() {
        nonetHandler.removeCallbacks(noNetRunnable);
        nonetHandler=null;
        noNetRunnable=null;
    }
}





