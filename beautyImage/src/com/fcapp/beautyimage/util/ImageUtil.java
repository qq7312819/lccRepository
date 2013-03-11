package com.fcapp.beautyimage.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.fcapp.beautyimage.model.Home;
import com.fcapp.beautyimage.model.HomePopup;

public class ImageUtil {
	private static final String SDCARD_CACHE_IMG_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/itcast/images/";

	private static final String TAG = "ImageUtil";

	/**
	 * 保存图片到SD卡
	 * 
	 * @param imagePath
	 * @param buffer
	 * @throws IOException
	 */
	public static void saveImage(String imagePath, byte[] buffer)
			throws IOException {
		boolean isExist = SystemInfo.existSDcard();
		if(!isExist){
			return;
		}
		File f = new File(imagePath);
		if (f.exists()) {
			return;
		} else {
			File parentFile = f.getParentFile();
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}
			f.createNewFile();
			FileOutputStream fos = new FileOutputStream(imagePath);
			fos.write(buffer);
			fos.flush();
			fos.close();
		}
	}
	
	public static void getImageFromNet(final HomePopup popup,final Handler handler){
			new Thread(){
				public void run() {
					try {
						Logger.i("广告图片的下载路径是: ", popup.getImg_url());
						URL url = new URL(popup.getImg_url());
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
						conn.setConnectTimeout(5000);
						int max = conn.getContentLength();
						InputStream is = conn.getInputStream();
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];
					int len;
					int total = 0;
					while ((len = is.read(buffer)) != -1) {
						baos.write(buffer, 0, len);
						total += len;
					}
					byte[] result = baos.toByteArray();
						Bitmap bm = BitmapFactory.decodeByteArray(result, 0, result.length);
						bm = zoomImg(bm,Integer.parseInt(popup.getWidth()),Integer.parseInt(popup.getHeight()));
						Message msg = Message.obtain();
						msg.obj = bm;
						handler.sendMessage(msg);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				};
			}.start();
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
		byte[] b = bas.toByteArray();
		return b;
	}
	
	 public static Bitmap getImageFromLocal( String imagePath, int imageWidth, int imageHeight) {
		 boolean isExist = SystemInfo.existSDcard();
			if(!isExist){
				return null;
			}
			final Bitmap bitmap = null;
			
			try {
				BitmapTool.getBitmap(imagePath,imageWidth,imageHeight);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			return bitmap;
	    }

	 
	 
	 
	/**
	 * 从本地或者服务端加载图片
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Bitmap loadImage(final String imagePath, final String imgUrl,
			final ImageCallback callback, final int imageWidth,final int imageHeight) {
		Bitmap bitmap = getImageFromLocal(imagePath,imageWidth,imageHeight);
		if (bitmap != null) {
			Logger.e("图片加载", "从本地加载成功");
			return bitmap;
		} else {// 从网上加载
			final Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					if (msg.obj != null) {
						Bitmap bitmap = (Bitmap) msg.obj;

						callback.loadImage(bitmap, imagePath);
					}
				}
			};

			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						byte[] result = getImageByte(imgUrl);
						Bitmap bm = getThumbnail(imageWidth,imageHeight, result);
						Logger.e("经过加工过的bitmap的 参数", "宽  "+bm.getWidth()+"   高  "+bm.getHeight());
						saveImage(imagePath, bitmap2Bytes(bm));
						Message msg = handler.obtainMessage();
						msg.obj = bm;
						handler.sendMessage(msg);
					} catch (IOException e) {
						e.printStackTrace();
						Logger.e(ImageUtil.class.getName(), "保存图片到本地存储卡出错！");
					}
				}

				
			};
			ThreadPoolManager.getInstance().addTask(runnable);
		}
		return null;
	}
	
	public static byte[] getImageByte(final String imgUrl)
			throws MalformedURLException, IOException {
		URL url = new URL(imgUrl);
		Logger.e("图片加载", imgUrl);
		URLConnection conn = url.openConnection();
		conn.connect();
		InputStream is = conn.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		int total = 0;
		while ((len = is.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
			total += len;
		}
		byte[] result = baos.toByteArray();
		baos.close();
		baos = null;
		return result;
	}
	
	public static Bitmap getThumbnail(final int imageWidth,int imageHeight, byte[] result) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeByteArray(result, 0, result.length, options);
		  float realWidth = options.outWidth;
		  float realHeight = options.outHeight;
		  float b = realWidth/realHeight;
		  Logger.i("图片真实的宽高是", ""+realWidth+"  "+realHeight);
		  if(realWidth<=imageWidth){
			 try {
				options.inJustDecodeBounds = false;
				 bitmap = BitmapFactory.decodeByteArray(result, 0, result.length, options);
				 bitmap = zoomImg(bitmap ,imageWidth,(int)imageHeight );
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		  }else{
		      options.inJustDecodeBounds = false;
		      options.inSampleSize = 3;
		      bitmap = BitmapFactory.decodeByteArray(result, 0, result.length, options);
//		      bitmap = zoomImg(bitmap,imageWidth,(int)imageHeight);
		  }
		return bitmap;
	}
	
	
	// 返回图片存到sd卡的路径
	public static String getCacheImgPath() {
		return SDCARD_CACHE_IMG_PATH;
	}

	public static String md5(String paramString) {
		String returnStr;
		try {
			MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
			localMessageDigest.update(paramString.getBytes());
			returnStr = byteToHexString(localMessageDigest.digest());
			return returnStr;
		} catch (Exception e) {
			return paramString;
		}
	}

	/**
	 * 将指定byte数组转换成16进制字符串
	 * 
	 * @param b
	 * @return
	 */
	public static String byteToHexString(byte[] b) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			hexString.append(hex.toUpperCase());
		}
		return hexString.toString();
	}
	

	/**
	 * @author Mathew
	 */
	public interface ImageCallback {
		public void loadImage(Bitmap bitmap, String imagePath);
	}

	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	public static void recycleBackgroundBitMap(ImageView view) {
		if (view != null) {
			BitmapDrawable bd = (BitmapDrawable) view.getBackground();
			rceycleBitmapDrawable(bd);
		}
	}

	public static void recycleImageViewBitMap(ImageView imageView) {
		if (imageView != null) {
			BitmapDrawable bd = (BitmapDrawable) imageView.getDrawable();
			rceycleBitmapDrawable(bd);
		}
	}

	private static void rceycleBitmapDrawable(BitmapDrawable bd) {
		if (bd != null) {
			Bitmap bitmap = bd.getBitmap();
			rceycleBitmap(bitmap);
		}
		bd = null;
	}

	private static void rceycleBitmap(Bitmap bitmap) {
		if ((bitmap == null) || (bitmap.isRecycled()))
			return;
		Logger.e(TAG, "rceycleBitmap");
//		bitmap.recycle();
		bitmap = null;
		System.gc();
		// if (bitmap != null && !bitmap.isRecycled()) {
		// Logger.e(TAG, "rceycleBitmap");
		// bitmap.recycle();
		// bitmap = null;
		// }

	}

	/**
	 * 处理图片
	 * 
	 * @param bm
	 *            所要转换的bitmap
	 * @param newWidth新的宽
	 * @param newHeight新的高
	 * @return 指定宽高的bitmap
	 */
	public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
		// 获得图片的宽高
		int width = bm.getWidth();
		int height = bm.getHeight();
		if(width == 0 || height == 0){
			Logger.i("zoomImg 中", bm == null?"  yes":"  no");
		}
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) /(float) width;
		float scaleHeight = ((float) newHeight) / (float) height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片  内存溢出可疑点！！！！！！！！！！！
		Bitmap newbm;
		try {
			newbm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix,true);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			android.os.Process.killProcess(android.os.Process.myPid());
			return bm;
		}
		return newbm;
	}
	
