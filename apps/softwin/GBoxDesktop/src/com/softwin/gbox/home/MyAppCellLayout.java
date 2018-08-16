package com.softwin.gbox.home;

import java.util.Collections;
import java.util.List;

import com.xin.util.SimpleAppsModel;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Toast;

public class MyAppCellLayout extends AppCellLayout {

	public MyAppCellLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MyAppCellLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyAppCellLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	@Override
	public List<SimpleAppsModel> getAllAppsByType() {
		BoxApplication  ba=(BoxApplication)((Activity)getContext()).getApplication();
		List<SimpleAppsModel> apps = ba.getDatabase().getDefaultApp();
		Collections.sort(apps, new SimpleAppsModel.NameSort());
		return apps;
	}


}
