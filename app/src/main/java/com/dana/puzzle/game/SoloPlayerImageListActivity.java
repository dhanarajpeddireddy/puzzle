package com.dana.puzzle.game;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.dana.puzzle.Ads;
import com.dana.puzzle.BaseActivity;
import com.dana.puzzle.Constants;
import com.dana.puzzle.R;
import com.dana.puzzle.databinding.ActivityMainBinding;
import com.dana.puzzle.tool.PreferenceUtills;
import com.dana.puzzle.tool.Utility;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

public class SoloPlayerImageListActivity extends BaseActivity implements ImageAdapter.IcallBack, Ads.IRewardAdListner {
    static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 3;
    static final int REQUEST_IMAGE_GALLERY = 4;

    String[] files;

    ActivityMainBinding binding;
    ImagesAdapter imageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_main);
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
        imageAdapter=new ImagesAdapter(null,this,this);

        final GridLayoutManager layoutManager = new GridLayoutManager(this,3);

        binding.grid.setLayoutManager(layoutManager);
        binding.grid.setHasFixedSize(true);

        binding.grid.setAdapter(imageAdapter);

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Intent intent = new Intent(this, SelectionPeicesSoloActivity.class);

                if (resultUri != null) {
                    intent.putExtra("mCurrentPhotoUri", resultUri.toString());
                    startActivity(intent);
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }


    public void onImageFromGalleryClick(View view) {

        Utility.bounce(view,null);

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
    public void onClickImageAdapterItem(String name) {
        Log.e("name",name);
        Intent intent = new Intent(this,SelectionPeicesSoloActivity.class);
        intent.putExtra(Constants.ASSET_NAME, name);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
       showBannerAd();
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


}
