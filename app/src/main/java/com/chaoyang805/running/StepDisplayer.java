package com.chaoyang805.running;

import com.chaoyang805.running.utils.LogHelper;

import java.util.ArrayList;

/**
 * Created by chaoyang805 on 2015/12/15.
 */
public class StepDisplayer implements StepListener {

    private static final String TAG = LogHelper.makeLogTag(StepDisplayer.class);

    private int mCount = 0;
    private ArrayList<Listener> mListeners = new ArrayList<>();

    public StepDisplayer(){
    }

    public void reloadSettings() {
        notifyListeners();
    }

    public interface Listener{
        public void stepsChanged(int value);
        public void passValue();
    }
    public void addListener(Listener listener) {
        mListeners.add(listener);
    }
    public void notifyListeners(){
        for (Listener listener : mListeners) {
            listener.stepsChanged(mCount);
        }
    }

    public void setSteps(int steps){
        mCount = steps;
        notifyListeners();
    }
    @Override
    public void onStep() {
        mCount++;
        notifyListeners();
    }
}
