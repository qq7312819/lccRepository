package com.fcapp.beautyimage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fcapp.beautyimage.ThirdActivity.MyReceiver;
import com.fcapp.beautyimage.appdata.Constant;
import com.fcapp.beautyimage.appdata.MyApplication;
import com.fcapp.beautyimage.model.Home;
import com.fcapp.beautyimage.model.MyImage;
import com.fcapp.beautyimage.model.RequestModel;
import com.fcapp.beautyimage.parser.ThirdParser;
import com.fcapp.beautyimage.util.FileUtil;
import com.fcapp.beautyimage.util.GestureSlideExt;
import com.fcapp.beautyimage.util.ImageUtil;
import com.fcapp.beautyimage.util.LoadImageAsyncTask;
import com.fcapp.beautyimage.util.SystemInfo;
import com.fcapp.beautyimage.util.ImageUtil.ImageCallback;
import com.fcapp.beautyimage.util.LoadImageAsyncTask.LoadImageAsynTaskCallBack;
import com.fcapp.beautyimage.util.Logger;
import com.fcapp.beautyimage.util.ProgressBarUtil;
import com.fcapp.beautyimage.widget.MyImageView;
import com.google.gson.Gson;

import dalvik.system.VMRuntime;

public class ThirdImageViewActivity extends BaseActivity {

	private GestureDetector mGestureDetector;
	private final static float TARGET_HEAP_UTILIZATION = 0.65f;  
	 private final static int CWJ_HEAP_SIZE = 10* 1024* 1024 ; 
	private MyImageView bigImage;
	private ImageView collect;
	private TextView title,back,save;
	private ArrayList<Home> list;
	private int position = 0;
	private String id;
	private int mark = 0;	//最新最热图片修改这个值，判断是否需要联网获取json
	private static String TAG =  "ThirdImageViewActivity";
	private RelativeLayout topBar;
	MyReceiver receiver;
	private boolean isCollect = false;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1314:
				ProgressBarUtil.showLoading(ThirdImageViewActivity.this, "加载中");
				break;

