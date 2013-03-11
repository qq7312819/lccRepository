package com.fcapp.beautyimage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.os.Environment;
import android.widget.Toast;

import com.fcapp.beautyimage.BaseActivity.DataCallback;
import com.fcapp.beautyimage.appdata.Constant;
import com.fcapp.beautyimage.appdata.MyApplication;
import com.fcapp.beautyimage.model.Home;
import com.fcapp.beautyimage.model.RequestModel;
import com.fcapp.beautyimage.parser.AllClassifyParser;
import com.fcapp.beautyimage.widget.WaterFallModule;

public class NewestActivity extends BaseActivity {

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.newest);
	}

	@Override
	protected void findViewById() {
		
	}

	@Override
	protected void setListtener() {
		
	}

	@Override
	protected void processLoggeric() {
		RequestModel model = new RequestModel();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("device_id", MyApplication.DEVICE_ID);
		model.requestDataMap = params;
		model.context = this;
		File root_dir = Environment.getExternalStorageDirectory();
		model.jsonName = root_dir +Constant.JSON_CACHE_DIR+"/newest_json_data.js";
		model.jsonParser = new AllClassifyParser(getApplicationContext(),model.jsonName);
		model.requestUrl = R.string.newest_api;
		model.requestMethod = Constant.GET;
		model.mark = R.string.newest_data;
		model.eTag = sp.getString(model.jsonName, " ");
		

		getDataFromServer(model, callBack);
	}
	
	private DataCallback callBack = new DataCallback<Object>(){
		
		@Override
		public void processData(Object paramObject, int ObjectMark) {
			
			if(paramObject == null){
				Toast.makeText(NewestActivity.this, "服务器返回空", 0).show();
				return;
			}
			switch (ObjectMark) {
			case R.string.newest_data:
				ArrayList<Home> list = (ArrayList<Home>) paramObject;
//				WaterFallModule wfm = new WaterFallModule(NewestActivity.this,list,this); 
//				wfm.initLayout();
				break;
			}
		}
	};

}
