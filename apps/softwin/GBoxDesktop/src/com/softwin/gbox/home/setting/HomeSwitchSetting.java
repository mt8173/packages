package com.softwin.gbox.home.setting;

import java.util.ArrayList;

import com.softwin.gbox.home.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class HomeSwitchSetting extends Activity{
	static final String TAG = "xin";
	static final int REQUESTING_UNINSTALL = 10;
	private PackageManager mPm;
	private IntentFilter mHomeFilter;
	private ComponentName[] mHomeComponentSet;
	private ArrayList<DataHolder> mDatas =new ArrayList<HomeSwitchSetting.DataHolder>();
	private LinearLayout mListView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPm = getPackageManager();
		setContentView(R.layout.activity_home_switch);
		mListView=(LinearLayout) findViewById(R.id.home_switch_list);
		buildHomeActivitiesList();

		mHomeFilter = new IntentFilter(Intent.ACTION_MAIN);
		mHomeFilter.addCategory(Intent.CATEGORY_HOME);
		mHomeFilter.addCategory(Intent.CATEGORY_DEFAULT);
	}


	private void buildHomeActivitiesList() {
		ArrayList<ResolveInfo> homeActivities = new ArrayList<ResolveInfo>();
		ComponentName currentDefaultHome = mPm
				.getHomeActivities(homeActivities);
		mDatas.clear();
		mListView.removeAllViews();
		for (int i = 0; i < homeActivities.size(); i++) {
			final ResolveInfo candidate = homeActivities.get(i);
			final ActivityInfo info = candidate.activityInfo;
			if("com.android.settings".equals(info.packageName)){
				continue;
			}
			ComponentName activityName = new ComponentName(info.packageName,
					info.name);
			Log.i(TAG, "buildHomeActivitiesList list="+activityName);
			try {
				Drawable icon = info.loadIcon(mPm);
				CharSequence name = info.loadLabel(mPm);
				DataHolder holder=new DataHolder(activityName, icon, name);
				mDatas.add(holder);
				View v=getView(holder);
				mListView.addView(v);
				if (activityName.equals(currentDefaultHome)) {
					v.requestFocus();
				}
			} catch (Exception e) {
				Log.v(TAG, "Problem dealing with activity " + activityName, e);
			}
		}
	}
	void makeCurrentHome(ComponentName activityName) {
		mPm.replacePreferredActivity(mHomeFilter,
				IntentFilter.MATCH_CATEGORY_EMPTY, mHomeComponentSet,
				activityName);                                                
	}

	void uninstallApp(String packageName,boolean isDefault) {
		Uri packageURI = Uri.parse("package:" + packageName);
		Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE,
				packageURI);
		uninstallIntent.putExtra(Intent.EXTRA_UNINSTALL_ALL_USERS, false);
		int requestCode = REQUESTING_UNINSTALL + (isDefault ? 1 : 0);
		startActivityForResult(uninstallIntent, requestCode);
	}
	
	public View getView(final DataHolder data) {
		View layout=View.inflate(HomeSwitchSetting.this, R.layout.item_home_app, null);
		ImageView iconView=(ImageView) layout.findViewById(R.id.item_home_app_icon);
		TextView titleView=(TextView) layout.findViewById(android.R.id.text1);
		iconView.setImageDrawable(data.icon);
		titleView.setText(data.title);
		layout.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
		    		if(event.getAction()==MotionEvent.ACTION_UP){
		    			v.requestFocus();
		    		}
		    	return v.onTouchEvent(event);
			}
		});
		layout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Log.i(TAG, "click "+data.title);
				makeCurrentHome(data.activityName);
			}
		});
		return layout;
	}
	private class DataHolder{
        ComponentName activityName;
		Drawable icon;
		CharSequence title;

        public DataHolder(ComponentName activity,Drawable icon, CharSequence title) {
            this.activityName=activity;
            this.icon=icon;
            this.title=title;
        }
        public boolean isDefault(){
        	return activityName.getPackageName().equals("com.softwin.gbox.home");
        }
        @Override
        public String toString() {
        	return title.toString();
        }
	}

}
