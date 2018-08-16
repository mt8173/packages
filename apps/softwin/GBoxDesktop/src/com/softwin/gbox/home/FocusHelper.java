package com.softwin.gbox.home;

import com.xin.util.XLog;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class FocusHelper {

	public static void add(View v) {
		v.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//XLog.v("@"+v+"\n"+event);
		    		if(event.getAction()==MotionEvent.ACTION_UP){
		    			v.requestFocus();
		    		}
		    	return v.onTouchEvent(event);
			}
		});
		
	}

}
