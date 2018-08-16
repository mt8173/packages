package com.softwin.gbox.home;


import com.xin.util.XLog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

public class DynamicCellLayout extends FrameLayout {
	public int space =2;
	public int cell = 300;
	public final int top = 50;
	public final int top2 = top + cell +space;
	public int left = 50;
	public LayoutParams lp1x1=new LayoutParams(cell, cell);
	public LayoutParams lp2x1=new LayoutParams(cell*2+space, cell);
	public LayoutParams lp1x2=new LayoutParams(cell,cell*2+space);
	public DynamicCellLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public DynamicCellLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public DynamicCellLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		refreshLayoutParams(params);
        super.addView(child, index, params);
    }
	public void addView(View child, int index) {
        ViewGroup.LayoutParams params = child.getLayoutParams();
        XLog.i("addView,params="+params);
        super.addView(child, index);
	}
	@Override
	protected boolean addViewInLayout(View child, int index,
			ViewGroup.LayoutParams params,
			boolean preventRequestLayout) {
		//refreshLayoutParams(params);
		return super.addViewInLayout(child, index, params, preventRequestLayout);
	}
	@Override
	protected LayoutParams generateDefaultLayoutParams() {
		return lp1x1;
	}
	private void refreshLayoutParams(ViewGroup.LayoutParams oParams){
		if(oParams!=null&&oParams instanceof LayoutParams){
			LayoutParams params = (LayoutParams) oParams;
			if(params.height > params.width){
				params.width = cell;
				params.height = cell*2+space;
			}else if(params.height <params.width){
				params.width = cell*2+space;
				params.height = cell;
			}else{
				params.width = cell;
				params.height = cell;
			}
			left= left +params.width + space;
			params.leftMargin = left;
			params.topMargin = top;
			printLayoutParms(params);
		}
	}
	public static void printLayoutParms(LayoutParams lp){
		XLog.i(lp.getClass().getSimpleName()+":("+lp.width+","+lp.height+") margin=("+lp.topMargin+","+lp.leftMargin+")");
	}
	public void setAdapter(BaseAdapter adapter){
		int count = adapter.getCount();
		boolean[][] cell = new boolean[2][count];
		for(int i=0;i<cell.length;i++){
			boolean[] row=new boolean[i];
			for(int j=0;j<row.length;j++){
				row[j]=false;
			}
		}
		for(int i=0;i<3;i++){
			View view=adapter.getView(i, null, this);
			addView(view);
		}
	}
}
