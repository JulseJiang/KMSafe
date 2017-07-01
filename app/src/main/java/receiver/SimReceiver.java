package receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.util.Log;

import com.julse.jules.kmsafe.R;

import service.LocationService;
import util.ConstantValue;
import util.SpUtils;
import util.ToastUtil;

/**
 * Created by jules on 2017/7/1.
 */

public class SimReceiver extends BroadcastReceiver {
    private String TAG="Life_SimReceiver";
    private ComponentName mDeviceAdminSample;
    private DevicePolicyManager mDPM;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"收到了短信");
        //判断手机有无开启防盗保护
        boolean open_security = SpUtils.getBoolean(context, ConstantValue.OPEN_SECURITY,false);
        if (open_security){
            //组件对象可以作为是否激活的判断标志，参数为：上下文环境，广播接收者对应的字节码文件
            mDeviceAdminSample = new ComponentName(context, DeviceAdmin.class);
            mDPM= (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            //获取短信内容：可能同时收到的多条短信
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            //循环遍历短信
            for (Object object :objects){
                //获取短信对象
                Log.i(TAG,"正在遍历短信");
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
                //获取短信对象的基本信息
                String originatingAdress = sms.getOriginatingAddress();
                String messageBody = sms.getMessageBody();
                Log.i(TAG,"originatingAdress:"+originatingAdress);
                Log.i(TAG,"messageBody:"+messageBody);
                //判断是否播放背景音乐
                if (messageBody.contains("#*alarm*#")){
                    Log.i("Life","开始播放音乐");
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.alarm_music);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
                //发送定位信息
                if (messageBody.contains("#*location*#")){
                    //service可以不依赖程序存在
                    context.startService(new Intent(context, LocationService.class));
                }
                //清除数据
                if (messageBody.contains("#*wipedata*#")){
                    if (mDPM.isAdminActive(mDeviceAdminSample)){
//                    危险！不要使用真机调试，模拟器调试的时候，不能清除数据，只能重启
                        //清除手机数据
//                    mDPM.wipeData(0);
                        //清除内存卡数据
//                    mDPM.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
                    }else {
                        ToastUtil.show(context,"请先激活设备");
                    }
                }
                //远程锁屏
                if (messageBody.contains("#*lockscreen*#")){
                    if (mDPM.isAdminActive(mDeviceAdminSample)){
                        //激活的情况下才能实现锁屏
                        mDPM.lockNow();
                    }else {
                        ToastUtil.show(context,"请先激活设备");
                    }
                }
            }
        }
    }
}
