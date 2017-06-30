package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.julse.jules.kmsafe.R;

/**
 * Created by jules on 2017/6/30.
 */
public class Setup3Activity extends Activity{
    private EditText et_phone_number;
    private Button bt_select_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting3);
        initUI();

    }

    private void initUI() {
        et_phone_number = findViewById(R.id.et_phone_number);
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
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void nextPage(View view){
        Intent intent = new Intent(getApplicationContext(), Setup4Activity.class);
        startActivity(intent);
        finish();
    }
    public void prePage(View view){
        Intent intent = new Intent(getApplicationContext(), Setup2Activity.class);
        startActivity(intent);
        finish();
    }
}
