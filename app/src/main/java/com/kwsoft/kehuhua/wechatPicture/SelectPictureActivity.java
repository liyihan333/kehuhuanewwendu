package com.kwsoft.kehuhua.wechatPicture;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.audiorecordmp3demo.view.AudioRecordView;
import com.kwsoft.kehuhua.adcustom.OperateDataActivity;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.utils.DataProcess;
import com.kwsoft.kehuhua.wechatPicture.andio.MediaManager;
import com.kwsoft.kehuhua.widget.CommonToolbar;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import me.iwf.photopicker.PhotoPicker;
import okhttp3.Call;

import static com.kwsoft.kehuhua.config.Constant.img_Paths;
import static com.kwsoft.kehuhua.config.Constant.pictureUrl;
import static com.kwsoft.kehuhua.config.Constant.sysUrl;
import static com.kwsoft.kehuhua.config.Constant.topBarColor;

/**
 * Created by Administrator on 2016/10/13 0013.
 */

public class SelectPictureActivity extends BaseActivity implements View.OnClickListener {

    private AudioRecordView mAudioRecordView;
    private LinearLayout mResultView;
    private ImageView mVolumeView;
    private TextView mAudioSecondView;

    /**
     * 录音文件路径
     */
    private String mAudioRecordFilePath = null;
    private AnimationDrawable mAnimation;

    private CommonToolbar mToolbar;
    String position, fieldRole;
    @Bind(R.id.gridView)
    GridView gridView;
    private ArrayList<String> imgPaths = new ArrayList<>();
    private PhotoPickerAdapter adapter;

