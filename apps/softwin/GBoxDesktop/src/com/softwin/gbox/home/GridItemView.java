package com.softwin.gbox.home;

import java.io.File;

import com.xin.util.ApkLoader;
import com.xin.util.AppLargeIconLoader;
import com.xin.util.ImageHelper;
import com.xin.util.ImageUtil;
import com.xin.util.SimpleAppsModel;
import com.xin.util.ViewPrint;
import com.xin.util.XLog;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class GridItemView extends FrameLayout implements OnFocusChangeListener,View.OnClickListener,View.OnLongClickListener, View.OnKeyListener {
	private TextView mTitleView;
	private ImageView mIconView;
	private Drawable normalBg;
	private Drawable focusBg;
	private PathGameBean mApp;
	private int mMenuId;
	
	private ImageView mReflectView;
	private ImageView mShadowView;
	private FrameLayout mContentView;
	public GridItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttribute(context, attrs);
		initLayout();
	}

	public GridItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAttribute(context, attrs);
		initLayout();
	}

	public GridItemView(Context context) {
		super(context);
		initLayout();
	}

	private void initAttribute(Context context, AttributeSet attrs) {

	}

	private void initLayout() {
		if(AppCellLayout.isNewVersion()){
			inflate(getContext(), R.layout.view_app_item_new, this);
		}else{
			inflate(getContext(), R.layout.view_app_item, this);
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mTitleView = (TextView) findViewById(R.id.app_item_name);
		mIconView = (ImageView) findViewById(R.id.app_item_icon);
		
		if(AppCellLayout.isNewVersion()){
			mShadowView = (ImageView) findViewById(R.id.app_item_shadow);
			mReflectView = (ImageView) findViewById(R.id.app_item_reflect);
			mContentView = (FrameLayout) findViewById(R.id.app_item_content);
			mContentView.setOnFocusChangeListener(this);
			mContentView.setOnClickListener(this);
			mContentView.setOnLongClickListener(this);
			mContentView.setOnKeyListener(this);
			FocusHelper.add(mContentView);
		}else{
			setOnFocusChangeListener(this);
			setOnClickListener(this);
			setOnLongClickListener(this);
			FocusHelper.add(this);
		}
		
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(false){
			XLog.i("AppItemView{" + ViewPrint.getViewClassAndId(v) + "}="+ hasFocus);
		}
		
		if (hasFocus) {
			ViewParent vp = getParent();
			if (vp != null) {
				vp.bringChildToFront(this);
				/*if(vp instanceof AppCellLayout){
					((AppCellLayout)vp).setSelectView(this);
				}*/
			}
			RecordFocusView.self().setSelectView(mMenuId, this);
			if(AppCellLayout.isNewVersion()){
				refreshNewUI(true);
			}else{
				refreshUI(true);
			}
			//printChildIndex();
		} else {
			if(AppCellLayout.isNewVersion()){
				refreshNewUI(false);
			}else{
				refreshUI(false);
			}
		}
	}
	public void refreshNewUI(boolean hasFocus){
		if(hasFocus){
			mTitleView.setSelected(true);
			mShadowView.setVisibility(View.VISIBLE);
			startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.myapp_new_item_big));
		}else{
			mTitleView.setSelected(false);
			mShadowView.setVisibility(View.INVISIBLE);
			startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.myapp_new_item_small));
		}
	}
	public void refreshUI(boolean hasFocus){
		if(hasFocus){
			mTitleView.setSelected(true);
			switchBackround(true);
			startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.myapp_item_big));
		}else{
			mTitleView.setSelected(false);
			switchBackround(false);
			startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.myapp_item_small));
		}
	}
	void printChildIndex(){
		ViewGroup layout=(ViewGroup)getParent();
		int count=layout.getChildCount();
		XLog.i("print child index");
		for(int i=0;i<count;i++){
			View v=layout.getChildAt(i);
			if(v instanceof GridItemView){
				GridItemView av=(GridItemView) v;
				XLog.i("index="+i+",tag="+av.mApp);
			}
			
		}
		XLog.i("--end");
	}


	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		//XLog.i("new AppItemView(focus="+System.identityHashCode(v)+") event="+event);
		if(event.getAction()==KeyEvent.ACTION_UP){
			if(keyCode==KeyEvent.KEYCODE_BUTTON_Y){
				return true;
			}
		}else if(event.getAction()==KeyEvent.ACTION_DOWN){
			if(keyCode==KeyEvent.KEYCODE_DPAD_UP){
				View nextFocus=v.focusSearch(FOCUS_UP);
				if(nextFocus!=null&&(nextFocus instanceof RadioButton)){
					if(nextFocus.getId()!=mMenuId){
						getRootView().findViewById(mMenuId).requestFocus();
						return true;
					}
				}
			}else if(keyCode==KeyEvent.KEYCODE_DPAD_LEFT){
				View nextFocus=v.focusSearch(FOCUS_LEFT);
				if(nextFocus!=null&&(nextFocus instanceof RadioButton)){
					return true;
				}
			}else if(keyCode==KeyEvent.KEYCODE_DPAD_RIGHT){
				View nextFocus=v.focusSearch(FOCUS_RIGHT);
				if(nextFocus!=null&&(nextFocus instanceof RadioButton)){
					return true;
				}
			}
		}
		return false;
	}
    void checkCurrentFocus(){
    	View v=focusSearch(FOCUS_UP);
		XLog.w("onKeyDown "+ViewPrint.getViewClassAndId(v));
		if(!(v instanceof GridItemView)){
			if(v.getId()!=R.id.navigation_myapp){
				while(v.getParent()!=null){
    				v = (View) v.getParent();
    				if(v.getId()==android.R.id.content){
    					break;
    				}
    				XLog.d("parent="+ViewPrint.getViewClassAndId(v));
    			}
				v= v.findViewById(R.id.navigation_myapp);
				v.requestFocus();
    		}
		}
    }
    
    @Override
    public View focusSearch(int direction) {
    	View v = super.focusSearch(direction);
    	if(direction==FOCUS_UP){
    		XLog.w("onKeyDown "+ViewPrint.getViewClassAndId(v));
    		if(v==null)return null;
    		if(!(v instanceof GridItemView)){
    			if(v.getId()!=mMenuId){
    				while(v.getParent()!=null){
        				v = (View) v.getParent();
        				if(v.getId()==android.R.id.content){
        					break;
        				}
        				XLog.d("parent="+ViewPrint.getViewClassAndId(v));
        			}
    				v= v.findViewById(mMenuId);
        		}
    		}
    	}
    	return v;
    	
    }
    
	public void switchBackround(boolean focus){
		if(focus){
			normalBg=getBackground();
			if(focusBg==null){
				Bitmap ob = ImageUtil.drawableToBitmap(getBackground());
				Bitmap b = ImageHelper.getMyAppShadow(ob);
				focusBg = new BitmapDrawable(b);
			}
			setBackground(focusBg);
		}else{
			setBackgroundDrawable(normalBg);
		}
	}



	public static class Item {
		public int id;
		public int focusId;

	}

	public void setText(String title) {
		if (mTitleView != null) {
			mTitleView.setText(title);
		}
	}

	public void setIcon(Drawable drawable) {
		if (mIconView != null) {
			mIconView.setImageDrawable(drawable);
		}
	}
	public void setContentIcon(int resid) {
		if(AppCellLayout.isNewVersion()){
			if (mContentView != null) {
				mContentView.setBackgroundResource(resid);
			}
		}else{
			setBackgroundResource(resid);
		}
	}
	public void setReflectIcon(Bitmap bitmap){
		if(mReflectView!=null){
			mReflectView.setVisibility(View.VISIBLE);
			mReflectView.setImageBitmap(bitmap);
		}
	}
	
	public void setTag(PathGameBean tag){
		this.mApp=tag;
		if (mTitleView != null) {
			mTitleView.setText(tag.label);
		}
		if (mIconView != null) {
			mApp.setIcon(mIconView);
		}
	}

	


	@Override
	public void onClick(View v) {
		if(mApp!=null){
			mApp.startGame(getContext());
		}
		
	}


	public void setMenuId(int mMenuId) {
		this.mMenuId=mMenuId;
		
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		return false;
	}





}
