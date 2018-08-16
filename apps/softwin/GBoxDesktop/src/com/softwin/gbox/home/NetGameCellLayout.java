package com.softwin.gbox.home;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.xin.util.SimpleAppsModel;
import com.xin.util.ViewPrint;
import com.xin.util.XLog;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;

public class NetGameCellLayout extends AppCellLayout {

	public NetGameCellLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public NetGameCellLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NetGameCellLayout(Context context) {
		super(context);
	}

	@Override
	public List<SimpleAppsModel> getAllAppsByType() {
		BoxApplication  ba=(BoxApplication)((Activity)getContext()).getApplication();
		AppDatabase db=ba.getDatabase();
		List<SimpleAppsModel> apps = db.getAppByType(AppDatabase.TYPE_NET_GAME);
		List<SimpleAppsModel> _extends=MyGameImport.importNetGames(getContext());
		Iterator<SimpleAppsModel> it=_extends.iterator();
		while(it.hasNext()){
			SimpleAppsModel item=it.next();
			if(apps.contains(item)){
				it.remove();
			}else{
				String pkg=item.componentName.getPackageName();
				String clz=item.componentName.getClassName();
				if(!db.existApp(pkg, null)){
					db.insert(pkg, null, AppDatabase.TYPE_NET_GAME, item.index, false);
				}
			}
		}
		apps.addAll(_extends);
		Collections.sort(apps, new SimpleAppsModel.NameSort());
		return apps;
	}
	
	public Object getNullContextTip(){
		return R.string.common_no_net_game;
	}
	public void showNullTipIfNeed(boolean show){
		Launcher launcher=(Launcher) getContext();
		launcher.getWorkspace().showTipText(show, show?getNullContextTip():null);
	}

}
