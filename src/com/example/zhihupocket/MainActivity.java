package com.example.zhihupocket;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.db.MainDBHelper;
import com.example.news.LoadingPreNew;
import com.example.news.LoadingTodayNews;
import com.example.task.StoriesGetTask;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * PagedHeadListView可以用这个开源项目做
 */
public class MainActivity extends FragmentActivity {

	/**
	 * 知乎日报今日新闻的API接口
	 */
	public static final String ZHIHU_API_TODAY = "http://news-at.zhihu.com/api/3/news/latest";
	
	/**
	 * 新闻详细内容的url的前缀
	 */
	public static final String ZHIHU_STORY_API = "http://daily.zhihu.com/story/";
	
	/**
	 * 获取旧消息的API接口（后面需要加上日期，若查询2016年6月14日新闻则需要加上20160615）
	 */
	public static final String ZHIHU_API_BEFORE = "http://news.at.zhihu.com/api/3/news/before/";
	
	/**
	 *记录日期 
	 */
	public static Calendar sys_calendar;
	
	/**
	 * 定义时间的格式
	 */
	@SuppressLint("SimpleDateFormat")
	public static SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyyMMdd");
	
	/**
	 * 支持上拉和下拉的开源控件
	 */
	private  PullToRefreshScrollView main_swiperefresh;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Log.v("My", "能打印日志消息");
		
		//ImageLoaderConfigurations配置
		initImageLoaderConfigurations();
		//初始化下拉菜单，并给下拉和上拉加上监听器
		initPullToRefresh();
		//日期的初始化
		initCalendar();
		//打开APP之后加载今日新闻
		new StoriesGetTask(MainActivity.this, new LoadingTodayNews(MainActivity.this, main_swiperefresh), main_swiperefresh, "today").execute();
	}
	
	/**
	 * 日期的初始化
	 */
	public void initCalendar(){
		MainActivity.sys_calendar = Calendar.getInstance();
	}
	
	/**
	 * 初始化下拉菜单，并给下拉和上拉加上监听器
	 */
	public void initPullToRefresh(){
		
		//获取到PullToRefreshScrollView对象
		main_swiperefresh = (PullToRefreshScrollView)findViewById(R.id.main_sv);
		
		//创建触发器对象，实现功能逻辑
		OnRefreshListener<ScrollView> swiperefresh_listener = new OnRefreshListener<ScrollView>() {
			
			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				// 下拉刷新
				if (PullToRefreshBase.Mode.PULL_FROM_START == main_swiperefresh.getCurrentMode()) {
					new StoriesGetTask(MainActivity.this, new LoadingTodayNews(MainActivity.this, main_swiperefresh), main_swiperefresh, "today").execute();
				}
				// 上拉加载
				else if (PullToRefreshBase.Mode.PULL_FROM_END == main_swiperefresh.getCurrentMode()) {
					new StoriesGetTask(MainActivity.this, new LoadingPreNew(MainActivity.this, main_swiperefresh), main_swiperefresh, "before").execute();
				}
				else {
					Toast.makeText(getApplicationContext(), "出错了……", Toast.LENGTH_SHORT).show();
				}
			}
		};
		
		//注册刷新的监听器
		main_swiperefresh.setOnRefreshListener(swiperefresh_listener);
		
	}
	
	/**
	 * ImageLoaderConfigurations配置
	 * @return
	 */
	public boolean initImageLoaderConfigurations(){
		try {
			//创建缓存目录，程序一启动就创建
			File pic_cache = new File(Environment.getExternalStorageDirectory(), "zhihupocketcache");
			if(!pic_cache.exists()){
					pic_cache.mkdir();
			}
			
			@SuppressWarnings("deprecation")
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
			.threadPriority(Thread.NORM_PRIORITY - 2)
			.denyCacheImageMultipleSizesInMemory()  //对于同一URL的图片内存中只缓存一个
			.discCache(new UnlimitedDiscCache(pic_cache))   //设置图片的缓存地址
			.diskCacheSize(50 * 1024 * 1024) // 50 Mb
			.tasksProcessingOrder(QueueProcessingType.LIFO)
			.writeDebugLogs() // Remove for release app
			.build();
			// Initialize ImageLoader with configuration.
			ImageLoader.getInstance().init(config);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_exit:
            	System.exit(0);
                return true;
            case R.id.clear_cache:
            	clearDBCache();
            	clearImageCache();
            	return true;
            default:
            	return super.onOptionsItemSelected(item);
        }
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}
	
	// 特殊指令
	public void clearDBCache(){
		AlertDialog.Builder builder = new Builder(MainActivity.this);
		builder.setMessage("你确定清空缓存");
		builder.setTitle("提示");
		builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				//这个是对话框要消失时的对话框
				arg0.dismiss();
				SQLiteDatabase db = null;
				MainDBHelper mdbhelper = null;
				try{
					mdbhelper = new MainDBHelper(getApplicationContext(), MainDBHelper.DATABASE_NAME, null, 1);
					db = mdbhelper.getReadableDatabase();
					db.close();
					if(getApplicationContext().deleteDatabase(MainDBHelper.DATABASE_NAME)){
						Toast.makeText(getApplicationContext(), "已成功清除缓存……", Toast.LENGTH_SHORT).show();
					}
					else {
						Toast.makeText(getApplicationContext(), "Sorry,出了一些故障", Toast.LENGTH_SHORT).show();
					}
				}
				catch(Exception e){
					e.printStackTrace();
					db.close();
				}
			}
		});
		builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
			}
		});
		builder.create().show();
	}	
	
	public boolean clearImageCache(){
		File pic_cache = new File(Environment.getExternalStorageDirectory(), "zhihupocketcache");
		if(!pic_cache.exists()){
			return false;
		}
		else {
			for (File i : pic_cache.listFiles()) {
				i.delete();
			}
		}
		return true;
	}
}
