package com.softwin.gbox.home;

import com.softwin.emulators2.C;
import com.xin.util.XLog;

import android.content.ComponentName;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class GameCenterG68 implements OnFocusChangeListener,OnClickListener{
	protected Context mContext;
	protected MyWorkspaceNew mWorkspace;
	protected View mLayout;
	protected ViewGroup mNavLayout;
	protected View mContentLayout;
	protected MyWorkspaceNew mContentLayoutUI;
	
	protected int mFromId;
	protected ImageView[] mNavs;
	
	
	protected View mFocusView;
	public GameCenterG68(MyWorkspaceNew myWorkspaceNew, View layout, int fromId) {
		this.mWorkspace=myWorkspaceNew;
		this.mLayout=layout;
		this.mFromId=fromId;
		this.mContext=this.mWorkspace.getContext();
		setupNavLayout();
		setupContentLayout();
	}
	protected void setupNavLayout(){
		mNavLayout=(ViewGroup) mLayout.findViewById(R.id.gc_image_nav_layout);
		mNavs=new ImageView[4];
		mNavs[0]=(ImageView) mNavLayout.findViewById(R.id.gc_image_nav1);
		mNavs[1]=(ImageView) mNavLayout.findViewById(R.id.gc_image_nav2);
		mNavs[2]=(ImageView) mNavLayout.findViewById(R.id.gc_image_nav3);
		mNavs[3]=(ImageView) mNavLayout.findViewById(R.id.gc_image_nav4);
		mNavs[0].setNextFocusUpId(mFromId);
		mNavs[0].setNextFocusLeftId(R.id.gc_image_nav1);
		mNavs[1].setNextFocusUpId(mFromId);
		mNavs[2].setNextFocusUpId(mFromId);
		mNavs[2].setNextFocusRightId(R.id.gc_image_nav3);
		mNavs[3].setNextFocusRightId(R.id.gc_image_nav4);
		for(int i=0;i<mNavs.length;i++){
			mNavs[i].setOnFocusChangeListener(this);
			FocusHelper.add(mNavs[i]);
			mNavs[i].setOnClickListener(this);
		}
	}
	protected void setupContentLayout(){
		mContentLayout=mLayout.findViewById(R.id.gc_content_layout);
		mContentLayoutUI=(MyWorkspaceNew) mContentLayout.findViewById(R.id.gc_content_layout_ui);
		mContentLayoutUI.setRecordFocus(true);
		mContentLayoutUI.setOnUnHandlerMoveListener(new MyWorkspaceNew.OnUnHandlerMoveListener() {
			private long[] mRepeat=new long[3];
			@Override
			public boolean unhandledMove(View focused, int direction) {
				if(direction==View.FOCUS_UP){
					long time=System.currentTimeMillis();
					mRepeat[0]=time;
					System.arraycopy(mRepeat, 0, mRepeat, 1, mRepeat.length-1);
					if(mRepeat[mRepeat.length-1]>time-500){
						showNavigationIfNeed();
					}
				}
				return true;
			}
		});
		View tip=mContentLayout.findViewById(R.id.gc_content_layout_tip);
		FocusHelper.add(tip);
		tip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showNavigationIfNeed();
			}
		});
	}
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(hasFocus){
			v.bringToFront();
			v.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.nav_big));
		}else{
			v.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.nav_small));
		}
	}
	
	@Override
	public void onClick(View v) {
		XLog.i("GameCenterG68.onClick");
		((Launcher)mContext).lockNavigation();
		mFocusView = v;
		
		int id=v.getId();
		switch (id) {
		case R.id.gc_image_nav1:
			mContentLayoutUI.showLoadingLayout();
			showContentUI();
			mContentLayoutUI.enterBodyGame(id);
			break;
		case R.id.gc_image_nav2:
			mContentLayoutUI.showLoadingLayout();
			showContentUI();
			mContentLayoutUI.switchMyGames(id,false);
			break;
		case R.id.gc_image_nav3:
			mContentLayoutUI.showLoadingLayout();
			showContentUI();
			mContentLayoutUI.switchSingleEmulator(id,C.DIR_ARCADE);
			break;
		case R.id.gc_image_nav4:
			((Launcher)mContext).startApp(new ComponentName("cn.gloud.client", "cn.gloud.client.activities.WelcomeActivity"));
			break;
		default:
			break;
		}
	}
	

	
	public void showContentUI(){
		final Animation hide=AnimationUtils.loadAnimation(mContext, R.anim.fading_out);
		hide.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				XLog.i("hide.onAnimationEnd");
				mNavLayout.setVisibility(View.GONE);
			}
		});
		mNavLayout.startAnimation(hide);
		final Animation show=AnimationUtils.loadAnimation(mContext, R.anim.fading_in);
		show.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				XLog.i("show.onAnimationEnd");
				mContentLayout.setVisibility(View.VISIBLE);
			}
		});
		mContentLayout.startAnimation(show);
		
	}
	public void showNavigationUI(){
		mContentLayout.setVisibility(View.GONE);
		if(mFocusView!=null){
			mNavLayout.requestChildFocus(mFocusView, null);
		}
		mNavLayout.setVisibility(View.VISIBLE);
		if(mFocusView!=null){
			mFocusView.requestFocus(View.FOCUS_UP);
			mFocusView.requestFocus(View.FOCUS_DOWN);
			mFocusView.requestFocus(View.FOCUS_LEFT);
			mFocusView.requestFocus(View.FOCUS_RIGHT);
		}
		((Launcher)mContext).unLockNavigation();
	}
	public void showNavigationIfNeed() {
		if(mContentLayout.getVisibility()==View.VISIBLE){
			showNavigationUI();
		}else if(mNavLayout.getVisibility()==View.VISIBLE){
			((Launcher)mContext).unLockNavigation();
		}
	}

}
