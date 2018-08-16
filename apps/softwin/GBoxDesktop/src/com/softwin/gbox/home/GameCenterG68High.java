package com.softwin.gbox.home;

import com.softwin.emulators2.C;
import com.xin.util.XLog;

import android.content.ComponentName;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class GameCenterG68High extends GameCenterG68 {

	public GameCenterG68High(MyWorkspaceNew myWorkspaceNew, View layout, int fromId) {
		super(myWorkspaceNew, layout, fromId);
	}
	
	@Override
	protected void setupNavLayout() {
		mNavLayout=(ViewGroup) mLayout.findViewById(R.id.gc_image_nav_layout);
		mNavs=new ImageView[6];
		mNavs[0]=(ImageView) mNavLayout.findViewById(R.id.gc_image_nav_body);
		mNavs[1]=(ImageView) mNavLayout.findViewById(R.id.gc_image_nav_big);
		mNavs[2]=(ImageView) mNavLayout.findViewById(R.id.gc_image_nav_arcade);
		mNavs[3]=(ImageView) mNavLayout.findViewById(R.id.gc_image_nav_psp);
		mNavs[4]=(ImageView) mNavLayout.findViewById(R.id.gc_image_nav_dc);
		mNavs[5]=(ImageView) mNavLayout.findViewById(R.id.gc_image_nav_n64);
		mNavs[0].setNextFocusUpId(mFromId);
		mNavs[1].setNextFocusUpId(mFromId);
		mNavs[3].setNextFocusUpId(mFromId);
		mNavs[4].setNextFocusUpId(mFromId);
		mNavs[0].setNextFocusLeftId(mNavs[0].getId());
		mNavs[4].setNextFocusRightId(mNavs[4].getId());
		mNavs[5].setNextFocusRightId(mNavs[5].getId());
		for(int i=0;i<mNavs.length;i++){
			mNavs[i].setOnFocusChangeListener(this);
			FocusHelper.add(mNavs[i]);
			mNavs[i].setOnClickListener(this);
		}
	}
	@Override
	public void onClick(View v) {
		XLog.i("GameCenterG68High.onClick");
		mFocusView = v;
		int id=v.getId();
		switch (id) {
		case R.id.gc_image_nav_body:
			((Launcher)mContext).lockNavigation();
			mContentLayoutUI.showLoadingLayout();
			showContentUI();
			mContentLayoutUI.enterBodyGame(id);
			break;
		case R.id.gc_image_nav_big:
			((Launcher)mContext).startApp(new ComponentName("cn.gloud.client", "cn.gloud.client.activities.WelcomeActivity"));
			break;
		case R.id.gc_image_nav_arcade:
			((Launcher)mContext).lockNavigation();
			mContentLayoutUI.showLoadingLayout();
			showContentUI();
			mContentLayoutUI.switchSingleEmulator(id,C.DIR_ARCADE);
			break;
		case R.id.gc_image_nav_psp:
			((Launcher)mContext).lockNavigation();
			mContentLayoutUI.showLoadingLayout();
			showContentUI();
			mContentLayoutUI.switchSingleEmulator(id,C.DIR_PSP);
			break;
		case R.id.gc_image_nav_dc:
			((Launcher)mContext).lockNavigation();
			mContentLayoutUI.showLoadingLayout();
			showContentUI();
			mContentLayoutUI.switchSingleEmulator(id,C.DIR_DC);
			break;
		case R.id.gc_image_nav_n64:
			((Launcher)mContext).lockNavigation();
			mContentLayoutUI.showLoadingLayout();
			showContentUI();
			mContentLayoutUI.switchSingleEmulator(id,C.DIR_N64);
			break;
		default:
			break;
		}
	}

}
