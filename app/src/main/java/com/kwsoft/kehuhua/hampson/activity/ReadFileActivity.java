package com.kwsoft.kehuhua.hampson.activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.hampson.adapter.ZuoYeImageGridViewAdapter;
import com.kwsoft.kehuhua.loadDialog.LoadingDialog;
import com.kwsoft.kehuhua.widget.CommonToolbar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import okhttp3.Call;

import static com.kwsoft.kehuhua.config.Constant.topBarColor;

/**
 * Created by Administrator on 2016/11/15 0015.
 *
 */

public class ReadFileActivity extends BaseActivity {

    private ZuoYeImageGridView zuoYeImageGridView;
    private String pathAndName="";
    private MediaPlayer playerNow;
    private List<String> imageDatas, ImageFileNames;
    private List<String> musicDatas, musicFileNames;
    private TextView seconds;
    private FrameLayout length;
    private LinearLayout ll_layout_audio;
    private String downLoadId;
    private static final String TAG = "ReadFileActivity";
    private List<Map<String, String>> fieldSet;
    private int position;
    private int mMinItemWith;// 设置对话框的最大宽度和最小宽度
    private int mMaxItemWith;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hampson_activity_read_file_layout);
        ButterKnife.bind(this);
        initView();
        getIntentData();
    }



    private void getIntentData() {
        Intent intent = getIntent();
        String positionStr = intent.getStringExtra("position");
        position = Integer.valueOf(positionStr);
        String fieldSetStr = intent.getStringExtra("fieldSet");
        downLoadId = intent.getStringExtra("downLoadId");
        fieldSet = JSON.parseObject(fieldSetStr,
                new TypeReference<List<Map<String, String>>>() {
                });
        showImage();
    }

    private void showImage() {
        List<String> mongoIds = new ArrayList<>();
        //获取mongoDB字符串
        Map<String, String> itemMap = fieldSet.get(position);
        String name = itemMap.get("fieldCnName");
        String value = itemMap.get("fieldCnName2");
        String[] downLoadIdArr = downLoadId.split(",");
        for (int m = 0; m < downLoadIdArr.length; m++) {
            mongoIds.add(downLoadIdArr[m]);
        }

        List<String> fileNames = new ArrayList<>();

        if (!value.equals("")) {
            String[] valueArr = value.split(",");
            for (int m = 0; m < valueArr.length; m++) {
                fileNames.add(valueArr[m]);
            }
        }

        if (mongoIds.size() > 0) {
            Log.e(TAG, "showImage: mongoIds " + mongoIds.toString());

            imageDatas = new ArrayList<>();
            ImageFileNames = new ArrayList<>();


            musicDatas = new ArrayList<>();
            musicFileNames = new ArrayList<>();

            //将mongodb分类
            for (int k = 0; k < fileNames.size(); k++) {
                String url = Constant.sysUrl + Constant.downLoadFileStr + mongoIds.get(k);
                String filename = fileNames.get(k);
                Log.e(TAG, "showImage: url " + url);
                Log.e(TAG, "showImage: filename " + filename);
                if (!filename.endsWith(".MP3") && !filename.endsWith(".mp3")) {
                    imageDatas.add(url);
                    ImageFileNames.add(filename);

                } else {
                    Log.e(TAG, "showImage: mp3   " + filename);
                    musicDatas.add(url);
                    musicFileNames.add(filename);

                }
            }


            ZuoYeImageGridViewAdapter gridViewAdapter = new ZuoYeImageGridViewAdapter(this, imageDatas, ImageFileNames);
            zuoYeImageGridView.setAdapter(gridViewAdapter);


            if (musicDatas.size() > 0) {
                ll_layout_audio.setVisibility(View.VISIBLE);
                downLoadMp3();
            }else {
                ll_layout_audio.setVisibility(View.GONE);
                dialog.dismiss();
            }
        }

    }


    @Override
    public void initView() {
        playerNow = new MediaPlayer();
        WindowManager wManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wManager.getDefaultDisplay().getMetrics(outMetrics);
        mMaxItemWith = (int) (outMetrics.widthPixels * 0.7f);
        mMinItemWith = (int) (outMetrics.widthPixels * 0.15f);
        CommonToolbar mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);
        mToolbar.setTitle(getString(R.string.view_attachments));
        mToolbar.setBackgroundColor(getResources().getColor(topBarColor));
        //左侧返回按钮
        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        zuoYeImageGridView = (ZuoYeImageGridView) findViewById(R.id.zuoYeImageGridView);
        seconds = (TextView) findViewById(R.id.recorder_time);
        length = (FrameLayout)findViewById(R.id.recorder_length);
        ll_layout_audio= (LinearLayout)findViewById(R.id.ll_layout_audio);
        try {
            length.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Log.e(TAG, "onTouch: 是否走length.setOnTouchListener");
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                        if (!pathAndName.equals("")&&playerNow!=null) {
    //                        play();
                            if (playerNow.isPlaying()) {
                                playerNow.stop();
                            }
                                try {
                                    playerNow.reset();//在开启播放的时候这段代码必须加，以便清除以前的进度
                                    playerNow.setDataSource(pathAndName);
                                    playerNow.prepare();
                                    playerNow.start();
                                } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                    Log.e(TAG, "onTouch: Mp3异常出现1 "+e.toString());
                                }

                        } else {
                            downLoadMp3();
                        }
                    }
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "initView: 播放音乐按钮异常");
        }

    }

    public void downLoadMp3() {
        dialog=new LoadingDialog(mContext,"mp3文件下加载中...");
        dialog.show();
        OkHttpUtils.get()//
                .url(musicDatas.get(0))
                .tag(this)//
                .build()
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getPath() + "/hampsonDownloadVoice/", musicFileNames.get(0)) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dialog.dismiss();
                        Toast.makeText(ReadFileActivity.this, R.string.voice_download_failed_please_click_reload, Toast.LENGTH_SHORT).show();

                    }
                    @Override
                    public void onResponse(File response, int id) {
                        playMp3(response);
                    }
                });

    }




    public void playMp3(File file)  {
      pathAndName=file.getPath();
        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(pathAndName);
            player.prepare();
            int secondDur = player.getDuration()/1000;
            String showSeconds=secondDur+"'";
            seconds.setText(showSeconds);
            ViewGroup.LayoutParams lParams = length.getLayoutParams();
            lParams.width = (int) (mMinItemWith + mMaxItemWith / 60f * secondDur) * 3;
            length.setLayoutParams(lParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (playerNow.isPlaying()) {
            playerNow.stop();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        playerNow.release();
    }
}
