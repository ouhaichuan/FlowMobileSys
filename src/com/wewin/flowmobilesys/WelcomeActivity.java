package com.wewin.flowmobilesys;

import com.wewin.flowmobilesys.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * 欢迎界面
 * 
 * @author HCOU
 * @time 2013.05.27 17:25:00
 */
public class WelcomeActivity extends Activity {
	private Handler mHandler;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		// 初始化界面
		initView();
	}

	public void initView() {
		mHandler = new Handler();
		// 延迟2秒加载欢迎界面
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				goLoginActivity();
			}
		}, 2000);
	}

	public void goLoginActivity() {
		Intent intent = new Intent();
		intent.setClass(this, LoginActivity.class);
		startActivity(intent);
		finish();// 将活动推向后台
	}
}