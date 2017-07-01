package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.julse.jules.kmsafe.R;
import util.ConstantValue;
import util.SpUtils;
import util.ToastUtil;

/**
 * Created by jules on 2017/6/30.
 */
public class Setup4Activity extends BaseSetupActivity{
    private CheckBox cb_box;
    private CheckBox cb_send;
    private EditText et_send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting4);
        Log.i("Life","打开了第四个界面");
        initUI();
    }

    @Override
    public void showNextPage() {
        //给选中的联系人发送短信，测试联系人是否有效
        if (cb_send.isChecked()){
            String text =et_send.getText().toString();
            if (TextUtils.isEmpty(text)){
                text=et_send.getHint().toString();
            }
            //            ---测试代码
            SmsManager sms = SmsManager.getDefault();
            String phone = SpUtils.getString(this,ConstantValue.CONTACT_PHONE_NUMBER,"");
            //5556：手机中是电话号码，模拟器中是端口号
            sms.sendTextMessage(phone,null,text,null,null);
//            测试结束代码---
        }
        boolean open_security = SpUtils.getBoolean(this, ConstantValue.OPEN_SECURITY, false);
        if (open_security){
            Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
            startActivity(intent);
            finish();
            SpUtils.putBoolean(this, ConstantValue.SETUP_OVER,true);
            //开启平移动画
//            overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
        }else {
            ToastUtil.show(this,"没有开启防盗措施");
        }
    }

    @Override
    public void showPrePage() {
        Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
        startActivity(intent);
        finish();
//        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    private void initUI() {
        cb_box = findViewById(R.id.cb_box);
        cb_send=findViewById(R.id.cb_send);
        et_send=findViewById(R.id.et_send);
        //回显
        boolean open_security = SpUtils.getBoolean(this, ConstantValue.OPEN_SECURITY, false);
        cb_box.setChecked(open_security);
        if (open_security){
            cb_box.setText("安全防盗已开启");
        }else {
            cb_box.setText("安全防盗已关闭");
        }
        //监听改变
        cb_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            //boolean b：点击之后的状态
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SpUtils.putBoolean(getApplication(),ConstantValue.OPEN_SECURITY,b);
                if (b){
                    cb_box.setText("安全防盗已开启");
                }else {
                    cb_box.setText("安全防盗已关闭");
                }
            }
        });
    }
}
