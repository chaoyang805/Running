package com.chaoyang805.running.controller.step;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

/**
 * Created by chaoyang805 on 2015/12/15.
 */
public class StepDetector implements SensorEventListener {

    private static final String TAG = "StepDetector";
    private float mLimit = 10;
    private float mLastValues[] = new float[3 * 2];
    private float mLastDirections[] = new float[3 * 2];

    private float mScale[] = new float[2];
    private float mYOffset;

    private float mLastExtremes[][] = {new float[3 * 2],new float[3 * 2]};
    private float mLastDiff[] = new float[3 * 2];
    private int mLastMatch = -1;

    private ArrayList<StepListener> mStepListeners = new ArrayList<>();

    public StepDetector(){
        int h = 480;
        mYOffset = h * 0.5f;
        mScale[0] = -(mYOffset * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = -(mYOffset * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
    }

    public void setSensitivity(float sensitivity) {
        mLimit = sensitivity;
    }

    public void addStepListener(StepListener listener) {
        mStepListeners.add(listener);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        synchronized (this) {
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float vSum = 0;
                for (int i = 0; i < 3; i++) {
                    final float v = mYOffset + event.values[i] * mScale[1];
                    vSum += v;
                }
                int k = 0;
                float v = vSum / 3;
                //direction的值如果v大于最前一次的值则为1小于为-1，等于为0
                float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
                //如果方向发生变化
                if (direction == -mLastDirections[k]) {
                    int extType = direction > 0 ? 0 : 1;
                    mLastExtremes[extType][k] = mLastValues[k];
                    float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);
                    if (diff > mLimit) {

                        boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k] * 2 / 3);
                        boolean isPreviousLargeEnough = mLastDiff[k] > (diff / 3);
                        boolean isNotContra = (mLastMatch != 1 - extType);

                        if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                            for (StepListener stepListener : mStepListeners) {
                                stepListener.onStep();
                            }
                            mLastMatch = extType;
                        } else {
                            mLastMatch = -1;
                        }
                    }
                    mLastDiff[k] = diff;
                }
                mLastDirections[k] = direction;
                mLastValues[k] = v;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}
