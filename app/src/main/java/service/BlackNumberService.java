package service;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.util.Log;

import db.dao.BlackNumberDao;
import receiver.DeviceAdmin;
import util.TelePhonyManager;

/**
 * Created by jules on 2017/7/2.
 */

public class BlackNumberService extends Service {
    private BlackNumberDao mDao;
    private InnerSmsReceiver mInnerSmsReceiver;
    private TelePhonyManager mTM;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i("Life","开启BlackNumberService");
        //拦截短信
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(1000);//设置优先级。1000是最大的优先级
        //拦截短信
        mInnerSmsReceiver = new InnerSmsReceiver();
        registerReceiver(mInnerSmsReceiver,intentFilter);

        //电话管理者对象
//        mTM= (TelePhonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        //监听电话状态
        super.onCreate();
    }


    @Override
    public void onDestroy() {
        Log.i("Life","关闭BlackNumberService");
        super.onDestroy();
    }

    private class InnerSmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取短信内容：可能同时收到的多条短信
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            //循环遍历短信
            for (Object object : objects) {
                //获取短信对象
                Log.i("Life", "正在遍历短信");
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
                //获取短信对象的基本信息
                String originatingAdress = sms.getOriginatingAddress();
                String messageBody = sms.getMessageBody();
                mDao= BlackNumberDao.getInstance(context);
                int mode = mDao.getMode(originatingAdress);
                if (mode==1||mode==3){
                    //拦截短信，终止广播
                    abortBroadcast();
                    Log.i("Life",originatingAdress+"发送的短信已拦截");
                }
            }
        }
    }
    class MyPhoneStateListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state){
//                case TelephoneManager.Cal
            }
        }
    }
}
