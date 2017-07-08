package activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.julse.jules.kmsafe.R;
import java.util.ArrayList;
import java.util.List;
import db.domain.ProcessInfo;
import util.ProcessInfoProvider;

/**
 * Created by jules on 2017/7/6.
 */
public class ProgcessManagerActivity extends Activity implements View.OnClickListener{
    private TextView tv_process_count,tv_memory_info,tv_des;
    private ListView lv_process_list;
    private Button bt_select_all,bt_reverse,bt_clean,bt_setting;
    private int mProcessCount;
    private List<ProcessInfo> mProcessInfoList,mCustomerList,mSystemList;
    private MyAdapter mAdapter;
    private Handler mHandler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mAdapter==null){
                mAdapter = new MyAdapter();
                lv_process_list.setAdapter(mAdapter);
//                tv_des.setText("加载中用户进程("+mCustomerList.size()+")");
            }else {
                mAdapter.notifyDataSetChanged();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_manager);
        initUI();
        initData();
        initListData();
    }

    private void initListData() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                mProcessInfoList = ProcessInfoProvider.getProcessInfo(getApplication());
                mCustomerList = new ArrayList<ProcessInfo>();
                mSystemList=new ArrayList<ProcessInfo>();
                for (ProcessInfo processInfo:mProcessInfoList){
                    if (processInfo.isSystem){
                        //系统进程
                        mSystemList.add(processInfo);
                    }else {
                        //用户进程
                        mCustomerList.add(processInfo);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initData() {
        mProcessCount = ProcessInfoProvider.getProcessCount(this);
        tv_process_count.setText("进程总数："+mProcessCount);

        //获取可用内存大小，并且格式化
        long availSpace = ProcessInfoProvider.getAvailSpace(this);
        String strAvailSpace = Formatter.formatFileSize(this,availSpace);

        long totalSpace = ProcessInfoProvider.getTotalSpace(this);
        String strTotal = Formatter.formatFileSize(this,totalSpace);
        tv_memory_info.setText("剩余"+strAvailSpace+"/"+"总数"+strTotal);
    }

    private void initUI() {
        tv_des=findViewById(R.id.tv_des);
        tv_process_count=findViewById(R.id.tv_process_count);
        tv_memory_info=findViewById(R.id.tv_memory_info);
        lv_process_list=findViewById(R.id.lv_process_list);
        bt_select_all=findViewById(R.id.bt_select_all);
        bt_reverse=findViewById(R.id.bt_reverse);
        bt_clean=findViewById(R.id.bt_clean);
        bt_setting=findViewById(R.id.bt_setting);

        bt_select_all.setOnClickListener(this);
        bt_reverse.setOnClickListener(this);
        bt_clean.setOnClickListener(this);
        bt_setting.setOnClickListener(this);
        
    }
    private class MyAdapter extends BaseAdapter {
        /**
         * 获取适配器中条目类型的总数，修改成两种（纯文本，图片+文本）
         * @return
         */
        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount()+1;
        }

        /**
         *
         * @param position
         * @return
         * 0;//返回纯文字条目状态码
         * 1;//返回图片+条目状态码
         */
        @Override
        public int getItemViewType(int position) {
            if (position==0||position==mCustomerList.size()+1){
                return 0;//返回纯文字条目状态码
            }else {
                return 1;//返回图片+条目状态码
            }
        }

        @Override
        public int getCount() {
            return mSystemList.size()+mCustomerList.size()+2;
        }

        @Override
        public ProcessInfo getItem(int i) {
            if (i==0||i==mCustomerList.size()+1){
                return null;
            }else {
                if (i<mCustomerList.size()+1){
                    return mCustomerList.get(i-1);
                }else {
                    //返回系统进程对应的条目
                    return mSystemList.get(i-mCustomerList.size()-2);
                }
            }


        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            //判断当前索引指向的条目类型状态码
            int type = getItemViewType(i);
            if (type==0){
                //展示纯文字条目
                ViewTitleHolder holder=null;
                if (view==null){
                    view =View.inflate(getApplication(),R.layout.listview_appinfo_item_title,null);
                    holder=new ViewTitleHolder();
                    holder.tv_title=view.findViewById(R.id.tv_title);
                    view.setTag(holder);
                }else {
                    holder= (ViewTitleHolder) view.getTag();
                }
                if (i==0){
                    holder.tv_title.setText("用户进程("+mCustomerList.size()+")");
                }else {
                    holder.tv_title.setText("系统进程("+mSystemList.size()+")");
                }
                return view;
            }else{
                //展示图文
                ViewHolder holder=null;
                if (view==null){
                    view =View.inflate(getApplication(),R.layout.listview_processinfo_item,null);
                    holder=new ViewHolder();
                    holder.iv_icon=view.findViewById(R.id.iv_icon);
                    holder.tv_name=view.findViewById(R.id.tv_name);
                    holder.tv_memory_info=view.findViewById(R.id.tv_memory_info);
                    holder.cb_box = view.findViewById(R.id.cb_box);
                    view.setTag(holder);
                }else {
                    holder= (ViewHolder) view.getTag();
                }
                holder.iv_icon.setBackground(getItem(i).icon);
                holder.tv_name.setText(getItem(i).name);
                String strSize = Formatter.formatFileSize(getApplication(),getItem(i).memSize);
                holder.tv_memory_info.setText(strSize);
                //本进程不能被选中，所以先将checkbox隐藏掉
                if (getItem(i).packageName.equals(getPackageManager())){
                    holder.cb_box.setVisibility(View.GONE);//不可见
                }else {
                    holder.cb_box.setVisibility(View.VISIBLE);
                }
                return view;
            }

        }
    }
    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name,tv_memory_info;
        CheckBox cb_box;
    }
    static class ViewTitleHolder{
        TextView tv_title;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_select_all:
                selectAll();
                break;
            case R.id.bt_reverse:
                break;
            case R.id.bt_clean:
                break;
            case R.id.bt_setting:
                break;

        }
    }

    private void selectAll() {
        //1.将所有的集合对象isCheck字段设置为true
        for (ProcessInfo processInfo:mCustomerList){
            if(processInfo.getPackageName().equals(getPackageName())){
                continue;
            }
            processInfo.isCheck=true;
        }
        for(ProcessInfo processInfo:mSystemList){
            processInfo.isCheck=true;
        }
        //2.通知数据适配器刷新
        if (mAdapter!=null){
            mAdapter.notifyDataSetChanged();
        }
    }
}
