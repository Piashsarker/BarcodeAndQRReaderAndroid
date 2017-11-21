package com.nextinnovation.pt.barcodescanner.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.zxing.integration.android.IntentIntegrator;
import com.nextinnovation.pt.barcodescanner.R;

/**
 * Created by PT on 2/9/2017.
 */

public class BarcodeFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_BARCODE_SCAN=1;
    private static final String TAG= "BarcodeFragment";
    private Button btnScan ;

    public BarcodeFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_barcode_scanner, container, false);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnScan = (Button) view.findViewById(R.id.btnScan);
        btnScan.setOnClickListener(this);
        loadAdMob(view);
        loadAdMob2(view);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnScan :
                startBarcodeScanning();
                break ;
        }
    }

    private void startBarcodeScanning() {
        IntentIntegrator scanIntentIntegrator = new IntentIntegrator(getActivity());
        scanIntentIntegrator.initiateScan();
    }
    private void loadAdMob(View view) {
        MobileAds.initialize(getContext(), "ca-app-pub-1714609736931391~6770372869");
        AdView mAdView = (AdView)view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
    private void loadAdMob2(View view) {
        MobileAds.initialize(getContext(), "ca-app-pub-1714609736931391~6770372869");
        AdView mAdView = (AdView)view.findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
}
