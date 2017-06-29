package View;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.julse.jules.kmsafe.R;

/**
 * Created by jules on 2017/6/28.
 */

@SuppressWarnings("unused")
public class SettingItemView extends RelativeLayout {
    private final String TAG= "Life_SettingItemView";
    private TextView tv_title;
    private TextView tv_des;
    private  CheckBox cb_box;
    public SettingItemView(Context context) {
        super(context,null);
        Log.i(TAG,".........SettingItemView(1)........");
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs,0);
        Log.i(TAG,".........SettingItemView(2)........");
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.i(TAG,".........SettingItemView(3)........");
        //xml-->view  this需要挂载在别的界面中
        View.inflate(context, R.layout.setting_item_view,this);
//        等效于
/*      View view = View.inflate(context, R.layout.setting_item_view,null);
        this.addView(view);*/
        tv_title = findViewById(R.id.tv_title);
        tv_des = findViewById(R.id.tv_des);
        cb_box = findViewById(R.id.cb_box);

    }

    /**
     * 返回当前SettingItemView是否为选中状态
     * @return true：选中，false:未选中
     */
    public boolean isCheck(){
        return cb_box.isChecked();
    }

    /**
     * 设置自动更新是否开启
     * @param isCheck
     */
    public void setCheck(boolean isCheck){
        cb_box.setChecked(isCheck);
        if (isCheck){
            tv_des.setText("自动更新已开启");
        }else {
            tv_des.setText("自动更新已关闭");
        }
    }
}
