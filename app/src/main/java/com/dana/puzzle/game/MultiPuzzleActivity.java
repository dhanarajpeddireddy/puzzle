package com.dana.puzzle.game;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dana.puzzle.Ads;
import com.dana.puzzle.GameCompletedActivity;
import com.dana.puzzle.MediaPlayerService;
import com.dana.puzzle.PreferenceUtills;
import com.dana.puzzle.R;
import com.dana.puzzle.Utility;
import com.dana.puzzle.database.GameBeen;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

public class MultiPuzzleActivity extends AppCompatActivity implements TouchListener.IlistnerBack, RequestListener<Drawable>, View.OnClickListener {
    ArrayList<PuzzlePiece> pieces;

    String assetName;

    int peiceSize;

    Bitmap scaledBitmap;

    ImageView imageView, iv_shuffle, iv_preview,iv_music;


    RelativeLayout layout;

    Ads inappAds;


    boolean widthCheck = true;
    int widthFinal, heightFinal;

    TouchListener touchListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multy_puzzle);

        init();

        getIntentData();

        final ViewTreeObserver obs = imageView.getViewTreeObserver();
        obs.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onGlobalLayout() {
                if (widthCheck) {
                    widthFinal = imageView.getWidth();
                    heightFinal = imageView.getHeight();

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
                    .into(imageView);
        }

    }

    private void init() {
        layout = findViewById(R.id.layout);
        imageView = findViewById(R.id.imageView);
        iv_shuffle = findViewById(R.id.iv_shuffle);
        iv_shuffle.setOnClickListener(this);
        iv_preview = findViewById(R.id.iv_preview);
        iv_preview.setOnClickListener(this);
        iv_music=findViewById(R.id.iv_music);
        iv_music.setOnClickListener(this);
        touchListener = new TouchListener(this);
        inappAds=new Ads();

    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private void setMusicIcon() {
        if (PreferenceUtills.getInstance(this).getBoolean(Constants.music))
        {
            iv_music.setImageDrawable(getResources().getDrawable(R.drawable.ic_volume_on));
        }else
        {
            iv_music.setImageDrawable(getResources().getDrawable(R.drawable.ic_volume_off));
        }
    }


    private void getIntentData() {
        Intent intent = getIntent();
        peiceSize = intent.getIntExtra(Constants.PUZZLE_PEICE_SIZE, Constants.DEFAULT_PEICENUMBER);
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

        if (isGameOver()) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    GameBeen gameBeen =new GameBeen();

                    gameBeen.setNumberOfPieces(peiceSize);
                    gameBeen.setDate(Utility.getDate(Calendar.getInstance(),"dd MMMM yyyy"));
                    gameBeen.setAssetName(assetName);

                    startActivity(new Intent(MultiPuzzleActivity.this, GameCompletedActivity.class)
                           .putExtra(Constants.acheive, gameBeen));

                    finish();
                }
            }, 1000);

        }
    }

    private void setPeiceStatusToBack(PuzzlePiece puzzlePiece) {
        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) puzzlePiece.getLayoutParams();
        lParams.leftMargin = puzzlePiece.xCoord;
        lParams.topMargin = puzzlePiece.yCoord;
        puzzlePiece.setLayoutParams(lParams);
        puzzlePiece.canMove = false;
        pieces.remove(puzzlePiece);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void pieceTouched() {

        if (imageView.getVisibility() == View.VISIBLE) {
            iv_preview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_visibility_off_24));
            imageView.setVisibility(View.INVISIBLE);
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

            pieces = Utility.splitImage(MultiPuzzleActivity.this, imageView, scaledBitmap, peiceSize);

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
                layout.addView(piece);
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) piece.getLayoutParams();
                if (pieces.indexOf(piece)<=pieces.size()/2)
                lParams.leftMargin = (layout.getWidth() - piece.pieceWidth);
                else
                    lParams.leftMargin = 5;

                lParams.topMargin = new Random().nextInt(layout.getHeight() - piece.pieceHeight);
                piece.setLayoutParams(lParams);
            }
        }


    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_preview) {
            Utility.bounce(view, null);


            if (imageView.getVisibility() == View.VISIBLE) {
                iv_preview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_visibility_off_24));
                imageView.setVisibility(View.INVISIBLE);
            } else {
                iv_preview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_visibility_24));
                imageView.setVisibility(View.VISIBLE);
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
                setMusicIcon();

            }else
            {
                PreferenceUtills.getInstance(this).setboolean(Constants.music,true);
                startService();
                setMusicIcon();

            }


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




    @Override
    protected void onResume() {

        AdView adView=findViewById(R.id.adView_banner);
        inappAds.googleBannerAd(adView);

        setMusicIcon();
        super.onResume();
    }

}





