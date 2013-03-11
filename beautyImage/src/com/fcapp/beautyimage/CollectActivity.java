package com.fcapp.beautyimage;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fcapp.beautyimage.appdata.Constant;
import com.fcapp.beautyimage.model.Home;
import com.fcapp.beautyimage.util.FileUtil;
import com.fcapp.beautyimage.util.Logger;
import com.fcapp.beautyimage.util.ProgressBarUtil;
import com.fcapp.beautyimage.widget.WaterFallModule;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class CollectActivity extends Activity implements ClickListener{

	private ArrayList<Home> list;
	private String collectDir;
	private ArrayList<Home> littleList;
	private LinearLayout ll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.collect);
		ll = (LinearLayout) findViewById(R.id.waterfall_container);
		super.onCreate(savedInstanceState);
	}


	public void addImage() {
		ll.removeAllViews();
		System.gc();
		collectDir = Constant.COLLECT_DIR+"/MyCollect.js";
		File file = new File(collectDir);
		if(!file.exists()){
			ProgressBarUtil.hideLoading();
			Toast.makeText(this, "暂无收藏", 0).show();
		}else{
			String collectContent = FileUtil.getFileContent(collectDir);
			Logger.i("收藏的图片的json", collectContent);
			Gson gson = new Gson();
			list = gson.fromJson(collectContent, new TypeToken<ArrayList<Home>>() { }.getType());
			ProgressBarUtil.hideLoading();
			WaterFallModule wfm = new WaterFallModule(this, list, this,getApplicationContext(),false);
			wfm.initLayout();
		}
	}
	
	@Override
	protected void onResume() {
		addImage();
		Logger.i("Collect activyt", "onResume  执行了addImage()方法");
		super.onResume();
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	
	@Override
	public void onShortClickImage(View v) {
		Intent thirdIntent = new Intent();
		thirdIntent.setClass(this, ThirdImageViewActivity.class);
		thirdIntent.putExtra("zxtp", list);
		thirdIntent.putExtra("id", "3");
		startActivity(thirdIntent);
		
	}

	@Override
	public void onLongClickImage(View v) {
		
	}

	
	private long exitTime = 0; 
    @Override 
    public boolean onKeyDown(int keyCode, KeyEvent event) { 
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){                   
        if((System.currentTimeMillis()-exitTime) > 2000){ 
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();                                exitTime = System.currentTimeMillis(); 
        } 
        else{ 
            finish(); 
            System.exit(0); 
            } 
        return true; 
        } 
        return super.onKeyDown(keyCode, event); 
    }  //End of 连续两次返回退出程序
}
