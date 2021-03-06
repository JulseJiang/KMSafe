package activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.julse.jules.kmsafe.R;

import java.util.List;

import db.dao.BlackNumberDao;
import db.domain.BlackNumberInfo;
import util.ToastUtil;

/**
 * Created by jules on 2017/7/2.
 */
public class BlackNumberActivity extends Activity{
    private ListView lv_blacknumber;
    private Button bt_add;
    private BlackNumberDao mDao;
    private List<BlackNumberInfo> mBlackNumberList;
    private int mode = 1;
    private Mydapter mAdapter;
    private boolean mIsload=false;
    private int mCount;
    private Handler mHandler= new Handler(){

        @Override
        public void handleMessage(Message msg) {
            //告诉listview可以设置数据适配器了
            if (mAdapter==null){
                mAdapter = new Mydapter();
                lv_blacknumber.setAdapter(mAdapter);
            }else {
                mAdapter.notifyDataSetChanged();
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacknumber);

        initUI();
        initData();
    }

    private void initData() {
        //获取数据库中电话号码
        new Thread(){
            @Override
            public void run() {
                super.run();
                //获取操作黑名单数据库的对象
                mDao = BlackNumberDao.getInstance(getApplicationContext());
                //获取数据库中黑名单信息
                mBlackNumberList=mDao.find(0);
                mCount=mDao.getCount();
                //通过消息机制，告诉主线程可以使用包含数据的集合
                Log.i("Life","mBlackNumberList大小："+mBlackNumberList.size());
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    /**
     * 分页加载数据
     */
    private void loadData(int index) {
        //获取操作黑名单数据库的对象
    mDao = BlackNumberDao.getInstance(getApplicationContext());
    //获取数据库中黑名单信息
    mBlackNumberList=mDao.find(index);
    //通过消息机制，告诉主线程可以使用包含数据的集合
    Log.i("Life","mBlackNumberList大小："+mBlackNumberList.size());
    mHandler.sendEmptyMessage(0);
}

    private void initUI() {
        bt_add=findViewById(R.id.bt_add);
        lv_blacknumber=findViewById(R.id.lv_blacknumber);
        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        lv_blacknumber.setOnScrollListener(new AbsListView.OnScrollListener() {
            //滚动过程中，状态发生改变的调用方法
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
//                AbsListView.OnScrollListener.SCROLL_STATE_FLING;//飞速滚动
//                AbsListView.OnScrollListener.SCROLL_STATE_IDLE;//空闲状态
//                AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL//触摸时滚动
                if (mBlackNumberList!=null){
                    if (scrollState== AbsListView.OnScrollListener.SCROLL_STATE_IDLE//滚到停止状态
                            &&lv_blacknumber.getLastVisiblePosition()>=mBlackNumberList.size()-1//从size是从1开始计数的，最后一个条目可见,触底的时候
                            &&!mIsload){//防止重复加载
                        /**
                         * 如果当前正在加载mIsLoad就是true，本次加载完毕之后，再将mIsLoad改为false，再去加载
                         *
                         */
                        if (mCount>mBlackNumberList.size()){
                            //加载后面的条目
                            new Thread(){
                                @Override
                                public void run() {
                                    super.run();
                                    //获取操作黑名单数据库的对象
                                    mDao = BlackNumberDao.getInstance(getApplicationContext());
                                    //获取数据库中黑名单信息
                                    List<BlackNumberInfo> moreData=mDao.find(mBlackNumberList.size());
                                    mBlackNumberList.addAll(moreData);
                                    //通过消息机制，告诉主线程可以使用包含数据的集合
                                    Log.i("Life","mBlackNumberList大小："+mBlackNumberList.size());
                                    mHandler.sendEmptyMessage(0);
                                }
                            }.start();
                        }

                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
    }

    private void showDialog() {
        final AlertDialog.Builder builer = new AlertDialog.Builder(this);
        builer.setTitle("骚扰拦截");
        builer.setIcon(R.drawable.defend);
        final EditText et_number = new EditText(this);
        et_number.setPadding(30,30,30,30);
        et_number.setInputType(EditText.AUTOFILL_TYPE_DATE);
        et_number.setHint("请输入要拦截的电话号码");
        builer.setView(et_number);
        builer.setSingleChoiceItems(new String[]{"拦截短信","拦截电话","拦截所有"}, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mode=i+1;
            }
        });

        builer.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String phone = et_number.getText().toString();
                if (!TextUtils.isEmpty(phone)){
                    BlackNumberInfo info = new BlackNumberInfo();
                    info.mode=mode+"";
                    info.phone=phone;
                    mDao.insert(phone,mode+"");
                    //更新listview中的显示,将添加的数据添加到集合顶部
                    mBlackNumberList.add(0,info);
                    //通知数据适配器刷新
                    if (mAdapter!=null){
                        mAdapter.notifyDataSetChanged();
                    }

                }else {
                    ToastUtil.show(getApplication(),"请输入拦截号码");
                }
            }
        });
        builer.setNegativeButton("cancel",null);
        builer.show();
    }

    private class Mydapter extends BaseAdapter{
        @Override
        public int getCount() {
            return mBlackNumberList.size();
        }

        @Override
        public BlackNumberInfo getItem(int i) {
            return mBlackNumberList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            ViewHolder holder=null;
            //复用convertview
            if (view==null){
                holder = new ViewHolder();
                view = View.inflate(getApplicationContext(), R.layout.blacknumber_item_view,null);
                holder.tv_phone = view.findViewById(R.id.tv_phone);
                holder.tv_mode =  view.findViewById(R.id.tv_mode);
                holder.iv_delete =  view.findViewById(R.id.iv_delete);
                view.setTag(holder);
            }else {
                holder= (ViewHolder) view.getTag();
            }

            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //删除数据库及集合中的条目
                    mDao.delete(mBlackNumberList.get(i).phone);
                    mBlackNumberList.remove(i);
                    if (mAdapter!=null){
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
            if (!mBlackNumberList.isEmpty()){
                holder.tv_phone.setText(mBlackNumberList.get(i).phone);
                int mode= Integer.parseInt(mBlackNumberList.get(i).mode);
                switch (mode){
                    case 1:
                        holder.tv_mode.setText("拦截短信");
                        break;
                    case 2:
                        holder.tv_mode.setText("拦截电话");
                        break;
                    case 3:
                        holder.tv_mode.setText("拦截所有");
                        break;

                }
            }


            return view;
        }
    }

    /**
     * 优化listview
     */
    static class ViewHolder {
        TextView tv_phone;
        TextView tv_mode ;
        ImageView iv_delete;
    }
}
