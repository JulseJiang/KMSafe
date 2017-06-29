package activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.julse.jules.kmsafe.R;
import View.SettingItemView;
import util.ConstantValue;
import util.SpUtils;

/**
 * Created by jules on 2017/6/28.
 */

public class SettingActivity extends Activity {
    private final String TAG= "Life_SettingActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Log.i(TAG,".........SettingActivity........");
        initUpdate();
    }

    /**
     * 自动更新
     */
    private void initUpdate() {
        Log.i(TAG,"R.id.siv_update:"+R.id.siv_update);
        final SettingItemView siv_update =(SettingItemView) findViewById(R.id.siv_update);
        //获取已有的开关状态，用作显示
        boolean open_update = SpUtils.getBoolean(this, ConstantValue.OPEN_UPDATE,false);
        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //切换选中状态
                boolean isCheck = siv_update.isCheck();
                siv_update.setCheck(!isCheck);
            }
        });
    }
}
