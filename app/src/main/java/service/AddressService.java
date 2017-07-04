package service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.julse.jules.kmsafe.R;

import util.ToastUtil;

/**
 * 归属地显示
 * Created by jules on 2017/7/3.
 */
public class AddressService extends Service{
    private final String TAG = "Life_AddressService";
    private ToastUtil toastUtil;
    private TelephonyManager mTM;
    private AddressService.MyPhoneStateListener myPhoneStateListener;
    @Override
    public void onCreate() {
        //第一次开启服务以后需要管理吐司的显示
        //服务关闭之后，就不监听电话
        //监听电话状态
        //--1，电话管理者对象
        mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        toastUtil=new ToastUtil(getApplication());
        //--2,监听电话状态
        myPhoneStateListener = new MyPhoneStateListener();
        mTM.listen(myPhoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        //销毁吐司
        if (myPhoneStateListener!=null){
            //取消对电话状态的监听
            mTM.listen(myPhoneStateListener,PhoneStateListener.LISTEN_NONE);
        }
        Log.i(TAG,"停止监听电话的服务");
        super.onDestroy();

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
                    Log.i(TAG,"没有活动，空闲了--------------");
                    //挂断电话时候移除吐司
                    toastUtil.closeStyleToast();
                    break;
                //摘机状态，至少一个电话活动
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.i(TAG,"拨打或者通话中--------------");
                    toastUtil.showStyleToast("illuminassion");
                    break;
                //响铃状态（展示吐司）
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.i(TAG,"响铃了--------------");
                    toastUtil.showStyleToast("illuminassion");
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }


}
