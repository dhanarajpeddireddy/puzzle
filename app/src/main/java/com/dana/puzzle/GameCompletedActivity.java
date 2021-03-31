package com.dana.puzzle;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import com.dana.puzzle.database.GameBeen;
import com.dana.puzzle.database.InsertGameBeenAsync;
import com.dana.puzzle.databinding.ActivityGameCompletedBinding;
import com.dana.puzzle.game.Constants;
import com.google.android.gms.ads.AdView;

public class GameCompletedActivity extends AppCompatActivity implements OnClickListner, RequestListener<Drawable>
, InsertGameBeenAsync.IInsertAcheivement{

ActivityGameCompletedBinding binding;




    Ads inappAds;

    GameBeen gameBeen;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_game_completed);
        binding.setOnclick(this);
        init();
        getIntentData();
        saveAcheivement();
        setImage();
        setMessage();

    }

    @SuppressLint("SetTextI18n")
    private void setMessage() {
        if (gameBeen !=null)
        {
            binding.tvNumOfPieces.setText(gameBeen.numberOfPieces +" "+getString(R.string.peices));
            if (gameBeen.getHours()!=0)
                binding.tvTime.append(gameBeen.getHours()+" Hours ");

            if (gameBeen.getMinutes()!=0)
                binding.tvTime.append(gameBeen.getMinutes()+" Minutes ");

            if (gameBeen.getSeconds()!=0)
                binding.tvTime.append(gameBeen.getSeconds()+" Seconds ");
        }
    }

    private void saveAcheivement() {
        if (gameBeen !=null)
        {
            new InsertGameBeenAsync(gameBeen
                    , this, this).execute();
        }
    }

    private void init() {
        inappAds=new Ads();
    }


    private void setImage() {
        if (gameBeen !=null)
        {
            String path = null;
            if (gameBeen.getAssetName() != null) {
                path="file:///android_asset/"+Constants.ASSET_FOLDER_NAME+"/"+ gameBeen.getAssetName();

            }  else if (gameBeen.getPhotoUri() != null) {
                path= gameBeen.getPhotoUri();
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
    }


    private void getIntentData() {
        gameBeen = (GameBeen) getIntent().getSerializableExtra(Constants.acheive);

    }


    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.bt_close)
        {
            finish();
        }else if (view.getId()==R.id.bt_share)
        {
            Utility.shareApp(this,"Just Now I Completed My "+gameBeen.numberOfPieces + " puzzle Game"
            +" with in "+ binding.tvTime.getText().toString()+"\n");
        }
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
    protected void onResume() {
        AdView adView=findViewById(R.id.adView_banner);
        inappAds.googleBannerAd(adView);
        super.onResume();
    }

    @Override
    public void success(GameBeen gameBeen) {

    }

    @Override
    public void fail(GameBeen gameBeen) {

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

}