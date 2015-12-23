package com.chaoyang805.running.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.chaoyang805.running.controller.calorie.CalorieNotifier;
import com.chaoyang805.running.controller.step.StepDetector;
import com.chaoyang805.running.controller.step.StepDisplayer;
import com.chaoyang805.running.utils.LogHelper;

/**
 * Created by chaoyang805 on 2015/12/15.
 */
public class StepService extends Service {

    private static final String TAG = LogHelper.makeLogTag(StepService.class);

    private SensorManager mSensorManager;
    private StepDetector mStepDetector;
    private StepDisplayer mStepDisplayer;

    private CalorieNotifier mCalorieNotifier;

    private Callback mCallback;
    private Sensor mSensor;

    private int mSteps;

    private float mCalories;
    private CalorieNotifier.Listener mCalorieListener = new CalorieNotifier.Listener() {
        @Override
        public void valueChanged(float value) {
            mCalories = value;
            passValue();
        }

        @Override
        public void passValue() {
            if (mCallback != null) {
                mCallback.calorieChanged(mCalories);
            }
        }
    };

    private StepDisplayer.Listener mStepListener = new StepDisplayer.Listener() {
        @Override
        public void stepsChanged(int value) {
            mSteps = value;
            LogHelper.i(TAG, "stepsChanged:" + mSteps);
            passValue();
        }

        @Override
        public void passValue() {
            if (mCallback != null) {
                mCallback.stepsChanged(mSteps);
            }
        }
    };

    public void addCallback(Callback callback) {
        mCallback = callback;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new StepBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mStepDetector = new StepDetector();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(mStepDetector, mSensor, SensorManager.SENSOR_DELAY_FASTEST);

        mStepDisplayer = new StepDisplayer();
        mStepDisplayer.addListener(mStepListener);
        mStepDetector.addStepListener(mStepDisplayer);

        mCalorieNotifier = new CalorieNotifier(mCalorieListener);
        mStepDetector.addStepListener(mCalorieNotifier);

    }

    public void reloadSettings() {
        if (mStepDisplayer != null) mStepDisplayer.reloadSettings();
        if (mCalorieNotifier != null) mCalorieNotifier.reloadSettings();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterDetector();
    }

    private void unregisterDetector() {
        mSensorManager.unregisterListener(mStepDetector);
    }

    public interface Callback {
        void stepsChanged(int value);

        void calorieChanged(float value);
    }

    public class StepBinder extends Binder {
        public StepService getService() {
            return StepService.this;
        }
    }

}
