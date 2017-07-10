package activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.julse.jules.kmsafe.R;

import java.io.File;

import util.SmsBackup;

/**
 * Created by jules on 2017/7/2.
 */
public class AToolActivity extends Activity{
    private TextView tv_phone_adress,tv_sms_backup,tv_commonnumber_query;
    private ProgressBar pb_bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atool);
        //短信备份方法
        initSmsBackUp();
        initCommonNumberQuery();
    }

    private void initCommonNumberQuery() {
        tv_commonnumber_query=findViewById(R.id.tv_commonnumber_query);
        tv_commonnumber_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),CommonNumberQueryActivity.class));
            }
        });
    }

    private void initSmsBackUp() {
        tv_sms_backup=findViewById(R.id.tv_sms_backup);
        pb_bar=findViewById(R.id.pb_bar);
        tv_sms_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSmsBackUpDialog();
            }
        });
    }

    /**
     * 显示备份对话框
     */
    private void showSmsBackUpDialog() {
        //创建一个带进度条的对话框
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.drawable.icon);
        progressDialog.setTitle("短信备份");
        //制定进度条的样式为水平
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        //直接调用备份短信方法即可
        new Thread(){
            @Override
            public void run() {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "sms_KMSafe.xml";
                SmsBackup.backup(getApplication(),path, new SmsBackup.CallBack() {
                    @Override
                    public void setMax(int max) {
                        //由开发者自己决定使用进度条还是对话框
                        pb_bar.setMax(max);
                        progressDialog.setMax(max);
                    }

                    @Override
                    public void setProgress(int index) {
                        pb_bar.setProgress(index);
                        progressDialog.setProgress(index);
                    }
                });
                super.run();

            }
        }.start();
    }
}
