package com.fcapp.beautyimage.widget;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fcapp.beautyimage.R;
import com.fcapp.beautyimage.ThirdActivity;
import com.fcapp.beautyimage.appdata.Constant;
import com.fcapp.beautyimage.appdata.MyApplication;
import com.fcapp.beautyimage.model.Home;
import com.fcapp.beautyimage.myinterface.OnBeautyLegClick;
import com.fcapp.beautyimage.util.FileUtil;
import com.fcapp.beautyimage.util.ImageUtil;
import com.fcapp.beautyimage.util.LoadImageAsyncTask;
import com.fcapp.beautyimage.util.SystemInfo;
import com.fcapp.beautyimage.util.LoadImageAsyncTask.LoadImageAsynTaskCallBack;
import com.google.gson.Gson;

/**
 * @author frankiewei
 * ����ItemView,�Զ���View.���㸴��.
 */
public class ViewPagerItemView extends FrameLayout  {

	/**
	 * ͼƬ��ImageView.
	 */
	private ImageView beautyLeg,bottom;
	private ImageView collect;
	private OnBeautyLegClick beautyLegClick;
	/**
	 * ͼƬ���ֵ�TextView.
	 */
	private TextView name;
	private boolean isCollect = false;
	
	/**
	 * ͼƬ��Bitmap.
	 */
	private Bitmap mBitmap;
	
//	private LoadImageAsyncTask task;
	/**
	 * Ҫ��ʾͼƬ��JSONOBject��.
	 */
	private Home home;
	private Context context;
	
	public ViewPagerItemView(Context context,Home home,OnBeautyLegClick beautyLegClick){
		super(context);
		setupViews();
		this.context = context;
		this.home = home;
		this.beautyLegClick = beautyLegClick;
		
	}
	
	public ViewPagerItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupViews();
	}
	
	private void setupViews(){
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.viewpager_itme, null);
		
		collect = (ImageView)view.findViewById(R.id.iv_collect);
		beautyLeg = (ImageView)view.findViewById(R.id.iv_beauty_leg);
		beautyLeg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				beautyLegClick.onBeautyLegClick(v);
			}
		});
		
		collect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isCollect = home.isCollect();
				ImageView iv = (ImageView) v;
				
				boolean isExist = SystemInfo.existSDcard();
				if(!isExist){
					Toast.makeText(context, "此功能暂不可用", 0).show();
					return;
				}
				
				if(!isCollect){
					Resources res=context.getResources();
					Bitmap bitmap=BitmapFactory.decodeResource(res, R.drawable.collect_yes);
					iv.setImageBitmap(bitmap);
					isCollect = true;
					home.setCollect(true);
					//设置收藏界面显示的高度
					Bitmap bigBitmap = (Bitmap) v.getTag();
					
					int layoutHeight = (bigBitmap.getHeight() * MyApplication.screntWidth/2)/ bigBitmap.getWidth();// 调整高度
					home.getFile().setHeight(layoutHeight);
					
					MyApplication.collectImage.add(home);
//					Toast.makeText(context, "加入收藏  "+home.toString(), 1).show();
					saveCollect(MyApplication.collectImage);
				}else{
					Resources res=context.getResources();
					Bitmap bitmap=BitmapFactory.decodeResource(res, R.drawable.collect_no);
					iv.setImageBitmap(bitmap);
					isCollect = false;
					home.setCollect(false);
					MyApplication.collectImage.remove(home);
					if(MyApplication.collectImage.size() == 0){
						File file = new File(Constant.COLLECT_DIR+"/MyCollect.js");
						FileUtil.deleteFile(file);
					}else{
						saveCollect(MyApplication.collectImage);
//						Toast.makeText(context, "移除收藏  "+home.toString(), 1).show();
					}
				}
			}
		});
		addView(view);
	}

	public void setData(TextView title){
			collect.setTag(home);
			String name = home.getTitle();
			LoadImageAsyncTask task = new LoadImageAsyncTask(new ImageTask(), home,context,new Handler());
			task.execute(home.getFile().getSrc());
			title.setText(name);
			if(home.isCollect()){
				collect.setImageDrawable(context.getResources().getDrawable(R.drawable.collect_yes));
			}else{
				collect.setImageDrawable(context.getResources().getDrawable(R.drawable.collect_no));
			}
	}
		
	public void recycle(){
		ImageUtil.recycleImageViewBitMap(beautyLeg);
	}
	
	
	/**
	 * ���¼���.�ⲿ����.
	 */
	public void reload(){
			String url = home.getFile().getSrc();
			LoadImageAsyncTask task = new LoadImageAsyncTask(new ImageTask(), home,context,new Handler());
			task.execute(url);
	}
	
	class ImageTask implements LoadImageAsynTaskCallBack{

		@Override
		public void beforeImageLoad() {
			beautyLeg.setBackgroundColor(Color.TRANSPARENT);
			collect.setClickable(false);
		}

		@Override
		public void onImageLoaded(Bitmap bitmap) {
			collect.setClickable(true);
			collect.setTag(bitmap);
			beautyLeg.setImageBitmap(bitmap);
			
		}
	}
	
	private void saveCollect(ArrayList<Home> collect) {
		Gson gson = new Gson();
		String collecStr = gson.toJson(collect);
		FileUtil.writeFile(collecStr, Constant.COLLECT_DIR+"/MyCollect.js");
	}
}
