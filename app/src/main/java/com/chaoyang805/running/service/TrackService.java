package com.chaoyang805.running.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.chaoyang805.running.controller.map.LocationManager;
import com.chaoyang805.running.model.SportsTrack;

import java.util.List;

/**
 * Created by chaoyang805 on 2015/12/19.
 */
public class TrackService extends Service implements LocationManager.OnLocationUpdateListener {
    private SportsTrack mTrack;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return new ServiceBinder(this);
    }

    @Override
    public void onCreate() {
        mTrack = new SportsTrack();
    }

    @Override
    public void onDestroy() {
    }

    public SportsTrack getTrack(){
        return mTrack;
    }

    public interface Callback{
        void onDrawNextTrack(List<LatLng> points);
    }

    private Callback mCallback = null;
    public void addCallback(Callback callback){
        mCallback = callback;
    }
    @Override
    public void onLocationUpdate(BDLocation bdLocation) {
        mTrack.add(new LatLng(bdLocation.getLatitude(),
                bdLocation.getLongitude()));
        if (mCallback != null) {
            mCallback.onDrawNextTrack(mTrack.getLastPath());
        }
    }
}

