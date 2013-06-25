package com.wewin.flowmobilesys.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.wewin.flowmobilesys.http.HttpConnSoap;

/**
 * 数据中间层，访问Soap，并处理获得的数据
 * 
 * @author HCOU
 * @time 2013.05.27 17:25:00
 */
public class DBUtil {
	private ArrayList<String> arrayList = new ArrayList<String>();
	private ArrayList<String> brrayList = new ArrayList<String>();
	private ArrayList<String> crrayList = new ArrayList<String>();
	private HttpConnSoap Soap = new HttpConnSoap();

	/**
	 * 验证用户登录
	 */
	public List<String> doLogin(String userid, String password) {
		arrayList.clear();
		brrayList.clear();

		arrayList.add("userid");
		arrayList.add("password");
		brrayList.add(userid);
		brrayList.add(password);

		crrayList = Soap.GetWebServre("doLogin", arrayList, brrayList);
		return crrayList;
	}

	/**
	 * 获取用户密码
	 * 
	 * @date 2013-6-4
	 * @param userid
	 * @return
	 */
	public List<String> doFindPassWord(String userid) {
		arrayList.clear();
		brrayList.clear();

		arrayList.add("userid");
		brrayList.add(userid);

		crrayList = Soap.GetWebServre("doFindPassWord", arrayList, brrayList);
		return crrayList;
	}

	/**
	 * 获取统计信息
	 * 
	 * @date 2013-6-7
	 * @param userid
	 * @return
	 */
	public List<String> doFindChartData(String userid) {
		arrayList.clear();
		brrayList.clear();

		arrayList.add("userid");
		brrayList.add(userid);

		crrayList = Soap.GetWebServre("doFindChartData", arrayList, brrayList);
		return crrayList;
	}

	/**
	 * 调用取消关注
	 * 
	 * @date 2013-6-5
	 * @param userid
	 * @param missionid
	 * @return
	 */
	public List<String> doAcessCancelReq(String userid, String missionid) {
		arrayList.clear();
		brrayList.clear();

		arrayList.add("userid");
		arrayList.add("missionid");
		brrayList.add(userid);
		brrayList.add(missionid);

		crrayList = Soap.GetWebServre("doAcessCancelReq", arrayList, brrayList);
		return crrayList;
	}

	/**
	 * 删除任务
	 * 
	 * @date 2013-6-5
	 * @param missionid
	 * @return
	 */
	public List<String> doDeleteReq(String missionid) {
		arrayList.clear();
		brrayList.clear();

		arrayList.add("missionid");
		brrayList.add(missionid);

		crrayList = Soap.GetWebServre("doDeleteReq", arrayList, brrayList);
		return crrayList;
	}

	/**
	 * 删除车辆申请
	 * 
	 * @date 2013-6-5
	 * @param userid
	 * @param missionid
	 * @return
	 */
	public List<String> doDeleteCarAppReq(String app_id) {
		arrayList.clear();
		brrayList.clear();

		arrayList.add("app_id");
		brrayList.add(app_id);

		crrayList = Soap
				.GetWebServre("doDeleteCarAppReq", arrayList, brrayList);
		return crrayList;
	}

	/**
	 * 调用关注WebService
	 * 
	 * @date 2013-6-5
	 * @param userId
	 * @param missionId
	 */
	public List<String> doAcessOkReq(String userId, String missionId) {
		arrayList.clear();
		brrayList.clear();

		arrayList.add("userid");
		arrayList.add("missionid");
		brrayList.add(userId);
		brrayList.add(missionId);

		crrayList = Soap.GetWebServre("doAcessOkReq", arrayList, brrayList);
		return crrayList;
	}

	/**
	 * 获取关注任务的信息
	 * 
	 * @return
	 */
	public List<HashMap<String, String>> selectWatchMissionInfo(String userId) {
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		arrayList.clear();
		brrayList.clear();
		crrayList.clear();

		// 传递用户编号
		arrayList.add("id");
		brrayList.add(userId);

		crrayList = Soap.GetWebServre("selectWatchMissionInfo", arrayList,
				brrayList);
		for (int j = 0; j < crrayList.size(); j += 8) {
			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("missionId", crrayList.get(j));
			hashMap.put("createUserName", crrayList.get(j + 1));
			hashMap.put("Title", crrayList.get(j + 2));
			hashMap.put("beginTime", crrayList.get(j + 3));
			hashMap.put("endTime", crrayList.get(j + 4));

			if ("1".equals(crrayList.get(j + 5)))
				hashMap.put("status", "进行中");
			else if ("2".equals(crrayList.get(j + 5)))
				hashMap.put("status", "即将超时");
			else if ("3".equals(crrayList.get(j + 5)))
				hashMap.put("status", "提交待审");
			else if ("4".equals(crrayList.get(j + 5)))
				hashMap.put("status", "已完成");
			else if ("5".equals(crrayList.get(j + 5)))
				hashMap.put("status", "超时完成");
			else if ("6".equals(crrayList.get(j + 5)))
				hashMap.put("status", "超时");

			hashMap.put("importance", crrayList.get(j + 6));// 重要性
			hashMap.put("counts", crrayList.get(j + 7));
			list.add(hashMap);
		}
		return list;
	}

