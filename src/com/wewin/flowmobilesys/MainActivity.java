package com.wewin.flowmobilesys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.wewin.flowmobilesys.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 主菜单Activity
 * 
 * @author HCOU
 * @time 2013.05.28 17:37:00
 */
public class MainActivity extends Activity {
	private GridView mGridView;// 菜单grid
	private ButtomMenu buttomMenu;// 底部菜单
	private TextView title;// 标题栏
	private int[] imageRes = { R.drawable.tasklist, R.drawable.watch,
			R.drawable.settings, R.drawable.datachart };
	private String[] itemName = { "我的任务", "我的关注", "可见任务", "数据总概" };
	private long exitTime = 0;// 退出倒计时
	private Button loginother_btn, exit_btn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();// 初始化界面
	}

	private void initView() {
		mGridView = (GridView) findViewById(R.id.MenuGridView);
		title = (TextView) findViewById(R.id.main_tilte);
		title.setText("重庆移动网络部项目管理平台");

		List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		int length = imageRes.length;
		for (int i = 0; i < length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImageView", imageRes[i]);
			map.put("ItemTextView", itemName[i]);
			data.add(map);
		}
		SimpleAdapter mSimpleAdapter = new SimpleAdapter(MainActivity.this,
				data, R.layout.menuitem, new String[] { "ItemImageView",
						"ItemTextView" }, new int[] { R.id.ItemImageView,
						R.id.ItemTextView });
		mGridView.setAdapter(mSimpleAdapter);
		mGridView.setOnItemClickListener(new GridViewItemOnClick());

		buttomMenu = new ButtomMenu();// 添加隐藏菜单

		loginother_btn = (Button) findViewById(R.id.loginother_btn);// 切换帐号按钮
		loginother_btn.setOnClickListener(new LoginExitBtnOnclickListener());
		exit_btn = (Button) findViewById(R.id.exit_btn);// 退出按钮
		exit_btn.setOnClickListener(new LoginExitBtnOnclickListener());
	}

	/*
	 * 底部隐藏按钮点击事件
	 */
	public class LoginExitBtnOnclickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.loginother_btn:// 切换帐号
				goToLoginActivity();
				break;
			case R.id.exit_btn:// 退出
				finish();
				System.exit(0);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 跳转到登录页面
	 * 
	 * @date 2013-6-20
	 */
	public void goToLoginActivity() {
		Intent intent = new Intent();
		intent.setClass(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	/*
	 * 菜单点击事件
	 */
	public class GridViewItemOnClick implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			buttomMenu.showBottom(true);// 收起底部菜单
			switch (position) {
			case 0:
				goToMyTaskListActivity();
				break;
			case 1:
				goToWatchTaskListActivity();
				break;
			case 2:
				goToCanSeeActivity();
				break;
			case 3:
				goToDataChartActivity();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 跳转到chartActivity
	 * 
	 * @date 2013-6-7
	 */
	public void goToDataChartActivity() {
		Intent intent = new Intent();
		intent.setClass(this, DataChartActivity.class);
		startActivity(intent);
	}

	/**
	 * 跳转可见任务菜单页面
	 */
	public void goToCanSeeActivity() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putInt("taskFlag", 3);
		intent.setClass(this, TaskListActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/**
	 * 跳转我的关注显示页面
	 */
	private void goToWatchTaskListActivity() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putInt("taskFlag", 1);
		intent.setClass(this, TaskListActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/**
	 * 跳转我的任务显示页面
	 */
	private void goToMyTaskListActivity() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putInt("taskFlag", 2);
		intent.setClass(this, TaskListActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/**
	 * 隐藏菜单内部类
	 * 
	 * @author HCOU
	 * @date 2013-5-29
	 */
	public class ButtomMenu {
		private View moreHideBottomView, input2;
		private ImageView more_imageView;
		private boolean mShowBottom = false;// 底部显示标记

		public ButtomMenu() {
			/*
			 * 更多选项隐藏菜单
			 */
			moreHideBottomView = findViewById(R.id.morehidebottom);
			more_imageView = (ImageView) findViewById(R.id.more_image);

			input2 = findViewById(R.id.input2);
			input2.setOnClickListener(new inputOnClick());

			showBottom(true);// 初始化时，隐藏
		}

		public class inputOnClick implements OnClickListener {
			@Override
			public void onClick(View v) {
				showBottom(!mShowBottom);
			}
		}

		public void showBottom(boolean bShow) {
			if (bShow) {
				moreHideBottomView.setVisibility(View.GONE);
				more_imageView.setImageResource(R.drawable.login_more_up);
				mShowBottom = true;
			} else {
				moreHideBottomView.setVisibility(View.VISIBLE);
				more_imageView.setImageResource(R.drawable.login_more);
				mShowBottom = false;
			}
		}
	}

	/**
	 * 添加返回菜单，退出按钮
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序", 0).show();
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