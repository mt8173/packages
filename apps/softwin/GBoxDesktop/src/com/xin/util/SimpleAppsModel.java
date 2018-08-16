package com.xin.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

public class SimpleAppsModel implements Comparable<SimpleAppsModel>{
	public static final int INDEX_ZERO=0;
	public Drawable icon;
	public String title;
	public ComponentName componentName;
	public boolean isLoadingLargeIcon=true;
	private boolean isSystem;
	public PathGame game;
	public int index = INDEX_ZERO;
	public boolean canMove=true;
	public Intent getLauncherIntent(){
		Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(componentName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        return intent;
	}
	public Intent getNormalIntent(){
		Intent intent = new Intent();
		intent.setComponent(componentName);
		return intent;
	}
	public void startActivity(Context context){
		context.startActivity(getLauncherIntent());
	}
	public boolean startActivitySafely(Context context){
		try{
			context.startActivity(getLauncherIntent());
			return true;
		}catch(Exception e){
			try{
				context.startActivity(getNormalIntent());
				return true;
			}catch(Exception e1){}
			e.printStackTrace();
			PackageManager pm=context.getPackageManager();
			Intent intent=pm.getLaunchIntentForPackage(componentName.getPackageName());
			if(intent!=null){
				context.startActivity(intent);
				return true;
			}else{
				XLog.i("app["+componentName.getPackageName()+"] is not LAUNCHER!!");
				return false;
			}
		}
	}
	@Override
	public int compareTo(SimpleAppsModel another) {
		if(isSystem!=another.isSystem){
			return isSystem?-1:1;
		}else{
			return title.compareToIgnoreCase(another.title);
		}
	}
	@Override
	public boolean equals(Object o) {
		if(componentName==null)return false;
		if(o instanceof SimpleAppsModel){
			SimpleAppsModel sam=(SimpleAppsModel) o;
			return componentName.equals(sam.componentName);
		}else{
			return false;
		}
	}
	@Override
	public String toString() {
		return componentName!=null?componentName.toString():super.toString();
	}
	public static class NameSort implements Comparator<SimpleAppsModel>{

		@Override
		public int compare(SimpleAppsModel lhs, SimpleAppsModel rhs) {
			if(lhs.index < rhs.index ){
				return -1;
			}else if(lhs.index > rhs.index){
				return 1;
			}
			if(lhs.title==null){
				if(rhs.title==null){
					return 0;
				}else{
					return 1;
				}
			}else{
				if(rhs.title==null){
					return -1;
				}else{
					return lhs.title.compareTo(rhs.title);
				}
			}
		}
	}
	public static ArrayList<SimpleAppsModel> loadApplications(Context context) {
        PackageManager manager = context.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
        //Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));
        ArrayList<SimpleAppsModel> mApplications=null;
        if (apps != null) {
            final int count = apps.size();
            mApplications = new ArrayList<SimpleAppsModel>(count);
            for (int i = 0; i < count; i++) {
            	SimpleAppsModel application = new SimpleAppsModel();
                ResolveInfo info = apps.get(i);
                application.title = (String) info.loadLabel(manager);
                application.componentName=new ComponentName(
                        info.activityInfo.applicationInfo.packageName,
                        info.activityInfo.name);
                application.icon = info.activityInfo.loadIcon(manager);
                application.isSystem=((info.activityInfo.applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)==ApplicationInfo.FLAG_SYSTEM);
                mApplications.add(application);
            }
        }
        return mApplications;
    }
	public static ArrayList<SimpleAppsModel> loadLauncherByPackage(Context ctx,String package_) {
		return loadLauncherByPackage(ctx.getPackageManager(), package_);
	}
	public static ArrayList<SimpleAppsModel> loadLauncherByPackage(PackageManager manager,String package_) {
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		mainIntent.setPackage(package_);
		final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
		ArrayList<SimpleAppsModel> mApplications=null;
		if (apps != null) {
			final int count = apps.size();
			mApplications = new ArrayList<SimpleAppsModel>(count);
			for (int i = 0; i < count; i++) {
				SimpleAppsModel application = new SimpleAppsModel();
				ResolveInfo info = apps.get(i);
				application.title = (String) info.loadLabel(manager);
				application.componentName=new ComponentName(
						info.activityInfo.applicationInfo.packageName,
						info.activityInfo.name);
				application.icon = info.activityInfo.loadIcon(manager);
				application.isSystem=((info.activityInfo.applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)==ApplicationInfo.FLAG_SYSTEM);
				mApplications.add(application);
			}
		}
		return mApplications;
	}

}
