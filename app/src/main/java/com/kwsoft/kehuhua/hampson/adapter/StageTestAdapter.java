package com.kwsoft.kehuhua.hampson.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kwsoft.kehuhua.adcustom.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by Administrator on 2016/11/7 0007.
 *
 */

public class StageTestAdapter extends BaseAdapter {
    private Context mContext;
    private List<List<Map<String, String>>> list = new ArrayList<>();

    public StageTestAdapter(Context mContext, List<List<Map<String, String>>> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolderStage holder = null;
        if (view == null) {
            //解析布局
            view = LayoutInflater.from(mContext).inflate(R.layout.activity_stage_test_list_item, null);
            //创建ViewHolderStage持有类
            holder = new ViewHolderStage();
            //将每个控件的对象保存到持有类中
            holder.tv_year= (TextView) view.findViewById(R.id.tv_year);
            holder.tv_month = (TextView) view.findViewById(R.id.tv_month);
            holder.tv_day = (TextView) view.findViewById(R.id.tv_day);
            holder.tv_title = (TextView) view.findViewById(R.id.tv_title);
            holder.tv_score = (TextView) view.findViewById(R.id.tv_score);
            holder.tv_content_title = (TextView) view.findViewById(R.id.tv_content_title);
            holder.tv_content = (TextView) view.findViewById(R.id.tv_content);
            //将每个convertView对象中设置这个持有类对象
            view.setTag(holder);
        }
        //每次需要使用的时候都会拿到这个持有类
        holder = (ViewHolderStage) view.getTag();
        List<Map<String, String>> map = list.get(i);
        //然后可以直接使用这个类中的控件，对控件进行操作，而不用重复去findViewById了
        Log.e(TAG, "getView: 成长轨迹单项map "+map.toString());
        //成绩标题
        holder.tv_title.setText(map.get(0).get("fieldCnName2"));
        //分数
        holder.tv_score.setText(map.get(1).get("fieldCnName2"));

        //成绩描述
        holder.tv_content_title.setText(map.get(2).get("fieldCnName"));
        holder.tv_content.setText(map.get(2).get("fieldCnName2"));

       //考试日期

        String date=map.get(3).get("fieldCnName2");
        Log.e(TAG, "getView: date "+date);
//分别获取年月日
        String cpYear=date.substring(0,4);
        String cpMonth=date.substring(5,7)+"月";
        String cpDay=date.substring(8,10)+"日";
        //年
        holder.tv_year.setText(cpYear);
        //月
        holder.tv_month.setText(cpMonth);
        //日
        holder.tv_day.setText(cpDay);

        return view;
    }

   static class  ViewHolderStage {
        TextView tv_year,tv_month, tv_day, tv_title, tv_score, tv_content_title, tv_content;
    }


    public void clear(){
        list.clear();
        notifyDataSetChanged();
    }
    public void addData(List<List<Map<String, String>>> addData){
        list.addAll(addData);
        notifyDataSetChanged();
    }

    public List<List<Map<String, String>>> getDatas(){
        return list;

    }
}