	/**
	 * 获取我的任务中任务详细信息
	 * 
	 * @return
	 */
	public List<String> selectMyMissionDetailedInfo(String missionId,
			String userid) {
		arrayList.clear();
		brrayList.clear();
		crrayList.clear();

		// 传递任务编号
		arrayList.add("id");
		brrayList.add(missionId);
		// 传递用户编号
		arrayList.add("userid");
		brrayList.add(userid);

		crrayList = Soap.GetWebServre("selectMyMissionDetailedInfo", arrayList,
				brrayList);

		return crrayList;
	}

	/**
	 * 获取任务详细信息
	 * 
	 * @return
	 */
	public List<String> selectWatchMissionDetailedInfo(String missionId,
			String userid) {
		arrayList.clear();
		brrayList.clear();
		crrayList.clear();

		// 传递任务编号
		arrayList.add("id");
		brrayList.add(missionId);
		// 传递用户编号
		arrayList.add("userid");
		brrayList.add(userid);

		crrayList = Soap.GetWebServre("selectWatchMissionDetailedInfo",
				arrayList, brrayList);

		return crrayList;
	}

	/**
	 * 获取任务详细信息
	 * 
	 * @return
	 */
	public List<String> selectCanSeeMissionDetailedInfo(String missionId,
			String userid) {
		arrayList.clear();
		brrayList.clear();
		crrayList.clear();

		// 传递任务编号
		arrayList.add("id");
		brrayList.add(missionId);
		// 传递用户编号
		arrayList.add("userid");
		brrayList.add(userid);

		crrayList = Soap.GetWebServre("selectCanSeeMissionDetailedInfo",
				arrayList, brrayList);

		return crrayList;
	}

	/**
	 * 获取我的任务的信息
	 * 
	 * @return
	 */
	public List<HashMap<String, String>> selectMyMissionInfo(String userId) {
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		arrayList.clear();
		brrayList.clear();
		crrayList.clear();

		// 传递用户编号
		arrayList.add("id");
		brrayList.add(userId);

		crrayList = Soap.GetWebServre("selectMyMissionInfo", arrayList,
				brrayList);

		for (int j = 0; j < crrayList.size(); j += 8) {
			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("missionId", crrayList.get(j));
			hashMap.put("createUserName", crrayList.get(j + 1));
			hashMap.put("Title", crrayList.get(j + 2));
			hashMap.put("beginTime", crrayList.get(j + 3));
			hashMap.put("endTime", crrayList.get(j + 4));

			if ("1".equals(crrayList.get(j + 5)))
				hashMap.put("status", "进行中");
			else if ("2".equals(crrayList.get(j + 5)))
				hashMap.put("status", "即将超时");
			else if ("3".equals(crrayList.get(j + 5)))
				hashMap.put("status", "提交待审");
			else if ("4".equals(crrayList.get(j + 5)))
				hashMap.put("status", "已完成");
			else if ("5".equals(crrayList.get(j + 5)))
				hashMap.put("status", "超时完成");
			else if ("6".equals(crrayList.get(j + 5)))
				hashMap.put("status", "超时");

			hashMap.put("importance", crrayList.get(j + 6));// 重要性
			hashMap.put("counts", crrayList.get(j + 7));

			list.add(hashMap);
		}
		return list;
	}

	/**
	 * 获取可见任务的信息
	 * 
	 * @return
	 */
	public List<HashMap<String, String>> selectCanSeeMissionInfo(String userId) {
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		arrayList.clear();
		brrayList.clear();
		crrayList.clear();

		// 传递用户编号
		arrayList.add("id");
		brrayList.add(userId);

		crrayList = Soap.GetWebServre("selectCanSeeMissionInfo", arrayList,
				brrayList);

		for (int j = 0; j < crrayList.size(); j += 8) {
			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("missionId", crrayList.get(j));
			hashMap.put("createUserName", crrayList.get(j + 1));
			hashMap.put("Title", crrayList.get(j + 2));
			hashMap.put("beginTime", crrayList.get(j + 3));
			hashMap.put("endTime", crrayList.get(j + 4));

			if ("1".equals(crrayList.get(j + 5)))
				hashMap.put("status", "进行中");
			else if ("2".equals(crrayList.get(j + 5)))
				hashMap.put("status", "即将超时");
			else if ("3".equals(crrayList.get(j + 5)))
				hashMap.put("status", "提交待审");
			else if ("4".equals(crrayList.get(j + 5)))
				hashMap.put("status", "已完成");
			else if ("5".equals(crrayList.get(j + 5)))
				hashMap.put("status", "超时完成");
			else if ("6".equals(crrayList.get(j + 5)))
				hashMap.put("status", "超时");

			hashMap.put("importance", crrayList.get(j + 6));// 重要性

			if (!"0".equals(crrayList.get(j + 7)))
				hashMap.put("counts", "已关注");
			else
				hashMap.put("counts", "未关注");

			list.add(hashMap);
		}
		return list;
	}

