package com.dana.puzzle;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dana.puzzle.databinding.ActivitySelectionBinding;
import com.dana.puzzle.game.Constants;
import com.dana.puzzle.game.PuzzleActivity;
import com.google.android.gms.ads.AdView;

public class SelectionActivity extends AppCompatActivity implements RequestListener<Drawable>, OnClickListner {
    String mCurrentPhotoUri;
    String assetName;

    ActivitySelectionBinding binding;

    Ads inappAds;

    int puzlePeiceNumber=3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding= DataBindingUtil.setContentView(this,R.layout.activity_selection);
       binding.setOnclick(this);
       init();
       getIntentData();
       setImage();

        binding.tvPeiceSize.setText(String.valueOf(Constants.PUZLE_PEICES[puzlePeiceNumber]));

    }

    private void init() {

        inappAds=new Ads();
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
                    .into(binding.imageView);
        }

    }

    private void getIntentData() {
        Intent intent = getIntent();
        assetName = intent.getStringExtra(Constants.ASSET_NAME);
        mCurrentPhotoUri = intent.getStringExtra(Constants.PHOTO_URI);
    }


    @Override
    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
        return false;
    }

    @Override
    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
        return false;
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.bt_play)
        {
            Log.e("bt_play",binding.tvPeiceSize.getText().toString());
            startActivity(new Intent(this, PuzzleActivity.class)
                    .putExtra(Constants.ASSET_NAME,assetName)
            .putExtra(Constants.PHOTO_URI,mCurrentPhotoUri)
            .putExtra(Constants.PUZZLE_PEICE_SIZE,Integer.parseInt(binding.tvPeiceSize.getText().toString())));
            finish();
        }else if (view.getId()==R.id.iv_prev)
        {
            if (puzlePeiceNumber>0)
            {
                puzlePeiceNumber--;
                binding.tvPeiceSize.setText(String.valueOf(Constants.PUZLE_PEICES[puzlePeiceNumber]));
            }
        }else if (view.getId()==R.id.iv_next)
        {
            if (puzlePeiceNumber<Constants.PUZLE_PEICES.length-1)
            {
                puzlePeiceNumber++;
                binding.tvPeiceSize.setText(String.valueOf(Constants.PUZLE_PEICES[puzlePeiceNumber]));
            }
        }
    }



    @Override
    protected void onResume() {
        AdView adView=findViewById(R.id.adView_banner);
        inappAds.googleBannerAd(adView);
        super.onResume();
    }
}