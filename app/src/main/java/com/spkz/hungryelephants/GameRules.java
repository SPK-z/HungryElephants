package com.spkz.hungryelephants;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class GameRules extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_rules);

        findViewById(R.id.button_game_rules_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToMainIntent = new Intent(GameRules.this, MainActivity.class);
                backToMainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(backToMainIntent);
            }
        });
    }
}