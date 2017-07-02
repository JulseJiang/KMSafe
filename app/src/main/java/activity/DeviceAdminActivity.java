package activity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.julse.jules.kmsafe.R;

import receiver.DeviceAdmin;
import util.ToastUtil;

/**
 * Created by jules on 2017/7/1.
 */

public class DeviceAdminActivity extends Activity{
    private final String TAG="Life_DeviceAdmin";
    private  Button bt_start,bt_lock,bt_wipedata,bt_uninstall;
    private ComponentName mDeviceAdminSample;
    private DevicePolicyManager mDPM;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_admin);
        initUI();
    }

    private void initUI() {
        bt_start = findViewById(R.id.bt_start);
        bt_lock=findViewById(R.id.bt_lock);
        bt_wipedata=findViewById(R.id.bt_wipedata);
        bt_uninstall=findViewById(R.id.bt_uninstall);
        //组件对象可以作为是否激活的判断标志，参数为：上下文环境，广播接收者对应的字节码文件
        mDeviceAdminSample = new ComponentName(this, DeviceAdmin.class);
        mDPM= (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        //安装软件后可以在设备管理器中手动激活，此按钮可省
        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"点击设备开启按钮");
                //隐式开启意图
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,mDeviceAdminSample);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"设备管理器");
                startActivity(intent);
                Log.i(TAG,"intent为空么:"+(intent==null));
            }
        });
        bt_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDPM.isAdminActive(mDeviceAdminSample)){
                    //激活的情况下才能实现锁屏
                    mDPM.lockNow();
                }else {
                    ToastUtil.show(getApplication(),"请先激活设备");
                }
            }
        });
        bt_wipedata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDPM.isAdminActive(mDeviceAdminSample)){
//                    危险！不要使用真机调试，模拟器调试的时候，不能清除数据，只能重启
                    //清除手机数据
//                    mDPM.wipeData(0);
                    //清除内存卡数据
//                    mDPM.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
                }else {
                    ToastUtil.show(getApplication(),"请先激活设备");
                }
            }
        });
        bt_uninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("android.intent.action.DELETE");
                intent.addCategory("android.intent.category.DEFAULT");
                //卸载本应用：getPackageName()：根据包名卸载程序
                intent.setData(Uri.parse("package:"+getPackageName()));
                startActivity(intent);
            }
        });
    }
}
