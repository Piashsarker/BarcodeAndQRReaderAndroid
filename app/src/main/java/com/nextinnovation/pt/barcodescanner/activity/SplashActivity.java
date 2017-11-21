package com.nextinnovation.pt.barcodescanner.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.nextinnovation.pt.barcodescanner.R;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        startMainActivity();
        finish();
    }

    private void startMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);

    }

}
