package com.softwin.gbox.home.status;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.TextView;

public class TimeController {
	private static TimeController sInstance;

	public static TimeController getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new TimeController(context);
		}
		return sInstance;

	}

	private Calendar mCalendar;
	private Context mContext;
	private Locale mLocale;
	private HashMap<TextView, SimpleDateFormat> mDisplayView;

	private TimeController(Context context) {
		this.mContext = context;
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_TIME_TICK);
		filter.addAction(Intent.ACTION_TIME_CHANGED);
		filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
		filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
		// filter.addAction(Intent.ACTION_USER_SWITCHED);
		mContext.registerReceiver(mIntentReceiver, filter, null,
				HandlerProvider.sHandler);
		// context.registerReceiverAsUser(mIntentReceiver,UserHandle.ALL,
		// filter, null, getHandler());
		mCalendar = Calendar.getInstance(TimeZone.getDefault());
		updateClock();
	}

	private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
				String tz = intent.getStringExtra("time-zone");
				mCalendar = Calendar.getInstance(TimeZone.getTimeZone(tz));
				if(mDisplayView != null){
					for(SimpleDateFormat format:mDisplayView.values()){
						if(format!=null){
							format.setTimeZone(mCalendar.getTimeZone());
						}
					}
				}
			} else if (action.equals(Intent.ACTION_CONFIGURATION_CHANGED)) {
				final Locale newLocale = mContext.getResources()
						.getConfiguration().locale;
				if (!newLocale.equals(mLocale)) {
					mLocale = newLocale;
				}
			}
			updateClock();
		}
	};
	public void addDateDisplay(TextView text){
		addDisplay(text, new SimpleDateFormat("yyyy-MM-dd"));
	}
	public void addTimeDisplay(TextView text){
		addDisplay(text, new SimpleDateFormat("HH:mm:ss"));
	}
	public void addWeekDisplay(TextView text){
		addDisplay(text, new SimpleDateFormat("E"));
	}
	public void addDisplay(TextView text,SimpleDateFormat format){
		if(mDisplayView==null){
			mDisplayView=new HashMap<TextView, SimpleDateFormat>(8);
		}
		synchronized (mDisplayView) {
			if(mCalendar!=null){
				format.setTimeZone(mCalendar.getTimeZone());
			}
			mDisplayView.put(text, format);
		}
		
	}
	public void removeDisplay(TextView text){
		synchronized (mDisplayView) {
			mDisplayView.remove(text);
		}
	}
	protected void updateClock() {
		mCalendar.setTimeInMillis(System.currentTimeMillis());
		//boolean is24 = DateFormat.is24HourFormat(context);
		refreshView();

	}
	private final void refreshView() {
		if(mDisplayView==null || mDisplayView.isEmpty())return;
		synchronized (mDisplayView) {
			for(TextView view:mDisplayView.keySet()){
				if(view.getVisibility()==View.VISIBLE){
					SimpleDateFormat format=mDisplayView.get(view);
					String text=format.format(mCalendar.getTime());
					view.setText(text);
				}
			}
		}
	}
}
