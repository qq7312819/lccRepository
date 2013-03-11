package com.fcapp.beautyimage.widget;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.fcapp.beautyimage.appdata.MyApplication;
import com.fcapp.beautyimage.util.Logger;

public class MyViewPager extends ViewPager {

	static final String TAG = "MyViewPager";
	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyViewPager(Context context) {
		super(context);
	}

	
	private long time = 0;
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		
//		boolean flag = true;
		
//		 int action = ev.getAction();
//         switch(action){
//         case MotionEvent.ACTION_DOWN:
//              Logger.d(TAG,"onTouchEvent action:ACTION_DOWN");
//              break;
//         case MotionEvent.ACTION_MOVE:
//              Logger.d(TAG,"onTouchEvent action:ACTION_MOVE");
//              break;
//         case MotionEvent.ACTION_UP:
//        	 Logger.d(TAG,"onTouchEvent action:ACTION_UP");
//        	
//        	 if((System.currentTimeMillis() - time)<1500){
//     			 Logger.e("onTouchEvent", "太快了");
//     			 time = System.currentTimeMillis();
//     			Intent myIntent = new Intent();
//     			myIntent.setAction("too_fast");
//     			MyApplication.ThirdActivityContext.sendBroadcast(myIntent);
//     			flag = true;
//     		 }else{
//     			time = System.currentTimeMillis();
//     			flag = false;
//     		 }
//        	break;
//         }
//         
//		if(flag){
//			return flag;
//		}else{
			return super.onTouchEvent(ev);
//		}
			 
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		//无论返回 ture还是false都没反应
		return super.dispatchTouchEvent(ev);
	}
	
	/*
	 * 当TouchEvent发生时，首先Activity将TouchEvent传递给最顶层的View，
	 *  TouchEvent最先到达最顶层 view 的 dispatchTouchEvent ，
	 *  然后由  dispatchTouchEvent 方法进行分发，
	 *  如果dispatchTouchEvent返回true ，则交给这个view的onTouchEvent处理，
	 *  如果dispatchTouchEvent返回 false ，则交给这个 view 的 interceptTouchEvent 方法来决定是否要拦截这个事件，
	 *  如果 interceptTouchEvent 返回 true ，也就是拦截掉了，
	 *  则交给它的 onTouchEvent 来处理，如果 interceptTouchEvent 返回 false ，
	 *  那么就传递给子 view ，由子 view 的 dispatchTouchEvent 再来开始这个事件的分发。
	 *  如果事件传递到某一层的子 view 的 onTouchEvent 上了，这个方法返回了 false ，
	 *  那么这个事件会从这个 view 往上传递，都是 onTouchEvent 来接收。
	 *  而如果传递到最上面的 onTouchEvent 也返回 false 的话，这个事件就会“消失”，而且接收不到下一次事件。
	 */
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
//		if((System.currentTimeMillis() - time)>2000){
//			time = System.currentTimeMillis();
//			Logger.e("onInterceptTouchEvent", "返回的是：false");
//			return false;
//		}else{
//			time = 0;
//			Logger.e("onInterceptTouchEvent", "返回的是　：true");
//			return true;
//		}
//此方法返回false的时候把事件传递给了子view
//		return true;
		
		return super.onInterceptTouchEvent(arg0);
	}
}
