package com.xin.util;

import android.util.Log;

public class XLog {
	public static String TAG = "gbox";
	public static int level=Log.VERBOSE;

	public static int v(String msg) {
		if(Log.VERBOSE>=level)
			return Log.v(TAG, msg);
		else
			return -1;
	}

	public static int v(String msg, Throwable tr) {
		if(Log.VERBOSE>=level)
			return Log.v(TAG, msg, tr);
		else
			return -1;
	}

	public static int d(String msg) {
		if(Log.DEBUG>=level)
			return Log.d(TAG, msg);
		else
			return -1;
	}

	public static int d(String msg, Throwable tr) {
		if(Log.DEBUG>=level)
			return Log.d(TAG, msg, tr);
		else
			return -1;
	}

	public static int i(String msg) {
		if(Log.INFO>=level)
			return Log.i(TAG,msg);
		else
			return -1;
	}

	
	public static int i(String msg, Throwable tr) {
		if(Log.INFO>=level)
			return Log.i(TAG,msg,tr);
		else
			return -1;
	}
	public static void printMemory(){
		long free=Runtime.getRuntime().freeMemory();
		long total=Runtime.getRuntime().totalMemory();
		long max=Runtime.getRuntime().maxMemory();
		int m=1024*1024;
		i("Memory Info:free="+free/m+"M, total="+total/m+"M, max="+max/m+"M");
	}

	public static int w(String msg) {
		if(Log.WARN>=level)
			return Log.w(TAG,msg);
		else
			return -1;
	}


	public static int w(String msg, Throwable tr) {
		if(Log.WARN>=level)
			return Log.w(TAG,msg,tr);
		else
			return -1;
	}





	public static int w(Throwable tr) {
		if(Log.WARN>=level)
			return Log.w(TAG, tr);
		else
			return -1;
	}


	public static int e(String msg) {
		if(Log.ERROR>=level)
			return Log.e(TAG, msg);
		else
			return -1;
	}


	public static int e(String msg, Throwable tr) {
		if(Log.ERROR>=level)
			return Log.e(TAG, msg, tr);
		else
			return -1;
	}



}
