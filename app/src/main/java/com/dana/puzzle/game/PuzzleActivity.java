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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

public class PuzzleActivity extends AppCompatActivity implements TouchListener.IlistnerBack, RequestListener<Drawable>, View.OnClickListener, Ads.IRewardAdListner {
    ArrayList<PuzzlePiece> pieces;

    String mCurrentPhotoUri;
    String assetName;
    int peiceSize;

    Bitmap scaledBitmap;

    ImageView imageView, iv_shuffle, iv_preview,iv_qlue,iv_music;

    TextView tv_timer;

    RelativeLayout layout;

    Ads inappAds;

    long startTime;

    Handler timer=new Handler();

    Runnable timeCaluculter=new Runnable() {
        @Override
        public void run()
        {
            showTime();

            timer.postDelayed(timeCaluculter,500);

        }
    };


    boolean widthCheck = true,qlueStatus=false;
    int widthFinal, heightFinal;

    TouchListener touchListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

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
    long elapsedSeconds=0;
    long elapsedMinutes=0;
    long elapsedHours=0;
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void showTime() {

                long curenttime=new Date().getTime();

                long different = curenttime-startTime;

                System.out.println("different : " + different);

                long secondsInMilli = 1000;
                long minutesInMilli = secondsInMilli * 60;
                long hoursInMilli = minutesInMilli * 60;
                // long daysInMilli = hoursInMilli * 24; if you want caluculate days

                 elapsedHours = different / hoursInMilli;
                different = different % hoursInMilli;

                 elapsedMinutes = different / minutesInMilli;
                different = different % minutesInMilli;

                 elapsedSeconds = different / secondsInMilli;

                System.out.printf(
                        "%d hours, %d minutes, %d seconds%n",
                         elapsedHours, elapsedMinutes, elapsedSeconds);

               tv_timer.setText(String.format("%02d", elapsedHours)+" : "
                       +String.format("%02d", elapsedMinutes)+" : "
                       +String.format("%02d", elapsedSeconds));

    }

