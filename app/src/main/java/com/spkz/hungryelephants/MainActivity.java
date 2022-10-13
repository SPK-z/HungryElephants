package com.spkz.hungryelephants;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Change start button and text box if game is paused
        // Also show previous or current score
        TextView textScore = findViewById(R.id.tv_previousScore_number);
        if (GameScreen.getGameIsPaused()) {
            // Get strings for start button and previous/current score
            String textContinueGame = getResources().getString(R.string.main_continue_game);
            String textCurrentScore = getResources().getString(R.string.main_current_score);

            // Change text on button and in textView
            TextView startButton = findViewById(R.id.button_main_start_game);
            startButton.setText(textContinueGame);
            TextView scoreText = findViewById(R.id.tv_main_previousScore);
            scoreText.setText(textCurrentScore);

            textScore.setText(String.valueOf(GameScreen.getSavedScore()));
        } else {
            // Retrieve previous score from shared preferences
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            int previousScore = sharedPreferences.getInt("previousScore", 0);
            textScore.setText(String.valueOf(previousScore));
        }

        // Define button behavior
        findViewById(R.id.button_main_start_game).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startGameIntent = new Intent(MainActivity.this, GameScreen.class);
                startActivity(startGameIntent);
            }
        });
        findViewById(R.id.button_main_game_rules).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gameRulesIntent = new Intent(MainActivity.this, GameRules.class);
                startActivity(gameRulesIntent);
            }
        });
        findViewById(R.id.button_main_high_scores).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showHighScoresIntent = new Intent(MainActivity.this, HighScores.class);
                startActivity(showHighScoresIntent);
            }
        });
        findViewById(R.id.tv_main_privacyPolicy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String privacyPolicyURL = getResources().getString(R.string.main_privacy_policy_link);
                Intent goToPrivacyPolicy = new Intent(Intent.ACTION_VIEW , Uri.parse(privacyPolicyURL));
                startActivity(goToPrivacyPolicy);
            }
        });
    }

}