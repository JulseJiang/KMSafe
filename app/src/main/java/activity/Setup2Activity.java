package activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.julse.jules.kmsafe.R;

import util.ConstantValue;
import util.SpUtils;
import util.ToastUtil;
import view.SettingItemView;

/**
 * Created by jules on 2017/6/30.
 */
public class Setup2Activity extends Activity{
    private SettingItemView siv_sim_bound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting2);
        initUI();
    }

    private void initUI() {
        siv_sim_bound=findViewById(R.id.siv_sim_bound);
        //回显sim卡的序列号
        String sim_number = SpUtils.getString(this, ConstantValue.SIM_NUMBER,"");
        //判断序列号是否为空
        if (TextUtils.isEmpty(sim_number)){
            siv_sim_bound.setCheck(false);
        }else{
            siv_sim_bound.setCheck(true);
        }
        siv_sim_bound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取原有状态
                boolean isCheck = siv_sim_bound.isCheck();
                //状态取反，并设置给当前条目
                siv_sim_bound.setCheck(!isCheck);
                if(!isCheck){
                    //存储序列号
                    //获取sim卡序列号TelephonyManager
                    TelephonyManager manager = (TelephonyManager)
                            getSystemService(Context.TELEPHONY_SERVICE);
                    //获取sim卡序列号获取sim卡序列卡卡号
                    String simSerialNumber = manager.getSimSerialNumber();
                    //存储
                    SpUtils.putString(getApplicationContext(),
                            ConstantValue.SIM_NUMBER,simSerialNumber);
                }else {
                    //删除节点
                    SpUtils.remove(getApplicationContext(),
                            ConstantValue.SIM_NUMBER);
                }
            }
        });
    }

    public void nextPage(View view){
        String serialNumber = SpUtils.getString(this,ConstantValue.SIM_NUMBER,"");
        if (!(TextUtils.isEmpty(serialNumber))){
            Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
            startActivity(intent);
            finish();
        }
        else{
            ToastUtil.show(this,"请绑定sim卡");
        }
    }
    public void prePage(View view){
        Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
        startActivity(intent);
        finish();
    }
}
