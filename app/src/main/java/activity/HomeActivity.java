package activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.julse.jules.kmsafe.R;

import org.w3c.dom.Text;

import util.ConstantValue;
import util.MD5util;
import util.SpUtils;
import util.ToastUtil;

/**
 * Created by jules on 2017/6/27.
 */
public class HomeActivity extends Activity{
    private final String TAG="Life_HomeActivity";
    private GridView gv_home;
    private LinearLayout home_root;
    private String[] mTitleStr;
    private int[] mImgInt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initUI();
        initData();
    }
    /**
     * 初始化动画，淡入
     */
    private void initAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
        alphaAnimation.setDuration(100);
//        home_root.startAnimation(alphaAnimation);
    }
    /**
     * 初始化动画，放大
     */
    private void itemAnimation() {
        ScaleAnimation sa = new ScaleAnimation(0.1f,1f,0.1f,1,
                Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        sa.setDuration(3000);
        home_root.startAnimation(sa);
    }
    private void initData() {
        //准备数据

        mTitleStr = new String[]{
                "手机防盗","通讯卫士","软件管理",
                "进程管理","流量统计","手机杀毒",
                "缓存清理","高级工具","设置中心",

        };

        mImgInt = new int[]{
                R.drawable.white_159,R.drawable.white_160,R.drawable.white_161,
                R.drawable.white_162,R.drawable.white_163,R.drawable.white_164,
                R.drawable.white_165,R.drawable.white_166,R.drawable.white_52
        };
        //九宫格控件设置数据适配器（等同ListView数据适配器）
        gv_home.setAdapter(new MyAdapter());
        //设置九宫格中的条目点击事件
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //点中列表条目的索引position
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.i(TAG,"点击了第"+position+"条");
                switch (position){
                    case 0:
                        //开启防盗功能
                        Log.i(TAG,"开启防盗功能");
                        showDialog();
                        break;
                    case 1:
                        Log.i(TAG,"跳转到通讯卫士模块");
                        Intent intent1 = new Intent(getApplicationContext(),BlackNumberActivity.class);
                        startActivity(intent1);
                        break;
                    case 2:
                        Log.i(TAG,"激活设备管理器");//此模块存在问题，不能成功激活，但是实现了卸载功能
                        Intent intent2 = new Intent(getApplicationContext(),DeviceAdminActivity.class);
                        startActivity(intent2);
                        break;
                    case 3:
                        Intent intent3 = new Intent(getApplicationContext(),TestActivity.class);
                        startActivity(intent3);
                        break;
                    case 7:
                        startActivity(new Intent(getApplicationContext(),AToolActivity.class));
                        Log.i(TAG,"打开高级工具");
                        break;
                    case 8://getApplicationContext当前上下文环境所对应的类
                        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(intent);
                        break;

                }
            }
        });
    }
    protected void showDialog(){
        //判断本地是否存储有密码，是：初次进入，没有密码，需要设置密码
        //否，非初次，有密码，需要确认密码
        String psd = SpUtils.getString(this, ConstantValue.MOBILE_SAFE_PSD, "");
        Log.i(TAG,"psd:"+psd);
        if (TextUtils.isEmpty(psd)){
            showSetPsdDealog();
        }else {
            Log.i(TAG,"弹出确认密码框");
            showConfirmPsdDialog(psd);
        }

    }

    /**
     * 设置密码对话框
     */
    private void showConfirmPsdDialog(final String psd) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog =builder.create();
        final View view = View.inflate(this,R.layout.dialog_confirm_psd,null);
        dialog.setView(view,0,0,0,0);
        dialog.show();

        Button bt_submit = view.findViewById(R.id.bt_sumit);
        Button bt_cancel = view.findViewById(R.id.bt_cancel);
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_confirm_psd = view.findViewById(R.id.et_confirm_psd);
                String confirmPsd = et_confirm_psd.getText().toString();
                Log.i(TAG,"psd"+psd);
                Log.i(TAG,"confirmPsd"+confirmPsd);
                Log.i(TAG,"MD5util.encoder(confirmPsd):"+MD5util.encoder(confirmPsd));
                if (!(TextUtils.isEmpty(confirmPsd))){
                    if (psd.equals(MD5util.encoder(confirmPsd))){
//                        Intent intent = new Intent(HomeActivity.this, TestActivity.class);
                        Intent intent = new Intent(HomeActivity.this, SetupOverActivity.class);
                        startActivity(intent);//主界面不要finish，便于回退

                    }else{
                        ToastUtil.show(HomeActivity.this,"密码错误");
                    }
                }else{
                    ToastUtil.show(HomeActivity.this,"输入的密码不能为空");
                }
                //结束当前对话框
                dialog.dismiss();
            }
        });
    }

    /**
     * 确认密码对话框
     */
    private void showSetPsdDealog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog =builder.create();
        final View view = View.inflate(this,R.layout.dialog_set_psd,null);
        dialog.setView(view);
        dialog.show();

        Button bt_submit = view.findViewById(R.id.bt_sumit);
        Button bt_cancel = view.findViewById(R.id.bt_cancel);
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_set_psd = view.findViewById(R.id.et_set_psd);
                EditText et_confirm_psd = view.findViewById(R.id.et_confirm_psd);
                String psd = et_set_psd.getText().toString();
                String confirmPsd = et_confirm_psd.getText().toString();
                Log.i(TAG,"psd"+psd);
                Log.i(TAG,"confirmPsd"+confirmPsd);
                if (!(TextUtils.isEmpty(psd)&&TextUtils.isEmpty(confirmPsd))){
                    if (psd.equals(confirmPsd)){
//                        Intent intent = new Intent(HomeActivity.this, TestActivity.class);
                        Intent intent = new Intent(HomeActivity.this, SetupOverActivity.class);
                        startActivity(intent);//主界面不要finish，便于回退
                        SpUtils.putString(HomeActivity.this,ConstantValue.MOBILE_SAFE_PSD, MD5util.encoder(psd));
                    }else{
                        ToastUtil.show(HomeActivity.this,"两次输入密码不一致");

                    }
                }else{
                    ToastUtil.show(HomeActivity.this,"两次输入的密码不能为空");
                }
                //结束当前对话框
                dialog.dismiss();
            }
        });
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
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View view= View.inflate(getApplicationContext(), R.layout.gridview_item, null);
            //有错
            Log.i(TAG,"i="+i);
            TextView tv_title= view.findViewById(R.id.tv_title);
            ImageView iv_icon = view.findViewById(R.id.iv_icon);
            tv_title.setText(mTitleStr[i]);
            iv_icon.setBackgroundResource(mImgInt[i]);
            return view;
        }

        @Override
        public CharSequence[] getAutofillOptions() {
            return new CharSequence[0];
        }
    }
    private void initUI() {
        gv_home=(GridView) findViewById(R.id.gv_home);
        home_root=findViewById(R.id.home_root);
    }
}
