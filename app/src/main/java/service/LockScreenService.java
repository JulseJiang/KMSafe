package service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import util.ProcessInfoProvider;

/**
 * Created by jules on 2017/7/9.
 */
public class LockScreenService extends Service{
    private IntentFilter intentFilter;
    private InnerReceiver innerReceiver;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        innerReceiver = new InnerReceiver();
        registerReceiver(innerReceiver,intentFilter);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (innerReceiver!=null){
            unregisterReceiver(innerReceiver);
            Log.i("Life","关闭锁屏杀进程");
        }
        super.onDestroy();
    }

    private class InnerReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //清理手机正在运行的进程
            ProcessInfoProvider.killAll(context);
        }
    }
}
