package com.spkz.hungryelephants;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

public class HighScores extends AppCompatActivity {

    // Final variables
    private static final int highScoresLength = 10;
    private static final String delimiter = "X#s";
    private static final String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_ .";
    private static final int maxNameLength = 10;

    // Static variables
    private static String[] names = new String[highScoresLength];
    private static int[] scores = new int[highScoresLength];
    private static String[] dates = new String[highScoresLength];

    private static boolean addNewHighScore = false;
    private static int highScoresIndex;
    private static int newHighScoreValue;

    // Other variables
    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);

        if (addNewHighScore) {
            addNewHighScore = false;
            getNameAndAddToList(highScoresIndex);
        } else {
            // Load previous high scores (or set default values)
            loadHighScores(this);
            // Print high scores list
            printHighScoresToScreen(false);
        }

        // Define button behavior
        findViewById(R.id.imageButton_clear_high_scores).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get strings for dialog and toast
                String dialogTitle = getResources().getString(R.string.high_scores_delete_dialog_title);
                String dialogMessage = getResources().getString(R.string.high_scores_delete_dialog_message);
                String toastMessage = getResources().getString(R.string.high_scores_delete_toast_message);
                String textPositiveButton = getResources().getString(R.string.high_scores_delete_positive_button);
                String textNegativeButton = getResources().getString(R.string.high_scores_delete_negative_button);

                // Make dialog and toast
                AlertDialog dialog = new AlertDialog.Builder(HighScores.this)
                        .setTitle(dialogTitle)
                        .setMessage(dialogMessage)
                        .setPositiveButton(textPositiveButton, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                clearHighScores(getApplicationContext());
                                Toast toast = Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 100);
                                toast.show();
                                printHighScoresToScreen(true);
                            }
                        })
                        .setNegativeButton(textNegativeButton, null)
                        .create();
                dialog.show();
            }
        });
        findViewById(R.id.button_high_scores_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToMainIntent = new Intent(HighScores.this, MainActivity.class);
                backToMainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(backToMainIntent);
            }
        });
    }

    // Static methods
    public static void setAddNewHighScore(boolean setValue) { addNewHighScore = setValue; }
    public static void setNewHighScoreValue(int score) { newHighScoreValue = score; }
    // This method is static so that it can be accessed from GameScreen without creating an instance
    public static boolean checkForHighScore(Context context, int score) {
        // Load previous high scores (or set default values)
        loadHighScores(context);

        // Check if score should be in high scores list
        // If so, highScoresIndex marks the position
        for (int i=0; i<highScoresLength; i++) {
            if (score > scores[i]) {
                highScoresIndex = i;
                return true;
            }
        }
        return false;
    }

    // These methods must be static because they are used by checkForHighScore
    private static void loadHighScores(Context context) {
        if (filesExist(context)) {
            FileInputStream fis = null;

            // Put names in local array
            try {
                fis = context.openFileInput("highscores_names");
                Scanner scanner = new Scanner(fis);
                scanner.useDelimiter(delimiter);
                int index = 0;
                while (scanner.hasNext()) {
                    String name = scanner.next();
                    names[index] = name;
                    index++;
                }
                scanner.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Put scores in local array
            try {
                fis = context.openFileInput("highscores_scores");
                Scanner scanner = new Scanner(fis);
                scanner.useDelimiter(delimiter);
                int index = 0;
                while (scanner.hasNext()) {
                    String score = scanner.next();
                    scores[index] = Integer.parseInt(score);
                    index++;
                }
                scanner.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Put dates in local array
            try {
                fis = context.openFileInput("highscores_dates");
                Scanner scanner = new Scanner(fis);
                scanner.useDelimiter(delimiter);
                int index = 0;
                while (scanner.hasNext()) {
                    String date = scanner.next();
                    dates[index] = date;
                    index++;
                }
                scanner.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            setDefaultValues();
        }
    }
    private static boolean filesExist(Context context) {
        File file = context.getFileStreamPath("highscores_names");
        if(file == null || !file.exists()) { return false; }
        file = context.getFileStreamPath("highscores_scores");
        if(file == null || !file.exists()) { return false; }
        file = context.getFileStreamPath("highscores_dates");
        return file != null && file.exists();
    }
    private static void setDefaultValues() {
        for (int i=0; i<highScoresLength; i++) {
            names[i] = "..........";
            scores[i] = 0;
            dates[i] = "";
        }
    }

    // Instance methods
    public void onBackPressed() {
        Intent backToMainIntent = new Intent(HighScores.this, MainActivity.class);
        backToMainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(backToMainIntent);
    }
    private void printHighScoresToScreen(boolean clearList) {
        LinearLayout ll_nr = (LinearLayout) findViewById(R.id.ll_high_scores_nr);
        LinearLayout ll_names = (LinearLayout) findViewById(R.id.ll_high_scores_names);
        LinearLayout ll_scores = (LinearLayout) findViewById(R.id.ll_high_scores_scores);
        LinearLayout ll_dates = (LinearLayout) findViewById(R.id.ll_high_scores_dates);

        if (clearList) {
            ll_nr.removeAllViews();
            ll_names.removeAllViews();
            ll_scores.removeAllViews();
            ll_dates.removeAllViews();
        }

        for (int nr=0; nr<highScoresLength; nr++) {
            TextView tv_nr = (TextView) new TextView(this);
            tv_nr.setText(Integer.toString(nr+1));
            tv_nr.setPadding(0, 16, 0, 0);
            ll_nr.addView(tv_nr);

            TextView tv_name = (TextView) new TextView(this);
            tv_name.setText(names[nr]);
            tv_name.setPadding(0, 16, 0, 0);
            ll_names.addView(tv_name);

            TextView tv_score = (TextView) new TextView(this);
            tv_score.setText(Integer.toString(scores[nr]));
            tv_score.setPadding(0, 16, 0, 0);
            ll_scores.addView(tv_score);

            TextView tv_date = (TextView) new TextView(this);
            tv_date.setText(dates[nr]);
            tv_date.setPadding(0, 16, 0, 0);
            ll_dates.addView(tv_date);
        }
    }
    private void getNameAndAddToList(int index) {
        // Get strings for dialog
        String textPositiveButton = getResources().getString(R.string.high_scores_name_positive_button);
        String textNegativeButton = getResources().getString(R.string.high_scores_name_negative_button);
        String invalidInput = getResources().getString(R.string.high_scores_name_invalid_input);

        // Make dialog
        View customDialogView = getLayoutInflater().inflate(R.layout.custom_dialog, null);
        final EditText nameText = customDialogView.findViewById(R.id.et_high_scores_dialog);
        nameText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(customDialogView)
                .setPositiveButton(textPositiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Push all values after index one place further
                        for (int i=highScoresLength-1; i>index; i--) {
                            names[i] = names[i-1];
                            scores[i] = scores[i-1];
                            dates[i] = dates[i-1];
                        }
                        // Get name from player
                        String highScoreName = String.valueOf(nameText.getText());
                        if (highScoreName.length()>maxNameLength) {
                            highScoreName = highScoreName.substring(0, maxNameLength);
                        }
                        if(!nameIsValid(highScoreName)) {
                            Toast toast = Toast.makeText(getApplicationContext(), invalidInput, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 100);
                            toast.show();
                            highScoreName = getResources().getString(R.string.high_scores_invalid_name_replacement);
                        }
                        // Get current date
                        calendar = Calendar.getInstance();
                        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        String currentDate = dateFormat.format(calendar.getTime());

                        // Fill index position with current name, score, and date
                        scores[index] = newHighScoreValue;
                        names[index] = highScoreName;
                        dates[index] = currentDate;
                        // Write updated high scores list to file
                        writeHighScoresToFile(HighScores.this);
                        // Print high scores list to screen
                        printHighScoresToScreen(false);
                    }
                })
                .setNegativeButton(textNegativeButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Print high scores list to screen
                        printHighScoresToScreen(false);
                    }
                })
                .create();
        dialog.show();
    }
    private boolean nameIsValid(String name) {
        if (name.length() == 0) {
            return false;
        }
        boolean allSpaces = true;
        for (int i=0; i<name.length(); i++) {
            char letter = name.charAt(i);
            int index = alphabet.indexOf(letter);
            if (index == -1) {
                return false;
            }
            if (Character.compare(letter, (char) ' ') != 0) {
                allSpaces = false;
            }
        }
        return !allSpaces;
    }
    private static void writeHighScoresToFile(Context context) {
        FileOutputStream fos;
        try {
            fos = context.openFileOutput("highscores_names", MODE_PRIVATE);
            for (String name : names) {
                fos.write(name.getBytes());
                fos.write(delimiter.getBytes());
            }
            fos = context.openFileOutput("highscores_scores", MODE_PRIVATE);
            for (int score : scores) {
                fos.write(Integer.toString(score).getBytes());
                fos.write(delimiter.getBytes());
            }
            fos = context.openFileOutput("highscores_dates", MODE_PRIVATE);
            for (String date : dates) {
                fos.write(date.getBytes());
                fos.write(delimiter.getBytes());
            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void clearHighScores(Context context) {
        setDefaultValues();
        writeHighScoresToFile(context);
    }

}