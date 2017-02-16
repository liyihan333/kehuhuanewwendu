package com.kwsoft.kehuhua.hampson.activity;

import android.os.Bundle;
import android.widget.ListView;

import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/8 0008.
 */

public class CourseRatingBarActivity extends BaseActivity {
    public ListView lv_listview;
    private List<Map<String, Object>> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_ratingbar_star_list);
        initView();
        Map<String, Object> map = new HashMap<>();
        map.put("title", "少儿一对一英语基础课程");
        map.put("teachName", "李明福");
        map.put("teachContent", "第一阶段第一阶段第一阶段第一阶段第一阶段第一阶段");
        String[] tags = {"一般", "没有新意", "作业太多", "我很认真", "讲课速度太快"};
        map.put("tags",tags);
        list.add(map);
        String[] tags1 = {"没有新意", "作业太多", "我很认真", "讲课速度太快"};
        map.put("tags",tags1);
        list.add(map);
        list.add(map);
        list.add(map);
//        CourseRatingBarAdapter adapter = new CourseRatingBarAdapter(this,list);
//        lv_listview.setAdapter(adapter);
    }

    @Override
    public void initView() {
        lv_listview= (ListView) findViewById(R.id.lv_listview);
    }
}
