package com.fcapp.beautyimage;

import java.io.File;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fcapp.beautyimage.appdata.Constant;
import com.fcapp.beautyimage.appdata.MyApplication;
import com.fcapp.beautyimage.util.FileUtil;
import com.fcapp.beautyimage.util.Logger;
import com.fcapp.beautyimage.util.ProgressBarUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.UMFeedbackService;

public class MoreActivity extends BaseActivity {

	private RelativeLayout feedback,cache;
	private TextView version;
	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.more);
	}

	@Override
	protected void findViewById() {
		feedback = (RelativeLayout) findViewById(R.id.rl_suggest);
		cache = (RelativeLayout) findViewById(R.id.ll_item2);
		version = (TextView) findViewById(R.id.version);
		
		try {
			version.setText(getVersionName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	protected void setListtener() {
		feedback.setOnClickListener(this);
		cache.setOnClickListener(this);
	}

	@Override
	protected void processLoggeric() {
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_suggest:
			UMFeedbackService.openUmengFeedbackSDK(this);
			break;
		case R.id.ll_item2:
			clearAll();
			break;

		default:
			break;
		}
		super.onClick(v);
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1610:
				ProgressBarUtil.hideLoading();
				Toast.makeText(MoreActivity.this, "清除成功", 0).show();
				break;
			case 1620:
				Toast.makeText(MoreActivity.this, "文件不存在", 0).show();
				break;
			case 1621:
				ProgressBarUtil.hideLoading();
				Toast.makeText(MoreActivity.this, "清除失败，再试一次", 0).show();
				break;
			default:
				break;
			}
		};
	};

	public  void clearAll(){
		//清空广告计时
		//删除根文件夹
		MyApplication.collectImage.clear();
		SharedPreferences sp = getApplicationContext().getSharedPreferences("config", MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.clear();
		edit.commit();
		new Thread(){
			public void run() {
				Looper.prepare();
				
				String rootDir = Constant.ALL_LETUN_DIR+"/fcapp";
				File file = new File(rootDir);
				if(file.exists()){
					Logger.i("deleteFile", "开始调用删除方法");
					ProgressBarUtil.showLoading(MoreActivity.this, "清除中...");
					boolean f = FileUtil.deleteFile(file);
					if(f){
						handler.sendEmptyMessage(1610);
					}else{
						handler.sendEmptyMessage(1621);
					}
				}else{
					handler.sendEmptyMessage(1620);
				}
				Looper.loop();
			};
		}.start();
		
	}
	
	private String getVersionName() throws Exception
	   {
	           // 获取packagemanager的实例
	           PackageManager packageManager = getPackageManager();
	           // getPackageName()是你当前类的包名，0代表是获取版本信息
	           PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
	           String version = packInfo.versionName;
	           return version;
	   }
	
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPause(this);
	}
	
	private long exitTime = 0; 
    @Override 
    public boolean onKeyDown(int keyCode, KeyEvent event) { 
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){                   
        if((System.currentTimeMillis()-exitTime) > 2000){ 
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();                                exitTime = System.currentTimeMillis(); 
        } 
        else{ 
            finish(); 
            System.exit(0); 
            } 
        return true; 
        } 
        return super.onKeyDown(keyCode, event); 
    }  //End of 连续两次返回退出程序
    
    
    
}
