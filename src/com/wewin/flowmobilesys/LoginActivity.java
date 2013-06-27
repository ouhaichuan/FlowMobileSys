package com.wewin.flowmobilesys;

import java.util.List;
import com.wewin.flowmobilesys.R;
import com.wewin.flowmobilesys.GlobalApplication;
import com.wewin.flowmobilesys.car.CarMainActivity;
import com.wewin.flowmobilesys.util.DBUtil;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 登录界面Activity
 * 
 * @author HCOU
 * @date 2013-5-29
 */
public class LoginActivity extends Activity implements OnClickListener {

	private Button mBtnRegister;
	private Button mBtnLogin;
	private Dialog mDialog;
	private EditText accout;
	private EditText password;
	private SharedPreferences sp;// 记住密码文件操作实例
	private CheckBox auto_save_pass_bx;// 记住密码复选框
	private String result = "false";
	public Handler mHandler;
	private CheckBox tasksysbox, carsysbox;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginpage);
		initView();
	}

	public void initView() {
		mBtnRegister = (Button) findViewById(R.id.regist);
		mBtnRegister.setOnClickListener(this);

		mBtnLogin = (Button) findViewById(R.id.login);
		mBtnLogin.setOnClickListener(this);

		accout = (EditText) findViewById(R.id.accounts);
		password = (EditText) findViewById(R.id.password);

		// 只允许本程序读写
		sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		auto_save_pass_bx = (CheckBox) findViewById(R.id.auto_save_password);
		// 判断密码框的状态,如果为真写入用户名、密码
		checkBoxState();

		tasksysbox = (CheckBox) findViewById(R.id.chk_tasksys);
		tasksysbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					carsysbox.setChecked(false);
				} else {
					carsysbox.setChecked(true);
				}
			}
		});
		carsysbox = (CheckBox) findViewById(R.id.chk_carsys);
		carsysbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					tasksysbox.setChecked(false);
				} else {
					tasksysbox.setChecked(true);
				}
			}
		});

		mHandler = new Handler();
	}

	/**
	 * 判断密码框的状态
	 * 
	 * @date 2013-5-29
	 */
	private void checkBoxState() {
		if (sp.getBoolean("ISCHECK", false)) {
			// 设置默认是记录密码状态
			auto_save_pass_bx.setChecked(true);
			accout.setText(sp.getString("ACCOUT", ""));
			password.setText(sp.getString("PASSWORD", ""));
		}
	}

	/**
	 * 用户名和密码写入文件
	 * 
	 * @date 2013-5-29
	 * @param userNameValue
	 * @param passwordValue
	 */
	public void editSharedPreferences() {
		Editor editor = sp.edit();
		if (auto_save_pass_bx.isChecked()) {
			// 记住用户和密码
			editor.putBoolean("ISCHECK", true);
			editor.putString("ACCOUT", accout.getText().toString());
			editor.putString("PASSWORD", password.getText().toString());
			editor.commit();
		} else {
			// 置空
			editor.putBoolean("ISCHECK", false);
			editor.putString("ACCOUT", "");
			editor.putString("PASSWORD", "");
			editor.commit();
		}
	}

	/**
	 * 点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.regist:
			goForgot_PasswordActivity();// 跳转找回密码页面
			break;
		case R.id.login:
			if (checkNullInPutValue()) {// 检查是否输入用户名、密码
				doLoginRequest();// 发送登录请求
				break;
			}
		default:
			break;
		}
	}

	private void doLoginRequest() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = DialogFactory.creatRequestDialog(this, "正在验证账号...");
		mDialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 验证数据
				List<String> list = new DBUtil().doLogin(accout.getText()
						.toString(), password.getText().toString());
				// 设置用户ID保存全局变量
				((GlobalApplication) getApplication()).setUserId(list.get(0));
				// 设置用户角色名称保存全局变量
				((GlobalApplication) getApplication()).setRolename(list.get(2));
				// 设置用户部门名称保存全局变量
				((GlobalApplication) getApplication()).setDepartment_name(list
						.get(3));
				// 设置用户名称
				((GlobalApplication) getApplication()).setUserName(list.get(4));
				result = list.get(1);// 验证结果
				// 更新界面，并记住用户、密码
				updateDialog();
				// 销毁窗口
				mDialog.dismiss();
			}
		}).start();
	}

	/**
	 * 更新界面线程,记住用户、密码
	 */
	public void updateDialog() {
		mHandler.post(new Runnable() {
			public void run() {
				if (result.equals("true")) {// 结果为真，表示验证成功，跳转到主页面
					editSharedPreferences();// 记住用户名和密码
					goMainActivity();// 跳转
				} else {
					// 验证失败
					Toast.makeText(LoginActivity.this, "用户名或密码错误",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	/**
	 * 跳转注册页面
	 */
	public void goForgot_PasswordActivity() {
		Intent intent = new Intent();
		intent.setClass(this, Forgot_PasswordActivity.class);
		startActivity(intent);
	}

	/**
	 * 跳转主页面
	 */
	public void goMainActivity() {
		Intent intent = new Intent();
		if (tasksysbox.isChecked()) {
			intent.setClass(this, MainActivity.class);
		} else {
			intent.setClass(this, CarMainActivity.class);
		}
		startActivity(intent);
		finish();
	}

	/**
	 * 验证输入框是否为空
	 * 
	 * @date 2013-6-4
	 * @return
	 */
	private boolean checkNullInPutValue() {
		if ("".equals(accout.getText().toString())
				|| "".equals(password.getText().toString())) {
			Toast.makeText(LoginActivity.this, "请输入用户名或密码", 0).show();
			return false;
		} else
			return true;
	}
}
