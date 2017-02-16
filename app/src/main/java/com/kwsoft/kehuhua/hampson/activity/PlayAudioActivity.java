package com.kwsoft.kehuhua.hampson.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.widget.CommonToolbar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import okhttp3.Call;

import static com.kwsoft.kehuhua.config.Constant.topBarColor;

/**
 * Created by Administrator on 2016/11/14 0014.
 *
 */

public class PlayAudioActivity extends BaseActivity {
    String imgPaths,fileName;
    private static final String TAG = "PlayAudioActivity";
    private CommonToolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hampson_activity_play_audio_layout);
        dialog.show();
        getIntentData();
        initView();



        OkHttpUtils.get()
                .url(imgPaths)
                .tag(this)
                .build()
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(),
                        fileName
                        ) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
dialog.dismiss();
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        dialog.dismiss();
                        Log.e(TAG, "onResponse: response "+response+" id "+id);
                  String filePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+fileName;
                        Log.e(TAG, "onResponse: filePath "+filePath );



//                        MediaManager.playSound(filePath,
//                                new MediaPlayer.OnCompletionListener() {
//
//                                    @Override
//                                    public void onCompletion(MediaPlayer mp) {
//                                        // viewanim.setBackgroundResource(R.id.id_recorder_anim);
//                                        viewanim.setBackgroundResource(R.mipmap.radio_voice_wifi);
//                                    }
//                                });
                        playAudio(filePath);

                    }
                });

    }

    private void getIntentData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            imgPaths = bundle.getString("imgPaths");
            fileName= bundle.getString("fileName");

            Log.e(TAG, "getInfoData: imgPaths "+imgPaths);

        }


        
    }
    private View viewanim;
    @Override
    public void initView() {
        mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);
        mToolbar.setTitle(getString(R.string.play_voice));
        mToolbar.setBackgroundColor(getResources().getColor(topBarColor));
        //左侧返回按钮
        mToolbar.setRightButtonIcon(getResources().getDrawable(R.mipmap.often_more));
        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
//        viewanim = findViewById(R.id.id_recorder_anim);
//        viewanim.setBackgroundResource(R.drawable.play);
//        AnimationDrawable drawable = (AnimationDrawable) viewanim
//                .getBackground();
//        drawable.start();
    }

    /**
     * 播放指定名称的歌曲
     * @param audioPath 指定默认播放的音乐
     */
    public  void playAudio(String audioPath){

        Uri uri = Uri.parse(audioPath);//替换成audiopath
               VideoView videoView = (VideoView)this.findViewById(R.id.video_view);
        videoView.setMediaController(new MediaController(this));
        videoView.setVideoURI(uri);
       videoView.start();
      videoView.requestFocus();
    }
}
