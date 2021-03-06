package com.moagrius.tileview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

public class DiskCache implements TileView.BitmapCache {

  private static final String DIRECTORY_NAME = "tileview-cache";
  private static final int IO_BUFFER_SIZE = 8 * 1024;

  private DiskLruCache mDiskCache;
  private Set<String> mIndex = new HashSet<>();

  public DiskCache(Context context, int size) throws IOException {
    File directory = new File(context.getCacheDir(), DIRECTORY_NAME);
    mDiskCache = DiskLruCache.open(directory, 1, 1, size);
  }

  @Override
  public Bitmap put(String key, Bitmap data) {
    if (contains(key)) {
      return data;
    }
    DiskLruCache.Editor editor = null;
    try {
      editor = mDiskCache.edit(key);
      if (editor != null) {
        if (writeBitmapToCache(data, editor)) {
          mDiskCache.flush();
          editor.commit();
          mIndex.add(key);
        } else {
          editor.abort();
        }
      }
    } catch (IOException e) {
      try {
        if (editor != null) {
          editor.abort();
        }
      } catch (IOException ignored) {
        //
      }
    }
    return data;
  }

  @Override
  public Bitmap get(String key) {
    DiskLruCache.Snapshot snapshot = null;
    try {
      snapshot = mDiskCache.get(key);
      if (snapshot == null) {
        return null;
      }
      InputStream inputStream = snapshot.getInputStream(0);
      if (inputStream != null) {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, IO_BUFFER_SIZE);
        return BitmapFactory.decodeStream(bufferedInputStream);
      }
    } catch (IOException e) {
      // no op
    } finally {
      if (snapshot != null) {
        snapshot.close();
      }
    }
    return null;
  }

  @Override
  public Bitmap remove(String key) {
    try {
      mDiskCache.remove(key);
      mIndex.remove(key);
    } catch (IOException e) {
      // no op
    }
    return null;
  }

  @Override
  public boolean has(String key) {
    return mIndex.contains(key);
  }

  /**
   * Note this is different from MemoryCache.clear, which simply emptie the in-memory map.
   *
   * Since, presumably, disk-level caching is meant to be persistent, only call this when you're done with the
   * DiskCache, as it will nt only delete all the file content, but also close (and therefore make inaccessible)
   * the cache itself.  This might be appriate for a `finish` event (altough most likely not), or perhaps a
   * "delete cache" or "delete app contents" setting or preference UI.
   */
  @Override
  public synchronized void clear() {
    try {
      mDiskCache.delete();
    } catch (IOException e) {
      Log.d("TileView", "failed to delete disk cache: " + e.getMessage());
    }
  }

  private boolean writeBitmapToCache(Bitmap bitmap, DiskLruCache.Editor editor) {
    OutputStream outputStream = null;
    try {
      outputStream = editor.newOutputStream(0);
      outputStream = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);
      return bitmap.compress(CompressFormat.PNG, 0, outputStream);
    } catch (Exception e) {
      // no op
    } finally {
      try {
        if (outputStream != null) {
          outputStream.close();
        }
      } catch (IOException e) {
        // no op
      }
    }
    return false;
  }

  private boolean contains(String key) {
    boolean contained = false;
    DiskLruCache.Snapshot snapshot = null;
    try {
      snapshot = mDiskCache.get(key);
      contained = snapshot != null;
    } catch (IOException e) {
      // no op
    } finally {
      if (snapshot != null) {
        snapshot.close();
      }
    }
    return contained;
  }

}
