package activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.julse.jules.kmsafe.R;

import util.ConstantValue;
import util.SpUtils;
import util.ToastUtil;
import view.SettingItemView;

/**
 * Created by jules on 2017/6/30.
 */
public class Setup2Activity extends BaseSetupActivity{
    private SettingItemView siv_sim_bound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting2);
        initUI();
    }

    @Override
    public void showNextPage() {
        String serialNumber = SpUtils.getString(this,ConstantValue.SIM_NUMBER,"");
        Log.i("Life2","序列号为"+serialNumber);
        if (!(TextUtils.isEmpty(serialNumber))){

            Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
            startActivity(intent);
            finish();
            //开启平移动画
//            overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
        }
        else{
            ToastUtil.show(this,"请绑定sim卡");
        }

    }

    @Override
    public void showPrePage() {
        Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
        startActivity(intent);
        finish();
        //开启平移动画
//        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);
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
                Log.i("Life","现在check box的状态是："+isCheck);
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
                    Log.i("Life","simSerialNumber"+simSerialNumber);
                }else {
                    //删除节点
                    SpUtils.remove(getApplicationContext(),
                            ConstantValue.SIM_NUMBER);
                }
            }
        });
    }

}
