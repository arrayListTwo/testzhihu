package com.example.news;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.adapter.StoriesAdapter;
import com.example.listener.StoryItemClickListener;
import com.example.zhihupocket.MainActivity;
import com.example.zhihupocket.R;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

/**
 * 加载旧新闻
 * @author lei
 *
 */
public class LoadingPreNew implements LoadingBaseNews{
	
	/**
	 * 旧新闻的ListView对象
	 */
	private ListView lv_showshortcontent;
	
	/**
	 * 旧新闻布局文件中的记录日期的TextView
	 */
	private TextView lv_header;
	
	/**
	 * 活动对象
	 */
	private MainActivity main;
	
	/**
	 * 支持下拉刷新的控件
	 */
	private PullToRefreshScrollView main_swiperefresh;
	
	public LoadingPreNew(MainActivity main, PullToRefreshScrollView main_swiperefresh) {
		this.main = main;
		this.main_swiperefresh = main_swiperefresh;
	}
	
	@SuppressLint({ "InlinedApi", "InflateParams" })
	public void initView(){
		//获取主布局文件中的LinearLayout对象（未将主布局文件中的LinearLayout对象中的内容清空，代表当上拉刷新旧新闻时，刷新的今日新闻还在）
		LinearLayout main_ll = (LinearLayout) main.findViewById(R.id.main_rl);
		//动态加载旧新闻布局文件
		LayoutInflater layoutInflater = LayoutInflater.from(main);
		RelativeLayout  rl = (RelativeLayout)layoutInflater.inflate(R.layout.main_storiescontent, null);
		
		lv_showshortcontent = (ListView)rl.getChildAt(1);
		lv_header = (TextView)rl.getChildAt(0);
		//将布局文件添加到
		main_ll.addView(rl);
	}
	
	public void runView(ArrayList<HashMap<String, Object>> stories_group, final ArrayList<HashMap<String, Object>> topstories_group){
		//在ui线程中设置listview
		String date = String.valueOf(MainActivity.sys_calendar.get(Calendar.YEAR))+"年"+
		       String.valueOf(MainActivity.sys_calendar.get(Calendar.MONTH) + 1)+"月"+// 获取当前月份  
		       String.valueOf(MainActivity.sys_calendar.get(Calendar.DAY_OF_MONTH))+"日";// 获取当前月份的日期号码
		lv_header.setText(date);
		//旧新闻的ListView对象设置适配器
		StoriesAdapter loadlistadapter = new StoriesAdapter(main.getApplicationContext(), stories_group);
		lv_showshortcontent.setAdapter(loadlistadapter);
		//旧新闻的ListItem添加监听器
		lv_showshortcontent.setOnItemClickListener(new StoryItemClickListener(main.getApplicationContext(), stories_group));
		//刷新完毕
	    main_swiperefresh.onRefreshComplete();
	}
	
}
