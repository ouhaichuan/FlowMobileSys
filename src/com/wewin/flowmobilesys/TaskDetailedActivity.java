package com.wewin.flowmobilesys;

import java.util.ArrayList;
import java.util.List;

import com.wewin.flowmobilesys.adapter.OnGestureAndTouchAdapter;
import com.wewin.flowmobilesys.menu.ActionItem;
import com.wewin.flowmobilesys.menu.TitlePopup;
import com.wewin.flowmobilesys.util.WebServiceUtil;
import com.wewin.flowmobilesys.util.FileUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 任务详细Acitivity
 * 
 * @author HCOU
 * @date 2013-6-6
 */
public class TaskDetailedActivity extends Activity {
	private TextView detailedTitle;
	private String missionId, currentMissionId;
	private String canSee = "";
	private String auditorcompleteFlag;
	int updownFlag = 0;// 点击上一条或者下一条标记0,上；1下
	private String userid, username, rolename, department_name;
	private String next_id;// 下一条
	private String for_id;// 上一条
	private int taskFlag;// 1我的关注，2我的任务，3可见任务
	private WebServiceUtil dbUtil;
	private Dialog mDialog;
	private Handler handler;
	private List<String> list;
	private Button control_btn, child_btn;
	private TitlePopup titlePopup;
	private GestureDetector mGestureDetector;
	private LinearLayout touchLayout;

