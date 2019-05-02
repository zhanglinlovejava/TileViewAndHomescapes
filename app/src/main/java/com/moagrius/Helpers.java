package com.moagrius;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Helpers {

  public static final String EXTERNAL_STORAGE_KEY = "external";
  public static final String INTERNAL_STORAGE_KEY = "internal";

  private static final String PREFS_FILE_NAME = "preferences";

  public static void copyStreams(InputStream inputStream, OutputStream outputStream) throws IOException {
    try {
      int data = inputStream.read();
      while (data != -1) {
        outputStream.write(data);
        data = inputStream.read();
      }
    } finally {
      inputStream.close();
      outputStream.close();
    }
  }

  private static void copyAssetTilesToDirectory(Context context, File destination) throws Exception {
    Log.d("TV", "about to copy asset tiles to " + destination);
    AssetManager assetManager = context.getAssets();
    String[] assetPaths = assetManager.list("tiles");
    for (String assetPath : assetPaths) {
      InputStream assetStream = assetManager.open("tiles/" + assetPath);
      File dest = new File(destination, assetPath);
      FileOutputStream outputStream = new FileOutputStream(dest);
      copyStreams(assetStream, outputStream);
      Log.d("TV", assetPath + " copied to " + dest);
    }
    Log.d("TV", "done copying files");
  }

  public static void saveBooleanPreference(Context context, String key, boolean value) {
    SharedPreferences preferences = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();
    editor.putBoolean(key, value);
    editor.apply();
  }

  public static boolean getBooleanPreference(Context context, String key) {
    return context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE).getBoolean(key, false);
  }

  public static void copyAssetTilesToInternalStorage(Context context) throws Exception {
    File directory = context.getFilesDir();
    Log.d("TV", "copying to internal storage: " + directory);
    copyAssetTilesToDirectory(context, directory);
    saveBooleanPreference(context, INTERNAL_STORAGE_KEY, true);
  }

  public static void copyAssetTilesToExternalStorage(Context context) throws Exception {
    File sdcard = Environment.getExternalStorageDirectory();
    Log.d("TV", "copying to SD card: " + sdcard);
    copyAssetTilesToDirectory(context, sdcard);
    saveBooleanPreference(context, EXTERNAL_STORAGE_KEY, true);
  }

}
