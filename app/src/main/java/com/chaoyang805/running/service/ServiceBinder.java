package com.chaoyang805.running.service;

import android.app.Service;
import android.os.Binder;

/**
 * Created by chaoyang805 on 2015/12/19.
 */
public class ServiceBinder extends Binder {

    private Service mService;

    public ServiceBinder(Service service){
        mService = service;

    }

    public Service getService(){
        return mService;
    }
}