    private void setImage() {
        String path = null;
        if (assetName != null) {
            path = "file:///android_asset/" + Constants.ASSET_FOLDER_NAME + "/" + assetName;

        } else if (mCurrentPhotoUri != null) {
            path = mCurrentPhotoUri;
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
        startTime=new Date().getTime();
        layout = findViewById(R.id.layout);
        tv_timer=findViewById(R.id.tv_timer);
        imageView = findViewById(R.id.imageView);
        iv_shuffle = findViewById(R.id.iv_shuffle);
        iv_shuffle.setOnClickListener(this);
        iv_preview = findViewById(R.id.iv_preview);
        iv_preview.setOnClickListener(this);
        iv_qlue = findViewById(R.id.iv_qlue);
        iv_qlue.setOnClickListener(this);
        iv_music=findViewById(R.id.iv_music);
        iv_music.setOnClickListener(this);
        touchListener = new TouchListener(this);
        inappAds=new Ads();
        timer.postDelayed(timeCaluculter,0);

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
        assetName = intent.getStringExtra(Constants.ASSET_NAME);
        mCurrentPhotoUri = intent.getStringExtra(Constants.PHOTO_URI);
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
        Utility.bounce(puzzlePiece);

        if (isGameOver()) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopTimer();

                    GameBeen gameBeen =new GameBeen();

                    gameBeen.setNumberOfPieces(peiceSize);
                    gameBeen.setHours(elapsedHours);
                    gameBeen.setMinutes(elapsedMinutes);
                    gameBeen.setSeconds(elapsedSeconds);
                    gameBeen.setDate(Utility.getDate(Calendar.getInstance(),"dd MMMM yyyy"));
                    gameBeen.setPhotoUri(mCurrentPhotoUri);
                    gameBeen.setAssetName(assetName);

                    startActivity(new Intent(PuzzleActivity.this, GameCompletedActivity.class)
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
            iv_preview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_visibility_24));
            imageView.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
        Log.e("setimageglide", "fail");
        Log.e("getPuzzles", e.getLocalizedMessage());
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

            pieces = Utility.splitImage(PuzzleActivity.this, imageView, scaledBitmap, peiceSize);

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
                lParams.leftMargin = (layout.getWidth() - piece.pieceWidth);
                lParams.topMargin = new Random().nextInt(layout.getHeight() - piece.pieceHeight);
                piece.setLayoutParams(lParams);
            }
        }


    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_preview) {
            Utility.bounce(view);

            if (imageView.getVisibility() == View.VISIBLE) {
                iv_preview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_visibility_24));
                imageView.setVisibility(View.INVISIBLE);
            }

            else if (PreferenceUtills.getInstance(this).IsValidDateByKey(Constants.PUZZLE_PREVIEW_REWARD_WATCHED_DATE)) {
                    iv_preview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_visibility_24));
                    imageView.setVisibility(View.INVISIBLE);
            }
            else popupForReward(getString(R.string.puzzle_preview),getString(R.string.preview_message),Constants.PUZZLE_PREVIEW_REWARD_WATCHED_DATE);


        }

       else if (view.getId() == R.id.iv_shuffle) {
            Utility.bounce(view);
            shuffle();
        } else if (view.getId() == R.id.iv_qlue) {
            Utility.bounce(view);

         /*   if (PreferenceUtills.getInstance(this).IsValidDateByKey(Constants.QLUE_REWARD_WATCHED_DATE))
            {
                giveQlue();
                }
            else */popupForReward(getString(R.string.qlue),getString(R.string.qlue_desc),Constants.QLUE_REWARD_WATCHED_DATE);


        }

        else if (view.getId()==R.id.iv_music)
        {
            Utility.bounce(view);
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

    @SuppressLint("UseCompatLoadingForDrawables")
    private void giveQlue() {
        if (imageView.getVisibility() == View.VISIBLE) {
            iv_preview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_visibility_24));
            imageView.setVisibility(View.INVISIBLE);
        }

        if (pieces!=null && pieces.size()>0&& pieces.get(0).canMove)
           pieceMatched(pieces.get(0));
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





    private void popupForReward(String title, String message, final String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                loadRewardVideo(id);
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });


        AlertDialog alertDialog = builder.create();
        if (!this.isFinishing())
            alertDialog.show();

        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.BLACK);

        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.BLACK);



    }

    private void loadRewardVideo(String id) {
        if (Utility.isOnline())
            inappAds.loadrewardAd(this,this,id);
        else
            Toast.makeText(getApplicationContext(),getString(R.string.no_net),Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRewardLoaded(String id) {
        inappAds.showRewardAd(this,id);
    }

    @Override
    public void onRewardailed() {

        Toast.makeText(getApplicationContext(),getString(R.string.somethingWrong),Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
       stopTimer();
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


    private void stopTimer() {
        if (timer!=null)
        {
            timer.removeCallbacks(timeCaluculter);
            timeCaluculter=null;
            timer=null;
        }

    }

    @Override
    protected void onResume() {
        setMusicIcon();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (qlueStatus)
                {
                    giveQlue();
                    qlueStatus=false;
                }
            }
        },1000);

        super.onResume();
    }

    @Override
    public void onRewardEan(String id) {
        if (id.equalsIgnoreCase(Constants.PUZZLE_PREVIEW_REWARD_WATCHED_DATE))
        {
            PreferenceUtills.getInstance(this)
                    .setValidDateInPreference(Constants.PUZZLE_PREVIEW_REWARD_WATCHED_DATE,Utility.getDate(Calendar.getInstance(),Constants.DATE_FORMAT_PREFERENCE));
            Toast.makeText(getApplicationContext(),getString(R.string.preview_ready),Toast.LENGTH_SHORT).show();

        }
       else if (id.equalsIgnoreCase(Constants.QLUE_REWARD_WATCHED_DATE))
        {
            qlueStatus=true;
/*            PreferenceUtills.getInstance(this)
                    .setValidDateInPreference(Constants.QLUE_REWARD_WATCHED_DATE,Utility.getDate(Calendar.getInstance(),Constants.DATE_FORMAT_PREFERENCE));*/
            Toast.makeText(getApplicationContext(),getString(R.string.qlue_ready),Toast.LENGTH_SHORT).show();

        }

    }
}








/*    public Bitmap combineImages(Bitmap c, Bitmap s) { // can add a 3rd parameter 'String loc' if you want to save the new image - left some code to do that at the bottom
        Bitmap cs = null;

        int width, height = 0;

        if(c.getWidth() > s.getWidth()) {
            width = c.getWidth() + s.getWidth();
            height = c.getHeight();
        } else {
            width = s.getWidth() + s.getWidth();
            height = c.getHeight();
        }

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, c.getWidth(), 0f, null);

        // this is an extra bit I added, just incase you want to save the new image somewhere and then return the location
    *//*String tmpImg = String.valueOf(System.currentTimeMillis()) + ".png";

    OutputStream os = null;
    try {
      os = new FileOutputStream(loc + tmpImg);
      cs.compress(CompressFormat.PNG, 100, os);
    } catch(IOException e) {
      Log.e("combineImages", "problem combining images", e);
    }*//*

        return cs;
    } */



