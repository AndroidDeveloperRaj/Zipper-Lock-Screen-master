package com.topandnewapps.newyearzipperlock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class Splash extends Activity {

    InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
       /* Load Intersial Add*/
        InterstitialAdmob_Load();
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {

                finish();

                Intent in = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(in);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                } else {
                    finish();
                    // InterstitialAdmob();
                    Intent in = new Intent(getApplicationContext(), SettingActivity.class);
                    startActivity(in);
                }


            }
        }, 2600);
    }

    /*Assign and Load Intercialtial*/
    public void InterstitialAdmob_Load() {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.intercitial_ad_unit_id));
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);


    }

}
