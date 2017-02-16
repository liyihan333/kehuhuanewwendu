package com.kwsoft.kehuhua.hampson.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.request.ImageRequest;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.utils.NoDoubleClickListener;
import com.kwsoft.kehuhua.view.ImageLoadingDrawable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.kwsoft.kehuhua.config.Constant.sdPath;
import static com.kwsoft.kehuhua.config.Constant.sdPathProject;


/**
 * Created by Administrator on 2016/11/14 0014.
 */

public class ZuoYeGridViewAdapter extends BaseAdapter {
    private Context context;
    private List<String> datas;
    private List<String> fileNames;


    public ZuoYeGridViewAdapter(Context context, List<String> datas, List<String> fileNames) {
        this.context = context;
        this.datas = datas;
        this.fileNames = fileNames;


    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        view = LayoutInflater.from(context).inflate(R.layout.hampson_zuoye_item_image_layout, null);
        SimpleDraweeView my_image_view = (SimpleDraweeView) view.findViewById(R.id.my_image_view);
        my_image_view.setVisibility(View.VISIBLE);
//          final String prefix=fileNames.get(i).substring(fileNames.get(i).lastIndexOf(".")+1);

        final String url = datas.get(i);
        Uri uri = Uri.parse(url);

        ImageLoadingDrawable imageLoadingDrawable = new ImageLoadingDrawable();
        GenericDraweeHierarchyBuilder hierarchyBuilder = new GenericDraweeHierarchyBuilder(context.getResources());
        hierarchyBuilder
                .setProgressBarImage(imageLoadingDrawable)
                .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE)
                .setFailureImage(context.getResources().getDrawable(R.mipmap.jiazaishibai));
        my_image_view.setHierarchy(hierarchyBuilder.build());
        my_image_view.setImageURI(uri);


        my_image_view.setOnClickListener(new NoDoubleClickListener() {//禁止急速连续点击
            @Override
            public void onNoDoubleClick(View view) {
                File file = getFileFromDiskCache(url);
                if (file!=null) {
                    File file1 = changeName(file, fileNames.get(i));
                    openFile(file1.getPath());
                }


            }
        });
        return view;
    }


    private File changeName(File beforefile, String newName) {
        Log.e(TAG, "changeName: 开始修改");
        //这是你的源文件，本身是存在的


//这是你要保存之后的文件，是自定义的，本身不存在
        File afterFile = new File(sdPath + sdPathProject + newName);
        Log.e(TAG, "changeName: 监测点0 " + sdPath + newName);
//定义文件输入流，用来读取beforefile文件
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(beforefile);
            Log.e(TAG, "changeName: 监测点1");

            File dirFile = new File(sdPath + newName);
            if (!dirFile.exists()) {
                dirFile.mkdirs(); // 第一段
            }

            if (!afterFile.getParentFile().exists()) {
                // 分两次mkdirs，是为了避免目录层级过高导致目录创建失败的情况
                afterFile.getParentFile().mkdirs();
            }
            if (!afterFile.exists()) {
                try {
                    afterFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "changeName: 创建新文件失败");
                }
            }
            //定义文件输出流，用来把信息写入afterfile文件中
            FileOutputStream fos = new FileOutputStream(afterFile);
            Log.e(TAG, "changeName: 监测点2");
            //文件缓存区
            byte[] b = new byte[1024];
            Log.e(TAG, "changeName: 监测点3");
            //将文件流信息读取文件缓存区，如果读取结果不为-1就代表文件没有读取完毕，反之已经读取完毕
            try {
                while (fis.read(b) != -1) {
                    Log.e(TAG, "changeName: 监测点4");
                    //将缓存区中的内容写到afterfile文件中
                    fos.write(b);
                    Log.e(TAG, "changeName: 监测点5");
                    fos.flush();
                    Log.e(TAG, "changeName: 监测点6");

                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "changeName: 写入失败");
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "changeName: 修改失败");
        }
        return afterFile;
    }


    public static File getFileFromDiskCache(String url) {

        File localFile = null;
        if (!TextUtils.isEmpty(url)) {
            CacheKey cacheKey = DefaultCacheKeyFactory.getInstance()
                    .getEncodedCacheKey(ImageRequest.fromUri(url), null);
            if (ImagePipelineFactory.getInstance().getMainFileCache().hasKey(cacheKey)) {
                BinaryResource resource = ImagePipelineFactory.getInstance().getMainFileCache().getResource(cacheKey);
                localFile = ((FileBinaryResource) resource).getFile();
            } else if (ImagePipelineFactory.getInstance().getSmallImageFileCache().hasKey(cacheKey)) {
                BinaryResource resource = ImagePipelineFactory.getInstance().getSmallImageFileCache().getResource(cacheKey);
                localFile = ((FileBinaryResource) resource).getFile();
            }
        }
        return localFile;

    }

    private static final String TAG = "ZuoYeGridViewAdapter";

    /**

     */
    private void openFile(final String filePath) {
        Log.e(TAG, "openFile: filePath " + filePath);
        String ext = (filePath.substring(filePath.lastIndexOf('.')).toLowerCase(Locale.US));
//        String ext =ext1.replace(".","");//适配安卓7.0，截取的时候7.0会包含文件后缀前的那个“点”
        Log.e(TAG, "openFile: ext "+ext);
        try {
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            Log.e(TAG, "changeName: 监测点7");
            String temp = ext.substring(1);
            Log.e(TAG, "changeName: 监测点8");
            String mime = mimeTypeMap.getMimeTypeFromExtension(temp);
            Log.e(TAG, "changeName: 监测点9");
            Intent intent = new Intent();
            Log.e(TAG, "changeName: 监测点10");
            intent.setAction(android.content.Intent.ACTION_VIEW);
            Log.e(TAG, "changeName: 监测点11");
            File file = new File(filePath);
            Log.e(TAG, "changeName: 监测点12");
            intent.setDataAndType(Uri.fromFile(file), mime);
            Log.e(TAG, "changeName: 监测点13");
            context.startActivity(intent);//需要更改为fileProvider
            Log.e(TAG, "changeName: 监测点14");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.unable_to_open_the_suffix_name) + ext + context.getString(R.string.de_file),
                    Toast.LENGTH_LONG).show();
        }
    }
}