    String codeListStr = "";
    private static final String TAG = "SelectPictureActivity";
    private WaterWaveProgress waveProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_picture_layout);
        ButterKnife.bind(this);
        initView();
        //展示音频
        initAudioView();
    }

    public void initView() {
        Intent intent = getIntent();
        position = intent.getStringExtra("position");
        fieldRole = intent.getStringExtra("fieldRole");

        mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);
        if (fieldRole.equals("18")) {
            mToolbar.setTitle(getString(R.string.single_file_selection));
        } else if (fieldRole.equals("19")) {
            mToolbar.setTitle(getString(R.string.multiple_file_selection));
        }

        mToolbar.setBackgroundColor(getResources().getColor(topBarColor));
        //左侧返回按钮
        mToolbar.setRightButtonIcon(getResources().getDrawable(R.mipmap.nav_scan_file));
        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mToolbar.showRightImageButton();

        //右侧下拉按钮
        mToolbar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFile();//收集文件
                Log.e(TAG, "onClick: 开始上传音频文件fieldRole " + fieldRole);
                Log.e(TAG, "onClick: myFile" + myFile.size());
                if (myFile.size() > 0) {
                    uploadMethod();//递归上传
                } else {
                    Toast.makeText(SelectPictureActivity.this, R.string.please_select_at_least_one_file, Toast.LENGTH_SHORT).show();
                }


            }
        });

        waveProgress = (WaterWaveProgress) findViewById(R.id.waterWaveProgress1);

        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        //  int height = wm.getDefaultDisplay().getHeight();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) waveProgress.getLayoutParams();
        //layoutParams.setMargins(width/4,12,10,5);//4个参数按顺序分别是左上右下
        layoutParams.setMarginStart(width / 3);
        waveProgress.setLayoutParams(layoutParams); //mView是控件
        adapter = new PhotoPickerAdapter(imgPaths);

        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imgPaths.size()) {
                    PermissionGen.with(SelectPictureActivity.this)
                            .addRequestCode(100)
                            .permissions(
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .request();
//                    PhotoPicker.builder()
//                            .setPhotoCount(9)
//                            .setShowCamera(true)
//                            .setSelected(imgPaths)
//                            .setShowGif(true)
//                            .setPreviewEnabled(true)
//                            .start(SelectPictureActivity.this, PhotoPicker.REQUEST_CODE);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("imgPaths", imgPaths);
                    bundle.putInt("position", position);
                    goToActivityForResult(SelectPictureActivity.this, EnlargePicActivity.class, bundle, position);
                }
            }
        });


    }

    private void initAudioView() {

        mAudioRecordView = (AudioRecordView) findViewById(com.example.audiorecordmp3demo.R.id.audioRecordView);
        mAudioRecordView.setAudioRecordFinishListener(new MyAudioRecordFinishListener());
        mResultView = (LinearLayout) findViewById(com.example.audiorecordmp3demo.R.id.ll_result);
        mVolumeView = (ImageView) findViewById(com.example.audiorecordmp3demo.R.id.iv_volume);
        mAudioSecondView = (TextView) findViewById(com.example.audiorecordmp3demo.R.id.tv_audio_second);

        // 点击取消当前录音
        findViewById(com.example.audiorecordmp3demo.R.id.iv_cancle_audio_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 删除当前录音文件
                mAudioRecordView.deleteRecorderPath();
                mResultView.setVisibility(View.GONE);
                mAudioRecordView.setVisibility(View.VISIBLE);
                com.example.audiorecordmp3demo.manager.MediaManager.pause();
            }
        });

        mResultView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAudioRecordFilePath == null)
                    return;

                // 播放动画
                if (mAnimation != null) {
                    mVolumeView.setBackgroundResource(com.example.audiorecordmp3demo.R.drawable.ic_voice1);
                }
                mVolumeView.setBackgroundResource(com.example.audiorecordmp3demo.R.drawable.anim_play_audio);
                mAnimation = (AnimationDrawable) mVolumeView.getBackground();
                mAnimation.start();

                // 播放音频
                com.example.audiorecordmp3demo.manager.MediaManager.playSound(mAudioRecordFilePath,
                        new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mVolumeView.setBackgroundResource(com.example.audiorecordmp3demo.R.drawable.ic_voice1);
                            }
                        });
            }
        });
    }

    /**
     * 录音完成回调监听
     */
    class MyAudioRecordFinishListener implements AudioRecordView.AudioRecordFinishListener {
        @Override
        public void onFinish(float second, String filePath) {
            mAudioRecordFilePath = filePath;
            mResultView.setVisibility(View.VISIBLE);
            mAudioRecordView.setVisibility(View.GONE);
            // 设置录音秒数
            String sec = String.valueOf(second);
            sec = sec.substring(0, sec.indexOf("."));
            mAudioSecondView.setText(sec + "s");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = 106)
    public void doSomething() {
        Toast.makeText(this, R.string.permission_Access_success, Toast.LENGTH_SHORT).show();
    }

    @PermissionFail(requestCode = 106)
    public void doFailSomething() {
        Toast.makeText(this, R.string.permission_Access_failed, Toast.LENGTH_SHORT).show();
    }


    @PermissionSuccess(requestCode = 100)
    public void doCapture() {
        PhotoPicker.builder()
                .setPhotoCount(9)
                .setShowCamera(true)
                .setSelected(imgPaths)
                .setShowGif(true)
                .setPreviewEnabled(true)
                .start(SelectPictureActivity.this, PhotoPicker.REQUEST_CODE);
    }

    @PermissionFail(requestCode = 100)
    public void doFailedCapture() {
        Toast.makeText(SelectPictureActivity.this,R.string.permission_Access_failed, Toast.LENGTH_SHORT).show();
    }

    public void goToActivityForResult(Context context, Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent(context, cls);
        if (bundle == null) {
            bundle = new Bundle();
        }
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                imgPaths.clear();
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                imgPaths.addAll(photos);
                adapter.notifyDataSetChanged();
            }
        }

        if (resultCode == RESULT_OK && requestCode >= 0 && requestCode <= 8) {
            imgPaths.remove(requestCode);
            adapter.notifyDataSetChanged();
        }
        img_Paths.clear();
        img_Paths.addAll(imgPaths);

    }

    List<File> myFile = new ArrayList<>();


    //上传文件
    public void getFile() {
        waveProgress.setVisibility(View.VISIBLE);
        waveProgress.setProgress(0);

        //待上传的两个文件

        //图片
        if (img_Paths.size() >= 0) {
            for (int i = 0; i < img_Paths.size(); i++) {
                File file = new File(img_Paths.get(i));
                myFile.add(file);
            }
        }
        //音频
        if (mAudioRecordFilePath != null) {
            File file = new File(mAudioRecordFilePath);
            myFile.add(file);
        }
    }

    int num = 0;

    public void uploadMethod() {

        String url = sysUrl + pictureUrl;
        Log.e(TAG, "uploadMethod: 开始上传文件" + myFile.get(num).toString());
//        if (files.size() > 0) {
        OkHttpUtils.post()//
                .addFile("myFile", myFile.get(num).getName(), myFile.get(num))
                .url(url)
                .build()
                .execute(new EdusStringCallback(SelectPictureActivity.this) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ErrorToast.errorToast(mContext, e);
                        waveProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            if (num + 1 < myFile.size()) {//一直请求到最后一个
                                num++;
                                getFileCode(response);
                                uploadMethod();
                            } else {//已达上限，返回关联添加页面
                                Toast.makeText(SelectPictureActivity.this, R.string.upload_success + (num + 1) + getString(R.string.ge), Toast.LENGTH_SHORT).show();
                                getFileCode(response);
                                jump2Activity();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {

                    }
                });
    }

    List<String> codeList = new ArrayList<>();

    //解析文件上传成功的code值
    private void getFileCode(String response) {
        if (num + 1 == myFile.size()) {
            waveProgress.setProgress(100);
            waveProgress.setVisibility(View.GONE);
        } else {
            waveProgress.setProgress((int) (100 * (num) / myFile.size()));
        }

        String[] valueTemp1 = response.split(":");
        String valueCode = valueTemp1[1];
        codeList.add(valueCode);
    }

    private void jump2Activity() {
        codeListStr = DataProcess.listToString(codeList);
        Intent intentTree = new Intent();
        intentTree.setClass(SelectPictureActivity.this, OperateDataActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("position", position);
        bundle.putString("codeListStr", codeListStr);
        intentTree.putExtra("bundle", bundle);
        setResult(101, intentTree);
        this.finish();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.pause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MediaManager.resume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MediaManager.release();
        mAudioRecordView.deleteRecorderPath();
    }
}
