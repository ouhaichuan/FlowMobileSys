package com.wewin.flowmobilesys;

import com.wewin.flowmobilesys.R;
import com.wewin.flowmobilesys.util.WebServiceUtil;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 注册类
 * 
 * @author HCOU
 * @time 2013.05.27 17:25:00
 */
public class Forgot_PasswordActivity extends Activity implements
		OnClickListener {

	private Button mBtnRegister;
	private EditText userid;
	private Dialog mDialog = null;
	public Handler mHandler;
	private String flag = "0";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgotpassword);
		initView();
	}

	/**
	 * 初始化界面
	 * 
	 * @date 2013-6-4
	 */
	public void initView() {
		mBtnRegister = (Button) findViewById(R.id.findback_btn);
		mBtnRegister.setOnClickListener(this);
		userid = (EditText) findViewById(R.id.userid);

		mHandler = new Handler();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.findback_btn:
			// 检查是否输入手机号
			if (checkNullInPutValue()) {
				doFindRequest();// 找回密码操作
				break;
			}
		default:
			break;
		}
	}

	/**
	 * 验证输入框是否为空
	 * 
	 * @date 2013-6-4
	 * @return
	 */
	private boolean checkNullInPutValue() {
		if ("".equals(userid.getText().toString())) {
			Toast.makeText(Forgot_PasswordActivity.this, "请输入用户名", 0).show();
			return false;
		} else
			return true;
	}

	/**
	 * 找回密码操作
	 * 
	 * @date 2013-6-4
	 */
	private void doFindRequest() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = DialogFactory.creatRequestDialog(this, "正在发送...");
		mDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				// 找回密码
				flag = new WebServiceUtil().doFindPassWord(
						userid.getText().toString()).get(0);
				// 更新界面
				updateDialog();
				// 销毁窗口
				mDialog.dismiss();

			}
		}).start();
	}

	/**
	 * 更新界面线程,记住用户、密码
	 * 
	 * @date 2013-6-4
	 */
	public void updateDialog() {
		mHandler.post(new Runnable() {
			public void run() {
				// 跳转回登录界面
				if (flag.equals("1")) {
					Toast.makeText(Forgot_PasswordActivity.this,
							"密码找回成功，请查收短信，\n并尽快登录Web系统重置", 0).show();
					goBackLoginActivity();
				} else {
					Toast.makeText(Forgot_PasswordActivity.this, "用户不存在", 0)
							.show();
				}
			}
		});
	}

	/**
	 * 跳转回登录界面
	 * 
	 * @date 2013-6-4
	 */
	public void goBackLoginActivity() {
		Intent intent = new Intent();
		intent.setClass(this, LoginActivity.class);
		startActivity(intent);
	}
}
