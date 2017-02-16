package com.kwsoft.version;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kwsoft.kehuhua.adcustom.ExampleUtil;
import com.kwsoft.kehuhua.adcustom.MessagAlertActivity;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.sessionService.SessionService;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.utils.CloseActivityClass;
import com.kwsoft.kehuhua.utils.Utils;
import com.kwsoft.kehuhua.widget.CnToolbar;
import com.kwsoft.version.fragment.AssortFragment;
import com.kwsoft.version.fragment.CourseFragment;
import com.kwsoft.version.fragment.MeFragment;
import com.kwsoft.version.fragment.StuFragmentTabAdapter;
import com.kwsoft.version.fragment.StudyFragment;
import com.kwsoft.version.view.CustomDialog;
import com.kwsoft.version.zxing.TestScanActivity;
import com.pgyersdk.update.PgyUpdateManager;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import okhttp3.Call;

/**
 * 学员端看板界面
 * wyl
 */
public class StuMainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "StuMainActivity";
    StuFragmentTabAdapter stutabAdapter;
    private RadioGroup radioGroup;
    private RadioButton radio3;
    private String arrStr, menuList, menuDataMap;//看板数据、课程表数据、主菜单数据
    private CnToolbar mToolbar;
    SharedPreferences sPreferences;
    private String useridOld;
    AssortFragment menuFragment;
    private String hideMenuList;//获取我的界面中的tableid pageid 个人资料
    private String feedbackInfoList;//反馈信息
    String admissInfoContent;//入学通知内容
    public static boolean isForeground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_main);