			default:
				break;
			}
		};
	};
	
	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.third);
		id = getIntent().getStringExtra("id");
		 dalvik.system.VMRuntime.getRuntime().setTargetHeapUtilization(TARGET_HEAP_UTILIZATION);
		
		 //设置最小heap内存为6MB大小 
		 VMRuntime.getRuntime().setMinimumHeapSize(CWJ_HEAP_SIZE); 
	}

	

	@Override
	protected void findViewById() {
		bigImage = (MyImageView) findViewById(R.id.iv_third_big_image);
		title = (TextView) findViewById(R.id.tv_third_top);
		topBar  = (RelativeLayout) findViewById(R.id.rl_top);
		back  = (TextView) findViewById(R.id.tv_back);
		save  = (TextView) findViewById(R.id.save_beauty);
		collect = (ImageView) findViewById(R.id.iv_collect);
		
		
		if("1".equals(id)){
			list = modifyData("zxtp");
			setData(position,list.get(position).isCollect());
			setImage( list, position);
			addGesture();
			mark = 1;
		}
		if("2".equals(id)){
			list = modifyData("zrtp");
			setData(position,list.get(position).isCollect());
			setImage( list, position);
			addGesture();
			mark = 1;
		}
		if("3".equals(id)){
			list = modifyData("zxtp");
			setData(position,list.get(position).isCollect());
			setImage( list, position);
			addGesture();
			mark = 1;
		}
	}

	@Override
	protected void setListtener() {
		back.setOnClickListener(this);
		save.setOnClickListener(this);
		collect.setOnClickListener(this);
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
				Toast.makeText(ThirdImageViewActivity.this, "返回的数据为空", 0).show();
			}
			switch (ObjectMark) {
			case R.string.third_data:
				list = (ArrayList<Home>) paramObject;
				setData(position,list.get(position).isCollect());
				setImage( list, position);
				addGesture();
				break;
			}
		}
	};
	
	
	private void setImage(ArrayList<Home> list, int position) {
		collect.setClickable(false);
		Home home = list.get(position);
		collect.setTag(R.string.btn_collect, home);
		LoadImageAsyncTask task = new LoadImageAsyncTask(new ImageTask(), home,this,handler);
		task.execute(home.getFile().getSrc());
	}
	Timer timer = new Timer();
	class ImageTask implements LoadImageAsynTaskCallBack{

		@Override
		public void beforeImageLoad() {
			changeMark = 1;
			
//			timer.schedule(new TimerTask() {
//				
//				@Override
//				public void run() {
//					if(changeMark == 1){
//						changeMark = 0;
//					}
//					
//				}
//			}, 300000);
		}

		@Override
		public void onImageLoaded(Bitmap bitmap) {
			collect.setClickable(true);
			timer.cancel();
			timer.purge();
			ProgressBarUtil.hideLoading();
			bigImage.setImageBitmap(bitmap);
			collect.setTag(bitmap);
			changeMark = 0;
			
			String s = position+1+"";
			if(Integer.parseInt(s)>list.size()-1){
				return;
			}
			prestrain(list.get(Integer.parseInt(s)));
		}
	}
	
	private void getPreImage(ArrayList<Home> list, int position){
		setData(position,list.get(position).isCollect());
		setImage( list, position);
	}
	private void getNextImage(ArrayList<Home> list, int position){
		setData(position,list.get(position).isCollect());
		setImage( list, position);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event == null){
			return true;
		}else{
			return mGestureDetector.onTouchEvent(event);
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
		}
		return list;
	}
	
	private int changeMark = 0;
	
	public void addGesture() {
		mGestureDetector = new GestureSlideExt(this, new GestureSlideExt.OnGestureResult() { 
            @Override 
            public void onGestureResult(int direction) { 
                // 业务逻辑处理 
                switch (direction) { 
                case GestureSlideExt.GESTURE_UP: 
                    
                    break; 
                case GestureSlideExt.GESTURE_RIGHT: 	//向右滑动
                	if(!isEnable(changeMark)){
                		return;
                	}
                	position--;
                	Logger.i("GESTURE_RIGHT", position+"");
                	if(position < 0){
                		position = list.size()-1;
                	}
                	
                	getPreImage(list,position);
                	
                    break; 
                case GestureSlideExt.GESTURE_DOWN: 
                	
                    break; 
                case GestureSlideExt.GESTURE_LEFT: 
                	if(!isEnable(changeMark)){
                		return;
                	}
                	position++;
                	Logger.i("GESTURE_LEFT", position+"");
                	if(position >= list.size()){
                		position = 0;
                	}
                	getNextImage(list, position);
                    break;
                case GestureSlideExt.GESTURE_CLICK:
                	if(topBar.getVisibility() == View.GONE){
        				topBar.setVisibility(View.VISIBLE);
        			}else{
        				topBar.setVisibility(View.GONE);
        			}
                	break;
                } 
            } 
        }).Buile();
	}
	
	private boolean isEnable(int i){
		if(i == 0){
			return true;
		}
		Toast.makeText(this, "一秒你也不行么？-_-|||", 0).show();
		return false;
	}
	
	private void setData(int position,boolean isCollect){
		title.setText(position + 1 +" of "+ list.size());
		if(TextUtils.isEmpty(id) || !id.equals("3")){
			collect.setVisibility(View.VISIBLE);
			if(isCollect){
				setResources(collect,R.drawable.collect_yes);
			}else{
				setResources(collect,R.drawable.collect_no);
			}
		}else{
			collect.setVisibility(View.INVISIBLE);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_back:
			finish();
			break;
		case R.id.save_beauty:
			Home home = list.get(position);
			saveImage(home);
			break;
		case R.id.iv_collect:
			collectImage(list.get(position),v);
			break;

		default:
			break;
		}
		super.onClick(v);
	}
	
	public void saveImage(Home home){
		boolean isExist = SystemInfo.existSDcard();
		if(!isExist){
			Toast.makeText(this, "此功能暂不可用", 0).show();
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
						Toast.makeText(ThirdImageViewActivity.this, "保存成功", 1).show();
					}
					},
					home.getFile().getWidth(), 
					home.getFile().getHeight());
		}
		Toast.makeText(this, "图片保存在"+dir, 1).show();
	}
	
	public void collectImage(Home home,View v){

		isCollect = home.isCollect();
		ImageView iv = (ImageView) v;
		
		boolean isExist = SystemInfo.existSDcard();
		if(!isExist){
			Toast.makeText(this, "此功能暂不可用", 0).show();
			return;
		}
		
		if(!isCollect){
			setResources(iv,R.drawable.collect_yes);
			isCollect = true;
			home.setCollect(true);
			//设置收藏界面显示的高度
			Bitmap bigBitmap = (Bitmap) v.getTag();
			Home homesrc = (Home) v.getTag(R.string.btn_collect);
			int layoutHeight = (bigBitmap.getHeight() * MyApplication.screntWidth/2)/ bigBitmap.getWidth();// 调整高度
			MyImage cover_img = new MyImage();
			cover_img.setHeight(layoutHeight);
			cover_img.setWidth(MyApplication.screntWidth/2);
			cover_img.setSrc(home.getFile().getSrc());
			home.setCover_img(cover_img);
			
			MyApplication.collectImage.add(home);
			saveCollect(MyApplication.collectImage);
		}else{
			setResources(iv,R.drawable.collect_no);
			isCollect = false;
			home.setCollect(false);
			MyApplication.collectImage.remove(home);
			if(MyApplication.collectImage.size() == 0){
				File file = new File(Constant.COLLECT_DIR+"/MyCollect.js");
				FileUtil.deleteFile(file);
			}else{
				saveCollect(MyApplication.collectImage);
			}
		}
	}
	
	/**
	 * 预加载下一张图片
	 * 
	 */
	public static void prestrain(Home home ){
		File file = new File(home.getImg_local_dir());
		if(!file.exists()){
			ImageUtil.getNextImage(home);
			Logger.i(TAG+"prestrain", "执行了预加载了");
		}
	}


	public void setResources(ImageView iv,int resID) {
		Resources res=this.getResources();
		Bitmap bitmap=BitmapFactory.decodeResource(res, resID);
		iv.setImageBitmap(bitmap);
	}
	private void saveCollect(ArrayList<Home> collect) {
		Gson gson = new Gson();
		String collecStr = gson.toJson(collect);
		FileUtil.writeFile(collecStr, Constant.COLLECT_DIR+"/MyCollect.js");
	}
	
	@Override
	protected void onDestroy() {
		if(timer != null){
			timer.cancel();
			timer.purge();
			timer = null;
		}
		
		super.onDestroy();
	}
}
