package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.julse.jules.kmsafe.R;

import util.ConstantValue;
import util.SpUtils;

/**
 * Created by jules on 2017/6/30.
 */
public class Setup4Activity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting4);
        Log.i("Life","打开了第四个界面");
    }

    public void nextPage(View view){
        Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
        startActivity(intent);
        finish();
        SpUtils.putBoolean(this, ConstantValue.SETUP_OVER,true);
    }
    public void prePage(View view){
        Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
        startActivity(intent);
        finish();
    }
}
