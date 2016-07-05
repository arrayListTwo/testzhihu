package com.example.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.example.db.StoriesHandleSQLite;
import com.example.db.TopStoriesHandleSQLite;
import com.example.news.LoadingBaseNews;
import com.example.zhihupocket.MainActivity;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * 为何要进行异步操作：
 * 	Android是不允许在子线程中进行UI操作的。在主线程中进行耗时操作，程序易发生ANR错误(Application Not Response)。
 * 	我们必须在子线程中执行一些耗时操作，然后根据任务的执行结果来更新相应的UI控件，这就用到了异步消息处理机制。
 * 	AsyncTask类的实现原理是基于异步消息处理机制的。
 * 	AsyncTask类是一个抽象类，欲使用之需创建一个类去继承它，继承AsyncTask需要指定三个泛型参数：
 * 		params：在执行AsyncTask时需要传入的参数，可用于在后台任务中使用
 * 		progress：后台任务执行时，如果需要在界面上显示当前的进度，则使用这里指定的泛型作为进度单位
 * 		result：当任务执行完毕后，如果需要对执行结果进行返回，则使用这里指定的泛型作为返回值的类型
 * 
 * 	@author lei
 * 
 */
public class StoriesGetTask extends AsyncTask<Void, Integer, Boolean>{
	
	/**
	 * 从网络上获取今日新闻的json数据
	 */
	private String json_data;
	
	/**
	 * 存储普通新闻 
	 */
	private ArrayList<HashMap<String, Object>> stories_group = new ArrayList<HashMap<String,Object>>();
	
	/**
	 * 存储头条新闻 
	 */
	private ArrayList<HashMap<String, Object>> topstories_group = new ArrayList<HashMap<String,Object>>();
	
	private LoadingBaseNews loadingnews;
	
	/**
	 * 活动对象
	 */
	private MainActivity main;
	
	/**
	 * 支持下拉刷新的ScrollView
	 */
	private PullToRefreshScrollView main_swiperefresh;
	
	/**
	 * 刷新今日新闻还是前一日的新闻的字符串
	 */
	private String query_type;
	
	public StoriesGetTask(MainActivity main, LoadingBaseNews loadingnews, 
			PullToRefreshScrollView main_swiperefresh, String query_type) {
		this.loadingnews = loadingnews;
		this.main_swiperefresh = main_swiperefresh;
		this.main = main;
		this.query_type = query_type;
	}
	
	/**
	 * 主线程中运行。后台任务开始执行之前调用，用于一些界面上的初始化操作
	 */
	@Override  
    protected void onPreExecute() {
    }
	
	/**
	 * 子线程中运行。在这个方法内去之执行所有的耗时任务，在onPreExecute方法结束以后调用，开启异步任务时传入的参数是在这个方法中使用的
	 * 任务一旦完成就可以通过return语句将任务的执行结果返回，如果AsyncTask的第三个泛型参数指定的是Void，就可以不返回任务执行的结果
	 * 在这个方法中是不可以进行UI操作的，如果需要更新UI元素，可以调用publishProgress(Progress...)方法来完成
	 * 方法结束，异步任务结束，返回结果，将结果以参数的形式传递给onPostExecute方法。。。
	 * 
	 * UI线程开启，获得新闻
	 */
	@Override
	protected Boolean doInBackground(Void... params) {
		if (query_type.equals("today")) {
			return getTodayNews();
		}
		else {
			return getPreNews();
		}
	}
	
