package com.dana.puzzle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class Ads {

    private RewardedAd mRewardedAd;
    private IRewardAdListner iRewardAdListner;
   public  interface IRewardAdListner
    {
        void onRewardLoaded();
        void onRewardailed();
        void onRewardEan();
    }


    void googleBannerAd(AdView adView) {
        Log.e("Ads", "banner ad");



        if (Utility.isOnline()) {
            if (adView != null) {
                Log.e("Ads", "banner ad in");

                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);
            }

        }

    }


    public void loadrewardAd(Activity activity, IRewardAdListner rewardAdListner)
    {
        if (activity.isFinishing())
            return;

       final ProgressDialog dailog = new ProgressDialog(activity);
        dailog.setMessage(activity.getString(R.string.ad_loading));
        dailog.setCancelable(false);
        dailog.show();

        iRewardAdListner=rewardAdListner;

        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(PuzzleApplication.getContext(), "ca-app-pub-3940256099942544/5224354917",
                adRequest, new RewardedAdLoadCallback(){
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d("TAG", loadAdError.getMessage());
                        mRewardedAd = null;
                        if (iRewardAdListner!=null)
                            iRewardAdListner.onRewardailed();
                        if (dailog.isShowing())
                            dailog.dismiss();

                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        if (iRewardAdListner!=null)
                            iRewardAdListner.onRewardLoaded();

                        if (dailog.isShowing())
                            dailog.dismiss();
                    }
                });


    }


   public void showRewardAd(Activity activity)
    {
        if (mRewardedAd != null) {

            mRewardedAd.show(activity, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    if (iRewardAdListner!=null)
                        iRewardAdListner.onRewardEan();
                }
            });
        } else {
            Log.d("TAG", "The rewarded ad wasn't ready yet.");
        }
    }

}
