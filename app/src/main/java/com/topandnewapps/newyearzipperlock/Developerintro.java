package com.topandnewapps.newyearzipperlock;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class Developerintro extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_developerintro);

        /*getSupportActionBar().setTitle("Developer Intro");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        AdView adView = (AdView) this.findViewById(R.id.adView);
        adView.loadAd(new AdRequest.Builder().build());




    }

    @Override
    public void onBackPressed() {
        AdRequest adRequest = new AdRequest.Builder().build();
        SettingActivity.interstitialAd.loadAd(adRequest);
        super.onBackPressed();

    }
}
