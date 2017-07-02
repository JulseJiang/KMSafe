package db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.util.Log;

import com.lidroid.xutils.db.annotation.Table;

import java.util.ArrayList;
import java.util.List;

import db.domain.BlackNumberInfo;
import db.BlackNumberOpenHelper;

/**
 * Created by jules on 2017/7/2.
 */

public class BlackNumberDao {
    private BlackNumberOpenHelper blackNumberOpenHelper;
    private final static String TABLE="blacknumber";
    private final static String ID="_id";
    private final static String PHONE="phone";
    private final static String MODE="mode";
    //BlackNumberDao单例模式
    //--1，私有化构造方法
    //--2，声明一个当前类对象
    //--3，提供一个方法，如果当前类的对象为空，创建一个新的
    private BlackNumberDao(Context context){
        //创建数据库以及其表结构
        blackNumberOpenHelper = new BlackNumberOpenHelper(context);
    }
    private static BlackNumberDao blackNumberDao = null;
    public static BlackNumberDao getInstance(Context context){
        if (blackNumberDao==null){
            blackNumberDao=new BlackNumberDao(context);
        }
        return  blackNumberDao;
    }

    /**
     * 增加一个条目
     * @param phone 拦截的电话号码
     * @param mode  拦截类型：1，电话 2，短信 3，电话和短信
     */
    public void insert(String phone,String mode){
        //开启数据库，准备写入操作
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PHONE,phone);
        values.put(MODE,mode);
        db.insert(TABLE,null,values);
        db.close();
    }

    /**
     * 从数据库中删除一条电话号码
     * @param phone 删除的电话号码
     */
    public void delete(String phone){
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        db.delete(TABLE,PHONE+"=?",new String[]{phone});
        db.close();
    }

    /**
     * 修改
     * @param phone
     * @param mode
     */
    public void update(String phone,String mode){
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PHONE,phone);
        values.put(MODE,mode);
        db.update(TABLE,values,PHONE+"=?",new String[]{phone});
        db.close();
    }

    /**
     * 查询所有信息
     */
    public List<BlackNumberInfo> findAll(){
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        Cursor cursor = db.query(TABLE,
                new String[]{PHONE, MODE},
                null, null, null, null, ID + " desc");//倒序排列
        List<BlackNumberInfo> blackNumberList= new ArrayList<>();
        while(cursor.moveToNext()){
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.phone=cursor.getString(0);
            blackNumberInfo.mode=cursor.getString(1);
            blackNumberList.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        Log.i("Life","数据库信息条数"+blackNumberList.size());
        return blackNumberList;
    }
    public List<BlackNumberInfo> find(int index){
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        String SQL = "select * from " + TABLE+
                " order by _id desc LIMIT ?,20;";
        Cursor cursor = db.rawQuery(SQL,new String[]{index+""});//从index开始查询，查20条
        List<BlackNumberInfo> blackNumberList= new ArrayList<>();
        while(cursor.moveToNext()){
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.phone=cursor.getString(1);
            blackNumberInfo.mode=cursor.getString(2);
            blackNumberList.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        Log.i("Life","数据库信息条数"+blackNumberList.size());
        return blackNumberList;
    }

    /**
     * 获取条目总数
     * @return 返回0表示0条消息或异常
     */
    public int getCount(){
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from blacknumber;",null);
        int count = 0;
        if (cursor.moveToNext()){
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return  count;
    }
}
