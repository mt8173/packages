package com.softwin.gbox.home;

import java.io.File;

import com.xin.pop.ActionItem;
import com.xin.pop.QuickAction;
import com.xin.util.ApkLoader;
import com.xin.util.AppLargeIconLoader;
import com.xin.util.ImageHelper;
import com.xin.util.ImageUtil;
import com.xin.util.SimpleAppsModel;
import com.xin.util.ViewPrint;
import com.xin.util.XLog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.view.MotionEvent;
import android.view.TouchDelegate;
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

public class AppItemView extends FrameLayout implements OnFocusChangeListener,View.OnClickListener,View.OnLongClickListener, View.OnKeyListener {
	private TextView mTitleView;
	private ImageView mIconView;
	private Drawable normalBg;
	private Drawable focusBg;
	private SimpleAppsModel mApp;
	private int mMenuId;
	
	private ImageView mReflectView;
	private ImageView mShadowView;
	private FrameLayout mContentView;
	public AppItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttribute(context, attrs);
		initLayout();
	}

	public AppItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAttribute(context, attrs);
		initLayout();
	}

	public AppItemView(Context context) {
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
			if(v instanceof AppItemView){
				AppItemView av=(AppItemView) v;
				XLog.i("index="+i+",tag="+av.mApp.title);
			}
			
		}
		XLog.i("--end");
	}
	private void enterUninstallProcess(){
		ViewParent vp = getParent();
		if (vp != null) {
			if(vp instanceof AppCellLayout){
				if(!((AppCellLayout)vp).isUnistall())return;
			}
		}
		PackageManager pm=getContext().getPackageManager();
		try {
			ActivityInfo info=pm.getActivityInfo(mApp.componentName, 0);
			boolean mUpdatedSysApp = (info.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
			boolean mSysApp = (info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
			if(mUpdatedSysApp){
				ApkLoader.uninstallApk(getContext(), mApp.componentName.getPackageName());
			}else{
				if(mSysApp){
					Toast.makeText(getContext(), R.string.mygame_tip_cantdelete, Toast.LENGTH_SHORT).show();
				}else{
					ApkLoader.uninstallApk(getContext(), mApp.componentName.getPackageName());
				}
			}
			
		} catch (NameNotFoundException e) {
			String tip=getResources().getString(R.string.mygame_tip_not_install, mApp.title);
			Toast.makeText(getContext(), tip, Toast.LENGTH_SHORT).show();
		}
		
	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		//XLog.i("AppCellLayout onKeyUp="+KeyEvent.keyCodeToString(keyCode));
		if(keyCode==KeyEvent.KEYCODE_BUTTON_Y){
			enterUninstallProcess();
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		//XLog.i("new AppItemView(focus="+System.identityHashCode(v)+") event="+event);
		if(event.getAction()==KeyEvent.ACTION_UP){
			if(keyCode==KeyEvent.KEYCODE_BUTTON_Y){
				enterUninstallProcess();
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
		if(!(v instanceof AppItemView)){
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
    		if(!(v instanceof AppItemView)){
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
	
	public void setTag(SimpleAppsModel tag){
		this.mApp=tag;
		if (mTitleView != null) {
			mTitleView.setText(tag.title);
		}
		if (mIconView != null) {
			if(tag.icon!=null){
				mIconView.setImageDrawable(tag.icon);
			}else if(tag.game.iconPath!=null){
				File file=new File(tag.game.iconPath);
				if(file.exists()){
					mIconView.setImageURI(Uri.fromFile(file));
				}
			}
			if(ApkLoader.isInstalled(getContext().getPackageManager(), tag.componentName.getPackageName())){
				new AppLargeIconLoader(getContext()).execute(mIconView,tag.componentName);
			}
		}
	}
	public void updateTag(SimpleAppsModel app){
		if(!mApp.componentName.equals(app)){
			setTag(app);
		}
	}
	
	public boolean sameAs(String package_){
		if(mApp!=null){
			return mApp.componentName.getPackageName().equals(package_);
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		if(mApp!=null){
			PackageManager pm=getContext().getPackageManager();
			if(isAppInstalled(pm,mApp.componentName.getPackageName())){
				if(mApp.game!=null&&mApp.game.existData()){
					XLog.i("onClick,build FileCopyObject");
					FileCopyHandler copyHandler=new FileCopyHandler(getContext(),mApp);
					XLog.i("onClick,build FileCopyObject2");
					copyHandler.start();
					XLog.i("onClick,build FileCopyObject3");
				}else{
					mApp.startActivitySafely(getContext());
					
					
				}
			}else{
				if(mApp.game.apkPath!=null){
					File file=new File(mApp.game.apkPath);
					if(file.exists()){
						ApkLoader.installApk(getContext(), mApp.game.apkPath);
					}else{
						Toast.makeText(getContext(), "not found setup file!", Toast.LENGTH_SHORT).show();
					}
					
				}else{
					String tip=getResources().getString(R.string.mygame_tip_not_install, mApp.title);
					Toast.makeText(getContext(), tip, Toast.LENGTH_SHORT).show();
				}
			}
		}
		
	}
	private static boolean isAppInstalled(PackageManager pm, String pkg){
		try{
			pm.getPackageInfo(pkg,PackageManager.GET_ACTIVITIES);
			return true;
		}catch(PackageManager.NameNotFoundException e){
			return false;
		}
	}
	private static final int ID_UP     = 1;
	private static final int ID_DOWN   = 2;
	private static final int ID_SEARCH = 3;
	private static final int ID_INFO   = 4;
	private static final int ID_ERASE  = 5;	
	private static final int ID_OK     = 6;
	@Override
	public boolean onLongClick(View v) {
		if(!StaticVar.self().canMove){
			openAppDetails();
			return true;
		}
		if(mMenuId == R.id.navigation_netgame){
			openAppDetails();
			return true;
		}
		final QuickAction quickAction= new QuickAction(getContext(), QuickAction.HORIZONTAL);
		ActionItem prevItem 	= new ActionItem(ID_UP, "Move", getResources().getDrawable(R.drawable.menu_up_arrow));
        ActionItem infoItem 	= new ActionItem(ID_INFO, "Info", getResources().getDrawable(R.drawable.menu_info));
		quickAction.addActionItem(prevItem);
        quickAction.addActionItem(infoItem);
		quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {			
			@Override
			public void onItemClick(QuickAction source, int pos, int actionId) {				
				ActionItem actionItem = quickAction.getActionItem(pos);
				//here we can filter which action item was clicked with pos or actionId parameter
				if (actionId == ID_UP) {
					if(mApp.canMove){
						doMoveTo(-1);
					}else{
						Toast.makeText(getContext(), "This cannot be moved!", Toast.LENGTH_SHORT).show();
					}
				} else if (actionId == ID_INFO) {
					openAppDetails();
				}
			}
		});
		quickAction.show(v);
		return true;
	}

	public void doMoveTo(int type){
		//Toast.makeText(getContext(), getParent().getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
		BoxApplication  ba=(BoxApplication)((Activity)getContext()).getApplication();
		AppDatabase db=ba.getDatabase();
		int currentType=AppDatabase.mapToType(getParent().getClass().getSimpleName());
		db.delete(mApp.componentName.getPackageName());
		if(currentType==AppDatabase.TYPE_MYAPP){
			db.insert(mApp.componentName.getPackageName(), null, AppDatabase.TYPE_MYGAME, AppDatabase.INDEX_APP_START, true);
		}else if(currentType==AppDatabase.TYPE_MYGAME){
			
		}
		refresh();
	}
	
	public void refresh(){
		Intent intent = new Intent(BoxModel.ACTION_ADD);
		intent.putExtra(BoxModel.FIELD_PACKAGE, mApp.componentName.getPackageName());
		getContext().sendBroadcast(intent);
	}
	
	public void openAppDetails(){
		String pkg=mApp.componentName.getPackageName();
		if(ApkLoader.isInstalled(getContext().getPackageManager(), pkg)){
			AppDetails.showInstalledAppDetails(getContext(), pkg);
		}else{
			Toast.makeText(getContext(), "please install application...", Toast.LENGTH_SHORT).show();
		}
		
	};

	public void setMenuId(int mMenuId) {
		this.mMenuId=mMenuId;
		
	}


}
