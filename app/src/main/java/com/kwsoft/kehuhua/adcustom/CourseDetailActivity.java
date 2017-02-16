package com.kwsoft.kehuhua.adcustom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kwsoft.kehuhua.widget.CommonToolbar;

import static com.kwsoft.kehuhua.config.Constant.topBarColor;

public class CourseDetailActivity extends AppCompatActivity {

    private static final String TAG = "CourseDetailActivity";
    private TextView startTime, endTime, tvdetail, zhangJieMingCheng, zhangJieRenWu, zhangJieMiaoShu;
    private CommonToolbar mToolbar;
    private LinearLayout llzhangJieMingCheng, llzhangJieRenWu, llzhangJieMiaoShu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        Log.e(TAG, "onCreate: 1");
        startTime = (TextView) findViewById(R.id.start_time);
        endTime = (TextView) findViewById(R.id.end_time);
        tvdetail = (TextView) findViewById(R.id.course_content);
        zhangJieMingCheng = (TextView) findViewById(R.id.CHAPTER_NAME);
        zhangJieRenWu = (TextView) findViewById(R.id.CHAPTER_TASK);
        zhangJieMiaoShu = (TextView) findViewById(R.id.CHAPTER_DESCRIBE);
        mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);
        llzhangJieMingCheng = (LinearLayout) findViewById(R.id.ll_CHAPTER_NAME);
        llzhangJieRenWu= (LinearLayout) findViewById(R.id.ll_CHAPTER_TASK);
        llzhangJieMiaoShu= (LinearLayout) findViewById(R.id.ll_CHAPTER_DESCRIBE);

        mToolbar.setBackgroundColor(getResources().getColor(topBarColor));
        //左侧返回按钮
        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            String sTimeStr = intent.getStringExtra("sTimeStr");
            String eTimeStr = intent.getStringExtra("eTimeStr");
            String content = intent.getStringExtra("content");
            String content1 = content.replaceAll(",", "\n");
            String content2 = content1.replaceAll("_", "：");
            String content3 = content2.replaceAll(":", "：");

            String CHAPTER_DESCRIBE = intent.getStringExtra("CHAPTER_DESCRIBE");
            if (CHAPTER_DESCRIBE != null && CHAPTER_DESCRIBE.length() > 0) {
                String CHAPTER_NAME = intent.getStringExtra("CHAPTER_NAME");
                String CHAPTER_TASK = intent.getStringExtra("CHAPTER_TASK");
                zhangJieMingCheng.setText(!CHAPTER_NAME.equals("null") ? CHAPTER_NAME : getString(R.string.no_content));
                zhangJieRenWu.setText(!CHAPTER_TASK.equals("null") ? CHAPTER_TASK : getString(R.string.no_content));
                zhangJieMiaoShu.setText(!CHAPTER_DESCRIBE.equals("null") ? CHAPTER_DESCRIBE : getString(R.string.no_content));
            }else {
                llzhangJieMingCheng.setVisibility(View.GONE);
                llzhangJieRenWu.setVisibility(View.GONE);
                llzhangJieMiaoShu.setVisibility(View.GONE);
            }
            startTime.setText(sTimeStr);
            endTime.setText(eTimeStr);
            tvdetail.setText(content3);
        }
        mToolbar.setTitle(getString(R.string.course_detail));
    }
}
