package com.colortap.game;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private static final String[] COLOR_NAMES = {"RED", "BLUE", "GREEN", "YELLOW", "PURPLE", "ORANGE"};
    private static final int[] COLOR_VALUES = {
            Color.rgb(239, 68, 68),    // RED
            Color.rgb(59, 130, 246),   // BLUE
            Color.rgb(34, 197, 94),    // GREEN
            Color.rgb(250, 204, 21),   // YELLOW
            Color.rgb(168, 85, 247),   // PURPLE
            Color.rgb(249, 115, 22)    // ORANGE
    };

    private TextView colorWord, scoreText, timerText, feedbackText;
    private ProgressBar timerBar;
    private Button yesButton, noButton;
    private AdView adView;

    private int score = 0;
    private int currentWordIndex;
    private int currentColorIndex;
    private boolean isMatch;
    private Random random = new Random();
    private CountDownTimer gameTimer;
    private InterstitialAd interstitialAd;
    private int gamesPlayed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        colorWord = findViewById(R.id.colorWord);
        scoreText = findViewById(R.id.scoreText);
        timerText = findViewById(R.id.timerText);
        timerBar = findViewById(R.id.timerBar);
        feedbackText = findViewById(R.id.feedbackText);
        yesButton = findViewById(R.id.yesButton);
        noButton = findViewById(R.id.noButton);
        adView = findViewById(R.id.adView);

        adView.loadAd(new AdRequest.Builder().build());
        loadInterstitialAd();

        SharedPreferences prefs = getSharedPreferences("colortap", MODE_PRIVATE);
        gamesPlayed = prefs.getInt("games_played", 0);

        yesButton.setOnClickListener(v -> checkAnswer(true));
        noButton.setOnClickListener(v -> checkAnswer(false));

        startGame();
    }

    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, getString(R.string.admob_interstitial_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(InterstitialAd ad) {
                        interstitialAd = ad;
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        interstitialAd = null;
                    }
                });
    }

    private void startGame() {
        score = 0;
        scoreText.setText("Score: 0");
        feedbackText.setText("");
        nextRound();

        gameTimer = new CountDownTimer(30000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                timerText.setText("⏱ " + seconds);
                timerBar.setProgress((int) (millisUntilFinished / 100));
            }

            @Override
            public void onFinish() {
                timerText.setText("⏱ 0");
                timerBar.setProgress(0);
                gameOver();
            }
        }.start();
    }

    private void nextRound() {
        currentWordIndex = random.nextInt(COLOR_NAMES.length);

        // 40% chance the color matches the word
        if (random.nextFloat() < 0.4f) {
            currentColorIndex = currentWordIndex;
            isMatch = true;
        } else {
            do {
                currentColorIndex = random.nextInt(COLOR_VALUES.length);
            } while (currentColorIndex == currentWordIndex);
            isMatch = false;
        }

        colorWord.setText(COLOR_NAMES[currentWordIndex]);
        colorWord.setTextColor(COLOR_VALUES[currentColorIndex]);

        yesButton.setEnabled(true);
        noButton.setEnabled(true);
    }

    private void checkAnswer(boolean playerSaidYes) {
        yesButton.setEnabled(false);
        noButton.setEnabled(false);

        if (playerSaidYes == isMatch) {
            score++;
            scoreText.setText("Score: " + score);
            feedbackText.setText("✓ Correct!");
            feedbackText.setTextColor(Color.rgb(34, 197, 94));
        } else {
            score = Math.max(0, score - 1);
            scoreText.setText("Score: " + score);
            feedbackText.setText("✗ Wrong!");
            feedbackText.setTextColor(Color.rgb(239, 68, 68));
        }

        colorWord.postDelayed(this::nextRound, 400);
    }

    private void gameOver() {
        yesButton.setEnabled(false);
        noButton.setEnabled(false);

        SharedPreferences prefs = getSharedPreferences("colortap", MODE_PRIVATE);
        int highScore = prefs.getInt("high_score", 0);
        boolean isNewBest = score > highScore;

        if (isNewBest) {
            prefs.edit().putInt("high_score", score).apply();
        }

        gamesPlayed++;
        prefs.edit().putInt("games_played", gamesPlayed).apply();

        String title = isNewBest ? "🏆 New High Score!" : "⏱ Time's Up!";
        String message = "Score: " + score
                + (isNewBest ? "\nYou beat your record!" : "\nBest: " + highScore);

        new AlertDialog.Builder(this, com.google.android.material.R.style.ThemeOverlay_MaterialComponents_Dialog)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Play Again", (d, w) -> {
                    showInterstitialAndRestart();
                })
                .setNegativeButton("Menu", (d, w) -> finish())
                .setCancelable(false)
                .show();
    }

    private void showInterstitialAndRestart() {
        // Show interstitial every 3 games
        if (gamesPlayed % 3 == 0 && interstitialAd != null) {
            interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    interstitialAd = null;
                    loadInterstitialAd();
                    startGame();
                }
            });
            interstitialAd.show(this);
        } else {
            startGame();
        }
    }

    @Override
    protected void onDestroy() {
        if (gameTimer != null) gameTimer.cancel();
        adView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        adView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adView.resume();
    }
}
