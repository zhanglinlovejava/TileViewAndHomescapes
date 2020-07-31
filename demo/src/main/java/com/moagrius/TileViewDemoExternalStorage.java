package com.moagrius;

import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.Nullable;

import com.moagrius.tileview.TileView;
import com.moagrius.tileview.io.StreamProviderFiles;

import java.io.File;

public class TileViewDemoExternalStorage extends TileViewDemoActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_demos_tileview);
    frameToCenterOnReady();
    TileView tileView = findViewById(R.id.tileview);
    File sdcard = Environment.getExternalStorageDirectory();
    new TileView.Builder(tileView)
        .setSize(16384, 13056)
        .setStreamProvider(new StreamProviderFiles())
        .defineZoomLevel(sdcard.getAbsolutePath() + "/phi-1000000-%1$d_%2$d.jpg")
        .build();
  }

}
