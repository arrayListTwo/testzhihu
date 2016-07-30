package com.example.fragment;


import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhihupocket.R;
import com.example.zhihupocket.StoryContent;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 上方头条新闻的碎片对象
 * 
 * @author lei
 *
 */
@SuppressLint("ValidFragment") public class MainHotStoriesFragment extends Fragment implements OnClickListener{

	/**
	 * position字符串
	 */
	private static final String ARG_POSITION = "position";

	/**
	 * ViewPage对象显示的当前位置
	 */
	private int position;
	
	/**
	 * 头条新闻的集合
	 */
	private ArrayList<HashMap<String, Object>> top_stories = new ArrayList<HashMap<String,Object>>();
	
	/**
	 * DisplayImageOptions对象
	 */
	private static DisplayImageOptions options;
	
	/**
	 * 创建一个碎片Fragment对象
	 * @param position 当前位置
	 * @param top_stories 头条新闻的集合
	 * @return Fragment对象
	 */
	public static MainHotStoriesFragment newInstance(int position, ArrayList<HashMap<String, Object>> top_stories) {
		MainHotStoriesFragment f = new MainHotStoriesFragment(top_stories);
		//创建一个Bundle对象，存储位置
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		//将Bundle对象存储到碎片Fragment对象中
		f.setArguments(b);
		return f;
	}
	
	public MainHotStoriesFragment() {
		super();
	}

	public MainHotStoriesFragment(ArrayList<HashMap<String, Object>> top_stories) {
		this.top_stories = top_stories;
	}
	
	/**
	 * 对加载图片的初始化配置
	 * @return 配置成功返回true，配置失败返回false
	 */
	public static boolean initDisplayImageOptions(){
		try {
			options = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.ic_empty)
			.showImageOnFail(R.drawable.ic_error)
			.resetViewBeforeLoading(true)
			.cacheInMemory(false)
			.cacheOnDisk(true)
			.imageScaleType(ImageScaleType.EXACTLY)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.considerExifParams(true)
			.displayer(new FadeInBitmapDisplayer(300))
			.build();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//获取Bundle对象中存储的position信息
		position = getArguments().getInt(ARG_POSITION);
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		//动态加载布局信息
		RelativeLayout pager_item_container = (RelativeLayout)inflater.inflate(R.layout.vf_show_item, null);
		//添加监听器
		pager_item_container.setOnClickListener(this);
		//获取布局中的View对象
		ImageView pic = (ImageView)pager_item_container.getChildAt(0);
		TextView txt = (TextView)pager_item_container.getChildAt(1);
		final ProgressBar spinner = (ProgressBar) pager_item_container.getChildAt(2);
		//设置文字信息
		txt.setText(top_stories.get(position).get("title").toString());
		//加载图片并显示在ImageView控件上
		ImageLoader.getInstance().displayImage(top_stories.get(position).get("image").toString(),
				pic, options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				//显示进度条
				spinner.setVisibility(View.VISIBLE);
			}
			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				String message = null;
				switch (failReason.getType()) {
					case IO_ERROR:
						message = "Input/Output error";
						break;
					case DECODING_ERROR:
						message = "Image can't be decoded";
						break;
					case NETWORK_DENIED:
						message = "Downloads are denied";
						break;
					case OUT_OF_MEMORY:
						message = "Out Of Memory error";
						break;
					case UNKNOWN:
						message = "Unknown error";
						break;
				}
				Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
				//进度条消失
				spinner.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				//进度条消失
				spinner.setVisibility(View.GONE);
			}
		});
		return pager_item_container;
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(getActivity(), StoryContent.class);
		intent.putExtra("stories_group", top_stories);
		intent.putExtra("story_order", position);
		startActivity(intent);
	}

}