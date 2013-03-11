package com.fcapp.beautyimage.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;

/**
 * ���ش�ͼƬ�����ࣺ���android���ش�ͼƬʱ��OOM�쳣
 * 
 * ���ԭ�?����������ѡ��ٶ�ȡ���ŵ�ͼƬ��ݵ��ڴ棬������ڴ������OOM
 */

public class BitmapTool {

	public static final int UNCONSTRAINED = -1;

	// ���������Ϣ
	public static Options getOptions(String path) {
		Options options = new Options();
		// ֻ��ߣ�����ȡ���
		options.inJustDecodeBounds = true;
		// ���ص��ڴ�
		BitmapFactory.decodeFile(path, options);
		return options;
	}

	// ���ͼ��
	private static Bitmap getBitmapByPath(String path, Options options,
			int screenWidth, int screenHeight)  {
		
		
		File file = new File(path);
		if (!file.exists()) {
			return null;
		}
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		if (options != null) {
			Rect r = getScreenRegion(screenWidth, screenHeight);
			// ȡ��ͼƬ�Ŀ�͸�
			int w = r.width();
			int h = r.height();
			int maxSize = w > h ? w : h;
			// �������ű���
			int inSimpleSize = computeSampleSize(options, maxSize, w * h);
			// �������ű���
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			options.inSampleSize = inSimpleSize;
			options.inJustDecodeBounds = false;
		}

		// ����ѹ�����ͼƬ
		Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
		try {
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	private static Rect getScreenRegion(int width, int height) {
		return new Rect(0, 0, width, height);
	}

	// ��ȡ��Ҫ�������ŵı���options.inSampleSize
	private static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
		return initialSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		// ���ͼƬ�Ŀ�͸�
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 : (int) Math
				.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == UNCONSTRAINED) ? 128 : (int) Math
				.min(Math.floor(w / minSideLength),
						Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			return lowerBound;
		}
		if ((maxNumOfPixels == UNCONSTRAINED)
				&& (minSideLength == UNCONSTRAINED)) {
			return 1;
		} else if (minSideLength == UNCONSTRAINED) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public static Bitmap getBitmap(final String path, final int screenWidth,
			final int screenHeight) throws FileNotFoundException {
		
				return BitmapTool.getBitmapByPath(path, BitmapTool.getOptions(path),
						screenWidth, screenHeight);
	}
}
