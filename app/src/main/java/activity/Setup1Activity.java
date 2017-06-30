package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.julse.jules.kmsafe.R;

/**
 * Created by jules on 2017/6/29.
 */
public class Setup1Activity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting1);

        initUI();
    }

    private void initUI() {
    }
    public void nextPage(View view){
        Intent intent = new Intent(getApplicationContext(), Setup2Activity.class);
        startActivity(intent);
        finish();
    }
}
