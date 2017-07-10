package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
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
import util.ConstantValue;
import util.ProcessInfoProvider;
import util.SpUtils;
import util.ToastUtil;

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
    private  ProcessInfo mProcessInfo;
    private long mAvailSpace;
    private long mTotalSpace;
    private String mStrTotal;
    private Handler mHandler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mAdapter==null){
                mAdapter = new MyAdapter();
                lv_process_list.setAdapter(mAdapter);
                tv_des.setText("用户进程("+mCustomerList.size()+")");
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
        mAvailSpace = ProcessInfoProvider.getAvailSpace(this);
        String strAvailSpace = Formatter.formatFileSize(this, mAvailSpace);
        mTotalSpace = ProcessInfoProvider.getTotalSpace(this);
        mStrTotal = Formatter.formatFileSize(this,mTotalSpace);
        tv_memory_info.setText("剩余/总共"+strAvailSpace+"/"+mStrTotal);
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
        lv_process_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            /**
             * @param absListView listview中对象
             * @param firstVisibleItem
             * @param visibleItemCount
             * @param totalItemCount
             */
            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (mCustomerList!=null&&mSystemList!=null){
                    if (firstVisibleItem>mCustomerList.size()){
                        //滚动到了系统条目
                        tv_des.setText("系统进程（"+mSystemList.size()+")");
                    }else {
                        //滚动到了用户应用条目
                        tv_des.setText("用户进程（"+mCustomerList.size()+")");
                    }
                }
            }
        });
        lv_process_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i==0||i==mCustomerList.size()+1){
                    Log.i("Life","点击了纯文字条目");
                    return ;//表示不处理，不响应点击事件
                }else {
                    if (i<mCustomerList.size()+1){
                        mProcessInfo=mCustomerList.get(i-1);
                    }else {
                        //返回系统应用对应的条目
                        mProcessInfo=mSystemList.get(i-mCustomerList.size()-2);
                    }
                    Log.i("Life","点击了  "+mProcessInfo.getName());
                   /* mAdapter.getItem(i).isCheck=!mAdapter.getItem(i).isCheck;
                    mAdapter.notifyDataSetChanged();*/
                    //修改对象记录的选中信息
                    if (mProcessInfo!=null&&!(mProcessInfo.packageName.equals(getPackageName()))){
                        mProcessInfo.isCheck=!mProcessInfo.isCheck;
                    }
                    //修改view对象的选中状态
                    CheckBox cb_box = (CheckBox) view.findViewById(R.id.cb_box);
                    cb_box.setChecked(mProcessInfo.isCheck);
                }
            }
        });

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

        /**
         * 决定显示条数
         * @return
         */
        @Override
        public int getCount() {
            if (SpUtils.getBoolean(getApplication(), ConstantValue.SHOW_SYSTEM,false)){
                return mSystemList.size()+mCustomerList.size()+2;
            }else {
                return mCustomerList.size()+1;
            }

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
                    holder.cb_box.setVisibility(View.INVISIBLE);//不可见
                }else {
                    holder.cb_box.setVisibility(View.VISIBLE);
                }
                holder.cb_box.setChecked(getItem(i).isCheck);
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
                selectReverse();
                break;
            case R.id.bt_clean:
                selectClear();
                break;
            case R.id.bt_setting:
                setting();
                break;

        }
    }

    /**
     * 点击设置
     */
    private void setting() {
        startActivityForResult(new Intent(this,ProcessSettingActivity.class),0);
    }

    /**
     * 清理选中的进程,仅限于能够杀死的进程
     */
    private void selectClear() {
        List<ProcessInfo> killProcessList = new ArrayList<>();
        for (ProcessInfo processInfo:mCustomerList){
            if(processInfo.getPackageName().equals(getPackageName())){
                continue;
            }
            if (processInfo.isCheck){
                //不能在集合循环过程中移除集合中的对象
//                mCustomerList.remove(processInfo);
                //3.记录要杀死的用户进程
                killProcessList.add(processInfo);
            }
        }
        for(ProcessInfo processInfo:mSystemList){
            if (processInfo.isCheck){
                //4.记录要杀死的系统进程
                killProcessList.add(processInfo);
            }
        }
        //5.遍历killProcessList，然后一处用户进程集合与系统进程集合中对象
        long totalReleaseSpace=0;
        for (ProcessInfo processInfo : killProcessList){
            //6.判断集合有没有包含当前对象
            if (mCustomerList.contains(processInfo)){
                mCustomerList.remove(processInfo);
            }

            if (mSystemList.contains(processInfo)){
                mSystemList.remove(processInfo);
            }
            //7.杀进程
            ProcessInfoProvider.killProcess(this,processInfo);
            //记录释放空间的总大小
            totalReleaseSpace+=processInfo.memSize;
        }
        //8.在集合改编后，需要通知数据刷新
        if (mAdapter!=null){
            mAdapter.notifyDataSetChanged();
        }
        //9.进程总数更新
        mProcessCount-=killProcessList.size();
        tv_process_count.setText("进程总数："+mProcessCount);
        //10.更新可用剩余空间
        mAvailSpace+=totalReleaseSpace;
        String strAvail = Formatter.formatFileSize(this,mAvailSpace);
        tv_memory_info.setText("剩余/总共"+strAvail+"/"+mStrTotal);
        //11.通过吐司高数用户释放了多少空间，杀死了几个进程，占位符
        String totalRelease = Formatter.formatFileSize(this,totalReleaseSpace);
        /*ToastUtil.show(this,"杀死了" +
                killProcessList.size() +
                "个进程,释放了"+totalRelease);*/
        //或者占位指定数据
        String str = String.format("杀死了%d个进程，释放了%s空间",killProcessList.size(),totalRelease);
        ToastUtil.show(this,str);

    }

    /**
     * 取反
     */
    private void selectReverse() {
        //1.将所有的集合对象isCheck字段取反
        for (ProcessInfo processInfo:mCustomerList){
            if(processInfo.getPackageName().equals(getPackageName())){
                continue;
            }
            processInfo.isCheck=!processInfo.isCheck;
        }
        for(ProcessInfo processInfo:mSystemList){
            processInfo.isCheck=!processInfo.isCheck;
        }
        //2.通知数据适配器刷新
        if (mAdapter!=null){
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 全部选择
     */
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mAdapter!=null){
            mAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
