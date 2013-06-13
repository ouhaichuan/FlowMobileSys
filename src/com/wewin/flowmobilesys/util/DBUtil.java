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
	 * @param userid
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
		for (int j = 0; j < crrayList.size(); j += 7) {
			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("missionId", crrayList.get(j));
			hashMap.put("createUserName", crrayList.get(j + 1));
			hashMap.put("Title", crrayList.get(j + 2));
			hashMap.put("beginTime", crrayList.get(j + 3));
			hashMap.put("endTime", crrayList.get(j + 4));

			if ("0".equals(crrayList.get(j + 5)))
				hashMap.put("status", "未开始");
			else if ("1".equals(crrayList.get(j + 5)))
				hashMap.put("status", "进行中");
			else
				hashMap.put("status", "完成");

			hashMap.put("counts", crrayList.get(j + 6));
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

		for (int j = 0; j < crrayList.size(); j += 7) {
			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("missionId", crrayList.get(j));
			hashMap.put("createUserName", crrayList.get(j + 1));
			hashMap.put("Title", crrayList.get(j + 2));
			hashMap.put("beginTime", crrayList.get(j + 3));
			hashMap.put("endTime", crrayList.get(j + 4));

			if ("0".equals(crrayList.get(j + 5)))
				hashMap.put("status", "未开始");
			else if ("1".equals(crrayList.get(j + 5)))
				hashMap.put("status", "进行中");
			else
				hashMap.put("status", "完成");

			hashMap.put("counts", crrayList.get(j + 6));

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

		for (int j = 0; j < crrayList.size(); j += 7) {
			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("missionId", crrayList.get(j));
			hashMap.put("createUserName", crrayList.get(j + 1));
			hashMap.put("Title", crrayList.get(j + 2));
			hashMap.put("beginTime", crrayList.get(j + 3));
			hashMap.put("endTime", crrayList.get(j + 4));
			if ("0".equals(crrayList.get(j + 5)))
				hashMap.put("status", "未开始");
			else if ("1".equals(crrayList.get(j + 5)))
				hashMap.put("status", "进行中");
			else
				hashMap.put("status", "完成");

			if (!"0".equals(crrayList.get(j + 6)))
				hashMap.put("counts", "已关注");
			else
				hashMap.put("counts", "未关注");

			list.add(hashMap);
		}
		return list;
	}
}
