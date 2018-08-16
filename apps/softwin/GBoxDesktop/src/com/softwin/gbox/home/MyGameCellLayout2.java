package com.softwin.gbox.home;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.xin.util.SimpleAppsModel;
import com.xin.util.XLog;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

public class MyGameCellLayout2 extends AppCellLayout {

	public MyGameCellLayout2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyGameCellLayout2(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyGameCellLayout2(Context context) {
		super(context);
	}

	@Override
	public List<SimpleAppsModel> getAllAppsByType() {
		BoxApplication  ba=(BoxApplication)((Activity)getContext()).getApplication();
		AppDatabase db=ba.getDatabase();
		List<SimpleAppsModel> apps = db.getAppByType(AppDatabase.TYPE_MYGAME);
		List<SimpleAppsModel> _extends=MyGameImport.importGames(getContext(), false);
		//XLog.i("apps="+apps);
		//XLog.i("_extends="+_extends);
		Iterator<SimpleAppsModel> it=_extends.iterator();
		while(it.hasNext()){
			SimpleAppsModel item=it.next();
			if(apps.contains(item)){
				it.remove();
			}else{
				String pkg=item.componentName.getPackageName();
				String clz=item.componentName.getClassName();
				if(!db.existApp(pkg, null)){
					db.insert(pkg, null, AppDatabase.TYPE_MYGAME, item.index, false);
				}
			}
		}
		apps.addAll(_extends);
		Collections.sort(apps, new SimpleAppsModel.NameSort());
		return apps;
	}



}
