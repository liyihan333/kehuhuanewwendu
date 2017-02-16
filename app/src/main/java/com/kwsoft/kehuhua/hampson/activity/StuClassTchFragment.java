package com.kwsoft.kehuhua.hampson.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.kwsoft.kehuhua.adcustom.InfoActivity;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.hampson.adapter.StuClassTchAdapter;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.utils.DataProcess;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;


public class StuClassTchFragment extends Fragment {
    @Bind(R.id.lv)
    ListView mListView;

    //    private List<Map<String, String>> list = new ArrayList<>();
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

    private TextView empty_text;
    private String operaButtonSet;
    private List<List<Map<String, String>>> datas;
    private StuClassTchAdapter mAdapter;
    private static final String TAG = "CourseRatingBarFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_course_ratingbar_star_list, container, false);
        ButterKnife.bind(this, view);
        ((BaseActivity) getActivity()).dialog.show();
        empty_text= (TextView) view.findViewById(R.id.empty_text);
        getDataIntent();//获取初始化数据
        initRefreshLayout();
        getData();
        return view;
    }

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

                if (mAdapter != null && mAdapter.getCount() < totalNum) {

                    loadMoreData();
                } else {
//                    Snackbar.make(mListView, "没有更多了", Snackbar.LENGTH_SHORT).show();
                    mRefreshLayout.finishRefreshLoadMore();
                }
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String itemData = JSON.toJSONString(mAdapter.getItem(i));
                toItem(itemData);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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
            paramsMap.put("sessionId", Constant.sessionId);
            paramsMap.put("start", start + "");
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
//                            mRefreshLayout.finishRefresh();
                            ((BaseActivity) getActivity()).dialog.dismiss();
                            backStart();
                            Log.e(TAG, "onError: Call  " + call + "  id  " + id);
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.e(TAG, "onResponse-wyl: " + "  id  " + response);

                            setStore(response);
                        }
                    });
        } else {

            ((BaseActivity) getActivity()).dialog.dismiss();
            mRefreshLayout.finishRefresh();
            Toast.makeText(getActivity(), R.string.please_connect_net, Toast.LENGTH_SHORT).show();
            backStart();
        }
    }

    public void backStart() {

        //下拉失败后需要将加上limit的strat返还给原来的start，否则会获取不到数据
        if (state == STATE_MORE) {
            //start只能是limit的整数倍
            if (start > limit) {
                start -= limit;
            }
//            mRefreshLayout.finishRefreshLoadMore();
        }
    }

    List<Map<String, Object>> operaButtonSetList;

    @SuppressWarnings("unchecked")
    public void setStore(String jsonData) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<Map<String, Object>> fieldSet = new ArrayList<>();
        Log.e("TAG", "解析set" + jsonData);
        try {
            Map<String, Object> setMap = JSON.parseObject(jsonData,
                    new TypeReference<Map<String, Object>>() {
                    });
//获取各项总配置pageSet父级
            Map<String, Object> pageSet = (Map<String, Object>) setMap.get("pageSet");
//获取条目总数
            totalNum = Integer.valueOf(String.valueOf(setMap.get("dataCount")));

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

//数据左侧配置数据
            fieldSet = (List<Map<String, Object>>) pageSet.get("fieldSet");
            Log.e("TAG", "获取fieldSet" + fieldSet.toString());
//获取dataList
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

//    public int isResume = 0;

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
//                if (mAdapter != null) {
//                    mAdapter.clear();
//                    mAdapter.addData(datas);
//                    mRefreshLayout.finishRefresh();
//                }
                normalRequest();
                mRefreshLayout.finishRefresh();
                break;
            case STATE_MORE:
                if (mAdapter != null) {
                    mListView.setSelection(mAdapter.getCount());
                    mAdapter.addData(datas);
//                    mListView.scrollToPosition(mAdapter.getDatas().size());

                    mRefreshLayout.finishRefreshLoadMore();
                    Snackbar.make(mListView, R.string.update + datas.size() + R.string.item, Snackbar.LENGTH_SHORT).show();
                }

                break;
        }
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

    public void normalRequest() {
        Log.e(TAG, "normalRequest: ");
        Log.e(TAG, "normalRequest: datas " + datas.toString());
        mAdapter = new StuClassTchAdapter(getActivity(), datas);
        mListView.setAdapter(mAdapter);
        ((BaseActivity) getActivity()).dialog.dismiss();
        if (totalNum == 0) {
            Snackbar.make(mListView, R.string.this_page_no_data, Snackbar.LENGTH_SHORT).show();
            empty_text.setVisibility(View.VISIBLE);

        }else{
            empty_text.setVisibility(View.GONE);
            Snackbar.make(mListView, R.string.loading_completed_total + totalNum + R.string.item, Snackbar.LENGTH_SHORT).show();
        }

    }

    /**
     * 跳转至子菜单列表
     */
    public void toItem(String itemData) {
        try {
            Intent intent = new Intent();
            intent.setClass(getActivity(), InfoActivity.class);
            intent.putExtra("childData", itemData);
            intent.putExtra("tableId", tableId);
            intent.putExtra("operaButtonSet", operaButtonSet);

            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
