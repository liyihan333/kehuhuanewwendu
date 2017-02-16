package com.kwsoft.kehuhua.hampson.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.kwsoft.kehuhua.adcustom.R;
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

import okhttp3.Call;

import static com.kwsoft.kehuhua.config.Constant.mainId;
import static com.kwsoft.kehuhua.config.Constant.topBarColor;

/**
 * Created by Administrator on 2016/11/8 0008.
 */

public class StarRatingBarActivity extends BaseActivity implements View.OnClickListener {
    public CheckBox cb_first_rb1, cb_first_rb2, cb_first_rb3, cb_first_rb4, cb_first_rb5;//一颗星
    public CheckBox cb_sec_rb1, cb_sec_rb2, cb_sec_rb3;//两颗星
    public CheckBox cb_third_rb1, cb_third_rb2, cb_third_rb3, cb_third_rb4;//三颗星
    public CheckBox cb_forth_rb1, cb_forth_rb2, cb_forth_rb3, cb_forth_rb4, cb_forth_rb5, cb_forth_rb6;//四颗星
    public CheckBox cb_fifth_rb1, cb_fifth_rb2, cb_fifth_rb3, cb_fifth_rb4, cb_fifth_rb5;//五颗星
    public EditText et_content;
    public Button ratebar1, ratebar2, ratebar22, ratebar3, ratebar33, ratebar4, ratebar44, ratebar5, ratebar55;
    public LinearLayout ll_cb_first, ll_cb_sec, ll_cb_third, ll_cb_forth, ll_cb_fifth;
    private CommonToolbar mToolbar;
    private Button btnAdd;
    private String ratingBar, rbDetailStr, assessStr, contentStr;//星级、星级对应的评价、备注内容
    private TextView tv_assess;//教师点评
    //获取星级字典id列表
    List<Map<String, String>> xingJiDicList = new ArrayList<>();
    //获取星级对应文字列表
    List<Map<String, String>> xingJiDicList1 = new ArrayList<>();
    String finalXingJiKey, finalXingJiKey1, ratingbarNum = "5";


