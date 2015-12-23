package com.chaoyang805.running.ui.fragment;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.chaoyang805.running.R;
import com.chaoyang805.running.controller.map.LocationManager;
import com.chaoyang805.running.controller.map.TrackDrawer;
import com.chaoyang805.running.service.ServiceBinder;
import com.chaoyang805.running.service.StepService;
import com.chaoyang805.running.service.TrackService;
import com.chaoyang805.running.ui.view.MainDetailTextView;
import com.chaoyang805.running.utils.LogHelper;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by chaoyang805 on 2015/11/24.
 */
public class HomeFragment extends Fragment {
    private static final String TAG = LogHelper.makeLogTag(HomeFragment.class);
    private Context mContext;

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LocationManager mLocationManager;

    private MainDetailTextView mTvStepCount;
    private MainDetailTextView mTvCalories;
    private MainDetailTextView mTvDistance;
    private StepService mStepService;
    private boolean mIsRunning = false;

    private ServiceConnection mStepConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mStepService = ((StepService.StepBinder) service).getService();
            mStepService.addCallback(mCallback);
            mStepService.reloadSettings();
            LogHelper.d(TAG, "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mStepService = null;
        }
    };
    private TrackService mTrackService;
    private ServiceConnection mTrackConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogHelper.d(TAG, "onTrackService connected");
            mTrackService = (TrackService) ((ServiceBinder) service).getService();

            mLocationManager.addListener(mTrackService);

            mDrawer = new TrackDrawer(mBaiduMap);

            mTrackService.addCallback(mTrackCallback);

            mBaiduMap.clear();

            mDrawer.drawAll(mTrackService.getTrack());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mLocationManager.removeListener(mTrackService);
            mTrackService = null;
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mTvStepCount = (MainDetailTextView) root.findViewById(R.id.tv_step_count);
        mTvCalories = (MainDetailTextView) root.findViewById(R.id.tv_calorie);
        mTvDistance = (MainDetailTextView) root.findViewById(R.id.tv_distance);

        mMapView = (MapView) root.findViewById(R.id.map_view);

        mMapView.showZoomControls(true);
        mMapView.showScaleControl(true);
        mBaiduMap = mMapView.getMap();
//        mBaiduMap.setMyLocationEnabled(true);
        mLocationManager = new LocationManager(getActivity(), mMapView);
        mLocationManager.init();

        mLocationManager.requestLocation();
/**
 //        try {
 //            long start = System.currentTimeMillis();
 //            XmlWriter writer = new XmlWriter();
 //            writer.start("chaoyang805", "2015.12.04 21:12:52");
 //            for (int i = 0; i < 5; i++) {
 //                writer.addPath(new LatLng(114.2342, 22.5322));
 //            }
 //            writer.end();
 //            writer.write(getActivity(), "2015-12-05.xml");
 //            long end = System.currentTimeMillis();
 //            Log.d(TAG, "total time = " + (end - start));
 //        } catch (IOException e) {
 //            e.printStackTrace();
 //        }
 //        try {
 //            XmlReader reader = new XmlReader();
 //            reader.parse(getActivity(), "2015-12-05.xml", new XmlReader.Callback() {
 //                @Override
 //                public void start(String name, String date) {
 //                    Log.d(TAG, String.format("name:%s,date:%s",name,date));
 //                }
 //
 //                @Override
 //                public void findNext(int position, double lat, double lng) {
 //                    Log.d(TAG, String.format("position:%d,lat:%f,lng:%f", position, lat, lng));
 //                }
 //            });
 //        } catch (XmlPullParserException e) {
 //            e.printStackTrace();
 //        }
 */
        return root;
    }

    private void startStepService() {
        if (!mIsRunning) {
            mContext = getActivity();
            mContext.startService(new Intent(mContext, StepService.class));
            mContext.startService(new Intent(mContext, TrackService.class));
            mIsRunning = true;
        }
    }

    private void bindStepService() {
        mContext.bindService(new Intent(mContext, StepService.class),
                mStepConnection, Context.BIND_AUTO_CREATE);
        mContext.bindService(new Intent(mContext, TrackService.class),
                mTrackConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindService() {
        mContext.unbindService(mStepConnection);
        mContext.unbindService(mTrackConnection);
    }

    private void stopService() {
        if (mStepService != null) {
            mContext.stopService(new Intent(mContext, StepService.class));
            mContext.stopService(new Intent(mContext, TrackService.class));
            mIsRunning = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mLocationManager.onResume();

        if (!mIsRunning) {
            startStepService();
            bindStepService();
        } else {
            bindStepService();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mLocationManager.onPause();
        unbindService();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationManager.onDestroy();
        mHandler.removeMessages(STEPS_MSG);
    }

    private TrackDrawer mDrawer;
    private TrackService.Callback mTrackCallback = new TrackService.Callback() {
        @Override
        public void onDrawNextTrack(List<LatLng> points) {
            mDrawer.drawPoints(points);
        }
    };

    private StepService.Callback mCallback = new StepService.Callback() {
        @Override
        public void stepsChanged(int value) {
            LogHelper.d(TAG, "HomeFragment_stepsChanged:" + value);
            mHandler.obtainMessage(STEPS_MSG, value, 0).sendToTarget();
        }

        @Override
        public void calorieChanged(float value) {
            mHandler.obtainMessage(CALORIE_MSG, (int) value, 0).sendToTarget();
        }
    };

    public void updateSteps(int value) {
        mTvStepCount.setText("" + value);
    }
    private void updateCalories(int calories) {
        LogHelper.d(TAG, "updateCalories:" + calories);
        mTvCalories.setText("" + calories);
    }
    private static final int STEPS_MSG = 0x01;
    private static final int CALORIE_MSG = 0x02;


    private StepServiceHandler mHandler = new StepServiceHandler(this);

    static class StepServiceHandler extends Handler {

        WeakReference<Fragment> mReference;

        public StepServiceHandler(Fragment fragment) {
            mReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final Fragment fragment = mReference.get();
            switch (msg.what) {
                case STEPS_MSG:
                    if (fragment != null) {
                        ((HomeFragment) fragment).updateSteps(msg.arg1);
                    }
                    break;
                case CALORIE_MSG:
                    if (fragment != null) {
                        ((HomeFragment) fragment).updateCalories(msg.arg1);
                    }
                    break;
                default:
                    break;
            }
        }
    }

}
