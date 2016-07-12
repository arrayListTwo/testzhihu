package com.example.listener;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.zhihupocket.StoryContent;

/**
 * 子项监听器对象
 * @author lei
 *
 */
public class StoryItemClickListener implements OnItemClickListener{
	
	/**
	 * Context对象
	 */
	Context context;
	
	/**
	 * 存储普通新闻的集合
	 */
	ArrayList<HashMap<String, Object>> stories_group;
	
	public StoryItemClickListener(Context context, ArrayList<HashMap<String, Object>> stories_group) {
		this.context = context;
		this.stories_group = stories_group;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// 设置意图对象
		Intent intent = new Intent(context, StoryContent.class);
		// 非activity中必用的
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//传递普通新闻集合和点击的ListView子项的位置
		intent.putExtra("stories_group", stories_group);
		intent.putExtra("story_order", position);
		//开启意图
		context.startActivity(intent);
	}
}