//        getSupportActionBar().hide();
        CloseActivityClass.activityList.add(this);
        sPreferences = getSharedPreferences(Constant.proId, MODE_PRIVATE);
        useridOld = sPreferences.getString("useridOld", "");

        initView();
        initFragment();
        if (!Constant.USERID.equals(useridOld)) {
            initDialog();
            sPreferences.edit().putString("useridOld", Constant.USERID).apply();
        }
        PgyUpdateManager.register(this);
        Utils.startPollingService(mContext, 30, SessionService.class, SessionService.ACTION);//启动20分钟一次的轮询获取session服务
        registerMessageReceiver();  // used for receive msg

    }

    public void initDialog() {
        String admissionInfoUrl = Constant.sysUrl + Constant.requestListData;
        //参数
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("tableId", "313");
        paramsMap.put("pageId", "2782");
        //请求
        OkHttpUtils
                .post()
                .params(paramsMap)
                .url(admissionInfoUrl)
                .build()
                .execute(new EdusStringCallback(StuMainActivity.this) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ErrorToast.errorToast(mContext, e);
                        Log.e(TAG, "onError: Call  " + call + "  id  " + id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e(TAG, "网络获取添加数据" + response);
                        //DLCH.put(volleyUrl + paramsStr, jsonData);
//                        setStore(jsonData);
                        Map<String, Object> admissionMap = JSON.parseObject(response,
                                new TypeReference<Map<String, Object>>() {
                                });
                        List<Map<String, Object>> rowsMap = (List<Map<String, Object>>) admissionMap.get("rows");
                        Map<String, Object> map = rowsMap.get(0);
                        String AFM_1Id = map.get("AFM_1").toString();

                        // String admissionInfoUrl2 = "http://192.168.6.150:8081/hps_edus_auto/phone_startSchoolInfo.do";
                        String admissionInfoUrl2 = Constant.sysUrl + Constant.admissionUrl;
                        Map<String, String> paramsMap2 = new HashMap<>();
                        paramsMap2.put("id", AFM_1Id);
                        //  paramsMap2.put("id", "794");

                        OkHttpUtils
                                .post()
                                .params(paramsMap2)
                                .url(admissionInfoUrl2)
                                .build()
                                .execute(new EdusStringCallback(StuMainActivity.this) {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        ErrorToast.errorToast(mContext, e);
                                        Log.e(TAG, "onError: Call  " + call + "  id  " + id);
                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        Log.e(TAG, "网络获取添加数据" + response);
                                        admissInfoContent = response;

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                CustomDialog.Builder builder = new CustomDialog.Builder(StuMainActivity.this);
//                builder.setMessage("这个就是自定义的提示框");
                                                builder.setTitle("入学须知");
                                                if (admissInfoContent == null || admissInfoContent.length() <= 0) {
                                                    admissInfoContent = "暂无数据！";
                                                }
                                                builder.setMessage(admissInfoContent);
                                                builder.setPositiveButton("我知道了!", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        //设置你的操作事项
                                                    }
                                                });

                                                builder.setNegativeButton("",
                                                        new android.content.DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }
                                                        });

                                                builder.create().show();
                                            }
                                        });
                                    }
                                });
                    }
                });
    }

    @Override
    public void initView() {
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        ((RadioButton) radioGroup.findViewById(R.id.radio0)).setChecked(true);// 设置radiogroup的机制

        RadioButton radio0 = (RadioButton) findViewById(R.id.radio0);
        RadioButton radio1 = (RadioButton) findViewById(R.id.radio1);
        RadioButton radio2 = (RadioButton) findViewById(R.id.radio2);
        radio3 = (RadioButton) findViewById(R.id.radio3);

        Intent intent = getIntent();
        arrStr = intent.getStringExtra("jsonArray");
//        menuList = intent.getStringExtra("menuList");
        menuList = "";
        hideMenuList = intent.getStringExtra("hideMenuList");
        menuDataMap = intent.getStringExtra("menuDataMap");
        feedbackInfoList = intent.getStringExtra("feedbackInfoList");
        Log.e("hidemel", JSON.toJSONString(hideMenuList));
        Log.e("feedb", JSON.toJSONString(feedbackInfoList));

        mToolbar = (CnToolbar) findViewById(R.id.stu_toolbar);
//        Resources resources = mContext.getResources().getDrawable(R.drawable.nav_news);
//        Drawable drawable = resources.getDrawable(R.drawable.nav_news);
//        mToolbar.setRightButtonIcon(mContext.getResources().getDrawable(R.drawable.nav_news));
        mToolbar.setLeftButtonIcon(mContext.getResources().getDrawable(R.drawable.nav_scan_code));
        mToolbar.setTitle("学员端");
        mToolbar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(StuMainActivity.this, MessagAlertActivity.class);
                startActivity(intent2);
            }
        });

        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionGen.with(StuMainActivity.this)
                        .addRequestCode(105)
                        .permissions(
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .request();
            }
        });

    }


    public void initFragment() {
        Fragment studyFragment = new StudyFragment();
        Fragment courseFragment = new CourseFragment();
        AssortFragment menuFragment = new AssortFragment();
        Fragment meFragment = new MeFragment();

        Bundle studyBundle = new Bundle();
        studyBundle.putString("arrStr", arrStr);
        studyBundle.putString("menuDataMap", menuDataMap);
        studyBundle.putBoolean("isLogin", true);
        studyFragment.setArguments(studyBundle);

        Bundle courseBundle = new Bundle();
        courseBundle.putString("menuList", menuList);
        courseFragment.setArguments(courseBundle);

        Bundle menuBundle = new Bundle();
        menuBundle.putString("menuDataMap", menuDataMap);
        menuFragment.setArguments(menuBundle);

        Bundle meBundle = new Bundle();
        meBundle.putString("hideMenuList", hideMenuList);
        meBundle.putString("feedbackInfoList", feedbackInfoList);
        meFragment.setArguments(meBundle);

        List<Fragment> mFragments = new ArrayList<>();
        mFragments.add(studyFragment);
        mFragments.add(menuFragment);
        mFragments.add(courseFragment);
        mFragments.add(meFragment);
        stutabAdapter = new StuFragmentTabAdapter(this, mFragments, R.id.content, radioGroup);

        stutabAdapter.setOnRgsExtraCheckedChangedListener(new StuFragmentTabAdapter.OnRgsExtraCheckedChangedListener() {
            @Override
            public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {
                // TODO Auto-generated method stub
                super.OnRgsExtraCheckedChanged(radioGroup, checkedId, index);
                switch (checkedId) {
                    case R.id.radio0:
                        mToolbar.setTitle("学员端");
                        break;
                    case R.id.radio1:
                        mToolbar.setTitle("课程表");
                        break;
                    case R.id.radio2:
                        mToolbar.setTitle("学员端");
                        break;
                    case R.id.radio3:
                        mToolbar.setTitle("学员端");

                        break;

                }
            }
        });
    }
    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
    }


    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }



    public void fragmentClick() {
        radio3.setChecked(true);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = 105)
    public void doCapture() {
        toCamera();
    }

    @PermissionFail(requestCode = 105)
    public void doFailedCapture() {
        Toast.makeText(StuMainActivity.this, "获取权限失败", Toast.LENGTH_SHORT).show();
    }

    public void toCamera() {
        Intent intent = new Intent(StuMainActivity.this,TestScanActivity.class);
        startActivityForResult(intent, 1);
    }

    private static long exitTime = 0;// 退出时间

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if ((System.currentTimeMillis() - exitTime) > 1000) {

                String msg = "再按一次退出";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

                exitTime = System.currentTimeMillis();
            } else {
                Toast.makeText(this, "直接退出", Toast.LENGTH_SHORT).show();
//                CloseActivityClass.exitClient(this);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                toCamera();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mMessageReceiver);
        super.onDestroy();
        Utils.stopPollingService(this, SessionService.class, SessionService.ACTION);
    }


    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (!ExampleUtil.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
                setCostomMsg(showMsg.toString());
            }
        }
    }

    private void setCostomMsg(String msg){
        Log.e(TAG, "setCostomMsg: msg "+msg);
    }
}

