package com.kwsoft.version.zxing;
/**
 * Created by Administrator on 2016/12/28 0028.
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.Map;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;
import okhttp3.Call;

public class TestScanActivity extends BaseActivity implements QRCodeView.Delegate {
    private static final String TAG = TestScanActivity.class.getSimpleName();
    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;
    //    public LocationClient mLocationClient = null;
//    public BDLocationListener myListener = new MyLocationListener();
    private QRCodeView mQRCodeView;
//    public LocationClient mLocationClient = null;
//    public BDLocationListener myListener = new MyLocationListener();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_scan);
//        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
//        mLocationClient.registerLocationListener(myListener);    //注册监听函数
//        initLocation();
//        mLocationClient.start();

        mQRCodeView = (ZXingView) findViewById(R.id.zxingview);
        mQRCodeView.setDelegate(this);
        mQRCodeView.startSpot();
    }

    private void initLocation() {
//        LocationClientOption option = new LocationClientOption();
//        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
//        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
//        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
//        int span = 1000;
//        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
//        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
//        option.setOpenGps(true);//可选，默认false,设置是否使用gps
//        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
//        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
//        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
//        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
//        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
//        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
//        mLocationClient.setLocOption(option);
    }

    @Override
    public void initView() {

    }

    //    @Override
//    protected void onStart() {
//        super.onStart();
//        mQRCodeView.startCamera();
//        mQRCodeView.showScanRect();
//    }
//
//    @Override
//    protected void onStop() {
//        mQRCodeView.stopCamera();
//        super.onStop();
//      //  mLocationClient.stop();
//    }
//
//    @Override
//    public void onDestroy() {
//        mQRCodeView.onDestroy();
//        super.onDestroy();
//    }
//
    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        // Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        vibrate();
        Log.e(TAG, "onScanQRCodeSuccess: result= " + result);
        getBook(result);
        // doDistinguish(result);
    }

    public void getBook(String urlStr) {
        if (!hasInternetConnected()) {
            Toast.makeText(this, "当前网络不可用，请检查网络！", Toast.LENGTH_SHORT).show();
        } else {
            urlStr = urlStr + "&stuId=" + Constant.USERID;
            Log.e(TAG, "getBook: urlStr " + urlStr);
            //请求
            OkHttpUtils
                    .post()
                    .url(urlStr)
                    .build()
                    .execute(new EdusStringCallback(TestScanActivity.this) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            ErrorToast.errorToast(mContext, e);
                            dialog.dismiss();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.e(TAG, "onResponse: postLogin1:" + response);
                            Map<String, Object> errorMap = JSON.parseObject(response,
                                    new TypeReference<Map<String, Object>>() {
                                    });
                            Toast.makeText(TestScanActivity.this, String.valueOf(errorMap.get("info")), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
        }
    }
//    public void doDistinguish(String result) {
//        if (result == null || "".equals(result)) {
//            Toast.makeText(TestScanActivity.this, "no data",
//                    Toast.LENGTH_SHORT).show();
//        } else {
//            requestData("http://" + result);//默认经纬度为soho
//        }
//    }
//
//    private void requestData(String volleyUrl) {
//        if (!hasInternetConnected()) {
//            Toast.makeText(this, "当前网络不可用，请检查网络！", Toast.LENGTH_LONG).show();
//            return;
//        }
////        dialog.show();
////        Log.e(TAG, "requestData: 扫码时请求地址1" + volleyUrl);
////        Log.e(TAG, "requestData: 学员ID" + USERID);
////        Log.e(TAG, "requestData: latStr " + latStr);
////        Log.e(TAG, "requestData: longStr " + longStr);
////        String pinJieUrl = "&stuId=" + USERID + "&stuLongitude=" + longStr + "&stuLatitude=" + latStr;
////        //请求
////        OkHttpUtils
////                .get()
////                .url(volleyUrl + pinJieUrl)
////                .build()
////                .execute(new EdusStringCallback(TestScanActivity.this) {
////                    @Override
////                    public void onError(Call call, Exception e, int id) {
////                        ErrorToast.errorToast(mContext, e);
////                        Log.e(TAG, "onError: 请求出错");
////                        Toast.makeText(TestScanActivity.this, "请求服务器出错", Toast.LENGTH_SHORT).show();
////                        dialog.dismiss();
//////                        mQRCodeView.startSpot();
////                    }
////
////                    @Override
////                    public void onResponse(String response, int id) {
////                        setStore(response);
////                    }
////
////
////                });
//    }
//
//
//    //扫码后请求结果onResponse: {"error":0,"errorInfo":"当前时间内不能考勤！"}
//    private void setStore(String response) {
//        dialog.dismiss();
//        Log.e(TAG, "扫码后请求结果onResponse: " + response);
//        if (response.contains("请重新登录")) {
//            Toast.makeText(this, "请重新登录或检查ip地址", Toast.LENGTH_SHORT).show();
////            Log.e(TAG, "打开相机出错");
//            return;
//        }
//
//        Map<String, Object> dataMap = JSON.parseObject(response,
//                new TypeReference<Map<String, Object>>() {
//                });
//        String error = String.valueOf(dataMap.get("error"));
//        if ("1".equals(error)) {
//            Toast.makeText(this, "考勤成功！", Toast.LENGTH_SHORT).show();
//        } else if ("-4".equals(error)) {
//           // Toast.makeText(this, "您当前位置：" + currentAdrStr + ",不在上课区域内，不能考勤！", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, String.valueOf(dataMap.get("errorInfo")), Toast.LENGTH_SHORT).show();
//        }
//        finish();
//    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Toast.makeText(this, R.string.open_the_camera_error_please_check_permissions, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "打开相机出错");
    }

//    public void onClick(View v) {
//        int i = v.getId();
//        if (i == cn.bingoogolapple.qrcode.zxingdemo.R.id.start_spot) {
//            mQRCodeView.startSpot();
//
//        } else if (i == cn.bingoogolapple.qrcode.zxingdemo.R.id.stop_spot) {
//            mQRCodeView.stopSpot();
//
//        } else if (i == cn.bingoogolapple.qrcode.zxingdemo.R.id.start_spot_showrect) {
//            mQRCodeView.startSpotAndShowRect();
//
//        } else if (i == cn.bingoogolapple.qrcode.zxingdemo.R.id.stop_spot_hiddenrect) {
//            mQRCodeView.stopSpotAndHiddenRect();
//
//        } else if (i == cn.bingoogolapple.qrcode.zxingdemo.R.id.show_rect) {
//            mQRCodeView.showScanRect();
//
//        } else if (i == cn.bingoogolapple.qrcode.zxingdemo.R.id.hidden_rect) {
//            mQRCodeView.hiddenScanRect();
//
//        } else if (i == cn.bingoogolapple.qrcode.zxingdemo.R.id.start_preview) {
//            mQRCodeView.startCamera();
//
//        } else if (i == cn.bingoogolapple.qrcode.zxingdemo.R.id.stop_preview) {
//            mQRCodeView.stopCamera();
//
//        } else if (i == cn.bingoogolapple.qrcode.zxingdemo.R.id.open_flashlight) {
//            mQRCodeView.openFlashlight();
//
//        } else if (i == cn.bingoogolapple.qrcode.zxingdemo.R.id.close_flashlight) {
//            mQRCodeView.closeFlashlight();
//
//        } else if (i == cn.bingoogolapple.qrcode.zxingdemo.R.id.scan_barcode) {
//            mQRCodeView.changeToScanBarcodeStyle();
//
//        } else if (i == cn.bingoogolapple.qrcode.zxingdemo.R.id.scan_qrcode) {
//            mQRCodeView.changeToScanQRCodeStyle();
//
//        }
////        else if (i == R.id.choose_qrcde_from_gallery) {/*
////                从相册选取二维码图片，这里为了方便演示，使用的是
////                https://github.com/bingoogolapple/BGAPhotoPicker-Android
////                这个库来从图库中选择二维码图片，这个库不是必须的，你也可以通过自己的方式从图库中选择图片
////                 */
////            startActivityForResult(BGAPhotoPickerActivity.newIntent(this, null, 1, null, false), REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY);
////
////        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: data:" + data);

        mQRCodeView.showScanRect();
    }


