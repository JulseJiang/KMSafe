package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.julse.jules.kmsafe.R;

import util.ConstantValue;
import util.SpUtils;

/**
 * Created by jules on 2017/6/29.
 */
public class SetupOverActivity extends Activity{
    private TextView tv_phone;
    private TextView tv_reset_setup;
    private TextView tv_lock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //密码输入成功，并且四个导航界面设置完成--->停留在设置完成的功能列表界面
        //密码输入成功，四个导航界面没有设置完成--->跳转到导航界面1
        boolean setup_over = SpUtils.getBoolean(this, ConstantValue.SETUP_OVER, false);
        if (setup_over){
            setContentView(R.layout.activity_setup_over);
            initUI();
        }else {
            Intent intent = new Intent(this,Setup1Activity.class);
            startActivity(intent);
            //开启新的界面以后，关闭功能列表界面
            finish();
        }
    }

    private void initUI() {
        String phone = SpUtils.getString(this,ConstantValue.CONTACT_PHONE_NUMBER,"");
        tv_phone=findViewById(R.id.tv_phone);
        tv_phone.setText(phone);
        //线程无法启动
        tv_lock=findViewById(R.id.tv_lock);
        boolean lock=SpUtils.getBoolean(this,ConstantValue.OPEN_SECURITY,false);
        if (lock){
            tv_lock.setText("已开启");
        }else {
            tv_lock.setText("已关闭");
        }
        tv_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String lock_text;
                if (tv_lock.getText().toString().equals("已开启")){
                    lock_text="已关闭";
                    SpUtils.putBoolean(getApplication(),ConstantValue.OPEN_SECURITY,false);
                }else{
                    lock_text="已开启";
                    SpUtils.putBoolean(getApplication(),ConstantValue.OPEN_SECURITY,true);
                }
                tv_lock.post(new Runnable() {
                    @Override
                    public void run() {
                        tv_lock.setText("");
                        Log.i("Life","点击了开启按钮--置空");
                    }
                });
                tv_lock.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tv_lock.setText(lock_text);
                    }
                },800);
            }
        });
        //重新设置条目被点击
        tv_reset_setup=findViewById(R.id.tv_reset_setup);
        tv_reset_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Setup1Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
