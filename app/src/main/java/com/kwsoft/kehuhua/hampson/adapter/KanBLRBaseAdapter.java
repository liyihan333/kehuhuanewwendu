package com.kwsoft.kehuhua.hampson.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.kwsoft.kehuhua.adapter.EmptyViewHolder;
import com.kwsoft.kehuhua.adcustom.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/8 0008.
 */

public class KanBLRBaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private List<Map<String, Object>> mDatas;
    private Context mContext;
    public OnRecyclerViewItemClickListener mOnItemClickListener = null;
    List<Map<String, Object>> menuListMap2Key = null;
    private static final int VIEW_TYPE = 1;
    public KanBLRBaseAdapter(List<Map<String, Object>> menuListMap2Key, List<Map<String, Object>> mDatas, Context mContext) {
        this.mDatas = mDatas;
//        this.mContext = mContext;
        this.menuListMap2Key = menuListMap2Key;
    }
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 加载数据item的布局，生成VH返回
        View view;
        mContext = parent.getContext();
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        Log.e(TAG, "onCreateViewHolder: mDatas "+mDatas.size());
        if (VIEW_TYPE == viewType) {
            Log.e(TAG, "onCreateViewHolder: VIEW_TYPE "+VIEW_TYPE+"     viewType"+viewType);
            view = mInflater.inflate(R.layout.empty_view, parent, false);
            return new EmptyViewHolder(view);
        }
        view = mInflater.inflate(R.layout.activity_kanbanlr_item, null);
        view.setOnClickListener(this);
        return new ComViewHolder(view);
    }

    private static final String TAG = "ListBaseAdapter";

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder thisholder, int position) {
        if (thisholder instanceof ComViewHolder) {
            final ComViewHolder holder = (ComViewHolder) thisholder;
            Map<String, Object> map = getData(position);
            for (int i = 0; i < menuListMap2Key.size(); i++) {
                Map<String,Object> menuKeyMap = menuListMap2Key.get(i);
                if (i==0){
                    String AFM_58 = String.valueOf(menuKeyMap.get("fieldAliasName"));
                    String left = String.valueOf(map.get(AFM_58));
                    holder.tv_left.setText(!left.equals("null") ? left : mContext.getResources().getString(R.string.no));
                }else {
                    String AFM_59 = String.valueOf(menuKeyMap.get("fieldAliasName"));
                    String right = String.valueOf(map.get(AFM_59));
                    holder.tv_right.setText(!right.equals("null") ? right : mContext.getResources().getString(R.string.no));
                }
            }
            holder.itemView.setTag(map);
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size() > 0 ? mDatas.size() : 1;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, String data);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(view, JSON.toJSONString(view.getTag()));
        }
    }

    // 可复用的VH
    class ComViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_left;
        public TextView tv_right;

        public ComViewHolder(View itemView) {
            super(itemView);
            tv_left = (TextView) itemView.findViewById(R.id.tv_left);
            tv_right = (TextView) itemView.findViewById(R.id.tv_right);
        }
    }


    /**
     * 获取单项数据
     */

    private Map<String, Object> getData(int position) {

        return mDatas.get(position);
    }

    /**
     * 获取全部数据
     */
    public List<Map<String, Object>> getDatas() {

        return mDatas;
    }

    /**
     * 清除数据
     */
    public void clearData() {

        mDatas.clear();
        notifyItemRangeRemoved(0, mDatas.size());
    }

}