	/**
	 * 获取车辆，我的申请
	 * 
	 * @date 2013-6-18
	 * @param userId
	 * @return
	 */
	public List<HashMap<String, String>> selectMyCarAppInfo(String userId) {
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		arrayList.clear();
		brrayList.clear();
		crrayList.clear();
		// 传递用户编号
		arrayList.add("userId");
		brrayList.add(userId);

		crrayList = Soap.GetWebServre("selectMyCarAppInfo", arrayList,
				brrayList);

		for (int j = 0; j < crrayList.size(); j += 7) {
			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("id", crrayList.get(j));
			hashMap.put("carid", crrayList.get(j + 1));
			hashMap.put("username", crrayList.get(j + 2));
			hashMap.put("carnum", crrayList.get(j + 3));
			hashMap.put("destination", crrayList.get(j + 4));
			if ("0".equals(crrayList.get(j + 5)))
				hashMap.put("status", "空闲");
			else if ("1".equals(crrayList.get(j + 5)))
				hashMap.put("status", "申请中");
			else
				hashMap.put("status", "预约中");
			hashMap.put("addtime", crrayList.get(j + 6));
			list.add(hashMap);
		}
		return list;
	}

	/**
	 * 车辆申请详细
	 * 
	 * @return
	 */
	public List<String> selectCarAppDetailedInfo(String app_id) {
		arrayList.clear();
		brrayList.clear();
		crrayList.clear();

		// 传递任务编号
		arrayList.add("app_id");
		brrayList.add(app_id);

		crrayList = Soap.GetWebServre("selectCarAppDetailedInfo", arrayList,
				brrayList);

		return crrayList;
	}

	/**
	 * 访问更新申请weservice
	 * 
	 * @date 2013-6-19
	 * @param app_id
	 * @param begin_time
	 * @param end_time
	 * @param person_num
	 * @param reason
	 * @param destination
	 * @param remarks
	 * @return
	 */
	public List<String> doUpdateCarAppReq(String app_id, String begin_time,
			String end_time, String person_num, String reason,
			String destination, String remarks) {
		arrayList.clear();
		brrayList.clear();
		crrayList.clear();

		// 传递任务编号
		arrayList.add("app_id");
		brrayList.add(app_id);

		arrayList.add("begin_time");
		brrayList.add(begin_time);

		arrayList.add("end_time");
		brrayList.add(end_time);

		arrayList.add("person_num");
		brrayList.add(person_num);

		arrayList.add("reason");
		brrayList.add(reason);

		arrayList.add("destination");
		brrayList.add(destination);

		arrayList.add("remarks");
		brrayList.add(remarks);

		crrayList = Soap
				.GetWebServre("doUpdateCarAppReq", arrayList, brrayList);

		return crrayList;
	}

	/**
	 * 查询所有的车辆
	 * 
	 * @date 2013-6-19
	 * @return
	 */
	public ArrayList<String> selectAllCars() {
		arrayList.clear();
		brrayList.clear();
		crrayList.clear();

		crrayList = Soap.GetWebServre("selectAllCars", arrayList, brrayList);

		return crrayList;
	}

	/**
	 * 添加申请信息
	 * 
	 * @date 2013-6-20
	 * @param car_id
	 * @param app_id
	 * @param begintime
	 * @param endtime
	 * @param personnum
	 * @param reason
	 * @param destination
	 * @param remark
	 */
	public List<String> doAddCarAppReq(String user_id, String car_num,
			String car_id, String begin_time, String end_time,
			String person_num, String reason, String destination, String remarks) {
		arrayList.clear();
		brrayList.clear();
		crrayList.clear();

		arrayList.add("user_id");
		brrayList.add(user_id);

		arrayList.add("car_num");
		brrayList.add(car_num);

		arrayList.add("car_id");
		brrayList.add(car_id);

		arrayList.add("begin_time");
		brrayList.add(begin_time);

		arrayList.add("end_time");
		brrayList.add(end_time);

		arrayList.add("person_num");
		brrayList.add(person_num);

		arrayList.add("reason");
		brrayList.add(reason);

		arrayList.add("destination");
		brrayList.add(destination);

		arrayList.add("remarks");
		brrayList.add(remarks);

		crrayList = Soap.GetWebServre("doAddCarAppReq", arrayList, brrayList);

		return crrayList;
	}

