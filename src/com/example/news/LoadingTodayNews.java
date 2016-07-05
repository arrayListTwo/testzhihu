package com.example.news;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.adapter.HotStoriesPagersAdapter;
import com.example.adapter.StoriesAdapter;
import com.example.listener.StoryItemClickListener;
import com.example.zhihupocket.MainActivity;
import com.example.zhihupocket.R;
import com.example.zhihupocket.StoryContent;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/**
 * @author lei
 *
 */
public class LoadingTodayNews implements LoadingBaseNews{
	
	/**
	 * 显示在界面上方的头条新闻，支持左右切换
	 */
	private ViewPager hotstoriespagers;
	
	/**
	 * 下方普通新闻的LIstView对象
	 */
	private ListView lv_showshortcontent;
	
	/**
	 * 当日的日期字符串
	 */
	private TextView tv_todaynews;
	
	/**
	 * 主界面对象
	 */
	private MainActivity main;
	
	/**
	 * 支持下拉刷新对象
	 */
	private PullToRefreshScrollView main_swiperefresh;
	
	/**
	 * 今日头条新闻的json数据
	 */
	private ArrayList<HashMap<String, Object>> m_topstoriesgroup = new ArrayList<HashMap<String,Object>>();
	
	/**
	 * 存放上方可左右切换的View对象
	 */
	private ArrayList<View> dots = new ArrayList<View>();
	
	/**
	 * 
	 */
	private int oldPosition = 0;
	
	public LoadingTodayNews(MainActivity main, PullToRefreshScrollView main_swiperefresh) {
		this.main = main;
		this.main_swiperefresh = main_swiperefresh;
	}
	
	@SuppressLint({ "InlinedApi", "InflateParams" })
	public void initView(){
		
		//获取主布局中的LinearLayout组件对象
		LinearLayout main_ll = (LinearLayout) main.findViewById(R.id.main_rl);
		// 清除layout里面的视图
		main_ll.removeAllViews();
		//动态加载布局文件
		LayoutInflater layoutInflater = LayoutInflater.from(main);
		RelativeLayout hotstories_container = (RelativeLayout)layoutInflater.inflate(R.layout.main_hotstoriescontent, null);
		//拿到第一个节点对象ViewPager对象
		hotstoriespagers = (ViewPager)hotstories_container.getChildAt(0);
		// 包含原点的容器
		LinearLayout dots_container = (LinearLayout)hotstories_container.getChildAt(1);
		dots_container.setVisibility(View.VISIBLE);
		View dot;
		for (int i = 0; i < 5; i++) {
			dot = (View)dots_container.getChildAt(i);
			dots.add(dot);
		}
		tv_todaynews = (TextView)hotstories_container.getChildAt(2);
		lv_showshortcontent = (ListView)hotstories_container.getChildAt(3);
		main_ll.addView(hotstories_container);
		
	}
	
	@Override
	public void runView(ArrayList<HashMap<String, Object>> stories_group, ArrayList<HashMap<String, Object>> topstories_group){
		// 在ui线程中设置listview
		// 先对这个进行赋值
		m_topstoriesgroup = topstories_group;
		String date = String.valueOf(MainActivity.sys_calendar.get(Calendar.YEAR)) + "年" + 
		       String.valueOf(MainActivity.sys_calendar.get(Calendar.MONTH) + 1) + "月" + // 获取当前月份  
		       String.valueOf(MainActivity.sys_calendar.get(Calendar.DAY_OF_MONTH)) + "日" // 获取当前月份的日期号码
		       + "  今天";
		tv_todaynews.setText(date);
		
		//为下方普通新闻的LIstView对象设置适配器
		StoriesAdapter loadlistadapter = new StoriesAdapter(main.getApplicationContext(), stories_group);
		lv_showshortcontent.setAdapter(loadlistadapter);
		
		// 设置当互动到当前的listitem时才去加载图片
		lv_showshortcontent.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));
		lv_showshortcontent.setOnItemClickListener(new StoryItemClickListener(main.getApplicationContext(), stories_group));
		
		//为上方左右切换的头条新闻设置适配器
		hotstoriespagers.setAdapter(new HotStoriesPagersAdapter(main.getSupportFragmentManager(), m_topstoriesgroup));
		hotstoriespagers.setVisibility(View.VISIBLE);
		
		// 为上方左右切换的头条新闻添加点击监听器
		hotstoriespagers.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				int i = hotstoriespagers.getCurrentItem() + 1;
				i = i < 5 ? i : i - 5;
				Intent intent = new Intent(main, StoryContent.class);
				intent.putExtra("stories_group", m_topstoriesgroup);
				// 万万没想到，标记的时候这个是反着来的
				intent.putExtra("story_order", i);
				main.startActivity(intent);
			}
		});
		
		hotstoriespagers.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int newPosition) {
				dots.get(newPosition).setBackgroundResource(R.drawable.dot_focused);
				dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
				oldPosition = newPosition;
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});
		
	    main_swiperefresh.onRefreshComplete();
	    // 更新时间,时间延后一天
	    MainActivity.sys_calendar.add(Calendar.DATE, -1);
	}
	
}
