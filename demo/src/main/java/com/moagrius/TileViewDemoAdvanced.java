package com.moagrius;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.graphics.Path;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.moagrius.helpers.FlyingAnimationHelper;
import com.moagrius.tileview.TileView;
import com.moagrius.tileview.plugins.MarkerPlugin;
import com.moagrius.tileview.plugins.ScalingMarkerPlugin;

import java.util.ArrayList;

public class TileViewDemoAdvanced extends AppCompatActivity {


    private ArrayList<int[]> mLocations = new ArrayList<>();

    {
        mLocations.add(new int[]{3000, 3300});
        mLocations.add(new int[]{4800, 2400});
        mLocations.add(new int[]{1500, 2600});
        mLocations.add(new int[]{3300, 1600});
    }

    private boolean mIsRestoring;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demos_tileview);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        TileView tileView = findViewById(R.id.tileview);
        mIsRestoring = savedInstanceState != null;
        new TileView.Builder(tileView)
                .setSize(7680, 4096)
                .defineZoomLevel("bg/tile-%1$d_%2$d.jpg")
                .installPlugin(new MarkerPlugin(this))
                .installPlugin(new ScalingMarkerPlugin(this))
                .addReadyListener(this::onReady)
                .build();
    }

    private void onReady(TileView tileView) {
        ScalingMarkerPlugin scalingMarkerPlugin = tileView.getPlugin(ScalingMarkerPlugin.class);
        addMarker(scalingMarkerPlugin);
        // 添加鲸鱼
        LottieAnimationView whaleView = addLottieView(scalingMarkerPlugin, "lottie/whale/whale.json",
                "lottie/whale/images", -1, 3200, 2200);
        updateViewSize(700, 600, whaleView);
        addBirdView("lottie/bird1.json", scalingMarkerPlugin, 8000, 1000);// 老鹰
        addBirdView("lottie/bird2.json", scalingMarkerPlugin, 6000, 4000);//凤凰
        scrollTo(tileView);
        if (!mIsRestoring) {
            int[] location = mLocations.get(0);
            tileView.scrollTo(location[0], location[1]);
        }
    }

    private View.OnClickListener buildClickListener() {
        return view -> {
            LottieAnimationView lottieView = (LottieAnimationView) view;
            int id = lottieView.getId();
            switch (id) {
                case 1:
                    lottieView.setAnimation("lottie/game_build.json");
                    lottieView.setImageAssetsFolder("lottie/build");
                    lottieView.setRepeatCount(0);
                    break;
                case 2:
                    lottieView.setAnimation("lottie/puddle_jumper/puddle_jumper.json");
                    lottieView.setImageAssetsFolder("lottie/puddle_jumper/images");
                    lottieView.setRepeatCount(-1);
                    updateViewSize(1000, 1000, lottieView);
                    break;
                case 3:
                    lottieView.setAnimation("lottie/open_gift_before.json");
                    lottieView.setImageAssetsFolder("lottie/open_gift_before");
                    lottieView.setRepeatCount(-1);
                    break;
                case 4:
                    lottieView.setAnimation("lottie/sapphire1/sapphire.json");
                    lottieView.setImageAssetsFolder("lottie/sapphire1/images");
                    lottieView.setRepeatCount(-1);
                    break;
            }
            lottieView.playAnimation();
            addAnimationListener(lottieView);
        };
    }

    private void addAnimationListener(LottieAnimationView lottieView) {
        lottieView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {


            }

            @SuppressLint("ResourceType")
            @Override
            public void onAnimationEnd(Animator animation) {
                if (lottieView.getId() == 1) {
                    lottieView.setImageResource(R.drawable.build);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void addMarker(ScalingMarkerPlugin scalingMarkerPlugin) {
        View.OnClickListener markerClickListener = buildClickListener();
        int count = 0;
        for (int[] location : mLocations) {
            int x = location[0];
            int y = location[1];

            LottieAnimationView lottieView = addLottieView(scalingMarkerPlugin,
                    "lottie/game_edit_land.json", "lottie/game_edit_land", -1, x, y);
            lottieView.setTag(location);
            lottieView.setId(count + 1);
            lottieView.setOnClickListener(markerClickListener);
            count++;
        }
    }

    private void addBirdView(String jsonName,
                             ScalingMarkerPlugin scalingMarkerPlugin,
                             int left, int top) {
        LottieAnimationView birdView = addLottieView(scalingMarkerPlugin, jsonName, "", -1, left, top);
        birdView.post(() -> FlyingAnimationHelper.create()
                .setSource(birdView)
                .setDuration(10000)
                .setFlyRadius(700)
                .setRepeatCount(-1)
                .setDirection(Path.Direction.CCW)
                .setTimeInterpolator(new LinearInterpolator())
                .start());

    }


    private LottieAnimationView addLottieView(ScalingMarkerPlugin scalingMarkerPlugin,
                                              String jsonPath, String imageFolder,
                                              int repeatCount, int left, int top) {

        LottieAnimationView lottieAnimationView = new LottieAnimationView(this);
        lottieAnimationView.setAnimation(jsonPath);
        if (!TextUtils.isEmpty(imageFolder)) {
            lottieAnimationView.setImageAssetsFolder(imageFolder);
        }
        lottieAnimationView.setRepeatCount(repeatCount);
        lottieAnimationView.playAnimation();
        scalingMarkerPlugin.addMarker(lottieAnimationView, left, top, -0.5f, -0.6f, 0, 0);
        return lottieAnimationView;
    }

    private void scrollTo(TileView tileView) {
        tileView.setScale(0.2f);
        int x = mLocations.get(0)[0];
        int y = mLocations.get(0)[1];
        tileView.smoothScrollTo(x - tileView.getWidth() / 2, y - tileView.getHeight() / 2);
    }

    private void updateViewSize(int width, int height, LottieAnimationView view) {
        ScalingMarkerPlugin.LayoutParams sl = (ScalingMarkerPlugin.LayoutParams) view.getLayoutParams();
        sl.width = width;
        sl.height = height;
        view.setLayoutParams(sl);
    }


}
