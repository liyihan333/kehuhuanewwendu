package com.kwsoft.version;

import com.kwsoft.kehuhua.adcustom.base.BaseActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by Administrator on 2017/1/3 0003.
 */

public class StartActivity extends BaseActivity {
    private final int SPLASH_DISPLAY_LENGHT = 0; //睡时间为3秒3000
    private SharedPreferences preferences;
    private Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("wendustustartAty", Context.MODE_PRIVATE);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                //判断如果是第一次打开APP
                if (preferences.getBoolean("wendustufstart", true)) {
                    //则将firststart状态改为false，下次在登陆的时候就是
                    editor = preferences.edit();
                    editor.putBoolean("wendustufstart", false);
                    editor.commit(); //需要提交
                    Intent intent = new Intent();
                    //第一次启动跳到4个图片切换的欢迎界面
                    intent.setClass(StartActivity.this, AndyViewPagerActivity.class);
                    StartActivity.this.startActivity(intent);
                    StartActivity.this.finish();
                } else {//否则直接跳到第二个主页面
                    Intent intent = new Intent();
                    intent.setClass(StartActivity.this, StuProLoginActivity.class);
                    StartActivity.this.startActivity(intent);
                    StartActivity.this.finish();
                }

            }
        }, SPLASH_DISPLAY_LENGHT);
    }

    @Override
    public void initView() {

    }
}
