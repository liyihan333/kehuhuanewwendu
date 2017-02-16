package com.kwsoft.version;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;

public class AndyViewPagerActivity extends BaseActivity implements OnClickListener, OnPageChangeListener
{

	private ViewPager vp; // 总结构集合
	private ViewPagerAdapter vpAdapter; // 适配器
	private List<View> views;// 容器
	//private Button button;// 立即体验按钮

	// 引导滑动4个图片资源
	private static final int[] pics = { R.mipmap.guide_image3, R.mipmap.guide_image3, R.mipmap.guide_image3};

	// 底部小点图片
	//private ImageView[] dots;

	// 记录当前选中位置
	private int currentIndex;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_andy_viewpager);
		//button = (Button) findViewById(R.id.button);
		views = new ArrayList<View>();

		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

		// 初始化引导图片列 ?
		for (int i = 0; i < pics.length; i++)
		{
			ImageView iv = new ImageView(this);
			iv.setLayoutParams(mParams);
			iv.setImageResource(pics[i]);
			views.add(iv);
		}
		vp = (ViewPager) findViewById(R.id.viewpager);
		// 初始化Adapter
		vpAdapter = new ViewPagerAdapter(views,this);
		vp.setAdapter(vpAdapter);
		// 绑定回调
		vp.setOnPageChangeListener(this);


		// button = (Button) findViewById(R.id.button);
		// 初始化底部小 ?
//		initDots();
//		button.setOnClickListener(new OnClickListener()
//		{
//
//			@Override
//			public void onClick(View arg0)
//			{
//				Intent intent = new Intent();
//				intent.setClass(AndyViewPagerActivity.this, StuProLoginActivity.class);
//				AndyViewPagerActivity.this.startActivity(intent);
//				finish();
//			}
//		});

	}

	@Override
	public void initView() {

	}

	private void initDots()
	{
//		LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
//
//		dots = new ImageView[pics.length];
//
//		// 循环取得小点图片
//		for (int i = 0; i < pics.length; i++)
//		{
//			dots[i] = (ImageView) ll.getChildAt(i);
//			dots[i].setEnabled(true);// 都设为灰 ?
//			dots[i].setOnClickListener(this);
//			dots[i].setTag(i);// 设置位置tag，方便取出与当前位置对应
//		}
//
//		currentIndex = 0;
//		dots[currentIndex].setEnabled(false);// 设置为白色，即 ?中状 ?
	}

	/**
	 * 设置当前的引导页
	 */
	private void setCurView(int position)
	{
		if (position < 0 || position >= pics.length)
		{
			return;
		}

		vp.setCurrentItem(position);
	}

	/**
	 * 这只当前引导小点的 ? ?
	 */
	private void setCurDot(int positon)
	{
		if (positon < 0 || positon > pics.length - 1 || currentIndex == positon)
		{
			return;
		}

//		dots[positon].setEnabled(false);
//		dots[currentIndex].setEnabled(true);

		currentIndex = positon;
	}

	// 当滑动状态改变时调用
	@Override
	public void onPageScrollStateChanged(int arg0)
	{
		// TODO Auto-generated method stub

	}

	// 当当前页面被滑动时调 ?
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2)
	{
		// TODO Auto-generated method stub

	}

	// 当新的页面被选中时调 ?
	@Override
	public void onPageSelected(int arg0)
	{
		// 设置底部小点选中状 ?
		setCurDot(arg0);
//		if (arg0 == 2)
//		{
//			button.setVisibility(View.VISIBLE);
//
//		} else
//		{
//			button.setVisibility(View.GONE);
//		}
	}

	@Override
	public void onClick(View v)
	{
		int position = (Integer) v.getTag();
		setCurView(position);
		setCurDot(position);
	}


}
