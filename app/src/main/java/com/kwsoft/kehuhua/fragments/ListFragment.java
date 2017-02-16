package com.kwsoft.kehuhua.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.kwsoft.kehuhua.adapter.ListAdapter2;
import com.kwsoft.kehuhua.adcustom.InfoActivity;
import com.kwsoft.kehuhua.adcustom.InfoTwoActivity;
import com.kwsoft.kehuhua.adcustom.ListActivity4;
import com.kwsoft.kehuhua.adcustom.OperateDataActivity;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.utils.DataProcess;
import com.kwsoft.kehuhua.view.RecycleViewDivider;
import com.kwsoft.kehuhua.view.WrapContentLinearLayoutManager;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**

 */
public class ListFragment extends Fragment {
    @Bind(R.id.lv)
    RecyclerView mRecyclerView;
    @Bind(R.id.searchButton)
    ImageView searchButton;
    @Bind(R.id.refresh_layout)
    MaterialRefreshLayout mRefreshLayout;


    private String tableId, pageId;


    private int totalNum = 0;
    private int start = 0;
    private final int limit = 20;
    private static final int STATE_NORMAL = 0;
    private static final int STATE_REFREH = 1;
    private static final int STATE_MORE = 2;
    private int state = STATE_NORMAL;


    private String operaButtonSet;
    private List<List<Map<String, String>>> datas;
    private ListAdapter2 mAdapter;
    private List<Map<String, Object>> childTab;
    private List<Map<String, Object>> childList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, view);
        ((BaseActivity) getActivity()).dialog.show();
        initRefreshLayout();//初始化控件
        getDataIntent();//获取初始化数据
        getData();
        IntentFilter filter = new IntentFilter(OperateDataActivity.action);
        getActivity().registerReceiver(broadcastReceiver, filter);
        return view;
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            refreshData();
        }
    };

    //初始化SwipeRefreshLayout
    private void initRefreshLayout() {
        mRefreshLayout.setLoadMore(true);
        mRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                refreshData();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                if (mAdapter != null && mAdapter.getItemCount() < totalNum) {
                    loadMoreData();
                } else {
//                    Snackbar.make(mRecyclerView, "没有更多了", Snackbar.LENGTH_SHORT).show();
                    mRefreshLayout.finishRefreshLoadMore();
                }
            }
        });
    }

    /**
     * 接收菜单传递过来的模块数据包
     */
    public Bundle listDataBundle;
    private Map<String, String> paramsMap;

    public void getDataIntent() {
        listDataBundle = getArguments();
        String paramsStr = listDataBundle.getString("listFragmentData");
        paramsMap = JSON.parseObject(paramsStr,
                new TypeReference<Map<String, String>>() {
                });
        tableId = paramsMap.get(Constant.tableId);
        pageId = paramsMap.get(Constant.pageId);
        Constant.mainTableIdValue = tableId;
        Constant.mainPageIdValue = pageId;
    }

    private static final String TAG = "ListFragment";

    /**
     * 获取字段接口数据
     */
    @SuppressWarnings("unchecked")
    public void getData() {
        if (((BaseActivity) getActivity()).hasInternetConnected()) {
            //地址
            String volleyUrl = Constant.sysUrl + Constant.requestListSet;
            Log.e("TAG", "列表请求地址：" + volleyUrl);
            //参数
            paramsMap.put("start", start + "");
            if (!Constant.stu_index.equals("")) {
                paramsMap.put("ctType", Constant.stu_index);
                Log.e(TAG, "getData: stu_index"+Constant.stu_index );
                paramsMap.put("SourceDataId", Constant.stu_homeSetId);
                paramsMap.put("pageType", "1");
                Log.e("TAG", "去看板的列表请求");
            }
            paramsMap.put("sessionId", Constant.sessionId);
            paramsMap.put("limit", limit + "");

            Log.e(TAG, "getData: paramsMap " + paramsMap.toString());
            //请求
            OkHttpUtils
                    .post()
                    .params(paramsMap)
                    .url(volleyUrl)
                    .build()
                    .execute(new EdusStringCallback(getActivity()) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            ErrorToast.errorToast(mContext, e);
                            mRefreshLayout.finishRefresh();
                            ((BaseActivity) getActivity()).dialog.dismiss();
                            backStart();
                            Log.e(TAG, "onError: Call  " + call + "  id  " + id);
                        }

                        @Override
                        public void onResponse(String response, int id) {
//                            Utils.printLog(TAG,"onResponse: "+ response);
                            setStore(response);
                        }
                    });
        } else {
            ((BaseActivity) getActivity()).dialog.dismiss();
            mRefreshLayout.finishRefresh();
            Toast.makeText(getActivity(), R.string.no_network, Toast.LENGTH_SHORT).show();
            backStart();
        }
    }

    private List<Map<String, Object>> buttonSet = new ArrayList<>();

    public void backStart() {

        //下拉失败后需要将加上limit的strat返还给原来的start，否则会获取不到数据
        if (state == STATE_MORE) {
            //start只能是limit的整数倍
            if (start > limit) {
                start -= limit;
            }
            mRefreshLayout.finishRefreshLoadMore();
        }
    }

    List<Map<String, Object>> operaButtonSetList;

    @SuppressWarnings("unchecked")
    public void setStore(String jsonData) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<Map<String, Object>> fieldSet = new ArrayList<>();

        try {
            Map<String, Object> setMap = JSON.parseObject(jsonData,
                    new TypeReference<Map<String, Object>>() {
                    });
//获取各项总配置pageSet父级
            Map<String, Object> pageSet = (Map<String, Object>) setMap.get("pageSet");
////获取时间戳，暂时屏蔽
//            if (setMap.get("alterTime") != null) {
//                dataTime = Utils.ObjectTOLong(setMap.get("alterTime"));
//                //Constant.dataTime= (long) pageSet.get("alterTime");
//                Log.e("TAG", "获取Constant.dataTime" + dataTime);
//            }
//获取条目总数
            totalNum = Integer.valueOf(String.valueOf(setMap.get("dataCount")));
            Log.e(TAG, "setStore: totalNum " + totalNum);

//获取搜索数据，如果有搜索数据但是仅仅是方括号没内容则隐藏搜索框
            if (pageSet.get("serachSet") != null) {
                List<Map<String, Object>> searchSetList = (List<Map<String, Object>>) pageSet.get("serachSet");
                String searchSet = JSONArray.toJSONString(searchSetList);
                //暂时设置搜索按钮为隐藏，以后做好了再展现
//                    if (searchSetList.size()==0) {
                searchButton.setVisibility(View.GONE);
//                    }
                Log.e("TAG", "获取serachSet" + searchSet);
            } else {//如果彻底无搜索字段则隐藏搜索框
                searchButton.setVisibility(View.GONE);
            }
//获取子项内部按钮
            if (pageSet.get("operaButtonSet") != null) {
                try {
                    operaButtonSetList = (List<Map<String, Object>>) pageSet.get("operaButtonSet");

                    if (operaButtonSetList.size() > 0) {
                        for (int i = 0; i < operaButtonSetList.size(); i++) {
                            operaButtonSetList.get(i).put("tableIdList", tableId);
                            operaButtonSetList.get(i).put("pageIdList", pageId);
                        }
                    }
                    operaButtonSet = JSONArray.toJSONString(operaButtonSetList);
                    Log.e("TAG", "获取operaButtonSet" + operaButtonSet);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//获得子表格：childTabs
            String childTabs;
            if (pageSet.get("childTabs") != null) {
                childTabs = String.valueOf(pageSet.get("childTabs"));
                childTab = new ArrayList<>();
                childTab = JSON.parseObject(childTabs,
                        new TypeReference<List<Map<String, Object>>>() {
                        });
            }

//数据左侧配置数据
            fieldSet = (List<Map<String, Object>>) pageSet.get("fieldSet");
            Log.e("TAG", "获取fieldSet" + fieldSet.toString());
//获取buttonSet
            String but = String.valueOf(pageSet.get("buttonSet"));
            Log.e(TAG, "setStore: but0 " + but);
            if (pageSet.get("buttonSet") != null) {
                Log.e(TAG, "setStore: but1 " + but);
                buttonSet = (List<Map<String, Object>>) pageSet.get("buttonSet");//初始化下拉按钮数据
                setButtonSet();
                Log.e("TAG", "获取buttonSet" + buttonSet);
                //判断右上角按钮是否可见

            }
//获取dataList
            Log.e(TAG, "setStore: setMap.get(\"dataList\") " + setMap.get("dataList").toString());
            dataList = (List<Map<String, Object>>) setMap.get("dataList");
            Log.e("TAG", "获取dataList" + dataList);

        } catch (Exception e) {
            e.printStackTrace();
            ((BaseActivity) getActivity()).dialog.dismiss();
        }
//将dataList与fieldSet合并准备适配数据
        datas = DataProcess.combineSetData(tableId, pageId, fieldSet, dataList);
        showData();


    }

    public void setButtonSet() {
        Constant.buttonSet = buttonSet;
        Log.e(TAG, "setButtonSet: Constant.buttonSet " + Constant.buttonSet.toString());
        if (Constant.buttonSet.size() > 0) {
            ((ListActivity4) getActivity()).mToolbar.showRightImageButton();
            for (int i = 0; i < buttonSet.size(); i++) {
                Constant.buttonSet.get(i).put("tableIdList", tableId);
                Constant.buttonSet.get(i).put("pageIdList", pageId);
            }
            ((ListActivity4) getActivity()).mToolbar.setRightButtonOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ListActivity4) getActivity()).buttonList();
                }
            });
        } else {
            ((ListActivity4) getActivity()).mToolbar.hideRightImageButton();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: setButtonSet()");
        setButtonSet();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        Constant.stu_index = "";
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    /**
     * 下拉刷新方法
     */
    private void refreshData() {
        start = 0;
        state = STATE_REFREH;

        getData();

    }

    /**
     * 上拉加载方法
     */
    private void loadMoreData() {

        start += limit;
        state = STATE_MORE;
        getData();

    }

    /**
     * 分动作展示数据
     */
    private void showData() {
        Log.e(TAG, "showData: " + state);
        switch (state) {
            case STATE_NORMAL:
                normalRequest();
                break;
            case STATE_REFREH:

                if (mAdapter != null) {

                    mAdapter.clearData();
                    mAdapter.addData(datas, childTab);
                    mRecyclerView.scrollToPosition(0);
                    mRefreshLayout.finishRefresh();
                    if (datas.size() != 0) {
                        Log.e(TAG, "showData: 执行了共x条");
                        Snackbar.make(mRecyclerView, totalNum + getResources().getString(R.string.datas), Snackbar.LENGTH_SHORT).show();
                    }

                }

                break;
            case STATE_MORE:
                if (mAdapter != null) {
                    mAdapter.addData(mAdapter.getDatas().size(), datas, childTab);
                    mRecyclerView.scrollToPosition(mAdapter.getDatas().size());
                    mRefreshLayout.finishRefreshLoadMore();
                    Snackbar.make(mRecyclerView, datas.size() + getResources().getString(R.string.count_datas_refreshed), Snackbar.LENGTH_SHORT).show();
                }

                break;
        }
    }

    public void normalRequest() {
        Log.e(TAG, "normalRequest: ");
        mAdapter = new ListAdapter2(datas, childTab, operaButtonSetList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayoutManager.VERTICAL));
//        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//                mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));
        mAdapter.setOnItemClickListener(new ListAdapter2.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, String data) {
                Log.e("TAG", "data " + data);
                toItem(data);
            }
        });
        ((BaseActivity) getActivity()).dialog.dismiss();
    }

    @OnClick(R.id.searchButton)
    public void onClick() {
    }

    /**
     * 跳转至子菜单列表
     */
    public void toItem(String itemData) {
        try {
            Intent intent = new Intent();
            intent.setClass(getActivity(), InfoTwoActivity.class);
            intent.putExtra("childData", itemData);
            intent.putExtra("tableId", tableId);
            intent.putExtra("operaButtonSet", operaButtonSet);
            Log.e(TAG, "toItem: 传递到info的operaButtonSet " + operaButtonSet);

            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
