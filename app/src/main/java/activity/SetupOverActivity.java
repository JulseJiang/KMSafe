package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
