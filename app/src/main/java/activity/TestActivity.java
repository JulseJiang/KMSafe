package activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by jules on 2017/6/29.
 */
public class TestActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("测试类");
        setContentView(textView);

    }
}
