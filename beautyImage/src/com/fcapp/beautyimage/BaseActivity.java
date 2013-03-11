package com.fcapp.beautyimage;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import com.fcapp.beautyimage.appdata.Constant;
import com.fcapp.beautyimage.appdata.MyApplication;
import com.fcapp.beautyimage.model.RequestModel;
import com.fcapp.beautyimage.util.NetUtil;
import com.fcapp.beautyimage.util.ProgressBarUtil;
import com.fcapp.beautyimage.util.ThreadPoolManager;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseActivity extends Activity implements View.OnClickListener{
	
	private ThreadPoolManager threadPoolManager;
    public BaseActivity() {
        threadPoolManager = ThreadPoolManager.getInstance();
    }
	
    protected  SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		sp = getSharedPreferences("eTagConfig", MODE_PRIVATE);
		DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        MyApplication.screntWidth = dm.widthPixels;
        MyApplication.screntHeight = dm.heightPixels;
			initView();
		
	}

	private void initView() {
		loadViewLayout();
		findViewById();
		setListtener();
		processLoggeric();
		
	}
	
	public abstract interface DataCallback<T> {
        public abstract void processData(T paramObject,
                                         int ObjectMark);
    }
	
	/*
	 * 要从服务器获取数据，首先要把参数定好，
	 * 然后要接收返回的数据,返回的数据已List<T>的形式
	 * */
	protected void getDataFromServer(RequestModel model,DataCallback callBack){
		ProgressBarUtil.showLoading(this, "加载中...");
		BaseHandler handler = new BaseHandler(this,callBack,model);
		BaseTask baseTask = new BaseTask(this,model,handler);
		threadPoolManager.addTask(baseTask);
	}
	
	
	 @SuppressWarnings("unchecked")
	    class BaseHandler extends Handler {
	        private Context context;
	        private DataCallback callBack;
	        private RequestModel model;

	        public BaseHandler(Context context, DataCallback callBack,
	        		RequestModel model) {
	            this.context = context;
	            this.callBack = callBack;
	            this.model = model;
	        }

	        public void handleMessage(Message msg) {
	            //closeProgressDiaLogger();
	            if (msg.what == Constant.SUCCESS) {
	                if (msg.obj == null) {
	                	//如果返回的对象为空
	                	//应该给个提示
	                	ProgressBarUtil.hideLoading();
	                	//Toast.makeText(getApplicationContext(), getString(R.string.net_error), 1).show();
	                	if(callBack!=null){
//	                		ProgressBarUtil.hideLoading();
//	                		System.out.println("这是在BaseActivity里面，看到这句话表明返回对象为空");
	                		callBack.processData(msg.obj, model.mark);
	                		
	                	}
	                } else {
	                	if(callBack!=null){
	                		ProgressBarUtil.hideLoading();
	                		callBack.processData(msg.obj, model.mark);
	                		
//		                    System.out.println("这是在BaseActivity里面，看到这句话表明返回对象不为空");
	                	}
	                }
	            } else if (msg.what == Constant.NET_FAILED) {
	            	//网络有问题
//	            	showSetNetWorkDiaLogger();
	            	ProgressBarUtil.hideLoading();
	            	Toast.makeText(BaseActivity.this, "网络有问题", 1).show();
	            }
	        }
	    }
	
	class BaseTask implements Runnable {
        private Context context;
        private RequestModel model;
        private Handler handler;

        public BaseTask(Context context, RequestModel model, Handler handler) {
            this.context = context;
            this.model = model;
            this.handler = handler;
        }

        @Override
        public void run() {
//        	 Looper.prepare(); 
            Object obj = null;
            Message msg = new Message();
            if (NetUtil.isNetWorkAvailable(context)) {
            		obj = NetUtil.head(model);
            
                msg.what = Constant.SUCCESS;
                msg.obj = obj;
                handler.sendMessage(msg);
            } else {
                msg.what = Constant.NET_FAILED;
                msg.obj = obj;
                handler.sendMessage(msg);
            }
        }

    }
	
	protected abstract void loadViewLayout();
	protected abstract void findViewById();
	protected abstract void setListtener();
	protected abstract void processLoggeric();
	

	@Override
	public void onClick(View v) {
		
	}
	
//	private void showSetNetWorkDiaLogger() {
//		AlertDiaLogger.Builder builder = new AlertDiaLogger.Builder(this); 
//		builder.setTitle("设置网络");
//		builder.setMessage("网络连接错误,请检查网络设置");
////		builder.setPositiveButton("设置网络", new OnClickListener() {
////			
////			public void onClick(DiaLoggerInterface diaLogger, int which) {
////				//cmp=com.android.settings/.WirelessSettings
////				Intent intent = new Intent();
////				intent.setClassName("com.android.settings","com.android.settings.WirelessSettings");
////				startActivity(intent);
////				
////			}
////		});
//		builder.setNegativeButton("确定",  new OnClickListener() {
//			
//			public void onClick(DiaLoggerInterface diaLogger, int which) {
//				diaLogger.dismiss();
//			}
//		});
//		AlertDiaLogger diaLogger = builder.show();  
//		
//	}
	
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
