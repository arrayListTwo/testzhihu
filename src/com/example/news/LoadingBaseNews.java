package com.example.news;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 管理界面中的视图
 * @author lei
 *
 */
public interface LoadingBaseNews {
	
	/**
	 * 初始化视图
	 */
	public void initView();
	
	/**
	 * 界面刷新以后，重新加载视图
	 * @param stories_group 普通新闻的json数据
	 * @param topstories_group 头条新闻的json数据
	 */
	public void runView(ArrayList<HashMap<String, Object>> stories_group, final ArrayList<HashMap<String, Object>> topstories_group);

}
