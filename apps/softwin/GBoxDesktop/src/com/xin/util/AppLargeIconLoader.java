package com.xin.util;

import java.util.HashMap;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;
/**
 * new AppLargeIconLoader(Context context).execute(ImageView mIconView,ComponentName cn);
 * @author wangxin
 *
 */
public class AppLargeIconLoader extends AsyncTask<Object,Void,Object>{
	public static HashMap<ComponentName,Drawable> mCache=new HashMap<ComponentName, Drawable>();
	private PackageManager pm;
	public AppLargeIconLoader(Context context) {
		pm=context.getPackageManager();
	}
	@Override
	protected Object doInBackground(Object... params) {
		ComponentName cn=(ComponentName) params[1];
		//Drawable drawable=null;
		Drawable drawable=mCache.get(cn);
		if(drawable!=null){
			return new Object[]{params[0],drawable};
		}
		try {
			Resources res=pm.getResourcesForActivity(cn);
			ActivityInfo info = pm.getActivityInfo(cn, 0);
			int eIcon=info.icon;
			if(eIcon==0){
				ApplicationInfo aInfo=info.applicationInfo;
				eIcon=aInfo.icon;
			}
			if(eIcon==0)return null;
			drawable=res.getDrawableForDensity(eIcon, 480);
			if(drawable!=null){
				//XLog.i("480");
				mCache.put(cn, drawable);
				return new Object[]{params[0],drawable};
			}
			drawable=res.getDrawableForDensity(eIcon, 320);
			if(drawable!=null){
				//XLog.i("320");
				mCache.put(cn, drawable);
				return new Object[]{params[0],drawable};
			}
			drawable=res.getDrawableForDensity(eIcon, 240);
			if(drawable!=null){
				//XLog.i("240");
				mCache.put(cn, drawable);
				return new Object[]{params[0],drawable};
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			XLog.i("throws exception use "+cn);
		}
		
		return null;
	}
	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);
		if(result==null)return;
		Object params[]=(Object[]) result;
		ImageView view=(ImageView) params[0];
		Drawable drawable=(Drawable) params[1];
		view.setImageDrawable(drawable);
		
	}

}
