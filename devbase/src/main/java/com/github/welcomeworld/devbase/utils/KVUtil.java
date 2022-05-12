package com.github.welcomeworld.devbase.utils;

import com.tencent.mmkv.MMKV;

public class KVUtil {
    private static final MMKV kv = MMKV.defaultMMKV();

    public static void putString(String key, String value) {
        kv.putString(key, value);
    }

    public static String getString(String key) {
        return getString(key, null);
    }

    public static String getString(String key, String value) {
        return kv.getString(key, value);
    }

    public static void putInt(String key, int value) {
        kv.putInt(key, value);
    }

    public static int getInt(String key) {
        return getInt(key, 0);
    }

    public static int getInt(String key, int value) {
        return kv.getInt(key, value);
    }

    public static void putLong(String key, long value) {
        kv.putLong(key, value);
    }

    public static long getLong(String key) {
        return getLong(key, 0);
    }

    public static long getLong(String key, long value) {
        return kv.getLong(key, value);
    }

    public static void putBoolean(String key, boolean value) {
        kv.putBoolean(key, value);
    }

    public static boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public static boolean getBoolean(String key, boolean value) {
        return kv.getBoolean(key, value);
    }
}
