package com.moagrius;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.moagrius.tileview.TileView;
import com.moagrius.widget.ScalingScrollView;

public class TileViewDemoAssets extends TileViewDemoActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_demos_tileview);
    TileView tileView = findViewById(R.id.tileview);
    frameToCenterOnReady();
    tileView.setMaximumScale(10f);
    tileView.setMinimumScaleMode(ScalingScrollView.MinimumScaleMode.CONTAIN);
    new TileView.Builder(tileView)
        .setSize(16384, 13056)
        .defineZoomLevel("bg/phi-1000000-%1$d_%2$d.jpg")
        .build();
  }

}
