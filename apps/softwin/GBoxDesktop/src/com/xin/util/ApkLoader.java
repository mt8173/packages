package com.xin.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

public class ApkLoader {
	public static void installApk(Context context, String path) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.parse("file://" + path),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	public static void uninstallApk(Context context, String pkg) {
		Uri uri = Uri.parse("package:"+pkg);
		Intent intent = new Intent(Intent.ACTION_DELETE, uri);
		context.startActivity(intent);
	}
	public static boolean isInstalled(PackageManager pm,String pkg){
		try {
			pm.getPackageInfo(pkg, 0);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}
	public static ApplicationInfo getApplicationInfo(PackageManager pm,String path){
		PackageInfo info=getPackageInfo(pm, path);
		if(info!=null){
			ApplicationInfo appInfo = info.applicationInfo;
			appInfo.sourceDir = path;
			appInfo.publicSourceDir = path;
			return appInfo;
		}
		return null;
	}
	public static PackageInfo getPackageInfo(PackageManager pm,String path){
		try{
			return pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
		}catch(Exception exception){
			exception.printStackTrace();
		}
		return null;
	}
	
	public static void open(Context ctx,String pkg){
		PackageManager pm=ctx.getPackageManager();
		Intent intent=pm.getLaunchIntentForPackage(pkg);
		if(intent!=null){
			ctx.startActivity(intent);
		}
	}
}