	/**
	 * 获取今天的数据
	 * @return true表示成功从网络或者数据库获取今日新闻；false表示获取今日新闻失败
	 */
	public boolean getTodayNews(){		
		
		TopStoriesHandleSQLite top = new TopStoriesHandleSQLite(main);
		
		StoriesHandleSQLite general = new StoriesHandleSQLite(main);
		
		// 先尝试从网络上获取今日的消息
		if (getTodayNewsFromOnLine()) {
			// 成功获取今日新闻，存入数据库
			if (top.storedTopStoriesIntoDB(topstories_group) && general.storedStoriesIntoDB(stories_group)) {
				// 在runviews之后需要进行修改系统时间
				Log.v("StoriesGetTask", "成功从网络处获得今日消息并存入数据库");
				MainActivity.sys_calendar = Calendar.getInstance();
				return true;
			}
			return false;
		}
		// 从数据库中查询有没有消息
		else {
			topstories_group = (top).getTopStoriesFromDB();
			stories_group = (general).getStoriesFromDB();
			if (stories_group != null && topstories_group != null) {
				// 在runviews之后需要进行修改系统时间
				MainActivity.sys_calendar = Calendar.getInstance();
				return true;
			}
			return false;
		}
	}
	
	/**
	 * 从网络上刷新今日的消息
	 * @return true表示成功获取新闻；false表示获取新闻失败
	 */
	public boolean getTodayNewsFromOnLine(){
		
		json_data = HttpRequestData.getJsonContent(MainActivity.ZHIHU_API_TODAY); 
		
		if (json_data.equals("-1")) {
			return false;
		}
		else {
			stories_group = (new ParseJsonStories(json_data)).getStories();
			topstories_group = (new ParseJsonTopStories(json_data)).getTopStories();
			if (stories_group != null && topstories_group != null) {
				return true;
			}
			return false;
		}
	}
	
	/**
	 * 获得之前的数据
	 * @return 
	 */
	public boolean getPreNews(){
		
		StoriesHandleSQLite general = new StoriesHandleSQLite(main);
		
		stories_group = general.getStoriesFromDB();
		
		// 先从数据库中取数据
		if (stories_group != null) {
			Log.v("StoriesGetTask", "成功从数据库获得以前的消息+消息数量"+stories_group.size());
			return true;
		}
		else {
			if (getPreNewsFromOnLine()) {	
				// 存入数据库
				if (general.storedStoriesIntoDB(stories_group)) {
					return true;
				}
				return false;
			}
		}
		return false;
	}

	
	// 从网络上获得之前新闻
	public boolean getPreNewsFromOnLine(){
		// 将日历提前一天,这个要是直接用形参额话有bug
		MainActivity.sys_calendar.add(Calendar.DATE, -1);
		Date format = MainActivity.sys_calendar.getTime();
		String date = MainActivity.DATEFORMAT.format(format);
		// 全局日期恢复正常
		MainActivity.sys_calendar.add(Calendar.DATE, 1);
		json_data = HttpRequestData.getJsonContent(MainActivity.ZHIHU_API_BEFORE+date); 
		if (json_data.equals("-1")) {
			return false;
		}
		else {
			stories_group = (new ParseJsonStories(json_data)).getStories();
			topstories_group = (new ParseJsonTopStories(json_data)).getTopStories();
			if (stories_group != null) {
				return true;
			}
			return false;
		}
	}
	
	/**
	 * 主线程中执行此方法。当在后台任务中调用了publishProgress(Progress...)方法后，这个方法就会很快的被调用，方法中携带的参数就是从
	 * 	后台任务中传递过来的，在这个方法中可以对UI进行操作，利用参数中的数值就可以对界面元素进行相应的更新
	 * 此方法此时为空，无逻辑！！
	 */
	@Override  
	protected void onProgressUpdate(Integer... values) {  
		
	}
	
	/**
	 * 主线程中运行。当后台任务执行完毕并通过return语句进行返回时，这个方法就会很快被调用。返回的数据作为参数传递到此方法中，可以利用
	 * 	返回的数据进行UI操作
	 */
	@Override  
    protected void onPostExecute(Boolean result) {    
        if (result) {
        	loadingnews.initView();
            loadingnews.runView(stories_group, topstories_group);
        } else {  
        	Toast.makeText(main, "没有成功从网络处获得数据", Toast.LENGTH_SHORT).show();
			main_swiperefresh.onRefreshComplete();
        }  
    }  

}
