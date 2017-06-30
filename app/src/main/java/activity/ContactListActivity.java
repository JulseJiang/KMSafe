package activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.julse.jules.kmsafe.R;

/**
 * Created by jules on 2017/6/30.
 */
public class ContactListActivity extends Activity{
    private ListView lv_contact;
    private final String TAG="Life_ContactL";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        initUI();
        initData();
    }
    private void initUI() {
        lv_contact = findViewById(R.id.lv_contact);

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
                //获取内瓤解析器对象
                ContentResolver contentResolver = getContentResolver();
                //查询系统联系人数据库表过程（要添加读取联系人权限）
                Cursor cursor = contentResolver.query(Uri.parse(
                        "content://com.android.contacts/raw_contacts"),
                        new String[]{"contact_id"},
                        null,//查询列
                        null,//筛选
                        null//默认排序方式
                );
                //循环游标，直到没有数据为止

                while (cursor.moveToNext()){
                    String id = cursor.getString(0);
//                    Log.i(TAG,"联系人id:"+id);
                    //根据用用户唯一性id值，查询data表生成的视图，获取data以及mimetype字段
                    Cursor indexCursor = contentResolver.query(Uri.parse(
                            "content://com.android.contacts/raw_contacts"),
                            new String[]{"data1", "mimetype"},
                            "raw_contact_id = ?",
                            new String[]{id},
                            null
                    );
                    while (indexCursor.moveToNext()){
                        Log.i(TAG,"data"+indexCursor.getString(0));
                        Log.i(TAG,"mimetype"+indexCursor.getString(1));
                    }
                }
                cursor.close();
            }
        }.start();

    }
}
