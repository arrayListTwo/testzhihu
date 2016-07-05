package com.example.task;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.example.zhihupocket.MainActivity;

/**
 * 解析json数据中的普通新闻数据
 * @author lei
 *
 */
public class ParseJsonStories {
	
	/**
	 * 存放解析出来的普通新闻数据
	 */
	ArrayList<HashMap<String, Object>> stories_group = new ArrayList<HashMap<String,Object>>();

	public ParseJsonStories(String json_data){
		transJsonStoriesIntoArrayList(json_data);
	}
	
	/**
	 * 获取存储普通新闻数据的集合
	 * @return ArrayList存储普通新闻的集合
	 */
	public ArrayList<HashMap<String, Object>> getStories(){
		return stories_group;
	}
	
	/**
	 * 将json格式的普通新闻数据提取出来存储到集合中
	 * 
	 * @param json_data 从网络端获取到的json数据
	 */
	public void transJsonStoriesIntoArrayList(String json_data){
		
		//存储每一个子新闻
		HashMap<String, Object> story_item;
		
		try {
			
			JSONObject json_parse_object = new JSONObject(json_data);
			
			JSONArray json_stories = json_parse_object.getJSONArray("stories");
			
			for(int i=0; i<json_stories.length(); i++){
				
				story_item = new HashMap<String, Object>();
				story_item.put("title", json_stories.getJSONObject(i).getString("title"));
				story_item.put("id", json_stories.getJSONObject(i).getString("id"));
				story_item.put("type", json_stories.getJSONObject(i).getString("type"));
				story_item.put("ga_prefix", json_stories.getJSONObject(i).getString("ga_prefix"));
				if (json_stories.getJSONObject(i).has("share_url")) {
					story_item.put("share_url", json_stories.getJSONObject(i).getString("share_url"));
				}
				else {
					story_item.put("share_url", MainActivity.ZHIHU_STORY_API + json_stories.getJSONObject(i).getString("id"));
				}
				
				// 判断数组中是否存在images这个项目
				if(json_stories.getJSONObject(i).has("images")){
					String str = json_stories.getJSONObject(i).getString("images");
					Log.v("images", str);
					str = HandleStringAndImage.getHandledURL(str);					
					story_item.put("images", str);
//					story_item.put("imguri", HandleStringAndImage.downloadPic(str, MainActivity.pic_cache));
				}
				else {
					story_item.put("images", "none");
				}
				stories_group.add(story_item);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
