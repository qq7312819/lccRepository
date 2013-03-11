package com.fcapp.beautyimage.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.fcapp.beautyimage.model.Home;
import com.fcapp.beautyimage.myinterface.WaterFallImage;

public class WaterFallImageLoader extends AsyncTask<String, Integer,Bitmap> {
	
	
	private WaterFallImage wfi;
	private Home home;

	public WaterFallImageLoader(WaterFallImage callback,Home home){
		this.home = home;
		this.wfi = callback;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		wfi.afterLoadImage(result);
		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
		wfi.beforLoadImage();
		super.onPreExecute();
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapTool.getBitmap(home.getImg_local_dir(), home.getCover_img().getWidth(),home.getCover_img().getHeight());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(bitmap != null){
			return bitmap;
		}else{
			try {
				byte[] b = ImageUtil.getImageByte(home.getCover_img().getSrc());
				Bitmap bm = ImageUtil.getThumbnail(home.getCover_img().getWidth(),home.getCover_img().getHeight(), b);
				ImageUtil.saveImage(home.getImg_local_dir(), ImageUtil.bitmap2Bytes(bm));
				return bm;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}

	

}
