package com.example.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.fragment.MainHotStoriesFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

/**
 * 
 * ViewPage对象的适配器
 * @author lei
 *
 */
public class HotStoriesPagersAdapter extends FragmentStatePagerAdapter {

	private final String[] TITLES = { "one", "two", "three", "four", "five"};
	
	/**
	 * 头条新闻的集合
	 */
	private ArrayList<HashMap<String, Object>> top_stories = new ArrayList<HashMap<String, Object>>();
	
	public HotStoriesPagersAdapter(FragmentManager fm, ArrayList<HashMap<String, Object>> top_stories) {
		super(fm);
		this.top_stories = top_stories;
		//对显示图片的初始化配置
		MainHotStoriesFragment.initDisplayImageOptions();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return TITLES[position];
	}

	@Override
	public int getCount() {
		return TITLES.length;
	}

	@Override
	public Fragment getItem(int position) {
		return MainHotStoriesFragment.newInstance(position, top_stories);
	}
	
	@Override
	public int getItemPosition(Object object){
		return PagerAdapter.POSITION_NONE;
	}

}