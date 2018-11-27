package com.pixnotes.utils;

import android.util.Log;

/**
 * @author thule
 * print log in console window when debug
 *
 */
public class LogUtils {
	
	/**
	 * Allow to show log or not
	 */
	public static final boolean IS_ALLOW_LOG = true; 
	
	public static final String INFO_TAG = "INFO_PIXNOTES";
	public static final String ERROR_TAG = "ERROR_PIXNOTES";
	
	/**
	 * @param msg
	 */
	public static void LogError(String msg){
		if(IS_ALLOW_LOG){
			Log.e(ERROR_TAG, msg);
		}
	}
	
	/**
	 * @param msg
	 */
	public static void LogInfo(String msg){
		if(IS_ALLOW_LOG){
			Log.i(INFO_TAG, msg);
		}
	}
	

	/**
	 * @param tag
	 * @param msg
	 */
	public static void LogError(String tag, String msg){
		if(IS_ALLOW_LOG){
			Log.e(tag, msg);
		}
	}
	/**
	 * @param tag
	 * @param msg
	 */
	public static void LogInfo(String tag, String msg){	
		if(IS_ALLOW_LOG){
			Log.i(tag, msg);
		}
	}
}
