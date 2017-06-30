package receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import util.ConstantValue;
import util.SpUtils;

/**
 * 监听手机重启的广播
 * Created by jules on 2017/6/30.
 */

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG="Life_boot_receiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Life","接收到手机重启的广播");
        //发送短信给选中的联系人号码
        SmsManager sms = SmsManager.getDefault();
        //5556：手机中是电话号码，模拟器中是端口号
        sms.sendTextMessage("5558",null,"sim change!",null,null);
        /*//获取手机开机后的sim卡序列号
        TelephonyManager tm = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        String simSerialNumber=tm.getSimSerialNumber();
        //获取Sp中存储的手机序列号
        String sim_number = SpUtils.getString(context, ConstantValue.SIM_NUMBER,"");
        //比对开机序列号
        if (!simSerialNumber.equals(sim_number)){
            //发送短信给选中的联系人号码
            SmsManager sms = SmsManager.getDefault();
            //5556：手机中是电话号码，模拟器中是端口号
            sms.sendTextMessage("5558",null,"sim change!",null,null);
        }*/
    }
}