    Map<String, String> commitMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star_rating_bar);
        getDataIntent();
        initView();
        getData();

    }

    private String mainTableId, mainPageId, tableId, pageId, dataId;
    private Map<String, String> paramsMap;

    private void getDataIntent() {
        //初始化参数Map
        paramsMap = new HashMap<>();
        //获取数据并解析
        Intent intent = getIntent();
        String buttonSetItemStr = intent.getStringExtra("itemSet");
        Map<String, Object> buttonSetItem = JSON.parseObject(buttonSetItemStr);
        Log.e(TAG, "getIntentData: buttonSetItem " + buttonSetItem.toString());
        //赋值页面标题

        //获取参数并添加
        //mainTableId
        mainTableId = String.valueOf(buttonSetItem.get("tableIdList"));
        paramsMap.put(Constant.mainTableId, mainTableId);
        //mainPageId
        mainPageId = String.valueOf(buttonSetItem.get("pageIdList"));
        paramsMap.put(Constant.mainPageId, mainPageId);
        //tableId
        tableId = String.valueOf(buttonSetItem.get("tableId"));
        paramsMap.put(Constant.tableId, tableId);
        //pageId
        pageId = String.valueOf(buttonSetItem.get("startTurnPage"));
        paramsMap.put(Constant.pageId, pageId);
        //dataId：在对列表操作的时候是没有的，只有行级操作的时候才有
        dataId = String.valueOf(buttonSetItem.get("dataId"));
        if (dataId != null && !dataId.equals("null")) {

            paramsMap.put(mainId, dataId);
        }
        Log.e(TAG, "getIntentData: paramsMap " + paramsMap.toString());
    }


    private void getData() {
        //不同页面类型请求Url不一样
        String volleyUrl = Constant.sysUrl + Constant.requestRowsAdd;
        paramsMap.put("sessionId", Constant.sessionId);
        //请求
        OkHttpUtils
                .post()
                .params(paramsMap)
                .url(volleyUrl)
                .build()
                .execute(new EdusStringCallback(StarRatingBarActivity.this) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ErrorToast.errorToast(mContext, e);
                        dialog.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e(TAG, "onResponse: " + response + "  id  " + id);
                        setStore(response);
                    }
                });
    }

    private List<Map<String, Object>> fieldSet = new ArrayList<>();
    private String keyRelation = "";
    private String hideFieldParagram = "";


    @SuppressWarnings("unchecked")
    private void setStore(String response) {

        Log.e("TAG", "解析操作数据");
        try {
            Map<String, Object> buttonSet = JSON.parseObject(response);
//获取fieldSet
            Map<String, Object> pageSet = (Map<String, Object>) buttonSet.get("pageSet");
            fieldSet = (List<Map<String, Object>>) pageSet.get("fieldSet");
            Log.e(TAG, "setStore: fieldSet " + fieldSet.toString());
//判断添加还是修改，keyRelation赋值不一样

            if (pageSet.get("relationFieldId") != null) {
                Constant.relationFieldId = String.valueOf(pageSet.get("relationFieldId"));

                keyRelation = "t0_au_" + tableId + "_" + pageId + "_" + Constant.relationFieldId + "=" + dataId;
            }

            Log.e(TAG, "setStore: keyRelation " + keyRelation);


            Log.e("TAG", "keyRelation " + keyRelation);

            //hideFieldSet,隐藏字段
            if (pageSet.get("hideFieldSet") != null) {
                List<Map<String, Object>> hideFieldSet = (List<Map<String, Object>>) pageSet.get("hideFieldSet");
                hideFieldParagram += DataProcess.toHidePageSet(hideFieldSet);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        try {//异步请求后才能走此方法
            initRateBarListener();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initView() {
        mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);
        mToolbar.setTitle(getString(R.string.assess));
        mToolbar.setBackgroundColor(getResources().getColor(topBarColor));
        //左侧返回按钮
        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        ratebar1 = (Button) findViewById(R.id.ratebar1);
        ratebar2 = (Button) findViewById(R.id.ratebar2);
        ratebar3 = (Button) findViewById(R.id.ratebar3);
        ratebar4 = (Button) findViewById(R.id.ratebar4);
        ratebar5 = (Button) findViewById(R.id.ratebar5);
        ratebar22 = (Button) findViewById(R.id.ratebar22);
        ratebar33 = (Button) findViewById(R.id.ratebar33);
        ratebar44 = (Button) findViewById(R.id.ratebar44);
        ratebar55 = (Button) findViewById(R.id.ratebar55);
        ratebar1.setOnClickListener(this);
        ratebar2.setOnClickListener(this);
        ratebar3.setOnClickListener(this);
        ratebar4.setOnClickListener(this);
        ratebar5.setOnClickListener(this);
        ratebar22.setOnClickListener(this);
        ratebar33.setOnClickListener(this);
        ratebar44.setOnClickListener(this);
        ratebar55.setOnClickListener(this);

        tv_assess = (TextView) findViewById(R.id.tv_assess);

        ll_cb_first = (LinearLayout) findViewById(R.id.ll_cb_first);
        ll_cb_sec = (LinearLayout) findViewById(R.id.ll_cb_sec);
        ll_cb_third = (LinearLayout) findViewById(R.id.ll_cb_third);
        ll_cb_forth = (LinearLayout) findViewById(R.id.ll_cb_forth);
        ll_cb_fifth = (LinearLayout) findViewById(R.id.ll_cb_fifth);

        cb_first_rb1 = (CheckBox) findViewById(R.id.cb_first_rb1);
        cb_first_rb2 = (CheckBox) findViewById(R.id.cb_first_rb2);
        cb_first_rb3 = (CheckBox) findViewById(R.id.cb_first_rb3);
        cb_first_rb4 = (CheckBox) findViewById(R.id.cb_first_rb4);
        cb_first_rb5 = (CheckBox) findViewById(R.id.cb_first_rb5);

        cb_sec_rb1 = (CheckBox) findViewById(R.id.cb_sec_rb1);
        cb_sec_rb2 = (CheckBox) findViewById(R.id.cb_sec_rb2);
        cb_sec_rb3 = (CheckBox) findViewById(R.id.cb_sec_rb3);

        cb_third_rb1 = (CheckBox) findViewById(R.id.cb_third_rb1);
        cb_third_rb2 = (CheckBox) findViewById(R.id.cb_third_rb2);
        cb_third_rb3 = (CheckBox) findViewById(R.id.cb_third_rb3);
        cb_third_rb4 = (CheckBox) findViewById(R.id.cb_third_rb4);

        cb_forth_rb1 = (CheckBox) findViewById(R.id.cb_forth_rb1);
        cb_forth_rb2 = (CheckBox) findViewById(R.id.cb_forth_rb2);
        cb_forth_rb3 = (CheckBox) findViewById(R.id.cb_forth_rb3);
        cb_forth_rb4 = (CheckBox) findViewById(R.id.cb_forth_rb4);
        cb_forth_rb5 = (CheckBox) findViewById(R.id.cb_forth_rb5);
        cb_forth_rb6 = (CheckBox) findViewById(R.id.cb_forth_rb6);

        cb_fifth_rb1 = (CheckBox) findViewById(R.id.cb_fifth_rb1);
        cb_fifth_rb2 = (CheckBox) findViewById(R.id.cb_fifth_rb2);
        cb_fifth_rb3 = (CheckBox) findViewById(R.id.cb_fifth_rb3);
        cb_fifth_rb4 = (CheckBox) findViewById(R.id.cb_fifth_rb4);
        cb_fifth_rb5 = (CheckBox) findViewById(R.id.cb_fifth_rb5);
        addTag();
        et_content = (EditText) findViewById(R.id.et_content);
        btnAdd = (Button) findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(this);


    }
    String xingJiKey,xingJiKey1,xingJiKey2;
    private void initRateBarListener() {
        Log.e(TAG, "initRateBarListener: fieldSet " + fieldSet.toString());
        Map<String, Object> xingJiMap = fieldSet.get(0);
        Map<String, Object> xingJiMap1 = fieldSet.get(1);
        Map<String, Object> xingJiMapNum = fieldSet.get(fieldSet.size()-1);

        //获取提交的key
        xingJiKey = String.valueOf(xingJiMap.get(Constant.primKey));
        xingJiKey1 = String.valueOf(xingJiMap1.get(Constant.primKey));
        xingJiKey2 = String.valueOf(xingJiMapNum.get(Constant.primKey));
        //获取不到key时所用的默认值
        if (xingJiKey.equals("") || xingJiKey.equals("null")) {
            xingJiKey = "t0_au_262_3020_4651";

        }
        if (xingJiKey1.equals("") || xingJiKey1.equals("null")) {
            xingJiKey1 = "t0_au_262_3020_4671";

        }
        //获取星级字典id列表
        xingJiDicList = (List<Map<String, String>>) xingJiMap.get("dicList");
        //获取星级对应文字列表
        xingJiDicList1 = (List<Map<String, String>>) xingJiMap1.get("dicList");

        String finalXingJiKey = xingJiKey;
        String finalXingJiKey1 = xingJiKey1;
        //默认好评，不选择任何项，直接提交评价为五星好评
        ll_cb_first.setVisibility(View.GONE);
        ll_cb_sec.setVisibility(View.GONE);
        ll_cb_third.setVisibility(View.GONE);
        ll_cb_forth.setVisibility(View.GONE);
        ll_cb_fifth.setVisibility(View.VISIBLE);
        tv_assess.setText(xingJiDicList1.get(0).get("DIC_NAME"));
        commitMap.put(xingJiKey, xingJiDicList.get(0).get("DIC_ID"));
        commitMap.put(xingJiKey1, xingJiDicList1.get(0).get("DIC_ID"));
        commitMap.put(xingJiKey2, "5");
        //  initChoise();
    }

    Map<String, CheckBox> mapCheckBox = new HashMap<>();

    //将checkBox加上标签，以便提交的时候直接遍历获取id
    public void addTag() {

        mapCheckBox = new HashMap<>();
        //一星五个
        mapCheckBox.put("893", cb_first_rb1);
        mapCheckBox.put("894", cb_first_rb2);
        mapCheckBox.put("895", cb_first_rb3);
        mapCheckBox.put("896", cb_first_rb4);
        mapCheckBox.put("897", cb_first_rb5);
//二星三个
        mapCheckBox.put("898", cb_sec_rb1);
        mapCheckBox.put("899", cb_sec_rb2);
        mapCheckBox.put("900", cb_sec_rb3);
//三星四个
        mapCheckBox.put("901", cb_third_rb1);
        mapCheckBox.put("902", cb_third_rb2);
        mapCheckBox.put("903", cb_third_rb3);
        mapCheckBox.put("904", cb_third_rb4);
//四星6个
        mapCheckBox.put("905", cb_forth_rb1);
        mapCheckBox.put("906", cb_forth_rb2);
        mapCheckBox.put("907", cb_forth_rb3);
        mapCheckBox.put("908", cb_forth_rb4);
        mapCheckBox.put("909", cb_forth_rb5);
        mapCheckBox.put("910", cb_forth_rb6);
//五星五个
        mapCheckBox.put("911", cb_fifth_rb1);
        mapCheckBox.put("912", cb_fifth_rb2);
        mapCheckBox.put("913", cb_fifth_rb3);
        mapCheckBox.put("914", cb_fifth_rb4);
        mapCheckBox.put("915", cb_fifth_rb5);
    }


//每次点击五星都要清除选择的checkBox

    public void clearCheckBox() {
        for (Map.Entry<String, CheckBox> entry : mapCheckBox.entrySet()) {

            if (entry.getValue().isChecked()) {
                entry.getValue().setChecked(false);
            }
        }

    }

    private static final String TAG = "StarRatingBarActivity";


    private void getCommit() {
        String pingJiaValue = "";
        for (Map.Entry entry : commitMap.entrySet()) {
            pingJiaValue += entry.getKey() + "=" + entry.getValue() + "&";
        }

        if (pingJiaValue.length() > 0) {
            pingJiaValue = pingJiaValue.substring(0, pingJiaValue.length() - 1);
        }

        if (hasInternetConnected()) {
            dialog.show();
            String volleyUrl1 = Constant.sysUrl + Constant.commitAdd + "?" +
                    Constant.tableId + "=" + tableId + "&" + Constant.pageId + "=" + pageId + "&" +
                    pingJiaValue + "&" + hideFieldParagram + "&" + keyRelation+"&sessionId="+Constant.sessionId;

            //请求地址（关联添加和修改）
            String volleyUrl = volleyUrl1.replaceAll(" ", "%20").replaceAll("&&", "&");
            //get请求
            OkHttpUtils
                    .get()
                    .url(volleyUrl)
                    .build()
                    .execute(new EdusStringCallback(StarRatingBarActivity.this) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            ErrorToast.errorToast(mContext, e);
                            dialog.dismiss();
                            Toast.makeText(StarRatingBarActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.e(TAG, "onResponse: " + response);
                            if (response != null && !response.equals("0")) {
                                //返回列表页面并刷新
                                backToInfo();
                            } else {
                                Toast.makeText(StarRatingBarActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "无网络", Toast.LENGTH_SHORT).show();
        }


    }

    public void backToInfo() {
        Toast.makeText(this, "评价成功", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
        this.finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                getCommitCheckBox();//点选评语
                getComment();//打字评语
                if (commitMap.size() > 0) {
                    getCommit();
                } else {
                    Toast.makeText(this, "请至少选择一项", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.ratebar1:
                if (ratebar2.getVisibility() == View.VISIBLE) {
                    ratebar2.setVisibility(View.GONE);
                    ratebar3.setVisibility(View.GONE);
                    ratebar4.setVisibility(View.GONE);
                    ratebar5.setVisibility(View.GONE);
                    ratebar22.setVisibility(View.VISIBLE);
                    ratebar33.setVisibility(View.VISIBLE);
                    ratebar44.setVisibility(View.VISIBLE);
                    ratebar55.setVisibility(View.VISIBLE);
                }
                tv_assess.setText(xingJiDicList1.get(4).get("DIC_NAME"));
                commitMap.put(xingJiKey, xingJiDicList.get(4).get("DIC_ID"));
                commitMap.put(xingJiKey1, xingJiDicList1.get(4).get("DIC_ID"));
                commitMap.put(xingJiKey2, "1");
                clearCheckBox();
                ll_cb_first.setVisibility(View.VISIBLE);
                ll_cb_sec.setVisibility(View.GONE);
                ll_cb_third.setVisibility(View.GONE);
                ll_cb_forth.setVisibility(View.GONE);
                ll_cb_fifth.setVisibility(View.GONE);
//                commitMap.put(finalXingJiKey, xingJiDicList.get(4).get("DIC_ID"));
//                commitMap.put(finalXingJiKey1, xingJiDicList1.get(4).get("DIC_ID"));

                  Log.e(TAG, "onCheckedChanged: commitMap " + commitMap.toString());
                break;
            case R.id.ratebar2:
            case R.id.ratebar22:
                if (ratebar3.getVisibility() == View.VISIBLE) {
                    ratebar3.setVisibility(View.GONE);
                    ratebar4.setVisibility(View.GONE);
                    ratebar5.setVisibility(View.GONE);
                    ratebar33.setVisibility(View.VISIBLE);
                    ratebar44.setVisibility(View.VISIBLE);
                    ratebar55.setVisibility(View.VISIBLE);

                } else {
                    ratebar2.setVisibility(View.VISIBLE);
                    ratebar22.setVisibility(View.GONE);
                }
                tv_assess.setText(xingJiDicList1.get(3).get("DIC_NAME"));
                commitMap.put(xingJiKey, xingJiDicList.get(3).get("DIC_ID"));
                commitMap.put(xingJiKey1, xingJiDicList1.get(3).get("DIC_ID"));
                commitMap.put(xingJiKey2, "2");
                clearCheckBox();
                ll_cb_first.setVisibility(View.GONE);
                ll_cb_sec.setVisibility(View.VISIBLE);
                ll_cb_third.setVisibility(View.GONE);
                ll_cb_forth.setVisibility(View.GONE);
                ll_cb_fifth.setVisibility(View.GONE);
                Log.e(TAG, "onCheckedChanged: commitMap " + commitMap.toString());
                break;
            case R.id.ratebar3:
            case R.id.ratebar33:
                if (ratebar4.getVisibility() == View.VISIBLE) {
                    ratebar4.setVisibility(View.GONE);
                    ratebar5.setVisibility(View.GONE);
                    ratebar44.setVisibility(View.VISIBLE);
                    ratebar55.setVisibility(View.VISIBLE);

                } else {
                    ratebar2.setVisibility(View.VISIBLE);
                    ratebar22.setVisibility(View.GONE);
                    ratebar3.setVisibility(View.VISIBLE);
                    ratebar33.setVisibility(View.GONE);
                }
                tv_assess.setText(xingJiDicList1.get(2).get("DIC_NAME"));
                commitMap.put(xingJiKey, xingJiDicList.get(2).get("DIC_ID"));
                commitMap.put(xingJiKey1, xingJiDicList1.get(2).get("DIC_ID"));
                commitMap.put(xingJiKey2, "3");
                clearCheckBox();
                ll_cb_first.setVisibility(View.GONE);
                ll_cb_sec.setVisibility(View.GONE);
                ll_cb_third.setVisibility(View.VISIBLE);
                ll_cb_forth.setVisibility(View.GONE);
                ll_cb_fifth.setVisibility(View.GONE);
                Log.e(TAG, "onCheckedChanged: commitMap " + commitMap.toString());
                break;
            case R.id.ratebar4:
            case R.id.ratebar44:

                if (ratebar5.getVisibility() == View.VISIBLE) {
                    ratebar5.setVisibility(View.GONE);
                    ratebar55.setVisibility(View.VISIBLE);
                } else {
                    ratebar2.setVisibility(View.VISIBLE);
                    ratebar22.setVisibility(View.GONE);
                    ratebar3.setVisibility(View.VISIBLE);
                    ratebar33.setVisibility(View.GONE);
                    ratebar4.setVisibility(View.VISIBLE);
                    ratebar44.setVisibility(View.GONE);
                }
                tv_assess.setText(xingJiDicList1.get(1).get("DIC_NAME"));
                commitMap.put(xingJiKey, xingJiDicList.get(1).get("DIC_ID"));
                commitMap.put(xingJiKey1, xingJiDicList1.get(1).get("DIC_ID"));
                commitMap.put(xingJiKey2, "4");

                clearCheckBox();
                ll_cb_first.setVisibility(View.GONE);
                ll_cb_sec.setVisibility(View.GONE);
                ll_cb_third.setVisibility(View.GONE);
                ll_cb_forth.setVisibility(View.VISIBLE);
                ll_cb_fifth.setVisibility(View.GONE);
                Log.e(TAG, "onCheckedChanged: commitMap " + commitMap.toString());
                break;
            case R.id.ratebar5:
                break;
            case R.id.ratebar55:
                clearCheckBox();
                ratebar2.setVisibility(View.VISIBLE);
                ratebar22.setVisibility(View.GONE);
                ratebar3.setVisibility(View.VISIBLE);
                ratebar33.setVisibility(View.GONE);
                ratebar4.setVisibility(View.VISIBLE);
                ratebar44.setVisibility(View.GONE);
                ratebar5.setVisibility(View.VISIBLE);
                ratebar55.setVisibility(View.GONE);

                ll_cb_first.setVisibility(View.GONE);
                ll_cb_sec.setVisibility(View.GONE);
                ll_cb_third.setVisibility(View.GONE);
                ll_cb_forth.setVisibility(View.GONE);
                ll_cb_fifth.setVisibility(View.VISIBLE);
                tv_assess.setText(xingJiDicList1.get(0).get("DIC_NAME"));
                commitMap.put(xingJiKey, xingJiDicList.get(0).get("DIC_ID"));
                commitMap.put(xingJiKey1, xingJiDicList1.get(0).get("DIC_ID"));
                commitMap.put(xingJiKey2, "5");
                Log.e(TAG, "onCheckedChanged: commitMap " + commitMap.toString());
                break;
            default:
                break;
        }


    }


    //获取评语备注
    private void getComment() {
//获取key
        Map<String, Object> commentMap = fieldSet.get(2);
        String commentKey = String.valueOf(commentMap.get(Constant.primKey));
//获取值
        commitMap.put(commentKey, et_content.getText().toString());
        Log.e(TAG, "getComment: commitMap " + commitMap.toString());
    }

    //获取点选checkBox后得到的id们
    public void getCommitCheckBox() {
//获取key
        Map<String, Object> checkMap = fieldSet.get(3);
        String checkKey = String.valueOf(checkMap.get(Constant.primKey)) + "_dicMany";//此key多了一个dicMany
//获取值
        List<String> value = new ArrayList<>();
        for (Map.Entry<String, CheckBox> entry : mapCheckBox.entrySet()) {

            entry.getValue().isChecked();
            if (entry.getValue().isChecked()) {
                value.add(entry.getKey());
            }
        }

        String value1 = DataProcess.listToString(value);
//最后将id放进参数
        commitMap.put(checkKey, value1);
        Log.e(TAG, "getCommitCheckBox: commitMap " + commitMap.toString());
    }
}
