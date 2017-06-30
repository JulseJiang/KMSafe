package view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.julse.jules.kmsafe.R;

import util.ConstantValue;
import util.SpUtils;

/**
 * Created by jules on 2017/6/28.
 */

@SuppressWarnings("unused")
public class SettingItemView extends RelativeLayout {
    private final  String NAMESPACE="http://schemas.android.com/apk/res/com.julse.jules.kmsafe";
    private final String TAG= "Life_SettingItemView";
    private TextView tv_title;
    private TextView tv_des;
    private  CheckBox cb_box;
    private Context context;
    private String mDestitle;
    private String mDesoff;
    private String mDeson;
    public SettingItemView(Context context) {
        super(context,null);
        Log.i(TAG,".........SettingItemView(1)........");
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs,0);
        Log.i(TAG,".........SettingItemView(2)........");
        //xml-->view  this需要挂载在别的界面中
        View.inflate(context, R.layout.setting_item_view,this);
//        等效于
/*      View view = View.inflate(context, R.layout.setting_item_view,null);
        this.addView(view);*/
        tv_title = findViewById(R.id.tv_title);
        tv_des = findViewById(R.id.tv_des);
        cb_box = findViewById(R.id.cb_box);
        //获取自定义以及原生属性，卸载此处AttributeSet attrs对象中获取
        initAttrs(attrs);
        //获取布局文件中定义的字符串赋值给自定义文字的标题
        tv_title.setText(mDestitle);
        tv_des.setText(mDesoff);
    }

    private void initAttrs(AttributeSet attrs) {
        Log.i(TAG,"获取自定义属性个数：attrs.getAttributeCount()"+attrs.getAttributeCount());
//        获取属性名称以及属性值
        /*for (int i=0;i<attrs.getAttributeCount();i++){
            Log.i(TAG,"attrs.getAttributeName(i)="+attrs.getAttributeName(i));
            Log.i(TAG,"attrs.getAttributeValue(i)="+attrs.getAttributeValue(i));
            Log.i(TAG,"---------------------------------------------------");
        }*/
        //通过名空间+属性名获取属性值

        mDestitle = attrs.getAttributeValue(NAMESPACE,"destitle");
        mDesoff = attrs.getAttributeValue(NAMESPACE,"desoff");
        mDeson = attrs.getAttributeValue(NAMESPACE,"deson");
        Log.i(TAG,"mDestitle"+mDestitle);
        Log.i(TAG,"mDesoff"+mDesoff);
        Log.i(TAG,"mDeson"+mDeson);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.i(TAG,".........SettingItemView(3)........");
/*        //xml-->view  this需要挂载在别的界面中
        View.inflate(context, R.layout.setting_item_view,this);
//        等效于
*//*      View view = View.inflate(context, R.layout.setting_item_view,null);
        this.addView(view);*//*
        tv_title = findViewById(R.id.tv_title);
        tv_des = findViewById(R.id.tv_des);
        cb_box = findViewById(R.id.cb_box);*/

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
            tv_des.setText(mDeson);
//            SpUtils.putBoolean(context, item,true);
        }else {
            tv_des.setText(mDesoff);
//            SpUtils.putBoolean(context, item,false);
        }
    }
}
