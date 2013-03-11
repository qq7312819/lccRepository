package com.fcapp.beautyimage.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.fcapp.beautyimage.ThirdImageViewActivity;
import com.fcapp.beautyimage.model.Home;

/**
 * 锟斤拷锟斤拷图片锟斤拷锟届步锟斤拷锟斤拷,锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟� 锟斤拷一锟斤拷锟斤拷锟斤拷 锟斤拷String 锟斤拷锟斤拷图片锟斤拷路锟斤拷 锟斤拷 integer 锟斤拷锟斤拷图片锟侥斤拷锟�锟斤拷 Bitmap
 * 锟斤拷锟截碉拷图片锟斤拷锟接︼拷锟絙itmap
 * 
 */
public class LoadImageAsyncTask extends AsyncTask<String, Integer, Bitmap> {
	private LoadImageAsynTaskCallBack callback;
	private Home home;
	private Context context;
	private Handler mHandler;
	public LoadImageAsyncTask(LoadImageAsynTaskCallBack callback,Home home,Context context,Handler handler) {
		this.callback = callback;
		this.context = context;
		this.home = home;
		this.mHandler = handler;
	}

	

	public interface LoadImageAsynTaskCallBack {
		public void beforeImageLoad();

		public void onImageLoaded(Bitmap bitmap);
	}

	@Override
	protected void onPreExecute() {

		callback.beforeImageLoad();
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		callback.onImageLoaded(result);
		
		super.onPostExecute(result);

	}

	@Override
	protected Bitmap doInBackground(String... params) {
		try {
			Bitmap bitmap = getImageFromLocal(home.getImg_local_dir());
			if(bitmap != null){
				
				Logger.i("doInBackground", "本地加载不为空  dir=="+home.getImg_local_dir());
				return bitmap;
			}
			mHandler.sendEmptyMessage(1314);
			bitmap = loadBigImage(params);
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Bitmap loadBigImage(String... params) throws MalformedURLException,
			IOException {
		Bitmap bitmap;
		byte[] result = null;
			String path = params[0];
			URL url = new URL(path.toString());
			Logger.i("LoadImageAsyncTask下载图片的地址是", url.toString());
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
			result = baos.toByteArray();
			baos.close();
			baos = null;
			bitmap = handlerBitmap(result,home.getFile().getWidth(),home.getFile().getHeight());
			saveImage(home.getImg_local_dir(),bitmap);
		return bitmap;
	}
	
	public static void saveImage(String imagePath, Bitmap bitmap) {
		boolean isExist = SystemInfo.existSDcard();
		if(!isExist){
			return;
		}
		try {
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
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Bitmap getImageFromLocal(final String imagePath) {
		Logger.e("getImageFromLocal", " imagePath == "+ imagePath );
		 File file = new File(imagePath);
	        if (file.exists()) {
	        	Bitmap bitmap = null;
				try {
					FileInputStream is = new FileInputStream(file);
					System.gc();
//					Thread.sleep(100);
					try{
							bitmap = BitmapFactory.decodeStream(is);
					   } catch (OutOfMemoryError e){
					             e.getLocalizedMessage(); 
					             System.gc();
					             bitmap =null;
					             android.os.Process.killProcess(android.os.Process.myPid());
					         }
					file.setLastModified(System.currentTimeMillis());
					return bitmap;
				} catch (Exception e) {
					e.printStackTrace();
				}
	        }
	    return null;
	}
	
	public static Bitmap handlerBitmap(byte[] data , int reqWidth,int reqHeight){
		
		
		
		BitmapFactory.Options opts = getOptions(data);
		opts.inSampleSize = calculateInSampleSize(opts,reqWidth,reqHeight);
		opts.inJustDecodeBounds = false;
		
	    Bitmap bitmap;
		try {
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
		} catch (OutOfMemoryError e) {
			e.getLocalizedMessage(); 
            System.gc();
            bitmap =null;
		}
	    return bitmap;
	}
	
	public static Options getOptions(byte[]  data){
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, options);
		int imageHeight = options.outHeight;
		int imageWidth = options.outWidth;
		
		return options;
	}
	
	public static int calculateInSampleSize( BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	
	        // Calculate ratios of height and width to requested height and width
	        final int heightRatio = Math.round((float) height / (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);
	
	        // Choose the smallest ratio as inSampleSize value, this will guarantee
	        // a final image with both dimensions larger than or equal to the
	        // requested height and width.
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }

	    return inSampleSize;
}

	@Override
	protected void onProgressUpdate(Integer... values) {
		int total = values[0];
		int max = values[1];
		super.onProgressUpdate(values);
	}
}
