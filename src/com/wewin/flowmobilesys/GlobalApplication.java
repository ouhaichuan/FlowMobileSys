package com.wewin.flowmobilesys;

import android.app.Application;

/**
 * 全局变量设置类，主要存储用户ID
 * 
 * @author HCOU
 * @time 2013.05.27 17:37:00
 */
public class GlobalApplication extends Application {
	private String userId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
