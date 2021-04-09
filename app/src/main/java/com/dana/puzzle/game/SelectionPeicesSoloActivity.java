package com.dana.puzzle.game;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dana.puzzle.BaseActivity;
import com.dana.puzzle.Constants;
import com.dana.puzzle.PeicesAdapter;
import com.dana.puzzle.R;
import com.dana.puzzle.databinding.ActivitySelectionPeicesBinding;
import com.dana.puzzle.multiplay.MatchPlayerActivity;

public class SelectionPeicesSoloActivity extends BaseActivity implements PeicesAdapter.IcallBack {

    ActivitySelectionPeicesBinding binding;
    PeicesAdapter peicesAdapter;
    String mCurrentPhotoUri;
    String assetName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding= DataBindingUtil.setContentView(this,R.layout.activity_selection_peices);
      init();
      getIntentData();

    }

    private void getIntentData() {
        assetName = getIntent().getStringExtra(Constants.ASSET_NAME);
        mCurrentPhotoUri = getIntent().getStringExtra(Constants.PHOTO_URI);
    }

    private void init() {
        peicesAdapter=new PeicesAdapter(Constants.PUZLE_PEICES,this,this);
        LinearLayoutManager  horizontalLayout = new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false);
        if (binding!=null)
        {
            binding.rvPeices.setLayoutManager(horizontalLayout);
            binding.rvPeices.setAdapter(peicesAdapter);
        }

    }


    @Override
    protected void onResume() {
        showBannerAd();
        super.onResume();
    }

    @Override
    public void onClickPeicesAdapterItem(int peice) {

        startActivity(new Intent(this, GameActivity.class)
                .putExtra(Constants.ASSET_NAME,assetName)
                .putExtra(Constants.PHOTO_URI,mCurrentPhotoUri)
                .putExtra(Constants.PUZZLE_PEICE_SIZE,peice));
        finish();

    }
}