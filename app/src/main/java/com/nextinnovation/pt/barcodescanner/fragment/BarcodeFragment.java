package com.nextinnovation.pt.barcodescanner.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nextinnovation.pt.barcodescanner.R;

/**
 * Created by PT on 2/9/2017.
 */

public class BarcodeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG= "BarcodeFragment";
    private Button btnScan ;
    private ScanRequest scanRequest ;

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
        btnScan = view.findViewById(R.id.btnScan);
        btnScan.setOnClickListener(this);
        loadAdd(view);

    }

    private void loadAdd(View view) {
        AdView mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            scanRequest = (ScanRequest) context;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement retryConnectionListener");
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnScan :
                // Pass the click event to activity to start the scanner .
                scanRequest.scanBarcode();
                break ;
        }
    }


    public interface  ScanRequest{
        void scanBarcode();
    }

}
