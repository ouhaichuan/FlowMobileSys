package com.wewin.flowmobilesys;

import java.util.List;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import com.wewin.flowmobilesys.service.WebServiceUtil;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * chartPie Activity
 * 
 * @author HCOU
 * @date 2013-6-7
 */
public class DataChartActivity extends Activity {
	private TextView dataChartTitle;
	private List<String> list;
	private String userId, rolename, department_name;
	private WebServiceUtil dbUtil;
	private Dialog mDialog;
	private Handler handler;

	/** Colors to be used for the pie slices. */
	private static int[] COLORS = new int[] { Color.GREEN, Color.BLUE,
			Color.MAGENTA, Color.CYAN, Color.RED, Color.GRAY };
	/** The main series that will include all the data. */
	private CategorySeries mSeries = new CategorySeries("");
	/** The main renderer for the main dataset. */
	private DefaultRenderer mRenderer = new DefaultRenderer();
	/** The chart view that displays the data. */
	private GraphicalView mChartView;

	@Override
	protected void onRestoreInstanceState(Bundle savedState) {
		super.onRestoreInstanceState(savedState);
		mSeries = (CategorySeries) savedState.getSerializable("current_series");
		mRenderer = (DefaultRenderer) savedState
				.getSerializable("current_renderer");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("current_series", mSeries);
		outState.putSerializable("current_renderer", mRenderer);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.datachart);
		initView();
	}

	/**
	 * 初始化界面
	 * 
	 * @date 2013-6-6
	 */
	private void initView() {
		// 得到全局用户ID
		userId = ((GlobalApplication) getApplication()).getUserId();
		// 得到全局用户角色名称
		rolename = ((GlobalApplication) getApplication()).getRolename();
		// 得到全局用户部门名称
		department_name = ((GlobalApplication) getApplication())
				.getDepartment_name();

		dbUtil = new WebServiceUtil();
		handler = new Handler();
		dataChartTitle = (TextView) findViewById(R.id.dataChartTitle);
		if (rolename.equals("普通员工") || rolename.equals("主管"))
			dataChartTitle.setText("个人数据总概");// 设置标题栏
		else if (rolename.equals("部门经理"))
			dataChartTitle.setText(department_name + "数据总概");// 设置标题栏
		else if (rolename.equals("副总经理") || rolename.equals("总经理"))
			dataChartTitle.setText("总体数据总概");// 设置标题栏
		else
			dataChartTitle.setText("个人数据总概");// 设置标题栏

		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setStartAngle(180);
		mRenderer.setDisplayValues(true);
		mRenderer.setLegendTextSize(40.0f);
		mRenderer.setLabelsColor(Color.WHITE);
		mRenderer.setLabelsTextSize(40.0f);

		setViewData();
	}

	/**
	 * 设置chartView
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
				// 调用webservice查询，统计信息
				list = dbUtil
						.doFindChartData(userId, rolename, department_name);

				mSeries.add("完成 ", Double.parseDouble(list.get(0)));
				SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
				renderer.setColor(COLORS[(mSeries.getItemCount() - 1)
						% COLORS.length]);
				mRenderer.addSeriesRenderer(renderer);

				mSeries.add("超时", Double.parseDouble(list.get(1)));
				SimpleSeriesRenderer renderer1 = new SimpleSeriesRenderer();
				renderer1.setColor(COLORS[(mSeries.getItemCount() - 1)
						% COLORS.length]);
				mRenderer.addSeriesRenderer(renderer1);

				mSeries.add("进行中 ", Double.parseDouble(list.get(2)));
				SimpleSeriesRenderer renderer2 = new SimpleSeriesRenderer();
				renderer2.setColor(COLORS[(mSeries.getItemCount() - 1)
						% COLORS.length]);
				mRenderer.addSeriesRenderer(renderer2);

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
				mChartView.repaint();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mChartView == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
			mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);
			mRenderer.setClickEnabled(true);
			mChartView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SeriesSelection seriesSelection = mChartView
							.getCurrentSeriesAndPoint();
					if (seriesSelection == null) {
					} else {
						for (int i = 0; i < mSeries.getItemCount(); i++) {
							mRenderer.getSeriesRendererAt(i).setHighlighted(
									i == seriesSelection.getPointIndex());
						}
						// index:0代表完成，1代表超时，2代表进行中
						goToTaskListActivity(seriesSelection.getPointIndex());
						mChartView.repaint();
					}
				}
			});
			layout.addView(mChartView, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		} else {
			mChartView.repaint();
		}
	}

	public void goToTaskListActivity(int index) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putInt("taskFlag", 5);
		bundle.putString("index", index + "");
		intent.setClass(getApplicationContext(), TaskListActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}
}
