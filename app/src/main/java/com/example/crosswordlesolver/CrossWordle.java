package com.example.crosswordlesolver;

import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class CrossWordle {
    ArrayList<String> words = new ArrayList<String>();
    String endWord;
    int attemptLimit = 5000;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String[] solveCrossword(InputStream stream, String input, String end, boolean randomize) {
        // TODO Auto-generated method stub
        fillWords(stream);
        if(words.size() <= 0) {
            return null;
        }
        Wordle[] problem = userInput(input, end);
        String[] answer = new String[problem.length];
        int attempt = 0;
        for(int i = problem.length-2; i >= 0; i--) {
            ArrayList<String> testG = problem[i].filterGreen(words, endWord);
//			System.out.println(testG.size());
            ArrayList<String> testY = problem[i].filterYellow(testG, endWord);
//			System.out.println(testY.size());
//			for(String s : testY) {
//				System.out.println(s);
//			}
//			System.out.println(words.size());
            //problem[i].possible = testY;
            while(testY.size() <= 0) {
                if(i >= problem.length-2 || attempt > attemptLimit) {
                    return null;
                }
                Wordle prev = problem[i+1];
                words = (ArrayList<String>) prev.backup.clone();
                //System.out.println(prev.toString() + "\n" + problem[i].possible.toString());
                try {
                    attempt++;
                    prev.nextWord(randomize);
                } catch (Wordle.OutOfWordsException e) {
                    // TODO Auto-generated catch block
//					System.out.println(prev.possible.toString() + "\t" + prev.toString());
//					e.printStackTrace();
//					return;
                    if(i < problem.length-3) {
                        i++;
                    } else {
                        //System.out.println(prev.possible.toString());
                        //e.printStackTrace();
                        return null;
                    }
                }
//				System.out.println("\n*BACKTRACK*\n");
//				System.out.println(prev.toString());
                answer[i+1] = prev.toString();
                prev.filterBlacks(words);
                prev.removeUnused(words, endWord);
                testG = problem[i].filterGreen(words, endWord);
                testY = problem[i].filterYellow(testG, endWord);
            }
            problem[i].possible = testY;
            problem[i].backup = (ArrayList<String>) words.clone();
            if(randomize) {
                problem[i].wordInd = (int) (Math.random() * testY.size());
                problem[i].startInd = problem[i].wordInd;
                problem[i].setWord(testY.get(problem[i].wordInd));
            } else {
                problem[i].setWord(testY.get(0));
            }
            if(i < problem.length-2) {
                Wordle prev = problem[i+1];
                HashMap<Character, Integer> prevChars = new HashMap<Character, Integer>();
                for(int pc = 0; pc < prev.word.length; pc++) {
                    if(prev.greens[pc] || prev.yellows[pc]) {
                        prevChars.put(prev.word[pc], prevChars.getOrDefault(prev.word[pc], 0) + 1);
                    }
                }
                boolean done;
                do {
                    done = true;
                    HashMap<Character, Integer> currChars = new HashMap<Character, Integer>();
                    for(int cc = 0; cc < problem[i].word.length; cc++) {
                        if(problem[i].yellows[cc]) {
                            currChars.put(problem[i].word[cc], currChars.getOrDefault(problem[i].word[cc], 0) + 1);
                        }
                    }
                    for(Character r : currChars.keySet()) {
                        if(currChars.get(r) > prevChars.get(r)) {
                            done = false;
                            try {
                                problem[i].nextWord(randomize);
                            } catch (Wordle.OutOfWordsException e) {
                                //e.printStackTrace();
                                return null;
                            }
                        }
                    }
                } while (!done);
            }
            answer[i] = problem[i].toString();
            problem[i].filterBlacks(words);
            problem[i].removeUnused(words, endWord);
//			System.out.println(words.size());
        }
        //System.out.println(problem[2].possible.toString());
        //System.out.println(answer + endWord);
        answer[answer.length-1] = endWord;
        return answer;
    }

    private void fillWords(InputStream inputStream) {
        //File f = new File(Environment.getDataDirectory(), fileName);
        Scanner readFile = null;
        readFile = new Scanner(inputStream);
        while(readFile.hasNext()) {
            words.add(readFile.next());
        }
        readFile.close();
    }

    private Wordle[] userInput(String input, String end) {
        Wordle[] out;
        ArrayList<Wordle> temp = new ArrayList<Wordle>();
        Scanner in = new Scanner(input);
        while (in.hasNext()) {
            String type = in.next();
            if (!validLength(type)) {
                System.out.println("Error: input is too long -> " + type);
                System.exit(0);
            }
            if (!colorInput(type)) {
                endWord = type;
                break;
            }
            boolean[] g = new boolean[5];
            boolean[] y = new boolean[5];
            boolean[] b = new boolean[5];
            for (int j = 0; j < type.length(); j++) {
                switch (type.charAt(j)) {
                    case 'g':
                        g[j] = true;
                        y[j] = false;
                        b[j] = false;
                        break;
                    case 'y':
                        g[j] = false;
                        y[j] = true;
                        b[j] = false;
                        break;
                    default:
                        g[j] = false;
                        y[j] = false;
                        b[j] = true;
                        break;
                }
            }
            Wordle wd = new Wordle(g, y, b);
            temp.add(wd);
        }
        //System.out.println("TEMP SIZE: " + temp.size());
        out = new Wordle[temp.size() + 1];
        for (int i = 0; i < temp.size(); i++) {
            out[i] = temp.get(i);
        }
        boolean[] g = {true, true, true, true, true};
        boolean[] y = new boolean[5];
        boolean[] b = new boolean[5];
        endWord = end;
        Wordle wd = new Wordle(endWord, g, y, b);
        out[out.length - 1] = wd;
        in.close();
        return out;
    }

    private boolean validLength(String str) {
        return str.length() == 5;
    }

    private boolean colorInput(String str) {
        HashSet<Character> valids = new HashSet<Character>(3);
        valids.add('g');
        valids.add('y');
        valids.add('b');
        for(int c = 0; c < str.length(); c++) {
            if(!valids.contains(str.charAt(c))) {
                return false;
            }
        }
        return true;
    }
}
