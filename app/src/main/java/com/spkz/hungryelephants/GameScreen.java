package com.spkz.hungryelephants;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;

public class GameScreen extends AppCompatActivity {

    // Static variables used to store values when the games is paused (back button)
    private static boolean gameIsPaused = false;
    private static int savedScore;
    private static int[] savedNumbers;
    private static boolean savedSymbol;

    // Instance variables
    private int currentScore;
    private int[] numbers;
    private boolean symbol;
    private boolean buttonClicked = false;
    private MediaPlayer mp;
    private boolean soundOn;

    // Static methods
    public static boolean getGameIsPaused() { return gameIsPaused; }
    public static int getSavedScore() { return savedScore; }

    // Instance methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        // Draw sound icon
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        soundOn = sharedPreferences.getBoolean("soundSetting", true);
        drawSoundIcon();

        // Set score to zero for new game or to savedScore for paused game
        currentScore = gameIsPaused ? savedScore : 0;
        TextView textScore = findViewById(R.id.tv_gameScreen_currentScore_number);
        textScore.setText(String.valueOf(currentScore));

        // Show first question
        showQuestion();

        // Define button behavior
        findViewById(R.id.tv_gameScreen_number1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!buttonClicked) {
                    buttonClicked = true;
                    checkAnswer(numbers[0]);
                }
            }
        });
        findViewById(R.id.tv_gameScreen_number2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!buttonClicked) {
                    buttonClicked = true;
                    checkAnswer(numbers[1]);
                }
            }
        });
        findViewById(R.id.imageButton_soundIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Switch sound setting
                soundOn = !soundOn;
                // Save sound setting to shared preferences
                SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("soundSetting", soundOn);
                editor.apply();
                // Change sound icon
                drawSoundIcon();
            }
        });
    }

    @Override
    public void onBackPressed() {
        gameIsPaused = true;
        savedScore = currentScore;
        savedNumbers = numbers;
        savedSymbol = symbol;
        Intent backToMainIntent = new Intent(GameScreen.this, MainActivity.class);
        backToMainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(backToMainIntent);
    }
    private void drawSoundIcon() {
        ImageView soundIcon = findViewById(R.id.imageButton_soundIcon);
        if (soundOn) {
            soundIcon.setImageResource(R.drawable.sound_on);
        } else {
            soundIcon.setImageResource(R.drawable.sound_off);
        }
    }
    private void showQuestion(){
        // Make question with range depending on current score
        // Retrieve random numbers and symbol (<, >)
        if (!gameIsPaused) {
            int difficultyFactor = (int) (currentScore / 5) + 1;
            Question question = new Question(difficultyFactor*10);
            numbers = question.getNumbers();
            symbol = question.getSymbol();
        } else {
            numbers = savedNumbers;
            symbol = savedSymbol;
            gameIsPaused = false;
        }

        // Display random numbers on screen
        TextView tv_number1 = findViewById(R.id.tv_gameScreen_number1);
        TextView tv_number2 = findViewById(R.id.tv_gameScreen_number2);
        tv_number1.setText(String.valueOf(numbers[0]));
        tv_number2.setText(String.valueOf(numbers[1]));

        // Display corresponding picture on screen
        setPicture(true, symbol);

        // Allow button clicks again
        buttonClicked = false;
    }
    private void checkAnswer(int userAnswer){
        // Determine correct answer
        int correctAnswer = symbol ? Math.max(numbers[0], numbers[1]) : Math.min(numbers[0], numbers[1]);
        boolean answerIsCorrect = (userAnswer == correctAnswer);
        if (answerIsCorrect){
            // If answer is correct, increase current score and update score on screen
            currentScore += 1;
            TextView textScore = findViewById(R.id.tv_gameScreen_currentScore_number);
            textScore.setText(String.valueOf(currentScore));
        }
        // Change picture of question to picture of check mark or red cross
        setPicture(false, answerIsCorrect);
        // Play sound to indicate (in)correct answer
        if (soundOn) { playSound(answerIsCorrect, symbol); }
        // After some time, go back to main screen or start new question
        continueGame(answerIsCorrect);
    }
    private void setPicture(boolean displayQuestion, boolean GreaterOrCorrect) {
        ImageView iv_operator_image = findViewById(R.id.imageOperator);
        if (displayQuestion) {
            if (GreaterOrCorrect) {
                iv_operator_image.setImageResource(R.drawable.elephant);
            } else {
                iv_operator_image.setImageResource(R.drawable.mouse);
            }
        } else {
            if (GreaterOrCorrect) {
                iv_operator_image.setImageResource(R.drawable.checkmark);
            } else {
                iv_operator_image.setImageResource(R.drawable.cross);
            }
        }
    }
    private void playSound(boolean correctAnswer, boolean caseElephant) {
        stopPlaying();
        if (correctAnswer) {
            if (caseElephant) {
                mp = MediaPlayer.create(GameScreen.this, R.raw.elephant);
            } else {
                mp = MediaPlayer.create(GameScreen.this, R.raw.mouse);
            }
        } else {
            mp = MediaPlayer.create(GameScreen.this, R.raw.wrong);
        }
        mp.start();
    }
    private void stopPlaying() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }
    private void continueGame(boolean newQuestion) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (newQuestion) {
                    showQuestion();
                } else {
                    // Save current score in shared preferences
                    SharedPreferences sharedPreferences = getSharedPreferences("MainActivity", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("previousScore", currentScore);
                    editor.apply();

                    // Check if current score belongs in high scores list
                    boolean newHighScore = HighScores.checkForHighScore(getApplicationContext(), currentScore);
                    if (newHighScore) {
                        // Go to high scores screen and put score in list
                        HighScores.setAddNewHighScore(true);
                        HighScores.setNewHighScoreValue(currentScore);
                        Intent toHighScoresIntent = new Intent(GameScreen.this, HighScores.class);
                        startActivity(toHighScoresIntent);
                        finish();
                    } else {
                        // Go back to main screen
                        Intent backToMainIntent = new Intent(GameScreen.this, MainActivity.class);
                        backToMainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(backToMainIntent);
                    }
                }
            }
        }, 3000);
    }

}