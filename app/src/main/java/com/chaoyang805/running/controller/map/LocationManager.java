package com.chaoyang805.running.controller.map;

import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.chaoyang805.running.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chaoyang805 on 2015/9/7.
 * 对百度地图进行管理的manager类
 */
public class LocationManager implements BDLocationListener {

    private static final String TAG = "BaiduMapManager";
    /**
     * context对象
     */
    private Context mContext;
    /**
     * 百度地图对象
     */
    private BaiduMap mBaiduMap;
    /**
     * 地图控件对象
     */
    private MapView mMapView;
    /**
     * 定位client对象
     */
    private LocationClient mClient;
    /**
     * 是否是第一次定位，如果是则自动移动到屏幕中心
     */
    private boolean isFirstLocate = true;
    /**
     * 扫描间隔，默认为5min，多次定位失败后间隔越来越长
     */
    private int mScanSpanMillis = 5 * 60 * 1000;
    /**
     * 显示用户位置信息的view
     */
    private LinearLayout mUserMarkerView;
    /**
     * 持有所有的BitmapDescriptor,程序销毁时回收。
     */
    private List<BitmapDescriptor> mMarkerViews = new ArrayList<>();
    /**
     * 保存用户id和对应的marker,每次将marker添加到地图上时，也要加入到map中
     */
    private Map<String, Marker> mMarkers = new HashMap<>();

    public LocationManager(Context context, MapView mapView) {
        mContext = context;
        mMapView = mapView;
        mMapView.showScaleControl(true);
        mMapView.showZoomControls(false);
        mBaiduMap = mapView.getMap();
    }

    /**
     * 初始化地图
     */
    public void init() {
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15f));
        MyLocationConfiguration config = new MyLocationConfiguration
                (MyLocationConfiguration.LocationMode.FOLLOWING, true, null);
        mBaiduMap.setMyLocationConfigeration(config);
        mClient = new LocationClient(mContext);

        mClient.registerLocationListener(this);
        //配置mClient对象的参数信息
        LocationClientOption option = new LocationClientOption();

        option.setIsNeedLocationPoiList(true);
        //设置扫描间隔为5min
        option.setScanSpan(mScanSpanMillis);
        //开启GPS
        option.setOpenGps(true);
        option.setEnableSimulateGps(true);
        //是否需要地址
        option.setIsNeedAddress(true);
        //是否需要手机的方向
        option.setNeedDeviceDirect(true);
        //坐标类型
        option.setCoorType("bd09ll");
        mClient.setLocOption(option);

    }

    /**
     * 开始进行定位的方法
     */
    public void requestLocation() {
        mClient.start();
//        mClient.requestLocation();
    }

    /**
     * 停止请求位置
     */
    public void stopRequest() {
        if (mClient.isStarted()) {
            mClient.stop();
        }
    }

    /**
     * 定位失败的次数
     */
    private int mFailureCount = 0;

    private MyLocationData mLocationData;

    /**
     * 定位返回数据的回调
     *
     * @param bdLocation
     */
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        Log.d("TAG", "onReceiveLocation");
        if (bdLocation == null) {
            return;
        }
        //定位成功的情况
        if (bdLocation.getLocType() == 61 ||
                bdLocation.getLocType() == 65 ||
                bdLocation.getLocType() == 66 ||
                bdLocation.getLocType() == 68 ||
                bdLocation.getLocType() == 161) {
            LatLng latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            mLocationData = new MyLocationData.Builder()
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude())
                    .accuracy(bdLocation.getRadius())
                    .direction(bdLocation.getDirection())
                    .build();
            mBaiduMap.setMyLocationData(mLocationData);
            isFirstLocate = false;
            //我的位置得到了更新，将我的最新位置信息发送给服务器
            notifyListener(bdLocation);
            //重新定位成功后，恢复默认的扫描间隔
            if (mFailureCount > 0) {
                mFailureCount = 0;
                setScanSpan(mScanSpanMillis);
            }
        } else {
            mFailureCount++;
            //随着失败次数的增加，逐渐延长扫描间隔
            if (mFailureCount > 0 && mFailureCount <= 3) {
                setScanSpan(10000);
            } else if (mFailureCount > 3 && mFailureCount <= 8) {
                setScanSpan(20000);
            } else {
                setScanSpan(60000);
            }
            ToastUtils.showToast(mContext, "定位失败");
        }
    }

    private void notifyListener(BDLocation bdLocation) {
        if (mListeners.size() > 0) {
            for (OnLocationUpdateListener listener : mListeners) {
                listener.onLocationUpdate(bdLocation);
            }
        }
    }

    /**
     * 设置定位扫描间隔
     *
     * @param timeInMillis
     */
    private void setScanSpan(int timeInMillis) {
        LocationClientOption option = mClient.getLocOption();
        option.setScanSpan(timeInMillis);
        mClient.setLocOption(option);
    }

    /**
     * 位置信息更新时的回调接口
     */
    public interface OnLocationUpdateListener {
        void onLocationUpdate(BDLocation bdLocation);
    }


    private List<OnLocationUpdateListener> mListeners = new ArrayList<>();

    /**
     * 注册位置更新监听的方法
     *
     * @param listener
     */
    public void addListener(OnLocationUpdateListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(OnLocationUpdateListener listener) {
        mListeners.remove(listener);
    }

    /**
     * 暂停地图
     */
    public void onPause() {
        mMapView.onPause();
    }

    /**
     * 恢复地图
     */
    public void onResume() {
        mMapView.onResume();
    }

    /**
     * 销毁地图
     */
    public void onDestroy() {
        // 退出时销毁定位
        mClient.unRegisterLocationListener(this);
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        //销毁BitmapDescriptor
        for (BitmapDescriptor markerView : mMarkerViews) {
            markerView.recycle();
        }
        mMapView.onDestroy();
        mMapView = null;
    }

}
