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
import com.fcapp.beautyimage.model.MyImage;
import com.fcapp.beautyimage.util.FileUtil;
import com.fcapp.beautyimage.util.Logger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class AllClassifyParser extends BaseParser<ArrayList<Home>> {
	ArrayList<Home> list;
	private Context context;
	private String json_cache_dir;
	public AllClassifyParser(Context context,String json_cache_dir){
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
		
		for(Home h : list){
			if(h == null ){
				list.remove(h);
				break;
			}
			
			if(h.getCover_img() == null){
				MyImage i = new MyImage();
				i.setSrc(h.getFile().getSrc());
				i.setWidth(h.getFile().getWidth());
				i.setHeight(h.getFile().getWidth());
				h.setCover_img(i);
			}
			String id = h.getId();
			if("最新图片".equals(h.getName())){
				id = "zxtp";
				h.setId(id);
			}
			if("最热图片".equals(h.getName())){
				id = "zrtp";
				h.setId(id);
			}
			
			h.setImg_local_dir(root_dir + Constant.CACHE+"/"+h.getId()+".jpg");
			//检查收藏
			for(Home home : MyApplication.collectImage){
				if(home.getId().equals(id)){
					h.setCollect(true);
				}
			}
			//修改瀑布流的高度
			modifyHeght(h);
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
		
		Logger.d("home parser", list.size()+"");
//		delData(list,indexList);
		
		return list;
	}
	
	public void delData(ArrayList<Home> l, ArrayList<Home> b){
		for(Home h:b){
			l.remove(h);
		}
	}
	private ArrayList<Home> indexList  = new ArrayList<Home>();
	public void modifyHeght(Home home){
		//这个if是修改瀑布流里面的高度的
		if(home.getCover_img() != null){
			int hw = home.getCover_img().getWidth();
			int hh = home.getCover_img().getHeight();
			float rh = (float)MyApplication.screntWidth/2/((float)hw/(float)hh);
			home.getCover_img().setHeight((int)rh);
			home.getCover_img().setWidth((int)MyApplication.screntWidth/2);
		}
		
		//修改大图界面的高度
		if(home.getFile() != null){
			int w = home.getFile().getWidth();
			int h = home.getFile().getHeight();
//			if(w == 0 || h == 0){
//				home.getFile().setWidth(MyApplication.screntWidth);
//				home.getFile().setHeight(MyApplication.screntHeight-50);
//			}
			
			if(MyApplication.screntWidth<w){
				home.getFile().setWidth(MyApplication.screntWidth);
				float hh = (float)MyApplication.screntWidth/((float)w/(float)h);
				home.getFile().setHeight((int)hh);
			}
		}
		
	}
}
