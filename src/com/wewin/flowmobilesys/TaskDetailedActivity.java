package com.wewin.flowmobilesys;

import java.util.List;
import com.wewin.flowmobilesys.util.DBUtil;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

/**
 * 任务显示Activity
 * 
 * @author HCOU
 * @date 2013-6-5
 */
public class TaskDetailedActivity extends Activity {
	private TextView detailedTitle;
	private String missionId;
	private DBUtil dbUtil;
	private Dialog mDialog;
	private Handler handler;
	private List<String> list;
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

	private void initView() {
		Intent intent = getIntent();
		missionId = intent.getStringExtra("missionId");
		dbUtil = new DBUtil();
		handler = new Handler();
		detailedTitle = (TextView) findViewById(R.id.detailedTitle);
		detailedTitle.setText("任务详细");// 设置标题栏

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

		setViewData();
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
				list = dbUtil.selectDetailedMissionInfo(missionId);
				// 更新界面
				updateView();
				// 销毁窗口
				mDialog.dismiss();
			}
		}).start();
	}

	public void updateView() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				txt_tasktype.setText(list.get(0));
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
			}
		});
	}
}
