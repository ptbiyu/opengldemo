package com.meitu.opengldemo.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * 屏幕显示工具
 * 
 * @author ljp1 2012-4-18
 * 
 */
@SuppressWarnings("deprecation")
public class DisplayUtil {

	/**
	 * dip 转换 px
	 */
	public static int dipToPx(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * px 转换 dip
	 */
	public static int pxToDip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 获取屏幕宽度
	 * 
	 * @param context
	 * @return
	 */

	public static int getScreenWidth(Context context) {
		return getDefaultDisplay(context).getWidth();
	}

	/**
	 * 获取高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		return getDefaultDisplay(context).getHeight();
	}

	/**
	 * 获取屏幕像素密度
	 * 
	 * @param context
	 * @return
	 */
	public static float getScreenDensity(Context context) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getDefaultDisplay(context).getMetrics(displayMetrics);
		return displayMetrics.density;
	}

	/**
	 * 获取屏幕大小
	 * 
	 * @return
	 */
	public static Display getDefaultDisplay(Context context) {
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		return windowManager.getDefaultDisplay();
	}
}
