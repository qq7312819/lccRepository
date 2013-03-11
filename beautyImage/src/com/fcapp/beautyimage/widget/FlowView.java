package com.fcapp.beautyimage.widget;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

import com.fcapp.beautyimage.NewestActivity;
import com.fcapp.beautyimage.SecondActivity;
import com.fcapp.beautyimage.ThirdActivity;
import com.fcapp.beautyimage.appdata.Constant;
import com.fcapp.beautyimage.util.FileUtil;
import com.fcapp.beautyimage.util.Logger;

public class FlowView extends ImageView implements View.OnClickListener,
		View.OnLongClickListener {

	private Context context;
	public Bitmap bitmap;
//	private int columnIndex;// 图片属于第几列
//	private int rowIndex;// 图片属于第几行
	private String fileName;
	private int ItemWidth;
	private String image_local_dir;
	private String image_url;
	private String myId;
	private String title;
	private String activityMark;
	
	private String stack_id;
	private String text;
	private int view_count;
	private int like_count;
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStack_id() {
		return stack_id;
	}

	public void setStack_id(String stack_id) {
		this.stack_id = stack_id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getView_count() {
		return view_count;
	}

	public void setView_count(int view_count) {
		this.view_count = view_count;
	}

	public int getLike_count() {
		return like_count;
	}

	public void setLike_count(int like_count) {
		this.like_count = like_count;
	}

	
	public String getActivityMark() {
		return activityMark;
	}

	public void setActivityMark(String activityMark) {
		this.activityMark = activityMark;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMyId() {
		return myId;
	}

	public void setMyId(String myId) {
		this.myId = myId;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public String getImage_local_dir() {
		return image_local_dir;
	}

	public void setImage_local_dir(String image_local_dir) {
		this.image_local_dir = image_local_dir;
	}

	private Handler viewHandler;

	public FlowView(Context c, AttributeSet attrs, int defStyle) {
		super(c, attrs, defStyle);
		this.context = c;
	}

	public FlowView(Context c, AttributeSet attrs) {
		super(c, attrs);
		this.context = c;
	}

	
	public FlowView(Context c) {
		super(c);
		this.context = c;
	}

	private ClickListener clickListener;
	
	public void Init(ClickListener clickListener) {
		
		this.clickListener = clickListener;
		
		setOnClickListener(this);
		setOnLongClickListener(this);
		setAdjustViewBounds(true); // 保持宽高比
		
	}

	@Override
	public boolean onLongClick(View v) {
		Logger.d("FlowView", "LongClick");
		clickListener.onShortClickImage(v);
		Toast.makeText(context, "长按：" + getMyId(), Toast.LENGTH_SHORT).show();
		return true;
	}
	
	
	interface ClickListener{
		void onShortClickImage(View v);
		void onLongClickImage(View v);
	}
	
	public void onClickImage(ClickListener click,View v){
		click.onShortClickImage(v);
	}
	
	public void onLongClickImage(ClickListener click,View v){
		click.onLongClickImage(v);
	}

	@Override
	public void onClick(View v) {
		Logger.d("FlowView", "Click");
		Logger.d("FlowView", "Click"+getFileName());
		Logger.d("FlowView", "Click"+getActivityMark());
		clickListener.onShortClickImage(v);
		
		if("HomeActivity".equals(getActivityMark())){
			Intent secondActivity = new Intent(context,SecondActivity.class);
			secondActivity.putExtra("TAG_NAME", getFileName());
			context.startActivity(secondActivity);
		}
		if("SecondActivity".equals(getActivityMark())){
			Intent thirdIntent = new Intent(context,ThirdActivity.class);
			thirdIntent.putExtra("id", getMyId());
			context.startActivity(thirdIntent);
		}
		if("zxtu".equals(getMyId())){
			Intent newestActivity = new Intent(context,NewestActivity.class);
			context.startActivity(newestActivity);
		}
	}
	/**
	 * 加载图片
	 */
	public void LoadImage() {
		Logger.d("getImage_local_dir", getImage_local_dir());
		bitmap = getImageFromLocal(getImage_local_dir());
		if (bitmap != null) {
			Logger.i("加载图片", "本地加载");
			int width = bitmap.getWidth();// 获取真实宽高
			int height = bitmap.getHeight();
			LayoutParams lp = getLayoutParams();
			int layoutHeight = (height * getItemWidth()) / width;// 调整高度
			if (lp == null) {
				lp = new LayoutParams(getItemWidth(), layoutHeight);
			}
			setLayoutParams(lp);

			setImageBitmap(bitmap);
			Handler h = getViewHandler();
			Message m = h.obtainMessage(Constant.HANDLER_WHAT, width,
					layoutHeight, FlowView.this);
			h.sendMessage(m);
		} else {
			new LoadImageThread().start();
		}

	}

	/**
	 * 重新加载图片
	 */
	public void Reload(String myId) {
		if (this.bitmap == null) {
			Logger.i("重新加载 getImage_local_dir", getImage_local_dir());
			bitmap = getImageFromLocal(getImage_local_dir());
			if (bitmap != null) {
				setImageBitmap(bitmap);
				int width = bitmap.getWidth();// 获取真实宽高
				int height = bitmap.getHeight();
				int layoutHeight = (height * getItemWidth()) / width;// 调整高度
				FlowView.this.setMyId(myId);
				FlowView.this.bitmap = bitmap;
				Handler h = getViewHandler();
				Message m = h.obtainMessage();
				m.obj = FlowView.this;
				Bundle b = new Bundle();
				b.putString("myId", myId);
				m.setData(b);
				m.what = Constant.RELOAD;
				h.sendMessage(m);
			} else {
				FlowView.this.setMyId(myId);
				new ReloadImageThread().start();
			}

		}
	}

	/**
	 * 回收内存
	 */
	public void recycle() {
		setImageBitmap(null);
		Logger.i("recycle image", "recycle image id:" + this.getId());
		if ((this.bitmap == null) || (this.bitmap.isRecycled()))
			return;
		this.bitmap.recycle();
		this.bitmap = null;
		
	}

//	public int getColumnIndex() {
//		return columnIndex;
//	}
//
//	public void setColumnIndex(int columnIndex) {
//		this.columnIndex = columnIndex;
//	}
//
//	public int getRowIndex() {
//		return rowIndex;
//	}
//
//	public void setRowIndex(int rowIndex) {
//		this.rowIndex = rowIndex;
//	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getItemWidth() {
		return ItemWidth;
	}

	public void setItemWidth(int itemWidth) {
		ItemWidth = itemWidth;
	}

	public Handler getViewHandler() {
		return viewHandler;
	}

	public FlowView setViewHandler(Handler viewHandler) {
		this.viewHandler = viewHandler;
		return this;
	}

	class ReloadImageThread extends Thread {

		@Override
		public void run() {

			getImageFromServer();

		}
	}

	class LoadImageThread extends Thread {

		public void run() {

			try {
				Logger.e("getImage_url", getImage_url());
				
				URL url = new URL(getImage_url());

				URLConnection conn = url.openConnection();
				conn.connect();
				BufferedInputStream bis = new BufferedInputStream(
						conn.getInputStream(), 8192);
				bitmap = BitmapFactory.decodeStream(bis);
				// bitmap = addString(bitmap,getFileName());
				Logger.e("图片加载从网络成功", getImage_url() + "   图片名字是:" + getFileName());
				// 保存文件到sd卡
				saveImage(getImage_local_dir(), bitmap2Bytes(bitmap));
				Logger.i("loadimagTHREAD", "执行了loadImage这个方法,本地没有发现 从网络加载了");
				
				((Activity) context).runOnUiThread(new Runnable() {
					public void run() {
						if (bitmap != null) {// 此处在线程过多时可能为null
							int width = bitmap.getWidth();// 获取真实宽高
							int height = bitmap.getHeight();

							LayoutParams lp = getLayoutParams();

							int layoutHeight = (height * getItemWidth())
										/ width;// 调整高度
							if (lp == null) {
								lp = new LayoutParams(getItemWidth(),
										layoutHeight);
							}
							setLayoutParams(lp);

							setImageBitmap(bitmap);
							Handler h = getViewHandler();
							Message m = h.obtainMessage(Constant.HANDLER_WHAT, width,
									layoutHeight, FlowView.this);
							h.sendMessage(m);
						}
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void getImageFromServer() {
		try {
			URL url = new URL(getImage_url());

			URLConnection conn = url.openConnection();
			conn.connect();
			BufferedInputStream bis = new BufferedInputStream(
					conn.getInputStream(), 8192);
			bitmap = BitmapFactory.decodeStream(bis);
			// bitmap = addString(bitmap,getFileName());
			Logger.e("图片加载从网络成功", getImage_url() + "   图片名字是:" + getFileName());
			// 保存文件到sd卡
			saveImage(getImage_local_dir(), bitmap2Bytes(bitmap));

			int width = bitmap.getWidth();// 获取真实宽高
			int height = bitmap.getHeight();
			LayoutParams lp = getLayoutParams();
			int layoutHeight = (height * getItemWidth()) / width;// 调整高度
			if (lp == null) {
				lp = new LayoutParams(getItemWidth(), layoutHeight);
			}
			setLayoutParams(lp);
			FlowView.this.bitmap = bitmap;
			setImageBitmap(bitmap);
			FlowView.this.setMyId(getMyId());
			((Activity) context).runOnUiThread(new Runnable() {
				public void run() {
					if (bitmap != null) {// 此处在线程过多时可能为null
						setImageBitmap(bitmap);
					}
				}
			});
			Handler h = getViewHandler();
			Message m = h.obtainMessage();
			m.obj = FlowView.this;
			Bundle b = new Bundle();
			b.putString("myId", myId);
			m.setData(b);
			m.what = Constant.RELOAD;
			h.sendMessage(m);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存图片到SD卡
	 * 
	 * @param imagePath
	 * @param buffer
	 * @throws IOException
	 */
	public static void saveImage(String imagePath, byte[] buffer)
			throws IOException {
		File f = new File(imagePath);
		if (f.exists()) {
			return;
		} else {
			File parentFile = f.getParentFile();
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}
			FileUtil.createFile(imagePath);
			FileOutputStream fos = new FileOutputStream(imagePath);
			fos.write(buffer);
			fos.flush();
			fos.close();
		}
	}

	/**
	 * 从SD卡加载图片
	 * 
	 * @param imagePath
	 * @return
	 */
	public static Bitmap getImageFromLocal(String imagePath) {
		File file = new File(imagePath);
		if (file.exists()) {
			Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
			file.setLastModified(System.currentTimeMillis());
			// bitmap = addString(bitmap,title);
			return bitmap;
		}
		return null;
	}

	/**
	 * Bitmap转换到Byte[]
	 * 
	 * @param bm
	 * @return
	 */
	public static byte[] bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, bas);
		return bas.toByteArray();
	}

	// public static Bitmap addString(Bitmap src,String title){
	// // Options opts = new Options();
	// // opts.inScaled = true;
	// // Bitmap src = BitmapFactory.decodeResource(activity.getResources(),
	// R.drawable.bg, null);
	//
	// if(TextUtils.isEmpty(title)){
	// return src;
	// }
	//
	// int srcWidth = src.getWidth();
	// int srcHeight = src.getHeight();
	//
	// float f = 0.0334261838440111f;
	//
	// float ff = (float)srcWidth/(float)srcHeight/f;
	//
	//
	//
	// Logger.e("", "src=" + srcWidth + "," + srcHeight);
	// Bitmap fileBitmap = Bitmap.createBitmap(srcWidth, srcHeight,
	// Config.ARGB_8888);
	// Canvas canvas = new Canvas(fileBitmap);
	// Bitmap dstbmp = Bitmap.createBitmap(src, 0, 0, srcWidth, srcHeight);
	// canvas.drawBitmap(dstbmp, 0, 0, null);
	//
	// TextPaint paint = new TextPaint();
	// paint.setTextSize(ff);
	// paint.setLinearText(true);
	// paint.setAntiAlias(true);
	// paint.setColor(Color.RED);
	// canvas.drawText(title, 0, srcHeight-10, paint);
	//
	// canvas.save();
	// return fileBitmap;
	// }
}
