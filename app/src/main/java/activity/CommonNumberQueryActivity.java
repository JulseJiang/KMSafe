package activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import com.julse.jules.kmsafe.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jules on 2017/7/10.
 */
public class CommonNumberQueryActivity extends Activity{
    private ExpandableListView elv_common_number;
    private String[] mGroup = {"订餐电话","公共服务","运营商","快递服务","机票酒店"};
    private String[] childList = {"123","123","123","123"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_number);
        initUI();
        initData();
    }

    private void initData() {
//        CommonNumDao commonNumDao = new CommonNumDao();
    }

    private void initUI() {
        elv_common_number = findViewById(R.id.elv_common_number);
    }

//    private class CommonNumDao {
//        String[] groupList = {"订餐电话","公共服务","运营商","快递服务","机票酒店"};
//        String[] childList = {"123","123","123","123"};
//
//    }

    class MyAdapter extends BaseExpandableListAdapter{
        @Override
        public int getGroupCount() {
            return 0;
        }
        @Override
        public int getChildrenCount(int i) {
            return 0;
        }
        @Override
        public Object getGroup(int i) {
            return null;
        }
        @Override
        public String getChild(int i, int i1) {
            return null;
        }
        @Override
        public long getGroupId(int i) {
            return 0;
        }
        @Override
        public long getChildId(int i, int i1) {
            return 0;
        }
        @Override
        public boolean hasStableIds() {
            return false;
        }
        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            return null;
        }
        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            return null;
        }
        @Override
        public boolean isChildSelectable(int i, int i1) {
            return false;
        }
    }
}
