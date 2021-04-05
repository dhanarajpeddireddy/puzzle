package com.dana.puzzle;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dana.puzzle.game.Constants;
import com.dana.puzzle.history.HistoryActivity;
import com.google.android.gms.ads.AdView;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements ImageAdapter.IcallBack, View.OnClickListener, Ads.IRewardAdListner {
    static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 3;
    static final int REQUEST_IMAGE_GALLERY = 4;

    Ads inappAds;

    String[] files;

    GridView grid;

    ImageAdapter imageAdapter;

    ImageView iv_share,iv_feedback,iv_music,iv_history;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        getFiles();
    }

    private void getFiles() {
        files  = Utility.getAssetFiles();
        if (files != null) {
            Collections.shuffle(Arrays.asList(files));
        }
        imageAdapter.updatelist(files);
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private void init() {
        grid = findViewById(R.id.grid);
        imageAdapter=new ImageAdapter(null,this,this);
        grid.setAdapter(imageAdapter);

        iv_share=findViewById(R.id.iv_share);
        iv_feedback=findViewById(R.id.iv_feedback);
        iv_share.setOnClickListener(this);
        iv_feedback.setOnClickListener(this);
        iv_music=findViewById(R.id.iv_music);
        iv_music.setOnClickListener(this);
        iv_history=findViewById(R.id.iv_history);
        iv_history.setOnClickListener(this);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Intent intent = new Intent(this, SelectionActivity.class);
            if (uri != null) {
                intent.putExtra("mCurrentPhotoUri", uri.toString());
                startActivity(intent);
            }

        }
    }

    public void onImageFromGalleryClick(View view) {

        if (PreferenceUtills.getInstance(this).IsValidDateByKey(Constants.LOCAL_PUZZLE_REWARD_WATCHED_DATE))
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
            } else {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
            }
        }else
        {
            popupForReward();
        }



    }

    private void popupForReward() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.local_puzzle));
        builder.setMessage(getResources().getString(R.string.rewardMessage));
        builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                loadRewardVideo();
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

    private void loadRewardVideo() {
        if (Utility.isOnline())
        inappAds.loadrewardAd(this,this, Constants.LOCAL_PUZZLE_REWARD_WATCHED_DATE);
        else
            Toast.makeText(getApplicationContext(),getString(R.string.no_net),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickImageAdapterItem(String name) {
        Log.e("name",name);
        Intent intent = new Intent(this, SelectionActivity.class);
        intent.putExtra(Constants.ASSET_NAME, name);
        startActivity(intent);
    }


    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.iv_share)
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


    @Override
    protected void onResume() {
        setMusicIcon();
        AdView adView=findViewById(R.id.adView_banner);
       inappAds.googleBannerAd(adView);
        super.onResume();
    }

    @Override
    public void onRewardLoaded(String id) {
        inappAds.showRewardAd(this, id);
    }

    @Override
    public void onRewardailed() {

        Toast.makeText(getApplicationContext(),getString(R.string.somethingWrong),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardEan(String id) {
        PreferenceUtills.getInstance(this)
                .setValidDateInPreference(Constants.LOCAL_PUZZLE_REWARD_WATCHED_DATE,Utility.getDate(Calendar.getInstance(),Constants.DATE_FORMAT_PREFERENCE));
        Toast.makeText(getApplicationContext(),getString(R.string.local_puzzle_ready),Toast.LENGTH_SHORT).show();

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
