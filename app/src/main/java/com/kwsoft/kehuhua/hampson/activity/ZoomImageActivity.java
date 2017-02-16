package com.kwsoft.kehuhua.hampson.activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.view.ImageLoadingDrawable;
import com.kwsoft.kehuhua.view.ZoomableDraweeView;
import com.kwsoft.kehuhua.widget.CommonToolbar;

import static com.kwsoft.kehuhua.config.Constant.topBarColor;

/**
 * Created by Administrator on 2016/11/14 0014.
 *
 *
 */

public class ZoomImageActivity extends BaseActivity{
    String url;
    private CommonToolbar mToolbar;
    ZoomableDraweeView my_image_view;
    private static final String TAG = "ZoomImageActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image_layout);
        getInfoData();
        initView();
    }

    private void getInfoData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = bundle.getString("url");
//            fileName= bundle.getString("fileName");
//            prefix= bundle.getString("fileEnd");
            Log.e(TAG, "getInfoData: 图片放大之后的url "+url);

        }

    }

    @Override
    public void initView() {
        mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);
        mToolbar.setTitle(getString(R.string.view_larger));
        mToolbar.setBackgroundColor(getResources().getColor(topBarColor));
        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        my_image_view=(ZoomableDraweeView)findViewById(R.id.my_image_view);
        Uri uri = Uri.parse(url);

        ImageLoadingDrawable imageLoadingDrawable = new ImageLoadingDrawable();
        GenericDraweeHierarchyBuilder hierarchyBuilder = new GenericDraweeHierarchyBuilder(getResources());
        hierarchyBuilder
                .setProgressBarImage(imageLoadingDrawable)
                .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE)
                .setFailureImage(getResources().getDrawable(R.mipmap.jiazaishibai));
        my_image_view.setHierarchy(hierarchyBuilder.build());
        my_image_view.setImageURI(uri);


    }
}
