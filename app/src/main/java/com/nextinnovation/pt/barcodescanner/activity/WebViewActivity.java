package com.nextinnovation.pt.barcodescanner.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nextinnovation.pt.barcodescanner.R;
import com.nextinnovation.pt.barcodescanner.utils.Utils;

public class WebViewActivity extends AppCompatActivity {
    Button btnRetry ;
    String barCode = "" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        btnRetry = findViewById(R.id.btnRetry);
        loadAdd();

        Intent intent = getIntent();
        if(intent.getExtras()!=null){
            barCode= intent.getStringExtra("product_id");
            loadWebView(barCode);
        }

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadWebView(barCode);
            }
        });

    }

    private void loadAdd() {
         AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }


    private void loadWebView(String barCode) {
        if(Utils.isNetworkAvailable(this)){
            btnRetry.setVisibility(View.INVISIBLE);
            WebView myWebView = findViewById(R.id.google_webview);
            myWebView.setWebViewClient(new WebViewClient());
            myWebView.getSettings().setJavaScriptEnabled(true);

            if(Utils.isValidURL(barCode)){
                myWebView.loadUrl(barCode);
            }else{
                myWebView.loadUrl("https://www.google.com/search?q="+barCode);
            }

        }
        else{
            btnRetry.setVisibility(View.VISIBLE);
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }



    }




}
