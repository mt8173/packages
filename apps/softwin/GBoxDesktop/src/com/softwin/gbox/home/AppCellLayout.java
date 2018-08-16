/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.softwin.gbox.home;

import java.util.Iterator;
import java.util.List;

import com.xin.util.SimpleAppsModel;
import com.xin.util.ViewPrint;
import com.xin.util.XLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public abstract class AppCellLayout extends CellLayout {
	private final static boolean REFRESH_ALL=true;
	public AppLoader mAppStyle;
	private int mMenuId;
	private BroadcastReceiver receiver;
	private List<SimpleAppsModel> mApps;
	private boolean unistall;
	public AppCellLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public AppCellLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AppCellLayout(Context context) {
		super(context);
	}
	
	
	protected String getRecord(){
		return null;
	}
	


	@Override
	protected void onAttachedToWindow() {
		if(DEBUG_LAYOUT)XLog.i("AppCellLayout.onAttachedToWindow");
		registerPackage();
		super.onAttachedToWindow();
	}
	@Override
	protected void onDetachedFromWindow() {
		if(DEBUG_LAYOUT)XLog.i("AppCellLayout.onDetachedFromWindow");
		unregisterPackage();
		super.onDetachedFromWindow();
	}
	
	public boolean removePackage(String packageName){
		Iterator<SimpleAppsModel> it=mApps.iterator();
		boolean changed=false;
		while(it.hasNext()){
			SimpleAppsModel sam=it.next();
			if(packageName.equals(sam.componentName.getPackageName())){
				it.remove();
				changed=true;
			}
		}
		return changed;
	}
	public boolean addPackage(String packageName){
		List<SimpleAppsModel> adds=SimpleAppsModel.loadLauncherByPackage(getContext().getPackageManager(), packageName);
		if(adds==null||adds.size()==0){
			XLog.i("App["+packageName+"] have 0 launcher");
			return false;
		}else{
			XLog.i("App["+packageName+"] have "+adds.size()+" launchers in "+getClass().getSimpleName());
			mApps.addAll(adds);
			//Collections.sort(mApps, new SimpleAppsModel.NameSort());
			return true;
		}
		
	}
	
	public void registerPackage(){
		if(receiver!=null){
			return;
		}
		XLog.i("registerPackage in "+getClass().getName());
		receiver = new PackageChanged();
        IntentFilter filter = new IntentFilter(BoxModel.ACTION_ADD);
        filter.addAction(BoxModel.ACTION_RM);
        getContext().registerReceiver(receiver, filter);
	}

	public void unregisterPackage(){
		if(receiver!=null){
			getContext().unregisterReceiver(receiver);
			receiver=null;
		}
	}
	public abstract List<SimpleAppsModel> getAllAppsByType();
	public int getRow(){
		return 2;
	}
	

	public Object getNullContextTip(){
		return R.string.common_no_data;
	}
	public void showNullTipIfNeed(boolean show){
		
	}
	
	public void checkNull() {
		if(mApps==null||mApps.size()==0){
			showNullTipIfNeed(true);
		}
	}
	public AppLoader createAndGet() {
		if(mAppStyle==null){
			mAppStyle=new AppLoader();
		}
		return mAppStyle;
	}
	public class AppLoader{
		private int mRow=2;
		private AppCellLayout mLayout;
		private LayoutInflater mInflater;
		
		AppLoader(){
			mLayout=AppCellLayout.this;
			mInflater=LayoutInflater.from(mLayout.getContext());
			mApps=getAllAppsByType();
			mRow=getRow();
		}
		public boolean init() {
			if(mApps==null||mApps.size()<=0){
				showNullTipIfNeed(true);
				return false;
			}
			for(int i=0;i<mApps.size();i++){
				SimpleAppsModel app=mApps.get(i);
				addApp(app,i/mRow, i%mRow, 1, 1);
			}
			return true;
		};
		public synchronized void reload(){
			if(DEBUG)XLog.i("reload all apps,"+ViewPrint.getViewClassAndId(mLayout));
			if(mApps==null||mApps.size()<=0){
				removeAllViews();
				//showNullTipIfNeed(true);
				return;
			}else{
				//showNullTipIfNeed(false);
			}
			int count=mApps.size();
			for(int i=0,x=0,y=0;true;i++){
				SimpleAppsModel app=(i<count)?mApps.get(i):null;
				View child=mLayout.getChildAt(x, y);
				if(child!=null){
					if(app!=null){
						if(DEBUG)XLog.i("update x="+x+",y="+y+",name="+app.title);
						if(child instanceof AppItemView){
							AppItemView appCell=(AppItemView) child;
							appCell.updateTag(app);
						}
					}else{
						if(DEBUG)XLog.i("remove x="+x+",y="+y);
						mLayout.removeView(child);
						if(y==mRow-1){
							View shadow=mLayout.getChildAt(x, y+1);
							if(shadow!=null){
								mLayout.removeView(shadow);
							}
						}
					}
				}else{
					if(app!=null){
						if(DEBUG)XLog.i("add x="+x+",y="+y+",name="+app.title);
						addApp(app,x,y,1,1);
					}else{
						if(DEBUG)XLog.i("exit");
						break;
					}
					
				}
				if(y<mRow-1){
					y++;
				}else{
					x++;
					y=0;
				}
			}
			View focusView=mLayout.getFocusedChild();
			if(DEBUG)XLog.i("getFocusedChild="+focusView);
			if(focusView!=null){
				focusView.bringToFront();
			}
			mLayout.invalidate();
			
		}
		public void addApp(SimpleAppsModel app,int cellX,int cellY,int cellHSpan, int cellVSpan){
			if(isNewVersion()){
				addAppNew(app, cellX, cellY, cellHSpan, cellVSpan);
			}else{
				addAppOld(app, cellX, cellY, cellHSpan, cellVSpan);
			}
		}
		public void addAppOld(SimpleAppsModel app,int cellX,int cellY,int cellHSpan, int cellVSpan){
			AppItemView view=(AppItemView) mInflater.inflate(R.layout.item_myapp_one, null);
			view.setMenuId(mMenuId);
			view.setTag(app);
			int backgroundId=IconConfig.getMyAppItemIcon(cellX, cellY);
			view.setContentIcon(backgroundId);
			CellLayout.LayoutParams cellParams=new CellLayout.LayoutParams(cellX, cellY, cellHSpan, cellVSpan);
			mLayout.addView(view, cellParams);
			if(cellY==mRow-1&&hasShadow()){
				ImageView shadow=(ImageView) mInflater.inflate(R.layout.item_myapp_shadow, null);
				shadow.setImageBitmap(CachedShadow.getShadow(backgroundId, getResources()));
				CellLayout.LayoutParams clp=new CellLayout.LayoutParams(cellX, cellY+1, cellHSpan, 1);
				clp.shadow=true;
				mLayout.addView(shadow,clp);
			}
		}
		public void addAppNew(SimpleAppsModel app,int cellX,int cellY,int cellHSpan, int cellVSpan){
			AppItemView view=(AppItemView) mInflater.inflate(R.layout.item_myapp_one_new, null);
			view.setMenuId(mMenuId);
			view.setTag(app);
			int backgroundId=IconConfig.getMyAppItemIcon(cellX, cellY);
			view.setContentIcon(backgroundId);
			CellLayout.LayoutParams cellParams=new CellLayout.LayoutParams(cellX, cellY, cellHSpan, cellVSpan);
			cellParams.topMargin=-StaticVar.self().mCellMarginTop;
			cellParams.leftMargin=-StaticVar.self().mCellMarginLeft;
			cellParams.bottomMargin=-StaticVar.self().mCellMarginBottom;
			cellParams.rightMargin=-StaticVar.self().mCellMarginRight;
			if(isReflectFocus()){
				if(cellY==mRow-1&&hasShadow()){
					view.setReflectIcon(CachedShadow.getShadow(backgroundId, getResources()));
					cellParams.appendHeight=StaticVar.self().mReflectHeight;
				}
			}else{
				if(cellY==mRow-1&&hasShadow()){
					ImageView shadow=(ImageView) mInflater.inflate(R.layout.item_myapp_shadow, null);
					shadow.setImageBitmap(CachedShadow.getShadow(backgroundId, getResources()));
					CellLayout.LayoutParams clp=new CellLayout.LayoutParams(cellX, cellY+1, cellHSpan, 1);
					clp.shadow=true;
					mLayout.addView(shadow,clp);
				}
			}
			mLayout.addView(view, cellParams);
			
			
		}
		public void removeChildByPackage(String package_){
			final int count = getChildCount();
	        for (int i = 0; i < count; i++) {
	            View child = getChildAt(i);
	            if(child instanceof AppItemView){
	            	AppItemView itemView=(AppItemView) child;
	            	if(itemView.sameAs(package_)){
	            		if(hasShadow()){
	            			LayoutParams lp = (LayoutParams) child.getLayoutParams();
	            			if(lp.cellY==mRow-1){
	            				View shadow=getChildAt(lp.cellX,lp.cellY+1);
	            				if(shadow!=null){
	            					mLayout.removeView(shadow);
	            				}
	            			}
	            		}
	            		mLayout.removeView(child);
	            	}
	            }
	        }
		}
		public void addChildByPackage(String package_){
			List<SimpleAppsModel> addApps=SimpleAppsModel.loadLauncherByPackage(getContext().getPackageManager(), package_);
			if(addApps==null||addApps.size()<=0)return;
			int count = addApps.size();
			int index = 0;
			int x=0;
			finish:
			while(true){
				for(int y=0;y<mRow;y++){
					View view=getChildAt(x, y);
					if(view==null){
						addApp(addApps.get(index),x, y, 1, 1);
						index++;
						if(index>=count){
							break finish;
						}
					}
				}
				x++;
			}
		}
	}
	public void setMenuId(int navItemId) {
		this.mMenuId=navItemId;
	}
	
	public boolean isUnistall() {
		return unistall;
	}

	public void setUnistall(boolean unistall) {
		this.unistall = unistall;
	}
	class PackageChanged extends BroadcastReceiver{
		private Handler mHandler;
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			//final String packageName = intent.getStringExtra(BoxModel.FIELD_PACKAGE);
			//final String record = intent.getStringExtra(BoxModel.FIELD_RECORD);
			XLog.i("onReceive "+action+"  "+getClass().getName());
			mApps=getAllAppsByType();
			reloadApp();
			
		}
		public void reloadApp(){
			if(mHandler==null){
				mHandler=getHandler();
			}
			if(mHandler==null){
				mHandler=new Handler();
			}
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					AppCellLayout.AppLoader appStyle=createAndGet();
					appStyle.reload();
				}
			});
		}
	}
	

	public static final boolean isNewVersion(){
		return true;
	}
	public static final boolean isReflectFocus(){
		return true;
	}


}
