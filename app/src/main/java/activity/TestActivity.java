package activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;

/**
 * Created by jules on 2017/6/29.
 */
public class TestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final TextView textView = new TextView(this);
        textView.setText("测试类");
        setContentView(textView);
        //获取经纬度坐标：（LocationManager）
        //--1
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //--2
        //权限检查
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                       //位置变化
                        //经度
                        double longitude = location.getLongitude();
                        //纬度
                        double latitude = location.getLatitude();
                        textView.setText("经度：" +longitude+"  "+
                                "纬度："+latitude);

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
                });

    }
}
