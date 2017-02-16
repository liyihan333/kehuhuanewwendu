package com.kwsoft.kehuhua.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.config.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/25 0025.
 */

public class TeachAmountAdapter extends BaseAdapter {
    public List<Map<String, String>> list = new ArrayList<>();
    public Context context;

    public TeachAmountAdapter(List<Map<String, String>> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            //解析布局
            view = LayoutInflater.from(context).inflate(R.layout.activity_today_course_item, null);
            //创建ViewHolder持有类
            viewHolder = new ViewHolder();
            //将每个控件的对象保存到持有类中
            viewHolder.tv_course_name = (TextView) view.findViewById(R.id.tv_course_name);
            viewHolder.tv_course_time = (TextView) view.findViewById(R.id.tv_course_time);

            //将每个convertView对象中设置这个持有类对象
            view.setTag(viewHolder);
        } else
            //每次需要使用的时候都会拿到这个持有类
            viewHolder = (ViewHolder) view.getTag();

        Map<String, String> map = list.get(i);
        //然后可以直接使用这个类中的控件，对控件进行操作，而不用重复去findViewById了
        viewHolder.tv_course_name.setText(map.get("courseName"));
        viewHolder.tv_course_time.setText(map.get("courseTime"));
        return view;
    }

    class ViewHolder {
        public TextView tv_course_name, tv_course_time;
    }
}