	private TextView txt_tasktype, txt_response_person, txt_taskid,
			txt_task_creator, txt_task_yanshou, txt_task_name,
			txt_task_startdate, txt_task_enddate, txt_task_mj, txt_task_status,
			txt_task_fenguan, txt_task_writedate, txt_task_des,
			txt_task_counts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.taskdetailed);
		initView();
	}

	/**
	 * 初始化界面
	 * 
	 * @date 2013-6-6
	 */
	private void initView() {
		Intent intent = getIntent();
		// 得到全局用户ID
		userid = ((GlobalApplication) getApplication()).getUserId();
		// 得到全局用户名称
		username = ((GlobalApplication) getApplication()).getUserName();
		// 得到全局用户ID
		rolename = ((GlobalApplication) getApplication()).getRolename();
		// 得到全局用户ID
		department_name = ((GlobalApplication) getApplication())
				.getDepartment_name();

		missionId = intent.getStringExtra("missionId");
		taskFlag = intent.getIntExtra("taskFlag", 0);
		canSee = intent.getStringExtra("canSee");
		dbUtil = new WebServiceUtil();
		handler = new Handler();

		detailedTitle = (TextView) findViewById(R.id.detailedTitle);
		detailedTitle.setText("任务明细");// 设置标题栏
		control_btn = (Button) findViewById(R.id.control_btn);// 标题栏菜单按钮
		control_btn.setOnClickListener(new TitleButtnOnclickLisenter());

		child_btn = (Button) findViewById(R.id.child_btn);// 子菜单按钮
		child_btn.setOnClickListener(new ChildButtnOnclickLisenter());

		txt_tasktype = (TextView) findViewById(R.id.txt_tasktype);
		txt_response_person = (TextView) findViewById(R.id.txt_response_person);
		txt_taskid = (TextView) findViewById(R.id.txt_taskid);
		txt_task_creator = (TextView) findViewById(R.id.txt_task_creator);
		txt_task_yanshou = (TextView) findViewById(R.id.txt_task_yanshou);
		txt_task_name = (TextView) findViewById(R.id.txt_task_name);
		txt_task_startdate = (TextView) findViewById(R.id.txt_task_startdate);
		txt_task_enddate = (TextView) findViewById(R.id.txt_task_enddate);
		txt_task_mj = (TextView) findViewById(R.id.txt_task_mj);
		txt_task_status = (TextView) findViewById(R.id.txt_task_status);
		txt_task_fenguan = (TextView) findViewById(R.id.txt_task_fenguan);
		txt_task_writedate = (TextView) findViewById(R.id.txt_task_writedate);
		txt_task_des = (TextView) findViewById(R.id.txt_task_des);
		txt_task_counts = (TextView) findViewById(R.id.txt_task_counts);

		initTouchListener();// 绑定触屏滑动事件
		setViewData();// 查询页面要显示的数据
	}

	/**
	 * 初始化底部菜单
	 * 
	 * @date 2013-6-8
	 */
	private void initTitleMenu() {
		titlePopup = new TitlePopup(this, new TitleItemOnclickLisenter(),
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		titlePopup.update();
		switch (taskFlag) {
		case 1:// 我的关注
			titlePopup.addAction(new ActionItem(this, "取消关注",
					R.drawable.cancelwatch_mini_2));
			break;
		case 2:// 我的任务
			titlePopup.addAction(new ActionItem(this, "填写情况",
					R.drawable.write_mini));
			break;
		case 3:// 可见任务
			titlePopup.addAction(new ActionItem(this, "关注",
					R.drawable.watch_mini_2));
			break;
		default:
			break;
		}
		titlePopup.addAction(new ActionItem(this, "导出",
				R.drawable.export_mini_2));
		titlePopup
				.addAction(new ActionItem(this, "下一条", R.drawable.next_mini_2));
		titlePopup.addAction(new ActionItem(this, "上一条",
				R.drawable.return_mini_2));

		if (txt_response_person.getText().toString().equals(username)
				&& taskFlag == 2
				&& (txt_task_status.getText().toString().equals("进行中") || txt_task_status
						.getText().toString().equals("即将超时"))) {// 我的任务中的自主式任务
			titlePopup.addAction(new ActionItem(this, "完成",
					R.drawable.complete_mini_2));
			auditorcompleteFlag = "complete";
		} else if (txt_task_yanshou.getText().toString().equals(username)
				&& taskFlag == 2
				&& txt_task_status.getText().toString().equals("提交待审")) {// 我的任务中的派发式任务
			titlePopup.addAction(new ActionItem(this, "审核",
					R.drawable.audit_mini_2));
			auditorcompleteFlag = "audit";
		} else if (txt_task_yanshou.getText().toString().equals(username)
				&& taskFlag == 2
				&& (txt_task_status.getText().toString().equals("已完成")
						|| txt_task_status.getText().toString().equals("超时完成") || txt_task_status
						.getText().toString().equals("超时"))) {
			titlePopup.addAction(new ActionItem(this, "重审",
					R.drawable.reaudit_mini_2));
			auditorcompleteFlag = "reaudit";
		} else {
		}
	}

	/**
	 * 顶部菜单点击事件类
	 * 
	 * @author HCOU
	 * @date 2013-6-8
	 */
	class TitleButtnOnclickLisenter implements OnClickListener {
		@Override
		public void onClick(View v) {
			initTitleMenu();
			titlePopup.show(v);
		}
	};

	/**
	 * 子任务按钮点击事件
	 * 
	 * @author HCOU
	 * @date 2013-6-25
	 */
	class ChildButtnOnclickLisenter implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putInt("taskFlag", 4);
			bundle.putString("intent_missionId", missionId);
			intent.setClass(getApplicationContext(), TaskListActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	};

	/**
	 * 顶部菜单项点击事件
	 * 
	 * @author HCOU
	 * @date 2013-6-9
	 */
	class TitleItemOnclickLisenter implements OnItemClickListener {
		public void onItemClick(android.widget.AdapterView<?> adapterView,
				View view, int position, long id) {
			switch (position) {
			case 0:// 第一个按钮
				switch (taskFlag) {
				case 1:// 我的关注的取消关注功能
					doCancleWatchReqAndShowDialog();
					break;
				case 2:// 我的任务中的填写完成情况功能
					goToWriteActivity();
					break;
				case 3:// 可见任务中的关注功能
					if ("已关注".equals(canSee)) {
						doShowWarningDialog();
						break;
					}
					doOkWatchReqAndShowDialog();
					break;
				default:
					break;
				}
				break;
			case 1:// 导出
				doExportFileShowDialog();
				break;
			case 2:// 下一条
				doGotoNextMissionDetailed();
				break;
			case 3:// 上一条
				doGotoForMissionDetailed();
				break;
			case 4:// 完成或者审核
				goToAuditOrCompleteWindow();
				break;
			default:
				break;
			}
			titlePopup.dismiss();
		}
	}

	/**
	 * 审核或者完成操作
	 * 
	 * @date 2013-6-26
	 */
	public void goToAuditOrCompleteWindow() {
		if (auditorcompleteFlag.equals("complete")) {// 自主式，完成操作
			doCompleteShowDialog();
		} else if (auditorcompleteFlag.equals("audit")) {// 派发式，审批操作
			doAuditShowDialog("审核");
		} else if (auditorcompleteFlag.equals("reaudit")) {
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
		mDialog = DialogFactory.creatRequestDialog(TaskDetailedActivity.this,
				"正在重新读取任务...");
		mDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				dbUtil.doAuditTaskReq(missionId, userid);// 审批任务webservice
				goToTaskListActivity(taskFlag);
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
		mDialog = DialogFactory.creatRequestDialog(TaskDetailedActivity.this,
				"正在重新读取任务...");
		mDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				dbUtil.doCompleteTaskReq(missionId, userid);// 完成任务webservice
				// 跳转到我的任务页面
				goToTaskListActivity(taskFlag);
				// 销毁窗口
				mDialog.dismiss();
			}
		}).start();
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
		bundle.putString("backFlag", "detailed");// 传送backFlag
		intent.setClass(this, ReportListActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
	}

	/**
	 * 导出文件确认
	 * 
	 * @date 2013-6-5
	 */
	public void doExportFileShowDialog() {
		Dialog alertDialog = new AlertDialog.Builder(this).setTitle("提示")
				.setMessage("您确定导出此条任务信息到SD卡吗？").setIcon(R.drawable.warning)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						doExportFileToSdCard();
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
	 * 导出文件到SdCard
	 * 
	 * @date 2013-6-13
	 */
	public void doExportFileToSdCard() {
		FileUtil util = new FileUtil();
		String data = "                    任务明细\n\n" + "    任务类型："
				+ txt_tasktype.getText().toString() + "\n" + "      责任人："
				+ txt_response_person.getText().toString() + "\n" + "    任务编号："
				+ txt_taskid.getText().toString() + "\n" + "  任务制定人："
				+ txt_task_creator.getText().toString() + "\n" + "      验收人："
				+ txt_task_yanshou.getText().toString() + "\n" + "    任务名称："
				+ txt_task_name.getText().toString() + "\n" + "任务开始日期："
				+ txt_task_startdate.getText().toString() + "\n" + "任务结束日期："
				+ txt_task_enddate.getText().toString() + "\n" + "    任务密级："
				+ txt_task_mj.getText().toString() + "\n" + "    任务状态："
				+ txt_task_status.getText().toString() + "\n" + "    分管领导："
				+ txt_task_fenguan.getText().toString() + "\n" + "    填报日期："
				+ txt_task_writedate.getText().toString() + "\n" + "任务内容描述："
				+ txt_task_des.getText().toString() + "\n" + "    子任务数："
				+ txt_task_counts.getText().toString() + "\n";
		util.WriteFile(missionId + userid + ".txt", data);
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
		mDialog = DialogFactory.creatRequestDialog(TaskDetailedActivity.this,
				"正在重新读取可见任务...");
		mDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				dbUtil.doAcessOkReq(userid, missionId);// 关注webservice
				// 跳转到可见任务页面
				goToTaskListActivity(3);
				// 销毁窗口
				mDialog.dismiss();
			}
		}).start();
	}

	/**
	 * 关注Activity中的取消关注
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
		mDialog = DialogFactory.creatRequestDialog(TaskDetailedActivity.this,
				"正在重新读取关注任务...");
		mDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				dbUtil.doAcessCancelReq(userid, missionId);// 取消关注webservice
				// 跳转到我的关注页面
				goToTaskListActivity(1);
				// 销毁窗口
				mDialog.dismiss();
			}
		}).start();
	}

	/**
	 * 加载下一条数据
	 * 
	 * @date 2013-6-13
	 */
	public void doGotoNextMissionDetailed() {
		updownFlag = 1;
		currentMissionId = missionId;// 保存当前missionid
		missionId = next_id;
		setViewData();// 重新加载数据
	}

	/**
	 * 加载上一条数据
	 * 
	 * @date 2013-6-13
	 */
	public void doGotoForMissionDetailed() {
		updownFlag = 0;
		currentMissionId = missionId;// 保存当前missionid
		missionId = for_id;
		setViewData();// 重新加载数据
	}

	/**
	 * 跳转我的任务显示页面
	 */
	private void goToTaskListActivity(final int flag) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("taskFlag", flag);
				intent.setClass(getApplicationContext(), TaskListActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
				finish();
			}
		});
	}

	/**
	 * 设置listView
	 */
	private void setViewData() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = DialogFactory.creatRequestDialog(this, "正在加载数据...");
		mDialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 访问webservice获取数据
				switch (taskFlag) {
				case 1:// 我的关注
					list = dbUtil.selectWatchMissionDetailedInfo(missionId,
							userid);
					break;
				case 2:// 我的任务
					list = dbUtil
							.selectMyMissionDetailedInfo(missionId, userid);
					break;
				case 3:// 可见任务
					list = dbUtil.selectCanSeeMissionDetailedInfo(missionId,
							userid, rolename, department_name);
					break;
				default:
					list = new ArrayList<String>();
					break;
				}
				// 更新界面
				updateView();
				// 销毁窗口
				mDialog.dismiss();
			}
		}).start();
	}

	/**
	 * 更新视图
	 * 
	 * @date 2013-6-6
	 */
	public void updateView() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (list.size() > 0) {
					if (list.get(0).equals("0")) {
						txt_tasktype.setText("自主式");
					} else {
						txt_tasktype.setText("派发式");
					}
					txt_response_person.setText(list.get(1).toString());
					txt_taskid.setText(list.get(2).toString());
					txt_task_creator.setText(list.get(3).toString());
					txt_task_yanshou.setText(list.get(4).toString());
					txt_task_name.setText(list.get(5).toString());
					txt_task_startdate.setText(list.get(6).toString());
					txt_task_enddate.setText(list.get(7).toString());
					txt_task_mj.setText(list.get(8).toString());
					txt_task_status.setText(list.get(9).toString());
					txt_task_fenguan.setText(list.get(10).toString());
					txt_task_writedate.setText(list.get(11).toString());
					txt_task_des.setText(list.get(12).toString());
					txt_task_counts.setText(list.get(13).toString());

					missionId = list.get(14).toString();
					next_id = list.get(15).toString();
					for_id = list.get(16).toString();
				} else {
					missionId = currentMissionId;// 重置为当前missionid
					switch (updownFlag) {
					case 0:
						Toast.makeText(getApplicationContext(), "已经到顶了", 0)
								.show();
						break;
					case 1:
						Toast.makeText(getApplicationContext(), "已经到底了", 0)
								.show();
						break;
					}
				}
			}
		});
	}

	/**
	 * 设置触屏事件
	 * 
	 * @date 2013-6-14
	 */
	public void initTouchListener() {
		/**
		 * 设置触屏滑动事件
		 */
		mGestureDetector = new GestureDetector(
				(OnGestureListener) new OnGestureAndTouchAdapter() {
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						if (e1.getX() - e2.getX() > 100
								&& Math.abs(velocityX) > 0) {// 向左手势
							doGotoForMissionDetailed();// 上一页
						} else if (e2.getX() - e1.getX() > 100
								&& Math.abs(velocityX) > 0) {// 向右手势
							doGotoNextMissionDetailed();// 下一页
						}
						return false;
					}
				});

		touchLayout = (LinearLayout) findViewById(R.id.touchlayout);
		touchLayout.setOnTouchListener(new OnGestureAndTouchAdapter() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return mGestureDetector.onTouchEvent(event);
			}
		});
		touchLayout.setLongClickable(true);
	}

	/**
	 * 添加返回菜单，退出按钮
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			goToTaskListActivity(taskFlag);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
