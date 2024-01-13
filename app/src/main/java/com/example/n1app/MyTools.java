package com.example.n1app;

import android.os.Handler;
import android.os.Looper;

public class MyTools {
    public void waitAndDo(int ms, Runnable runnable){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(runnable, ms);
    }
}
