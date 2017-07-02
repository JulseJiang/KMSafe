package test;

import android.test.AndroidTestCase;

import db.dao.BlackNumberDao;

/**
 * 测试类开启失败
 * Created by jules on 2017/7/2.
 */

public class Test extends AndroidTestCase {
    public void insert(){
        //getContext:仅用于测试的环境
        BlackNumberDao dao = BlackNumberDao.getInstance(getContext());
        dao.insert("110","1");

    }
}
