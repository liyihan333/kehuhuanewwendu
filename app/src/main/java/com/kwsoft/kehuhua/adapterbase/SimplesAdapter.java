package com.kwsoft.kehuhua.adapterbase;

import android.content.Context;

import java.util.List;

/**
 * Created by <a href="http://www.cniao5.com">菜鸟窝</a>
 * 一个专业的Android开发在线教育平台
 */
public abstract class SimplesAdapter<T> extends BaseAdapter<T,BaseViewHolder> {

    public SimplesAdapter(Context context, int layoutResId) {
        super(context, layoutResId);
    }

    public SimplesAdapter(Context context, int layoutResId, List<T> datas) {
        super(context, layoutResId, datas);
    }


}
