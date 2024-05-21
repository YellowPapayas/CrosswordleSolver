package com.example.crosswordlesolver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class WordleActivity extends ActivityWithMenu {
    Button enter, reset;
    ImageButton info;
    LinearLayout display, suggestions;
    TextView word;
    WordleSolver solver = null;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.Wordle) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordle);

        try {
            solver = new WordleSolver(getAssets().open("letter_values.txt"));
            solver.fillWords(getAssets().open("sgb-words.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        enter = findViewById(R.id.nextWordle);
        reset = findViewById(R.id.reset);
        display = findViewById(R.id.displayWordle);
        suggestions = findViewById(R.id.suggestions);
        word = findViewById(R.id.wordleEnter);
        info = findViewById(R.id.wordleInfo);

        for (int i = 0; i < 5; i++) {
            Button clrChar = new Button(this);
            int charDim = 65 * (int) getResources().getDisplayMetrics().density;
            LinearLayout.LayoutParams charParams = new LinearLayout.LayoutParams(charDim, (int) (charDim * 1.1));
            int marginDp = 5 * (int) getResources().getDisplayMetrics().density;
            charParams.setMargins(marginDp, marginDp, marginDp, marginDp);
            charParams.weight = 0.2f;
            clrChar.setLayoutParams(charParams);
            clrChar.setBackgroundColor(0xff787c7f);
            clrChar.setOnClickListener(new WordleActivity.ColorClickListener());
            clrChar.setTag("b");
            display.addView(clrChar);
        }

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(word.getText().toString().length() != 5) {
                    Toast.makeText(v.getContext(), "Please enter a five-letter word", Toast.LENGTH_SHORT).show();
                    return;
                }
                String seq = "";
                for(int c = 0; c < display.getChildCount(); c++) {
                    seq += display.getChildAt(c).getTag();
                }
                for(int cc = 0; cc < suggestions.getChildCount(); cc++) {
                    solver.wordSet.add((String) suggestions.getChildAt(cc).getTag());
                }
                solver.removeWords(seq, word.getText().toString().toLowerCase());
                clearWordle();
                getTopK(10);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    clearWordle();
                    solver.fillWords(getAssets().open("sgb-words.txt"));
                    getTopK(10);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        getTopK(10);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setView(R.layout.wordle_info);
                AlertDialog dialog = builder.create();
                dialog.show();
                Button accept = dialog.findViewById(R.id.wordleAccept);
                accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void clearWordle() {
        display.removeAllViews();
        for (int i = 0; i < 5; i++) {
            Button clrChar = new Button(this);
            int charDim = 65 * (int) getResources().getDisplayMetrics().density;
            LinearLayout.LayoutParams charParams = new LinearLayout.LayoutParams(charDim, (int) (charDim * 1.1));
            int marginDp = 5 * (int) getResources().getDisplayMetrics().density;
            charParams.setMargins(marginDp, marginDp, marginDp, marginDp);
            charParams.weight = 0.2f;
            clrChar.setLayoutParams(charParams);
            clrChar.setBackgroundColor(0xff787c7f);
            clrChar.setOnClickListener(new WordleActivity.ColorClickListener());
            clrChar.setTag("b");
            display.addView(clrChar);
        }

        word.setText("");

        suggestions.removeAllViews();
    }

    private void getTopK(int k) {
        for(int j = 0; j < k; j++) {
            if(solver.wordSet.isEmpty()) {
                break;
            }
            TextView testWord = new TextView(this);
            testWord.setText(solver.wordSet.pollLast());
            testWord.setTag(testWord.getText().toString());
            testWord.setTextSize(20);
            testWord.setTextColor(Color.BLACK);
            testWord.setGravity(Gravity.CENTER_HORIZONTAL);
            testWord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    word.setText(testWord.getText().toString());
                }
            });
            suggestions.addView(testWord);
        }
    }

    private class ColorClickListener implements View.OnClickListener {
        int color = 0;

        @Override
        public void onClick(View v) {
            color++;
            if(color > 2) {
                color = 0;
            }
            switch (color) {
                case 1:
                    v.setBackgroundColor(0xffc8b653);
                    v.setTag("y");
                    break;
                case 2:
                    v.setBackgroundColor(0xff6ca965);
                    v.setTag("g");
                    break;
                default:
                    v.setBackgroundColor(0xff787c7f);
                    v.setTag("b");
                    break;
            }
        }
    }
}