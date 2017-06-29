package view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 能够获取焦点的自定义TextView
 * Created by jules on 2017/6/28.
 */

public class FocusTextView extends TextView{
    //通过java代码创建控件--new
    public FocusTextView(Context context) {
        super(context);
    }
    //由系统调用（带属性+上下文环境构造方法+布局文件样式文件构造方法）--布局文件带属性
    public FocusTextView(Context context, AttributeSet attrs) {
        super(context,attrs);
    }
    public FocusTextView(Context context, AttributeSet attrs,int defStyle) {
        super(context,attrs,defStyle);
    }
    //重写获取焦点的方法,系统调用时默认有焦点--布局文件带属性带样式
    @Override
    public boolean isFocused() {
        return true;
    }

}
