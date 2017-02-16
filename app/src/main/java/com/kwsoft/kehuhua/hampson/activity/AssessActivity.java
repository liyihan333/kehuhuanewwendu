package com.kwsoft.kehuhua.hampson.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.hampson.activity.CourseHpsFragment;
import com.kwsoft.kehuhua.hampson.activity.CourseRatingBarFragment;
import com.kwsoft.kehuhua.widget.CommonToolbar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.kwsoft.kehuhua.config.Constant.pageId;
import static com.kwsoft.kehuhua.config.Constant.tableId;

/**
 * Created by Administrator on 2016/11/10 0010.
 */

public class AssessActivity extends BaseActivity {
    private CommonToolbar mToolbar;
    private FrameLayout fragment_container;
    private List<Map<String, Object>> childList = new ArrayList<>();
    private PopupWindow toolListPop, childListPop;

    private FragmentManager manager;
    private List<Fragment> mFragments = new ArrayList<>();
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assess_stu);
        initView();
        getDataIntent();

    }

    @Override
    public void initView() {
        fragment_container = (FrameLayout) findViewById(R.id.fragment_container);

        mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);
        mToolbar.setBackgroundColor(getResources().getColor(Constant.topBarColor));
        mToolbar.setRightButtonIcon(getResources().getDrawable(R.mipmap.often_more)); //右侧pop
        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() { //左侧返回按钮
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
    }

    /**
     * 接收菜单传递过来的模块数据包
     */
    public void getDataIntent() {
        Map<String, Object> itemMap = new HashMap<>();
        Intent intent = getIntent();
        String itemData = intent.getStringExtra("itemData");
        if (intent.getStringExtra("childData") != null) {

            String childData = intent.getStringExtra("childData");
            childList = JSON.parseObject(childData,
                    new TypeReference<List<Map<String, Object>>>() {
                    });
        }
        try {
            itemMap = JSON.parseObject(itemData,
                    new TypeReference<Map<String, Object>>() {
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }

        String titleName;
        if (childList.size() > 0) {
            titleName = childList.get(0).get("menuName") + "";
            for (int i = 0; i < childList.size(); i++) {

                String fragmentTableId = childList.get(i).get("tableId") + "";
                String fragmentPageId = childList.get(i).get("pageId") + "";
                Map<String, String> paramsMap = new HashMap<>();
                paramsMap.put(tableId, fragmentTableId);
                paramsMap.put(pageId, fragmentPageId);
                paramsMap.put(Constant.timeName, "-1");
                String listFragmentData = JSON.toJSONString(paramsMap);
                Bundle listBundle = new Bundle();
                listBundle.putString("listFragmentData", listFragmentData);

                CourseRatingBarFragment listFragment = new CourseRatingBarFragment();
                listFragment.setArguments(listBundle);
                mFragments.add(listFragment);
                transaction.add(R.id.fragment_container, listFragment);
            }

            transaction.replace(R.id.fragment_container, mFragments.get(0));//把f1的界面替换container
            transaction.commit();

            mToolbar.showChildIv();
            mToolbar.setTextTitleOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    childChose();
                }
            });

        } else {
            String fragmentTableId = itemMap.get("tableId") + "";
            String fragmentPageId = itemMap.get("pageId") + "";
            titleName = itemMap.get("menuName") + "";

            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put(tableId, fragmentTableId);
            paramsMap.put(pageId, fragmentPageId);
            paramsMap.put(Constant.timeName, "-1");
            String listFragmentData = JSON.toJSONString(paramsMap);
            Bundle listBundle = new Bundle();
            listBundle.putString("listFragmentData", listFragmentData);

            CourseRatingBarFragment xFragment= new CourseRatingBarFragment();

            xFragment.setArguments(listBundle);
            transaction.add(R.id.fragment_container, xFragment);
            transaction.replace(R.id.fragment_container, xFragment);//把f1的界面替换container
            transaction.commit();
        }
        mToolbar.setTitle(titleName);
//        Constant.mainTableIdValue = tableId;
//        Constant.mainPageIdValue = pageId;

    }

    //顶部展开popwindow 选择子菜单切换
    public void childChose() {
        Log.e("TAG", "展开子菜单popWindow");
        try {
            if (childList.size() > 0) {

                if (childListPop != null && childListPop.isShowing()) {
                    childListPop.dismiss();
                } else {
                    final View toolLayout = getLayoutInflater().inflate(
                            R.layout.activity_list_childlist, null);
                    ListView childListPopView = (ListView) toolLayout
                            .findViewById(R.id.child_menu_List);
                    for (int i = 0; i < childList.size(); i++) {
                        childList.get(i).put("image", R.mipmap.often_drop_curriculum);
                    }

                    final SimpleAdapter adapter = new SimpleAdapter(
                            this,
                            childList,
                            R.layout.activity_list_childlist_item,
                            new String[]{"image", "menuName"},
                            new int[]{R.id.childListItemImg, R.id.childListItemName});
                    childListPopView.setAdapter(adapter);
                    // 点击listview中item的处理
                    childListPopView
                            .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> arg0,
                                                        View arg1, int arg2, long arg3) {
//                                    refreshPage(arg2);
                                    String transTitleName = childList.get(arg2).get("menuName") + "";
                                    mToolbar.setTitle(transTitleName);
                                    FragmentTransaction transaction = manager.beginTransaction();
                                    transaction.replace(R.id.fragment_container, mFragments.get(arg2));//把f1的界面替换container
                                    transaction.commit();
                                    // 隐藏弹出窗口
                                    if (childListPop != null && childListPop.isShowing()) {
                                        childListPop.dismiss();
                                    }
                                }
                            });
                    DisplayMetrics metric = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metric);
                    int width = metric.widthPixels;     // 屏幕宽度（像素）
                    childListPop = new PopupWindow(toolLayout, (width / 3) * 2,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    //设置半透明
                    WindowManager.LayoutParams params = getWindow().getAttributes();
                    getWindow().setAttributes(params);

                    childListPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            WindowManager.LayoutParams params = getWindow().getAttributes();
                            getWindow().setAttributes(params);
                        }
                    });
                    childListPop.setTouchable(true); // 设置popupwindow可点击
                    childListPop.setOutsideTouchable(true); // 设置popupwindow外部可点击
                    childListPop.setFocusable(true); // 获取焦点
                    childListPop.update();
                    toolLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    int delta = width / 6;
                    childListPop.showAsDropDown(mToolbar, delta, 0);
                    childListPop.setTouchInterceptor(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                                childListPop.dismiss();
                                return true;
                            }
                            return false;
                        }
                    });

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
