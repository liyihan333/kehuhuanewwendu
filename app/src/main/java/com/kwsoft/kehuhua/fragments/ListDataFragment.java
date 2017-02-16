package com.kwsoft.kehuhua.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/19 0019.
 *
 */
public class ListDataFragment extends Fragment {


    @Bind(R.id.lv)
    RecyclerView lv;
    @Bind(R.id.refresh_layout)
    MaterialRefreshLayout mRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_list_data_fragment, container, false);
        ButterKnife.bind(this, view);

        ((BaseActivity) getActivity()).dialog.show();
        initRefreshLayout();//初始化空间
//        initView();
//        getDataIntent();//获取初始化数据
//        getData();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
    //初始化SwipeRefreshLayout
    private void initRefreshLayout() {
        mRefreshLayout.setLoadMore(true);
        mRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {

//                refreshData();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
//
//                if (mAdapter!=null&&mAdapter.getItemCount() < totalNum) {
//
//                    loadMoreData();
//                } else {
////                    Snackbar.make(mRecyclerView, "没有更多了", Snackbar.LENGTH_SHORT).show();
//                    mRefreshLayout.finishRefreshLoadMore();
//                }
            }
        });
    }

}