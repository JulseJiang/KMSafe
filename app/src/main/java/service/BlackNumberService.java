package service;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.SmsMessage;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import db.dao.BlackNumberDao;
import receiver.DeviceAdmin;

/**
 * Created by jules on 2017/7/2.
 */

public class BlackNumberService extends Service {
    private String TAG="Life_BService";
    private BlackNumberDao mDao;
    private InnerSmsReceiver mInnerSmsReceiver;
    private TelephonyManager mTM;
    private MyPhoneStateListener myPhoneStateListener;
    private MyContentObserver myContentObserver;
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
        mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //--2,监听电话状态
        myPhoneStateListener = new BlackNumberService.MyPhoneStateListener();
        mTM.listen(myPhoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);
        //监听电话状态
        super.onCreate();
    }


    @Override
    public void onDestroy() {
        Log.i("Life","关闭BlackNumberService");
        //注销广播
        if (mInnerSmsReceiver!=null){
            unregisterReceiver(mInnerSmsReceiver);
        }
        //注销内容观察者
        if (myContentObserver!=null){
            getContentResolver().unregisterContentObserver(myContentObserver);
        }
        //取消对电话状态的监听
        if (myPhoneStateListener!=null){
            mTM.listen(myPhoneStateListener,PhoneStateListener.LISTEN_NONE);
        }
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
    /**
     * 添加触发方式
     */
    public class MyPhoneStateListener extends PhoneStateListener{
//        手动重写电话状态发生改变会触发的方法

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state){
                //空闲状态，没有任何活动
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.i(TAG,"没有活动，空闲了***************");
                    //挂断电话时候移除吐司

                    break;
                //摘机状态，至少一个电话活动
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.i(TAG,"拨打或者通话中***************");

                    break;
                //响铃状态（展示吐司）
                case TelephonyManager.CALL_STATE_RINGING:
                    //idel文件中有挂断电话的api：endCall
                    Log.i(TAG,"响铃了***************");
                    endCall(incomingNumber);
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    private void endCall(String phone) {
        int mode = mDao.getMode(phone);
        if (mode==2||mode==3){
            try {
                //1.获取ServiceManager字节码文件
                Class<?> clazz = Class.forName("android.os.ServiceManager");
                //2.获取方法
                Method method = clazz.getMethod("getService", String.class);
                //3.反射调用此方法
                IBinder iBinder = (IBinder) method.invoke(null, Context.TELECOM_SERVICE);
                //4.调用获取aidl文件对象方法
//                ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
                //5.调用在aidl中隐藏的endCall方法
//                iTelephony.endCall();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            //6.在内容解析器上注册内容观察者，通过内容观察者，观察数据库（Uri决定表和库）的变化
            myContentObserver = new MyContentObserver(new Handler(), phone);
            getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"),
                    true,//模糊匹配
            myContentObserver);
        }
    }
    class MyContentObserver extends ContentObserver {
        private String phone;
        public MyContentObserver(Handler handler,String phone) {
            super(handler);
            this.phone=phone;
        }

        @Override
        public void onChange(boolean selfChange) {
            //7..删除次被拦截的电话号码的通信记录（权限）
            getContentResolver().delete(Uri.parse("content://call_log/calls"),
                    "number=?",
                    new String[]{phone});
            super.onChange(selfChange);

        }

    }

}
