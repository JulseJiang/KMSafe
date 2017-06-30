package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by jules on 2017/6/30.
 */

public abstract class BaseSetupActivity extends Activity {
    private GestureDetector gestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //监听手势移动：velocityX：在x轴上的移动速度
                if (e1.getX() - e2.getX() > 0) {
                    //由右向左移动到下一页,调用子类的下一页方法，抽象方法，让子类实现
                    showNextPage();
                } else if (e1.getX() - e2.getX() < 0) {
                    //由左向右移动到下一页
                    showPrePage();
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }
    /**
     * 重写：监听屏幕上响应的事件类型（按下（1），移动（多），抬起（1））
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //接受多种类型的事件，用作处理的方法
        //用类管理手势
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    /**
     * 下一页
     */
    public abstract void showNextPage();

    /**
     * 上一页
     */
    public abstract void showPrePage();
    //公有方法可以传递到子类
    public void nextPage(View view){
        showNextPage();
    }
    public void prePage(View view){
        showPrePage();
    }
}
