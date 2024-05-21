package com.example.crosswordlesolver;

import java.util.ArrayList;
import java.util.HashSet;

public class Wordle {
    public class OutOfWordsException extends Exception {
        public OutOfWordsException(String errorMessage) {
            super(errorMessage);
        }
    }

    public char[] word = new char[5];
    public boolean[] greens;
    public boolean[] yellows;
    public boolean[] blacks;
    public int wordInd = 0;
    public ArrayList<String> possible;
    public ArrayList<String> backup;
    public int startInd = 0;

    public Wordle(boolean[] g, boolean[] y, boolean[] b) {
        greens = g;
        yellows = y;
        blacks = b;
    }

    public Wordle(String str, boolean[] g, boolean[] y, boolean[] b) {
        this(g, y, b);
        for(int c = 0; c < word.length; c++) {
            word[c] = str.charAt(c);
        }
    }

    public void setWord(String str) {
        for(int c = 0; c < word.length; c++) {
            word[c] = str.charAt(c);
        }
    }

    public void nextWord(boolean loop) throws Wordle.OutOfWordsException {
        if(loop && (wordInd+1)%possible.size() != startInd) {
            if(hasNextWord()) {
                wordInd++;
            } else {
                wordInd = 0;
            }
            setWord(possible.get(wordInd));
        } else if(!loop && hasNextWord()) {
            wordInd++;
            setWord(possible.get(wordInd));
        } else {
            throw new OutOfWordsException("Wordle only has " + possible.size() + " words and has reached its limit");
        }
    }

    public boolean hasNextWord() {
        return wordInd < possible.size() - 1;
    }

    public ArrayList<String> filterGreen(ArrayList<String> currWords, String target) {
        ArrayList<String> newWords = (ArrayList<String>) currWords.clone();
        for(int i = 0; i < greens.length; i++) {
            if(greens[i]) {
                for(int j = newWords.size()-1; j >= 0; j--) {
                    if(newWords.get(j).charAt(i) != target.charAt(i)) {
                        newWords.remove(j);
                    }
                }
            } else {
                for(int j = newWords.size()-1; j >= 0; j--) {
                    if(newWords.get(j).charAt(i) == target.charAt(i)) {
                        newWords.remove(j);
                    }
                }
            }
        }
        return newWords;
    }

    public ArrayList<String> filterYellow(ArrayList<String> currWords, String target) {
        ArrayList<String> newWords = (ArrayList<String>) currWords.clone();
        HashSet<Character> letters = new HashSet<Character>();
        for(int c = 0; c < target.length(); c++) {
            letters.add(target.charAt(c));
        }
        for(int i = 0; i < yellows.length; i++) {
            if(yellows[i]) {
                for(int j = newWords.size()-1; j >= 0; j--) {
                    if(!letters.contains(newWords.get(j).charAt(i))) {
                        newWords.remove(j);
                    } else {
                        int tarCount = 0;
                        int currCount = 0;
                        for(int c = 0; c < target.length(); c++) {
                            if((yellows[c] || greens[c]) && newWords.get(j).charAt(c) == newWords.get(j).charAt(i)) {
                                currCount++;
                            }
                            if(target.charAt(c) == newWords.get(j).charAt(i)) {
                                tarCount++;
                            }
                        }
                        if(currCount > tarCount) {
                            newWords.remove(j);
                        }
                    }
                }
            } else if(blacks[i]) {
                for(int j = newWords.size()-1; j >= 0; j--) {
                    if(letters.contains(newWords.get(j).charAt(i))) {
                        boolean remove = true;
                        for(int c = 0; c < newWords.get(j).length(); c++) {
                            if(((yellows[c] && c < i) || greens[c]) && newWords.get(j).charAt(i) == newWords.get(j).charAt(c)) {
                                remove = false;
                                break;
                            }
                        }
                        if(remove) {
                            newWords.remove(j);
                        }
                    }
                }
            }
        }
        return newWords;
    }

    public void filterBlacks(ArrayList<String> currWords) {
        HashSet<Character> using = new HashSet<Character>();
        for(int g = 0; g < word.length; g++) {
            if(greens[g] || yellows[g]) {
                using.add(word[g]);
            }
        }
        for(int i = 0; i < blacks.length; i++) {
            if(blacks[i]) {
                if(!using.contains(word[i])) {
                    for(int j = currWords.size()-1; j >= 0; j--) {
                        HashSet<Character> letters = new HashSet<Character>();
                        for(int c = 0; c < currWords.get(j).length(); c++) {
                            letters.add(currWords.get(j).charAt(c));
                        }
                        if(letters.contains(word[i])) {
                            currWords.remove(j);
                        }
                    }
                } else {
                    for(int j = currWords.size()-1; j >= 0; j--) {
                        int tarCount = 0;
                        int currCount = 0;
                        for(int c = 0; c < word.length; c++) {
                            if(currWords.get(j).charAt(c) == word[i]) {
                                currCount++;
                            }
                            if(word[c] == word[i]) {
                                tarCount++;
                            }
                        }
                        if(currCount >= tarCount || word[i] == currWords.get(j).charAt(i)) {
                            currWords.remove(j);
                        }
                    }
                }
            } else if(yellows[i]) {
                for(int j = currWords.size()-1; j >= 0; j--) {
                    if(currWords.get(j).charAt(i) == word[i]) {
                        currWords.remove(j);
                    }
                }
            }
        }
    }

    public void removeUnused(ArrayList<String> currWords, String target) {
        HashSet<Character> using = new HashSet<Character>();
        for(int g = 0; g < word.length; g++) {
            if(yellows[g] || greens[g]) {
                using.add(word[g]);
            }
        }
        HashSet<Character> unused = new HashSet<Character>();
        for(int u = 0; u < target.length(); u++) {
            if(!using.contains(target.charAt(u))) {
                unused.add(target.charAt(u));
            }
        }
        for(int j = currWords.size()-1; j >= 0; j--) {
            for(int c = 0; c < currWords.get(j).length(); c++) {
                if(unused.contains(currWords.get(j).charAt(c))) {
                    currWords.remove(j);
                    break;
                }
            }
        }
    }

    public String toString() {
        String out = "";
        for(int i = 0; i < word.length; i++) {
            out += word[i];
        }
        return out;
    }

    public String sequence() {
        String out = "";
        for(int i = 0; i < greens.length; i++) {
            if(greens[i]) {
                out += "g";
            } else if(yellows[i]) {
                out += "y";
            } else {
                out += "b";
            }
        }
        return out;
    }
}
