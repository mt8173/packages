package com.softwin.gbox.home;

import java.util.Locale;

import com.softwin.gbox.home.setting.ParentManagerSetting;
import com.softwin.gbox.home.setting.StorageSetting;
import com.softwin.gbox.home.setting.SystemInfoSetting;
import com.softwin.gbox.home.setting.VoiceSetting;
import com.softwin.gbox.home.test.Test;
import com.softwin.keymapping.GuideUtils;
import com.softwin.keymapping.KeyMapUtils;
import com.umeng.analytics.MobclickAgent;
import com.xin.util.ApkLoader;
import com.xin.util.IntentPrint;
import com.xin.util.ViewPrint;
import com.xin.util.XLog;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver.OnGlobalFocusChangeListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.animation.AnimationUtils;

public class Launcher extends Activity implements View.OnFocusChangeListener,BoxModel.Callback, OnCheckedChangeListener{
	public static final boolean IS_TEST = false;
	public static final boolean DEBUG = true;
	public static final boolean DEBUG_FOCUS = true;
	private RadioGroup mNavigationLayout;
	private MyWorkspaceNew mContentView;
	private ImageView mTipView;
	private ImageView mTipView2;
	private TextView mTipText;
	private int mNavigationId=View.NO_ID;
	private Locale mLocale;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(IS_TEST){
			Test.startTest(this);
			finish();
			return;
		}
		XLog.i("Launcher.onCreate");
		StaticVar.self().loadVar(this);
		RecordFocusView.init(this);
		getLayoutInflater();
		if(DEBUG_FOCUS)
		getWindow().getDecorView().getViewTreeObserver().addOnGlobalFocusChangeListener(new OnGlobalFocusChangeListener() {
			@Override
			public void onGlobalFocusChanged(View oldFocus, View newFocus) {
				XLog.i("%%oldFocus="+ViewPrint.getViewClassAndId(oldFocus)+",newFocus="+ViewPrint.getViewClassAndId(newFocus));
				
			}
		});
		initLayout();
		int orientation=getRequestedOrientation();
		XLog.i("orientation="+orientation);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		KeyMapUtils.startService(this);
		GuideUtils.start(this);
	}
	public void initLayout(){
		mLocale=getResources().getConfiguration().locale;
		((BoxApplication)getApplication()).setCallback(this);
		setContentView(StaticVar.self().mLauncherLayoutId);
		setupTip();
		mContentView = (MyWorkspaceNew) findViewById(R.id.launcher_content);
		mNavigationLayout=(RadioGroup) findViewById(R.id.navigation_radiogroup);
		mNavigationLayout.setOnCheckedChangeListener(this);
		for(int i=0;i<mNavigationLayout.getChildCount();i++){
			View view=mNavigationLayout.getChildAt(i);
			view.setOnFocusChangeListener(this);
			view.setOnKeyListener(mNavigationKeyListener);
		}
	}
	public void lockNavigation(){
		XLog.i("lockNavigation");
		//mNavigationLayout.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		mNavigationLayout.setVisibility(View.GONE);
	}
	public void unLockNavigation(){
		//mNavigationLayout.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
		mNavigationLayout.setVisibility(View.VISIBLE);
	}
	public void setupTip(){
		View layout=findViewById(R.id.opoeration_layout);
		if(StaticVar.self().mShowOperationTip){
			mTipView=(ImageView)findViewById(R.id.opoeration_tip);
			mTipView2=(ImageView)findViewById(R.id.opoeration_tip2);
			mTipText = (TextView) findViewById(R.id.operation_tip_text);
		}else{
			layout.setVisibility(View.GONE);
		}
	}

	View.OnKeyListener mNavigationKeyListener=new View.OnKeyListener() {
		
		@Override
		public boolean onKey(View child, int keyCode, KeyEvent event) {
			if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
				int id=child.getId();
				View recordView=RecordFocusView.self().getView(id);
				if(inContentLayout(recordView)){
					if(recordView.requestFocus()){
						XLog.i("success focus "+ViewPrint.getViewClassAndId(recordView));
						return true;
					}else{
						XLog.i("fail focus "+ViewPrint.getViewClassAndId(recordView));
					}
					
				}
	    	}else if(keyCode == KeyEvent.KEYCODE_BUTTON_X){
	    		if(event.getAction()==KeyEvent.ACTION_UP){
	    			reloadCurrentWorkspace();
		    		return true;
	    		}
	    	}
			return false;
		}
		
		public boolean inContentLayout(View child){
			if(child==null)return false;
			ViewParent parent=null;
			while((parent=child.getParent())!=null){
				if(parent instanceof View){
					View view=(View)parent;
					XLog.i("inContentLayout "+ViewPrint.getViewClassAndId(view));
					if(view.getId()==android.R.id.content){
						return true;
					}else{
						child=view;
					}
				}else{
					return false;
				}
			}
			return false;
		}
	};
    
    @Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	public void onPackageAdd(String package_){
	}
	public void onPackageRemove(String package_){
	}
	
	public void showToastTip(final String text){
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				Toast.makeText(Launcher.this, text, Toast.LENGTH_SHORT).show();
			}
			
		});
	}
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if(DEBUG)XLog.i("onCheckedChanged  ...check="+ViewPrint.getIdString(getResources(), checkedId));
	}
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(hasFocus){
			if(DEBUG)XLog.i("focus "+ViewPrint.getViewClassAndId(v));
			v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.nav_big));
			if(v instanceof RadioButton){
				if(DEBUG)XLog.i("setChecked "+ViewPrint.getViewClassAndId(v));
				((RadioButton) v).setChecked(true);
				selectNavigation(v.getId());
			}
		}else{
			if(DEBUG)XLog.i("un-focus "+ViewPrint.getViewClassAndId(v));
			v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.nav_small));
		}
	}
	public void selectNavigation(int id){
		if(id==mNavigationId)return;
		mNavigationId=id;
		loadWorkspace();
	}
	
	public MyWorkspaceNew getWorkspace(){
		return mContentView;
	}
	void reloadAllWorkspace(){
		mContentView.clearWorkspace();
		loadWorkspace();
	}
	void reloadCurrentWorkspace(){
		if(mNavigationId!=View.NO_ID){
			mContentView.clearWorkspace(mNavigationId);
		}
		loadWorkspace();
	}
	public void loadWorkspace(){
		if(mNavigationId==View.NO_ID){
			mNavigationId=R.id.navigation_video;
		}
		switch (mNavigationId) {
		case R.id.navigation_video:
			setTip(R.drawable.operation_tip_menu,-1);
			mContentView.enterVideoCenter(mNavigationId);
			break;
		case R.id.navigation_search:
			setTip(R.drawable.operation_tip_menu,-1);
			mContentView.switchSearchLayout(mNavigationId);
			break;
		case R.id.navigation_gamecenter:
			setTip(R.drawable.operation_tip_menu,-1);
			mContentView.switchGameCenter(mNavigationId);
			break;
		case R.id.navigation_mygame:
			setTip(R.drawable.operation_tip_menu,R.drawable.operation_tip_uninstall);
			mContentView.switchMyGames(mNavigationId,true);
			break;
		case R.id.navigation_netgame:
			setTip(R.drawable.operation_tip_menu,R.drawable.operation_tip_uninstall);
			mContentView.switchNetGames(mNavigationId,true);
			break;
		case R.id.navigation_myapp:
			setTip(R.drawable.operation_tip_menu,R.drawable.operation_tip_uninstall);
			mContentView.switchMyApps(mNavigationId,true);
			break;
		case R.id.navigation_setting:
			setTip(R.drawable.operation_tip_menu,-1);
			mContentView.switchSettings(mNavigationId);
			break;
		case R.id.navigation_pc_game:
			setTip(R.drawable.operation_tip_menu,-1);
			mContentView.switchPCGame(mNavigationId);
			break;
		case R.id.navigation_service:
			setTip(R.drawable.operation_tip_menu,-1);
			mContentView.switchService(mNavigationId);
			break;
		default:
			XLog.w("this is not navigation! "+mNavigationId);
			break;
		}
	}
	public static final String GUIDE_PACKGAE="com.sanbuapp.wade892917520_4";
	public static final String GUIDE_CLASS="cn.box.cloudbox.Cloudbox";
	public void enterSetting(View v){
		if(v==null)return;
		if(DEBUG)XLog.i("settings "+ViewPrint.getViewClassAndId(v));
		switch (v.getId()) {
		case R.id.home_account_setting_content:
			startApp(ParentManagerSetting.class);
			break;
		case R.id.home_setting_language_content:
			//startApp(GuideSetting.class);
			startApp(new ComponentName(GUIDE_PACKGAE, GUIDE_CLASS));
			break;
		case R.id.home_setting_systeminfo_content:
			startApp(SystemInfoSetting.class);
			break;
		case R.id.home_setting_storage_content:
			startApp(StorageSetting.class);
			break;
		case R.id.home_setting_wifi_manager_content:
			startWifiSetting(false);
			break;
		case R.id.home_setting_gamepad_content:
			startBluetoothSetting(false);
			break;
		case R.id.home_app_management_content:
			startApp(new ComponentName("com.android.settings","com.android.settings.screen.ScreenScaleActivity"));
			break;
		case R.id.home_settings_voice_content:
			startApp(new ComponentName("com.softwin.gamepad","com.softwin.gamepad.MainActivity"));
			//startApp(VoiceSetting.class);
			break;
		case R.id.home_setting_video_content:
			startApp(Video.class);
			//startApp(new ComponentName("com.mxtech.videoplayer.ad", "com.mxtech.videoplayer.ad.ActivityMediaList"));
			//startApp(new ComponentName("com.softwin.gbox.settings", "com.softwin.gbox.settings.time.TimeSettings"));
			break;
		case R.id.home_advanced_settings_content:
			startApp(new ComponentName("com.android.settings", "com.android.settings.Settings"));
			break;
		case R.id.home_setting_update_content:
			//startApp(new ComponentName("com.softwin.system.upgrade", "com.softwin.system.upgrade.MainActivity"));
			showSystemBrightness();
			break;
		case R.id.game_ref_xiaoji:
			ApkLoader.open(this, "com.xiaoji.emulator");
			break;
		case R.id.game_ref_ko:
			ApkLoader.open(this, "cn.vszone.tv.gamebox");
			break;
		case R.id.game_ref_ppsspp:
			ApkLoader.open(this, "org.ppsspp.ppsspp");
			break;
		case R.id.home_home_switch_content:
			startApp(new ComponentName("com.softwin.gbox.settings", "com.softwin.gbox.home.setting.HomeSwitchSetting"));
			break;
		default:
			Toast.makeText(this, R.string.do_working, Toast.LENGTH_SHORT).show();
			break;
		}
	}
	
	public void showSystemBrightness(){
		Intent intent = new Intent();
		intent.setClassName("com.android.systemui","com.android.systemui.settings.BrightnessDialog");
		startActivity(intent);
	}
	public void startApp(Class<? extends Activity> clazz){
		Intent intent=new Intent();
		intent.setClass(this, clazz);
		startActivity(intent);
	}
	public void startApp(ComponentName cn){
		try{
			Intent intent=new Intent();
			intent.setComponent(cn);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			startActivity(intent);
		}catch(Exception e){
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	public void startBluetoothSetting(boolean isGuide){
		Intent intent=new Intent();
		intent.setAction("xin.guide.bluetooth");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		if(isGuide){
			intent.putExtra("guide", true);
		}
		startActivity(intent);
	}
	public void startWifiSetting(boolean isGuide){
		Intent intent=new Intent();
		intent.setAction("xin.guide.wifi");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		if(isGuide){
			intent.putExtra("guide", true);
		}
		startActivity(intent);
	}
	public void setTip(int id,int id2){
		if(!StaticVar.self().mShowOperationTip)return;
		String language=getResources().getConfiguration().locale.getLanguage();
		//if("zh".equals(language)){
		if(true){
			mTipText.setVisibility(View.GONE);
			if(id>0){
				mTipView.setVisibility(View.VISIBLE);
				mTipView.setImageResource(id);
			}else{
				mTipView.setVisibility(View.GONE);
			}
			if(id2>0){
				mTipView2.setVisibility(View.VISIBLE);
				mTipView2.setImageResource(id2);
			}else{
				mTipView2.setVisibility(View.GONE);
			}
		}else{
			mTipView.setVisibility(View.GONE);
			mTipView2.setVisibility(View.GONE);
			mTipText.setVisibility(View.VISIBLE);
			if(id==R.drawable.operation_tip_menu&&id2==R.drawable.operation_tip_uninstall){
				mTipText.setText(R.string.tip_menu_and_uninstall);
			}else{
				mTipText.setText(R.string.tip_menu);
			}
		}
		

	}


	@Override
	public void onBackPressed() {
		if(mContentView.getVisibility()==View.VISIBLE){
			mContentView.onBackPressed();
		}
	}
	@Override
	protected void onNewIntent(Intent intent) {
		if (Intent.ACTION_MAIN.equals(intent.getAction())) {
            final boolean alreadyOnHome = (intent.getFlags() &
                    Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
                    != Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT;
			if(alreadyOnHome){
				mContentView.repeatHome();
			}
		}
	}
	@Override
	public boolean dispatchKeyShortcutEvent(KeyEvent event) {
		XLog.i("dispatchKeyShortcutEvent="+event);
		return super.dispatchKeyShortcutEvent(event);
	}
	@Override
	public boolean onKeyShortcut(int keyCode, KeyEvent event) {
		XLog.i("onKeyShortcut="+event);
		return super.onKeyShortcut(keyCode, event);
	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		//XLog.i("onKeyUp="+event);
		//XLog.i("onKeyUp="+KeyEvent.keyCodeToString(keyCode));
		switch (event.getScanCode()) {
		case 59:
			//mNavigationLayout.findViewById(R.id.navigation_video).requestFocus();
			return true;
		case 60:
			mNavigationLayout.findViewById(R.id.navigation_gamecenter).requestFocus();
			return true;
		case 61:
			mNavigationLayout.findViewById(R.id.navigation_myapp).requestFocus();
			return true;
		case 62:
			mNavigationLayout.findViewById(R.id.navigation_setting).requestFocus();
			return true;
		default:
			break;
		}
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			try{
				//startApp(new ComponentName("com.tv.clean", "com.tv.clean.HomeAct"));
				return true;
			}catch (Exception e) {
				System.gc();
				System.gc();
				System.gc();
				return false;
			}
		case KeyEvent.KEYCODE_BUTTON_L1:
			XLog.printMemory();
			break;
		default:
			break;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void forceReloadUI() {
		Locale newLocale=getResources().getConfiguration().locale;
		XLog.i("new="+newLocale+" old="+mLocale);
		String newLaunguage=newLocale.getLanguage();
		String oldLaunguage=mLocale.getLanguage();
		boolean newChina=newLaunguage.equals("zh");
		boolean oldChina=oldLaunguage.equals("zh");
		if(newChina ^ oldChina){
			XLog.i("forceReload");
		}
	}
	public void forceReloadWorksapce(){
		XLog.i("forceReloadWorksapce");
		reloadAllWorkspace();
	}



}
