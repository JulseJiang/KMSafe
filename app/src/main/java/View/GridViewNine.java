package view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;

/**
 * Created by jules on 2017/6/29.
 */

public class GridViewNine extends GridView implements View.OnTouchListener {
    public GridViewNine(Context context) {
        super(context);
    }

    public GridViewNine(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGridMatrix();
    }

    private void initGridMatrix() {
        GrideItem item ;
        for (int i = 0 ;i<3;i++){
            for (int j = 0;i<3;j++){
                item = new GrideItem(getContext(),"",0);
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }
}
