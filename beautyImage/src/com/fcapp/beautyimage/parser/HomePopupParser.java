package com.fcapp.beautyimage.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.content.Context;
import android.os.Environment;

import com.fcapp.beautyimage.appdata.Constant;
import com.fcapp.beautyimage.model.Home;
import com.fcapp.beautyimage.model.HomePopup;
import com.fcapp.beautyimage.util.FileUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class HomePopupParser extends BaseParser<HomePopup> {
	
	private Context context;
	private String json_cache_dir;
	public HomePopupParser(Context context,String json_cache_dir){
		this.context = context;
		this.json_cache_dir = json_cache_dir;
	}

	@Override
	public HomePopup parseJSON(String paramString) throws JSONException {
		Gson gson = new Gson();
		HomePopup popup = gson.fromJson(paramString, HomePopup.class);
		popup.setLocal_dir(Constant.ALL_LETUN_DIR+Constant.CACHE+popup.get_id()+".jpg");
		File file = new File(json_cache_dir);
		try {
			if(!file.exists()){
				//创建文件
				FileUtil.createFile(json_cache_dir);
				//写入文件
				FileUtil.writeFile(paramString, json_cache_dir);
			}
		} catch (Exception e) {
			//SD卡未挂载
			e.printStackTrace();
		}
		
		return popup;
	}

}
