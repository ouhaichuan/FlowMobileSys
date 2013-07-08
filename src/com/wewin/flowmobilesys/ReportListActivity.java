package com.wewin.flowmobilesys;

import java.util.HashMap;
import java.util.List;
import com.wewin.flowmobilesys.DialogFactory;
import com.wewin.flowmobilesys.GlobalApplication;
import com.wewin.flowmobilesys.R;
import com.wewin.flowmobilesys.service.WebServiceUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * 完成情况填报列表
 * 
 * @author HCOU
 * @date 2013-6-24
 */
public class ReportListActivity extends Activity {
	private ListView listView;
	private TextView listTitle;
	private WebServiceUtil dbUtil;
	private Dialog mDialog;
	private Handler handler;
	private SimpleAdapter adapter;
	private String userId = "";
	private List<HashMap<String, String>> list;
	private String missionId;// 任务ID
	private String backFlag;
	private String report_id;
	private Button addreport_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reportlist);
		initView();
	}

	private void initView() {
		Intent intent = getIntent();
		missionId = intent.getStringExtra("missionId");// 得到任务ID
		backFlag = intent.getStringExtra("backFlag");// 返回键标记

		listView = (ListView) findViewById(R.id.reportlistView);
		listView.setOnItemClickListener(new MyItemClickListhener());// 注册点击事件
		listView.setOnItemLongClickListener(new ItemLongClickListhener());// 长按事件

		listTitle = (TextView) findViewById(R.id.reportTitle);
		dbUtil = new WebServiceUtil();
		handler = new Handler();

		/**
		 * 添加按钮
		 */
		addreport_btn = (Button) findViewById(R.id.addreport_btn);
		addreport_btn.setOnClickListener(new AddMenuOnClicklistener());

		// 得到全局用户ID
		userId = ((GlobalApplication) getApplication()).getUserId();

		setView();
	}

	class AddMenuOnClicklistener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.addreport_btn:
				gotoAddActivity();// 添加
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 设置listView
	 */
	private void setView() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = DialogFactory.creatRequestDialog(this, "正在加载数据...");
		mDialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 得到某个任务的完成情况
				list = dbUtil.selectReportInfo(userId, missionId);
				// 更新界面
				updateDialog();
				// 销毁窗口
				mDialog.dismiss();
			}
		}).start();
	}

	/**
	 * list view 点击事件
	 * 
	 * @author HCOU
	 * @date 2013-6-24
	 */
	class MyItemClickListhener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

		}
	}

	/**
	 * list view 长按事件
	 * 
	 * @author HCOU
	 * @date 2013-6-24
	 */
	class ItemLongClickListhener implements OnItemLongClickListener {
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View view,
				int arg2, long arg3) {
			report_id = ((TextView) view.findViewById(R.id.txt_reportId))
					.getText().toString();// 得到当前填报情况ID
			doDeleteReqAndShowDialog();
			return true;
		}
	}

	/**
	 * 更新界面线程
	 */
	public void updateDialog() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				adapter = new SimpleAdapter(getApplicationContext(), list,
						R.layout.repadapter_item, new String[] { "id", "des",
								"addtime" }, new int[] { R.id.txt_reportId,
								R.id.txt_repdes, R.id.txt_repaddtime });
				listView.setAdapter(adapter);

				listTitle.setText("任务完成情况");
			}
		});
	}

	/**
	 * 跳转添加完成情况Activity
	 * 
	 * @date 2013-6-24
	 */
	public void gotoAddActivity() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("missionId", missionId);// 传送missionId
		bundle.putString("backFlag", backFlag);// 传送backFlag
		intent.setClass(this, ReportAddActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
	}

	/**
	 * 跳转主页面
	 */
	public void goToTaskListActivity() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putInt("taskFlag", 2);
		intent.setClass(this, TaskListActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
	}

	/**
	 * 跳转任务详细Activity
	 * 
	 * @date 2013-6-6
	 */
	public void gotoTaskDetailedActivity() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("missionId", missionId);// 传送missionId
		bundle.putInt("taskFlag", 2);// 传送菜单标签
		bundle.putString("canSee", "");// 传送是否已关注标记
		intent.setClass(this, TaskDetailedActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
	}

	/**
	 * 完成情况中的删除
	 * 
	 * @date 2013-6-24
	 */
	public void doDeleteReqAndShowDialog() {
		Dialog alertDialog = new AlertDialog.Builder(this).setTitle("提示")
				.setMessage("您确定删除该条完成情况吗？").setIcon(R.drawable.warning)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						doDeleteReq();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				}).create();
		alertDialog.show();
	}

	/**
	 * 访问删除申请webservice
	 * 
	 * @date 2013-6-18
	 */
	public void doDeleteReq() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = DialogFactory.creatRequestDialog(ReportListActivity.this,
				"正在重新读取完成情况...");
		mDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				dbUtil.doDeleteReportReq(report_id);// 删除完成情况webservice
				list = dbUtil.selectReportInfo(userId, missionId);
				// 更新界面
				updateDialog();
				// 销毁窗口
				mDialog.dismiss();
			}
		}).start();
	}

	/**
	 * 添加返回菜单，退出按钮
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (backFlag.equals("detailed"))
				gotoTaskDetailedActivity();
			else if (backFlag.equals("list"))
				goToTaskListActivity();

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}