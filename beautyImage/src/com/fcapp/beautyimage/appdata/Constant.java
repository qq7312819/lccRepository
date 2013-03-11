package com.fcapp.beautyimage.appdata;

import android.os.Environment;

public class Constant {

	public final static int COLUMN_COUNT = 2; // 显示列数
	
	public final static int RELOAD = 125; // 显示列数
	
	
	public final static int PICTURE_COUNT_PER_LOAD = 15; // 每次加载30张图片
	
	public final static int PICTURE_TOTAL_COUNT = 10000;   //允许加载的最多图片数
	
	public final static int HANDLER_WHAT = 1;
	
	public final static int MESSAGE_DELAY = 200;
	
	public static final int SUCCESS = 100;
	public static final int NET_FAILED = 60;
	public static final int ADD_DOWNLOADQUEUE = 50;
	public static final int REMOVE_DOWNLOADQUEUE = 51;
	public static final int DOWNLOAD_FINISH = 52;
	
	public final static String GET = "GET";
	public final static String POST = "POST";
	public final static String HEAD = "HEAD";
	
	public final static String JSON_CACHE_DIR = "/fcapp/json";
	public final static String CACHE = "/fcapp/cache";
	public final static String COLLECT_CACHE_DIR = "/fcapp/collect";
	
	public final static String ALL_LETUN_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
	
	public final static String EXIST = "exist";
	public final static String NOT_FOUND = "not_found";
	
	public final static String ETAG = "etag";
	public final static String FILE_NOT_FOUND = "file not found";
	
	public final static String COLLECT_DIR = ALL_LETUN_DIR+COLLECT_CACHE_DIR;

	
	
	
	
		
}
