package com.chaoyang805.running.app;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by chaoyang805 on 2015/12/1.
 */
public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
    }
}
