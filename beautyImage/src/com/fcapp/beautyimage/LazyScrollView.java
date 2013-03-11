package com.fcapp.beautyimage;

import com.fcapp.beautyimage.appdata.Constant;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class LazyScrollView extends ScrollView {
	
	private static final String tag = "LazyScrollView";
	
	private Handler handler;
	private View view;

	public LazyScrollView(Context context) {
		super(context);

	}

	public LazyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public LazyScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if(onScrollListener == null){
			return;
		}
		onScrollListener.onAutoScroll(l, t, oldl, oldt);
		
	}

	private void init() {

		this.setOnTouchListener(onTouchListener);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
			
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:
					//view.getMeasuredHeight() 得到这个Scroll里View的总高度过
					//getScrollY得到ScrollView顶部位置  如果在顶部 这里的值为0
					//得到scroll在手机屏幕上的高度.最大为手机屏幕高度
					if (view.getMeasuredHeight() - 20 <= getScrollY()
							+ getHeight() ) {
						if (onScrollListener != null) {
							onScrollListener.onBottom();
						}

					} else if (getScrollY() == 0) {
						if (onScrollListener != null) {
							onScrollListener.onTop();
						}
					} else {
						if (onScrollListener != null) {
							onScrollListener.onScroll();//发现调用方没有实现
						}
					}
					break;
				default:
					break;
				}
			}
		};
	}

	OnTouchListener onTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP://用户弹出界面后，200毫秒通过调用层滑动发生改变
				if (view != null && onScrollListener != null) {
					handler.sendMessageDelayed(handler.obtainMessage(
							Constant.HANDLER_WHAT), Constant.MESSAGE_DELAY);
				}
				break;
			}
			return false;
		}
	};

	/**
	 * 获得参考的View，主要是为了获得它的MeasuredHeight，然后和滚动条的ScrollY+getHeight作比较。
	 */
	public void getView() {
		this.view = getChildAt(0);
		if (view != null) {
			init();
		}
	}

	/**
	 * 定义接口
	 * 
	 * @author admin
	 * 
	 */
	public interface OnScrollListener {
		void onBottom();

		void onTop();

		void onScroll();

		void onAutoScroll(int l, int t, int oldl, int oldt);
	}

	private OnScrollListener onScrollListener;

	public void setOnScrollListener(OnScrollListener onScrollListener) {
		this.onScrollListener = onScrollListener;
	}
}
