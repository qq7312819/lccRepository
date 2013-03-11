package com.fcapp.beautyimage.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.TextView;

import com.fcapp.beautyimage.util.Logger;

public class MyScrollView extends ViewGroup {

	private GestureDetector gestureDetector;
	private Scroller scroller;

	private Context ctx;

	private IMyScrollListener myScrollListener;
	private int width;
	private int height;
	private Activity activity;
	private Handler handler;

	public MyScrollView(Context context, Activity activity, Handler handler) {
		super(context);
		// TODO Auto-generated constructor stub
		ctx = context;
		this.activity = activity;
		this.handler = handler;
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels; // 屏幕宽度（像素）
		height = metric.heightPixels; // 屏幕高度（像素）
		initView();

	}

	private void initView() {
		scroller = new Scroller(ctx);
		gestureDetector = new GestureDetector(ctx, new OnGestureListener() {

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onShowPress(MotionEvent e) {

			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {
				scrollBy((int) distanceX, 0);
				return false;
			}

			@Override
			public void onLongPress(MotionEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
//				if (e2.getRawX() - e1.getRawX() > 100) {// 从左到右滑动
//					System.out.println("切换上一个");
//					return true;
//				}
//
//				if (e1.getRawX() - e2.getRawX() > 100) {// 从右向左的滑动
//					System.out.println("切换下一个");
//					return true;
//				}
				return false;
			}

			@Override
			public boolean onDown(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}
		});

	}

	
	private TextView loading;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// gestureDetector.onTouchEvent(event);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastX = event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			scrollBy((int) (mLastX - event.getX()), 0);

			mLastX = event.getX();

			break;
		case MotionEvent.ACTION_UP:
			moveToDest();
			break;

		default:
			break;
		}

		return true;
	}
	
	private int preDestId = 0;	//前一个页面的id

	/**
	 * ��view�������ʵ���λ�� leo 2013-1-22
	 */
	private void moveToDest() {
		
		int destId = (getScrollX() + getWidth() / 2) / getWidth();
		
		moveToDest(destId);
	}

	public void moveToDest(int destId) {
		int distance = destId * getWidth() - getScrollX();

		if (myScrollListener != null) {
			myScrollListener.moveToDest(destId);
		}

		// scrollBy(distance, 0);
		scroller.startScroll(getScrollX(), getScrollY(), distance, 0,
				Math.abs(distance));
		Logger.e("preDestId ", preDestId+"");
		Logger.e("destId ", destId+"");
		if(destId<preDestId){
			//加载上一个
			Logger.e("移到上一个 ", "移到上一个");
			Message msg = Message.obtain();
			msg.what = 127;
			handler.sendMessage(msg);
		}
		if (destId > preDestId) {
			//发送消息，加载下一个
			Logger.e("移到下一个 ", "移到下一个");
			Message msg = Message.obtain();
			msg.what = 128;
			handler.sendMessage(msg);
//			destId = getChildCount() - 1;
		}
		preDestId = destId;
		invalidate();
	}

	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			// Tools.Loggerleo("computeScroll scroller.getCurrX:"+scroller.getCurrX());
			scrollTo(scroller.getCurrX(), scroller.getCurrY());
			invalidate();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		MeasureSpec.getSize(widthMeasureSpec);
		MeasureSpec.getMode(widthMeasureSpec);

		for (int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);
			view.measure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * ˮƽ�ƶ�ʱ��ק
	 */
	private boolean isDrop;

	private float mLastX;
	private float mLastY;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastX = ev.getX();
			mLastY = ev.getY();
			isDrop = false;
			break;
		case MotionEvent.ACTION_MOVE:
			float distanceX = Math.abs(ev.getX() - mLastX);
			float distanceY = Math.abs(ev.getY() - mLastY);
			mLastX = ev.getX();
			mLastY = ev.getY();

			if (distanceX > 10 && distanceX > distanceY) {
				isDrop = true;
			}

			break;
		case MotionEvent.ACTION_UP:
			isDrop = false;
			break;

		default:
			break;
		}
		return isDrop;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		for (int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);
			view.layout(l + i * getWidth(), t, r + i * getWidth(), b);
		}
	}

	public IMyScrollListener getMyScrollListener() {
		return myScrollListener;
	}

	public void setMyScrollListener(IMyScrollListener myScrollListener) {
		this.myScrollListener = myScrollListener;
	}

	public interface IMyScrollListener {
		public void moveToDest(int destId);
	}

}
