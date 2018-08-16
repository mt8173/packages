package com.softwin.gbox.home;

import java.util.Collections;
import java.util.List;

import com.xin.util.SimpleAppsModel;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

public class VideoCellLayout extends AppCellLayout {

	public VideoCellLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public VideoCellLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public VideoCellLayout(Context context) {
		super(context);
	}

	@Override
	public List<SimpleAppsModel> getAllAppsByType() {
		BoxApplication  ba=(BoxApplication)((Activity)getContext()).getApplication();
		List<SimpleAppsModel> apps = ba.getDatabase().getAppByType(AppDatabase.TYPE_VIDIA);
		Collections.sort(apps, new SimpleAppsModel.NameSort());
		return apps;
	}





}
