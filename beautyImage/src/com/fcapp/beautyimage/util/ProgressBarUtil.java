package com.fcapp.beautyimage.util;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressBarUtil {
	/**
	 * 显示"正在加载..."提示框
	 */
	static ProgressDialog mProgressDiaLog;
	
	
	public static  void showLoading(Context context,String msg){
	mProgressDiaLog = new ProgressDialog(context);
	mProgressDiaLog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	mProgressDiaLog.setMessage(msg);
	mProgressDiaLog.setIndeterminate(false);
	mProgressDiaLog.setCancelable(false);
	mProgressDiaLog.show();
	} 
	
	/**
	 * 隐藏"正在加载..."提示框,该方法在获取到数据的子线程中调用
	 */
	public static void hideLoading(){
	if (null != mProgressDiaLog && mProgressDiaLog.isShowing()) {
		mProgressDiaLog.dismiss();
		mProgressDiaLog = null;
	}
	} 
}
