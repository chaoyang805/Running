package com.chaoyang805.running.controller.map;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.model.LatLng;
import com.chaoyang805.running.controller.TrackDrawer;
import com.chaoyang805.running.model.SportsTrack;
import com.chaoyang805.running.utils.LogHelper;

/**
 * Created by chaoyang805 on 2015/12/19.
 */
public class TrackRecorder implements LocationManager.OnLocationUpdateListener {

    private static final String TAG = LogHelper.makeLogTag(TrackRecorder.class);

    private SportsTrack mTrack;

    private TrackDrawer mDrawer;

    public TrackRecorder(BaiduMap map){
        mTrack = new SportsTrack();
        mDrawer = new TrackDrawer(map);
    }
    @Override
    public void onLocationUpdate(BDLocation bdLocation) {
        mTrack.add(new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude()));
        mDrawer.draw(mTrack);
    }
}
