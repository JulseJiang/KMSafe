package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import com.julse.jules.kmsafe.R;

/**
 * Created by jules on 2017/6/29.
 */
public class Setup1Activity extends BaseSetupActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting1);

    }

    @Override
    public void showNextPage() {
        Intent intent = new Intent(getApplicationContext(), Setup2Activity.class);
        startActivity(intent);
        finish();
        //开启平移动画
//        overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
//        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    @Override
    public void showPrePage() {

    }


}
