package com.wewin.flowmobilesys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.wewin.flowmobilesys.GlobalApplication;
import com.wewin.flowmobilesys.adapter.ListAdapter;
import com.wewin.flowmobilesys.menu.TabMenu;
import com.wewin.flowmobilesys.util.WebServiceUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 任务显示Activity
 * 
 * @author HCOU
 * @date 2013-6-5
 */
public class TaskListActivity extends Activity {
	private ListView listView;
	private TextView taskTitle;
	private WebServiceUtil dbUtil;
	private Dialog mDialog;
	private Handler handler;
	private ListAdapter adapter;
	private int taskFlag = 0;
	private String userId = "", userName = "", rolename = "",
			department_name = "";
	private String missionId = "";
	private String intent_missionId;
	private String canSee = "";
	private String datachart_index = "";
	private String task_status = "", zrrName = "", ysrName = "";
	private List<HashMap<String, String>> list;
	TabMenu.MenuBodyAdapter bodyAdapter;
	TabMenu tabMenu;
	private String auditorcompleteFlag = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tasklist);
		initView();
	}

	private void initView() {
		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(new MyItemClickListhener());// 注册点击事件

		taskTitle = (TextView) findViewById(R.id.taskTitle);
		dbUtil = new WebServiceUtil();
		handler = new Handler();

		// 得到全局用户ID
		userId = ((GlobalApplication) getApplication()).getUserId();
		// 用户名
		userName = ((GlobalApplication) getApplication()).getUserName();
		// 得到全局用户角色名称
		rolename = ((GlobalApplication) getApplication()).getRolename();
		// 得到全局用户部门名称
		department_name = ((GlobalApplication) getApplication())
				.getDepartment_name();

		Intent intent = getIntent();
		taskFlag = intent.getIntExtra("taskFlag", 0);

		/**
		 * 设置弹出菜单图标
		 */
		switch (taskFlag) {
		case 1:
			bodyAdapter = new TabMenu.MenuBodyAdapter(this, new int[] {
					R.drawable.menu2, R.drawable.cancelwatch_mini },
					new String[] { "任务明细", "取消关注" });
			break;
		case 2:
			bodyAdapter = new TabMenu.MenuBodyAdapter(this, new int[] {
					R.drawable.menu2, R.drawable.write }, new String[] {
					"任务明细", "填报完成情况" });
			break;
		case 3:
			bodyAdapter = new TabMenu.MenuBodyAdapter(this, new int[] {
					R.drawable.menu2, R.drawable.watch_mini }, new String[] {
					"任务明细", "关注任务" });
			break;
		case 4:// 子任务列表
			intent_missionId = intent.getStringExtra("intent_missionId");
			break;
		case 5:// DataChart列表
			datachart_index = intent.getStringExtra("index");
			break;
		}

		tabMenu = new TabMenu(this, new BodyClickEvent(), R.drawable.login_bg);// 出现与消失的动画
		tabMenu.update();
		tabMenu.SetBodyAdapter(bodyAdapter);
		setView();
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
				switch (taskFlag) {
				case 1:// 关注任务
					list = dbUtil.selectWatchMissionInfo(userId);
					break;
				case 2:// 我的任务
					list = dbUtil.selectMyMissionInfo(userId);
					break;
				case 3:// 可见任务
					list = dbUtil.selectCanSeeMissionInfo(userId, rolename,
							department_name);
					break;
				case 4:// 子任务
					list = dbUtil.selectChildMissionInfo(intent_missionId);
					break;
				case 5:// DataChart列表
					list = dbUtil.selectChartMissionInfo(userId,
							datachart_index, rolename, department_name);
					break;
				default:
					list = new ArrayList<HashMap<String, String>>();
					break;
				}
				// 更新界面
				updateDialog();
				// 销毁窗口
				mDialog.dismiss();
			}
		}).start();
	}

	/**
	 * listView item点击响应事件
	 * 
	 * @author HCOU
	 * @date 2013-5-30
	 */
	public class MyItemClickListhener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// 得到当前任务ID
			missionId = ((TextView) view.findViewById(R.id.txt_missionid))
					.getText().toString();
			// 得到是否已关注标记
			canSee = ((TextView) view.findViewById(R.id.txt_counts)).getText()
					.toString();
			// 任务状态
			task_status = ((TextView) view.findViewById(R.id.txt_status))
					.getText().toString();
			// 责任人
			zrrName = ((TextView) view.findViewById(R.id.txt_zrrName))
					.getText().toString();
			// 验收人
			ysrName = ((TextView) view.findViewById(R.id.txt_ysrName))
					.getText().toString();

			reInitTabMenu();// 根据missionType重新构造弹出菜单

			int[] positions = new int[2];
			view.getLocationInWindow(positions);

			if (taskFlag != 4 && taskFlag != 5) {// 子任务和DataChart不弹出
				tabMenu.showAtLocation(view, Gravity.TOP, positions[0],
						positions[1]);
			}
		}
	}

	/**
	 * 重新构造弹出菜单
	 * 
	 * @date 2013-6-26
	 */
	public void reInitTabMenu() {
		if (zrrName.equals(userName) && taskFlag == 2
				&& (task_status.equals("进行中") || task_status.equals("即将超时"))) {
			bodyAdapter = new TabMenu.MenuBodyAdapter(TaskListActivity.this,
					new int[] { R.drawable.menu2, R.drawable.write,
							R.drawable.complete_mini }, new String[] { "任务明细",
							"填报完成情况", "完成" });
			auditorcompleteFlag = "complete";
		} else if (ysrName.equals(userName) && taskFlag == 2
				&& task_status.equals("提交待审")) {
			bodyAdapter = new TabMenu.MenuBodyAdapter(TaskListActivity.this,
					new int[] { R.drawable.menu2, R.drawable.write,
							R.drawable.audit_mini }, new String[] { "任务明细",
							"填报完成情况", "审核" });
			auditorcompleteFlag = "audit";
		} else if (ysrName.equals(userName)
				&& taskFlag == 2
				&& (task_status.equals("已完成") || task_status.equals("超时完成") || task_status
						.equals("超时"))) {
			bodyAdapter = new TabMenu.MenuBodyAdapter(TaskListActivity.this,
					new int[] { R.drawable.menu2, R.drawable.write,
							R.drawable.reaudit_mini }, new String[] { "任务明细",
							"填报完成情况", "重审" });
			auditorcompleteFlag = "reaudit";
		} else {
			/**
			 * 设置弹出菜单图标
			 */
			switch (taskFlag) {
			case 1:
				bodyAdapter = new TabMenu.MenuBodyAdapter(this, new int[] {
						R.drawable.menu2, R.drawable.cancelwatch_mini },
						new String[] { "任务明细", "取消关注" });
				break;
			case 2:
				bodyAdapter = new TabMenu.MenuBodyAdapter(this, new int[] {
						R.drawable.menu2, R.drawable.write }, new String[] {
						"任务明细", "填报完成情况" });
				break;
			case 3:
				bodyAdapter = new TabMenu.MenuBodyAdapter(this, new int[] {
						R.drawable.menu2, R.drawable.watch_mini },
						new String[] { "任务明细", "关注任务" });
				break;
			}
		}
		tabMenu = new TabMenu(TaskListActivity.this, new BodyClickEvent(),
				R.drawable.login_bg);// 出现与消失的动画
		tabMenu.update();
		tabMenu.SetBodyAdapter(bodyAdapter);
	}

	/**
	 * 更新界面线程
	 */
	public void updateDialog() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				adapter = new ListAdapter(getApplicationContext(), list);
				listView.setAdapter(adapter);

				switch (taskFlag) {
				case 1:
					taskTitle.setText("我的关注");
					break;
				case 2:
					taskTitle.setText("我的任务");
					break;
				case 3:
					if (rolename.equals("普通员工") || rolename.equals("主管")) {
						taskTitle.setText("可见任务");
					} else if (rolename.equals("部门经理")) {
						taskTitle.setText(department_name + "任务");
					} else if (rolename.equals("副总经理")
							|| rolename.equals("总经理")) {
						taskTitle.setText("所有任务");
					} else {
						taskTitle.setText("可见任务");
					}
					break;
				case 4:
					taskTitle.setText("子任务");
					break;
				case 5:
					taskTitle.setText("数据总概任务显示");
					break;
				}
			}
		});
	}

	/**
	 * 弹出菜单点击事件
	 * 
	 * @author HCOU
	 * @date 2013-6-5
	 */
	class BodyClickEvent implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long id) {
			tabMenu.SetBodySelect(position, Color.GRAY);// 设置选中状态
			switch (position) {
			case 0:
				// 跳转任务详细
				gotoDetailedActivity();
				break;
			case 1:
				// 现在关注选项窗口
				checkWitchWindow();
				break;
			case 2:// 审核或者完成操作
				goToAuditOrCompleteWindow();
				break;
			default:
				break;
			}
			tabMenu.dismiss();// 销毁弹出菜单
		}
	}

	/**
	 * 跳转任务详细Activity
	 * 
	 * @date 2013-6-6
	 */
	public void gotoDetailedActivity() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("missionId", missionId);// 传送missionId
		bundle.putInt("taskFlag", taskFlag);// 传送菜单标签
		bundle.putString("canSee", canSee);// 传送是否已关注标记
		intent.setClass(this, TaskDetailedActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
	}

	/**
	 * 审核或者完成操作
	 * 
	 * @date 2013-6-26
	 */
	public void goToAuditOrCompleteWindow() {
		if (auditorcompleteFlag.equals("complete")) {// 完成操作
			doCompleteShowDialog();
		} else if (auditorcompleteFlag.equals("audit")) {// 审批操作
			doAuditShowDialog("审核");
		} else if (auditorcompleteFlag.equals("reaudit")) {// 重审操作
			doAuditShowDialog("重新审核");
		}
	}

	/**
	 * 审核Dialog
	 * 
	 * @date 2013-6-5
	 */
	public void doAuditShowDialog(String str) {
		Dialog alertDialog = new AlertDialog.Builder(this).setTitle("提示")
				.setMessage("你确定" + str + "该任务吗？").setIcon(R.drawable.warning)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						doAuditReq();
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
	 * 访问审核任务webservice
	 * 
	 * @date 2013-6-26
	 */
	public void doAuditReq() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = DialogFactory.creatRequestDialog(TaskListActivity.this,
				"正在重新读取任务...");
		mDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				dbUtil.doAuditTaskReq(missionId, userId);// 审批任务webservice

				list = dbUtil.selectMyMissionInfo(userId);// 重新读取我的任务
				// 更新界面
				updateDialog();
				// 销毁窗口
				mDialog.dismiss();
			}
		}).start();
	}

	/**
	 * 完成Dialog
	 * 
	 * @date 2013-6-5
	 */
	public void doCompleteShowDialog() {
		Dialog alertDialog = new AlertDialog.Builder(this).setTitle("提示")
				.setMessage("您确定提交该条任务吗？").setIcon(R.drawable.warning)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						doCompleteReq();
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
	 * 访问完成任务webservice
	 * 
	 * @date 2013-6-26
	 */
	public void doCompleteReq() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = DialogFactory.creatRequestDialog(TaskListActivity.this,
				"正在重新读取任务...");
		mDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				dbUtil.doCompleteTaskReq(missionId, userId);// 完成任务webservice
				list = dbUtil.selectMyMissionInfo(userId);// 重新读取我的任务
				// 更新界面
				updateDialog();
				// 销毁窗口
				mDialog.dismiss();
			}
		}).start();
	}

	/**
	 * 判断是那个activity函数
	 * 
	 * @date 2013-6-5
	 */
	public void checkWitchWindow() {
		switch (taskFlag) {
		case 1:// 关注页面
			doCancleWatchReqAndShowDialog();
			break;
		case 2:// 我的任务页面
			goToWriteActivity();
			break;
		case 3:// 可见任务
			if ("已关注".equals(canSee)) {
				doShowWarningDialog();
				break;
			}
			doOkWatchReqAndShowDialog();
			break;
		case 4:// 子任务页面
			break;
		default:
			break;
		}
	}

	/**
	 * 已被关注弹出提示
	 * 
	 * @date 2013-6-5
	 */
	public void doShowWarningDialog() {
		Dialog alertDialog = new AlertDialog.Builder(this).setTitle("提示")
				.setMessage("此任务已被你关注？").setIcon(R.drawable.warning)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				}).create();
		alertDialog.show();
	}

	/**
	 * 我的任务Activity中的填报完成情况
	 * 
	 * @date 2013-6-5
	 */
	public void goToWriteActivity() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("missionId", missionId);// 传送missionId
		bundle.putString("backFlag", "list");// 传送backFlag
		intent.setClass(this, ReportListActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
	}

	/**
	 * 关注Activity中的关注
	 * 
	 * @date 2013-6-5
	 */
	public void doCancleWatchReqAndShowDialog() {
		Dialog alertDialog = new AlertDialog.Builder(this).setTitle("提示")
				.setMessage("您确定取消该条任务的关注吗？").setIcon(R.drawable.warning)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						doAcessCancelReq();
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
	 * 访问取消关注webservice
	 * 
	 * @date 2013-6-5
	 */
	public void doAcessCancelReq() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = DialogFactory.creatRequestDialog(TaskListActivity.this,
				"正在重新读取关注任务...");
		mDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				dbUtil.doAcessCancelReq(userId, missionId);// 取消关注webservice
				list = dbUtil.selectWatchMissionInfo(userId);// 重新读取关注任务
				// 更新界面
				updateDialog();
				// 销毁窗口
				mDialog.dismiss();
			}
		}).start();
	}

	/**
	 * 我的任务Activity中的关注
	 * 
	 * @date 2013-6-5
	 */
	public void doOkWatchReqAndShowDialog() {
		Dialog alertDialog = new AlertDialog.Builder(this).setTitle("提示")
				.setMessage("您确定关注该条任务吗？").setIcon(R.drawable.warning)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						doAcessOkReq();
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
	 * 访问关注webservice
	 * 
	 * @date 2013-6-5
	 */
	public void doAcessOkReq() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = DialogFactory.creatRequestDialog(TaskListActivity.this,
				"正在重新读取可见任务...");
		mDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				dbUtil.doAcessOkReq(userId, missionId);// 关注webservice
				list = dbUtil.selectCanSeeMissionInfo(userId, rolename,
						department_name);// 重新读取可见任务
				// 更新界面
				updateDialog();
				// 销毁窗口
				mDialog.dismiss();
			}
		}).start();
	}
}
