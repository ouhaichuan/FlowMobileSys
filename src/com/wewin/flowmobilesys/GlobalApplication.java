package com.wewin.flowmobilesys;

import android.app.Application;

/**
 * 全局变量设置类，主要存储用户ID,角色名称
 * 
 * @author HCOU
 * @time 2013.05.27 17:37:00
 */
public class GlobalApplication extends Application {
	private String userId;
	private String userName;
	private String rolename;
	private String department_name;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRolename() {
		return rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

	public String getDepartment_name() {
		return department_name;
	}

	public void setDepartment_name(String department_name) {
		this.department_name = department_name;
	}
}
