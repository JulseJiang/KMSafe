package util;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.julse.jules.kmsafe.R;

import service.AddressService;

import static android.content.Context.WINDOW_SERVICE;


public class ToastUtil {
    private  WindowManager mWM;
    private Context ctx;
    private  int[] mToastColor=new int[]{R.color.colorWhite,R.color.colorYellow,R.color.colorOrange,
            R.color.colorAccent,R.color.colorBlue};
//    private static int[] mIcon=new int[]{R.drawable.minman,R.drawable.white_52,
//    R.drawable.toast_bg,R.drawable.ic_earth,R.drawable.ic_phone,R.drawable.buttom_ok};
    /**
     * 弹出对话框的view布局
     */
    private static View mViewToast;
    private static WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    public ToastUtil(Context ctx){
        this.ctx=ctx;
    }
    /**
     * 维护Toast，防止忘记使用show方法
     * Created by jules on 2017/6/27.
     */
    public static  void show(Context ctx,String msg){
        Toast.makeText(ctx,msg,Toast.LENGTH_SHORT).show();
    }

    public  void showStyleToast(String msg) {
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
        TextView tv_toast=mViewToast.findViewById(R.id.tv_toast);
        LinearLayout l_drag = mViewToast.findViewById(R.id.l_drag);
        tv_toast.setText(msg);
        int index = SpUtils.getInt(ctx, ConstantValue.TOAST_STYLE, 0);
//        设置颜色有问题，始终是黑色
//        tv_toast.setTextColor(mToastColor[index]);
        if (index!=0){
            l_drag.setBackgroundResource(mToastColor[index]);
        }
        //在窗体上挂载一个view（需要权限）
        mWM.addView(mViewToast,mParams);
    }
    public  void closeStyleToast(){

        if (mWM!=null&&mViewToast!=null){
            mWM.removeView(mViewToast);
//            mWM.removeViewImmediate(mViewToast);
        }
    }

}
