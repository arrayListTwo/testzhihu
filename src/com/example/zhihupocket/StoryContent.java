package com.example.zhihupocket;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * 新闻内容
 * 
 * @author lei
 *
 */
@SuppressWarnings("unused")
public class StoryContent extends Activity{
	
	/**
	 * 新闻集合
	 */
	private ArrayList<HashMap<String, Object>> top_or_not_stories_group;
	
	/**
	 * 点击新闻的位置position
	 */
	private int story_order;
	
	/**
	 * 新闻集合的长度
	 */
	private int story_number;
	
	/**
	 * WebView组件对象
	 */
	private WebView wv_show_story;
	
	/**
	 * 
	 */
	private ProgressBar progress;
	
	/**
	 * 
	 */
	private ShareActionProvider mShareActionProvider; // 分享按钮
	
	/**
	 * 
	 */
	private String title;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.story_content);
		//在界面左上方添加一个回退键
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		//获取到传递过来的新闻集合
		initStoriesGroup();
		//初始化webview控件
		initAllView();
		
		loadStory();
		
	}

	/**
	 * 获取到传递过来的新闻集合
	 */
	@SuppressWarnings("unchecked")
	public void initStoriesGroup(){
		try {
			
			top_or_not_stories_group = new ArrayList<HashMap<String,Object>>();
			top_or_not_stories_group = (ArrayList<HashMap<String,Object>>) this.getIntent().getSerializableExtra("stories_group");
			story_order = this.getIntent().getIntExtra("story_order", 0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		story_number = top_or_not_stories_group.size();
	}
	
	/**
	 * 初始化webview控件
	 */
	@SuppressLint("SetJavaScriptEnabled")
	public void initAllView(){
		wv_show_story = (WebView)findViewById(R.id.wv_show_story);
		progress = (ProgressBar)findViewById(R.id.pb_show_progress);
		// 对WebView的基本设置
		wv_show_story.getSettings().setBuiltInZoomControls(false);
		wv_show_story.getSettings().setJavaScriptEnabled(true);
	}
	
	/**
	 * 加载数据
	 */
	public void loadStory(){
		// 加载链接
		wv_show_story.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				//根据传入的参数再去加载新的网页
				view.loadUrl(url);
				//表示当前WebView可以处理打开新网页的请求，不用借助系统浏览器
				return true;
			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Toast.makeText(getApplicationContext(), "Oh no, " + description, Toast.LENGTH_SHORT).show();
			}
		});
		
		wv_show_story.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onProgressChanged(WebView view, int newProgress){
				super.onProgressChanged(view, newProgress);
				progress.setProgress(newProgress);
				// 当进度到100%时，结束，视图变为不见
				if (newProgress == 100 || wv_show_story.getHeight()!=0) {
					progress.setVisibility(View.GONE);
				}
			}
		});
		//加载此网址，显示点击的新闻内容
		wv_show_story.loadUrl(top_or_not_stories_group.get(story_order).get("share_url").toString());
		
		if (mShareActionProvider != null) {
			mShareActionProvider.setShareIntent(createShareStory());
		}
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub		
		if(keyCode == KeyEvent.KEYCODE_BACK && wv_show_story.canGoBack()){
			wv_show_story.goBack();
			return true;
		}
		else {
			return super.onKeyDown(keyCode, event);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.story_exit:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		// 初始化ActionBar
		getMenuInflater().inflate(R.menu.story_content_actionbar, menu);
		
		// Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.story_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
		return super.onCreateOptionsMenu(menu);
	}
	
	// 分享故事
	public Intent createShareStory(){
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, top_or_not_stories_group.get(story_order).get("title").toString());
		return intent;		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (wv_show_story != null && wv_show_story.canGoBack()) {
			wv_show_story.goBack();
		}
		super.onBackPressed();
	}
	
}