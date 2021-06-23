package com.dana.puzzle;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dana.puzzle.tool.Utility;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class Ads {

    private RewardedAd mRewardedAd;
    private IRewardAdListner iRewardAdListner;
   public  interface IRewardAdListner
    {
        void onRewardLoaded(String id);
        void onRewardailed();
        void onRewardEan(String id);
    }


    public void googleBannerAd(AdView adView) {
        Log.e("Ads", "banner ad");



        if (Utility.isOnline()) {
            if (adView != null) {
                Log.e("Ads", "banner ad in");

                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);
            }

        }

    }


    public void loadrewardAd(Activity activity, IRewardAdListner rewardAdListner, final String id)
    {
        if (activity.isFinishing())
            return;

       final ProgressDialog dailog = new ProgressDialog(activity);
        dailog.setMessage(activity.getString(R.string.ad_loading));
        dailog.setCancelable(false);
        dailog.show();

        iRewardAdListner=rewardAdListner;

        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(PuzzleApplication.getContext(), activity.getString(R.string.google_reward_ad_id),
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
                            iRewardAdListner.onRewardLoaded(id);

                        if (dailog.isShowing())
                            dailog.dismiss();
                    }
                });


    }


   public void showRewardAd(Activity activity, final String id)
    {
        if (mRewardedAd != null) {

            mRewardedAd.show(activity, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    if (iRewardAdListner!=null)
                        iRewardAdListner.onRewardEan(id);
                }
            });
        } else {
            Log.d("TAG", "The rewarded ad wasn't ready yet.");
        }
    }



    public void nativeAd(final  NativeAdView adView,Activity activity)
    {

        Log.e("nativeAd", "in");

        AdLoader adLoader = new AdLoader.Builder(activity, activity.getString(R.string.google_native_ad_id))
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        Log.e("nativeAd", nativeAd.getHeadline());
                            TextView headlineView = adView.findViewById(R.id.tv_body);
                            headlineView.setText(nativeAd.getHeadline());
                            adView.setHeadlineView(headlineView);


                          ImageView imageView= adView.findViewById(R.id.iv_adIcon);

                         if (nativeAd.getIcon()!=null && nativeAd.getIcon().getDrawable()!=null)
                         imageView.setImageDrawable(nativeAd.getIcon().getDrawable());

                            adView.setCallToActionView(adView.findViewById(R.id.view_ad));

                            adView.setNativeAd(nativeAd);


                    }

                })  .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.e("nativeAdError", loadAdError.toString());
                        super.onAdFailedToLoad(loadAdError);
                    }
                }).build();


             adLoader.loadAd(new AdRequest.Builder().build());

    }

}
