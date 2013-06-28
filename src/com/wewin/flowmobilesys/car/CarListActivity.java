package com.wewin.flowmobilesys.car;

import java.util.HashMap;
import java.util.List;
import com.wewin.flowmobilesys.DialogFactory;
import com.wewin.flowmobilesys.GlobalApplication;
import com.wewin.flowmobilesys.R;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * 车辆显示Activity
 * 
 * @author HCOU
 * @date 2013-6-18
 */
public class CarListActivity extends Activity {
	private ListView listView;
	private TextView listTitle;
	private WebServiceUtil dbUtil;
	private Dialog mDialog;
	private Handler handler;
	private SimpleAdapter adapter;
	private String userId = "";
	private List<HashMap<String, String>> list;
	private String app_id;// 申请ID
	private Button addmenu_btn;
	TabMenu.MenuBodyAdapter bodyAdapter;
	TabMenu tabMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.carapplist);
		initView();
	}

	private void initView() {
		listView = (ListView) findViewById(R.id.carlistview);
		listView.setOnItemClickListener(new MyItemClickListhener());// 注册点击事件

		listTitle = (TextView) findViewById(R.id.carlistTitle);
		dbUtil = new WebServiceUtil();
		handler = new Handler();

		/**
		 * 添加按钮
		 */
		addmenu_btn = (Button) findViewById(R.id.addmenu_btn);
		addmenu_btn.setOnClickListener(new AddMenuOnClicklistener());

		// 得到全局用户ID
		userId = ((GlobalApplication) getApplication()).getUserId();

		/**
		 * 设置弹出菜单图标
		 */
		bodyAdapter = new TabMenu.MenuBodyAdapter(this, new int[] {
				R.drawable.menu2, R.drawable.recycle }, new String[] { "申请明细",
				"删除申请" });

		tabMenu = new TabMenu(this, new BodyClickEvent(), R.drawable.login_bg);// 出现与消失的动画
		tabMenu.update();
		tabMenu.SetBodyAdapter(bodyAdapter);
		setView();
	}

	class AddMenuOnClicklistener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.addmenu_btn:
				gotoDetailedActivity(1);// 添加不编辑
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
				// 得到我的申请
				list = dbUtil.selectMyCarAppInfo(userId);
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
			app_id = ((TextView) view.findViewById(R.id.txt_id)).getText()
					.toString();

			int[] positions = new int[2];
			view.getLocationInWindow(positions);

			tabMenu.showAtLocation(view, Gravity.TOP, positions[0],
					positions[1]);
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
						R.layout.adapter_item_car, new String[] { "id",
								"carid", "username", "addtime", "carnum",
								"destination", "status" }, new int[] {
								R.id.txt_id, R.id.txt_carid, R.id.txt_username,
								R.id.txt_addtime, R.id.txt_carnum,
								R.id.txt_destination, R.id.txt_carstatus });
				listView.setAdapter(adapter);

				listTitle.setText("我的申请");
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
				// 跳转申请详细
				gotoDetailedActivity(2);// 可编辑
				break;
			case 1:
				// 删除申请
				doDeleteReqAndShowDialog();
				break;
			default:
				break;
			}
			tabMenu.dismiss();// 销毁弹出菜单
		}
	}

	/**
	 * 跳转申请详细Activity
	 * 
	 * @date 2013-6-6
	 */
	public void gotoDetailedActivity(int editflag) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("app_id", app_id);// 传送app_id
		bundle.putInt("editflag", editflag);// 是否编辑标记
		intent.setClass(this, AppDetailedActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
	}

	/**
	 * 跳转主页面
	 */
	public void goMainActivity() {
		Intent intent = new Intent();
		intent.setClass(this, CarMainActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 我的申请Activity中的删除
	 * 
	 * @date 2013-6-5
	 */
	public void doDeleteReqAndShowDialog() {
		Dialog alertDialog = new AlertDialog.Builder(this).setTitle("提示")
				.setMessage("您确定删除该条申请吗？").setIcon(R.drawable.warning)
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
		mDialog = DialogFactory.creatRequestDialog(CarListActivity.this,
				"正在重新读取我的申请...");
		mDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				dbUtil.doDeleteCarAppReq(app_id);// 删除申请webservice
				list = dbUtil.selectMyCarAppInfo(userId);// 重新读取我的申请
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
			goMainActivity();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}