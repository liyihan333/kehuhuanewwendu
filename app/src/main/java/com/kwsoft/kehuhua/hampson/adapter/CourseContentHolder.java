package com.kwsoft.kehuhua.hampson.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kwsoft.kehuhua.adcustom.R;

/**
 * Created by Administrator on 2016/10/21 0021.
 *
 */

public class CourseContentHolder extends RecyclerView.ViewHolder {
    View mView;
    TextView tv_time,teach_title,
            teach_content_title,
            tv_title,tv_teach_name ,
            tv_teach_content,
            tv_attence,tv_homework;
    ImageView iv_attence,iv_homework;


    CourseContentHolder(View view) {
        super(view);
        mView = view;
        tv_time = (TextView) view.findViewById(R.id.tv_time);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        teach_title=(TextView) view.findViewById(R.id.teach_title);

        teach_content_title=(TextView) view.findViewById(R.id.teach_content_title);
        tv_homework = (TextView) view.findViewById(R.id.tv_homework);
        tv_teach_name = (TextView) view.findViewById(R.id.tv_teach_name);
        tv_teach_content = (TextView) view.findViewById(R.id.tv_teach_content);
        tv_attence = (TextView) view.findViewById(R.id.tv_attence);
        iv_attence = (ImageView) view.findViewById(R.id.iv_attence);
        iv_homework = (ImageView) view.findViewById(R.id.iv_homework);
    }
}
