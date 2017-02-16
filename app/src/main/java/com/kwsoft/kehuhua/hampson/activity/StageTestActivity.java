package com.kwsoft.kehuhua.hampson.activity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/7 0007.
 */

public class StageTestActivity extends BaseActivity {
    private TextView tv_year;
    private ListView listView;
    private List<Map<String, String>> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_test_list);

        initView();

        Map<String, String> map = new HashMap<>();
        map.put("month", "12月");
        map.put("day", "31日");
        map.put("title", "第二阶段模拟测试");
        map.put("score", "120");
        map.put("contentTitle", "成绩描述");
        map.put("content", "第一阶段第一阶段第一阶段第一阶段第一阶段第一阶段第一阶段第一阶段第一阶段第一阶段第一阶段第一阶段");
        list.add(map);
        list.add(map);
        list.add(map);
        list.add(map);
//        StageTestAdapter adapter = new StageTestAdapter(this,list);
//        listView.setAdapter(adapter);


    }

    @Override
    public void initView() {
        tv_year = (TextView) findViewById(R.id.tv_year);
        listView = (ListView) findViewById(R.id.lv_listview);
    }
}
