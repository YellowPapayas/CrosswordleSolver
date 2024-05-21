package com.example.crosswordlesolver;

import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeSet;

public class WordleSolver {
    private double[][] freqs = new double[26][5];
    public TreeSet<String> wordSet;

    public WordleSolver(InputStream inputStream) {
        fillFreqs(inputStream);
        WordleComparator compare = new WordleComparator();
        wordSet = new TreeSet<String>(compare);
    }

    private void fillFreqs(InputStream inputStream) {
        Scanner in = new Scanner(inputStream);
        int freqPos = 0;
        while(in.hasNextLine()) {
            String freqLine = in.nextLine();
            freqLine = freqLine.replace("%", "");
            String[] lettFreqs = freqLine.split("\\t");
            for(int i = 1; i <= 5; i++) {
                freqs[freqPos][i-1] = Double.parseDouble(lettFreqs[i]);
            }
            freqPos++;
        }
        in.close();
    }

    public void fillWords(InputStream inputStream) {
        wordSet.clear();
        Scanner in = new Scanner(inputStream);
        while(in.hasNext()) {
            String toAdd = in.next();
            wordSet.add(toAdd);
        }
        in.close();
    }

    public void removeWords(String seq, String input) {
        Iterator<String> iterator = wordSet.iterator();
        ArrayList<String> toRemove = new ArrayList<String>();
        while(iterator.hasNext()) {
            String check = iterator.next();
            for (int i = 0; i < 5; i++) {
                int charInd = check.indexOf(input.charAt((i)));
                boolean charsMatch = check.charAt(i) == input.charAt(i);
                switch (seq.charAt(i)) {
                    case 'b':
                        if((charInd != -1 && seq.charAt(charInd) != 'g' && seq.charAt(input.indexOf(input.charAt(i))) != 'y') || charsMatch) {
                            toRemove.add(check);
                        }
                        break;
                    case 'y':
                        if(charInd == -1 || charsMatch) {
                            toRemove.add(check);
                        }
                        break;
                    case 'g':
                        if(!charsMatch) {
                            toRemove.add(check);
                        }
                        break;
                }
            }
        }
        for(String str : toRemove) {
            wordSet.remove(str);
        }
    }

    private class WordleComparator implements Comparator<String> {
        public double calcWordleValue(String str) {
            int diffLetts = 0;
            double val = 0;
            for(int i = 0; i < 5; i++) {
                int charPos = str.charAt(i) - 97;
                val += freqs[charPos][i];
                if(str.indexOf(str.charAt(i)) == i) {
                    diffLetts++;
                }
            }
            val *= diffLetts/5.0;
            return val;
        }

        @Override
        public int compare(String str1, String str2) {
            if((int)((calcWordleValue(str1) - calcWordleValue(str2)) * 1000) != 0) {
                return (int) ((calcWordleValue(str1) - calcWordleValue(str2)) * 1000);
            } else {
                return str1.compareTo(str2);
            }
        }
    }
}
