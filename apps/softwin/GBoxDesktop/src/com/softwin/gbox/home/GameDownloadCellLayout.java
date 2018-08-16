package com.softwin.gbox.home;

import java.util.Collections;
import java.util.List;

import com.xin.util.SimpleAppsModel;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

public class GameDownloadCellLayout extends AppCellLayout {
	public GameDownloadCellLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public GameDownloadCellLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public GameDownloadCellLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public int getRow(){
		return 1;
	}


	@Override
	public List<SimpleAppsModel> getAllAppsByType() {
		BoxApplication  ba=(BoxApplication)((Activity)getContext()).getApplication();
		List<SimpleAppsModel> apps = ba.getDatabase().getAppByType(AppDatabase.TYPE_DOWNLOAD);
		Collections.sort(apps, new SimpleAppsModel.NameSort());
		return apps;
	}

}
