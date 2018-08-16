package com.softwin.gbox.settings.time;

import android.content.Context;

public class TimeTest {
	private Context mContext;
	private TimeUpdateListener mListener;
	public TimeTest(Context mContext, TimeUpdateListener mListener) {
		super();
		this.mContext = mContext;
		this.mListener = mListener;
	}
	public void request(){
		Runnable run = new Runnable() {
			@Override
			public void run() {
				requestSntpArray();
			}
		};
		new Thread(run).start();
	}
	protected void requestSntpArray() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        if(mListener!=null){
        	mListener.onSuccess(0);
        }
		
	}
}
