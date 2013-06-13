package com.wewin.flowmobilesys.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import android.os.Environment;

/**
 * 文件读写工具类
 * 
 * @author HCOU
 * @date 2013-6-13
 */
public class FileUtil {
	/**
	 * 读文件
	 * 
	 * @date 2013-6-13
	 * @param fileName
	 * @return
	 */
	public String ReadFile(String fileName) {
		File file = new File(Environment.getExternalStorageDirectory(),
				fileName);
		String data = "something is wrong !";
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			try {
				FileInputStream inputStream = new FileInputStream(file);
				byte[] b = new byte[inputStream.available()];
				inputStream.read(b);
				data = new String(b);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	/**
	 * 写文件
	 * 
	 * @date 2013-6-13
	 * @param fileName
	 * @param data
	 */
	public boolean WriteFile(String fileName, String data) {
		File file = new File(Environment.getExternalStorageDirectory(),
				fileName);
		boolean flag = false;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			try {
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(data.getBytes());
				fos.close();
				flag = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return flag;
	}
}
