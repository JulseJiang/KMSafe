package util;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by jules on 2017/7/3.
 */

public class ServiceUtil {
    /**
     *
     * @param ctx       上下文环境
     * @param serviceName   判断是否正在运行的服务
     * @return true 运行，false,没有运行
     */
    public static boolean isRunning(Context ctx, String serviceName){
        //获取activityMananger管理者对象，可以获取当前手机正在运行的所有服务
        ActivityManager mAM = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //后去手机中正在运行的服务集合
        List<ActivityManager.RunningServiceInfo> runningServices = mAM.getRunningServices(100);//手机最多有100个服务，超过会卡死
        //遍历
        for (ActivityManager.RunningServiceInfo runningService:runningServices) {
            //获取每一个真正运行服务的名称
            if (serviceName.equals(runningService.service.getClassName())){
                return true;
            }
        }
        return false;
    }
}
