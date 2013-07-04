package com.wewin.flowmobilesys.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * 连接WebService，属于底层类
 * 
 * @author HCOU
 * @time 2013.05.27 17:25:00
 */
public class HttpConnSoap {
	public ArrayList<String> GetWebService(String methodName,
			ArrayList<String> Parameters, ArrayList<String> ParValues) {
		ArrayList<String> Values = new ArrayList<String>();

		// ServerUrl是指webservice的url
		// 10.0.2.2是让android模拟器访问本地（PC）服务器，不能写成127.0.0.1
		// 2364是指端口号，即挂载到IIS上的时候开启的端口
		// DbService.asmx是指提供服务的页面
		// 测试地址
		String ServerUrl = "http://www.wewin.com.cn:2364/DbService.asmx";
		// String ServerUrl =
		// "http://218.206.25.36:12015/service/DbService.asmx";
		// 外网地址
		// String ServerUrl = "http://10.0.2.2:2364/DbService.asmx";

		// String soapAction="http://tempuri.org/LongUserId1";
		String soapAction = "http://tempuri.org/" + methodName;
		// String data = "";
		String soap = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				+ "<soap:Body />";
		String tps, vps, ts;
		String mreakString = "";

		mreakString = "<" + methodName + " xmlns=\"http://tempuri.org/\">";
		for (int i = 0; i < Parameters.size(); i++) {
			tps = Parameters.get(i).toString();
			// 设置该方法的参数为.net webService中的参数名称
			vps = ParValues.get(i).toString();
			ts = "<" + tps + ">" + vps + "</" + tps + ">";
			mreakString = mreakString + ts;
		}
		mreakString = mreakString + "</" + methodName + ">";
		/*
		 * +"<HelloWorld xmlns=\"http://tempuri.org/\">" +"<x>string11661</x>"
		 * +"<SF1>string111</SF1>" + "</HelloWorld>"
		 */
		String soap2 = "</soap:Envelope>";
		String requestData = soap + mreakString + soap2;

		try {
			URL url = new URL(ServerUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			byte[] bytes = requestData.getBytes("utf-8");
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setConnectTimeout(6000);// 设置超时时间
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "text/xml;charset=utf-8");
			con.setRequestProperty("SOAPAction", soapAction);
			con.setRequestProperty("Content-Length", "" + bytes.length);
			OutputStream outStream = con.getOutputStream();
			outStream.write(bytes);
			outStream.flush();
			outStream.close();
			InputStream inStream = con.getInputStream();
			Values = inputStreamtovaluelist(inStream, methodName);
			return Values;
		} catch (Exception e) {
			return new ArrayList<String>();
		}
	}

	public ArrayList<String> inputStreamtovaluelist(InputStream in,
			String MonthsName) throws IOException {
		StringBuffer out = new StringBuffer();
		String s1 = "";
		byte[] b = new byte[1024];
		ArrayList<String> Values = new ArrayList<String>();
		Values.clear();

		for (int n; (n = in.read(b)) != -1;) {
			s1 = new String(b, 0, n);
			out.append(s1);
		}

		String[] s13 = out.toString().split("><");
		String ifString = MonthsName + "Result";
		String TS = "";
		String vs = "";

		Boolean getValueBoolean = false;
		for (int i = 0; i < s13.length; i++) {
			TS = s13[i];
			int j, k, l;
			j = TS.indexOf(ifString);
			k = TS.lastIndexOf(ifString);

			if (j >= 0) {
				if (getValueBoolean == false) {
					getValueBoolean = true;
				} else {

				}

				if ((j >= 0) && (k > j)) {
					l = ifString.length() + 1;
					vs = TS.substring(j + l, k - 2);
					Values.add(vs);
					getValueBoolean = false;
					return Values;
				}

			}
			if (TS.lastIndexOf("/" + ifString) >= 0) {
				getValueBoolean = false;
				return Values;
			}
			if ((getValueBoolean) && (TS.lastIndexOf("/" + ifString) < 0)
					&& (j < 0)) {
				k = TS.length();
				vs = TS.substring(7, k - 8);
				Values.add(vs);
			}
		}
		return Values;
	}
}
