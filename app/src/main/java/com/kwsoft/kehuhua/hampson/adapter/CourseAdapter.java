package com.kwsoft.kehuhua.hampson.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kwsoft.kehuhua.adcustom.R;

import java.util.List;
import java.util.Map;

import static com.kwsoft.kehuhua.adcustom.R.id.tv_attence;

/**
 * Created by Administrator on 2016/11/8 0008.
 */

public class CourseAdapter extends BaseAdapter {
    private Context mContext;
    private List<List<Map<String, String>>> mDatas;

    public CourseAdapter(Context mContext, List<List<Map<String, String>>> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int i) {
        return mDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private static final String TAG = "CourseAdapter";
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            //解析布局
            view = LayoutInflater.from(mContext).inflate(R.layout.activity_course_list_item, null);
            //创建ViewHolder持有类
            holder = new ViewHolder();
            //将每个控件的对象保存到持有类中
            holder.tv_time = (TextView) view.findViewById(R.id.tv_time);
            holder.tv_title = (TextView) view.findViewById(R.id.tv_title);
            holder.tv_homework = (TextView) view.findViewById(R.id.tv_homework);
            holder.teach_title=(TextView) view.findViewById(R.id.teach_title);
            holder.tv_teach_name = (TextView) view.findViewById(R.id.tv_teach_name);
            holder.teach_content_title=(TextView) view.findViewById(R.id.teach_content_title);

            holder.tv_teach_content = (TextView) view.findViewById(R.id.tv_teach_content);
            holder.tv_attence = (TextView) view.findViewById(tv_attence);
            holder.iv_attence = (ImageView) view.findViewById(R.id.iv_attence);
            holder.iv_homework = (ImageView) view.findViewById(R.id.iv_homework);
            //将每个convertView对象中设置这个持有类对象
            view.setTag(holder);
        }
        //每次需要使用的时候都会拿到这个持有类
        holder = (ViewHolder) view.getTag();
        List<Map<String, String>> item = mDatas.get(i);
        //然后可以直接使用这个类中的控件，对控件进行操作，而不用重复去findViewById了
        Log.e(TAG, "onBindViewHolder: item "+item.toString());
        holder.tv_time.setText(item.get(0).get("fieldCnName2"));
        holder.tv_title.setText(item.get(1).get("fieldCnName2"));
        //老师
        holder.teach_title.setText(item.get(2).get("fieldCnName"));
        holder.tv_teach_name.setText(item.get(2).get("fieldCnName2"));
        //教学内容
        holder.teach_content_title.setText(item.get(3).get("fieldCnName"));
        holder.tv_teach_content.setText(item.get(3).get("fieldCnName2"));
//        //考勤
//        holder.tv_attence.setText(item.get(4).get("fieldCnName"));
//        //课后作业
//        holder.tv_homework.setText(item.get(5).get("fieldCnName"));

        return view;
    }

    class ViewHolder {
        TextView tv_time,
                tv_title,
                tv_teach_name ,
                tv_teach_content,
                tv_attence,tv_homework,
                teach_title,
                teach_content_title;
        ImageView iv_attence,iv_homework;
    }

    public void clear(){
        mDatas.removeAll(mDatas);
        notifyDataSetChanged();
    }
    public void addData(List<List<Map<String, String>>> addData){
        mDatas.addAll(addData);
        notifyDataSetChanged();
    }

    public List<List<Map<String, String>>> getDatas(){
        return mDatas;

    }
}
