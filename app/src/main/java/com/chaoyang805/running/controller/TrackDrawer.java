package com.chaoyang805.running.controller;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.chaoyang805.running.controller.map.TrackRecorder;
import com.chaoyang805.running.model.SportsTrack;
import com.chaoyang805.running.utils.LogHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chaoyang805 on 2015/12/19.
 */
public class TrackDrawer {
    private static final String TAG = LogHelper.makeLogTag(TrackRecorder.class);
    private BaiduMap mBaiduMap;

    public TrackDrawer(BaiduMap map) {
        mBaiduMap = map;
    }

    public void draw(SportsTrack track) {
        List<LatLng> points = track.getLastPath();
        drawPoints(points);
    }

    public void drawPoints(List<LatLng> points) {
        if (points != null && points.size() >= 2) {
            LogHelper.d(TAG, "drawPoints_" + points.size());
            OverlayOptions ooPolyline = new PolylineOptions().width(10)
                    .color(0xAAFF0000).points(points);
            Polyline mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
        }
    }

    public void clear() {
        mBaiduMap.clear();
    }

    public void drawAll(SportsTrack track) {
        List<LatLng> points = new ArrayList<>(track.getPaths());
        drawPoints(points);
    }
}
