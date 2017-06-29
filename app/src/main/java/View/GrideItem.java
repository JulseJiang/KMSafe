package view;

import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by jules on 2017/6/29.
 */

public class GrideItem extends FrameLayout {
    // Item显示标题
    private String mTitle;
    //Item显示图片
    private int mIconId;
    // 标题Title
    private TextView tv_title;
    //  标题Title LayoutParams
    private LayoutParams mParams;
    public GrideItem(Context context, String mTitle,int mIconId) {
        super(context);
        this.mTitle = mTitle;
        this.mIconId=mIconId;
        // 初始化Item
        initCardItem();
    }

    /**
     * 初始化Item
     */
    private void initCardItem() {
        // 设置面板背景色，是由Frame拼起来的
        setBackgroundColor(Color.BLUE);
        tv_title = new TextView(getContext());
        tv_title.setTextSize(35);
        TextPaint tp = tv_title.getPaint();
        tp.setFakeBoldText(true);
        tv_title.setGravity(Gravity.CENTER);
        mParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        mParams.setMargins(5, 5, 5, 5);
        addView(tv_title, mParams);
    }
    public View getItemView() {
        return tv_title;
    }
    public String getmTitle() {
        return mTitle;
    }
}
