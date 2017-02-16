package com.kwsoft.kehuhua.adcustom;

import com.kwsoft.kehuhua.adcustom.base.BaseActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.kwsoft.kehuhua.adapter.OperateDataAdapter;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.utils.DataProcess;
import com.kwsoft.kehuhua.widget.CommonToolbar;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

import static com.kwsoft.kehuhua.config.Constant.mainId;
import static com.kwsoft.kehuhua.config.Constant.topBarColor;

/**
 * Created by Administrator on 2016/12/23 0023.
 */

public class OperateDataTwoActivity extends BaseActivity {
    @Bind(R.id.lv)
    RecyclerView mRecyclerView;
    @Bind(R.id.refresh_layout)
    MaterialRefreshLayout mRefreshLayout;

    private CommonToolbar mToolbar;
    private String buttonName0, buttonName1;
    private String mainTableId0, mainPageId0, tableId0, pageId0, dataId0;
    private String mainTableId1, mainPageId1, tableId1, pageId1, dataId1;
    private Map<String, String> paramsMap0, paramsMap1;
    private int buttonType0, buttonType1;
    private String keyRelation0 = "", keyRelation1 = "";
    private String hideFieldParagram0 = "&", hideFieldParagram1 = "&";


    private static final String TAG = "OperateDataActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operate);
        ButterKnife.bind(this);
        dialog.show();
        getIntentData();
        initRefreshLayout();
        initView();
        getData(buttonType0, 0);
    }

    private void getData(final int buttonType, int num) {
        //不同页面类型请求Url不一样
        String volleyUrl = "";
//        switch (buttonType) {
//            case 0://列表添加
//                volleyUrl = Constant.sysUrl + Constant.requestAdd;
//                break;
//            case 12://关联修改
//                volleyUrl = Constant.sysUrl + Constant.requestEdit;
//                paramsMap0.put("tNumber", "0");
//                break;
//            case 18://关联添加
        volleyUrl = Constant.sysUrl + Constant.requestRowsAdd;
//                break;
//        }

        // paramsMap0.put("sessionId", Constant.sessionId);
        if (num == 0) {
            paramsMap0.put("sessionId", Constant.sessionId);
            //请求
            OkHttpUtils
                    .post()
                    .params(paramsMap0)
                    .url(volleyUrl)
                    .build()
                    .execute(new EdusStringCallback(OperateDataTwoActivity.this) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            ErrorToast.errorToast(mContext, e);
                            dialog.dismiss();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.e(TAG, "onResponse: " + response + "  id  " + id);
                            setStore(response, buttonType0, tableId0, pageId0, dataId0, 0);
                        }
                    });
        } else {
            paramsMap1.put("sessionId", Constant.sessionId);
            //请求
            OkHttpUtils
                    .post()
                    .params(paramsMap1)
                    .url(volleyUrl)
                    .build()
                    .execute(new EdusStringCallback(OperateDataTwoActivity.this) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            ErrorToast.errorToast(mContext, e);
                            dialog.dismiss();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.e(TAG, "onResponse: " + response + "  id  " + id);
                            setStore(response, buttonType1, tableId1, pageId1, dataId1, 1);

                        }
                    });
        }
    }

    private List<Map<String, Object>> fieldSet = new ArrayList<>();
    private List<Map<String, Object>> fieldSet0 = new ArrayList<>();
    private List<Map<String, Object>> fieldSet1 = new ArrayList<>();

    @SuppressWarnings("unchecked")
    private void setStore(String response, int buttonType, String tableId, String pageId, String dataId, int num) {

        Log.e("TAG", "解析操作数据");
        try {
            Map<String, Object> buttonSet = JSON.parseObject(response);
//获取fieldSet
            Map<String, Object> pageSet = (Map<String, Object>) buttonSet.get("pageSet");


//判断添加还是修改，keyRelation赋值不一样
            if (num == 0) {
                fieldSet0 = (List<Map<String, Object>>) pageSet.get("fieldSet");
                keyRelation0 = reqKeyRelation(buttonType, pageSet, tableId, pageId, dataId);
                Log.e("TAG", "keyRelation " + keyRelation0);
                //hideFieldSet,隐藏字段
                if (pageSet.get("hideFieldSet") != null) {
                    List<Map<String, Object>> hideFieldSet = (List<Map<String, Object>>) pageSet.get("hideFieldSet");
                    hideFieldParagram0 += DataProcess.toHidePageSet(hideFieldSet);
                    Log.e(TAG, "setStore: hideFieldParagram0" + hideFieldParagram0);
                }
                getData(buttonType1, 1);
            } else {
                fieldSet1 = (List<Map<String, Object>>) pageSet.get("fieldSet");
                fieldSet.addAll(fieldSet0);
                fieldSet.addAll(fieldSet1);
                keyRelation1 = reqKeyRelation(buttonType, pageSet, tableId, pageId, dataId);
                Log.e("TAG", "keyRelation " + keyRelation1);
                //hideFieldSet,隐藏字段
                if (pageSet.get("hideFieldSet") != null) {
                    List<Map<String, Object>> hideFieldSet = (List<Map<String, Object>>) pageSet.get("hideFieldSet");
                    hideFieldParagram1 += DataProcess.toHidePageSet(hideFieldSet);
                    Log.e(TAG, "setStore: hideFieldParagram1" + hideFieldParagram1);
                }
                showData();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String reqKeyRelation(int buttonType, Map<String, Object> pageSet, String tableId, String pageId, String dataId) {
        String keyRelation0 = "";
        switch (buttonType) {
            case 0://添加无此参数
                break;
            case 18:
                if (pageSet.get("relationFieldId") != null) {
                    Constant.relationFieldId = String.valueOf(pageSet.get("relationFieldId"));

                    keyRelation0 = "t0_au_" + tableId + "_" + pageId + "_" + Constant.relationFieldId + "=" + dataId;
                }

                Log.e(TAG, "setStore: keyRelation " + keyRelation0);
                break;
            case 12:
                keyRelation0 = "&t0_au_" + tableId + "_" + pageId + "=" + dataId;
                break;
        }
        return keyRelation0;
    }

    private static final int STATE_NORMAL = 0;
    private static final int STATE_REFREH = 1;
    private int state = STATE_NORMAL;
    private OperateDataAdapter mAdapter;


    /**
     * 下拉刷新方法
     */
    //暂时禁止刷新，刷新会导致显示view错乱
    private void refreshData() {
        state = STATE_REFREH;
        getData(buttonType0, 0);
        mRefreshLayout.finishRefresh();
    }

    public void normalRequest() {
        Log.e(TAG, "normalRequest: fieldSet" + fieldSet.toString());
        Log.e(TAG, "normalRequest: fieldSet0" + fieldSet0.toString());
        Log.e(TAG, "normalRequest: fieldSet1" + fieldSet1.toString());
        Log.e(TAG, "normalRequest: paramsMap0" + paramsMap0.toString());
        mAdapter = new OperateDataAdapter(fieldSet, paramsMap0);
        mRecyclerView.setAdapter(mAdapter);
        dialog.dismiss();
    }

    private void showData() {

        Log.e(TAG, "showData: " + state);
        switch (state) {
            case STATE_NORMAL:
                normalRequest();
                break;
            case STATE_REFREH:
//                if (mAdapter != null) {
//                    mAdapter.clearData();
//                    mAdapter.addData(fieldSet);
//                    mRecyclerView.scrollToPosition(0);
//                    mRefreshLayout.finishRefresh();
//                    if (fieldSet.size() == 0) {
//                        Snackbar.make(mRecyclerView, "本页无数据", Snackbar.LENGTH_SHORT).show();
//                    } else {
//                        Snackbar.make(mRecyclerView, "更新完成", Snackbar.LENGTH_SHORT).show();
//                    }
//                }
                normalRequest();
                if (fieldSet.size() == 0) {
                    Snackbar.make(mRecyclerView, R.string.no_data_on_this_page, Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(mRecyclerView, R.string.update_completed, Snackbar.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }

    }

    private void getCommit(int buttonType, String tableId, String pageId, String keyRelation, final int num, List<Map<String, Object>> fieldSets) {
        String value = DataProcess.commit(OperateDataTwoActivity.this, fieldSets);
        if (!value.equals("no")) {
            if (hasInternetConnected()) {
                dialog.show();
                String volleyUrl1 = "";
                Log.e(TAG, "getCommit:buttonType "+buttonType);
                if (num == 0) {
                    //  String value = DataProcess.commit(OperateDataTwoActivity.this, fieldSet0);
                    switch (buttonType) {
                        case 0://添加提交地址
                            volleyUrl1 = Constant.sysUrl + Constant.commitAdd + "?" +
                                    Constant.tableId + "=" + tableId + "&" + Constant.pageId + "=" + pageId + "&" +
                                    value + hideFieldParagram0;
                            break;
                        case 18:
                            volleyUrl1 = Constant.sysUrl + Constant.commitAdd + "?" +
                                    Constant.tableId + "=" + tableId + "&" + Constant.pageId + "=" + pageId + "&" +
                                    value + hideFieldParagram0 + "&" + keyRelation;
                            break;
                        case 12:
                            volleyUrl1 = Constant.sysUrl + Constant.commitEdit + "?" +
                                    Constant.tableId + "=" + tableId + "&" + Constant.pageId + "=" + pageId + "&" +
                                    value + hideFieldParagram0 + "&" + keyRelation;
                            break;
                    }
                } else {
                    switch (buttonType) {
                        case 0://添加提交地址
                            volleyUrl1 = Constant.sysUrl + Constant.commitAdd + "?" +
                                    Constant.tableId + "=" + tableId + "&" + Constant.pageId + "=" + pageId + "&" +
                                    value + hideFieldParagram1;
                            break;
                        case 18:
                            volleyUrl1 = Constant.sysUrl + Constant.commitAdd + "?" +
                                    Constant.tableId + "=" + tableId + "&" + Constant.pageId + "=" + pageId + "&" +
                                    value + hideFieldParagram1 + "&" + keyRelation;
                            break;
                        case 12:
                            volleyUrl1 = Constant.sysUrl + Constant.commitEdit + "?" +
                                    Constant.tableId + "=" + tableId + "&" + Constant.pageId + "=" + pageId + "&" +
                                    value + hideFieldParagram1 + "&" + keyRelation;
                            break;
                    }
                }
                //请求地址（关联添加和修改）
                String volleyUrl = volleyUrl1.replaceAll(" ", "%20").replaceAll("&&", "&");
                volleyUrl = volleyUrl + "&sessionId=" + Constant.sessionId;
                Log.e(TAG, "getCommit: volleyUrl" + volleyUrl);
                //get请求
                OkHttpUtils
                        .get()
                        .url(volleyUrl)
                        .build()
                        .execute(new EdusStringCallback(OperateDataTwoActivity.this) {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                ErrorToast.errorToast(mContext, e);
                                dialog.dismiss();
                                Toast.makeText(OperateDataTwoActivity.this, R.string.operation_failed, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.e(TAG, "onResponse: " + response);
                                if (response != null && !response.equals("0")) {
                                   // Toast.makeText(OperateDataTwoActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                                    if (num == 0) {
                                        getCommit(buttonType1, tableId1, pageId1, keyRelation1, 1, fieldSet1);
                                    } else {
                                        Toast.makeText(OperateDataTwoActivity.this, R.string.operation_success, Toast.LENGTH_SHORT).show();
                                        backToInfo();
                                    }
                                } else {
                                    Toast.makeText(OperateDataTwoActivity.this, R.string.operation_failed, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Toast.makeText(this,R.string.no_network, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * {
     * "butWhereJs":"rowData.AFM_3 == 9",
     * "tableIdList":"19",
     * "buttonName":"修改",
     * "buttonType":12,
     * "buttonId":191,
     * "butJyWhereJs":"",
     * "tableId":19,
     * "dataId":"90",
     * "startTurnPage":1256
     * }
     */
    //获取参数
    private void getIntentData() {
        //初始化参数Map
        paramsMap0 = new HashMap<>();
        paramsMap1 = new HashMap<>();
        //获取数据并解析
        Intent intent = getIntent();
        String buttonSetItemStr0 = intent.getStringExtra("itemSet");
        Map<String, Object> buttonSetItem0 = JSON.parseObject(buttonSetItemStr0);
        // String buttonName =buttonSetItem.get("buttonName").toString();
        Log.e(TAG, "getIntentData: buttonSetItem " + buttonSetItem0.toString());
        //赋值页面标题
        buttonName0 = String.valueOf(buttonSetItem0.get("buttonName"));
//        Log.e("buttonName=", buttonName);
        String buttonTypeStr0 = String.valueOf(buttonSetItem0.get("buttonType"));
        buttonType0 = Integer.valueOf(buttonTypeStr0);
        //获取参数并添加
        //mainTableId
        mainTableId0 = String.valueOf(buttonSetItem0.get("tableIdList"));
        paramsMap0.put(Constant.mainTableId, mainTableId0);
        //mainPageId
        mainPageId0 = String.valueOf(buttonSetItem0.get("pageIdList"));
        paramsMap0.put(Constant.mainPageId, mainPageId0);
        //tableId
        tableId0 = String.valueOf(buttonSetItem0.get("tableId"));
        paramsMap0.put(Constant.tableId, tableId0);
        //pageId
        pageId0 = String.valueOf(buttonSetItem0.get("startTurnPage"));
        paramsMap0.put(Constant.pageId, pageId0);
        //dataId：在对列表操作的时候是没有的，只有行级操作的时候才有
        dataId0 = String.valueOf(buttonSetItem0.get("dataId"));
        if (dataId0 != null && !dataId0.equals("null")) {

            paramsMap0.put(mainId, dataId0);
        }
        Log.e(TAG, "getIntentData: paramsMap0 " + paramsMap0.toString());


        String buttonSetItemStr1 = intent.getStringExtra("itemSet1");
        Map<String, Object> buttonSetItem1 = JSON.parseObject(buttonSetItemStr1);
        Log.e(TAG, "getIntentData:buttonSetItem1 " + buttonSetItem1.toString());
        //赋值页面标题
        buttonName1 = String.valueOf(buttonSetItem1.get("buttonName"));
//        Log.e("buttonName=", buttonName);
        String buttonTypeStr1 = String.valueOf(buttonSetItem1.get("buttonType"));
        buttonType1 = Integer.valueOf(buttonTypeStr1);
        //获取参数并添加
        //mainTableId
        mainTableId1 = String.valueOf(buttonSetItem1.get("tableIdList"));
        paramsMap1.put(Constant.mainTableId, mainTableId1);
        //mainPageId
        mainPageId1 = String.valueOf(buttonSetItem1.get("pageIdList"));
        paramsMap1.put(Constant.mainPageId, mainPageId1);
        //tableId
        tableId1 = String.valueOf(buttonSetItem1.get("tableId"));
        paramsMap1.put(Constant.tableId, tableId1);
        //pageId
        pageId1 = String.valueOf(buttonSetItem1.get("startTurnPage"));
        paramsMap1.put(Constant.pageId, pageId1);
        //dataId：在对列表操作的时候是没有的，只有行级操作的时候才有
        dataId1 = String.valueOf(buttonSetItem1.get("dataId"));
        if (dataId1 != null && !dataId1.equals("null")) {
            paramsMap1.put(mainId, dataId1);
        }
        Log.e(TAG, "getIntentData: paramsMap1 " + paramsMap1.toString());
    }

    @Override
    public void initView() {
        mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);
        mToolbar.setTitle(buttonName0);
        mToolbar.setBackgroundColor(getResources().getColor(topBarColor));
//        if (buttonName.contains("预约")) {
//            mToolbar.showRightTextView();
//            mToolbar.hideRightImageButton();
//            mToolbar.setRightTextView("预约");
//            mToolbar.setRightTextViewOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    toCommit();
//                }
//            });
//        } else if (buttonName.contains("请假")) {
//            mToolbar.showRightTextView();
//            mToolbar.hideRightImageButton();
//            mToolbar.setRightTextView("请假");
//            mToolbar.setRightTextViewOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    toCommit();
//                }
//            });
//        } else {
        mToolbar.hidetvRightTextView();
        mToolbar.showRightImageButton();
        //左侧返回按钮
        mToolbar.setRightButtonIcon(getResources().getDrawable(R.mipmap.edit_commit1));
        mToolbar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toCommit();
            }
        });
//        }

        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    private void initRefreshLayout() {
        mRefreshLayout.setLoadMore(false);
        mRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {

                refreshData();
            }
        });
    }


    private void toCommit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(buttonName0 + "？");
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getCommit(buttonType0, tableId0, pageId0, keyRelation0, 0, fieldSet0);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    public static final String action = "com.kwsoft.kehuhua.fragments.ListFragment.REFRESH_LIST";

    public void backToInfo() {
        Toast.makeText(OperateDataTwoActivity.this, R.string.operation_success, Toast.LENGTH_SHORT).show();
        //发送广播给listFragment
        Intent intent = new Intent(action);
        sendBroadcast(intent);

        //关闭dialog动画
        dialog.dismiss();
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (2 == requestCode) {
            if (2 == resultCode) {
                //返回添加页面后复位jump值
                Constant.jumpNum = 0;
                //传递数据
                Bundle bundle = data.getBundleExtra("bundle");
                String strFromAct2 = bundle.getString("myValue");
                //解析选择结果值
                Map<String, Object> dataMap = JSON.parseObject(strFromAct2,
                        new TypeReference<Map<String, Object>>() {
                        });
                assert dataMap != null;
                //num代表数目，ids代表所选的id们，names所选id对应的名称，isMulti为true多选，都则单选
                //position为记录的值应该插入的位置
                String ids = String.valueOf(dataMap.get("ids"));
                String names = String.valueOf(dataMap.get("names"));
                String isMulti = String.valueOf(dataMap.get("isMulti"));
                int position = Integer.valueOf(String.valueOf(dataMap.get("position")));
                //多选情况
                if (isMulti.equals("true")) {

//                    fieldSet.get(position).put(Constant.primValue, num);//选择总数
                    //将id列表记录到单元中，留待提交或者回显使用
                    fieldSet.get(position).put(Constant.itemValue, ids);//id列表
                    fieldSet.get(position).put(Constant.itemName, names);//名称列表
                    //找到下一层的key值
//                    fieldSet.get(position).put(Constant.secondKey, "t1_au_" + fieldSet.get(position).get("relationTableId") + "_" +
//                            fieldSet.get(position).get("showFieldArr") +
//                            "_" + fieldSet.get(position).get("dialogField"));
                    mAdapter.notifyItemChanged(position);
                    //单选情况
                } else {
                    fieldSet.get(position).put(Constant.itemValue, ids);
                    Log.e("TAG", "单选回填的值 " + ids);
                    fieldSet.get(position).put(Constant.itemName, names);
                    mAdapter.notifyItemChanged(position);
                    //订单编号生成,只有
                    if (!Constant.tmpFieldId.equals("") && Constant.tmpFieldId.equals(String.valueOf(fieldSet.get(position).get("fieldId")))) {
                        try {
                            Map<String, String> parMap = paramsMap0;
                            String key = String.valueOf(fieldSet.get(position).get(Constant.primKey));
                            String value = String.valueOf(fieldSet.get(position).get(Constant.itemValue));
                            parMap.put(key, value);
                            requestRule(parMap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            } else if (5 == resultCode) {
                //无限添加返回的listListMap字符串
                Constant.jumpNum = 0;
                Bundle bundle = data.getBundleExtra("bundle");
                String myValue = bundle.getString("myValue");
                int positionLast = Integer.valueOf(bundle.getString("position"));
                fieldSet.get(positionLast).put("tempListValue", myValue);
                List<List<Map<String, Object>>> myValueList = new ArrayList<>();
                String secondValue = "";
                if (myValue != null && !myValue.equals("")) {
                    myValueList = JSON.parseObject(myValue,
                            new TypeReference<List<List<Map<String, Object>>>>() {
                            });
                    if (myValueList.size() > 0) {
                        //将选择结果赋值给父类dz值

                        for (int i = 0; i < myValueList.size(); i++) {
                            for (int j = 0; j < myValueList.get(i).size(); j++) {
                                String tempKeyIdArr = String.valueOf(myValueList.get(i).get(j).get("tempKeyIdArr"));
                                if (tempKeyIdArr != null && !tempKeyIdArr.equals("")) {
                                    myValueList.get(i).get(j).put(Constant.itemValue, tempKeyIdArr.replace("t1", "t2"));
                                }
                            }
                            secondValue += "&" + DataProcess.toCommitStr(
                                    (Activity) mContext,
                                    myValueList.get(i));
                        }
                    }
                }
                Log.e("TAG", "secondValue " + secondValue);
                fieldSet.get(positionLast).put(Constant.itemValue, myValueList.size() + "&" + secondValue);
                mAdapter.notifyItemChanged(positionLast);
            } else if (resultCode == 101) {
                //返回添加页面后复位jump值
                Constant.jumpNum = 0;
                Log.e("TAG", "RESULT_OK " + 101);
                Bundle bundle = data.getBundleExtra("bundle");
                String positionStr = bundle.getString("position");
                String codeListStr = bundle.getString("codeListStr");
                int position = Integer.valueOf(positionStr);
                Log.e(TAG, "onActivityResult: 收获的positionStr " + position);
                fieldSet.get(position).put(Constant.itemValue, codeListStr);
                fieldSet.get(position).put(Constant.itemName, codeListStr);
                Log.e("TAG", "fieldSet.get(picturePosition) " + fieldSet.get(position).toString());
                mAdapter.notifyItemChanged(position);
                mAdapter.notifyDataSetChanged();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void requestRule(final Map<String, String> parMap) {
        String volleyUrl = Constant.sysUrl + Constant.requestMaxRule;
        parMap.put("sessionId", Constant.sessionId);
        Log.e("TAG", "网络获取规则dataUrl " + volleyUrl);
        Log.e("TAG", "网络获取规则table " + parMap.toString());

        //请求
        OkHttpUtils
                .post()
                .params(parMap)
                .url(volleyUrl)
                .build()
                .execute(new EdusStringCallback(mContext) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ErrorToast.errorToast(mContext, e);
                        dialog.dismiss();
                        Log.e(TAG, "onError: Call  " + call + "  id  " + id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e(TAG, "onResponse: " + "  id  " + id);
                        putValue(response);
                    }
                });
    }

    private void putValue(String jsonData) {
        Log.e("TAG", "规则生成结果：" + jsonData);

        for (int i = 0; i < fieldSet.size(); i++) {
            int fieldRole = Integer.valueOf(String.valueOf(fieldSet.get(i).get("fieldRole")));
            if (fieldRole == 8) {
                fieldSet.get(i).put(Constant.itemValue, jsonData);
                fieldSet.get(i).put(Constant.itemName, jsonData);
                mAdapter.notifyItemChanged(i);

            }
        }
    }

}
