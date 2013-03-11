package com.fcapp.beautyimage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fcapp.beautyimage.appdata.Constant;
import com.fcapp.beautyimage.appdata.MyApplication;
import com.fcapp.beautyimage.model.Home;
import com.fcapp.beautyimage.model.RequestModel;
import com.fcapp.beautyimage.parser.AllClassifyParser;
import com.fcapp.beautyimage.util.Logger;
import com.fcapp.beautyimage.widget.WaterFallModule;

public class SecondActivity extends BaseActivity implements ClickListener{

	private SharedPreferences sp;
	private String tagName ;
	private TextView back,title;
	
	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.second);
		sp = getSharedPreferences("eTagConfig", MODE_PRIVATE);
		
		tagName = getIntent().getStringExtra("TAG_NAME");
		Logger.d("TAG_NAME", tagName);
		
//		AdMogoTargeting.setTestMode(false);// 测试模式，true为测试状态
	}

	@Override
	protected void findViewById() {
		back = (TextView) findViewById(R.id.tv_seconds_back);
		title = (TextView) findViewById(R.id.tv_seconds_top);
		title.setText(tagName);
	}

	@Override
	protected void setListtener() {
		back.setOnClickListener(this);
	}

	@Override
	protected void processLoggeric() {
		RequestModel model = new RequestModel();
		HashMap<String, String> params = new HashMap<String, String>();http://42.121.130.168:8001/api/hotest
		params.put("device_id", MyApplication.DEVICE_ID);
		if(!tagName.equals("最新图片") && !tagName.equals("最热图片") ){
			params.put("tag", tagName);
			model.requestUrl = R.string.second_api;
		}else{
			if(tagName.equals("最新图片")){
				model.requestUrl = R.string.newest_api;
			}
			if(tagName.equals("最热图片")){
				model.requestUrl = R.string.hottest_api;
			}
		}
		model.requestDataMap = params;
		model.context = this;
		File root_dir = Environment.getExternalStorageDirectory();
		model.jsonName = root_dir + Constant.JSON_CACHE_DIR + "/second_"+tagName+"_waterfall_json_data.js";
		model.jsonParser = new AllClassifyParser(getApplicationContext(),model.jsonName);
		model.requestMethod = Constant.GET;
		model.mark = R.string.second_waterfall_data;
		model.eTag = sp.getString(model.jsonName, " ");
		getDataFromServer(model, callBack);
	}

	ArrayList<Home> list;
	private DataCallback callBack = new DataCallback<Object>() {

		@Override
		public void processData(Object paramObject, int ObjectMark) {
			if(paramObject == null){
				Toast.makeText(SecondActivity.this, "返回的数据为空", 0).show();
			}
			switch (ObjectMark) {
			case R.string.second_waterfall_data:
				list = (ArrayList<Home>) paramObject;
				Logger.i("callBack  second_waterfall_data", ObjectMark+"  "+list.size() );
				WaterFallModule wfm = new WaterFallModule(SecondActivity.this, list, SecondActivity.this,getApplicationContext(),true);
				wfm.initLayout();
				break;
			}
		}
	};

	public void onDestroy() {
		System.gc();
		super.onDestroy();
		
	}

	@Override
	public void onShortClickImage(View v) {
		Home home = (Home) v.getTag();
		Intent thirdIntent = new Intent();
		thirdIntent.setClass(this, ThirdImageViewActivity.class);
		String idMark = home.getId();
		if(tagName.equals("最新图片")){
			idMark = "1";
			thirdIntent.putExtra("zxtp", list);
		}
		if(tagName.equals("最热图片")){
			idMark = "2";
			thirdIntent.putExtra("zrtp", list);
		}
		
		Logger.i("home.getId()", home.getId());
			
			thirdIntent.putExtra("id", idMark);
			startActivity(thirdIntent);
	}

	@Override
	public void onLongClickImage(View v) {
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_seconds_back:
			finish();
			break;

		default:
			break;
		}
		super.onClick(v);
	}
	
	
	
}
