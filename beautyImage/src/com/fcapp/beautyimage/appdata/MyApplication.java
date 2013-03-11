package com.fcapp.beautyimage.appdata;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.Context;

import com.fcapp.beautyimage.model.Home;
import com.fcapp.beautyimage.util.FileUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MyApplication extends Application {
	
	
	public  static  String DEVICE_ID;
	
	public static ArrayList<Home> collectImage;
	
	public static int screntWidth, screntHeight;
	public static Context ThirdActivityContext;

	static{
		String collectDir = Constant.COLLECT_DIR+"/MyCollect.js";
		File file = new File(collectDir);
		if(!file.exists()){
			collectImage = new ArrayList<Home>();
		}else{
			String collectContent = FileUtil.getFileContent(collectDir);
			Gson gson = new Gson();
			collectImage = gson.fromJson(collectContent, new TypeToken<List<Home>>() { }.getType());
		}
		
	}
}
