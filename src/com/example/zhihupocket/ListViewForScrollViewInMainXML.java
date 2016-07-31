package com.example.zhihupocket;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 
 * 布局中利用到的ListView对象
 * 
 * @author lei
 *
 */
public class ListViewForScrollViewInMainXML extends ListView{

	public ListViewForScrollViewInMainXML(Context arg0) {
		super(arg0);
	}
	
	public ListViewForScrollViewInMainXML(Context arg0, AttributeSet arg1){
		super(arg0, arg1);
	}
	
	public ListViewForScrollViewInMainXML(Context arg0, AttributeSet arg1, int arg2){
		super(arg0, arg1, arg2);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		/*
		 * 在ScrollView中嵌套ListView，会出现ListView对象的高度计算不准确的问题，会导致ListView对象只显示一行数据的现象
		 * 	解决方法：
		 * 		继承ListView对象，重写其onMeasure()方法。
		 * 		具体详解参见：http://blog.csdn.net/hanhailong726188/article/details/46136569
		 */
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}