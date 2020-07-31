package com.moagrius.helpers;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by zhanglin on 2020/7/31.
 */
public class FlyingAnimationHelper {

    private View mSource;
    private TimeInterpolator mTimeInterpolator;
    private OnCompleteListener mCompleteListener;
    private int mFlyRadius = 600;
    private int mRepeatCount = 0;
    private Path.Direction mDirection = Path.Direction.CCW;

    private int mDuration = 500;

    private FlyingAnimationHelper() {
    }

    public static FlyingAnimationHelper create() {
        return new FlyingAnimationHelper();
    }

    public FlyingAnimationHelper setRepeatCount(int mRepeatCount) {
        this.mRepeatCount = mRepeatCount;
        return this;
    }

    public FlyingAnimationHelper setSource(View source) {
        mSource = source;
        return this;
    }

    public FlyingAnimationHelper setFlyRadius(int mFlyRadius) {
        this.mFlyRadius = mFlyRadius;
        return this;
    }

    public FlyingAnimationHelper setDirection(Path.Direction direction) {
        this.mDirection = direction;
        return this;
    }

    public FlyingAnimationHelper setDuration(int duration) {
        this.mDuration = duration;
        return this;
    }

    public FlyingAnimationHelper setTimeInterpolator(TimeInterpolator timeInterpolator) {
        mTimeInterpolator = timeInterpolator;
        return this;
    }

    public void setCompleteListener(OnCompleteListener completeListener) {
        mCompleteListener = completeListener;
    }

    public void start() {

        if (mSource == null) {
            return;
        }
        if (mTimeInterpolator == null) {
            mTimeInterpolator = new AccelerateInterpolator();
        }
        startInternal();
    }

    private void startInternal() {
        int startLoc[] = new int[2];
        mSource.getLocationInWindow(startLoc);
        float startX = startLoc[0];
        float startY = startLoc[1];
        Path path = new Path();
        path.moveTo(startX, startY);
        path.addCircle(startX - mFlyRadius, startY, mFlyRadius, mDirection);
        final PathMeasure mPathMeasure = new PathMeasure(path, false);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, mPathMeasure.getLength());
        valueAnimator.setDuration(mDuration);
        valueAnimator.setRepeatCount(mRepeatCount);
        // 匀速线性插值器

        valueAnimator.setInterpolator(mTimeInterpolator);


        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                float[] mCurrentPosition = new float[2];
                mPathMeasure.getPosTan(value, mCurrentPosition, null);//mCurrentPosition此时就是中间距离点的坐标值
                // 移动的商品图片（动画图片）的坐标设置为该中间点的坐标
                mSource.setX(mCurrentPosition[0]);
                mSource.setY(mCurrentPosition[1]);
                float rotation = 360 / mPathMeasure.getLength() * value;
                mSource.setRotation(-rotation);
            }
        });
        // 五、 开始执行动画
        valueAnimator.start();

        // 六、动画结束后的处理
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            //当动画结束后：
            @Override
            public void onAnimationEnd(Animator animation) {

                if (mCompleteListener != null) {
                    mCompleteListener.onComplete();
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

    public interface OnCompleteListener {
        void onComplete();
    }
}
