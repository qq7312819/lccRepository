package com.fcapp.beautyimage;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.fcapp.beautyimage.appdata.Constant;
import com.fcapp.beautyimage.appdata.MyApplication;
import com.fcapp.beautyimage.model.Home;
import com.fcapp.beautyimage.model.HomePopup;
import com.fcapp.beautyimage.model.RequestModel;
import com.fcapp.beautyimage.parser.AllClassifyParser;
import com.fcapp.beautyimage.parser.HomePopupParser;
import com.fcapp.beautyimage.util.ImageUtil;
import com.fcapp.beautyimage.util.Logger;
import com.fcapp.beautyimage.util.ProgressBarUtil;
import com.fcapp.beautyimage.util.SystemInfo;
import com.fcapp.beautyimage.util.ImageUtil.ImageCallback;
import com.fcapp.beautyimage.widget.WaterFallModule;

public class HomeActivity extends BaseActivity implements ClickListener{
	
	private ViewPager poster;
	private ImageView popupImage;
	private int popup_finish = 0225100;
	private HomePopup popup;
	private SharedPreferences sp;
	private Editor edit;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case 0225100:
				Bitmap bitmap = (Bitmap)msg.obj;
				showPopup(bitmap);
				break;

			default:
				break;
			}
		};
	};
	@Override
	protected void loadViewLayout() {
		TelephonyManager tm= (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
		MyApplication.DEVICE_ID = tm.getDeviceId();
		setContentView(R.layout.home);
		sp = getSharedPreferences("config" , MODE_PRIVATE);
		edit = sp.edit();
		boolean isExist = SystemInfo.existSDcard();
		if(!isExist){
			Toast.makeText(this, "存储卡未挂载，部分功能无法使用", 0).show();
			return;
		}
	}

	@Override
	protected void findViewById() {
		poster = (ViewPager) findViewById(R.id.vp);
	}

	@Override
	protected void setListtener() {

	}

	@Override
	protected void processLoggeric() {
		getHomeDataFromServer();
		
		//判断是否显示广告
		Calendar c = Calendar.getInstance();
	    String preTime = sp.getString("preTime", "");
	    Logger.i("上次显示广告的时间", preTime);
		boolean isShow = isShowPopup(preTime,c);
		if(isShow){
			ProgressBarUtil.hideLoading();
			getHomePopup();
		}
	}
	
	private boolean isShowPopup(String preTime,Calendar nowTime) {
		int mYear = nowTime.get(Calendar.YEAR); //获取当前年份 
	    int mMonth = nowTime.get(Calendar.MONTH);//获取当前月份 
	    int mDay = nowTime.get(Calendar.DAY_OF_MONTH);//获取当前月份的日期号码 
	    String n = mYear+","+mMonth+","+mDay;
	    if(TextUtils.isEmpty(preTime)){
	    	
	    	Logger.i("本次存储的时间是", n);
	    	edit.putString("preTime", n);
	    	edit.commit();
	    	return true;
	    }
	    String[] p = preTime.split(",");
	    int y = Integer.parseInt(p[0]);
	    if(y<mYear){
	    	Logger.i("本次存储的时间是", n);
	    	edit.putString("preTime", n);
	    	edit.commit();
	    	return true;
	    }
	    if(y == mYear){
	    	int m = Integer.parseInt(p[1]);
	    	if(m<mMonth){
	    		Logger.i("本次存储的时间是", n);
		    	edit.putString("preTime", n);
		    	edit.commit();
	    		return true;
	    	}
	    	if(m == mMonth){
	    		int d = Integer.parseInt(p[2]);
	    		if(d<mDay){
	    			Logger.i("本次存储的时间是", n);
	    	    	edit.putString("preTime", n);
	    	    	edit.commit();
	    			return true;
	    		}
	    		if(d == mDay){
	    			return false;
	    		}
	    		if(d>mDay){
	    			return false;
	    		}
	    	}
	    	if(m>mMonth){
	    		return false;
	    	}
	    }
	    if(y>mYear){
	    	return false;
	    }
	    Logger.i("本次存储的时间是", n);
    	edit.putString("preTime", n);
    	edit.commit();
	    return true;
	}

	private void getHomeDataFromServer() {
		RequestModel model = new RequestModel();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("device_id", MyApplication.DEVICE_ID);
		model.requestDataMap = params;
		model.context = this;
		File root_dir = Environment.getExternalStorageDirectory();
		model.jsonName = root_dir+Constant.JSON_CACHE_DIR+"home_waterfall_json_data";
		model.jsonParser = new AllClassifyParser(getApplicationContext(),model.jsonName);
		model.requestUrl = R.string.home_api;
		model.requestMethod = Constant.GET;
		model.mark = R.string.home_waterfall_data;
		model.eTag =sp.getString(model.jsonName, "");
		
		getDataFromServer(model, callBack);
	}
	
	private void getHomePopup(){
		
		RequestModel model = new RequestModel();
		HashMap<String, String> params = new HashMap<String, String>();
		
		params.put("device_id", MyApplication.DEVICE_ID);
		params.put("type","popup");
		model.requestDataMap = params;
		model.context = this;
		File root_dir = Environment.getExternalStorageDirectory();
		model.jsonName = root_dir +Constant.JSON_CACHE_DIR+"/home_popup_json_data.js";
		model.jsonParser = new HomePopupParser(getApplicationContext(),model.jsonName);
		model.requestUrl = R.string.home_popup;
		model.requestMethod = Constant.GET;
		model.mark = R.string.home_popup_data;
		model.eTag = getString(R.string.no_need_eTag);
		model.host = getString(R.string.app_host_popup);
		getDataFromServer(model, callBack);
	}
	
	private DataCallback callBack = new DataCallback<Object>(){
		
		@Override
		public void processData(Object paramObject, int ObjectMark) {
			
			if(paramObject == null){
				Toast.makeText(HomeActivity.this, "网络超时", 0).show();
				return;
			}
			switch (ObjectMark) {
			case R.string.home_waterfall_data:
				ArrayList<Home> list = (ArrayList<Home>) paramObject;
				fillWaterFall(list);
				break;
			case R.string.home_popup_data:
				popup = (HomePopup) paramObject;
				Logger.i("广告的参数", popup.toString());
				Handler handler1 = new Handler(){
					public void handleMessage(Message msg) {
						Bitmap bm = (Bitmap) msg.obj;
						Logger.i("bm == ?", bm==null?"bm==null":"bm!=null");
						if(bm != null){
							Message msg1 = Message.obtain();
					        msg1.what = popup_finish;
					        msg1.obj = bm;
					        handler.sendMessageDelayed(msg1, 3000);
						}
					};
				};
				ImageUtil.getImageFromNet(popup,handler1);
				break;
			}
		}
	};
	
	private ImageCallback ImageCallback = new ImageCallback() {
        @Override
        public void loadImage(Bitmap bitmap, String imagePath) {
           Message msg = Message.obtain();
           msg.what = popup_finish;
           msg.obj = bitmap;
           handler.sendMessage(msg);
           Logger.i("ImageCallback", "网络加载  执行了ImageCallback方法");
        }

    };
	
    
	private AlertDialog dlg;
	private void showPopup(Bitmap bitmap) {
			  dlg = new AlertDialog.Builder(this).create();
			  dlg.show();
			  Window window = dlg.getWindow();
			  Logger.i("广告图片的宽高是： ", bitmap.getWidth()+"   "+bitmap.getHeight());
			  View view = View.inflate(this, R.layout.popup, null);
			  window.setContentView(view);
			  popupImage = (ImageView) view.findViewById(R.id.iv_home_popup);
			  ImageButton popupClose = (ImageButton) view.findViewById(R.id.ib_home_close_popup);
			  popupImage.setOnClickListener(this);
			  popupClose.setOnClickListener(this);
			  BitmapDrawable bd=new BitmapDrawable(bitmap); 
			  //控制显示的大小
			  window.setLayout(bitmap.getWidth(), bitmap.getHeight());
			  popupImage.setBackgroundDrawable(bd);
			  Logger.i("showPopup", "执行了showPopup方法");
	}
	
	private void fillWaterFall(ArrayList<Home> list){
		ProgressBarUtil.hideLoading();
		WaterFallModule wfm = new WaterFallModule(this,list,this,getApplicationContext(),true);
		wfm.initLayout();
	}
	
	
	private void getHomeBanner(){
		//type=banner
		RequestModel model = new RequestModel();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("device_id", MyApplication.DEVICE_ID);
		params.put("type", "banner");
		model.requestDataMap = params;
		model.context = this;
		File root_dir = Environment.getExternalStorageDirectory();
		model.jsonName = root_dir +Constant.JSON_CACHE_DIR+"/home_banner_json_data.js";
		model.jsonParser = new AllClassifyParser(getApplicationContext(),model.jsonName);
		model.requestUrl = R.string.home_banner_api;
		model.requestMethod = Constant.GET;
		model.mark = R.string.home_waterfall_data;
		model.eTag = sp.getString(model.jsonName, " ");

		getDataFromServer(model, callBack);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_home_popup:
			//连接
			Intent intent = new Intent();        
			intent.setAction("android.intent.action.VIEW");
			Uri content_url = Uri.parse(popup.getClick_url());   
			intent.setData(content_url);  
			startActivity(intent);
			dlg.dismiss();
			ProgressBarUtil.hideLoading();
			break;
		case R.id.ib_home_close_popup:
			dlg.dismiss();
			ProgressBarUtil.hideLoading();
			break;
		
		}
		super.onClick(v);
	}

	@Override
	public void onShortClickImage(View v) {
		Home home = (Home) v.getTag();
		String tagName = home.getName();
		Intent secondIntet = new Intent();
		secondIntet.putExtra("TAG_NAME", tagName);
		Logger.i("TAG_NAME", tagName);
		secondIntet.setClass(this, SecondActivity.class);
		startActivity(secondIntet);
	}

	@Override
	public void onLongClickImage(View v) {
//		Toast.makeText(this, "长按了", 0).show();
	}
	
	private long exitTime = 0; 
    @Override 
    public boolean onKeyDown(int keyCode, KeyEvent event) { 
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){                   
        if((System.currentTimeMillis()-exitTime) > 2000){ 
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();                          
            exitTime = System.currentTimeMillis(); 
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
