package activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.Layout;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.julse.jules.kmsafe.R;

import java.util.ArrayList;
import java.util.List;

import db.domain.AppInfo;
import util.AppInfoProvider;
import util.ToastUtil;

/**
 * Created by jules on 2017/7/5.
 */
public class AppManagerActivity extends Activity implements View.OnClickListener{
    private TextView tv_memory,tv_sd_memory,tv_des;
    private ListView lv_app_list;
    private MyAdapter mAdapter;
    private List<AppInfo> mAppInfoList,mSystemList,mCustomerList;
    private AppInfo mAppInfo;
    private  PopupWindow mPopupWindow;
    private Handler mHandler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mAdapter==null){
                mAdapter = new MyAdapter();
                lv_app_list.setAdapter(mAdapter);
                tv_des.setText("用户应用("+mCustomerList+")");
            }else {
                mAdapter.notifyDataSetChanged();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        initTitle();
//        List<AppInfo> appInfoList = AppInfoProvider.getAppInfoList(this);
        initList();
    }

    private void initList() {
        lv_app_list=findViewById(R.id.lv_app_list);
        tv_des=findViewById(R.id.tv_des);
        new Thread(){
            @Override
            public void run() {
                super.run();
                mAppInfoList = AppInfoProvider.getAppInfoList(getApplication());
                mCustomerList=new ArrayList<AppInfo>();
                mSystemList=new ArrayList<AppInfo>();
                for (AppInfo appInfo:mAppInfoList){
                    if (appInfo.isSystem){
                        //系统应用
                        mSystemList.add(appInfo);
                    }else {
                        //用户应用
                        mCustomerList.add(appInfo);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();
        lv_app_list.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                    if (firstVisibleItem>mCustomerList.size()+1){
                        //滚动到了系统条目
                        tv_des.setText("系统应用（"+mSystemList.size()+")");
                    }else {
                        //滚动到了用户应用条目
                        tv_des.setText("用户应用（"+mCustomerList.size()+")");
                    }
                }
            }
        });
        lv_app_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (i==0||i==mCustomerList.size()){
                    Log.i("Life","点击了纯文字条目");
                    return ;//表示不处理，不响应点击事件
                }else {
                    if (i<mCustomerList.size()+1){
                        mAppInfo=mCustomerList.get(i-1);
                        return ;
                    }else {
                        //返回系统应用对应的条目
                        mAppInfo=mSystemList.get(i-mCustomerList.size()-2);
                    }
                    Log.i("Life","点击了  "+mAppInfo.getName());
                    showPopWindow(view);
                }
            }
        });
    }

    private void showPopWindow(View v) {
        View view = View.inflate(this, R.layout.pupupwindow_layout, null);
        TextView tv_uninstall= view.findViewById(R.id.tv_uninstall);
        TextView tv_start=view.findViewById(R.id.tv_start);
        TextView tv_share=view.findViewById(R.id.tv_share);

        tv_uninstall.setOnClickListener(this);
        tv_start.setOnClickListener(this);
        tv_share.setOnClickListener(this);

        //透明动画（透明-->不透明）
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setFillAfter(true);

        //缩放动画
        ScaleAnimation scaleAnimation = new ScaleAnimation(
          0,1,0,1, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f
        );
        scaleAnimation.setDuration(1000);
        scaleAnimation.setFillAfter(true);
//        alphaAnimation.setFillAfter(true);

        //动画集合
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);



        //创建窗体对象，指定宽高
        mPopupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        //设置一个透明背景
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        //指定窗体位置
        mPopupWindow.showAsDropDown(v,50,-v.getHeight());//挂载控件，偏移量
        view.startAnimation(animationSet);
    }


    private void initTitle() {
        //1.获取磁盘(内存，区分于手机运行内存)可用大小，磁盘路径
        String path = Environment.getDataDirectory().getAbsolutePath();
        //2.获取sd卡可用大小，sd卡路径
        String sdPath=Environment.getExternalStorageDirectory().getAbsolutePath();
        //3.获取以上两个路径下文件夹的可用大小
        String memoryAvailSpace = Formatter.formatFileSize(this, getAvilSpace(path));
        String sdMemoryAvailSpace = Formatter.formatFileSize(this, getAvilSpace(sdPath));

        tv_memory=findViewById(R.id.tv_memory);
        tv_sd_memory=findViewById(R.id.tv_sd_memory);
        tv_memory.setText("磁盘可用："+memoryAvailSpace);
        tv_sd_memory.setText("sd卡可用："+sdMemoryAvailSpace);
        tv_memory.setTextColor(getResources().getColor(R.color.colorBlue));
    }

    /**
     * 返回值结果单位是byte=8bit,最大结果为Integer.MAX_VALUE bytes
     * 获取path路径下文件夹的可用大小
     * @param path
     * @return  返回指定路径可用区域的byte类型值
     */
    private long getAvilSpace(String path) {
        //获取可用磁盘大小类
        StatFs statFs = new StatFs(path);
        //获取可用区块的个数
        long count = statFs.getAvailableBlocks();
        //获取区块的大小
        long size = statFs.getBlockSize();
        //区块大小*可用区块==可用空间大小
        return count*size;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_uninstall:
                if (mAppInfo.isSystem){
                    ToastUtil.show(getApplication(),mAppInfo.getName()+"  此应用不能卸载");
                }else {
                    Intent intent = new Intent("android.intent.action.DELETE");
                    intent.addCategory("android.intent.category.DEFAULT");
                    //根据包名卸载程序
                    intent.setData(Uri.parse("package:"+mAppInfo.getPackageName()));
                    startActivity(intent);
                }
                break;
            case R.id.tv_start:
                //通过左面去开启制定包名应用
                PackageManager pm = getPackageManager();
                //通过Launch开启制定包名的意图，去开启应用
                Intent launchIntentForPackage = pm.getLaunchIntentForPackage(mAppInfo.getPackageName());
                if (launchIntentForPackage!=null){
                    startActivity(launchIntentForPackage);
                }else {
                    ToastUtil.show(getApplication(),mAppInfo.getName()+"此应用不能被开启");
                }
                break;
            //分享到第三方平台（微信，新浪，腾讯），智慧北京
            //拍照-->分享：将图片上传到微信服务器,微信提供接口api，推广
            //查看朋友圈的时候，从服务器上获取数据（获取我们上传的图片）
            case R.id.tv_share:
                //通过短信应用向外发送短信
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT,"分享一个应用，应用名称为："+mAppInfo.getName());
                intent.setType("text/plain");
                startActivity(intent);
                break;
        }
        if (mPopupWindow!=null){
            mPopupWindow.dismiss();
        }
    }

    private class MyAdapter extends BaseAdapter{
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
        public AppInfo getItem(int i) {
            if (i==0||i==mCustomerList.size()+1){
                return null;
            }else {
                if (i<mCustomerList.size()+1){
                    return mCustomerList.get(i-1);
                }else {
                    //返回系统应用对应的条目
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
                    holder.tv_title.setText("用户应用("+mCustomerList.size()+")");
                }else {
                    holder.tv_title.setText("系统应用("+mSystemList.size()+")");
                }
                return view;
            }else{
                //展示图文
                ViewHolder holder=null;
                if (view==null){
                    view =View.inflate(getApplication(),R.layout.listview_appinfo_item,null);
                    holder=new ViewHolder();
                    holder.iv_icon=view.findViewById(R.id.iv_icon);
                    holder.tv_name=view.findViewById(R.id.tv_name);
                    holder.tv_path=view.findViewById(R.id.tv_path);
                    view.setTag(holder);
                }else {
                    holder= (ViewHolder) view.getTag();
                }
                holder.iv_icon.setBackground(mAppInfoList.get(i).icon);
                holder.tv_name.setText(mAppInfoList.get(i).name);
                if (mAppInfoList.get(i).isSdCard){
                    holder.tv_path.setText("sd卡应用");
                }else {
                    holder.tv_path.setText("手机应用");
                }
                return view;
            }

        }
    }
    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name,tv_path;
    }
    static class ViewTitleHolder{
        TextView tv_title;
    }
}
