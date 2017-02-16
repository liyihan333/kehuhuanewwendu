package com.kwsoft.version;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.kwsoft.kehuhua.adcustom.ExampleUtil;
import com.kwsoft.kehuhua.adcustom.MessagAlertActivity;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.sessionService.SessionService;
import com.kwsoft.kehuhua.utils.CloseActivityClass;
import com.kwsoft.kehuhua.utils.Utils;
import com.kwsoft.kehuhua.widget.CommonToolbar;
import com.kwsoft.kehuhua.zxing.TestScanActivity;
import com.kwsoft.version.androidRomType.AndtoidRomUtil;
import com.kwsoft.version.fragment.CourseFragment;
import com.kwsoft.version.fragment.MeFragment;
import com.kwsoft.version.fragment.MenuFragment;
import com.kwsoft.version.fragment.StuFragmentTabAdapter;
import com.kwsoft.version.fragment.StudyFragment;
import com.kwsoft.version.view.CustomDialog;
import com.pgyersdk.update.PgyUpdateManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 学员端看板界面
 * wyl
 */
public class StuMainActivity extends BaseActivity implements View.OnClickListener {
    StuFragmentTabAdapter stutabAdapter;
    private RadioGroup radioGroup;
      private RadioButton radio3;
    private String arrStr;
    private String menuDataMap, menuList;//看板数据、课程表数据、主菜单数据
    private CommonToolbar mToolbar;
    private String hideMenuList;//获取我的界面中的tableid pageid 个人资料
    private String homePageList;//今明日课表
    private String feedbackInfoList;//反馈信息
    public static boolean isForeground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_main);
//        getSupportActionBar().hide();
        CloseActivityClass.activityList.add(this);
        initView();
        initFragment();
        // initDialog();
        PgyUpdateManager.register(this);
        Utils.startPollingService(mContext, 5 * 60, SessionService.class, SessionService.ACTION);//启动20分钟一次的轮询获取session服务
        registerMessageReceiver();  // used for receive msg
    }

    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    private static final String TAG = "StuMainActivity";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
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

    private void setCostomMsg(String msg) {
        Log.e(TAG, "setCostomMsg: msg " + msg);
    }

    public void initDialog() {

        CustomDialog.Builder builder = new CustomDialog.Builder(StuMainActivity.this);
//                builder.setMessage("这个就是自定义的提示框");
        builder.setTitle(getResources().getString(R.string.start_school));
        builder.setPositiveButton(getResources().getString(R.string.i_known), new DialogInterface.OnClickListener() {
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

    @Override
    public void initView() {
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        ((RadioButton) radioGroup.findViewById(R.id.radio0)).setChecked(true);// 设置radiogroup的机制

        RadioButton radio0 = (RadioButton) findViewById(R.id.radio0);
        RadioButton radio2 = (RadioButton) findViewById(R.id.radio2);
        // radio3 = (RadioButton) findViewById(R.id.radio3);

        Intent intent = getIntent();
        arrStr = intent.getStringExtra("jsonArray");
        //   menuList = intent.getStringExtra("menuList");

        menuDataMap = intent.getStringExtra("menuDataMap");
        hideMenuList = intent.getStringExtra("hideMenuList");
        homePageList = intent.getStringExtra("homePageList");
        feedbackInfoList = intent.getStringExtra("feedbackInfoList");

        mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);
//        Resources resources = mContext.getResources().getDrawable(R.drawable.nav_news);
//        Drawable drawable = resources.getDrawable(R.drawable.nav_news);
//        mToolbar.setRightButtonIcon(mContext.getResources().getDrawable(R.drawable.nav_news));
//        mToolbar.setLeftButtonIcon(mContext.getResources().getDrawable(R.drawable.nav_scan_code));
        mToolbar.setTitle(getString(R.string.project_name));
        mToolbar.setRightButtonIcon(getResources().getDrawable(R.drawable.nav_news));

        mToolbar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(StuMainActivity.this, MessagAlertActivity.class);
                startActivity(intent2);
            }
        });

//        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                toCamera();
//            }
//        });

    }


    public void initFragment() {
        Fragment studyFragment = new StudyFragment();
        Fragment menuFragment = new MenuFragment();
        Fragment courseFragment = new CourseFragment();
        Fragment meFragment = new MeFragment();

        Bundle studyBundle = new Bundle();
        studyBundle.putString("arrStr", arrStr);
        studyBundle.putString("menuDataMap", menuDataMap);
        studyBundle.putString("homePageList", homePageList);
        Log.e("homlie", homePageList);
        studyBundle.putBoolean("isLogin", true);
        studyFragment.setArguments(studyBundle);

        Bundle menuBundle = new Bundle();
        menuBundle.putString("menuDataMap", menuDataMap);
        menuFragment.setArguments(menuBundle);

//        Bundle courseBundle = new Bundle();
//          courseBundle.putString("menuList", menuList);
//        courseFragment.setArguments(courseBundle);

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

                        break;
                    case R.id.radio2:

                        break;
                    case R.id.radio3:

                        break;

                }
            }
        });
    }

//    public void fragmentClick() {
//        radio3.setChecked(true);
//    }


    public void toCamera() {

        boolean emui = AndtoidRomUtil.isEMUI();
        boolean miui = AndtoidRomUtil.isMIUI();
        boolean flyme = AndtoidRomUtil.isFlyme();

        if (emui) {
            //华为
//                    PackageManager pm = getActivity().getPackageManager();
//                    //MediaStore.ACTION_IMAGE_CAPTURE android.permission.RECORD_AUDIO
//                    boolean permission = (PackageManager.PERMISSION_GRANTED ==
//                            pm.checkPermission("MediaStore.ACTION_IMAGE_CAPTURE", "packageName"));
//                    if (permission) {
//                        Intent intent = new Intent(getActivity(), CaptureActivity.class);
//                        startActivityForResult(intent, 1);
//                    } else {
//                        Constant.goHuaWeiSetting(getActivity());
//                    }
            Intent intent = new Intent(StuMainActivity.this, TestScanActivity.class);
            startActivityForResult(intent, 1);
        } else if (miui) {
            //小米
            Intent intent = new Intent(StuMainActivity.this, TestScanActivity.class);
            startActivityForResult(intent, 1);
        } else if (flyme) {
            //魅族rom
            Intent intent = new Intent(StuMainActivity.this, TestScanActivity.class);
            startActivityForResult(intent, 1);
        } else {
            Intent intent = new Intent(StuMainActivity.this, TestScanActivity.class);
            startActivityForResult(intent, 1);
        }
    }

    private static long exitTime = 0;// 退出时间


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if ((System.currentTimeMillis() - exitTime) > 1000) {

                String msg = getString(R.string.press_another_exit);
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

                exitTime = System.currentTimeMillis();
            } else {
                Toast.makeText(this, R.string.exit, Toast.LENGTH_SHORT).show();
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
        Utils.stopPollingService(this, SessionService.class, SessionService.ACTION);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        Utils.stopPollingService(this, SessionService.class, SessionService.ACTION);
        super.onStop();
    }
}
