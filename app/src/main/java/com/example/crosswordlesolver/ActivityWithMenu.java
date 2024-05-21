package com.example.crosswordlesolver;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public abstract class ActivityWithMenu extends AppCompatActivity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Crosswordle:
                Intent myIntent = new Intent(this, MainActivity.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(myIntent);
                return true;
            case R.id.Anagram:
                Intent anagramIntent = new Intent(this, AnagramActivity.class);
                anagramIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
                anagramIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(anagramIntent);
                return true;
            case R.id.Wordle:
                Intent wordleIntent = new Intent(this, WordleActivity.class);
                wordleIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
                wordleIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(wordleIntent);
                return true;
            case R.id.WordLadder:
                Intent wordLadderIntent = new Intent(this, WordLadderActivity.class);
                wordLadderIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
                wordLadderIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(wordLadderIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}