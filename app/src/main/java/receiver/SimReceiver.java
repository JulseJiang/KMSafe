package receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.util.Log;

import com.julse.jules.kmsafe.R;

import service.LocationService;
import util.ConstantValue;
import util.SpUtils;

/**
 * Created by jules on 2017/7/1.
 */

public class SimReceiver extends BroadcastReceiver {
    private String TAG="Life_SimReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"收到了短信");
        //判断手机有无开启防盗保护
        boolean open_security = SpUtils.getBoolean(context, ConstantValue.OPEN_SECURITY,false);
        if (open_security){
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

                if (messageBody.contains("#*location*#")){
                    new Intent(context, LocationService.class);
                }
            }
        }
    }
}
