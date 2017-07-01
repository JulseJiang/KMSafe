package activity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.julse.jules.kmsafe.R;

import receiver.DeviceAdmin;

/**
 * Created by jules on 2017/7/1.
 */

public class DeviceAdminActivity extends Activity{
    private  Button bt_start,bt_lock,bt_wipedata,bt_uninstall;
    private ComponentName mDeviceAdminSample;
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
        mDeviceAdminSample = new ComponentName(this, DeviceAdmin.class);
        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,mDeviceAdminSample);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"设备管理器");
                startActivity(intent);
            }
        });

    }
}
