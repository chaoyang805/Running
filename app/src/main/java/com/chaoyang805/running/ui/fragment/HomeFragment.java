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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.chaoyang805.running.R;
import com.chaoyang805.running.controller.map.LocationManager;
import com.chaoyang805.running.service.StepService;
import com.chaoyang805.running.utils.LogHelper;
import com.chaoyang805.running.utils.XmlReader;

import org.xmlpull.v1.XmlPullParserException;

import java.lang.ref.WeakReference;

/**
 * Created by chaoyang805 on 2015/11/24.
 */
public class HomeFragment extends Fragment {
    private static final String TAG = LogHelper.makeLogTag(HomeFragment.class);
    private MapView mMapView;
    private boolean isFirstLocate = true;
    private BaiduMap mBaiduMap;

    private BDLocationListener mLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            LogHelper.d(TAG, "onReceiveLocation" + bdLocation.getLocType());
            if (bdLocation == null) {
                LogHelper.d(TAG, "onReceiveLocation bdLocation == null");
                return;
            }
            //定位成功的情况
            if (bdLocation.getLocType() == 61 ||
                    bdLocation.getLocType() == 65 ||
                    bdLocation.getLocType() == 66 ||
                    bdLocation.getLocType() == 68 ||
                    bdLocation.getLocType() == 161) {
                MyLocationData locationData = new MyLocationData.Builder()
                        .latitude(bdLocation.getLatitude())
                        .longitude(bdLocation.getLongitude())
                        .accuracy(bdLocation.getRadius())
                        .direction(bdLocation.getDirection())
                        .build();
                if (isFirstLocate) {
                    LogHelper.d(TAG, "first locate");
                    LogHelper.d(TAG, "" + locationData.latitude + "  " + locationData.longitude);
                    mBaiduMap.setMyLocationData(locationData);
                    isFirstLocate = false;
                }

            }
        }
    };
    private LocationManager mLocationManager;
    private TextView mTvStepCount;

    private Context mContext;
    private StepService mStepService;

    private boolean mIsRunning = false;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mMapView = (MapView) root.findViewById(R.id.map_view);
        mTvStepCount = (TextView) root.findViewById(R.id.tv_step_count);
        mMapView.showZoomControls(false);
        mMapView.showScaleControl(true);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mLocationManager = new LocationManager(getActivity(), mMapView);
        mLocationManager.init();
        mLocationManager.requestLocation();

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
        try {
            XmlReader reader = new XmlReader();
            reader.parse(getActivity(), "2015-12-05.xml", new XmlReader.Callback() {
                @Override
                public void start(String name, String date) {
                    Log.d(TAG, String.format("name:%s,date:%s",name,date));
                }

                @Override
                public void findNext(int position, double lat, double lng) {
                    Log.d(TAG, String.format("position:%d,lat:%f,lng:%f", position, lat, lng));
                }
            });
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return root;
    }

    private void startStepService() {
        if (!mIsRunning) {
            mContext = getActivity();
            mContext.startService(new Intent(mContext, StepService.class));
            mIsRunning = true;
        }
    }

    private void bindStepService() {
        mContext.bindService(new Intent(mContext, StepService.class),
                mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindService() {
        mContext.unbindService(mServiceConnection);
    }

    private void stopService() {
        if (mStepService != null) {
            mContext.stopService(new Intent(mContext, StepService.class));
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

    private StepService.Callback mCallback = new StepService.Callback() {
        @Override
        public void stepsChanged(int value) {
            LogHelper.d(TAG, "HomeFragment_stepsChanged:" + value);
            mHandler.obtainMessage(STEPS_MSG, value, 0).sendToTarget();

        }
    };

    public void updateSteps(int value) {
        mTvStepCount.setText("" + value);
    }

    private static final int STEPS_MSG = 0x01;
    private StepServiceHandler mHandler = new StepServiceHandler(this);

    static class StepServiceHandler extends Handler {

        WeakReference<Fragment> mReference;

        public StepServiceHandler(Fragment fragment) {
            mReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case STEPS_MSG:
                    final Fragment fragment = mReference.get();
                    if (fragment != null) {
                        ((HomeFragment) fragment).updateSteps(msg.arg1);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
