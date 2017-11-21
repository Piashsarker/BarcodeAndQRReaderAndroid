package com.nextinnovation.pt.barcodescanner.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.nextinnovation.pt.barcodescanner.R;

public class WebViewActivity extends AppCompatActivity {
    Button btnRetry ;
    String barCode = "" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        btnRetry = (Button) findViewById(R.id.btnRetry);



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

    private void loadWebView(String barCode) {
        if(isNetworkAvailable(this)){
            btnRetry.setVisibility(View.INVISIBLE);
            WebView myWebView = (WebView)findViewById(R.id.google_webview);
            myWebView.setWebViewClient(new WebViewClient());
            myWebView.getSettings().setJavaScriptEnabled(true);
            myWebView.loadUrl("https://www.google.com/search?q="+barCode);
        }
        else{
            btnRetry.setVisibility(View.VISIBLE);
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }



    }
    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



}
