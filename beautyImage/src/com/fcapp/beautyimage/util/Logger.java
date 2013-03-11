package com.fcapp.beautyimage.util;

import android.util.Log;


public class Logger {
	private static int Logger_LEVEL=0;
	private static int ERROR = 1;
	private static int WARN = 2;
	private static int INFO =3;
	private static int DEBUG =4;
	private static int VERBOS=5;
	private static String tag = "Logger";
	
	public static void e(String tag,String msg){
		if(Logger_LEVEL>ERROR){
			Log.e(tag,msg);
		}
	}
	public static void w(String tag,String msg){
		if(Logger_LEVEL>WARN){
			Log.w(tag,msg);
		}
	}
	public static void i(String tag,String msg){
		if(Logger_LEVEL>INFO){
			Log.i(tag,msg);
		}
	}
	public static void d(String tag,String msg){
		if(Logger_LEVEL>DEBUG){
			Log.d(tag,msg);
		}
	}
	public static void v(String tag,String msg){
		if(Logger_LEVEL>VERBOS){
			Log.v(tag,msg);
		}
	}
	
}
