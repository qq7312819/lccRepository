package com.fcapp.beautyimage.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.content.Context;
import android.os.Environment;

import com.fcapp.beautyimage.appdata.Constant;
import com.fcapp.beautyimage.appdata.MyApplication;
import com.fcapp.beautyimage.model.Home;
import com.fcapp.beautyimage.util.FileUtil;
import com.fcapp.beautyimage.util.Logger;
import com.fcapp.beautyimage.widget.MyScrollView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ThirdParser extends BaseParser<ArrayList<Home>> {

	ArrayList<Home> list;
	private Context context;
	private String json_cache_dir;
	public ThirdParser(Context context,String json_cache_dir){
		this.context = context;
		this.json_cache_dir = json_cache_dir;
	}
	
	@Override
	public ArrayList<Home> parseJSON(String paramString) throws JSONException {
		if(paramString == null){
			return null;
		}
		File root_dir = Environment.getExternalStorageDirectory();
		list = new ArrayList<Home>();
		Gson gson = new Gson();
		list = gson.fromJson(paramString, new TypeToken<List<Home>>() { }.getType());
		for(int i = 0;i<list.size();i++){
			Home home = list.get(i);
			home.getFile().setWidth(MyApplication.screntWidth);
			home.getFile().setHeight(MyApplication.screntHeight-40);
			home.setImg_local_dir(root_dir + Constant.CACHE+"/"+home.getId()+".jpg");
			//检查收藏
			for(Home h : MyApplication.collectImage){
				if(home.getId().equals(h.getId())){
					home.setCollect(true);
				}
			}
			
			if(home.getFile() == null){
				list.remove(i);
				Logger.i("ThirdParser", "因为getFile  被删除掉");
			}
			Logger.i("修改完home后的数据是", home.toString());
		}
		File file = new File(json_cache_dir);
		try {
			if(!file.exists()){
				//创建文件
				FileUtil.createFile(json_cache_dir);
				//写入文件
				FileUtil.writeFile(paramString, json_cache_dir);
				//存入sp中 
			}
		} catch (Exception e) {
			//SD卡未挂载
			e.printStackTrace();
		}
		return list;
	}

}
