package com.colortap.game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {

    private TextView highScoreText;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, initializationStatus -> {});

        highScoreText = findViewById(R.id.highScoreText);
        Button playButton = findViewById(R.id.playButton);
        adView = findViewById(R.id.adView);

        adView.loadAd(new AdRequest.Builder().build());

        playButton.setOnClickListener(v -> {
            startActivity(new Intent(this, GameActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("colortap", MODE_PRIVATE);
        int highScore = prefs.getInt("high_score", 0);
        highScoreText.setText("Best: " + highScore);
        adView.resume();
    }

    @Override
    protected void onPause() {
        adView.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        adView.destroy();
        super.onDestroy();
    }
}
