package com.example.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zhihupocket.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 普通新闻的适配器类
 * 
 * @author lei
 *
 */
public class StoriesAdapter extends BaseAdapter{
		
		/**
		 * 可动态加载布局对象
		 */
		private LayoutInflater mInflater = null;
		
		/**
		 * 记录普通新闻的json数据集合
		 */
		private ArrayList<HashMap<String, Object>> stories_group;
		
		/**
		 * 
		 */
		private DisplayImageOptions options;
		
		/**
		 * 
		 */
		private AnimateFirstDisplayListener animateFirstListener;
		
		//构造函数
		public StoriesAdapter(Context context, ArrayList<HashMap<String, Object>> stories_group) {
			this.mInflater = LayoutInflater.from(context);
			this.stories_group = stories_group;
			//对图片下载时初始化
			initImageLoaderOptions();
			animateFirstListener = new AnimateFirstDisplayListener(); 
		}
		
		/**
		 * 对图片下载时初始化
		 * @return true表示配置成功；false表示配置失败
		 */
		private boolean initImageLoaderOptions(){
			try {
				options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error)
				.cacheInMemory(false)
				.cacheOnDisk(true)
				.considerExifParams(true)
				.displayer(new SimpleBitmapDisplayer())
				.build();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		
		@Override
		public int getCount(){
			return stories_group.size();
		}
		
		@Override
		public Object getItem(int arg0){
			return null;
		}
		
		@Override
		public long getItemId(int arg0){
			return 0;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup Parent){
			PicAndTextViewGroup picandtext = null;
			if(convertView == null){
				picandtext = new PicAndTextViewGroup();
				convertView = mInflater.inflate(R.layout.main_lv_item, null);
				picandtext.iv_story_img = (ImageView)convertView.findViewById(R.id.iv_story_img);
				picandtext.tv_story_title = (TextView)convertView.findViewById(R.id.tv_story_title);
				convertView.setTag(picandtext);
			}
			else{
				picandtext = (PicAndTextViewGroup)convertView.getTag();
			}
			String images = stories_group.get(position).get("images").toString();
			if (!images.equals("none")) {
				ImageLoader.getInstance().displayImage(stories_group.get(position).get("images").toString()
						, picandtext.iv_story_img, options, animateFirstListener);
			}
			picandtext.tv_story_title.setText(stories_group.get(position).get("title").toString());
			return convertView;
		}
		
		private class PicAndTextViewGroup{
			ImageView iv_story_img;
			TextView tv_story_title;
		}
		
		private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

			static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				if (loadedImage != null) {
					ImageView imageView = (ImageView) view;
					boolean firstDisplay = !displayedImages.contains(imageUri);
					if (firstDisplay) {
						FadeInBitmapDisplayer.animate(imageView, 500);
						displayedImages.add(imageUri);
					}
				}
			}
		}
		
	}