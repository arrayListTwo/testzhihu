package com.example.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 数据库帮助类
 * @author lei
 *
 */
public class MainDBHelper extends SQLiteOpenHelper{
	
	/**
	 * 创建的数据库名
	 */
	public static String DATABASE_NAME = "zhihupocket.db";
	
	/**
	 * 存储普通新闻的表
	 */
	public static String TABLE_STORIES = "stories_cache";
	
	/**
	 * 存储头条新闻的表
	 */
	public static String TABLE_TOPSTORIES = "topstories_cache";

	public MainDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//创建头条新闻表
		db.execSQL("create table if not exists topstories_cache("
				+ "date varchar,"
                + "image varchar,"  
                + "id varchar primary key,"
                + "type varchar,"
                + "ga_prefix varchar,"
                + "share_url,"
                + "title varchar)");
		//创建普通新闻表
		db.execSQL("create table if not exists stories_cache("  
                  + "date varchar,"  
                  + "images varchar,"
                  + "id varchar primary key,"
                  + "ga_prefix varchar,"
                  + "type varchar,"
                  + "share_url,"
                  + "title varchar)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.v("MainDBHelper", "MainDBHelper onUpgrade!");
	}

}
