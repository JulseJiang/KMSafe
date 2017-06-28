package activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


import com.julse.jules.kmsafe.R;

/**
 * Created by jules on 2017/6/27.
 */
public class HomeActivity extends Activity{
    private GridView gv_home;
    private String[] mTitleStr;
    private int[] mImgInt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initUI();
        initData();
    }

    private void initData() {
        //准备数据

        mTitleStr = new String[]{
                "手机防盗","通讯卫士","软件管理",
                "进程管理","流量统计","手机杀毒",
                "缓存清理","手机防盗","手机防盗",

        };

        mImgInt = new int[]{
                R.drawable.white_159,R.drawable.white_160,R.drawable.white_161,
                R.drawable.white_162,R.drawable.white_163,R.drawable.white_164,
                R.drawable.white_165,R.drawable.white_166,R.drawable.white_167,R.drawable.purple_38

        };
        //九宫格控件设置数据适配器（等同ListView数据适配器）
        gv_home.setAdapter(new MyAdapter());
    }
    class MyAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return mTitleStr.length;
        }

        @Override
        public Object getItem(int i) {
            return mTitleStr[i];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View view= View.inflate(getApplicationContext(), R.layout.gridview_item, null);
            //有错
            TextView tv_title= (TextView)view.findViewById(R.id.tv_title);
            ImageView iv_icon =(ImageView)view.findViewById(R.id.iv_icon);
            tv_title.setText(mTitleStr[i]);
            iv_icon.setBackgroundResource(mImgInt[i]);
            return null;
        }

        @Override
        public CharSequence[] getAutofillOptions() {
            return new CharSequence[0];
        }
    }
    private void initUI() {
        gv_home=(GridView) findViewById(R.id.gv_home);
    }
}
