package com.example.crosswordlesolver;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WordLadder {
    final int THREADS = 5;
    List<String> words = null;
    HashSet<String> wordSet = new HashSet<String>();

    public WordLadder(FileInputStream inputStream) throws IOException {
        words = new ArrayList<String>();

        FileChannel channel = inputStream.getChannel();
        long rem_sz = channel.size();
        long chunk_sz = rem_sz/THREADS;

        ExecutorService exec = Executors.newFixedThreadPool(THREADS);
        long start_loc = 0;
        while(rem_sz >= chunk_sz) {
            exec.execute(new FileReader(words, channel, start_loc, (int)chunk_sz));
            rem_sz -= chunk_sz;
            start_loc += chunk_sz;
        }
        exec.execute(new FileReader(words, channel, start_loc, (int)rem_sz));
        exec.shutdown();
        while(!exec.isTerminated()) {

        }
        removeDuplicates();
    }

    private void removeDuplicates() {
        for(int i = 0; i < words.size(); i++) {
            if(wordSet.contains(words.get(i))) {
                words.remove(i);
                i--;
            } else {
                wordSet.add(words.get(i));
            }
        }
    }

    public ArrayList<String> getLadders(String inputWord) {
        ArrayList<String> output = new ArrayList<String>();
        for(int i = 0; i < words.size(); i++) {
            if(isOneOff(inputWord, words.get(i))) {
                output.add(words.get(i));
            }
        }
        return output;
    }

    public boolean isOneOff(String inputWord, String test) {
        if(test == null || inputWord.length() != test.length()) {
            return false;
        }
        boolean hasDiff = false;
        for(int i = 0; i < inputWord.length(); i++) {
            if(inputWord.charAt(i) != test.charAt(i)) {
                if(hasDiff) {
                    return false;
                } else {
                    hasDiff = true;
                }
            }
        }
        return hasDiff;
    }
}
