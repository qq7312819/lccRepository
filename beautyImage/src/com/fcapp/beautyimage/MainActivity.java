package com.fcapp.beautyimage;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost;
import android.widget.Toast;

import com.adsmogo.adview.AdsMogoLayout;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.NotificationType;
import com.umeng.fb.UMFeedbackService;
import com.umeng.update.UmengDownloadListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

public class MainActivity extends TabActivity implements OnCheckedChangeListener{

	private TabHost mHost;
	private RadioGroup radioderGroup;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		UMFeedbackService.enableNewReplyNotification(this, NotificationType.AlertDialog);
		UmengUpdateAgent.update(this);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
	        @Override
	        public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
	            switch (updateStatus) {
	            case 0: // has update
	                UmengUpdateAgent.showUpdateDialog(MainActivity.this, updateInfo);
	                break;
	            case 1: // has no update
//	                Toast.makeText(MainActivity.this, "没有更新", Toast.LENGTH_SHORT).show();
	                break;
	            case 2: // none wifi
//	                Toast.makeText(MainActivity.this, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
	                break;
	            case 3: // time out
//	                Toast.makeText(MainActivity.this, "超时", Toast.LENGTH_SHORT).show();
	                break;
	            }
	        }
	});
		
		UmengUpdateAgent.setOnDownloadListener(new UmengDownloadListener(){
		    @Override
		    public void OnDownloadEnd(int result) {
		        Toast.makeText(MainActivity.this, "download result : " + result , Toast.LENGTH_SHORT).show();
		    }           
		});
		
		mHost = this.getTabHost();

		mHost.addTab(mHost.newTabSpec("HOME").setIndicator("HOME")
				.setContent(new Intent(this, HomeActivity.class)));
		mHost.addTab(mHost.newTabSpec("COLLECT").setIndicator("COLLECT")
				.setContent(new Intent(this, CollectActivity.class)));
		mHost.addTab(mHost.newTabSpec("MORE").setIndicator("MORE")
				.setContent(new Intent(this, MoreActivity.class)));
		
		
		radioderGroup = (RadioGroup) findViewById(R.id.main_radio);
		radioderGroup.setOnCheckedChangeListener(this);
		
		
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch(checkedId){
		case R.id.rb_home:
			mHost.setCurrentTabByTag("HOME");
			break;
		case R.id.rb_collect:
			mHost.setCurrentTabByTag("COLLECT");
			break;
		case R.id.rb_more:
			mHost.setCurrentTabByTag("MORE");
			break;
		}		
	}
	
	
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
		
		AdsMogoLayout.clear();
		// 清除 adsMogoLayout 实例 所产生用于多线程缓冲机制的线程池
		// 此方法请不要轻易调用，如果调用时间不当，会造成无法统计计数
//		adsMogoLayoutCode.clearThread();
		
		super.onDestroy();
	}
}
