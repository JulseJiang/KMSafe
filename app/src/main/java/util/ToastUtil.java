package util;

import android.content.Context;
import android.widget.Toast;

/**
 * 维护Toast，防止忘记使用show方法
 * Created by jules on 2017/6/27.
 */

public class ToastUtil {
    public static  void show(Context ctx,String msg){
        Toast.makeText(ctx,msg,Toast.LENGTH_SHORT).show();
    }
}
