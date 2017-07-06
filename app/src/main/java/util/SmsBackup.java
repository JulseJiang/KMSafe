package util;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;
import android.widget.ProgressBar;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 短信备份引擎
 * 传递一个进度条所在的对话框
 * 传递一个进度条
 * Created by jules on 2017/7/4.
 */
public class SmsBackup {
    public static void backup(Context ctx, String path,CallBack callBack){
        Cursor cursor=null;
        FileOutputStream fos=null;
        try {
            //需要用到对象上下文环境，备份文件夹路径，进度条所在的对话框对象用于备份过程中进度的更新
            //获取备份短信写入的文件
            File file = new File(path);
            //获取内容解析器
            cursor = ctx.getContentResolver().query(Uri.parse("content://sms/"),
                    new String[]{"address", "date", "type", "body"}, null, null, null);
            //准备文件相应的输出流
            fos = new FileOutputStream(file);
            //序列化数据库中读取的数据，放置到xml中
            XmlSerializer newSerializer = Xml.newSerializer();
            //设置xml
            newSerializer.setOutput(fos,"utf-8");
            //DTD(xml规范)
            newSerializer.startDocument("utf-8",true);
            newSerializer.startTag(null,"smss");
            //记录进度条的变化
            //如果传递进来的是对话进度框（进度条），制定对话框进度条的总数
            if (callBack!=null){
                callBack.setMax(cursor.getCount());
            }
            int index=0;
            //读取数据库中的每一行数据写到xml中
            while(cursor.moveToNext()){
                newSerializer.startTag(null,"sms");

                newSerializer.startTag(null,"address");
                newSerializer.text(cursor.getString(0));
                newSerializer.endTag(null,"address");

                newSerializer.startTag(null,"date");
                newSerializer.text(cursor.getString(1));
                newSerializer.endTag(null,"date");

                newSerializer.startTag(null,"type");
                newSerializer.text(cursor.getString(2));
                newSerializer.endTag(null,"type");

                newSerializer.startTag(null,"body");
                newSerializer.text(cursor.getString(3));
                newSerializer.endTag(null,"body");

                newSerializer.endTag(null,"sms");

                //每循环一次，需要让进度条叠加
                index++;
                //为了让进度条效果更加明显
                Thread.sleep(500);
                //progress更新进度可以在子线程中进行
                if (callBack!=null){
                    callBack.setProgress(index);
                }
            }
            newSerializer.endTag(null,"smss");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            if (cursor!=null&&fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cursor.close();
            }
        }
    }
    //回调
    //1.定义一个接口
    //2.接口实现业务逻辑方法（短信总数设置，备份过程中短信百分比的更新）
    //3.传递一个实现了此接口的类的对象（至备份短信的工具类中）
    //4.传递进来的对象，在合适地方调用（设置短信总条数，百分比）
    public interface CallBack{
        //短信总条数
        public void setMax(int max);
        //备份过程中百分比更新
        public void setProgress(int index);
    }
}
