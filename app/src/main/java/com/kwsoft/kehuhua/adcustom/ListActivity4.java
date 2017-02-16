package com.kwsoft.kehuhua.adcustom;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kwsoft.kehuhua.adapter.PageFragmentAdapter;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.bean.Channel;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.fragments.ListFragment;
import com.kwsoft.kehuhua.fragments.TeaachAmountFragment;
import com.kwsoft.kehuhua.hampson.activity.CourseHpsFragment;
import com.kwsoft.kehuhua.hampson.activity.CourseRatingBarFragment;
import com.kwsoft.kehuhua.hampson.activity.StageTestFragment;
import com.kwsoft.kehuhua.hampson.activity.StuClassTchFragment;
import com.kwsoft.kehuhua.utils.CloseActivityClass;
import com.kwsoft.kehuhua.widget.CommonToolbar;
import com.kwsoft.version.StuPra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

import static com.kwsoft.kehuhua.config.Constant.buttonSet;
import static com.kwsoft.kehuhua.config.Constant.pageId;
import static com.kwsoft.kehuhua.config.Constant.tableId;
import static com.kwsoft.kehuhua.config.Constant.topBarColor;

/**
 * Created by Administrator on 2016/11/17 0017.
 *
 */

public class ListActivity4 extends BaseActivity implements ViewPager.OnPageChangeListener {
    private ViewPager viewPager;
    private RadioGroup rgChannel = null;
    private HorizontalScrollView hvChannel;
    private PageFragmentAdapter adapter = null;
    private List<Fragment> fragmentList = new ArrayList<>();
//    private List<Fragment> singleFragment = new ArrayList<>();
    private List<Channel> selectedChannel = new ArrayList<>();
    public CommonToolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_avtivity4);
        ButterKnife.bind(this);
        CloseActivityClass.activityList.add(this);
        Log.e(TAG, "进入Tab");
        initView();
    }



    public void initView() {

        mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);
        mToolbar.setBackgroundColor(getResources().getColor(topBarColor));
        //左侧返回按钮
        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //右键
        mToolbar.setRightButtonIcon(getResources().getDrawable(R.mipmap.often_more)); //右侧pop

        getIntentData();
        initViewPager();
        initTab();//动态产生RadioButton

        rgChannel.check(0);
    }





    private List<Map<String, Object>> childList = new ArrayList<>();
    private Map<String, Object> itemMap;

    private void getIntentData() {
        itemMap = new HashMap<>();
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


        String titleName = String.valueOf(itemMap.get("menuName"));//获取名称


//        String mainId=intent.getStringExtra("mainId");//第二个获取的参数
//        Log.e("TAG", "tab页中的mainId："+mainId);
        mToolbar.setTitle(titleName);
        if ("office Hour".equals(titleName)){
            Constant.isOfficeHour = true;
        }else {
            Constant.isOfficeHour = false;
        }

//        String childTab = intent.getStringExtra("childTab");//第三个获取的参数
//        List<Map<String, Object>> childTabList = JSON.parseObject(childTab,
//                new TypeReference<List<Map<String, Object>>>() {
//                });
//
//        for (int i = 0; i < childList.size(); i++) {
//            String name = String.valueOf(childList.get(i).get("menuName"));
//            String tableId = String.valueOf(childList.get(i).get("tableId"));
//            String pageId = String.valueOf(childList.get(i).get("pageId"));
//            selectedChannel.add(new Channel("", name, 0, tableId, pageId, null));
//
//        }


        Log.e(TAG, "tab中获取完传递数据");
    }


    private void initTab() {
        for (int i = 0; i < selectedChannel.size(); i++) {
            RadioButton rb = (RadioButton) LayoutInflater.from(this).
                    inflate(R.layout.tab_rb, null);
            rb.setId(i);
            rb.setText(selectedChannel.get(i).getName());
            RadioGroup.LayoutParams params = new
                    RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT);
            rgChannel.addView(rb, params);
        }
        Log.e(TAG, "初始化Tab完毕");
    }

    private static final String TAG = "ListActivity4";
    private void initViewPager() {

        Log.e(TAG, "initViewPager: childList.size() "+childList.size());
        if (childList.size() > 0) {
            for (int i=0;i<childList.size();i++) {
                String name=childList.get(i).get("menuName") + "";
                selectedChannel.add(new Channel("", name, 0, null, null, null));
                String fragmentTableId = childList.get(i).get("tableId") + "";
                String fragmentPageId = childList.get(i).get("pageId") + "";
                Map<String,String> paramsMap = new HashMap<>();
                paramsMap.put(tableId, fragmentTableId);
                paramsMap.put(pageId, fragmentPageId);
                paramsMap.put(Constant.timeName, "-1");
                String listFragmentData=JSON.toJSONString(paramsMap);
                Bundle listBundle = new Bundle();
                listBundle.putString("listFragmentData", listFragmentData);


                String menuNamePanDuan=String.valueOf(childList.get(i).get("menuName"));
                Log.e(TAG, "initViewPager: StuPra.studentProId "+StuPra.studentProId);
                if (menuNamePanDuan.contains(getString(R.string.grow_path))&& StuPra.studentProId.equals("57159822f07e75084cb8a1fe")) {
                    Fragment stageTestFragment = new StageTestFragment();
                    stageTestFragment.setArguments(listBundle);
                    fragmentList.add(stageTestFragment);
//                    transaction.add(R.id.fragment_container,stageTestFragment);
                    Log.e(TAG, "refreshPage: 学员端走定制化阶段测评页面");

                }else if (menuNamePanDuan.contains(getString(R.string.teaching_log))&& StuPra.studentProId.equals("57159822f07e75084cb8a1fe")) {

                    Fragment courseHpsFragment = new CourseHpsFragment();
                    courseHpsFragment.setArguments(listBundle);
                    fragmentList.add(courseHpsFragment);
//                    transaction.add(R.id.fragment_container, courseHpsFragment);
                    Log.e(TAG, "refreshPage: 学员端走定制化课堂内容（一对一教学日志）页面");
//评价列表有数据之后在空串后加数字，共2个地方需要修改 //Log.e("评价列表：",)
                }else if(menuNamePanDuan.contains(getString(R.string.student_evaluation))&& StuPra.studentProId.equals("57159822f07e75084cb8a1fe")){
                    Fragment courseRatingBarFragment = new CourseRatingBarFragment();
                    courseRatingBarFragment.setArguments(listBundle);
                    fragmentList.add(courseRatingBarFragment);
                    Log.e(TAG, "refreshPage: 学员端走定制化评价列表展示页面");
                }else if(menuNamePanDuan.contains(getString(R.string.stu_course_teacher))&& StuPra.studentProId.equals("57159822f07e75084cb8a1fe")){
                    Fragment stuClassTchFragment = new StuClassTchFragment();
                    stuClassTchFragment.setArguments(listBundle);
                    fragmentList.add(stuClassTchFragment);
                    Log.e(TAG, "refreshPage: 学员端走学员课程教师列表展示页面");
                }else {
                    Fragment listFragment = new ListFragment();
                    listFragment.setArguments(listBundle);
                    fragmentList.add(listFragment);
                }
            }

            rgChannel = (RadioGroup) super.findViewById(R.id.rgChannel);
            viewPager = (ViewPager) super.findViewById(R.id.vpNewsList);
            hvChannel = (HorizontalScrollView) super.findViewById(R.id.hvChannel);
            hvChannel.setVisibility(View.VISIBLE);
            rgChannel.setOnCheckedChangeListener(
                    new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group,
                                                     int checkedId) {
                            viewPager.setCurrentItem(checkedId);
                        }
                    });
            adapter = new PageFragmentAdapter(super.getSupportFragmentManager(), fragmentList);
            viewPager.setAdapter(adapter);
            viewPager.setOffscreenPageLimit(20);//fragment数量大于21个才会销毁第一个fragment

        }else{
            String fragmentTableId= itemMap.get("tableId") + "";
            String fragmentPageId = itemMap.get("pageId") + "";
            String titleName = itemMap.get("menuName") + "";
            selectedChannel.add(new Channel("", titleName, 0, null, null, null));
            Map<String,String> paramsMap = new HashMap<>();
            paramsMap.put(tableId, fragmentTableId);
            paramsMap.put(pageId, fragmentPageId);
            paramsMap.put(Constant.timeName, "-1");
            String listFragmentData=JSON.toJSONString(paramsMap);
            Bundle listBundle = new Bundle();
            listBundle.putString("listFragmentData", listFragmentData);

            Fragment xFragment;

            if (titleName.contains(getString(R.string.grow_path))&& StuPra.studentProId.equals("57159822f07e75084cb8a1fe")) {
                xFragment = new StageTestFragment();
            }else if (titleName.contains(getString(R.string.teaching_log))&& StuPra.studentProId.equals("57159822f07e75084cb8a1fe")) {
                xFragment = new CourseHpsFragment();
            }else if(titleName.contains(getString(R.string.student_evaluation))&& StuPra.studentProId.equals("57159822f07e75084cb8a1fe")){
                xFragment = new CourseRatingBarFragment();
            }else if(titleName.contains(getString(R.string.stu_course_teacher))&& StuPra.studentProId.equals("57159822f07e75084cb8a1fe")){
                xFragment = new StuClassTchFragment();
            }else if(titleName.equals("Class Amount")){
                xFragment = new TeaachAmountFragment();
                Log.e("kl","kl");
            }else {
                xFragment = new ListFragment();
                Log.e(TAG, "initViewPager: ListFragment ");
            }
            xFragment.setArguments(listBundle);
            fragmentList.clear();
            fragmentList.add(xFragment);
            rgChannel = (RadioGroup) super.findViewById(R.id.rgChannel);
            viewPager = (ViewPager) super.findViewById(R.id.vpNewsList);
            hvChannel = (HorizontalScrollView) super.findViewById(R.id.hvChannel);
            hvChannel.setVisibility(View.GONE);


//            viewPager.setOnPageChangeListener(this);
            adapter = new PageFragmentAdapter(super.getSupportFragmentManager(), fragmentList);
            viewPager.setAdapter(adapter);
            viewPager.setOffscreenPageLimit(20);//fragment数量大于21个才会销毁第一个fragment
        }
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTab(position);
                fragmentList.get(position).onResume();
                Log.e(TAG, "onPageScrolled: zhixing");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 滑动ViewPager时调整ScroollView的位置以便显示按钮
     *
     * @param idx
     */
    private void setTab(int idx) {
        RadioButton rb = (RadioButton) rgChannel.getChildAt(idx);
        rb.setChecked(true);
        int left = rb.getLeft();
        int width = rb.getMeasuredWidth();
        DisplayMetrics metrics = new DisplayMetrics();
        super.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int len = left + width / 2 - screenWidth / 2;
        hvChannel.smoothScrollTo(len, 0);//滑动ScroollView
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int position) {
        // TODO Auto-generated method stub
        setTab(position);
    }

    private PopupWindow toolListPop;
    public void buttonList() {
        try {
            if (toolListPop != null && toolListPop.isShowing()) {
                toolListPop.dismiss();
            } else {
                final View popInflateView = getLayoutInflater().inflate(
                        R.layout.activity_list_buttonlist, null);
                ListView toolListPopView = (ListView) popInflateView
                        .findViewById(R.id.buttonList);
                TextView tv_dismiss = (TextView) popInflateView.findViewById(R.id.tv_dismiss);
                tv_dismiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toolListPop.dismiss();
                    }
                });
                final SimpleAdapter adapter = new SimpleAdapter(
                        this,
                        buttonSet,
                        R.layout.activity_list_buttonlist_item,
                        new String[]{"buttonName"},
                        new int[]{R.id.listItem});
                toolListPopView.setAdapter(adapter);
                // 点击listview中item的处理
                toolListPopView
                        .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> arg0,
                                                    View arg1, int arg2, long arg3) {
                                toPage(arg2);
                                // 隐藏弹出窗口
                                if (toolListPop != null && toolListPop.isShowing()) {
                                    toolListPop.dismiss();
                                }
                            }
                        });
                initPopWindowDropdown(popInflateView);
            }
        } catch (Exception e) {
            Toast.makeText(ListActivity4.this, getString(R.string.no_button_data), Toast.LENGTH_SHORT).show();
        }
    }

    public void toPage(int position) {
        int buttonType = (int) buttonSet.get(position).get("buttonType");
        Map<String, Object> buttonSetItem = buttonSet.get(position);

        switch (buttonType) {
            case 0://添加页面
                Log.e(TAG, "toPage: buttonType"+"添加页面");
                Intent intent = new Intent(ListActivity4.this, OperateDataActivity.class);
                Log.e("buttonSetItem=",JSON.toJSONString(buttonSetItem));
                intent.putExtra("itemSet", JSON.toJSONString(buttonSetItem));
                startActivityForResult(intent, 5);
                break;
            case 3://批量删除操作
//              listAdapter.flag = true;
//              listAdapter.notifyDataSetChanged();
//              setGone();
                break;
        }
    }

    public void initPopWindowDropdown(View view) {
        //内容，高度，宽度
        toolListPop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //动画效果
        toolListPop.setAnimationStyle(R.style.PopupWindowAnimation);
        //菜单背景色
        ColorDrawable dw = new ColorDrawable(0xffffffff);
        toolListPop.setBackgroundDrawable(dw);
        //显示位置
        toolListPop.showAtLocation(getLayoutInflater().inflate(R.layout.activity_list_avtivity2, null), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        //设置背景半透明
        backgroundAlpha(0.7f);
        //关闭事件
        toolListPop.setOnDismissListener(new ListActivity4.popupDismissListener());
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /*if( popupWindow!=null && popupWindow.isShowing()){
                    popupWindow.dismiss();
                    popupWindow=null;
                }*/
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
                return false;
            }
        });
    }
    /**
     * 设置添加屏幕的背景透明度
     */
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     *
     */
    class popupDismissListener implements PopupWindow.OnDismissListener{

        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }

    }
}
