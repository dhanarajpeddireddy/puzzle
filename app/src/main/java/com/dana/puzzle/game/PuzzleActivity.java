package com.dana.puzzle.game;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dana.puzzle.GameCompletedActivity;
import com.dana.puzzle.R;
import com.dana.puzzle.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class PuzzleActivity extends AppCompatActivity implements TouchListener.IlistnerBack, RequestListener<Drawable>, View.OnClickListener {
    ArrayList<PuzzlePiece> pieces;

    String mCurrentPhotoUri;
    String assetName;
    int peiceSize;

    Bitmap scaledBitmap;

    ImageView imageView,iv_shuffle,iv_preview;

    RelativeLayout layout;


    boolean widthCheck = true;
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

                    widthCheck=false;

                }
            }
        });




    }

    private void setImage() {
        String path = null;
                if (assetName != null) {
                    path="file:///android_asset/"+Constants.ASSET_FOLDER_NAME+"/"+assetName;

                }  else if (mCurrentPhotoUri != null) {
                    path=mCurrentPhotoUri;
                }
                if (path!=null)
                {

                    Log.e("setImage","in : "+path);
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
        touchListener = new TouchListener(this);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        assetName = intent.getStringExtra(Constants.ASSET_NAME);
        mCurrentPhotoUri = intent.getStringExtra(Constants.PHOTO_URI);
        peiceSize = intent.getIntExtra(Constants.PUZZLE_PEICE_SIZE,Constants.DEFAULT_PEICENUMBER);
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
    public void onBackPressed() {



        super.onBackPressed();
    }

    @Override
    public void pieceMatched(PuzzlePiece puzzlePiece) {
        Utility.vibrate(100);
        Utility.bounce(puzzlePiece);
        if (isGameOver()) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(PuzzleActivity.this, GameCompletedActivity.class)
                            .putExtra(Constants.ASSET_NAME,assetName)
                            .putExtra(Constants.PHOTO_URI,mCurrentPhotoUri));

                    finish();
                }
            },2000);

        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void pieceTouched() {

        if (imageView.getVisibility()==View.VISIBLE)
        {
            iv_preview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_visibility_24));
            imageView.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
        Log.e("setimageglide","fail");
        Log.e("getPuzzles",e.getLocalizedMessage());
        return false;
    }

    @Override
    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
        Bitmap bitmap = ((BitmapDrawable)resource).getBitmap();
        getPuzzles(bitmap);
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void getPuzzles(Bitmap re) {

        if (re!=null)
        {

            scaledBitmap = Bitmap.createScaledBitmap(re, widthFinal, heightFinal, true);

            pieces = Utility.splitImage(PuzzleActivity.this,imageView,scaledBitmap,peiceSize);

            widthCheck = false;

            shuffle();
        }else
        {
            Log.e("getPuzzles","null");
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void shuffle() {

        Collections.shuffle(pieces);

        for (PuzzlePiece piece : pieces) {

            piece.setOnTouchListener(touchListener);

            if (piece.canMove)
            {
                if(piece.getParent() != null) {
                    ((ViewGroup)piece.getParent()).removeView(piece);
                }

                Log.e("peicex : ", piece.pieceHeight + " : " + piece.pieceWidth
                        + " : " + piece.xCoord + " : " + piece.yCoord);
                layout.addView(piece);
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) piece.getLayoutParams();
                lParams.leftMargin = (layout.getWidth() - piece.pieceWidth);
                lParams.topMargin =new Random().nextInt( layout.getHeight() - piece.pieceHeight);
                piece.setLayoutParams(lParams);
            }
        }


    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.iv_preview)
        {
            Utility.bounce(view);
            if (imageView.getVisibility()==View.INVISIBLE)
            {
                iv_preview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_visibility_off_24));
                imageView.setVisibility(View.VISIBLE);
            }
            else
            {
                iv_preview.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_visibility_24));
                imageView.setVisibility(View.INVISIBLE);
            }

        }

        if (view.getId()==R.id.iv_shuffle)
        {
            Utility.bounce(view);
            shuffle();
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



