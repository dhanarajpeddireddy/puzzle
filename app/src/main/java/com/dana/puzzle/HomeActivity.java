package com.dana.puzzle;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dana.puzzle.databinding.ActivityHomeBinding;
import com.dana.puzzle.game.ImageAdapter;
import com.dana.puzzle.game.ImagesAdapter;
import com.dana.puzzle.game.SelectionPeicesSoloActivity;
import com.dana.puzzle.game.SoloPlayerImageListActivity;
import com.dana.puzzle.history.HistoryActivity;
import com.dana.puzzle.multiplay.MatchPlayerActivity;
import com.dana.puzzle.multiplay.SelectionPeicesActivity;
import com.dana.puzzle.tool.OnClickListner;
import com.dana.puzzle.tool.PreferenceUtills;
import com.dana.puzzle.tool.Utility;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;


public class HomeActivity extends BaseActivity implements OnClickListner, ImageAdapter.IcallBack, Ads.IRewardAdListner {


    ActivityHomeBinding binding;
    ImagesAdapter imageAdapter;
    static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 3;
    String[] files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding= DataBindingUtil.setContentView(this,R.layout.activity_home);
      binding.setOnclick(this);
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
    private void setMusicIcon() {
        if (PreferenceUtills.getInstance(this).getBoolean(Constants.music))
        {
            binding.ivMusic.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_music_note_24));
        }else
        {
            binding.ivMusic.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_music_off_24));
        }
    }


    private void init() {
        imageAdapter=new ImagesAdapter(null,this,this);

        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        binding.grid.setLayoutManager(mLayoutManager);
        binding.grid.setAdapter(imageAdapter);
    }


    public void onImageFromGalleryClick() {

        if (PreferenceUtills.getInstance(this).IsValidDateByKey(Constants.LOCAL_PUZZLE_REWARD_WATCHED_DATE))
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
            } else {
               /* Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE_GALLERY);*/

                CropImage.activity()
                        .start(this);
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
    protected void onResume() {
        showBannerAd();
        setMusicIcon();
        super.onResume();
    }



    @Override
    public void onClick(View view) {

        if (view.getId()==R.id.cv_multi)
        {
            Utility.bounce(binding.cvMulti,null);
            if (Utility.isOnline())
            startActivity(new Intent(this, SelectionPeicesActivity.class));
            else
                Toast.makeText(getApplicationContext(),getString(R.string.no_net),Toast.LENGTH_SHORT).show();
        }else if (view.getId()==R.id.iv_gallery)
        {
            Utility.bounce(binding.ivGallery,null);
            onImageFromGalleryClick();
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

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    }


    @Override
    public void onClickImageAdapterItem(String name) {

        Log.e("name",name);
        Intent intent = new Intent(this, SelectionPeicesSoloActivity.class);
        intent.putExtra(Constants.ASSET_NAME, name);
        startActivity(intent);

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
}