package com.wewin.flowmobilesys.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * UIπ§æﬂ¿‡
 * 
 * @author HCOU
 * @time 2013.05.27 17:25:00
 */
public class ScreenUtils {

	public static int getScreenWidth(Context context) {
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getWidth();
	}

	public static int getScreenHeight(Context context) {
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getHeight();
	}

	public static float getScreenDensity(Context context) {
		try {
			DisplayMetrics dm = new DisplayMetrics();
			WindowManager manager = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			manager.getDefaultDisplay().getMetrics(dm);
			return dm.density;
		} catch (Exception ex) {

		}
		return 1.0f;
	}
}