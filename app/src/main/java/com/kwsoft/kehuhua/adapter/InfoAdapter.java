package com.kwsoft.kehuhua.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.hampson.activity.ReadFileActivity;

import java.util.List;
import java.util.Map;

import static android.view.View.GONE;

/**
 * Created by Administrator on 2016/11/14 0014.
 */

public class InfoAdapter extends BaseAdapter {

    private List<Map<String, String>> fieldSet;
    private Context context;
    private LayoutInflater inflater = null;
    private static final String TAG = "InfoAdapter";
    public static int is8 = 0;
    public static int is9 = 0;

    public InfoAdapter(Context context, List<Map<String, String>> fieldSet) {
        this.context = context;
        this.fieldSet = fieldSet;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return fieldSet.size();
    }

    @Override
    public Object getItem(int i) {
        return fieldSet.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.activity_info_item, null);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        TextView tv_entity_name = (TextView) view.findViewById(R.id.tv_entity_name);
        RelativeLayout info_item_layout = (RelativeLayout) view.findViewById(R.id.info_item_layout);
        ImageView fileRight = (ImageView) view.findViewById(R.id.fileRight);
        View info_item_line = view.findViewById(R.id.info_item_line);
        Map<String, String> itemMap = fieldSet.get(i);
        Log.e(TAG, "getView: itemMap " + itemMap.toString());
        String name = itemMap.get("fieldCnName");
        String value = itemMap.get("fieldCnName2");

        if (name.contains("mongo")) {
            info_item_layout.setVisibility(GONE);
            info_item_line.setVisibility(GONE);
        }
        tv_name.setText(name);
        //如果名称中包含附件两个字并且不包含mongodbid，就讲附件查看text显示 Attachment of AccomplishmentmongodbId
        if (!name.contains("mongo")) {
            if (name.contains(context.getString(R.string.attachment)) || name.contains("Attachment of Homework") || name.contains("Attachment of Accomplishment") || name.contains("Attachment of Correction")) {
//            if (name.contains("附件") && !name.contains("mongo")) {
                tv_entity_name.setVisibility(View.VISIBLE);
                //找到downLoadId
                String downLoadId = "";
                for (int f = 0; f < fieldSet.size(); f++) {//查找名称一致的name
                    String mongoName = String.valueOf(fieldSet.get(f).get("fieldCnName"));
                    Log.e(TAG, "getView: mongoName " + mongoName);
                    Log.e(TAG, "getView: name " + name);
                    if (mongoName.equals(name + "mongodbId")) {
                        downLoadId = String.valueOf(fieldSet.get(f).get("fieldCnName2"));//获取id
                        Log.e(TAG, "getView: downLoadId " + downLoadId);
                        break;
                    }
                }
                if (!value.equals("") && !value.equals("null")) {
                    fileRight.setVisibility(View.VISIBLE);

                    String[] valueArr = value.split(",");
                    String fileNum = valueArr.length + context.getString(R.string.attachments);
                    Log.e(TAG, "getView: fileNum " + fileNum);
                    tv_entity_name.setText(fileNum);
                    final String fieldSetString = JSON.toJSONString(fieldSet);
                    final String finalDownLoadId = downLoadId;
                    if (!downLoadId.equals("") && !downLoadId.equals("null")) {
                        Log.e(TAG, "getView: 能够响应附件点击事件");
                        tv_entity_name.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(context, ReadFileActivity.class);
                                intent.putExtra("position", i + "");
                                intent.putExtra("fieldSet", fieldSetString);
                                intent.putExtra("downLoadId", finalDownLoadId);
                                context.startActivity(intent);
                            }
                        });
                    }

                } else {
                    tv_entity_name.setText(context.getString(R.string.no_attachment));
                }
            } else {
                tv_entity_name.setText(value);
            }
        } else {
            tv_entity_name.setText(value);
        }
        return view;
    }
}
//            zuoYeImageGridView.setVisibility(View.VISIBLE);
//
//            //获取mongoDB字符串
//          String downLoadId="";
//            for (int k=0;k<fieldSet.size();k++) {
//
//               String fieldCnName= fieldSet.get(k).get("fieldCnName");
//
//                if (fieldCnName.equals("mongodbId")) {
//                    downLoadId=fieldSet.get(k).get("fieldCnName2");
//                }
//
//            }
//
//
//
//            Log.e(TAG, "getView: downLoadId "+downLoadId);
//            List<String> mongoIds=new ArrayList<>();
//            if (!downLoadId.equals("")) {
//             String[] downLoadIdArr=downLoadId.split(",");
//                for (int m=0;m<downLoadIdArr.length;m++) {
//                    mongoIds.add(downLoadIdArr[m]);
//                }
//            }
//            Log.e(TAG, "getView: mongoIds "+mongoIds.toString() );


//            List<String> fileNames=new ArrayList<>();