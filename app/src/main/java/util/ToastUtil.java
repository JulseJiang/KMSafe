package util;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
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
    private int mScreenHeight,mSreenWidth;
    public static int[] mToastColor=new int[]{R.color.colorNone,R.color.colorYellow,R.color.colorOrange,
            R.color.colorAccent,R.color.colorBlue};
//    private static int[] mIcon=new int[]{R.drawable.minman,R.drawable.white_52,
//    R.drawable.toast_bg,R.drawable.ic_earth,R.drawable.ic_phone,R.drawable.buttom_ok};
    /**
     * 弹出对话框的view布局
     */
    private  View mViewToast;
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
        mScreenHeight = mWM.getDefaultDisplay().getHeight();
        mSreenWidth = mWM.getDefaultDisplay().getWidth();
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
        params.x=SpUtils.getInt(ctx,ConstantValue.LOCATION_X,0);
        params.y=SpUtils.getInt(ctx,ConstantValue.LOCATION_Y,0);
        //吐司显示效果（解析布局文件）
        mViewToast = View.inflate(ctx, R.layout.toast_view,null);
        TextView tv_toast=mViewToast.findViewById(R.id.tv_toast);
        final LinearLayout l_drag = mViewToast.findViewById(R.id.l_drag);
        l_drag.setOnTouchListener(new View.OnTouchListener() {
            private int startX,startY;
            //对不同的事件做不同的逻辑处理
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX= (int) event.getRawX();
                        startY= (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int moveX = (int) event.getRawX();
                        int moveY= (int) event.getRawY();

                        //移动的位置
                        int disX = moveX-startX;
                        int disY = moveY-startY;
                        params.x=params.x+disX;
                        params.y=params.y+disY;

                        if (params.x<0){
                            params.x=0;
                        }
                        if (params.y<0){
                            params.y=0;
                        }
                        if(params.x>mSreenWidth-l_drag.getWidth()){
                            params.x=mSreenWidth-l_drag.getWidth();
                        }
                        if (params.y>mScreenHeight-22-l_drag.getHeight()){
                            params.y=mScreenHeight-22-l_drag.getHeight();
                        }
                        mWM.updateViewLayout(l_drag,params);
                        //重置坐标
                        startX= (int) event.getRawX();
                        startY= (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP:
                        //记录左上角的点，通过控件的宽高计算其他点的值。存储移动到的位置
                        SpUtils.putInt(ctx, ConstantValue.LOCATION_X,params.x);
                        SpUtils.putInt(ctx, ConstantValue.LOCATION_Y,params.y);
                        break;

                }
                return true;//false:不响应事件，
                // 或者既响应点击事件，又响应触摸事件Onclick（实质就是ACTION_UP事件）
                //让OnClick返回true
            }
        });
        tv_toast.setText(msg);
        int index = SpUtils.getInt(ctx, ConstantValue.TOAST_STYLE, 0);
//        设置颜色有问题，始终是黑色
//        tv_toast.setTextColor(mToastColor[index]);
        l_drag.setBackgroundResource(mToastColor[index]);
        //在窗体上挂载一个view（需要权限）
        mWM.addView(mViewToast,params);
    }
    public  void closeStyleToast(){
        //存在都非空但没有添加的状态
        if (mWM!=null&&mViewToast!=null){
            mWM.removeView(mViewToast);
//            mWM.removeViewImmediate(mViewToast);
        }
    }

}
