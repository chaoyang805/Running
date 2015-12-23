package com.chaoyang805.running.model;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.chaoyang805.running.utils.LogHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by chaoyang805 on 2015/12/19.
 */
public class SportsTrack {

    private static final int MIN_UPDATE_DISTANCE = 3;
    private static final String TAG = LogHelper.makeLogTag(SportsTrack.class);

    private String mAccount;

    private String mDate;

    public SportsTrack() {
        mPaths = new LinkedList<>();
    }

    public LinkedList<LatLng> getPaths() {
        return mPaths;
    }

    private LinkedList<LatLng> mPaths;

    public String getAccount() {
        return mAccount;
    }

    public void setAccount(String account) {
        mAccount = account;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public boolean add(LatLng latLng) {
        if (mPaths.size() <= 0) {
            LogHelper.d(TAG, "add First location");
            mPaths.addLast(latLng);
            return true;
        }
        double dis = DistanceUtil.getDistance(mPaths.getLast(), latLng);
        LogHelper.d(TAG, "dis = " + dis);
        if (dis >= MIN_UPDATE_DISTANCE) {
            LogHelper.d(TAG, "add new location:" + latLng.latitude + " " + latLng.longitude);
            mPaths.addLast(latLng);
            return true;
        }
        LogHelper.d(TAG, "too close return!");
        return false;
    }

    public LatLng getLast() {
        return mPaths.getLast();
    }

    public void addALl(List<LatLng> list){
        mPaths.clear();
        mPaths.addAll(list);
    }

    public List<LatLng> getLastPath() {
        if (mPaths.size() < 2) {
            return null;
        } else {
            List<LatLng> result = new ArrayList<>(2);
            result.add(mPaths.get(mPaths.size() - 2));
            result.add(mPaths.getLast());
            return result;
        }
    }

    public void clear() {
        mPaths.clear();
    }

    public int size() {
        return mPaths.size();
    }
}
