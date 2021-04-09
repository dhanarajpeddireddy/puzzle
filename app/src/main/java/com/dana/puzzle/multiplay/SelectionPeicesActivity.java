package com.dana.puzzle.multiplay;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.dana.puzzle.BaseActivity;
import com.dana.puzzle.Constants;
import com.dana.puzzle.PeicesAdapter;
import com.dana.puzzle.R;
import com.dana.puzzle.databinding.ActivitySelectionPeicesBinding;
import com.dana.puzzle.tool.Utility;

public class SelectionPeicesActivity extends BaseActivity implements PeicesAdapter.IcallBack {

    ActivitySelectionPeicesBinding binding;
    PeicesAdapter peicesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_selection_peices);
        init();
    }

    private void init() {
        peicesAdapter=new PeicesAdapter(Constants.PUZLE_PEICES_MULTY,this,this);
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

        if (Utility.isOnline())
        {
            startActivity(new Intent(this,
                    MatchPlayerActivity.class).putExtra(Constants.PUZZLE_PEICE_SIZE,peice));
            finish();
        }  else
            Toast.makeText(getApplicationContext(),getString(R.string.no_net),Toast.LENGTH_SHORT).show();



    }
}