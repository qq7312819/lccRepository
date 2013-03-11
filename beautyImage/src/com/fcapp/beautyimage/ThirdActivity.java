package com.fcapp.beautyimage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fcapp.beautyimage.appdata.Constant;
import com.fcapp.beautyimage.appdata.MyApplication;
import com.fcapp.beautyimage.model.Home;
import com.fcapp.beautyimage.model.RequestModel;
import com.fcapp.beautyimage.myinterface.OnBeautyLegClick;
import com.fcapp.beautyimage.parser.ThirdParser;
import com.fcapp.beautyimage.util.ImageUtil;
import com.fcapp.beautyimage.util.ImageUtil.ImageCallback;
import com.fcapp.beautyimage.util.Logger;
import com.fcapp.beautyimage.util.SystemInfo;
import com.fcapp.beautyimage.widget.MyViewPager;
import com.fcapp.beautyimage.widget.ViewPagerItemView;

public class ThirdActivity extends BaseActivity implements OnBeautyLegClick{

	private LinearLayout body_layout;
	private String id;
	private ArrayList<Home> list;
	private int currentDownloadNum = 0;
	private int width;
	private int height;
	private RelativeLayout top_rl,bottom;
	private MyViewPager vp;
	private TextView back;
	private int mark = 0;	//最新最热图片修改这个值，判断是否需要联网获取json
	private String TAG =  "ThirdActiviy";
	private TextView title,options;
	private PopupWindow window;
	private TextView currentNum;
	private Home home;
	MyReceiver receiver;
	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.third);
		
		MyApplication.ThirdActivityContext = this;
		receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("too_fast");
		registerReceiver(receiver, filter);
	}
	
	
	long time = 0;
	@Override
	protected void findViewById() {
		
		id = getIntent().getStringExtra("id");
		
//		bottom = (RelativeLayout)findViewById(R.id.rl_third_bottom);
		options = (TextView)findViewById(R.id.save_beauty);
		vp = (MyViewPager) findViewById(R.id.vp);
		

		body_layout = (LinearLayout) findViewById(R.id.body_layout);
		top_rl = (RelativeLayout) findViewById(R.id.rl_top);
		back = (TextView) findViewById(R.id.tv_back);
//		title = (TextView) findViewById(R.id.tv_third_collect);
		currentNum = (TextView) findViewById(R.id.tv_third_top);
		
		
		Display display = getWindowManager().getDefaultDisplay();
	    width = display.getWidth();
	    height = display.getHeight();
	    if("1".equals(id)){
			list = modifyData("zxtp");
			MyAdapter myAdapter = new MyAdapter(list,title);
			currentNum.setText(1+" of "+list.size());
			home = list.get(0);
			vp.setAdapter(myAdapter);
			mark = 1;
		}
		if("2".equals(id)){
			list = modifyData("zrtp");
			MyAdapter myAdapter = new MyAdapter(list,title);
			currentNum.setText(1+" of "+list.size());
			home = list.get(0);
			vp.setAdapter(myAdapter);
			mark = 1;
		}
	}

	public ArrayList<Home> modifyData(String which) {
		ArrayList<Home> list = (ArrayList<Home>) getIntent().getSerializableExtra(which);
		for(Home home :list){
			String src = home.getFile().getSrc();
			if(src.contains("!")){
				src = src.substring(0, src.lastIndexOf("!"));
				Logger.i("modifyData  修改后src是：", src);
			}
			title.setText(home.getTitle());
		}
		return list;
	}

	@Override
	protected void setListtener() {
		top_rl.setOnClickListener(this);
		vp.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				int ci = vp.getCurrentItem();
				currentNum.setText(ci+1+" of "+list.size());
				home = list.get(ci);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
		
		back.setOnClickListener(this);
		options.setOnClickListener(this);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		
		
		
		return super.dispatchTouchEvent(ev);
	}
	

	@Override
	protected void processLoggeric() {
		if(mark == 1)
			return;
		RequestModel model = new RequestModel();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("device_id", MyApplication.DEVICE_ID);
		params.put("id", id);
		model.requestDataMap = params;
		model.context = this;
		File root_dir = Environment.getExternalStorageDirectory();
		model.jsonName = root_dir + Constant.JSON_CACHE_DIR + "/" +id+ "_waterfall_json_data.js";
		model.jsonParser = new ThirdParser(getApplicationContext(),model.jsonName);
		model.requestUrl = R.string.third_api;
		model.requestMethod = Constant.GET;
		model.mark = R.string.third_data;
		model.eTag = sp.getString(model.jsonName, " ");
		getDataFromServer(model, callBack);
	}

	private DataCallback callBack = new DataCallback<Object>() {

		@Override
		public void processData(Object paramObject, int ObjectMark) {
			if (paramObject == null) {
				Toast.makeText(ThirdActivity.this, "返回的数据为空", 0).show();
			}
			switch (ObjectMark) {
			case R.string.third_data:
				list = (ArrayList<Home>) paramObject;
				MyAdapter myAdapter = new MyAdapter(list,title);
				currentNum.setText(1+" of "+list.size());
				home = list.get(0);
				vp.setAdapter(myAdapter);
				break;
			}
		}
	};
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.vp:
			break;
		case R.id.tv_back:
			Logger.i("终于点击了", "点击了点击了点击了");
			finish();
			break;
		case R.id.save_beauty:
			
						boolean isExist = SystemInfo.existSDcard();
						if(!isExist){
							Toast.makeText(ThirdActivity.this, "此功能暂不可用", 0).show();
							return;
						}
						String dir = home.getImg_local_dir();
						File file = new File(dir);
						if(!file.exists()){
							ImageUtil.loadImage(
									dir, 
									home.getFile().getSrc(), 
									new ImageCallback() {
								
									@Override
									public void loadImage(Bitmap bitmap, String imagePath) {
										Toast.makeText(ThirdActivity.this, "保存成功", 1).show();
									}
									},
									home.getFile().getWidth(), 
									home.getFile().getHeight());
						}
						Toast.makeText(ThirdActivity.this, "图片保存在"+dir, 1).show();
			break;
		case R.id.tv_tv_options_menu_save:
			dismissPopWindow();
			break;
		}
	};
	


	class MyAdapter extends PagerAdapter{
		
		private ArrayList<Home> list;
		private TextView title;
		private HashMap<Integer, ViewPagerItemView> mHashMap;
		public MyAdapter(ArrayList<Home> list,TextView title){
			this.list = list;
			this.title = title;
			mHashMap = new HashMap<Integer, ViewPagerItemView>();
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0==arg1;
		}
		
		@Override
		public void destroyItem(ViewGroup view, int position, Object object) {
			ViewPagerItemView itemView = (ViewPagerItemView)object;
			itemView.recycle();
		}
		
		@Override
		public Object instantiateItem(final ViewGroup view, int position) {
			ViewPagerItemView itemView;
			if(mHashMap.containsKey(position)){
				itemView = mHashMap.get(position);
				itemView.reload();
			}else{
				Home home = list.get(position);
				itemView = new ViewPagerItemView(ThirdActivity.this,home,ThirdActivity.this);
				itemView.setData(title);
				mHashMap.put(position, itemView);
				view.addView(itemView);
			}
			
			return itemView;
		}
	}
	
	@Override
	protected void onDestroy() {
		dismissPopWindow();
		System.gc();
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	public void dismissPopWindow() {
		if (window != null && window.isShowing()) {
			window.dismiss();
			window = null;
		}
	}

	@Override
	public void onBeautyLegLongClick(View v) {
		
	}

	
	boolean isVisiable = false;
	@Override
	public void onBeautyLegClick(View v) {
		dismissPopWindow();
		if(!isVisiable){
			top_rl.setVisibility(View.GONE);
			bottom.setVisibility(View.INVISIBLE);
			isVisiable = true;
		}else{
			top_rl.setVisibility(View.VISIBLE);
			bottom.setVisibility(View.VISIBLE);
			isVisiable = false;
		}
		
	}
	
	
public void showOptionsDiaLog(){
	
	 final AlertDialog dlg = new AlertDialog.Builder(this).create();
	 dlg.setCancelable(true);
	  dlg.show();
	  Window window = dlg.getWindow();
	  View view = View.inflate(this, R.layout.options_menu, null);
	  window.setContentView(view);
	  Button save = (Button) view.findViewById(R.id.tv_tv_options_menu_save);
	  save.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			dlg.dismiss();
			String dir = home.getImg_local_dir();
			File file = new File(dir);
			if(!file.exists()){
				ImageUtil.loadImage(
						dir, 
						home.getFile().getSrc(), 
						new ImageCallback() {
					
						@Override
						public void loadImage(Bitmap bitmap, String imagePath) {
							Toast.makeText(ThirdActivity.this, "保存成功", 1).show();
						}
						},
						home.getFile().getWidth(), 
						home.getFile().getHeight());
			}
			Toast.makeText(ThirdActivity.this, "图片保存在"+dir, 0).show();
		}
	});
	}

	
class MyReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Toast.makeText(ThirdActivity.this, "是男人就要坚持两秒哦！ ↖(^ω^)↗", 0).show();
	}
	
}
}
