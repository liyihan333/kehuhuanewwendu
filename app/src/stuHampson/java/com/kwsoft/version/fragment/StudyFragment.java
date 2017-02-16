package com.kwsoft.version.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.kwsoft.kehuhua.adcustom.ListActivity4;
import com.kwsoft.kehuhua.adcustom.MessagAlertActivity;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.utils.DataProcess;
import com.kwsoft.kehuhua.zxing.TestScanActivity;
import com.kwsoft.version.ResetPwdActivity;
import com.kwsoft.version.StuInfoActivity;
import com.kwsoft.version.StuLoginActivity;
import com.kwsoft.version.StuMainActivity;
import com.kwsoft.version.StuPra;
import com.kwsoft.version.androidRomType.AndtoidRomUtil;
import com.kwsoft.version.view.StudyGridView;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2016/9/6 0006.
 */
public class StudyFragment extends Fragment implements View.OnClickListener {

    private TextView stuName;
    //PullToRefreshGridView homeGridView;
    private StudyGridView homeGridView;
    private List<Map<String, Object>> parentList = new ArrayList<>();
    private SimpleAdapter simpleAdapter = null;

    private int[] imgs2 = {R.mipmap.k1, R.mipmap.k2,
            R.mipmap.k3, R.mipmap.k4, R.mipmap.k5,
            R.mipmap.k6, R.mipmap.k7, R.mipmap.k8,
            R.mipmap.k9, R.mipmap.k10, R.mipmap.k11,
            R.mipmap.k12, R.mipmap.k13, R.mipmap.k14,
            R.mipmap.k15, R.mipmap.k16, R.mipmap.k17,
            R.mipmap.k18, R.mipmap.k19, R.mipmap.k20};
    private GridView gridView;
    private List<Map<String, Object>> menuListAll = new ArrayList<>();
    private List<Map<String, Object>> menuListMap = new ArrayList<>();
    private PullToRefreshScrollView pull_refresh_scrollview;
    private SharedPreferences sPreferences;
    private Boolean isLogin = false;
    public String arrStr, menuStr, stuUrl;
    public Bundle arrBundle;
   //private ImageView iv_head;
    private SimpleDraweeView stuHeadImage;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study, container, false);
        initView(view);
        stuUrl = Constant.sysUrl + Constant.projectLoginUrl;
        ButterKnife.bind(this, view);
        return view;
    }

    public void initView(View view) {
        stuName = (TextView) view.findViewById(R.id.stu_name);
       // iv_head = (ImageView) view.findViewById(R.id.iv_head);
        stuHeadImage = (SimpleDraweeView) view.findViewById(R.id.stu_head_image);
        //设置首页头像
        String urlStr = Constant.sysUrl + Constant.downLoadFileStr + Constant.teaMongoId;
        updateImage(urlStr);
       // Bitmap bitmap= BitmapFactory.decodeFile(StuPra.hpsStuHeadPath);
//       Drawable drawable= getResources().getDrawable(R.mipmap.icon);
//        BitmapDrawable bd = (BitmapDrawable) drawable;
//        Bitmap bitmap = bd.getBitmap();
//        iv_head.setImageBitmap(bitmap);

        try {
            String username = Constant.loginName;
            stuName.setText(username);
        } catch (Exception e) {
            e.printStackTrace();
        }


        RollPagerView mRollViewPager = (RollPagerView) view.findViewById(R.id.roll_view_pager);

        //设置播放时间间隔
        mRollViewPager.setPlayDelay(4000);
        //设置透明度
        mRollViewPager.setAnimationDurtion(1500);
        //设置适配器
        mRollViewPager.setAdapter(new TestNormalAdapter());


        //隐藏指示器
        //mRollViewPager.setHintView(new IconHintView(this, R.drawable.point_focus, R.drawable.point_normal));
        mRollViewPager.setHintView(new ColorPointHintView(getActivity(), getResources().getColor(R.color.text6), getResources().getColor(R.color.text5)));
        mRollViewPager.setHintPadding(0, 0, 0, 40);
        mRollViewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        homeGridView = (StudyGridView) view.findViewById(R.id.home_grid);
        homeGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> itemData = new HashMap<>();
                itemData.put("tableId", String.valueOf(parentList.get(position).get("tableId")));
                itemData.put("pageId", String.valueOf(parentList.get(position).get("penetratePageId")));
                itemData.put("menuName", String.valueOf(parentList.get(position).get("cnName")));

                Constant.stu_index = String.valueOf(parentList.get(position).get("ctType"));

                Constant.stu_homeSetId = String.valueOf(parentList.get(position).get("SourceDataId"));
                try {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), ListActivity4.class);
                    intent.putExtra("itemData", JSON.toJSONString(itemData));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        gridView = (GridView) view.findViewById(R.id.gridView);
        gridView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        pull_refresh_scrollview = (PullToRefreshScrollView) view.findViewById(R.id.pull_refresh_scrollview);
        pull_refresh_scrollview.getLoadingLayoutProxy().setLastUpdatedLabel(getResources().getString(R.string.data_refresh));
        pull_refresh_scrollview.getLoadingLayoutProxy().setPullLabel(getResources().getString(R.string.Pull_down_refresh));
        pull_refresh_scrollview.getLoadingLayoutProxy().setRefreshingLabel(getResources().getString(R.string.data_refreshing));
        pull_refresh_scrollview.getLoadingLayoutProxy().setReleaseLabel(getResources().getString(R.string.data_will_refresh));

        //上拉、下拉设定
