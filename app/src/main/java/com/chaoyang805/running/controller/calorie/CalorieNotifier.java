package com.chaoyang805.running.controller.calorie;

import com.chaoyang805.running.controller.step.StepListener;
import com.chaoyang805.running.utils.LogHelper;

/**
 * Created by chaoyang805 on 2015/12/21.
 */
public class CalorieNotifier implements StepListener {

    private static final String TAG = LogHelper.makeLogTag(CalorieNotifier.class);

    private static final int STEP_LENGTH = 40;

    private static final double RUNNING_FACTOR = 1.02784823;

    private  double mCalories = 0;

    private float mBodyWeight;

    public CalorieNotifier(Listener listener){
        mListener = listener;
    }

    public void reloadSettings(){
        mBodyWeight = 60;
        notifyListener();
    }

    @Override
    public void onStep() {
        mCalories += mBodyWeight * STEP_LENGTH * RUNNING_FACTOR / 100000.0;
        notifyListener();
    }

    private void notifyListener() {
        if (mListener != null) {
            LogHelper.d(TAG, "calorie value changed:" + mCalories);
            mListener.valueChanged((float) mCalories);
        }
    }

    private Listener mListener;

    public interface Listener{
        void valueChanged(float value);
        void passValue();
    }
}
