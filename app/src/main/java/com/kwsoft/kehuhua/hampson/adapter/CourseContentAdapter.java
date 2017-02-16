package com.kwsoft.kehuhua.hampson.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.kwsoft.kehuhua.adcustom.R;

import java.util.List;
import java.util.Map;


public class CourseContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private List<List<Map<String, String>>> mDatas;
    private Context mContext;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private static final int VIEW_TYPE = 1;
    /**
     * 获取条目 View填充的类型
     * 默认返回0
     * 将lists为空返回 1
     */
    public int getItemViewType(int position) {
        if (mDatas.size() <= 0) {
            return VIEW_TYPE;
        }
        return super.getItemViewType(position);
    }


    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, String data);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public CourseContentAdapter(List<List<Map<String, String>>> mDatas) {
        this.mDatas = mDatas;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        mContext = parent.getContext();
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        Log.e("TAG", "viewType:" + viewType);
        Log.e(TAG, "onCreateViewHolder: viewType "+viewType);
//        if (VIEW_TYPE == viewType) {
//            view = mInflater.inflate(R.layout.empty_view, parent, false);
//
//            return new EmptyViewHolder(view);
//        }
        view = mInflater.inflate(R.layout.activity_course_list_item, null);
        //将创建的View注册点击事件
        view.setOnClickListener(this);
        Log.e(TAG, "onCreateViewHolder: view "+view);
        return new CourseContentHolder(view);
    }

    private static final String TAG = "CourseContentAdapter";
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder thisHolder, int position) {
        if (thisHolder instanceof CourseContentHolder) {
            final CourseContentHolder holder = (CourseContentHolder) thisHolder;
            List<Map<String, String>> item = getData(position);
            Log.e(TAG, "onBindViewHolder: item "+item.toString());
            holder.tv_time.setText(item.get(0).get("fieldCnName2"));
            holder.tv_title.setText(item.get(1).get("fieldCnName2"));
            //老师
            holder.teach_title.setText(item.get(2).get("fieldCnName"));
            holder.tv_teach_name.setText(item.get(2).get("fieldCnName2"));
            //教学内容
            holder.teach_content_title.setText(item.get(3).get("fieldCnName"));
            holder.tv_teach_content.setText(item.get(3).get("fieldCnName2"));
            //考勤
            holder.tv_attence.setText(item.get(4).get("fieldCnName"));
            //课后作业
            holder.tv_homework.setText(item.get(5).get("fieldCnName"));
            holder.itemView.setTag(item);
        }

    }

    /**
     * 获取单项数据
     */

    private List<Map<String, String>> getData(int position) {

        return mDatas.get(position);
    }

    /**
     * 获取全部数据
     */
    public List<List<Map<String, String>>> getDatas() {

        return mDatas;
    }

    /**
     * 清除数据
     */
    public void clear() {

        mDatas.clear();
        notifyItemRangeRemoved(0, mDatas.size());
    }


    /**
     * 下拉刷新更新数据
     */
    public void addData(List<List<Map<String, String>>> datas) {

        addData(0, datas);
    }

    /**
     * 上拉加载添加数据的方法
     */
    public void addData(int position, List<List<Map<String, String>>> datas) {

        if (datas != null && datas.size() > 0) {

            mDatas.addAll(datas);
            notifyItemRangeChanged(position, mDatas.size());
        }

    }

    @Override
    public int getItemCount() {

        return mDatas.size() > 0 ? mDatas.size() : 1;

    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(view, JSON.toJSONString(view.getTag()));
        }
    }
}