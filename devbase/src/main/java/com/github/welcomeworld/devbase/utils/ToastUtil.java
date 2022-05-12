package com.github.welcomeworld.devbase.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

@SuppressLint("SoonBlockedPrivateApi")
public class ToastUtil {
    private static WeakReference<Toast> mToast;
    private static Context applicationContext;

    public static void init(Context context) {
        applicationContext = context.getApplicationContext();
    }

    public static void showToast(CharSequence message) {
        showToast(message, Toast.LENGTH_SHORT);
    }

    private static void showToast(CharSequence message, int duration) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            return;
        }
        Toast lastToast = mToast == null ? null : mToast.get();
        if (lastToast != null) {
            //防止多个Toast排队显示
            lastToast.cancel();
        }
        lastToast = Toast.makeText(applicationContext, message, duration);
        mToast = new WeakReference<>(lastToast);
        hook(lastToast);
        lastToast.show();
    }

    public static void showToast(int rId) {
        showToast(applicationContext.getString(rId));
    }


    public static void showLongToast(CharSequence message) {
        showToast(message, Toast.LENGTH_LONG);
    }

    public static void showLongToast(int rId) {
        showLongToast(applicationContext.getString(rId));
    }

    private static class SafelyHandlerWarpper extends Handler {

        private final Handler impl;

        public SafelyHandlerWarpper(Handler impl) {
            this.impl = impl;
        }

        @Override
        public void dispatchMessage(Message msg) {
            try {
                super.dispatchMessage(msg);
            } catch (Exception e) {
                //ignore
            }
        }

        @Override
        public void handleMessage(Message msg) {
            impl.handleMessage(msg);//需要委托给原Handler执行
        }
    }

    private static Field sField_TN;
    private static Field sField_TN_Handler;

    static {
        try {
            sField_TN = Toast.class.getDeclaredField("mTN");
            sField_TN.setAccessible(true);
            sField_TN_Handler = sField_TN.getType().getDeclaredField("mHandler");
            sField_TN_Handler.setAccessible(true);
        } catch (Exception e) {
            //ignore
        }
    }

    private static void hook(Toast toast) {
        try {
            Object tn = sField_TN.get(toast);
            Handler preHandler = (Handler) sField_TN_Handler.get(tn);
            sField_TN_Handler.set(tn, new SafelyHandlerWarpper(preHandler));
        } catch (Exception e) {
            //ignore
        }
    }
}
