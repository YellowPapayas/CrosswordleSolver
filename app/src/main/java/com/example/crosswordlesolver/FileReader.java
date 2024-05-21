package com.example.crosswordlesolver;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class FileReader implements Runnable {
    List<String> list = null;
    private FileChannel channel;
    private long startLocation;
    private int size;

    public FileReader(List<String> arr, FileChannel fc, long sl, int sz) {
        list = arr;
        channel = fc;
        startLocation = sl;
        size = sz;
        Log.d("READING THREAD START", "created new thread with start loc " + startLocation);
    }

    @Override
    public void run() {
        ByteBuffer buf = ByteBuffer.allocate(size);
        try {
            channel.read(buf, startLocation);
            String wordPile = new String(buf.array(), Charset.forName("UTF-8"));
            Scanner in = new Scanner(wordPile);
            long startTime = System.currentTimeMillis();
            while(in.hasNext()) {
                String str = in.next();
                if(str != null) {
                    try {
                        list.add(str);
                    } catch (IndexOutOfBoundsException e) {
                        list.add(0, str);
                    }
                }
                if(System.currentTimeMillis() - startTime > 10000) {
                    break;
                }
            }
            Log.d("READING THREAD", "thread with start loc " + startLocation + " is done!!!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
