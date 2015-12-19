package com.chaoyang805.running.model;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by chaoyang805 on 2015/12/19.
 */
public class SportsTrack {

    private String mAccount;

    private String mDate;


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

    public SportsTrack() {
        mPaths = new LinkedList<>();
    }

    public void add(LatLng latLng) {
        mPaths.addLast(latLng);
    }

    public LatLng getLast() {
        return mPaths.getLast();
    }

    public List<LatLng> getLastPath() {
        if (mPaths.size() < 2) {
//            throw new IndexOutOfBoundsException("Do not has enough points");
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
