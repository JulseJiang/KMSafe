package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.julse.jules.kmsafe.R;

import service.LockScreenService;
import util.ConstantValue;
import util.ServiceUtil;
import util.SpUtils;

/**
 * Created by jules on 2017/7/9.
 */
public class ProcessSettingActivity extends Activity{
    private CheckBox cb_show_system,cb_lock_clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_setting);

        initSystemShow();
        initLockScreenClear();

    }

    /**
     * 锁屏清理
     */
    private void initLockScreenClear() {
        cb_lock_clear=findViewById(R.id.cb_lock_clear);
        //回显
        boolean lockScreen = ServiceUtil.isRunning(this,"service.LockScreenService");
        //单选框的显示状态
        cb_lock_clear.setChecked(lockScreen);
        if (lockScreen){
            cb_lock_clear.setText("锁屏清理已开启");
        }
        else{
            cb_lock_clear.setText("锁屏清理已关闭");
        }
        //对选中状态监听
        cb_lock_clear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                //isChecked 作为是否选中的状态
                if (isChecked){
                    cb_lock_clear.setText("锁屏清理已开启");
                    //开启服务
                    startService(new Intent(getApplicationContext(), LockScreenService.class));
                }else {
                    cb_lock_clear.setText("锁屏清理已关闭");
                    stopService(new Intent(getApplicationContext(),LockScreenService.class));
                }
            }
        });

    }

    private void initSystemShow() {
        cb_show_system=findViewById(R.id.cb_show_system);
        //回显
        boolean showSystem = SpUtils.getBoolean(this, ConstantValue.SHOW_SYSTEM, false);
        //单选框的显示状态
        cb_show_system.setChecked(showSystem);
        if (showSystem){
            cb_show_system.setText("显示系统进程");
        }
        else{
            cb_show_system.setText("隐藏系统进程");
        }
        //对选中状态监听
        cb_show_system.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                //isChecked 作为是否选中的状态
                if (isChecked){
                    cb_show_system.setText("显示系统进程");
                }else {
                    cb_show_system.setText("隐藏系统进程");
                }
                SpUtils.putBoolean(getApplication(), ConstantValue.SHOW_SYSTEM,isChecked);
            }
        });
    }
}
