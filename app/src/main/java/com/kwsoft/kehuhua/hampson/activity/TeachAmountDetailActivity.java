package com.kwsoft.kehuhua.hampson.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.widget.CommonToolbar;

/**
 * Created by Administrator on 2016/11/25 0025.
 */

public class TeachAmountDetailActivity extends BaseActivity {
    private String courseName, courseTime;
    private TextView tv_classname, tv_class_hour;
    private CommonToolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teach_amount_detail);

        initView();

        Intent intent = getIntent();
        tv_classname.setText(intent.getStringExtra("courseName"));
        tv_class_hour.setText(intent.getStringExtra("courseTime"));
    }

    @Override
    public void initView() {
        tv_classname = (TextView) findViewById(R.id.tv_classname);
        tv_class_hour = (TextView) findViewById(R.id.tv_class_hour);
        mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);
        mToolbar.setTitle("detail");
        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
