package com.softwin.gbox.home;

import java.util.HashMap;

import android.content.Context;
import android.view.View;

public class RecordFocusView {
	private static RecordFocusView instance=null;

	public static void init(Context ctx) {
		if(instance==null){
			instance=new RecordFocusView(ctx);
		}else{
			instance.mViews.clear();
		}
	}
	public static RecordFocusView self() {
		return instance;
	}
	private HashMap<Integer, View> mViews;
	private RecordFocusView(Context ctx) {
		mViews=new HashMap<Integer, View>();
	}
	public void setSelectView(int layoutId, View v) {
		mViews.put(layoutId, v);
		
	}
	public View getView(int id) {
		return mViews.get(id);
	}
}
