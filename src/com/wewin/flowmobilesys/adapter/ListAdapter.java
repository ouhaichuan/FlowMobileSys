package com.wewin.flowmobilesys.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.wewin.flowmobilesys.R;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 自定义ListAdapter
 * 
 * @author HCOU
 * @date 2013-6-24
 */
public class ListAdapter extends BaseAdapter {

	private List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
	private Context context = null;

	/**
	 * 自定义构造方法
	 * 
	 * @param activity
	 * @param handler
	 * @param list
	 */
	public ListAdapter(Context context, List<HashMap<String, String>> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ListViewHolder holder = null;
		if (convertView == null) {
			holder = new ListViewHolder();
			// 下拉项布局
			convertView = LayoutInflater.from(context).inflate(
					R.layout.adapter_item, null);
			holder.txt_missionid = (TextView) convertView
					.findViewById(R.id.txt_missionid);
			holder.txt_createUserName = (TextView) convertView
					.findViewById(R.id.txt_createUserName);
			holder.txt_Title = (TextView) convertView
					.findViewById(R.id.txt_Title);
			holder.txt_beginTime = (TextView) convertView
					.findViewById(R.id.txt_beginTime);
			holder.txt_endTime = (TextView) convertView
					.findViewById(R.id.txt_endTime);
			holder.txt_status = (TextView) convertView
					.findViewById(R.id.txt_status);
			holder.txt_counts = (TextView) convertView
					.findViewById(R.id.txt_counts);

			convertView.setTag(holder);
		} else {
			holder = (ListViewHolder) convertView.getTag();
		}

		HashMap<String, String> map = list.get(position);
		holder.txt_missionid.setText(map.get("missionId"));
		holder.txt_createUserName.setText(map.get("createUserName"));
		holder.txt_Title.setText(map.get("Title"));
		holder.txt_beginTime.setText(map.get("beginTime"));
		holder.txt_endTime.setText(map.get("endTime"));
		holder.txt_status.setText(map.get("status"));
		holder.txt_counts.setText(map.get("counts"));

		if (map.get("importance").equals("1")) {
			convertView.setBackgroundColor(Color.RED);
		} else if (map.get("importance").equals("2")) {
			convertView.setBackgroundColor(Color.DKGRAY);
		}
		return convertView;
	}
}

class ListViewHolder {
	TextView txt_missionid;
	TextView txt_createUserName;
	TextView txt_Title;
	TextView txt_beginTime;
	TextView txt_endTime;
	TextView txt_status;
	TextView txt_counts;
}
