package com.fcapp.beautyimage.util;

public class SystemInfo {
	
	
	/**
	      * 判断存储卡是否存在
	      *
	      * @return
	      */
	     public static boolean existSDcard()
	     {
	        if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState()))
	         {
	            return true;
	        }
	        else
	            return false;
	    }
}