//        pull_refresh_scrollview.setMode(PullToRefreshBase.Mode.BOTH);
        //上拉监听函数
        pull_refresh_scrollview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                //执行刷新函数

                getLoginData(stuUrl);
            }
        });

        //获取ScrollView布局，此文中用不到
        //mScrollView = mPullRefreshScrollView.getRefreshableView();

        getData();
        // initData();

        //设置广播
        IntentFilter filter = new IntentFilter(MeFragment.action);
        getActivity().registerReceiver(broadcastReceiver, filter);
    }


    public int isResume = 0;

    @Override
    public void onResume() {
        isResume = 1;
        Log.e("isLogin=", isLogin + "");
        super.onResume();
        if (!isLogin) {
            isLogin = arrBundle.getBoolean("isLogin");
            initData();
        } else {
            getLoginData(stuUrl);
        }

    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            updateImage(intent.getExtras().getString("uriStr"));
        }
    };

    public void updateImage(String uriStr) {
        Uri uri = Uri.parse(uriStr);
        stuHeadImage.setImageURI(uri);
    }

    public void initData() {
        parentList = getkanbanData(arrStr);
        if (parentList != null && parentList.size() > 0) {
            setKanbanAdapter(parentList);
            Log.e("isLogin=", isLogin + "");
        }
        //菜单列表中的gridview数据

        if (menuStr != null && menuStr.length() > 0) {
            menuListAll = JSON.parseObject(menuStr,
                    new TypeReference<List<Map<String, Object>>>() {
                    });
            if (menuListAll.size() > 0) {
                //展示菜单
                menuListMap = getMenuListData(menuListAll);
                setMenuAdapter(menuListMap);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if ((simpleAdapter.getCount() == (i + 1))) {
                            // if (i == 7) {
                            StuMainActivity activity = (StuMainActivity) getActivity();
                            activity.fragmentClick();
                        } else {
//                            int menuId = (int) menuListMap.get(i).get("menuId");
//                            toItem(menuId, menuListMap.get(i));
                            DataProcess.toList(getActivity(), menuListMap.get(i));
                        }
                    }
                });
            } else {
                Toast.makeText(getActivity(), "无分类数据", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getData() {
        //设置看板数据
        arrBundle = getArguments();
        arrStr = arrBundle.getString("arrStr");
        menuStr = arrBundle.getString("menuDataMap");
    }

    public void setMenuAdapter(List<Map<String, Object>> menuListMaps) {
        simpleAdapter = new SimpleAdapter(getActivity(), menuListMaps,
                R.layout.fragment_study_gridview_item, new String[]{"image", "menuName"},
                new int[]{R.id.itemImage, R.id.itemName});
        gridView.setAdapter(simpleAdapter);
    }

    public void setKanbanAdapter(List<Map<String, Object>> parentLists) {
        if ((parentLists.size()) % 2 == 1) {
            Map<String, Object> map = new HashMap<>();
            map.put("image", R.color.white);
            map.put("cnName", "");
            map.put("name", "");
            parentLists.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), parentLists,
                R.layout.activity_stu_study_item, new String[]{"image", "cnName", "name"},
                new int[]{R.id.iv_item, R.id.text1, R.id.text2});
        homeGridView.setAdapter(adapter);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getkanbanData(String arrStr) {
        List<Map<String, Object>> parentLists = new ArrayList<>();
        if (arrStr != null && arrStr.length() > 0) {

            try {
                List<Map<String, Object>> listMap = JSON.parseObject(arrStr,
                        new TypeReference<List<Map<String, Object>>>() {
                        });
                String cnName;
                for (int i = 0; i < listMap.size(); i++) {
                    Map<String, Object> map = new HashMap<>();
                    cnName = String.valueOf(listMap.get(i).get("cnName"));
                    map.put("ctType", "3");
                    map.put("cnName", cnName);
                   // int j = i % 4;
                    map.put("image", imgs2[i]);
                    map.put("SourceDataId", listMap.get(i).get("homeSetId") + "_" + listMap.get(i).get("index"));
                    map.put("penetratePageId", listMap.get(i).get("phonePageId"));
                    map.put("tableId", listMap.get(i).get("tableId"));
                    List<Map<String, Object>> listMap1 = (List<Map<String, Object>>) listMap.get(i).get("valueMap");
                    String name = "";
                    if (listMap1.size() > 0) {
                        if (listMap1.get(0) != null && listMap1.get(0).size() > 0) {
                            name = String.valueOf(listMap1.get(0).get("name"));
                        }
                    }
                    map.put("name", name);
                    parentLists.add(map);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return parentLists;
    }

    //获取父类菜单数据，并取不大于7个
    public List<Map<String, Object>> getMenuListData(List<Map<String, Object>> menuListAlls) {
        List<Map<String, Object>> menuListMaps = new ArrayList<>();
        menuListMap = DataProcess.toStuParentList(menuListAlls);
        //大于7个的情况
        if (menuListMap.size() > 7) {
            for (int k = 0; k < 7; k++) {
                menuListMaps.add(menuListMap.get(k));
            }

            Map<String, Object> map = new HashMap<>();
            map.put("menuName", "全部");
            map.put("image", R.drawable.stu_see_all);
            menuListMaps.add(map);
        } else {
            //小于7个的情况
            menuListMaps.addAll(menuListMap);
        }

        Log.e("TAG", "parentList去掉手机端 " + menuListMap.toString());
        return menuListMaps;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                boolean emui = AndtoidRomUtil.isEMUI();
                boolean miui = AndtoidRomUtil.isMIUI();
                boolean flyme = AndtoidRomUtil.isFlyme();

                if (emui) {
                    //华为
//                    PackageManager pm =  getActivity().getPackageManager();
//                    //MediaStore.ACTION_IMAGE_CAPTURE android.permission.RECORD_AUDIO
//                    boolean permission = (PackageManager.PERMISSION_GRANTED ==
//                            pm.checkPermission("MediaStore.ACTION_IMAGE_CAPTURE", "packageName"));
//                    if (permission) {
//                        Intent intent = new Intent(getActivity(), CaptureActivity.class);
//                        startActivityForResult(intent, 1);
//                    } else {
//                        Constant.goHuaWeiSetting(getActivity());
//                    }
                    Intent intent = new Intent(getActivity(), TestScanActivity.class);
                    startActivityForResult(intent, 1);
                } else if (miui) {
                    //小米
                    Intent intent = new Intent(getActivity(), TestScanActivity.class);
                    startActivityForResult(intent, 1);
                } else if (flyme) {
                    //魅族rom
                    Intent intent = new Intent(getActivity(), TestScanActivity.class);
                    startActivityForResult(intent, 1);
                } else {
                    Intent intent = new Intent(getActivity(), TestScanActivity.class);
                    startActivityForResult(intent, 1);
                }
                break;
            case R.id.layout_1:
                Intent intent2 = new Intent(getActivity(), MessagAlertActivity.class);
                startActivity(intent2);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(broadcastReceiver);
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.stu_homepage_info)
    public void onClick() {
        Intent intent = new Intent(getActivity(), StuInfoActivity.class);
        startActivity(intent);
    }

    private class TestNormalAdapter extends StaticPagerAdapter {
        private int[] imgs = {
                R.mipmap.first,
                R.mipmap.first,
                R.mipmap.first,
                R.mipmap.first,
        };


        @Override
        public View getView(ViewGroup container, final int position) {
            ImageView view = new ImageView(container.getContext());
            view.setImageResource(imgs[position]);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position == 0) {
                        Intent intent = new Intent(getActivity(), StuLoginActivity.class);
                        startActivity(intent);
                    } else if (position == 1) {
                        Intent intent = new Intent(getActivity(), ResetPwdActivity.class);
                        startActivity(intent);
                    } else if (position == 2) {
                        Intent intent = new Intent(getActivity(), ResetPwdActivity.class);
                        startActivity(intent);
                    } else if (position == 3) {
                        Intent intent = new Intent(getActivity(), ResetPwdActivity.class);
                        startActivity(intent);
                    }
                }
            });

            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return view;
        }


        @Override
        public int getCount() {
            return imgs.length;
        }
    }

    private static final String TAG = "StudyFragment";

    public void getLoginData(String volleyUrl) {
        if (((BaseActivity) getActivity()).hasInternetConnected()) {

            //参数
            sPreferences = getActivity().getSharedPreferences(Constant.proId, MODE_PRIVATE);
            String nameValue = sPreferences.getString("name", "");
            String pwdValue = sPreferences.getString("pwd", "");
            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put(Constant.USER_NAME, nameValue);
            paramsMap.put(Constant.PASSWORD, pwdValue);
            paramsMap.put(Constant.proIdName, Constant.proId);
            paramsMap.put(Constant.timeName, Constant.menuAlterTime);
            paramsMap.put(Constant.sourceName, Constant.sourceInt);

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
                            pull_refresh_scrollview.onRefreshComplete();

                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.e(TAG, "onResponse: " + "  id  " + id);
                            mainPage(response);
                        }
                    });
        } else {

            pull_refresh_scrollview.onRefreshComplete();
            Toast.makeText(getActivity(), "无网络", Toast.LENGTH_SHORT).show();
        }
    }

    //此方法传递菜单JSON数据
    @SuppressWarnings("unchecked")
    private void mainPage(String menuData) {
        try {
            Map<String, Object> menuMap = JSON.parseObject(menuData,
                    new TypeReference<Map<String, Object>>() {
                    });
            Map<String, Object> loginfo = (Map<String, Object>) menuMap.get("loginInfo");
            Constant.USERID = String.valueOf(loginfo.get("USERID"));
            Constant.sessionId = String.valueOf(loginfo.get("sessionId"));
//            List<Map<String, Object>> menuListMap1 = (List<Map<String, Object>>) menuMap.get("roleFollowList");
//            List<Map<String, Object>> menuListMap2 = (List<Map<String, Object>>) menuMap.get("menuList");
            List<Map<String, Object>> menuListMap1 = null;
            if (menuMap.containsKey("roleFollowList")) {
                menuListMap1 = (List<Map<String, Object>>) menuMap.get("roleFollowList");
                Log.e("menuListMap1", JSON.toJSONString(menuListMap1));
            }

            List<Map<String, Object>> menuListMap2 = null;
            if (menuMap.containsKey("menuList")) {
                menuListMap2 = (List<Map<String, Object>>) menuMap.get("menuList");
                Log.e("menuListMap2", JSON.toJSONString(menuListMap2));
            }
//看板模块数据
            String arrStr = "";
            if (menuListMap1 != null && menuListMap1.size() > 0) {
                arrStr = JSON.toJSONString(menuListMap1);
            }
            parentList.clear();
            parentList = getkanbanData(arrStr);
            if (parentList != null && parentList.size() > 0) {
                setKanbanAdapter(parentList);
            }
            //菜单列表中的gridview数据
            String menuStr = "";
            if (menuListMap2 != null && menuListMap2.size() > 0) {
                menuStr = JSON.toJSONString(menuListMap2);
            }
            if (menuStr != null && menuStr.length() > 0) {
                menuListAll.clear();
                menuListAll = JSON.parseObject(menuStr,
                        new TypeReference<List<Map<String, Object>>>() {
                        });
                if (menuListAll.size() > 0) {
                    //展示菜单
                    menuListMap.clear();
//                    menuListMap =  DataProcess.toStuParentList(menuListAll);
                    menuListMap = getMenuListData(menuListAll);
                    setMenuAdapter(menuListMap);

                } else {
                    Toast.makeText(getActivity(), "无分类数据", Toast.LENGTH_SHORT).show();
                }
                //pull_refresh_scrollview.setMode(PullToRefreshBase.Mode.DISABLED);

                // Call onRefreshComplete when the list has been refreshed.
                //在更新UI后，无需其它Refresh操作，系统会自己加载新的listView
                pull_refresh_scrollview.onRefreshComplete();
                if (isResume == 0) {
                    Toast.makeText(getActivity(), "数据已刷新", Toast.LENGTH_SHORT).show();
                }
            } else {
                pull_refresh_scrollview.onRefreshComplete();
                Log.e("无数据","无数据");
//                if (isResume == 0) {
//                    Toast.makeText(getActivity(), "暂无数据", Toast.LENGTH_SHORT).show();
//                }
            }
            isResume = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}














