package com.softwin.gbox.home;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

public class Welcome extends Activity implements Runnable{
	private long mDelayMillis=3000;
	private Handler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		handler=new Handler();
		handler.postDelayed(this, mDelayMillis);
	}

	@Override
	public void run() {
		finish();
	}
	
}
