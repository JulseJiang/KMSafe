package util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jules on 2017/6/29.
 */

public class SpUtils {
    private static   SharedPreferences sp;
    //写

    /**
     * 写入boolean类型变量到sp中
     * @param ctx 上下文
     * @param key 键
     * @param value 值
     */
    public  static  void putBoolean(Context ctx,String key,boolean value){
        //存储节点文件的名称，读写方式
        if (sp==null){
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key,value).commit();

    }
    //读

    /**
     * 读取boolean类型的标识
     * @param ctx 上下文
     * @param key 键
     * @param defValue 值:默认值或者此节点读取到的结果
     */
    public  static  boolean getBoolean(Context ctx,String key,boolean defValue){
        //存储节点文件的名称，读写方式
        if (sp==null){
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key,defValue);

    }
    /**
     * 写入boolean类型变量到sp中
     * @param ctx 上下文
     * @param key 键
     * @param value 值
     */
    public  static  void putString(Context ctx,String key,String value){
        //存储节点文件的名称，读写方式
        if (sp==null){
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putString(key,value).commit();

    }
    //读

    /**
     * 读取boolean类型的标识
     * @param ctx 上下文
     * @param key 键
     * @param defValue 值:默认值或者此节点读取到的结果
     */
    public  static  String getString(Context ctx,String key,String defValue){
        //存储节点文件的名称，读写方式
        if (sp==null){
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getString(key,defValue);

    }

    /**
     * 根据上下文和节点名删除节点
     * @param ctx
     * @param key
     */
    public static void remove(Context ctx, String key) {
        if (sp==null){
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().remove(key).commit();
    }
}

