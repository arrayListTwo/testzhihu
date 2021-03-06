package com.example.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.zhihupocket.MainActivity;

/**
 * 对数据库头条新闻的存取操作
 * @author lei
 *
 */
public class TopStoriesHandleSQLite {
	
	/**
	 * 活动对象
	 */
	private Context context;
	
	/**
	 * 数据库帮助类
	 */
	private MainDBHelper dbhelper;
	
	/**
	 * 数据库对象
	 */
	private SQLiteDatabase db;
	
	/**
	 * 格式化后的日期
	 */
	private String date;
	
	public TopStoriesHandleSQLite(Context context){
		this.context = context;
		Date format = MainActivity.sys_calendar.getTime();
		date = MainActivity.DATEFORMAT.format(format);
	}
	
	/**
	 * 将加载出来的今日头条新闻存储到数据库中
	 * @param topstories_group 存储头条新闻的集合
	 * @return true表示插入头条数据库成功；false表示插入头条数据库失败
	 */
	public boolean storedTopStoriesIntoDB(ArrayList<HashMap<String, Object>> topstories_group){
		try {
			//创建数据库帮助类(和一个数据库绑定起来)
			dbhelper = new MainDBHelper(context, MainDBHelper.DATABASE_NAME, null, 1);
			// 判断数据库中是否有原来记录
			db = dbhelper.getReadableDatabase();
			//查询语句，从存储头条新闻的表中查询出id列，查询条件是数据库中的日期是当前日期。selection指定where约束条件，selectionArgs为where中的占位符提供具体的值
			Cursor cursor = db.query(MainDBHelper.TABLE_TOPSTORIES, new String[]{"id"},"date="+date, null, null, null, null, null);
			// 删除数据库中原来有记录
			db = dbhelper.getWritableDatabase();
			if (cursor.getCount() != 0) {
				String del = "delete from '"+MainDBHelper.TABLE_TOPSTORIES+"' where date='"+date+"'";
				db.execSQL(del);
			}
			//创建ContentValues对象，存储数据库插入数据时的数据
			ContentValues values = new ContentValues();
			for (int i = 0; i < topstories_group.size(); i++) {
				 values.put("date", date);
				 values.put("image", topstories_group.get(i).get("image").toString());
				 values.put("id", topstories_group.get(i).get("id").toString());
				 values.put("type", topstories_group.get(i).get("type").toString());
				 values.put("title", topstories_group.get(i).get("title").toString());
				 values.put("share_url", topstories_group.get(i).get("share_url").toString());
				 values.put("ga_prefix", topstories_group.get(i).get("ga_prefix").toString());
				 db.insert(MainDBHelper.TABLE_TOPSTORIES, "id", values);
				 //清除ContentValues对象里存储的数据
				 values.clear();
			}
			db.close();
			dbhelper.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 从数据库中取出今日普通新闻
	 * @return 若数据库不为空，则返回存储普通新闻的ArrayList<HashMap<String, Object>>类型数据；若数据库为空则返回null
	 */
	public ArrayList<HashMap<String, Object>> getTopStoriesFromDB(){
		try {
			dbhelper = new MainDBHelper(context, MainDBHelper.DATABASE_NAME, null, 1);
			db = dbhelper.getReadableDatabase();
			Cursor cursor = db.query(MainDBHelper.TABLE_TOPSTORIES, new String[]{"id", "image", "title", "type", "share_url", "ga_prefix"},"date="+date, null, null, null, "ga_prefix DESC", null);
			if (cursor.getCount() == 0) {
				Toast.makeText(context, "数据库出现错误！", Toast.LENGTH_SHORT).show();
				return null;
			}
			else {
				ArrayList<HashMap<String, Object>> topstories = new ArrayList<HashMap<String,Object>>();
				HashMap<String, Object> item;
				while (cursor.moveToNext()) {
					item = new HashMap<String, Object>();
					item.put("id", cursor.getString(cursor.getColumnIndex("id")));
					item.put("title", cursor.getString(cursor.getColumnIndex("title")));
					item.put("image", cursor.getString(cursor.getColumnIndex("image")));
					item.put("type", cursor.getString(cursor.getColumnIndex("type")));
					item.put("ga_prefix", cursor.getString(cursor.getColumnIndex("ga_prefix")));
					item.put("share_url", cursor.getString(cursor.getColumnIndex("share_url")));
					topstories.add(item);
				}
				db.close();
				dbhelper.close();
				return topstories;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			db.close();
			dbhelper.close();
			return null;
		}
	}
}
