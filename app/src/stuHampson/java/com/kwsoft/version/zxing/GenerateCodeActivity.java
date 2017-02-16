package com.kwsoft.version.zxing;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.config.Constant;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import cn.bingoogolapple.qrcode.zxingdemo.TestGeneratectivity;

/**
 * Created by Administrator on 2017/1/18 0018.
 */

public class GenerateCodeActivity extends AppCompatActivity {

    private ImageView iv;
    private String classUrl="";
    private static final String TAG = "GenerateCodeActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_code);
        iv = (ImageView) findViewById(R.id.iv_english);
        classUrl = Constant.sysUrl+"kq_readCard.do?stuId="+Constant.USERID;
        Log.e(TAG, "onCreate: "+classUrl);
        createAttendQRCode();
    }

    private void createAttendQRCode() {
        /*
        这里为了偷懒，就没有处理匿名 AsyncTask 内部类导致 Activity 泄漏的问题
        请开发在使用时自行处理匿名内部类导致Activity内存泄漏的问题，处理方式可参考 https://github.com/GeniusVJR/LearningNotes/blob/master/Part1/Android/Android%E5%86%85%E5%AD%98%E6%B3%84%E6%BC%8F%E6%80%BB%E7%BB%93.md
         */
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                return QRCodeEncoder.syncEncodeQRCode(classUrl, BGAQRCodeUtil.dp2px(GenerateCodeActivity.this, 200));
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    iv.setImageBitmap(bitmap);
                } else {
                    Toast.makeText(GenerateCodeActivity.this, "生成二维码失败", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}
