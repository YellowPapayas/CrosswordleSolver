package com.example.crosswordlesolver;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends ActivityWithMenu {
    Button addRow, removeRow, submit, clear;
    ImageButton info;
    TextView endWord;
    Switch randomize;
    LinearLayout rowList;
    CrossWordle solver = new CrossWordle();

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.Crosswordle) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addRow = findViewById(R.id.addRow);
        removeRow = findViewById(R.id.removeRow);
        submit = findViewById(R.id.submit);
        clear = findViewById(R.id.clear);
        rowList = findViewById(R.id.rowList);
        info = findViewById(R.id.crosswordleInfo);

        endWord = findViewById(R.id.endWord);
        randomize = findViewById(R.id.random);

        LinearLayout wordleRow = new LinearLayout(this);
        wordleRow.setMinimumWidth(300 * (int) getResources().getDisplayMetrics().density);
        wordleRow.setMinimumHeight(90 * (int) getResources().getDisplayMetrics().density);
        wordleRow.setWeightSum(1);
        for (int i = 0; i < 5; i++) {
            Button clrChar = new Button(this);
//                    clrChar.setWidth(60);
//                    clrChar.setHeight(60);
            int charDim = 60 * (int) getResources().getDisplayMetrics().density;
            LinearLayout.LayoutParams charParams = new LinearLayout.LayoutParams(charDim, (int) (charDim * 1.1));
            int marginDp = 5 * (int) getResources().getDisplayMetrics().density;
            charParams.setMargins(marginDp, marginDp, marginDp, marginDp);
            charParams.weight = 0.2f;
            clrChar.setLayoutParams(charParams);
            clrChar.setTextColor(Color.WHITE);
            clrChar.setTextSize(24);
            clrChar.setTypeface(null, Typeface.BOLD);
            clrChar.setBackgroundColor(0xff787c7f);
            clrChar.setTag("b");
            clrChar.setOnClickListener(new ColorClickListener());
            wordleRow.addView(clrChar);
        }
        rowList.addView(wordleRow, rowList.getChildCount()-1);

        submit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if(endWord.getText().toString().length() < 5) {
                    Toast.makeText(MainActivity.this, "Please enter a five-letter end word", Toast.LENGTH_SHORT).show();
                    return;
                }
                String seq = "";
                String[] answer = null;
                for(int i = 0; i < rowList.getChildCount()-1; i++) {
                    LinearLayout wordleRow = (LinearLayout) rowList.getChildAt(i);
                    for(int c = 0; c < wordleRow.getChildCount(); c++) {
                        seq += wordleRow.getChildAt(c).getTag();
                    }
                    seq += "\n";
                }
                try {
                    answer = solver.solveCrossword(getAssets().open("sgb-words.txt"), seq, endWord.getText().toString().toLowerCase(), randomize.isChecked());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(answer != null) {
                    for(int r = 0; r < rowList.getChildCount()-1; r++) {
                        LinearLayout wordleRow = (LinearLayout) rowList.getChildAt(r);
                        for(int b = 0; b < wordleRow.getChildCount(); b++) {
                            ((Button) wordleRow.getChildAt(b)).setText(answer[r].charAt(b) + "");
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "This sequence is unsolvable", Toast.LENGTH_SHORT).show();
                }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int a = 0; a < rowList.getChildCount()-1; a++) {
                    LinearLayout wordleRow = (LinearLayout) rowList.getChildAt(a);
                    for(int s = 0; s < 5; s++) {
                        Button ch = (Button) wordleRow.getChildAt(s);
                        ch.setText("");
                        switch ((String) ch.getTag()) {
                            case "y":
                                ch.callOnClick();
                            case "g":
                                ch.callOnClick();
                        }
                    }
                }
            }
        });

        addRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rowList.getChildCount() < 7) {
                    LinearLayout wordleRow = new LinearLayout(v.getContext());
                    wordleRow.setMinimumWidth(300 * (int) getResources().getDisplayMetrics().density);
                    wordleRow.setMinimumHeight(90 * (int) getResources().getDisplayMetrics().density);
                    wordleRow.setWeightSum(1);
                    for (int i = 0; i < 5; i++) {
                        Button clrChar = new Button(v.getContext());
//                    clrChar.setWidth(60);
//                    clrChar.setHeight(60);
                        int charDim = 60 * (int) getResources().getDisplayMetrics().density;
                        LinearLayout.LayoutParams charParams = new LinearLayout.LayoutParams(charDim, (int) (charDim * 1.1));
                        int marginDp = 5 * (int) getResources().getDisplayMetrics().density;
                        charParams.setMargins(marginDp, marginDp, marginDp, marginDp);
                        charParams.weight = 0.2f;
                        clrChar.setLayoutParams(charParams);
                        clrChar.setTextColor(Color.WHITE);
                        clrChar.setTextSize(24);
                        clrChar.setTypeface(null, Typeface.BOLD);
                        clrChar.setBackgroundColor(0xff787c7f);
                        clrChar.setTag("b");
                        clrChar.setOnClickListener(new ColorClickListener());
                        wordleRow.addView(clrChar);
                    }
                    rowList.addView(wordleRow, rowList.getChildCount()-1);
                } else {
                    Toast toast = Toast.makeText(v.getContext(), "Out of space!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        removeRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rowList.getChildCount() > 2) {
                    rowList.removeViewAt(rowList.getChildCount()-2);
                } else {
                    Toast toast = Toast.makeText(v.getContext(), "Can't remove anymore!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setView(R.layout.crosswordle_info);
                AlertDialog dialog = builder.create();
                dialog.show();
                Button accept = dialog.findViewById(R.id.crossAccept);
                accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
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