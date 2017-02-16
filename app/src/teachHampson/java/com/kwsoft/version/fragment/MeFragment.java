package com.kwsoft.version.fragment;


import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.facebook.drawee.view.SimpleDraweeView;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.loadDialog.LoadingDialog;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.utils.Utils;
import com.kwsoft.version.Common.AppConfig;
import com.kwsoft.version.Common.DataCleanManager;
import com.kwsoft.version.Common.FileUtil;
import com.kwsoft.version.Common.MethodsCompat;
import com.kwsoft.version.FeedbackActivity;
import com.kwsoft.version.ResetPwdActivity;
import com.kwsoft.version.StuInfoActivity;
import com.kwsoft.version.StuLoginActivity;
import com.pgyersdk.update.PgyUpdateManager;
import com.zhy.http.okhttp.OkHttpUtils;

import org.kymjs.kjframe.Core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import me.iwf.photopicker.PhotoPicker;
import okhttp3.Call;

import static android.app.Activity.RESULT_OK;
import static com.kwsoft.kehuhua.config.Constant.pictureUrl;
import static com.kwsoft.kehuhua.config.Constant.sysUrl;
import static com.kwsoft.kehuhua.config.Constant.tableId;

/**
 * Created by Administrator on 2016/9/6 0006.
 */
public class MeFragment extends Fragment implements View.OnClickListener {
    @Bind(R.id.tv_clean_cache)
    TextView tvCleanCache;
    @Bind(R.id.stu_name)
    TextView stuName;
    @Bind(R.id.stu_phone)
    TextView stuPhone;
    @Bind(R.id.stu_school_area)
    TextView stuSchoolArea;
    @Bind(R.id.stu_version)
    TextView stuVersion;
    @Bind(R.id.stu_head_image)
    SimpleDraweeView stuHeadImage;

