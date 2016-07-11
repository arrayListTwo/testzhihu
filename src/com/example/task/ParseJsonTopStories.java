package com.example.task;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.zhihupocket.MainActivity;

/**
 * 解析json数据中的头条新闻数据
 * @author lei
 *
 */
public class ParseJsonTopStories {
	
	/**
	 * 存放解析出来的头条新闻数据
	 */
	ArrayList<HashMap<String, Object>> topstories_group = new ArrayList<HashMap<String,Object>>();
	
	public ParseJsonTopStories(String json_data){
		transJsonTopStoriesIntoArrayList(json_data);
	}
	
	/**
	 * 获取存储头条新闻数据的集合
	 * @return ArrayList存储头条新闻的集合
	 */
	public ArrayList<HashMap<String, Object>> getTopStories(){
		return topstories_group;
	}
	
	/**
	 * 将json格式的头条新闻数据提取出来存储到集合中
	 * @param json_data 从网络端获取到的json数据
	 */
	public void transJsonTopStoriesIntoArrayList(String json_data){
		
		//存储每一个子新闻
		HashMap<String, Object> story_item;
		
		try {
			JSONObject json_parse_object = new JSONObject(json_data);
			JSONArray json_topstories = json_parse_object.getJSONArray("top_stories");
			//循环添加数据
			for(int i=0;i<json_topstories.length();i++){
				story_item = new HashMap<String, Object>();
				story_item.put("title", json_topstories.getJSONObject(i).getString("title"));
				story_item.put("type", json_topstories.getJSONObject(i).getString("type"));
				story_item.put("id", json_topstories.getJSONObject(i).getString("id"));
				story_item.put("ga_prefix", json_topstories.getJSONObject(i).getString("ga_prefix"));
				// 记录新闻的url
				if(json_topstories.getJSONObject(i).has("share_url")){
					story_item.put("share_url", json_topstories.getJSONObject(i).getString("share_url"));
				}
				else {
					story_item.put("share_url", MainActivity.ZHIHU_STORY_API + json_topstories.getJSONObject(i).getString("id"));
				}
				String str = json_topstories.getJSONObject(i).getString("image");
				str = HandleStringAndImage.getHandledURL(str);
				story_item.put("image", str);
				topstories_group.add(story_item);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