	/**
	 * 获取已添加完成情况
	 * 
	 * @return
	 */
	public List<HashMap<String, String>> selectReportInfo(String userId,
			String missionId) {
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		arrayList.clear();
		brrayList.clear();
		crrayList.clear();

		// 传递用户编号
		arrayList.add("userId");
		brrayList.add(userId);
		arrayList.add("missionId");
		brrayList.add(missionId);

		crrayList = Soap.GetWebServre("selectReportInfo", arrayList, brrayList);

		for (int j = 0; j < crrayList.size(); j += 3) {
			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("id", crrayList.get(j));
			hashMap.put("des", crrayList.get(j + 1));
			hashMap.put("addtime", crrayList.get(j + 2));

			list.add(hashMap);
		}
		return list;
	}

	/**
	 * 
	 * @date 2013-6-24
	 * @param report_id
	 */
	public List<String> doDeleteReportReq(String report_id) {
		arrayList.clear();
		brrayList.clear();

		arrayList.add("report_id");
		brrayList.add(report_id);

		crrayList = Soap
				.GetWebServre("doDeleteReportReq", arrayList, brrayList);
		return crrayList;
	}

	public List<String> doAddReportReq(String report_info, String missionId,
			String userId) {
		arrayList.clear();
		brrayList.clear();

		arrayList.add("report_info");
		brrayList.add(report_info);
		arrayList.add("missionId");
		brrayList.add(missionId);
		arrayList.add("userId");
		brrayList.add(userId);

		crrayList = Soap.GetWebServre("doAddReportReq", arrayList, brrayList);
		return crrayList;
	}

	public List<HashMap<String, String>> selectChildMissionInfo(
			String intent_missionId) {
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		arrayList.clear();
		brrayList.clear();
		crrayList.clear();
		arrayList.add("intent_missionId");
		brrayList.add(intent_missionId);

		crrayList = Soap.GetWebServre("selectChildMissionInfo", arrayList,
				brrayList);

		for (int j = 0; j < crrayList.size(); j += 8) {
			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("missionId", crrayList.get(j));
			hashMap.put("createUserName", crrayList.get(j + 1));
			hashMap.put("Title", crrayList.get(j + 2));
			hashMap.put("beginTime", crrayList.get(j + 3));
			hashMap.put("endTime", crrayList.get(j + 4));

			if ("1".equals(crrayList.get(j + 5)))
				hashMap.put("status", "进行中");
			else if ("2".equals(crrayList.get(j + 5)))
				hashMap.put("status", "即将超时");
			else if ("3".equals(crrayList.get(j + 5)))
				hashMap.put("status", "提交待审");
			else if ("4".equals(crrayList.get(j + 5)))
				hashMap.put("status", "已完成");
			else if ("5".equals(crrayList.get(j + 5)))
				hashMap.put("status", "超时完成");
			else if ("6".equals(crrayList.get(j + 5)))
				hashMap.put("status", "超时");

			hashMap.put("importance", crrayList.get(j + 6));// 重要性
			hashMap.put("counts", crrayList.get(j + 7));

			list.add(hashMap);
		}
		return list;
	}

	public List<HashMap<String, String>> selectChartMissionInfo(String userId,
			String datachart_index) {
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		arrayList.clear();
		brrayList.clear();
		crrayList.clear();
		// 传递用户编号
		arrayList.add("userId");
		brrayList.add(userId);
		arrayList.add("datachart_index");
		brrayList.add(datachart_index);

		crrayList = Soap.GetWebServre("selectChartMissionInfo", arrayList,
				brrayList);

		for (int j = 0; j < crrayList.size(); j += 8) {
			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("missionId", crrayList.get(j));
			hashMap.put("createUserName", crrayList.get(j + 1));
			hashMap.put("Title", crrayList.get(j + 2));
			hashMap.put("beginTime", crrayList.get(j + 3));
			hashMap.put("endTime", crrayList.get(j + 4));

			if ("1".equals(crrayList.get(j + 5)))
				hashMap.put("status", "进行中");
			else if ("2".equals(crrayList.get(j + 5)))
				hashMap.put("status", "即将超时");
			else if ("3".equals(crrayList.get(j + 5)))
				hashMap.put("status", "提交待审");
			else if ("4".equals(crrayList.get(j + 5)))
				hashMap.put("status", "已完成");
			else if ("5".equals(crrayList.get(j + 5)))
				hashMap.put("status", "超时完成");
			else if ("6".equals(crrayList.get(j + 5)))
				hashMap.put("status", "超时");

			hashMap.put("importance", crrayList.get(j + 6));// 重要性
			hashMap.put("counts", crrayList.get(j + 7));

			list.add(hashMap);
		}
		return list;
	}
}
