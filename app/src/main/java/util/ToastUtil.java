package util;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.julse.jules.kmsafe.R;

import service.AddressService;

import static android.content.Context.WINDOW_SERVICE;


public class ToastUtil {
    private static WindowManager mWM;
    /**
     * 弹出对话框的view布局
     */
    private static View mViewToast;
    private static WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    /**
     * 维护Toast，防止忘记使用show方法
     * Created by jules on 2017/6/27.
     */
    public static  void show(Context ctx,String msg){
        Toast.makeText(ctx,msg,Toast.LENGTH_SHORT).show();
    }

    public static void showStyleToast(Context ctx,String msg) {
        //获取窗体对象
        mWM = (WindowManager) ctx.getSystemService(WINDOW_SERVICE);
        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                        |WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE//默认能够被触摸
                |WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;//保持屏幕亮

        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;//响铃的时候展示，和电话类型一致
        params.setTitle("Toast");
        //指定吐司所在位置（左上角）
        params.gravity = Gravity.LEFT+Gravity.TOP;
        //吐司显示效果（解析布局文件）
        mViewToast = View.inflate(ctx, R.layout.toast_view,null);
//        TextView tv_toast=mViewToast.findViewById(R.layout.tv_toast);
        //在窗体上挂载一个view（需要权限）
        mWM.addView(mViewToast,mParams);
    }
    public static void closeStyleToast(){
        if (mWM!=null&&mViewToast!=null){
            mWM.removeView(mViewToast);
        }
    }
}
