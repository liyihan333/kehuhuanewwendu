package com.kwsoft.version;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.widget.CommonToolbar;
import com.kwsoft.version.adapter.TodayCourseTabAdapter;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/11/14 0014.
 */

public class TodayCourseTableActivity extends BaseActivity {
    public ListView lv_listview;
    public List<Map<String, String>> list = null;
    private CommonToolbar mToolbar;
    private String titleName;//顶栏名称
    private String todayPageId, tomorrowPageId, todayTableid, tomorrowTableId, isToday;//金明日课表page/table

    MaterialRefreshLayout mRefreshLayout;
    private String tableId, pageId;


    private int totalNum = 0;
    private int start = 0;
    private final int limit = 20;
    private static final int STATE_NORMAL = 0;
    private static final int STATE_REFREH = 1;
    private static final int STATE_MORE = 2;
    private int state = STATE_NORMAL;
    private TodayCourseTabAdapter adapter;
    private TextView empty_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_course);

        initView();
        initRefreshLayout();
        initData();

        dialog.show();
    }

    private void initData() {
        Intent intent = getIntent();
        titleName = intent.getStringExtra("titleName");
        isToday = intent.getStringExtra("isToday");
        if (isToday.equals("1")) {
            todayPageId = intent.getStringExtra("todayPageId");
            todayTableid = intent.getStringExtra("todayTableid");

            getTData(todayPageId, todayTableid);
        } else {
            tomorrowPageId = intent.getStringExtra("tomorrowPageId");
            tomorrowTableId = intent.getStringExtra("tomorrowTableId");
            getTData(tomorrowPageId, tomorrowTableId);
        }
        mToolbar.setTitle(titleName);

    }

    @Override
    public void initView() {
        lv_listview = (ListView) findViewById(R.id.lv_listview);

        mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);
        mToolbar.setBackgroundColor(getResources().getColor(Constant.topBarColor));
        mRefreshLayout = (MaterialRefreshLayout) findViewById(R.id.refresh_layout);
        empty_text = (TextView) findViewById(R.id.empty_text);

        mToolbar.setRightButtonIcon(getResources().getDrawable(R.mipmap.often_more)); //右侧pop
        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() { //左侧返回按钮
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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

                if (adapter != null && adapter.getCount() < totalNum) {

                    loadMoreData();
                } else {
//                    Snackbar.make(mListView, "没有更多了", Snackbar.LENGTH_SHORT).show();
                    mRefreshLayout.finishRefreshLoadMore();
                }
            }
        });
    }


    public void getTData(String pageid, String tableid) {
        if (hasInternetConnected()) {
            //地址
            String volleyUrl = Constant.sysUrl + Constant.requestListSet;
            Log.e("TAG", "列表请求地址：" + volleyUrl);
            Map<String, String> paramsMap = new HashMap<>();
            //参数
            paramsMap.put("tableId", tableid);
            paramsMap.put("pageId", pageid);
            paramsMap.put("start", start + "");
            paramsMap.put("limit", limit + "");
            paramsMap.put("sessionId", Constant.sessionId);

            Log.e(TAG, "getTData: " + paramsMap.toString());
//请求
            OkHttpUtils
                    .post()
                    .params(paramsMap)
                    .url(volleyUrl)
                    .build()
                    .execute(new EdusStringCallback(TodayCourseTableActivity.this) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            ErrorToast.errorToast(mContext, e);

                            dialog.dismiss();
                            backStart();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.e(TAG, "today:" + response);

                            parseData(response);
                        }
                    });
        } else {
            dialog.dismiss();
            mRefreshLayout.finishRefresh();
            Toast.makeText(TodayCourseTableActivity.this, R.string.no_network, Toast.LENGTH_SHORT).show();
            backStart();
        }
    }

    /**
     * 下拉刷新方法
     */
    private void refreshData() {
        start = 0;
        state = STATE_REFREH;
        if (isToday.equals("1")) {
            getTData(todayPageId, todayTableid);
        } else {
            getTData(tomorrowPageId, tomorrowTableId);
        }
//        getData();

    }

    /**
     * 上拉加载方法
     */
    private void loadMoreData() {

        start += limit;
        state = STATE_MORE;
        if (isToday.equals("1")) {
            getTData(todayPageId, todayTableid);
        } else {
            getTData(tomorrowPageId, tomorrowTableId);
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

    private static final String TAG = "TodayCourseTableActivit";

    private void parseData(String response) {
        Log.e(TAG, "parseData: response " + response);
        Map<String, Object> menuMap = JSON.parseObject(response,
                new TypeReference<Map<String, Object>>() {
                });
        if (!menuMap.containsKey("promptMessage")) {

            List<Map<String, Object>> dataList = (List<Map<String, Object>>) menuMap.get("dataList");
            Map<String, Object> pageSet = (Map<String, Object>) menuMap.get("pageSet");
            List<Map<String, Object>> fieldSet = (List<Map<String, Object>>) pageSet.get("fieldSet");
            totalNum = Integer.valueOf(String.valueOf(menuMap.get("dataCount")));

            String fieldCnName, teacherfieldAliasName = "", classfieldAliasName = "";
            list = new ArrayList<>();
            if (fieldSet != null && dataList != null && (fieldSet.size() > 0) && (dataList.size() > 0)) {
                for (int i = 0; i < fieldSet.size(); i++) {
                    Map<String, Object> map = fieldSet.get(i);
                    fieldCnName = map.get("fieldCnName") + "";

                if (fieldCnName.contains("内容展示")) {
                    teacherfieldAliasName = map.get("fieldAliasName") + "";
                    continue;
                } else if (fieldCnName.contains("时段")) {
                    classfieldAliasName = map.get("fieldAliasName") + "";
                    Log.e("fdsf", classfieldAliasName);
                    continue;
                }
            }
            Log.e("fdsf", classfieldAliasName);
            for (int j = 0; j < dataList.size(); j++) {
                Map<String, Object> dataListmap = dataList.get(j);
                Map<String, String> map = new HashMap<String, String>();
                if (dataListmap.containsKey(teacherfieldAliasName)) {
                    String courseNameStr = (dataListmap.get(teacherfieldAliasName)).toString();
                    if (courseNameStr != null && courseNameStr.length() > 0) {
                        String[] courseNameArr = courseNameStr.split(",");
                        if (courseNameArr.length > 2) {
                            map.put("courseName", courseNameArr[courseNameArr.length - 2].substring(3));
                        }
                    }
                }
                if (dataListmap.containsKey(classfieldAliasName)) {
                    map.put("courseTime", dataListmap.get(classfieldAliasName) + "");
                }
                list.add(map);
            }
        } else {
//            Toast.makeText(TodayCourseTableActivity.this, "暂时无课表数据", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
        showData();

        }else {
            Toast.makeText(TodayCourseTableActivity.this, menuMap.get("promptMessage").toString(), Toast.LENGTH_SHORT).show();
        }
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
//                if (mAdapter != null) {
//                    mAdapter.clear();
//                    mAdapter.addData(datas);
//                    mRefreshLayout.finishRefresh();
//                }
                normalRequest();
                mRefreshLayout.finishRefresh();
                break;
            case STATE_MORE:
                if (adapter != null) {
                    lv_listview.setSelection(adapter.getCount());
//                    adapter.addData(datas);
                    adapter.notifyDataSetChanged();
//                    mListView.scrollToPosition(mAdapter.getDatas().size());

                    mRefreshLayout.finishRefreshLoadMore();
                    Snackbar.make(lv_listview, list.size() + getResources().getString(R.string.count_datas_refreshed), Snackbar.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private void normalRequest() {
        adapter = new TodayCourseTabAdapter(list, this);
        lv_listview.setAdapter(adapter);
        dialog.dismiss();
        if (totalNum == 0) {
//            Snackbar.make(lv_listview, "本页无数据", Snackbar.LENGTH_SHORT).show();
            empty_text.setVisibility(View.VISIBLE);

        } else {
            empty_text.setVisibility(View.GONE);
//            Snackbar.make(lv_listview, "加载完成，共" + totalNum + "条", Snackbar.LENGTH_SHORT).show();
        }
    }


}
