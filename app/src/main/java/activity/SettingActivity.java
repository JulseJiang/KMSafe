package activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.julse.jules.kmsafe.R;

import service.BlackNumberService;
import service.AddressService;
import util.ServiceUtil;
import util.ToastUtil;
import view.SettingItemClickView;
import view.SettingItemView;
import util.ConstantValue;
import util.SpUtils;

/**
 * Created by jules on 2017/6/28.
 */

public class SettingActivity extends Activity {
    private final String TAG= "Life_SettingActivity";
    private ToastUtil toastUtil;
    private int mToastStyle;
    private String[] mToastStyleDes;
    private SettingItemClickView siv_toast_style;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Log.i(TAG,".........SettingActivity........");
        initUpdate();
        initAdress();
        initToastStyle();
        initLocation();
        initBlackNumber();
    }

    /**
     * 双击居中view所在屏幕的位置
     */
    private void initLocation() {
        SettingItemClickView scv_location=findViewById(R.id.scv_location);
        scv_location.setTitle("归属地提示框的位置");
        scv_location.setDes("设置归属地提示框的位置");
        scv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ToastLocationActivity.class));
            }
        });
    }

    /**
     * 吐司样式自定义
     */
    private void initToastStyle() {
        siv_toast_style = findViewById(R.id.siv_toast_style);
        //话述（产品经理会提供）
        siv_toast_style.setTitle("设置归属地显示风格");
        //创建描述文字所在的string类型数组
        mToastStyleDes = new String[]{"透明","黄色","橙色","紫色","蓝色"};
        //sp获取吐司显示的风格索引值，用于获取描述内容控件
        mToastStyle = SpUtils.getInt(this, ConstantValue.TOAST_STYLE,0);
        //通过索引，获取字符串数组的文字
        siv_toast_style.setDes(mToastStyleDes[this.mToastStyle]);
        toastUtil=new ToastUtil(getApplication());
        //监听点击事件，弹出对话框
        siv_toast_style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToastStyleDialog();
            }
        });
    }

    /**
     * 显示吐司样式的对话框
     */
    private void showToastStyleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_earth);
        builder.setTitle("选择昵称样式");
        /**
         * 参数1：String 类型描述颜色的文字数组，
         * 参数2：弹出对话框时选中条目索引值
         * 参数3：触发事件：记录选中的索引值，关闭对话框
         */
        builder.setSingleChoiceItems(mToastStyleDes, mToastStyle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                //记录选中的索引值，关闭对话框，显示选中色值文字
                SpUtils.putInt(getApplication(),ConstantValue.TOAST_STYLE,i);
                dialog.dismiss();
                siv_toast_style.setDes(mToastStyleDes[i]);
                toastUtil.closeStyleToast();
                toastUtil.showStyleToast(mToastStyleDes[i]);
            }
        });
        //取消按钮
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 黑名单是否开启
     */
    private void initBlackNumber() {
        final SettingItemView siv_blacknumber =findViewById(R.id.siv_blacknumber);
        boolean isRunning = ServiceUtil.isRunning(this,"service.BlackNumberService");
        siv_blacknumber.setCheck(isRunning);
        Log.i(TAG,"--黑名单拦截是否开启："+isRunning);
        siv_blacknumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //切换选中状态
                boolean isCheck = siv_blacknumber.isCheck();
                siv_blacknumber.setCheck(!isCheck);
                if (!isCheck){
                    startService(new Intent(getApplicationContext(),BlackNumberService.class));
                }else {
                    stopService(new Intent(getApplicationContext(),BlackNumberService.class));
                }
            }
        });
    }
    /**
     * 是否手机归属地
     */
    private void initAdress() {
        final SettingItemView siv_local_phone = findViewById(R.id.siv_address);
        boolean isRunning = ServiceUtil.isRunning(this, " service.AddressService");
        Log.i(TAG,"--手机归属地是否开启："+isRunning);
        siv_local_phone.setCheck(isRunning);
        siv_local_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //切换选中状态
                boolean isCheck = siv_local_phone.isCheck();
                siv_local_phone.setCheck(!isCheck);
                if (!isCheck){
                    startService(new Intent(getApplicationContext(), AddressService.class));
                    ToastUtil.show(getApplication(),"开启服务");
                }else {
                    stopService(new Intent(getApplicationContext(), AddressService.class));
                }
                //存储选中状态
                SpUtils.putBoolean(getApplication(), ConstantValue.OPEN_LOCAL_PHONE,!isCheck);
            }
        });
    }

    /**
     * 自动更新
     */
    private void initUpdate() {
        Log.i(TAG,"R.id.siv_update:"+R.id.siv_update);
        final SettingItemView siv_update = findViewById(R.id.siv_update);
        //获取已有的开关状态，用作显示
        boolean open_update = SpUtils.getBoolean(this, ConstantValue.OPEN_UPDATE,false);
        siv_update.setCheck(open_update);
        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //切换选中状态
                boolean isCheck = siv_update.isCheck();
                siv_update.setCheck(!isCheck);
                SpUtils.putBoolean(getApplication(), ConstantValue.OPEN_UPDATE,!isCheck);
            }
        });

    }



    @Override
    protected void onPause() {
        super.onPause();
        if (toastUtil!=null){
            toastUtil.closeStyleToast();
        }
    }
}
