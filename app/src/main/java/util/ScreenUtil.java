package util;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
/**
 * 屏幕工具类
 * 获取屏幕相关参数
 * @author jules
 *
 */
public class ScreenUtil {
    /**
     *
     * @param context
     * @returnp metrics屏幕宽高
     */
    public static DisplayMetrics getScreenSize(Context context){
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(metrics);
        return metrics;
    }
    /**
     *
     * @param context
     * @return density 屏幕density
     */
    public static float getDeviceDensity(Context context){
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics.density;
    }
}