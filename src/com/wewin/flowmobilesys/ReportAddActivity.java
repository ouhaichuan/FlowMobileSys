package com.wewin.flowmobilesys;

import com.wewin.flowmobilesys.R;
import com.wewin.flowmobilesys.util.WebServiceUtil;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 添加完成情况类
 * 
 * @author HCOU
 * @date 2013-6-24
 */
public class ReportAddActivity extends Activity implements OnClickListener {

	private Button mBtnRegister;
	private EditText reportInfoTxt;
	private Dialog mDialog = null;
	public Handler mHandler;
	private WebServiceUtil dbUtil;
	private String missionId, userId;
	private String backFlag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reportadd);
		initView();
	}

	/**
	 * 初始化界面
	 * 
	 * @date 2013-6-24
	 */
	public void initView() {
		mBtnRegister = (Button) findViewById(R.id.addreport_btn);
		mBtnRegister.setOnClickListener(this);
		reportInfoTxt = (EditText) findViewById(R.id.reportinfo);
		dbUtil = new WebServiceUtil();

		Intent intent = getIntent();
		missionId = intent.getStringExtra("missionId");// 得到任务ID
		backFlag = intent.getStringExtra("backFlag");// 返回键标记
		userId = ((GlobalApplication) getApplication()).getUserId();// 得到全局用户ID

		mHandler = new Handler();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addreport_btn:
			// 检查是否输入描述
			if (checkNullInPutValue()) {
				doAddReportRequest();// 添加完成情况操作
				break;
			}
		default:
			break;
		}
	}

	/**
	 * 验证输入框是否为空
	 * 
	 * @date 2013-6-24
	 * @return
	 */
	private boolean checkNullInPutValue() {
		if ("".equals(reportInfoTxt.getText().toString())) {
			Toast.makeText(ReportAddActivity.this, "请输入描述", 0).show();
			return false;
		} else
			return true;
	}

	/**
	 * 添加完成情况操作
	 * 
	 * @date 2013-6-24
	 */
	private void doAddReportRequest() {
		// 隐藏输入法
		InputMethodManager imm = (InputMethodManager) getApplicationContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		// 显示或者隐藏输入法
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

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
				dbUtil.doAddReportReq(reportInfoTxt.getText().toString(),
						missionId, userId);
				// 更新界面
				updateDialog();
				// 销毁窗口
				mDialog.dismiss();
			}
		}).start();
	}

	/**
	 * 更新界面线程
	 * 
	 * @date 2013-6-24
	 */
	public void updateDialog() {
		mHandler.post(new Runnable() {
			public void run() {
				goBackReportListActivity();// 跳转
			}
		});
	}

	/**
	 * 跳转回完成情况页面
	 * 
	 * @date 2013-6-24
	 */
	public void goBackReportListActivity() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("missionId", missionId);// 传送missionId
		bundle.putString("backFlag", backFlag);// 传送backFlag
		intent.setClass(this, ReportListActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
	}

	/**
	 * 添加返回菜单
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			goBackReportListActivity();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
