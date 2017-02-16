package com.kwsoft.kehuhua.hampson.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kwsoft.kehuhua.adcustom.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2016/11/8 0008.
 *
 */

public class StuClassTchAdapter extends BaseAdapter {
    private Context mContext;
    private List<List<Map<String, String>>> list = new ArrayList<>();

    private boolean isFristTime = true;

    /**
     * 标签之间的间距 px
     */
    final int itemMargins = 17;

    /**
     * 标签的行间距 px
     */
    final int lineMargins = 10;

    private ViewGroup container = null;


    public StuClassTchAdapter(Context mContext, List<List<Map<String, String>>> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;

        List<Map<String, String>> map = list.get(i);


//                = (String[]) map.get(0).get("tags");
        if (view == null) {
            //解析布局
            view = LayoutInflater.from(mContext).inflate(R.layout.activity_stu_course_teach_list_item, null);
            //创建ViewHolder持有类
            holder = new ViewHolder();
            //将每个控件的对象保存到持有类中
            holder.tv_title = (TextView) view.findViewById(R.id.tv_title);
            holder.my_ping_jia = (TextView) view.findViewById(R.id.my_ping_jia);

            holder.tv_teach_name = (TextView) view.findViewById(R.id.tv_teach_name);
            holder.teach_name_title = (TextView) view.findViewById(R.id.teach_name_title);
            holder.star0 = view.findViewById(R.id.star0);
            holder.star1 = view.findViewById(R.id.star1);
            holder.star2 = view.findViewById(R.id.star2);
            holder.star3 = view.findViewById(R.id.star3);
            holder.star4 = view.findViewById(R.id.star4);
            view.setTag(holder);
        } else {
            //每次需要使用的时候都会拿到这个持有类
            holder = (ViewHolder) view.getTag();

        }
        try {
           //老师名称，第一项
            holder.tv_title.setText(map.get(0).get("fieldCnName2"));
            //课程名称

            holder.teach_name_title.setText(map.get(1).get("fieldCnName"));
            holder.tv_teach_name.setText(map.get(1).get("fieldCnName2"));
            //获取星级名称
            holder.my_ping_jia.setText(map.get(2).get("fieldCnName"));
            //老师标题和老师名字

            float xingJiF = Float.valueOf(map.get(2).get("fieldCnName2"));
           int xingji =floatToInt(xingJiF);
            Log.e(TAG, "getView:  " +map.get(2).toString());

            switch (xingji) {


                case 5:
                    holder.star0.setBackgroundResource(R.mipmap.star_ratingbar_full);
                    holder.star1.setBackgroundResource(R.mipmap.star_ratingbar_full);
                    holder.star2.setBackgroundResource(R.mipmap.star_ratingbar_full);
                    holder.star3.setBackgroundResource(R.mipmap.star_ratingbar_full);
                    holder.star4.setBackgroundResource(R.mipmap.star_ratingbar_full);
                    break;
                case 4:
                    holder.star0.setBackgroundResource(R.mipmap.star_ratingbar_full);
                    holder.star1.setBackgroundResource(R.mipmap.star_ratingbar_full);
                    holder.star2.setBackgroundResource(R.mipmap.star_ratingbar_full);
                    holder.star3.setBackgroundResource(R.mipmap.star_ratingbar_full);
                    holder.star4.setBackgroundResource(R.mipmap.star_ratingbar_empty);

                    break;
                case 3:
                    holder.star0.setBackgroundResource(R.mipmap.star_ratingbar_full);
                    holder.star1.setBackgroundResource(R.mipmap.star_ratingbar_full);
                    holder.star2.setBackgroundResource(R.mipmap.star_ratingbar_full);
                    holder.star3.setBackgroundResource(R.mipmap.star_ratingbar_empty);
                    holder.star4.setBackgroundResource(R.mipmap.star_ratingbar_empty);
                    break;
                case 2:
                    holder.star0.setBackgroundResource(R.mipmap.star_ratingbar_full);
                    holder.star1.setBackgroundResource(R.mipmap.star_ratingbar_full);
                    holder.star2.setBackgroundResource(R.mipmap.star_ratingbar_empty);
                    holder.star3.setBackgroundResource(R.mipmap.star_ratingbar_empty);
                    holder.star4.setBackgroundResource(R.mipmap.star_ratingbar_empty);
                    break;
                case 1:
                    holder.star0.setBackgroundResource(R.mipmap.star_ratingbar_full);
                    holder.star1.setBackgroundResource(R.mipmap.star_ratingbar_empty);
                    holder.star2.setBackgroundResource(R.mipmap.star_ratingbar_empty);
                    holder.star3.setBackgroundResource(R.mipmap.star_ratingbar_empty);
                    holder.star4.setBackgroundResource(R.mipmap.star_ratingbar_empty);
                    break;
                case 0:
                    holder.star0.setBackgroundResource(R.mipmap.star_ratingbar_empty);
                    holder.star1.setBackgroundResource(R.mipmap.star_ratingbar_empty);
                    holder.star2.setBackgroundResource(R.mipmap.star_ratingbar_empty);
                    holder.star3.setBackgroundResource(R.mipmap.star_ratingbar_empty);
                    holder.star4.setBackgroundResource(R.mipmap.star_ratingbar_empty);


                    break;



                default:
                    holder.star0.setBackgroundResource(R.mipmap.star_ratingbar_empty);
                    holder.star1.setBackgroundResource(R.mipmap.star_ratingbar_empty);
                    holder.star2.setBackgroundResource(R.mipmap.star_ratingbar_empty);
                    holder.star3.setBackgroundResource(R.mipmap.star_ratingbar_empty);
                    holder.star4.setBackgroundResource(R.mipmap.star_ratingbar_empty);


                    break;

            }

            Log.e(TAG, "getView: 总数 "+(map.size()-1)+" 现在执行到 "+i);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }
  private   int floatToInt(float f){
        int i;
        if(f>0) //正数
            i = (int)(f*10 + 5)/10;
        else if(f<0) //负数
            i = (int)(f*10 - 5)/10;
        else i = 0;

        return i;

    }
    private static final String TAG = "CourseRatingBarAdapter";

    class ViewHolder {
        TextView tv_title, teach_name_title,tv_teach_name,my_ping_jia;
        View star0, star1, star2, star3, star4;
    }

    public void clear(){
        list.removeAll(list);
        notifyDataSetChanged();
    }
    public void addData(List<List<Map<String, String>>> addData){
        list.addAll(addData);
        notifyDataSetChanged();
    }

    public List<List<Map<String, String>>> getDatas(){
        return list;

    }
}
