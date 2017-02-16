package com.kwsoft.kehuhua.wechatPicture.andio;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.kwsoft.kehuhua.adcustom.R;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Mp3ListAdapter extends BaseAdapter {

    private List<File> dataList;
    private LayoutInflater inflater;
    private String path;
    private int mMinItemWith;// 设置对话框的最大宽度和最小宽度
    private int mMaxItemWith;
    private Context context;
    private static final String TAG = "Mp3ListAdapter";


    public Mp3ListAdapter(Context context, List<File> dataList) {

        Log.e(TAG, "Mp3ListAdapter: 适配器初始化"+dataList.size());
        inflater = LayoutInflater.from(context);
        this.dataList=dataList;
        this.context=context;
        path= Environment.getExternalStorageDirectory().getPath()+"/hampsonDownloadVoice/";
        // 获取系统宽度
        WindowManager wManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wManager.getDefaultDisplay().getMetrics(outMetrics);
        mMaxItemWith = (int) (outMetrics.widthPixels * 0.7f);
        mMinItemWith = (int) (outMetrics.widthPixels * 0.15f);
        Log.e(TAG, "Mp3ListAdapter: 适配器初始化完毕"+dataList.size());
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


            Log.e(TAG, "getView: 适配器开始getView");
            convertView = inflater.inflate(R.layout.audio_item_layout, null);


        TextView  seconds = (TextView) convertView.findViewById(R.id.recorder_time);
            final FrameLayout length = (FrameLayout)convertView.findViewById(R.id.recorder_length);
        ImageView item_icon_right = (ImageView) convertView.findViewById(R.id.item_icon_right);


        item_icon_right.setVisibility(View.GONE);
        MediaPlayer player1 = new MediaPlayer();
        Log.e(TAG, "getView: path + dataList.get(position).getName() "+path + dataList.get(position).getName());
        try {
            player1.setDataSource(path + dataList.get(position).getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        int secondDur = player1.getDuration();
        Log.e(TAG, "getView: 获取秒数" + secondDur);
        seconds.setText(secondDur);
        ViewGroup.LayoutParams lParams = length.getLayoutParams();
        lParams.width = (int) (mMinItemWith + mMaxItemWith / 60f * secondDur) * 3;
        length.setLayoutParams(lParams);


        length.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    length.setBackgroundColor(context.getResources().getColor(R.color.blue));

                    try {
                        MediaPlayer player = new MediaPlayer();
                        player.setDataSource(path + dataList.get(position).getName());
                        player.prepare();
                        player.start();
                    } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    length.setBackgroundColor(context.getResources().getColor(R.color.white));
                }
                return true;
            }
        });
        return convertView;
    }
}
