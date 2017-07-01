package activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.julse.jules.kmsafe.R;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jules on 2017/6/30.
 */
public class ContactListActivity extends Activity{
    private ListView lv_contact;
    private List<HashMap<String,String>> contactList = new ArrayList<>();
    private final String TAG="Life_ContactL";
    private MyAdapter mAdapter;
    private Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mAdapter = new MyAdapter();
            lv_contact.setAdapter(mAdapter);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        initUI();
        initData();
    }
    private void initUI() {
        lv_contact = findViewById(R.id.lv_contact);
        lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mAdapter!=null){
                    //获取点击此条目获取的对象
                    HashMap<String,String> hashMap = mAdapter.getItem(i);
                    String phone = hashMap.get("phone");
                    Intent intent = new Intent();
                    intent.putExtra("phone",phone);
                    setResult(0,intent);
                    finish();
                }
            }
        });
    }
    /**
     * 获取联系人数据
     */
    private void initData() {
        //读取联系人，可能是一个耗时操作，放到子线程中处理
        new Thread(){
            @Override
            public void run() {
                super.run();
                //获取内容解析器对象
                ContentResolver contentResolver = getContentResolver();
                //查询系统联系人数据库表过程（要添加读取联系人权限）--第一张表
                Cursor cursor = contentResolver.query(Uri.parse(
                        "content://com.android.contacts/raw_contacts"),
                        new String[]{"contact_id"},//查询列
                        null,//查询列条件
                        null,//筛选
                        null//默认排序方式

                );
                contactList.clear();
                //循环游标，直到没有数据为止
                int i =0;
                while (cursor.moveToNext()){
                    i++;
                    String id = cursor.getString(0);
                    if(TextUtils.isEmpty(id)) {
                        continue;
                    }
                    Log.i(TAG,"联系人id:"+id);
                    //根据用用户唯一性id值，查询data表生成的视图，获取data以及mimetype字段--第二张表
                    Cursor indexCursor = contentResolver.query(Uri.parse(
                            "content://com.android.contacts/data"),
                            new String[]{"data1", "mimetype"},
                            "raw_contact_id = ?",
                            new String[]{id},
                            null
                    );
                    /**
                     * 能查询到表中的第一条消息，
                     * data:
                     * 07-01 16:04:37.987 15770-16022/com.julse.jules.kmsafe I/Life_ContactL: data:OPPO官方客服
                     07-01 16:04:37.997 15770-16022/com.julse.jules.kmsafe I/Life_ContactL: data:4001666888
                     07-01 16:04:37.997 15770-16022/com.julse.jules.kmsafe I/Life_ContactL: data:www.oppo.com
                     07-01 16:04:37.997 15770-16022/com.julse.jules.kmsafe I/Life_ContactL: data:null
                     *
                     * mimtype:
                     * 07-01 16:04:37.997 15770-16022/com.julse.jules.kmsafe I/Life_ContactL: mimetype:vnd.android.cursor.item/name
                     07-01 16:04:37.997 15770-16022/com.julse.jules.kmsafe I/Life_ContactL: column1:mimetype
                     07-01 16:04:37.997 15770-16022/com.julse.jules.kmsafe I/Life_ContactL: mimetype:vnd.android.cursor.item/phone_v2
                     07-01 16:04:37.997 15770-16022/com.julse.jules.kmsafe I/Life_ContactL: column1:mimetype
                     07-01 16:04:37.997 15770-16022/com.julse.jules.kmsafe I/Life_ContactL: mimetype:vnd.android.cursor.item/website
                     07-01 16:04:37.997 15770-16022/com.julse.jules.kmsafe I/Life_ContactL: column1:mimetype
                     07-01 16:04:37.997 15770-16022/com.julse.jules.kmsafe I/Life_ContactL: mimetype:vnd.android.cursor.item/photo
                     07-01 16:04:37.997 15770-16022/com.julse.jules.kmsafe I/Life_ContactL: column1:mimetype
                     *
                     * 不能查询到第二行
                     * IllegalArgumentException: the bind value at index 1 is null
                     at android.database.sqlite.SQLiteProgram.bindString(SQLiteProgram.java:164)
                     */
                    HashMap<String, String> hashMap = new HashMap<>();
                    while (indexCursor.moveToNext()){
                        String data = indexCursor.getString(0);
                        String type=indexCursor.getString(1);
                        Log.i(TAG,"data:"+data);
                        Log.i(TAG,"mimetype:"+type);
                        //区分类型以填充适配器
                        if (type.equals("vnd.android.cursor.item/phone_v2")){
                            //判断数据非空
                            if (!(TextUtils.isEmpty(data))){
                                hashMap.put("phone",data);
                            }
                        }else if (type.equals("vnd.android.cursor.item/name")){
                            if (!(TextUtils.isEmpty(data))){
                                hashMap.put("name",data);
                            }
                        }
                    }
                    indexCursor.close();
                    //---测试代码
                    /*HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("phone","15310320113");
                    hashMap.put("name","julse");*/
                    //---测试结束
                    if (!hashMap.isEmpty()){
                        contactList.add(hashMap);
                    }
                    Log.i(TAG,"循环一次查询");
                }

                Log.i(TAG,"共查到i个联系人：i="+i);
                cursor.close();
                //通过消息机制回到主线程处理逻辑,
                // 发送一个空的消息，用状态码告诉主线程集合准备完毕，可以填充数据适配器了
                mhandler.sendEmptyMessage(0);
            }
        }.start();

    }

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return contactList.size();
        }

        @Override
        public HashMap<String, String> getItem(int i) {
            return contactList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View view = View.inflate(getApplicationContext(), R.layout.listview_contact_item, null);
            TextView tv_name = view.findViewById(R.id.tv_name);
            TextView tv_phone = view.findViewById(R.id.tv_phone);
            tv_name.setText(getItem(i).get("name"));
            tv_phone.setText(getItem(i).get("phone"));
            return view;
        }
    }
}
