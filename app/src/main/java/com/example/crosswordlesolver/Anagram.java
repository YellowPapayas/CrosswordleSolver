package com.example.crosswordlesolver;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Anagram {
    final int THREADS = 5;
    List<String> words = null;
    HashSet<String> wordSet = new HashSet<String>();

    public Anagram(FileInputStream inputStream) throws IOException {
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

    public ArrayList<String> getAnagrams(String inputWord, boolean includeSubs) {
        ArrayList<String> output = new ArrayList<String>();
        for(String str : words) {
            if(!inputWord.equals(str) && isAnagram(inputWord, str, includeSubs)) {
                output.add(str);
            }
        }
        return output;
    }

    public boolean isAnagram(String word, String test, boolean includeSub) {
        if(test == null || (word.length() != test.length() && !includeSub)) {
            return false;
        }
        char[] s1Arr = word.toCharArray();
        char[] s2Arr = test.toCharArray();
        Arrays.sort(s1Arr);
        Arrays.sort(s2Arr);
        return Arrays.equals(s1Arr, s2Arr) || (includeSub && isSubset(s1Arr, s2Arr));
    }

    private String charArr(char[] str) {
        String out = "";
        for(int i = 0; i < str.length; i++) {
            out += str[i];
        }
        return out;
    }

    public boolean isSubset(char[] word, char[] test) {
        int testInd = 0;
        for(int i = 0; i < word.length; i++) {
            if(word[i] == test[testInd]) {
                testInd++;
            }
            if(testInd >= test.length) {
                return true;
            }
        }
        return false;
    }
}
