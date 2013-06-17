package com.wewin.flowmobilesys;

import java.util.List;
import com.wewin.flowmobilesys.R;
import com.wewin.flowmobilesys.GlobalApplication;
import com.wewin.flowmobilesys.util.DBUtil;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * ��¼����Activity
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
	private SharedPreferences sp;// ��ס�����ļ�����ʵ��
	private CheckBox auto_save_pass_bx;// ��ס���븴ѡ��
	private String result = "false";
	public Handler mHandler;
	private long exitTime = 0;// �˳�����ʱ

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

		// ֻ�����������д
		sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		auto_save_pass_bx = (CheckBox) findViewById(R.id.auto_save_password);
		// �ж�������״̬,���Ϊ��д���û���������
		checkBoxState();

		mHandler = new Handler();
	}

	/**
	 * �ж�������״̬
	 * 
	 * @date 2013-5-29
	 */
	private void checkBoxState() {
		if (sp.getBoolean("ISCHECK", false)) {
			// ����Ĭ���Ǽ�¼����״̬
			auto_save_pass_bx.setChecked(true);
			accout.setText(sp.getString("ACCOUT", ""));
			password.setText(sp.getString("PASSWORD", ""));
		}
	}

	/**
	 * �û���������д���ļ�
	 * 
	 * @date 2013-5-29
	 * @param userNameValue
	 * @param passwordValue
	 */
	public void editSharedPreferences() {
		Editor editor = sp.edit();
		if (auto_save_pass_bx.isChecked()) {
			// ��ס�û�������
			editor.putBoolean("ISCHECK", true);
			editor.putString("ACCOUT", accout.getText().toString());
			editor.putString("PASSWORD", password.getText().toString());
			editor.commit();
		} else {
			// �ÿ�
			editor.putBoolean("ISCHECK", false);
			editor.putString("ACCOUT", "");
			editor.putString("PASSWORD", "");
			editor.commit();
		}
	}

	/**
	 * ����¼�
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.regist:
			goForgot_PasswordActivity();// ��ת�һ�����ҳ��
			break;
		case R.id.login:
			if (checkNullInPutValue()) {// ����Ƿ������û���������
				doLoginRequest();// ���͵�¼����
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
		mDialog = DialogFactory.creatRequestDialog(this, "������֤�˺�...");
		mDialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// ��֤����
				List<String> list = new DBUtil().doLogin(accout.getText()
						.toString(), password.getText().toString());
				// �����û�ID����ȫ�ֱ���
				((GlobalApplication) getApplication()).setUserId(list.get(0));
				result = list.get(1);// ��֤���
				// ���½��棬����ס�û�������
				updateDialog();
				// ���ٴ���
				mDialog.dismiss();
			}
		}).start();
	}

	/**
	 * ���½����߳�,��ס�û�������
	 */
	public void updateDialog() {
		mHandler.post(new Runnable() {
			public void run() {
				if (result.equals("true")) {// ���Ϊ�棬��ʾ��֤�ɹ�����ת����ҳ��
					editSharedPreferences();// ��ס�û���������
					goMainActivity();// ��ת
				} else {
					// ��֤ʧ��
					Toast.makeText(LoginActivity.this, "�û������������",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	/**
	 * ��תע��ҳ��
	 */
	public void goForgot_PasswordActivity() {
		Intent intent = new Intent();
		intent.setClass(this, Forgot_PasswordActivity.class);
		startActivity(intent);
	}

	/**
	 * ��ת��ҳ��
	 */
	public void goMainActivity() {
		Intent intent = new Intent();
		intent.setClass(this, MainActivity.class);
		startActivity(intent);
	}

	/**
	 * ��֤������Ƿ�Ϊ��
	 * 
	 * @date 2013-6-4
	 * @return
	 */
	private boolean checkNullInPutValue() {
		if ("".equals(accout.getText().toString())
				|| "".equals(password.getText().toString())) {
			Toast.makeText(LoginActivity.this, "�������û���������", 0).show();
			return false;
		} else
			return true;
	}

	/**
	 * ���ӷ��ز˵����˳���ť
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "�ٰ�һ���˳�����", 0).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}