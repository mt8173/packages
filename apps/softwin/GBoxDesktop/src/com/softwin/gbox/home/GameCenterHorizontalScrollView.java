package com.softwin.gbox.home;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class GameCenterHorizontalScrollView extends HorizontalScrollView {
	private OnMoveLinstener mLinsten;
	public GameCenterHorizontalScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public GameCenterHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public GameCenterHorizontalScrollView(Context context) {
		super(context);
		init(context);
	}
	void init(Context context){
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if(mLinsten!=null){
			mLinsten.move(l,t,oldl,oldt);
		}
	}

	public void setOnMoveLinstener(OnMoveLinstener linstener) {
		this.mLinsten=linstener;
	}
	
	interface OnMoveLinstener{
		void move(int l, int t, int oldl, int oldt);
	}

}
