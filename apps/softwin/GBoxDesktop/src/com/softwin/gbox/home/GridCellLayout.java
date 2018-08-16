package com.softwin.gbox.home;

import java.util.ArrayList;

import com.xin.util.XLog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

public class GridCellLayout extends CellLayout {
	protected int mRow=2;
	protected LayoutInflater mInflater;
	protected int mMenuId;
	protected ArrayList<PathGameBean> mBeans;
	public GridCellLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public GridCellLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GridCellLayout(Context context) {
		super(context);
		init();
	}
	private void init(){
		mInflater=LayoutInflater.from(getContext());
	}
	public void setMenuId(int id){
		this.mMenuId=id;
	}
	public void setList(ArrayList<PathGameBean> beans){
		this.mBeans=beans;
	}
	public void load(){
		if(mBeans==null||mBeans.size()<=0)return;
		int size=mBeans.size();
		for(int index=0;index<size;index++){
			PathGameBean app=mBeans.get(index);
			addCellView(app,index/mRow, index%mRow, 1, 1);
		}
	}
	public boolean load(int id, TabletNormalHelper tabletGameHelper) {
		if(mBeans==null||mBeans.size()<=0)return true;
		int size=mBeans.size();
		for(int index=0;index<size;index++){
			if(id==tabletGameHelper.currentId){
				PathGameBean app=mBeans.get(index);
				addCellView(app,index/mRow, index%mRow, 1, 1);
			}else{
				XLog.i("########layout load break########");
				return false;
			}
		}
		return true;
	}
	public boolean load(int id, TabletG9Helper tabletGameHelper) {
		if(mBeans==null||mBeans.size()<=0)return true;
		int size=mBeans.size();
		for(int index=0;index<size;index++){
			if(id==tabletGameHelper.currentId){
				PathGameBean app=mBeans.get(index);
				addCellView(app,index/mRow, index%mRow, 1, 1);
			}else{
				XLog.i("########layout load break########");
				return false;
			}
		}
		return true;
	}
	
	public void addCellView(PathGameBean app,int cellX,int cellY,int cellHSpan, int cellVSpan){
		GridItemView view=(GridItemView) mInflater.inflate(R.layout.item_grid, null);
		view.setMenuId(mMenuId);
		view.setTag(app);
		int backgroundId=IconConfig.getMyAppItemIcon(cellX, cellY);
		view.setContentIcon(backgroundId);
		CellLayout.LayoutParams cellParams=new CellLayout.LayoutParams(cellX, cellY, cellHSpan, cellVSpan);
		cellParams.topMargin=-StaticVar.self().mCellMarginTop;
		cellParams.leftMargin=-StaticVar.self().mCellMarginLeft;
		cellParams.bottomMargin=-StaticVar.self().mCellMarginBottom;
		cellParams.rightMargin=-StaticVar.self().mCellMarginRight;
		if(cellY==mRow-1&&hasShadow()){
			view.setReflectIcon(CachedShadow.getShadow(backgroundId, getResources()));
			cellParams.appendHeight=46;
		}
		addView(view, cellParams);
		
		
	}

}
