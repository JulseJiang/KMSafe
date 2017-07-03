package view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.julse.jules.kmsafe.R;

/**
 * Created by jules on 2017/6/28.
 */

@SuppressWarnings("unused")
public class SettingItemClickView extends RelativeLayout {
    private final String TAG= "Life_SettingClickView";
    private TextView tv_title;
    private TextView tv_des;
    private ImageView iv_arrow;

    public SettingItemClickView(Context context) {
        super(context,null);
        Log.i(TAG,".........SettingItemView(1)........");

    }

    public SettingItemClickView(Context context, AttributeSet attrs) {
        super(context, attrs,0);
        Log.i(TAG,".........SettingItemView(2)........");
        //xml-->view  this需要挂载在别的界面中
        View.inflate(context, R.layout.setting_item_click_view,this);
//        等效于
/*      View view = View.inflate(context, R.layout.setting_item_view,null);
        this.addView(view);*/
        tv_title = findViewById(R.id.tv_title);
        tv_des = findViewById(R.id.tv_des);
        iv_arrow = findViewById(R.id.iv_arrow);

    }

    public SettingItemClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.i(TAG,".........SettingItemView(3)........");

    }

    /**
     *
     * @param title 设置标题内容
     */
    public void setTitle(String title){
        tv_title.setText(title);
    }

    /**
     *
     * @param des 设置描述内容
     */
    public void setDes(String des){
        tv_des.setText(des);
    }
}
