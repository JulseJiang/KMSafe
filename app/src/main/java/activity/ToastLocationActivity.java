package activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.julse.jules.kmsafe.R;

import util.ConstantValue;
import util.SpUtils;
import util.ToastUtil;

/**
 * Created by jules on 2017/7/3.
 */
public class ToastLocationActivity extends Activity{
    private Button bt_top,bt_bottom;
    private LinearLayout l_drag;
    private WindowManager mWM;
    private RelativeLayout.LayoutParams layoutParams;
    private long mHits[] = new long[2];
    /**
     * 屏幕可见宽高
     */
    private int mScreenHeight,mSreenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast_location);
        initUI();
    }

    private void initUI() {

        l_drag=findViewById(R.id.l_drag);
        bt_top = findViewById(R.id.bt_top);
        bt_bottom=findViewById(R.id.bt_bottom);
        mWM= (WindowManager) getSystemService(WINDOW_SERVICE);
        mScreenHeight = mWM.getDefaultDisplay().getHeight();
        mSreenWidth = mWM.getDefaultDisplay().getWidth();
        int locationX= SpUtils.getInt(this,ConstantValue.LOCATION_X,0);
        int locationY= SpUtils.getInt(this,ConstantValue.LOCATION_Y,0);
        int index = SpUtils.getInt(this, ConstantValue.TOAST_STYLE, 0);
        l_drag.setBackgroundResource(ToastUtil.mToastColor[index]);
//        <!--包含图片的LinearLayout在相对布局中，所在位置规则需要相对布局提供-->
        layoutParams=new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = locationX;
        layoutParams.topMargin = locationY;
        //将以上规则作用在l_drag上
        l_drag.setLayoutParams(layoutParams);
        placeBt(locationY);
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

                        //当前控件所在屏幕的（左，上,右，下）角的位置
                        int left = l_drag.getLeft()+disX;
                        int top = l_drag.getTop()+disY;
                        int right = l_drag.getRight()+disX;
                        int bottom = l_drag.getBottom()+disY;

                        //容错处理
                        if (left<0||
                                top<0||
                                right>mSreenWidth||
                                bottom>mScreenHeight-22){//22:通知栏高度
                            return true;
                        }
                        placeBt(top);
                        //告知移动的控件，按照计算出来的坐标展示
                        l_drag.layout(left,top,right,bottom);
                        //重置坐标
                        startX= (int) event.getRawX();
                        startY= (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP:
                        //双击居中
                        System.arraycopy(mHits,1,mHits,0,mHits.length-1);
                        mHits[mHits.length-1]= SystemClock.uptimeMillis();
                        if (mHits[mHits.length-1]-mHits[0]<500){
                            Log.i("Life","双击事件");
                            //当前控件所在屏幕的（左，上,右，下）角的位置
                            left = (mSreenWidth-l_drag.getWidth())/2;
                            top = (mScreenHeight-22-l_drag.getHeight())/2;
                            right =left+l_drag.getWidth();
                            bottom = top+l_drag.getHeight();
                            //告知移动的控件，按照计算出来的坐标展示
                            l_drag.layout(left,top,right,bottom);
                            //重置坐标
                            startX= (int) event.getRawX();
                            startY= (int) event.getRawY();
                            placeBt(top);
                        }

                        //记录左上角的点，通过控件的宽高计算其他点的值。存储移动到的位置
                        SpUtils.putInt(getApplication(), ConstantValue.LOCATION_X,l_drag.getLeft());
                        SpUtils.putInt(getApplication(), ConstantValue.LOCATION_Y,l_drag.getTop());
                        break;

                }
                return true;//false:不响应事件，
                // 或者既响应点击事件，又响应触摸事件Onclick（实质就是ACTION_UP事件）
                //让OnClick返回true
            }
        });
    }
    public void placeBt(int locationY){
        if (locationY>(mScreenHeight-22-l_drag.getHeight())/2){
            bt_bottom.setVisibility(View.INVISIBLE);
            bt_top.setVisibility(View.VISIBLE);
        }else {
            bt_bottom.setVisibility(View.VISIBLE);
            bt_top.setVisibility(View.INVISIBLE);
        }
    }
}
