package com.github.welcomeworld.devbase.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {
    public static final int FILE_SELECT_CODE = 233;

    public static String openAssert(Context context, String filepath) {
        try {
            InputStream inputStream = context.getAssets().open(filepath);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
            return outputStream.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static InputStream openAssertStream(Context context, String filepath) {
        try {
            return context.getAssets().open(filepath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param type like image/* or audio/* or others
     */
    public static void selectFile(Activity activity, String type) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        activity.startActivityForResult(intent, FILE_SELECT_CODE);
    }

    public static File uriToFile(Uri uri, Context context) {
        File file = null;
        if (uri == null) return null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_FILE)) {
            file = new File(uri.getPath());
        } else if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //把文件复制到沙盒目录
            ContentResolver contentResolver = context.getContentResolver();
            String displayName = System.currentTimeMillis() + Math.round((Math.random() + 1) * 1000)
                    + "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri));

//           可以获取到原文件的文件名，但是比较耗时
            String[] filePathColumn = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};
            Cursor cursor = contentResolver.query(uri, filePathColumn, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                String tempDisplayName = cursor.getString(cursor.getColumnIndex(filePathColumn[1]));
                if (!TextUtils.isEmpty(tempDisplayName)) {
                    displayName = tempDisplayName;
                }
                try {
                    String filePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
                    return new File(filePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                cursor.close();
            }
            try {
                InputStream is = contentResolver.openInputStream(uri);
                File cache = new File(context.getCacheDir().getAbsolutePath(), displayName);
                FileOutputStream fos = new FileOutputStream(cache);
                copy(is, fos);
                file = cache;
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static long copy(@NonNull InputStream in, @NonNull OutputStream out) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return FileUtils.copy(in, out);
        } else {
            int read;
            byte[] buffer = new byte[8 * 1024];
            long count = 0;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
                count += read;
            }
            out.flush();
            return count;
        }
    }

    public static Uri fileToUri(File file, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, context.getPackageName() + ".github.common.dev.file.provider", file);
        } else {
            return Uri.fromFile(file);
        }
    }
}
