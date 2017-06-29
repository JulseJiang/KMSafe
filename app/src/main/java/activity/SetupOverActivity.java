package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.julse.jules.kmsafe.R;

import util.ConstantValue;
import util.SpUtils;

/**
 * Created by jules on 2017/6/29.
 */
public class SetupOverActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //密码输入成功，并且四个导航界面设置完成--->停留在设置完成的功能列表界面
        //密码输入成功，四个导航界面没有设置完成--->跳转到导航界面1
        boolean setup_over = SpUtils.getBoolean(this, ConstantValue.SETUP_OVER, false);
        if (setup_over){
            setContentView(R.layout.activity_setup_over);
        }else {
            Intent intent = new Intent(this,Setup1Activity.class);
            startActivity(intent);
            //开启新的界面以后，关闭功能列表界面
            finish();
        }
    }
}
