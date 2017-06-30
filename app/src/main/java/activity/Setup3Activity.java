package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.julse.jules.kmsafe.R;

import util.ConstantValue;
import util.SpUtils;
import util.ToastUtil;

/**
 * Created by jules on 2017/6/30.
 */
public class Setup3Activity extends BaseSetupActivity{
    private EditText et_phone_number;
    private Button bt_select_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting3);
        initUI();

    }

    @Override
    public void showNextPage() {
        String phone = et_phone_number.getText().toString();

        if ((!TextUtils.isEmpty(phone))&&phone.trim().length()==11){
//        if (!TextUtils.isEmpty(phone)){
            //存储联系人
            SpUtils.putString(this, ConstantValue.CONTACT_PHONE_NUMBER,phone);
            Intent intent = new Intent(getApplicationContext(), Setup4Activity.class);
            startActivity(intent);
            finish();
            //开启平移动画
//            overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);

        }else {
            ToastUtil.show(this,"联系人号码不规范");
        }
    }

    @Override
    public void showPrePage() {
        Intent intent = new Intent(getApplicationContext(), Setup2Activity.class);
        startActivity(intent);
        finish();
//        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);
    }

    private void initUI() {
        et_phone_number = findViewById(R.id.et_phone_number);
        //获取联系人信息并回显
        String phone = SpUtils.getString(this, ConstantValue.CONTACT_PHONE_NUMBER, "");
        et_phone_number.setText(phone);
        bt_select_number= findViewById(R.id.bt_select_number);
        bt_select_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ContactListActivity.class);
                startActivityForResult(intent,0);

            }
        });
    }

    /**
     * 返回到当前界面接受结果的方法
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //如果用户没有选择数据，回退到这个界面，data是null
        if (data!=null){
            //如果手机数据库里面存储的是000-1111-1111含中华线的形式，则需要将中划线替换成空字符串
            //并且过滤掉用户可能误存前后端,中间的空格
            String phone = data.getStringExtra("phone").replace("-","").replace(" ","").trim();
            et_phone_number.setText(phone);
        }

        super.onActivityResult(requestCode, resultCode, data);

    }
}