////    public class MyLocationListener implements BDLocationListener {
////
////        @Override
////        public void onReceiveLocation(BDLocation location) {
////            //Receive Location
////            StringBuffer sb = new StringBuffer(256);
////            sb.append("time : ");
////            sb.append(location.getTime());
////            sb.append("\nerror code : ");
////            sb.append(location.getLocType());
////            sb.append("\nlatitude : ");
////            sb.append(location.getLatitude());
////            latStr = String.valueOf(location.getLatitude());//在此赋值纬度
////            sb.append("\nlontitude : ");
////            sb.append(location.getLongitude());
////            longStr = String.valueOf(location.getLongitude());//在此赋值经度
////            currentAdrStr = String.valueOf(location.getAddrStr());//获取地址
////            Log.e(TAG, "onReceiveLocation: currentadr" + currentAdrStr);
////            sb.append("\nradius : ");
////            sb.append(location.getRadius());
////            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
////                sb.append("\nspeed : ");
////                sb.append(location.getSpeed());// 单位：公里每小时
////                sb.append("\nsatellite : ");
////                sb.append(location.getSatelliteNumber());
////                sb.append("\nheight : ");
////                sb.append(location.getAltitude());// 单位：米
////                sb.append("\ndirection : ");
////                sb.append(location.getDirection());// 单位度
////                sb.append("\naddr : ");
////                sb.append(location.getAddrStr());
////                sb.append("\ndescribe : ");
////                sb.append("gps定位成功");
////
////            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
////                sb.append("\naddr : ");
////                sb.append(location.getAddrStr());
////                //运营商信息
////                sb.append("\noperationers : ");
////                sb.append(location.getOperators());
////                sb.append("\ndescribe : ");
////                sb.append("网络定位成功");
////            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
////                sb.append("\ndescribe : ");
////                sb.append("离线定位成功，离线定位结果也是有效的");
////            } else if (location.getLocType() == BDLocation.TypeServerError) {
////                sb.append("\ndescribe : ");
////                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
////            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
////                sb.append("\ndescribe : ");
////                sb.append("网络不同导致定位失败，请检查网络是否通畅");
////            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
////                sb.append("\ndescribe : ");
////                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
////            }
////            sb.append("\nlocationdescribe : ");
////            sb.append(location.getLocationDescribe());// 位置语义化信息
////            List<Poi> list = location.getPoiList();// POI数据
////            if (list != null) {
////                sb.append("\npoilist size = : ");
////                sb.append(list.size());
////                for (Poi p : list) {
////                    sb.append("\npoi= : ");
////                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
////                }
////            }
////            Log.e(TAG, "BaiduLocationApiDem" + sb.toString());
////        }
////
////        private static final String TAG = "MyLocationListener";
////    }


}