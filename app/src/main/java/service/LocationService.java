package service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;

import util.ConstantValue;
import util.SpUtils;

/**
 * 不启动Activity情况下，以最优方式获取经纬度
 * Created by jules on 2017/7/1.
 */
public class LocationService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        //第一次启动服务调用
        //--1，获取位置管理者对象
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        //--2,以最优方式获取经纬度
        Criteria criteria = new Criteria();
        //允许花费
        criteria.setCostAllowed(true);
        //指定获取获得经纬度的精度
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //选择最合适的方式定位
        String bestProvider = lm.getBestProvider(criteria, true);
        //--3,在一定时间间隔，移动一定距离后获取经纬度坐标

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        lm.requestLocationUpdates(bestProvider, 0, 0, new MyLocationListener());
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //反复启动服务时调用
        return super.onStartCommand(intent, flags, startId);
    }
    class MyLocationListener implements LocationListener{
        @Override
        public void onLocationChanged(Location location) {
            //位置变化
            //经度
            double longitude = location.getLongitude();
            //纬度
            double latitude = location.getLatitude();
            SmsManager sms = SmsManager.getDefault();
            String phone = SpUtils.getString(getApplication(), ConstantValue.CONTACT_PHONE_NUMBER,"");
            if (TextUtils.isEmpty(phone)){
                sms.sendTextMessage(phone,null,"My phone is here:"+"longitude:" +
                        longitude+
                        "latitude" +
                        longitude,null,null);
                Log.i("life-LocationSer","经纬度发送成功");
            }

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            //GPS状态发生切换的事件监听

        }

        @Override
        public void onProviderEnabled(String s) {
            //
        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }

}
