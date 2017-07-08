package util;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Debug;
import android.util.Log;

import com.julse.jules.kmsafe.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import db.domain.ProcessInfo;

/**
 * Created by jules on 2017/7/6.
 */

public class ProcessInfoProvider {
    /**
     * 获取进程总数
     * @param ctx
     * @return
     */
    public static int getProcessCount(Context ctx){
        //1.获取activityManager
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //2.获取正在运行进程的集合
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        //3.返回集合总数
        return runningAppProcesses.size();
    }

    /**
     *
     * @param ctx
     * @return 返回可用的内存数 bytes
     */
    public static long getAvailSpace(Context ctx){
        //1.获取activityManager
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //2.构建存储可用内存的对象
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //3.给memoryInfo对象赋值
        am.getMemoryInfo(memoryInfo);
        //4.获取memoryInfo的可用内存大小
        return  memoryInfo.availMem;
    }
    /**
     *
     * @param ctx
     * @return 返回可用的内存数 bytes
     */
    public static long getTotalSpace(Context ctx){
       /* //1.获取activityManager
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //2.构建存储可用内存的对象
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //3.给memoryInfo对象赋值
        am.getMemoryInfo(memoryInfo);
        //4.获取memoryInfo的可用内存大小
        return  memoryInfo.totalMem;
        //不能兼容低版本，兼容api16以上版本*/

        //内存大小已经写入文件中，读取proc/meinfo文件，读取第一行，获取数字字符，转换为bytes，返回
        FileReader fileReader = null;
        BufferedReader bufferedReader=null;
        long result=0;
        try {
            fileReader = new FileReader("proc/meminfo");
            bufferedReader = new BufferedReader(fileReader);
            String lineOne = bufferedReader.readLine();
            //将字符串转换为字符数组
            char[] charArray = lineOne.toCharArray();
            //循环遍历每一个字符，如果此字符的ASCII码在0到9的区域，说明此字符有效
            StringBuffer stringBuffer = new StringBuffer();
            for (char c:charArray) {
                if (c>='0'&&c<='9'){
                    stringBuffer.append(c);
                }
            }
            Log.i("Life"," 进程总长度"+Long.parseLong(stringBuffer.toString()));
            result=Long.parseLong(stringBuffer.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            Log.i("Life","运行了Finally");
            if (fileReader!=null&&bufferedReader!=null){
                try {
                    fileReader.close();
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return result;
        }

    }

    /**
     * 获取正在进行的进程信息集合
     * @param ctx
     * @return
     */
    public static  List<ProcessInfo> getProcessInfo(Context ctx){
        //获取进程相关信息
        ArrayList<ProcessInfo> processInfoList = new ArrayList<>();
        //1.activityManager管理者对象和packageManager管理者对象
        ActivityManager am = (ActivityManager)ctx.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = ctx.getPackageManager();
        //2.获取正在运行的进程的集合
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        //3.循环遍历以上集合，获取相关信息
        for (ActivityManager.RunningAppProcessInfo info : runningAppProcesses){
            ProcessInfo processInfo = new ProcessInfo();
            //4.获取进程名称==应用的包名
            processInfo.packageName = info.processName;
            //5.pid:进程唯一标识
            Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{info.pid});
            //6.返回数组中索引位置为0的对象，为当前进程的内存信息的对象
            Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
            //7.获取已使用的大小 byte*1024=kb
            processInfo.memSize = memoryInfo.getTotalSharedDirty()*1024;
            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(processInfo.packageName, 0);
                //8.获取应用名称
                processInfo.name=applicationInfo.loadLabel(pm).toString();
                //9.获取应用图标
                processInfo.icon=applicationInfo.loadIcon(pm);
                //10.判断是否为系统进程
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)==ApplicationInfo.FLAG_SYSTEM){
                    processInfo.isSystem = true;
                }else {
                    processInfo.isSystem=false;
                }
            } catch (PackageManager.NameNotFoundException e) {
                //通过包名找不到对应的应用,需要处理
                processInfo.name=info.processName;
                processInfo.icon = ctx.getResources().getDrawable(R.drawable.ic_earth);
                processInfo.isSystem=true;
                e.printStackTrace();
            }
            processInfoList.add(processInfo);
        }
        return processInfoList;
    }
}
