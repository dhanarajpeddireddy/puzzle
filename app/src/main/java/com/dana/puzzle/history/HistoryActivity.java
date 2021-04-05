package com.dana.puzzle.history;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.dana.puzzle.Ads;
import com.dana.puzzle.R;
import com.dana.puzzle.Utility;
import com.dana.puzzle.database.GameBeen;
import com.dana.puzzle.database.GetGameBeenAsync;
import com.dana.puzzle.databinding.ActivityHistoryBinding;
import com.google.android.gms.ads.AdView;

import java.util.Collections;
import java.util.List;

public class HistoryActivity extends AppCompatActivity implements GetGameBeenAsync.IGettAcheivement, View.OnClickListener {

    ActivityHistoryBinding binding;
    HistoryAdapter historyAdapter;
    Ads inappAds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding= DataBindingUtil.setContentView(this,R.layout.activity_history);

       init();

       getHistory();


       binding.ivBack.setOnClickListener(this);

    }


    private void getHistory() {
        new GetGameBeenAsync(this,this).execute();
    }

    private void init() {
        inappAds=new Ads();
        historyAdapter=new HistoryAdapter(null);
        binding.rvGames.setLayoutManager(new LinearLayoutManager(this));
        binding.rvGames.setAdapter(historyAdapter);
    }


    @Override
    public void success(List<GameBeen> gameBeens) {

        if (gameBeens.size()>0)
        {
            Collections.reverse(gameBeens);
            binding.lyData.setVisibility(View.VISIBLE);
            binding.progress.setVisibility(View.GONE);
            binding.tvMsg.setVisibility(View.GONE);
            historyAdapter.updateList(gameBeens);
        }else
        {
            binding.lyData.setVisibility(View.GONE);
            binding.progress.setVisibility(View.GONE);
            binding.tvMsg.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void fail(String error) {
        binding.lyData.setVisibility(View.GONE);
        binding.progress.setVisibility(View.GONE);
        binding.tvMsg.setVisibility(View.VISIBLE);
        binding.tvMsg.setText(getString(R.string.somethingWrong));
    }

    @Override
    protected void onResume() {
        AdView adView=findViewById(R.id.adView_banner);
        inappAds.googleBannerAd(adView);
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.iv_back)
        {
            Utility.bounce(view,null);
            finish();
        }

    }
}