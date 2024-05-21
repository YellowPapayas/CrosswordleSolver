package com.example.crosswordlesolver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnagramActivity extends ActivityWithMenu {
    Button find;
    ImageButton info;
    TextView inputWord;
    LinearLayout wordList;
    Switch subsets;
    Anagram anagram;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.Anagram) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anagram);

        ExecutorService exec = Executors.newFixedThreadPool(1);
        exec.execute(new AnagramStartup());
        exec.shutdown();

        find = findViewById(R.id.findAnagrams);
        inputWord = findViewById(R.id.inputWord);
        wordList = findViewById(R.id.wordList);
        subsets = findViewById(R.id.subsets);
        info = findViewById(R.id.anagramInfo);

        find.setText("Loading words...");
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Please wait about ten seconds for the words to load", Toast.LENGTH_SHORT).show();
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setView(R.layout.anagram_info);
                AlertDialog dialog = builder.create();
                dialog.show();
                Button accept = dialog.findViewById(R.id.anagramAccept);
                accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private class AnagramStartup implements Runnable {

        @Override
        public void run() {
            try {
                FileDescriptor fd = getAssets().openFd("words_alpha.txt").getFileDescriptor();
                FileInputStream fis = new FileInputStream(fd);
                anagram = new Anagram(fis);
                Log.d("ANAGRAM SETUP", "done setting up anagram");
            } catch (IOException e) {
                Log.d("ANAGRAM SETUP", e.getMessage());
            }
            find.setText("Find Anagrams");
            find.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    wordList.removeAllViews();
                    if(inputWord.getText().toString().length() < 4) {
                        Toast.makeText(v.getContext(), "Please enter a word with at least 4 letters", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ArrayList<String> anagramList = anagram.getAnagrams(inputWord.getText().toString(), subsets.isChecked());
                    if(anagramList == null || anagramList.size() <= 0) {
                        Toast.makeText(v.getContext(), "No anagrams could be found", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for(String str : anagramList) {
                        TextView testWord = new TextView(v.getContext());
                        testWord.setText(str);
                        testWord.setTextSize(20);
                        testWord.setTextColor(Color.BLACK);
                        testWord.setGravity(Gravity.CENTER_HORIZONTAL);
                        wordList.addView(testWord);
                    }
                }
            });
        }
    }
}