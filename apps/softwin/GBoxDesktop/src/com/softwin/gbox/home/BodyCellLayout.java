package com.softwin.gbox.home;

import java.util.Collections;
import java.util.List;

import com.xin.util.SimpleAppsModel;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

public class BodyCellLayout extends AppCellLayout {

	public BodyCellLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public BodyCellLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BodyCellLayout(Context context) {
		super(context);
	}



	@Override
	public List<SimpleAppsModel> getAllAppsByType() {
		BoxApplication  ba=(BoxApplication)((Activity)getContext()).getApplication();
		List<SimpleAppsModel> apps = ba.getDatabase().getAppByType(AppDatabase.TYPE_BODY);
		Collections.sort(apps, new SimpleAppsModel.NameSort());
		return apps;
	}



}
