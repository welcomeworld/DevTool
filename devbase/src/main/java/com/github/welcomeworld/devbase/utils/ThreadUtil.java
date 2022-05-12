package com.github.welcomeworld.devbase.utils;

import android.os.Handler;
import android.os.Looper;

import org.jdeferred.android.AndroidDeferredManager;

public class ThreadUtil {
    private static final AndroidDeferredManager gDM = new AndroidDeferredManager();
    private static final Handler gUiHandler = new Handler(Looper.getMainLooper());

    public static AndroidDeferredManager defer() {
        return gDM;
    }

    public static void post(Runnable r) {
        gUiHandler.post(r);
    }

    public static void postDelayed(long delay, Runnable r) {
        gUiHandler.postDelayed(r, delay);
    }
}