//	public Bitmap decodeBitmap(String imagePath,int imageWidth)
//    {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        // 通过这个bitmap获取图片的宽和高&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;
//        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
//        float realWidth = options.outWidth;
//        float realHeight = options.outHeight;
//        // 计算缩放比&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;
//        if(realWidth<=imageWidth){
//			 options.inJustDecodeBounds = false;
//			 bitmap = BitmapFactory.decodeFile(imagePath, options);
//			 bitmap = zoomImg(bitmap ,imageWidth,(int)realHeight );
//		  }else{
//			  int scale = (int)(realWidth/imageWidth);
//		      Logger.i("imageWidth", imageWidth+"");
//		      Logger.i("scale", scale+"");
//		      if (scale <= 0)
//		      {
//		          scale = 1;
//		      }
//		      options.inSampleSize = scale;
//		      options.inJustDecodeBounds = false;
//		      bitmap = BitmapFactory.decodeFile(imagePath, options);
//		      int w = bitmap.getWidth();
//		      int h = bitmap.getHeight();
////		      System.out.println("缩略图宽度：" + w + "高度:" + h);
//		  }
//        return bitmap;
//    }
	
	
	/**
	 * 获取圆角图片
	 * @param bitmap
	 * @param roundPx
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}
	
	//从资源中获取Bitmap
	public static Bitmap getBitmapFromDrawable(Context context,int resource) {
		Resources res = context.getResources();
		  InputStream is = res.openRawResource(resource);  
		  BitmapDrawable  bmpDraw = new BitmapDrawable(is);  
		  Bitmap bmp = bmpDraw.getBitmap();
		return bmp;
	}
	
	
	public static void getNextImage(final Home home){
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				try {
					byte[] b = getImageByte(home.getFile().getSrc());
					saveImage(home.getImg_local_dir(), b);
					Logger.i("ImageUtil  getNextImage ", "执行了预加载");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		ThreadPoolManager.getInstance().addTask(r);
	}
}