    private ArrayList<String> imgPaths = new ArrayList<>();
    public String valueCode;
    public static final String action = "com.kwsoft.version.fragment.mefragaction";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        ButterKnife.bind(this, view);
        stuHeadImage.setOnClickListener(this);
        initData();
        return view;
    }

    private static final String TAG = "MeFragment";

    @Override
    public void onResume() {
        super.onResume();
        caculateCacheSize();
    }

    public void initData() {
        //    tvCleanCache.setText(getCache());
        stuName.setText(Constant.loginName);
        stuPhone.setText(Constant.USERNAME_ALL);
//        if (Constant.teaMongoId != null && Constant.teaMongoId.length() > 0) {
        Uri uri = Uri.parse(Constant.sysUrl + Constant.downLoadFileStr + Constant.teaMongoId);
        stuHeadImage.setImageURI(uri);
//        }else {
//            stuHeadImage.setBackground(R.drawable.ic_bg);
//        }


        // stuSchoolArea.setText("北京校区");
        try {
            //开始获取版本号
            String stuVersionCode = "v " + Utils.getVersionName(getActivity());
            stuVersion.setText(stuVersionCode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bundle meBundle = getArguments();
        String meStr = meBundle.getString("hideMenuList");
        String feedbackInfoListstr = meBundle.getString("feedbackInfoList");

        List<Map<String, Object>> meListMap = new ArrayList<>();
        Log.e(TAG, "initData: " + meStr);
        getMeTableId(meStr, meListMap);

        List<Map<String, Object>> feedbackListMap = new ArrayList<>();
        Log.e(TAG, "feedbackInfoListstr: " + feedbackInfoListstr);
//        if (feedbackInfoListstr != null) {
//            try {
//                feedbackListMap = JSON.parseObject(feedbackInfoListstr,
//                        new TypeReference<List<Map<String, Object>>>() {
//                        });
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if (feedbackListMap != null && feedbackListMap.size() > 0) {
//                for (int i = 0; i < feedbackListMap.size(); i++) {
//                    Map<String, Object> map = feedbackListMap.get(i);
//                    String menuName = map.get("menuName").toString();
//                    if (menuName.contains("反馈信息")) {
//                        Constant.teachBackPAGEID = map.get("pageId").toString();
//                        Constant.teachBackTABLEID = map.get("tableId").toString();
//                        Log.e("backtableid", Constant.teachBackPAGEID + "/" + Constant.teachBackTABLEID);
//                        break;
//                    }
//                }
//                requestSet();
//            } else {
//             //   Toast.makeText(getActivity(), "无菜单数据", Toast.LENGTH_SHORT).show();
//            }
        //   Log.e("TAG", "获得学员端菜单数据：" + meStr);

        // }
    }

    private void getMeTableId(String meStr, List<Map<String, Object>> meListMap) {
        Log.e(TAG, "getMeTableId: meListMap " + meListMap.toString());
        if (meStr != null) {
            try {
                meListMap = JSON.parseObject(meStr,
                        new TypeReference<List<Map<String, Object>>>() {
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (meListMap != null && meListMap.size() > 0) {
                for (int i = 0; i < meListMap.size(); i++) {
                    Map<String, Object> map = meListMap.get(i);
                    String menuName = map.get("menuName").toString();
                    if (menuName.contains("个人资料")) {
                        Constant.teachPerPAGEID = map.get("pageId").toString();
                        Constant.teachPerTABLEID = map.get("tableId").toString();
                        Log.e("pagetable", Constant.teachPerPAGEID + "/" + Constant.teachPerTABLEID);
                        requestSet();
                        break;
                    } else {
                        stuSchoolArea.setText(getResources().getString(R.string.no_school));
                    }
//                    else if (menuName.contains("反馈信息")){
//                        Constant.teachBackPAGEID = map.get("pageId").toString();
//                        Constant.teachBackTABLEID = map.get("tableId").toString();
//                    }
                }


            } else {
                // Toast.makeText(getActivity(), "无菜单数据", Toast.LENGTH_SHORT).show();
                stuSchoolArea.setText(getResources().getString(R.string.no_school));
            }
            Log.e("TAG", "获得学员端菜单数据：" + meStr);

        } else {
            stuSchoolArea.setText(getResources().getString(R.string.no_school));
        }
//        if (!Constant.teachPerTABLEID.equals("")&&!Constant.teachPerPAGEID.equals("")) {
//        getData();
//        }else{
//            Toast.makeText(getActivity(), "缺少参数", Toast.LENGTH_SHORT).show();
//        }
    }

    //    private Map<String, String> paramsMap=;
    @SuppressWarnings("unchecked")
    public void getData() {
        if (((BaseActivity) getActivity()).hasInternetConnected()) {
            //地址
            String volleyUrl = Constant.sysUrl + Constant.requestListSet;
            Log.e("TAG", "列表请求地址：" + volleyUrl);
            //参数
            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put(Constant.tableId, Constant.teachPerTABLEID);
            paramsMap.put(Constant.pageId, Constant.teachPerPAGEID);
            paramsMap.put("sessionId", Constant.sessionId);
            Log.e(TAG, "getData: paramsMap" + paramsMap.toString());
            //请求
            OkHttpUtils
                    .post()
                    .params(paramsMap)
                    .url(volleyUrl)
                    .build()
                    .execute(new EdusStringCallback(getActivity()) {
                        @Override
                        public void onError(Call call, Exception e, int id) {

                            Log.e(TAG, "onError: Call  " + call + "  id  " + id);
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.e(TAG, "onResponse: response " + response);
                            getDataId(response);
                        }
                    });
        } else {
            ((BaseActivity) getActivity()).dialog.dismiss();
            Toast.makeText(getActivity(), R.string.please_connect_net, Toast.LENGTH_SHORT).show();

        }
    }

    String dataId;

    private void getDataId(String response) {
        Log.e(TAG, "getDataId: response" + response);
        try {
            Map<String, Object> setMap = JSON.parseObject(response,
                    new TypeReference<Map<String, Object>>() {
                    });
            List<Map<String, Object>> mapList = (List<Map<String, Object>>) setMap.get("dataList");
            dataId = String.valueOf(mapList.get(0).get("T_" + "2" + "_0"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取个人信息、主要校区
     */
    public void requestSet() {

        final String volleyUrl = Constant.sysUrl + Constant.requestListSet;
        Log.e("TAG", "学员端请求个人信息地址：" + volleyUrl);
//参数
        Map<String, String> paramsMap = new HashMap<>();
//                paramsMap.put(tableId, StuPra.stuInfoTableId);
//                paramsMap.put(Constant.pageId, StuPra.stuInfoPageId);
        paramsMap.put(tableId, Constant.teachPerTABLEID);
        paramsMap.put(Constant.pageId, Constant.teachPerPAGEID);
        paramsMap.put("sessionId", Constant.sessionId);
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
                        ((BaseActivity) getActivity()).dialog.dismiss();
                        Log.e(TAG, "onError: Call  " + call + "  id  " + id);
                        stuSchoolArea.setText(getResources().getString(R.string.no_school));
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e(TAG + "me", "onResponse: " + "  id  " + response);
                        setStore(response);
                    }
                });
    }

    @SuppressWarnings("unchecked")
    private void setStore(String jsonData) {
        String jsonData1 = jsonData.replaceAll("00:00:00", "");
        Log.e("TAG", "jsonData1 " + jsonData1);
        Map<String, Object> stuInfoMap = null;
        try {
            stuInfoMap = Utils.str2map(jsonData1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Map<String, Object>> dataList;

        try {
            assert stuInfoMap != null;
            dataList = (List<Map<String, Object>>) stuInfoMap.get("dataList");

            Map<String, Object> pageSetMap = (Map<String, Object>) stuInfoMap.get("pageSet");
            List<Map<String, Object>> pageSet = (List<Map<String, Object>>) pageSetMap.get("fieldSet");
            Log.e("pageSet", pageSet.toString());
            String fieldCnName, fieldAliasName = "";
            for (int i = 0; i < pageSet.size(); i++) {
                Map<String, Object> map = pageSet.get(i);
                fieldCnName = map.get("fieldCnName") + "";
                if (fieldCnName.contains("Teaching Center")) {
                    fieldAliasName = map.get("fieldAliasName") + "";
                    break;
                }
            }
            Map<String, Object> map = dataList.get(0);
            if ((fieldAliasName.length() > 0) && (map.containsKey(fieldAliasName))) {
                String school = (String) map.get(fieldAliasName);
                stuSchoolArea.setText(school);
            } else {
                stuSchoolArea.setText(getString(R.string.no_school));
            }
        } catch (Exception e) {
            //e.printStackTrace();
            stuSchoolArea.setText(getString(R.string.no_school));
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.stu_head_image, R.id.stu_log_out, R.id.stu_resetPwd, R.id.stu_info_data, R.id.ll_stu_clear_cache, R.id.ll_stu_feedback, R.id.ll_stu_version_check})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stu_head_image:
                PermissionGen.needPermission(MeFragment.this, 105,
                        new String[]{
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        }
                );
                break;
            case R.id.stu_log_out:
                Intent intentLogout = new Intent(getActivity(), StuLoginActivity.class);
                startActivity(intentLogout);
                getActivity().finish();
                break;
            case R.id.stu_resetPwd:
                Intent intent = new Intent(getActivity(), ResetPwdActivity.class);
                startActivity(intent);
                break;
            case R.id.stu_info_data:
                if (!Constant.teachPerTABLEID.equals("") && !Constant.teachPerPAGEID.equals("")) {
                    Intent intentStuInfo = new Intent(getActivity(), StuInfoActivity.class);
                    startActivity(intentStuInfo);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.no_personal_info, Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.ll_stu_clear_cache:
                clearAppCache();
                break;
            case R.id.ll_stu_feedback:
                Intent intent1 = new Intent(getActivity(), FeedbackActivity.class);
                startActivity(intent1);
                break;

            case R.id.ll_stu_version_check:
                PgyUpdateManager.register(getActivity());

                break;
            default:
                break;
        }
    }

    private final int CLEAN_SUC = 1001;
    private final int CLEAN_FAIL = 1002;


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = 105)
    public void doSomething() {
     /*打开权限成功时执行的功能  */
        PhotoPicker.builder()
                .setPhotoCount(1)
                .setShowCamera(true)
                .setSelected(imgPaths)
                .setShowGif(true)
                .setPreviewEnabled(true)
                .start(getActivity(), MeFragment.this, PhotoPicker.REQUEST_CODE);
    }

    @PermissionFail(requestCode = 105)
    public void doFailSomething() {
        /*打开权限失败后，给出的提示*/
        Toast.makeText(getActivity(), R.string.open_the_camera_error_please_check_permissions, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                imgPaths.clear();
                ((BaseActivity) getActivity()).dialog.show();
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                imgPaths.addAll(photos);
                Log.e(TAG, "onActivityResult: " + imgPaths.get(0));
                uploadMethod(imgPaths.get(0));
            }
        }
    }

    public void uploadMethod(String path) {

        String url = sysUrl + pictureUrl;
        File file = new File(path);
//        if (files.size() > 0) {
        OkHttpUtils.post()//
                .addFile("myFile", file.getName(), file)
                .url(url)
                .build()
                .execute(new EdusStringCallback(getActivity()) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ErrorToast.errorToast(mContext, e);
//                        waveProgress.setVisibility(View.GONE);
                        ((BaseActivity) getActivity()).dialog.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            String[] valueTemp1 = response.split(":");
                            valueCode = valueTemp1[1];
                            Log.e("MyfrgupLoad", response);
                            Toast.makeText(getActivity(), R.string.upload_success, Toast.LENGTH_SHORT).show();
                            String url = Constant.sysUrl + Constant.teachHeadUpdate;
                            Map<String, String> paramsMap = new HashMap<>();
                            paramsMap.put("t0_au_2_4171", dataId);
                            paramsMap.put("t0_au_2_4171_3569", valueCode);
                            paramsMap.put("ifCleanInnerData", "undefined");
                            paramsMap.put("ifRecordingLog", "0");
                            paramsMap.put(Constant.tableId, "2");
                            paramsMap.put(Constant.pageId, "4171");
                            paramsMap.put("sessionId", Constant.sessionId);
                            Log.e("up2", paramsMap.toString());

                            OkHttpUtils
                                    .post()
                                    .params(paramsMap)
                                    .url(url)
                                    .build()
                                    .execute(new EdusStringCallback(getActivity()) {
                                        @Override
                                        public void onError(Call call, Exception e, int id) {
                                            ErrorToast.errorToast(mContext, e);
                                            Log.e(TAG, "onError: Call  " + call + "  id  " + id);
                                            ((BaseActivity) getActivity()).dialog.dismiss();
                                        }

                                        @Override
                                        public void onResponse(String response, int id) {
                                            Log.e(TAG, "onResponse: " + response);
                                            if ("1".equals(response.trim())) {
                                                Log.e(TAG, "onResponse: " + "sccg");  //  setStore(response);
                                                ((BaseActivity) getActivity()).dialog.dismiss();
                                                Toast.makeText(getActivity(), R.string.commit_success, Toast.LENGTH_SHORT).show();
                                                final String uriStr = "file://" + imgPaths.get(0);
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
//                                                        File file = new File(imgPaths.get(0));
//                                                        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
//                                                        Drawable drawable = new BitmapDrawable(bitmap);
//                                                        // stuHeadImage.setImageBitmap(bitmap);
//                                                        stuHeadImage.setBackground(drawable);
                                                        Uri uri = Uri.parse(uriStr);
                                                        stuHeadImage.setImageURI(uri);
                                                    }
                                                });

                                                //更新第一个activity的ui
                                                Intent intent = new Intent(action);
                                                intent.putExtra("uriStr", uriStr);
                                                getActivity().sendBroadcast(intent);

                                            } else {
                                                Toast.makeText(getActivity(), R.string.no_personal_info, Toast.LENGTH_SHORT).show();
                                                ((BaseActivity) getActivity()).dialog.dismiss();
                                            }
                                        }
                                    });

                        } catch (Exception e) {
                            e.printStackTrace();
                            ((BaseActivity) getActivity()).dialog.dismiss();
                        }
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {

                    }
                });
    }

    /**
     * 计算缓存的大小
     */
    private void caculateCacheSize() {
        long fileSize = 0;
        String cacheSize = "0KB";
        File filesDir = getActivity().getFilesDir();
        File cacheDir = getActivity().getCacheDir();

        fileSize += FileUtil.getDirSize(filesDir);
        fileSize += FileUtil.getDirSize(cacheDir);
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            File externalCacheDir = MethodsCompat
                    .getExternalCacheDir(getActivity());
            fileSize += FileUtil.getDirSize(externalCacheDir);
            fileSize += FileUtil.getDirSize(new File(
                    org.kymjs.kjframe.utils.FileUtils.getSDCardPath()
                            + File.separator + "KJLibrary/cache"));
        }
        if (fileSize > 0)
            cacheSize = FileUtil.formatFileSize(fileSize);
        Log.e("cachesize=", cacheSize);
        tvCleanCache.setText(cacheSize);
    }

    /**
     * 清除app缓存
     *
     * @param
     */
    private LoadingDialog dialogClear;

    public void clearAppCache() {
        dialogClear = new LoadingDialog(getActivity(), getString(R.string.cleaning));
        dialogClear.show();
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    myclearaAppCache();
                    msg.what = CLEAN_SUC;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = CLEAN_FAIL;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 清除app缓存
     */
    public void myclearaAppCache() {
        DataCleanManager.cleanDatabases(getActivity());
        // 清除数据缓存
        DataCleanManager.cleanInternalCache(getActivity());
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            DataCleanManager.cleanCustomCache(MethodsCompat.getExternalCacheDir(getActivity()).getPath());
        }
        // 清除编辑器保存的临时内容
        Properties props = getProperties();
        for (Object key : props.keySet()) {
            String _key = key.toString();
            if (_key.startsWith("temp"))
                removeProperty(_key);
        }
        Core.getKJBitmap().cleanCache();
    }

    public void removeProperty(String... key) {
        AppConfig.getAppConfig(getActivity()).remove(key);
    }

    /**
     * 清除保存的缓存
     */
    public Properties getProperties() {
        return AppConfig.getAppConfig(getActivity()).get();
    }

    public static boolean isMethodsCompat(int VersionCode) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        return currentVersion >= VersionCode;
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case CLEAN_FAIL:
                    dialogClear.dismiss();
                    Toast.makeText(getActivity(), R.string.clear_failure, Toast.LENGTH_SHORT).show();
                    break;
                case CLEAN_SUC:
                    dialogClear.dismiss();
                    tvCleanCache.setText("0KB");
                    Toast.makeText(getActivity(), R.string.clear_success, Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    };


    public String getCache() {
        String cache = "";
        try {
//           cache = DataCleanManager.getVolleyCache(getActivity());
            cache = DataCleanManager.getTotalCacheSize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cache;
    }

    public void clearCache() {
        try {
            DataCleanManager.cleanExternalCache(getActivity());

            DataCleanManager.cleanInternalCache(getActivity());


            // cache = DataCleanManager.getTotalCacheSize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
