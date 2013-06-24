package com.wewin.flowmobilesys.car;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.wewin.flowmobilesys.DialogFactory;
import com.wewin.flowmobilesys.GlobalApplication;
import com.wewin.flowmobilesys.R;
import com.wewin.flowmobilesys.adapter.OptionsAdapter;
import com.wewin.flowmobilesys.menu.ActionItem;
import com.wewin.flowmobilesys.menu.TitlePopup;
import com.wewin.flowmobilesys.util.DBUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 申请详细Activity
 * 
 * @author HCOU
 * @date 2013-6-18
 */
public class AppDetailedActivity extends Activity implements Callback {
	private TextView detailedTitle;
	private String app_id, car_id, userId;
	private DBUtil dbUtil;
	private Dialog mDialog;
	private Handler handler, showBoxHandler;
	private List<String> list;
	private Button control_btn;// 头部菜单按钮
	private TitlePopup titlePopup;
	private int flag;
	private int editflag = 0;// 编辑、添加标记
	private int pwidth;
	// 下拉框选项数据源
	private ArrayList<String> datas = new ArrayList<String>();
	// 展示所有下拉选项的ListView
	private ListView listView = null;
	// 自定义Adapter
	private OptionsAdapter optionsAdapter = null;
	// PopupWindow对象
	private PopupWindow selectPopupWindow = null;

	private LinearLayout layout, addBtnlayout, carlist_layout;
	private Button showbox_btn;
	private Button datapicker_btn1, datapicker_btn2;// 显示时间按钮
	private Button complete_btn, cancle_btn, add_btn;// 完成和取消按钮
	private int mYear, mMonth, mDay;
	private static final int DATE_DIALOG_ID = 1;
	private static final int SHOW_DATAPICK = 0;

	private EditText txt_addcarname, txt_begintime, txt_endtime, txt_personnum,
			txt_reason, txt_destination, txt_remark;

