package com.softwin.gbox.home;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.xin.util.SimpleAppsModel;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.AttributeSet;

public class RecordCellLayout extends AppCellLayout {
	protected RecordPrefs mPrefs;
	protected RecordPrefs getPrefs(){
		if(mPrefs==null){
			mPrefs=new RecordPrefs(getContext(), getClass().getSimpleName());
		}
		return mPrefs;
	}
	public RecordCellLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public RecordCellLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RecordCellLayout(Context context) {
		super(context);
	}
	
	protected String getRecord(){
		return getClass().getName();
	}

	public List<SimpleAppsModel> getAllAppsByType(){
		PackageManager pm=getContext().getPackageManager();
		ArrayList<String> pkgs=getPrefs().getAllList();
		ArrayList<SimpleAppsModel> mRecords=new ArrayList<SimpleAppsModel>();
		for(String packageName:pkgs){
			ArrayList<SimpleAppsModel> record=SimpleAppsModel.loadLauncherByPackage(pm, packageName);
			mRecords.addAll(record);
		}
		if(mRecords.size()>1){
			Collections.sort(mRecords, new SimpleAppsModel.NameSort());
		}
		mRecords.add(AppPicker.create());
		return mRecords;
	}
	@Override
	public boolean addPackage(String packageName) {
		getPrefs().add(packageName);
		return super.addPackage(packageName);
	}
	@Override
	public boolean removePackage(String packageName) {
		getPrefs().remove(packageName);
		return super.removePackage(packageName);
	}



}
