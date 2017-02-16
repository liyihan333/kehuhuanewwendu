package com.kwsoft.kehuhua.hampson.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.hampson.adapter.KanBLRBaseAdapter;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.view.RecycleViewDivider;
import com.kwsoft.kehuhua.view.WrapContentLinearLayoutManager;
import com.kwsoft.kehuhua.widget.CommonToolbar;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/15 0015.
 *
 */

public class KanBanLRActivity extends BaseActivity {

    @Bind(R.id.common_toolbar)
    CommonToolbar mToolbar;
    @Bind(R.id.lv)
    RecyclerView mRecyclerView;
    @Bind(R.id.refresh_layout)
    MaterialRefreshLayout mRefreshLayout;


    private int totalNum = 0;
    private int start = 0;
    private final int limit = 20;
    private static final int STATE_NORMAL = 0;
    private static final int STATE_REFREH = 1;
    private static final int STATE_MORE = 2;
    private int state = STATE_NORMAL;


    private List<Map<String, Object>> datas = new ArrayList<>();
    private KanBLRBaseAdapter adapter;
    private List<Map<String, Object>> childDatas = new ArrayList<>();
    private final static String TAG = "KanBanLRActivity";
    private String name, tableId, pageId, itemData;
    private Map<String, Object> itemMap = new HashMap<>();
    List<Map<String, Object>> menuListMap2Key = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kanbanlr);

        ButterKnife.bind(this);
        dialog.show();
        itemData = getIntent().getStringExtra("itemData");
        itemMap = JSON.parseObject(itemData,
                new TypeReference<Map<String, Object>>() {
                });
        name = String.valueOf(itemMap.get("menuName"));//获取名称
        tableId = String.valueOf(itemMap.get("tableId"));
        pageId = String.valueOf(itemMap.get("pageId"));
        //tableId = "17796";
        initView();
        initRefreshLayout();
        requestData();
    }

    @Override
    public void initView() {
        mToolbar.setTitle(name);
        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mToolbar.setRightButtonIcon(getResources().getDrawable(R.mipmap.ic_add_pic));
        mToolbar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(KanBanLRActivity.this, AddConvActivity.class);
//                startActivity(intent);
            }
        });

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        datas.clear();
//        childDatas.clear();
//        requestData();
//    }

    private void initRefreshLayout() {
        mRefreshLayout.setLoadMore(true);
        mRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                datas.clear();
                childDatas.clear();
                refreshData();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                if (adapter != null && adapter.getItemCount() < totalNum) {
                    Log.e(TAG, "onRefreshLoadMore: " + adapter.getItemCount());
                    loadMoreData();
                } else {
                    Snackbar.make(mRecyclerView, R.string.no_more, Snackbar.LENGTH_SHORT).show();
                    mRefreshLayout.finishRefreshLoadMore();
                }
            }
        });
    }

    /**
     * 下拉刷新方法
     */
    private void refreshData() {
        start = 0;
        state = STATE_REFREH;
        requestData();
    }

    /**
     * 上拉加载方法
     */
    private void loadMoreData() {
        start += limit;
        state = STATE_MORE;
        requestData();
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

                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                    mRecyclerView.scrollToPosition(0);
                    mRefreshLayout.finishRefresh();
                    dialog.dismiss();
                    if (datas.size() == 0) {
                        Snackbar.make(mRecyclerView, R.string.no_data, Snackbar.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "showData: 执行了共x条");
                        Snackbar.make(mRecyclerView, totalNum + R.string.count_datas_refreshed, Snackbar.LENGTH_SHORT).show();
                    }

                }

                break;
            case STATE_MORE:
                if (adapter != null) {
                    //  adapter.addData(adapter.getDatas().size(), datas, childTab);
                    datas.addAll(childDatas);
                    adapter.notifyDataSetChanged();
                    mRecyclerView.scrollToPosition(adapter.getDatas().size());
                    mRefreshLayout.finishRefreshLoadMore();
                    Snackbar.make(mRecyclerView, childDatas.size() + R.string.count_datas_refreshed, Snackbar.LENGTH_SHORT).show();
                }
                break;
        }
    }


    public void requestData() {
        if (hasInternetConnected()) {
            //地址
            String volleyUrl = Constant.sysUrl + Constant.requestListSet;
            Log.e("TAG", "学员端登陆地址 " + Constant.sysUrl + Constant.requestListSet);
            //参数
            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put("tableId", tableId);
            paramsMap.put("pageId", pageId);
            paramsMap.put(Constant.timeName, "-1");
            Log.e("TAG", "学员端登陆map " + paramsMap.toString());

            paramsMap.put("start", start + "");
            if (!Constant.stu_index.equals("")) {
                paramsMap.put("ctType", Constant.stu_index);
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
                    .execute(new EdusStringCallback(KanBanLRActivity.this) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            ErrorToast.errorToast(mContext, e);
                            mRefreshLayout.finishRefresh();
                            dialog.dismiss();
                            backStart();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.e("", "onResponse: " + "  id  " + response);
                            check(response);
                        }
                    });
        } else {
//            ((BaseActivity) getActivity()).dialog.dismiss();
            mRefreshLayout.finishRefresh();
            Toast.makeText(KanBanLRActivity.this, R.string.no_network, Toast.LENGTH_SHORT).show();
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
            mRefreshLayout.finishRefreshLoadMore();
        }
    }

    public void check(String menuData) {
        Log.e(TAG, "check: 0");
        Map<String, Object> menuMap = null;
        try {
            menuMap = JSON.parseObject(menuData,
                    new TypeReference<Map<String, Object>>() {
                    });
        } catch (Exception e) {
            e.printStackTrace();
            dialog.dismiss();
        }
        List<Map<String, Object>> menuListMap2 = new ArrayList<>();
        Log.e(TAG, "check: 1");
        if (menuMap.containsKey("pageSet")) {
            Log.e(TAG, "check: 2");
            Map<String, Object> pageSet = (Map<String, Object>) menuMap.get("pageSet");
            if (pageSet.containsKey("fieldSet")) {
                Log.e(TAG, "check: 3");
                menuListMap2Key = (List<Map<String, Object>>) pageSet.get("fieldSet");
                Log.e(TAG,"menuListMap2Key "+JSON.toJSONString(menuListMap2Key));

                if (menuMap.containsKey("dataList")) {
                    menuListMap2 = (List<Map<String, Object>>) menuMap.get("dataList");
                    Log.e(TAG,"menuListMap2 "+JSON.toJSONString(menuListMap2));
                }
                childDatas.clear();
                totalNum = Integer.parseInt(menuMap.get("dataCount") + "");
                Log.e(TAG,"menuListMap2num "+menuMap.get("dataCount") + "");
                if (menuListMap2 != null && menuListMap2.size() > 0) {
                    for (int i = 0; i < menuListMap2.size(); i++) {
                        Map<String, Object> map = menuListMap2.get(i);
                        if (start > 0) {
                            childDatas.add(map);
                        } else {
                            datas.add(map);
                            Log.e(TAG, "check: datas "+datas.size());
                        }
                    }
                }
                showData();
            }
        }
        dialog.dismiss();
    }

    private void normalRequest() {
        Log.e(TAG, "normalRequest: datas "+datas.size());
        adapter = new KanBLRBaseAdapter(menuListMap2Key,datas, this);
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this));
         mRecyclerView.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL));
//        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//                mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));
        adapter.setOnItemClickListener(new KanBLRBaseAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, String data) {
                Log.e("TAG", "data " + data);
                // Toast.makeText(ConvListActivity.this, data, Toast.LENGTH_SHORT).show();
                toItem(data);
            }
        });
        dialog.dismiss();
        //((BaseActivity) getActivity()).dialog.dismiss();
    }

    private void toItem(String data) {
        Map<String, Object> menuMap = JSON.parseObject(data,
                new TypeReference<Map<String, Object>>() {
                });
//        Log.e(TAG, "onItemClick: " + menuMap.get("T_17796_0").toString());
//        Intent intent = new Intent(KanBanLRActivity.this, ChatActivity.class);
//        intent.putExtra("tableId", menuMap.get("T_17796_0").toString());
//        startActivity(intent);
    }
}