	// 是否初始化完成标志
	private boolean initflag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appdetailed);
		initView();
	}

	/**
	 * 初始化界面
	 * 
	 * @date 2013-6-6
	 */
	private void initView() {
		Intent intent = getIntent();

		app_id = intent.getStringExtra("app_id");
		editflag = intent.getIntExtra("editflag", 0);

		// 得到全局用户ID
		userId = ((GlobalApplication) getApplication()).getUserId();

		dbUtil = new DBUtil();
		handler = new Handler();

		detailedTitle = (TextView) findViewById(R.id.detailedTitle);
		detailedTitle.setText("申请明细");// 设置标题栏
		control_btn = (Button) findViewById(R.id.carcontrol_btn);// 标题栏菜单按钮
		control_btn.setOnClickListener(new TitleButtnOnclickLisenter());

		datapicker_btn1 = (Button) findViewById(R.id.datapicker_btn1);// 时间按钮
		datapicker_btn1
				.setOnClickListener(new DatePickerButtnOnclickLisenter());
		datapicker_btn2 = (Button) findViewById(R.id.datapicker_btn2);// 时间按钮
		datapicker_btn2
				.setOnClickListener(new DatePickerButtnOnclickLisenter());

		carlist_layout = (LinearLayout) findViewById(R.id.carlist_layout);// 第一个combobox布局

		layout = (LinearLayout) findViewById(R.id.btn_layout);
		complete_btn = (Button) findViewById(R.id.complete_btn);
		complete_btn.setOnClickListener(new CompleteCancleAddOnclickLisenter());
		cancle_btn = (Button) findViewById(R.id.cancle_btn);
		cancle_btn.setOnClickListener(new CompleteCancleAddOnclickLisenter());

		addBtnlayout = (LinearLayout) findViewById(R.id.btn_layout_add);
		add_btn = (Button) findViewById(R.id.add_btn);
		add_btn.setOnClickListener(new CompleteCancleAddOnclickLisenter());

		txt_addcarname = (EditText) findViewById(R.id.txt_addcarname);
		txt_begintime = (EditText) findViewById(R.id.txt_begintime);
		txt_endtime = (EditText) findViewById(R.id.txt_endtime);
		txt_personnum = (EditText) findViewById(R.id.txt_personnum);
		txt_reason = (EditText) findViewById(R.id.txt_reason);
		txt_destination = (EditText) findViewById(R.id.txt_destination);
		txt_remark = (EditText) findViewById(R.id.txt_remark);

		initTitleMenu();// 初始化顶部菜单
		setDateTime();// 设置时间框的初始值
		/**
		 * 设置标题栏菜单是否可见,和底部添加按钮
		 */
		switch (editflag) {
		case 1:// Add
			control_btn.setVisibility(View.GONE);
			addBtnlayout.setVisibility(View.VISIBLE);
			carlist_layout.setVisibility(View.GONE);// 隐藏车辆选择框 //TODO

			datapicker_btn1.setVisibility(View.VISIBLE);
			datapicker_btn2.setVisibility(View.VISIBLE);
			txt_personnum.setFocusableInTouchMode(true);
			txt_reason.setFocusableInTouchMode(true);
			txt_destination.setFocusableInTouchMode(true);
			txt_remark.setFocusableInTouchMode(true);
			break;
		case 2:// Edit
			carlist_layout.setVisibility(View.GONE);
			control_btn.setVisibility(View.VISIBLE);
			addBtnlayout.setVisibility(View.GONE);
			setViewData();// 查询页面要显示的数据
			break;
		default:
			break;
		}
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
		titlePopup.addAction(new ActionItem(this, "编辑", R.drawable.edit_mini));
		titlePopup.addAction(new ActionItem(this, "删除",
				R.drawable.recycle_mini_2));
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
			titlePopup.show(v);
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
			case 0:// 第一个按钮,编辑
				activeEditing();// 激活输入框
				break;
			case 1:// 删除
				doDeleteReqAndShowDialog();
				break;
			default:
				break;
			}
			titlePopup.dismiss();
		}
	}

	/**
	 * 完成取消按钮点击事件
	 * 
	 * @author HCOU
	 * @date 2013-6-19
	 */
	class CompleteCancleAddOnclickLisenter implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.complete_btn:
				if (checkNull()) {
					doupdateReqAndShowDialog();// 完成编辑
				} else {
					Toast.makeText(AppDetailedActivity.this, "前3项为必填内容", 0)
							.show();
				}
				break;
			case R.id.cancle_btn:
				cancleEditing();// 取消编辑
				break;
			case R.id.add_btn:
				if (checkNull()) {
					doaddReqAndShowDialog();// 添加
				} else {
					Toast.makeText(AppDetailedActivity.this, "前3项为必填内容", 0)
							.show();
				}
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 检查输入框时候为空
	 * 
	 * @date 2013-6-20
	 * @return
	 */
	public boolean checkNull() {
		if (txt_begintime.getText().toString().equals("")) {
			return false;
		} else if (txt_endtime.getText().toString().equals("")) {
			return false;
		} else if (txt_personnum.getText().toString().equals("")) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 激活输入框
	 * 
	 * @date 2013-6-19
	 */
	public void activeEditing() {
		datapicker_btn1.setVisibility(View.VISIBLE);
		datapicker_btn2.setVisibility(View.VISIBLE);
		layout.setVisibility(View.VISIBLE);

		txt_personnum.setFocusableInTouchMode(true);
		txt_reason.setFocusableInTouchMode(true);
		txt_destination.setFocusableInTouchMode(true);
		txt_remark.setFocusableInTouchMode(true);
	}

	/**
	 * 取消输入
	 * 
	 * @date 2013-6-19
	 */
	public void cancleEditing() {
		datapicker_btn1.setVisibility(View.GONE);
		datapicker_btn2.setVisibility(View.GONE);
		layout.setVisibility(View.GONE);

		txt_personnum.setFocusable(false);
		txt_reason.setFocusable(false);
		txt_destination.setFocusable(false);
		txt_remark.setFocusable(false);
	}

	/**
	 * 访问编辑申请webservice
	 * 
	 * @date 2013-6-18
	 */
	public void doUpdateReq() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = DialogFactory.creatRequestDialog(AppDetailedActivity.this,
				"正在重新读取我的申请...");
		mDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				dbUtil.doUpdateCarAppReq(app_id, txt_begintime.getText()
						.toString(), txt_endtime.getText().toString(),
						txt_personnum.getText().toString(), txt_reason
								.getText().toString(), txt_destination
								.getText().toString(), txt_remark.getText()
								.toString());// 修改申请webservice
				// 跳转回申请页面
				goToCarAppListActivity();
				// 销毁窗口
				mDialog.dismiss();
			}
		}).start();
	}

	/**
	 * 申请详情Activity中的编辑修改
	 * 
	 * @date 2013-6-19
	 */
	public void doupdateReqAndShowDialog() {
		Dialog alertDialog = new AlertDialog.Builder(this).setTitle("提示")
				.setMessage("您确定修改该条申请吗？").setIcon(R.drawable.warning)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						doUpdateReq();
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
	 * 访问添加申请webservice
	 * 
	 * @date 2013-6-18
	 */
	public void doAddReq() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = DialogFactory.creatRequestDialog(AppDetailedActivity.this,
				"正在重新读取我的申请...");
		mDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				dbUtil.doAddCarAppReq(userId, "  ", "0", txt_begintime
						.getText()// TODO
						.toString(), txt_endtime.getText().toString(),
						txt_personnum.getText().toString(), txt_reason
								.getText().toString(), txt_destination
								.getText().toString(), txt_remark.getText()
								.toString());// 添加申请webservice
				// 跳转回申请页面
				goToCarAppListActivity();
				// 销毁窗口
				mDialog.dismiss();
			}
		}).start();
	}

	/**
	 * 申请详情Activity中的添加
	 * 
	 * @date 2013-6-20
	 */
	public void doaddReqAndShowDialog() {
		Dialog alertDialog = new AlertDialog.Builder(this).setTitle("提示")
				.setMessage("您确定添加该条申请吗？").setIcon(R.drawable.warning)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						doAddReq();
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
	 * @date 2013-6-19
	 */
	public void doDeleteReq() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = DialogFactory.creatRequestDialog(AppDetailedActivity.this,
				"正在重新读取我的申请...");
		mDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				dbUtil.doDeleteCarAppReq(app_id);// 删除申请webservice
				// 跳转回申请页面
				goToCarAppListActivity();
				// 销毁窗口
				mDialog.dismiss();
			}
		}).start();
	}

	/**
	 * 申请详情Activity中的删除
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
	 * 跳转我的任务显示页面
	 */
	private void goToCarAppListActivity() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), CarListActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	/**
	 * 跳转到CarListActivity
	 * 
	 * @date 2013-6-7
	 */
	public void goToMyCarAppActivity() {
		Intent intent = new Intent();
		intent.setClass(this, CarListActivity.class);
		startActivity(intent);
		finish();
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
				list = dbUtil.selectCarAppDetailedInfo(app_id);
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
				txt_begintime.setText(list.get(0).toString());
				txt_endtime.setText(list.get(1).toString());
				txt_personnum.setText(list.get(2).toString());
				txt_reason.setText(list.get(3).toString());
				txt_destination.setText(list.get(4).toString());
				txt_remark.setText(list.get(5).toString());
			}
		});
	}

	/**
	 * 时间按钮响应事件
	 * 
	 * @author HCOu
	 * @date 2013-6-18
	 */
	class DatePickerButtnOnclickLisenter implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.datapicker_btn1:
				flag = 1;

				Message msg = new Message();
				msg.what = AppDetailedActivity.SHOW_DATAPICK;
				AppDetailedActivity.this.saleHandler.sendMessage(msg);
				break;
			case R.id.datapicker_btn2:
				flag = 2;

				Message msg2 = new Message();
				msg2.what = AppDetailedActivity.SHOW_DATAPICK;
				AppDetailedActivity.this.saleHandler.sendMessage(msg2);
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 处理日期控件的Handler
	 */
	Handler saleHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AppDetailedActivity.SHOW_DATAPICK:
				showDialog(DATE_DIALOG_ID);
				break;
			}
		}
	};

	/**
	 * 
	 * 日期控件的事件
	 */
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			// 更新输入框
			updateDisplay(flag);
		}
	};

	/**
	 * 更新日期
	 */
	private void updateDisplay(int disflag) {
		switch (disflag) {
		case 1:
			txt_begintime.setText(new StringBuilder()
					.append(mYear)
					.append(".")
					.append((mMonth + 1) < 10 ? "0" + (mMonth + 1)
							: (mMonth + 1)).append(".")
					.append((mDay < 10) ? "0" + mDay : mDay));
			break;
		case 2:
			txt_endtime.setText(new StringBuilder()
					.append(mYear)
					.append(".")
					.append((mMonth + 1) < 10 ? "0" + (mMonth + 1)
							: (mMonth + 1)).append(".")
					.append((mDay < 10) ? "0" + mDay : mDay));
			break;
		default:
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		}
		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DATE_DIALOG_ID:
			((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
			break;
		}
	}

	/**
	 * 初始化时间显示框
	 * 
	 * @date 2013-6-18
	 */
	private void setDateTime() {
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 添加返回菜单，退出按钮
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			goToMyCarAppActivity();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 没有在onCreate方法中调用initBoxWedget()，而是在onWindowFocusChanged方法中调用，
	 * 是因为initWedget()中需要获取PopupWindow浮动下拉框依附的组件宽度，在onCreate方法中是无法获取到该宽度的
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		while (!initflag) {
			initBoxWedget();
			initflag = true;
		}
	}

	/**
	 * 初始化界面控件
	 * 
	 * @date 2013-6-19
	 */
	private void initBoxWedget() {
		// 初始化Handler,用来处理消息
		showBoxHandler = new Handler(AppDetailedActivity.this);
		// 初始化界面组件
		showbox_btn = (Button) findViewById(R.id.showbox_btn);
		// 获取下拉框依附的组件宽度
		int width = txt_addcarname.getWidth();
		pwidth = width;
		// 设置点击下拉箭头图片事件，点击弹出PopupWindow浮动下拉框
		showbox_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (initflag) {
					// 显示PopupWindow窗口
					popupWindwShowing();
				}
			}
		});
		// 初始化PopupWindow
		initDatasAndWindow();
	}

	/**
	 * 查询数据和更新窗口
	 * 
	 * @date 2013-6-19
	 */
	private void initDatasAndWindow() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				datas = dbUtil.selectAllCars();// 查询车辆数据
				handler.post(new Runnable() {// 更新界面
					@Override
					public void run() {
						initPopuWindow();
					}
				});
			}
		}).start();
	}

	/**
	 * 初始化PopupWindow
	 * 
	 * @date 2013-6-19
	 */
	private void initPopuWindow() {
		// PopupWindow浮动下拉框布局
		View loginwindow = (View) this.getLayoutInflater().inflate(
				R.layout.options, null);
		listView = (ListView) loginwindow.findViewById(R.id.listoptions);

		// 设置自定义Adapter
		optionsAdapter = new OptionsAdapter(this, showBoxHandler, datas);
		listView.setAdapter(optionsAdapter);
		selectPopupWindow = new PopupWindow(loginwindow, pwidth,
				LayoutParams.WRAP_CONTENT, true);
		selectPopupWindow.setOutsideTouchable(true);
		// 这一句是为了实现弹出PopupWindow后，当点击屏幕其他部分及Back键时PopupWindow会消失，
		// 没有这一句则效果不能出来，但并不会影响背景
		// 本人能力极其有限，不明白其原因，还望高手、知情者指点一下
		selectPopupWindow.setBackgroundDrawable(new BitmapDrawable());
	}

	/**
	 * 显示PopupWindow窗口
	 * 
	 * @date 2013-6-19
	 */
	public void popupWindwShowing() {
		// 将selectPopupWindow作为parent的下拉框显示，并指定selectPopupWindow在Y方向上向上偏移3pix，
		// 这是为了防止下拉框与文本框之间产生缝隙，影响界面美化
		// （是否会产生缝隙，及产生缝隙的大小，可能会根据机型、Android系统版本不同而异吧，不太清楚）
		selectPopupWindow.showAsDropDown(txt_addcarname, 0, 0);
	}

	/**
	 * PopupWindow消失
	 * 
	 * @date 2013-6-19
	 */
	public void dismiss() {
		selectPopupWindow.dismiss();
	}

	/**
	 * 处理Hander消息
	 */
	@Override
	public boolean handleMessage(Message message) {
		Bundle data = message.getData();
		switch (message.what) {
		case 1:
			// 选中下拉项，下拉框消失
			int selIndex = data.getInt("selIndex");
			txt_addcarname.setText(datas.get(selIndex).split("\\$")[0]);
			car_id = datas.get(selIndex).split("\\$")[1];

			dismiss();
			break;
		}
		return false;
	}
}
