package com.softwin.gbox.home;

import java.util.HashMap;

import com.softwin.gbox.home.setting.ParentLock;
import com.xin.util.ImageHelper;
import com.xin.util.ViewPrint;
import com.xin.util.XLog;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

public class MyWorkspaceNew extends FrameLayout {
	public static final boolean DEBUG = false;
	public static final int WHAT_LOADING = 1;
	public static final int WHAT_SHOW_CONTENT = 2;
	public static final int WHAT_SHOW_ERROR = 3;
	public static final int WHAT_SHOW_SEARCH = 4;
	public static final int WHAT_REFERSH_SEARCH = 5;
	public static final int WHAT_LOCK = 6;
	public static final int WHAT_SHOW_ONLY = 7;

	public static final int STATE_LOADING = 1;
	public static final int STATE_ERROR = 2;
	public static final int STATE_SEARCH = 3;
	public static final int STATE_SETTINGS = 4;
	public static final int STATE_GAME_CENTER = 5;
	public static final int STATE_MY_GAMES = 6;
	public static final int STATE_MY_APPS = 7;
	public static final int STATE_VIDEO = 8;

	private boolean mMerging;
	private LayoutInflater mInflater;
	private ParentLock mLock;
	private View mLoadView;
	private TextView mResultView;
	private View mLockView;
	private FrameLayout mContentLayout;
	private int mCurrentViewId;
	private int currentSelectScreen = View.NO_ID;
	private boolean mRecordFocus = false;
	private boolean mUseLock = true;
	private HashMap<Integer, Integer> fromIdMapToChildId = new HashMap<Integer, Integer>();
	private OnUnHandlerMoveListener mListener;

	interface OnUnHandlerMoveListener {
		boolean unhandledMove(View focused, int direction);

	}

	public void setOnUnHandlerMoveListener(OnUnHandlerMoveListener linstener) {
		this.mListener = linstener;
	}

	public MyWorkspaceNew(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init0(context);
	}

	public MyWorkspaceNew(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init0(context);
	}

	public MyWorkspaceNew(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init0(context);
	}

	private void init0(Context context) {
		mMerging = true;
		mInflater = LayoutInflater.from(context);
		mLock = new ParentLock(context);
		mInflater.inflate(R.layout.view_loadcontent_with_lock, this);
		mMerging = false;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if (!mMerging) {
			mLoadView = findViewById(R.id.loadcontent_doing);
			mResultView = (TextView) findViewById(R.id.loadcontent_result);
			mLockView = findViewById(R.id.loadcontent_lock);
			mContentLayout=(FrameLayout) findViewById(R.id.loadcontent_content);
		}
	}

	public synchronized void setScreen(int screen) {
		this.currentSelectScreen = screen;
		mResultView.setText("");
	}

	public synchronized int getScreen() {
		return currentSelectScreen;
	}
	
	public void setUseLock(boolean useLock){
		this.mUseLock=useLock;
	}

	public synchronized boolean isScreen(int screen) {
		return screen == currentSelectScreen;
	}

	public void showLoadingLayout() {
		mLockView.setVisibility(View.GONE);
		mContentLayout.setVisibility(View.GONE);
		mLoadView.setVisibility(View.VISIBLE);
	}

	public void showResultText(Object obj) {
		String text = null;
		if (obj == null) {
			text = getResources().getString(R.string.common_no_data);
		} else if (obj instanceof CharSequence) {
			text = obj.toString();
		} else if (obj instanceof Integer) {
			text = getResources().getString((Integer) obj);
		}
		mLockView.setVisibility(View.GONE);
		mContentLayout.setVisibility(View.GONE);
		mLoadView.setVisibility(View.VISIBLE);
		mResultView.setText(text);
	}
	private void handleShowTipText(boolean show,Object obj) {
		if(show){
			String text = null;
			if (obj == null) {
				text = getResources().getString(R.string.common_no_data);
			} else if (obj instanceof CharSequence) {
				text = obj.toString();
			} else if (obj instanceof Integer) {
				text = getResources().getString((Integer) obj);
			}
			mResultView.setText(text);
		}else{
			mResultView.setText("");
		}
	}
	public void showTipText(boolean show,Object obj){
		mHandler.obtainMessage(WHAT_SHOW_ONLY, show?1:0, 0, obj).sendToTarget();
	}

	

	public void showLock() {
		if (true) {
			mHandler.obtainMessage(WHAT_LOCK).sendToTarget();
		} else {
			mHandler.obtainMessage(WHAT_SHOW_ERROR, R.string.result_lock).sendToTarget();
		}
	}

	void showLockImpl() {
		mContentLayout.setVisibility(View.GONE);
		mLoadView.setVisibility(View.GONE);
		mLockView.setVisibility(View.VISIBLE);
		mCurrentViewId = R.id.loadcontent_lock;
	}



	public synchronized void showContentLayout(View view) {
		if(view==null)return;
		mLoadView.setVisibility(View.GONE);
		mLockView.setVisibility(View.GONE);
		mContentLayout.setVisibility(View.VISIBLE);
		int layoutId = view.getId();
		int count = mContentLayout.getChildCount();
		boolean viewExist = false;
		for (int i = 0; i < count; i++) {
			View childView = mContentLayout.getChildAt(i);
			if (childView.getId() == layoutId) {
				XLog.i("Workspace.show " + ViewPrint.getViewClassAndId(view));
				viewExist = true;
				requestCurrentViewFocus(childView);
				childView.setVisibility(View.VISIBLE);
				mCurrentViewId=layoutId;
				requestCurrentViewFocus(childView);
				//check null content
				if(view instanceof ViewGroup){
					View view0=((ViewGroup)view).getChildAt(0);
					if(view0!=null&&view0 instanceof AppCellLayout){
						((AppCellLayout)view0).checkNull();
					}
				}
			} else {
				childView.setVisibility(View.GONE);
			}
		}
		if (!viewExist) {
			XLog.i("Workspace.add " + ViewPrint.getViewClassAndId(view));
			mContentLayout.addView(view);
			mCurrentViewId=layoutId;
			requestCurrentViewFocus(view);
		}

	}

	public void setRecordFocus(boolean isRecord) {
		this.mRecordFocus = isRecord;
	}

	public void requestCurrentViewFocus(View view) {
		if (!mRecordFocus) {
			return;
		}
		if (view != null) {
			if (view.getVisibility() == View.VISIBLE) {
				View recordView = RecordFocusView.self().getView(getScreen());
				XLog.i("RecordFocusView= " + recordView);
				if (recordView != null) {
					recordView.requestFocus(View.FOCUS_UP);
					recordView.requestFocus(View.FOCUS_DOWN);
					recordView.requestFocus(View.FOCUS_LEFT);
					recordView.requestFocus(View.FOCUS_RIGHT);
				}
			} else {
				if (view instanceof ViewGroup) {
					View recordView = RecordFocusView.self().getView(getScreen());
					XLog.i("gone-RecordFocusView= " + recordView);
					if (recordView != null) {
						ViewGroup itemView = (ViewGroup) view;
						XLog.i("showContentLayout2 " + itemView.getFocusedChild());
						itemView.requestChildFocus(recordView, null);
						XLog.i("showContentLayout3 " + itemView.getFocusedChild());
					}
				}
			}
		}
	}

	public View getCachedLayout(int id) {
		int rootId = getChildIdByFromId(id);
		int count = mContentLayout.getChildCount();
		for (int index = 0; index < count; index++) {
			View v = mContentLayout.getChildAt(index);
			if (v != null && v.getId() == rootId) {
				return v;
			}
		}
		return null;
	}

	Handler mHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
			case WHAT_LOADING:
				showLoadingLayout();
				break;
			case WHAT_SHOW_ERROR:
				showResultText(msg.obj);
				break;
			case WHAT_SHOW_ONLY:
				handleShowTipText(msg.arg1!=0, msg.obj);
				break;
			case WHAT_LOCK:
				showLockImpl();
				break;
			case WHAT_SHOW_CONTENT:
				Object viewObj = msg.obj;
				if (viewObj != null) {
					showContentLayout((View) viewObj);
				} else {
					XLog.e("WHAT_SHOW_CONTENT error!!!!!");
				}

				break;
			case WHAT_SHOW_SEARCH:
				if (DEBUG)
					XLog.i("show search");
				View layout = (View) msg.obj;
				showContentLayout(layout);
				if (msg.arg1 == 1) {
					KeywordsFlow mFlow = (KeywordsFlow) layout.findViewById(R.id.search_flow);
					mFlow.go2Show();
				}
				break;
			case WHAT_REFERSH_SEARCH:
				if (DEBUG)
					XLog.i("refresh search");
				if (mContentLayout != null) {
					KeywordsFlow mFlow = (KeywordsFlow) mContentLayout.findViewById(R.id.search_flow);
					if (mFlow != null) {
						mFlow.go2Show();
					}
				}
				break;
			default:
				break;
			}
		};
	};

	public boolean dispatchUnhandledMove(View focused, int direction) {
		if (mListener != null) {
			if (mListener.unhandledMove(focused, direction)) {
				return true;
			}
		}
		return super.dispatchUnhandledMove(focused, direction);
	};

	public void onBackPressed() {
		if (mGameCenterG68 != null) {
			mGameCenterG68.showNavigationIfNeed();
		}
	}

	public void repeatHome() {
		if (mGameCenterG68 != null) {
			mGameCenterG68.showNavigationIfNeed();
		}
	}

	public void removeAllOperation() {
		mHandler.removeMessages(WHAT_LOADING);
		mHandler.removeMessages(WHAT_SHOW_CONTENT);
		mHandler.removeMessages(STATE_ERROR);
		mHandler.removeMessages(WHAT_SHOW_SEARCH);
		mHandler.removeMessages(WHAT_REFERSH_SEARCH);
	}

	public void clearWorkspace() {
		mContentLayout.removeAllViews();
	}

	public synchronized void clearWorkspace(int id) {
		View v = getCachedLayout(id);
		if (v != null) {
			XLog.i("remove " + ViewPrint.getViewClassAndId(v));
			mContentLayout.removeView(v);
		}
	}

	public void switchSettings(final int fromId) {
		if (DEBUG)
			XLog.i("switchSettings");
		removeAllOperation();
		setScreen(fromId);
		View layout = getCachedLayout(fromId);
		if (layout != null) {
			if (layout.getId() != mCurrentViewId) {
				if (DEBUG)
					XLog.i("exist!!!switch settings layout[[" + ViewPrint.getViewClassAndId(layout));
				mHandler.obtainMessage(WHAT_SHOW_CONTENT, layout).sendToTarget();
			} else {
				requestCurrentViewFocus(layout);
			}
			return;
		}
		mHandler.sendEmptyMessage(WHAT_LOADING);
		Runnable run = new Runnable() {
			@Override
			public void run() {
				if (DEBUG)
					XLog.i("load setting layout");
				int layoutId = StaticVar.self().mSettingLayoutId;
				View layout = mInflater.inflate(layoutId, null);
				relateFromIdToChildId(fromId, layout.getId());
				if (isScreen(fromId)) {
					mHandler.obtainMessage(WHAT_SHOW_CONTENT, layout).sendToTarget();
				}

			}
		};
		new Thread(run).start();
	}

	public void switchGameCenter(int id) {
		int type=StaticVar.self().mType;
		switch(type){
		case StaticVar.TYPE_GBOX:
			switchGameCenterByGBox(id);
			break;
		case StaticVar.TYPE_G68:
			switchGameCenterByG68(id);
			break;
		case StaticVar.TYPE_GBOX_NEW:
			switchGameCenterByG68High(id);
			break;
		case StaticVar.TYPE_TABLET_Q9:
		case StaticVar.TYPE_TABLET_Q9_EN:
		case StaticVar.TYPE_TABLET_XD:
		case StaticVar.TYPE_TABLET_ANDROID7:
		case StaticVar.TYPE_TABLET_XD_EN:
		case StaticVar.TYPE_APK:
			switchGameCenterByG9(id);
			break;
		default:
			switchGameCenterByQ88(id);
			break;
		}
	}

	public void switchSingleEmulator(int fromId, String type) {
		removeAllOperation();
		setScreen(fromId);
		if(showLockIfNeed(fromId))return;
		View layout = getCachedLayout(fromId);
		if (layout != null) {
			if (layout.getId() != mCurrentViewId) {
				mHandler.obtainMessage(WHAT_SHOW_CONTENT, layout).sendToTarget();
			}
			return;
		}
		showLoadingLayout();
		if (DEBUG)
			XLog.i("load game center layout");
		String nameString="category_emulator_single_"+type.toLowerCase();
		int id=getResources().getIdentifier(nameString, "id", getContext().getPackageName());
		if(id==0){
			throw new IllegalArgumentException(nameString+" is must!!!");
		}
		GameCenterSingleContainer newLayout = (GameCenterSingleContainer) mInflater.inflate(R.layout.category_emulator_single, null);
		newLayout.setId(id);
		relateFromIdToChildId(fromId, newLayout.getId());
		newLayout.setType(type);
		newLayout.setFromId(fromId);
		newLayout.loadContent();
		if (isScreen(fromId)) {
			showContentLayout(newLayout);
		}
	}

	public void switchGameCenterByGBox(int fromId) {
		int layoutId=R.layout.category_game_center;
		createLayoutAtMain(fromId, layoutId);
	}

	public void switchGameCenterByG9(int navItemId) {
		int layoutId=R.layout.category_game_center_g9;
		View layout=createLayoutAtMain(navItemId, layoutId);
		if(layout!=null){
			new TabletG9Helper(layout, navItemId);
		}
	}
	public void switchGameCenterByQ88(int navItemId) {
		int layoutId=R.layout.category_game_center_tablet;
		View layout=createLayoutAtMain(navItemId, layoutId);
		if(layout!=null){
			new TabletNormalHelper(layout, navItemId);
		}
	}

	GameCenterG68 mGameCenterG68;
	public void switchGameCenterByG68(int navItemId) {
		int layoutId=R.layout.category_game_center_g68;
		View layout=createLayoutAtMain(navItemId, layoutId);
		if(layout!=null){
			mGameCenterG68 = new GameCenterG68(this, layout, navItemId);
		}
	}
	public void switchGameCenterByG68High(int navItemId) {
		int layoutId=R.layout.category_game_center_g68_high;
		View layout=createLayoutAtMain(navItemId, layoutId);
		if(layout!=null){
			mGameCenterG68 = new GameCenterG68High(this, layout, navItemId);
		}
	}
	private View createLayoutAtMain(int navItemId,int layoutId) {
		removeAllOperation();
		setScreen(navItemId);
		if(showLockIfNeed(navItemId))return null;
		View layout = getCachedLayout(navItemId);
		if (layout != null) {
			if (layout.getId() != mCurrentViewId) {
				mHandler.obtainMessage(WHAT_SHOW_CONTENT, layout).sendToTarget();
			}
			return null;
		} 
		String navIdString=ViewPrint.getIdString(getResources(), navItemId);
		String layoutIdString=ViewPrint.getIdString(getResources(), layoutId);
		XLog.i("createLayout "+navIdString+"-->"+layoutIdString);
		layout = mInflater.inflate(layoutId, null);
		relateFromIdToChildId(navItemId, layout.getId());
		if (isScreen(navItemId)) {
			showContentLayout(layout);
		}
		return layout;
	}

	public void switchNetGames(int id, boolean canUnistall) {
		enterAppCenter(id, R.layout.category_net_game, canUnistall);
	}
	
	public void switchMyGames(int id, boolean canUnistall) {
		enterAppCenter(id, R.layout.category_my_game2, canUnistall);
	}

	public void switchMyApps(int id, boolean canUnistall) {
		enterAppCenter(id, R.layout.category_my_app, canUnistall);
	}

	public void switchRecords(int id) {
		enterAppCenter(id, R.layout.category_record, false);
	}

	public void enterVideoCenter(int id) {
		enterAppCenter(id, R.layout.category_video);

	}

	public void enterBodyGame(int id) {
		enterAppCenter(id, R.layout.category_body_game, false);
	}

	public void switchSearchLayout(int id) {
		enterAppCenter(id, R.layout.category_game_download);
	}

	void enterAppCenter(final int navItemId, final int layoutId) {
		enterAppCenter(navItemId, layoutId, false);
	}

	public void enterAppCenter(final int navItemId, final int layoutId, final boolean canInstall) {
		removeAllOperation();
		setScreen(navItemId);
		if(showLockIfNeed(navItemId))return;
		View layout = getCachedLayout(navItemId);
		if (layout != null) {
			if (layout.getId() != mCurrentViewId) {
				mHandler.obtainMessage(WHAT_SHOW_CONTENT, layout).sendToTarget();
			} else {
				requestCurrentViewFocus(layout);
			}
			return;
		} else {
			mHandler.sendEmptyMessage(WHAT_LOADING);
			Runnable run = new Runnable() {
				@Override
				public void run() {
					HorizontalScrollView layout = (HorizontalScrollView) mInflater.inflate(layoutId, null);
					relateFromIdToChildId(navItemId, layout.getId());
					AppCellLayout gridView = (AppCellLayout) layout.getChildAt(0);
					gridView.setMenuId(navItemId);
					gridView.setUnistall(canInstall);
					AppCellLayout.AppLoader appStyle = gridView.createAndGet();
					appStyle.init();
					if (isScreen(navItemId)) {
						mHandler.obtainMessage(WHAT_SHOW_CONTENT, layout).sendToTarget();
					}

				}
			};
			new Thread(run).start();
		}
	}
	private boolean showLockIfNeed(int navItemId){
		if(mUseLock){
			int mask = getMaskByScreen(navItemId);
			if (mask != ParentLock.MASK_NO_PASSWORD) {
				if (mLock.isLock(mask)) {
					showLock();
					return true;
				}
			}
		}
		return false;
	}

	private void relateFromIdToChildId(int fromId, int childId) {
		if (fromId == View.NO_ID || childId == View.NO_ID) {
			throw new RuntimeException("fromId or childId is -1");
		}
		fromIdMapToChildId.put(fromId, childId);
	}

	private int getChildIdByFromId(int fromId) {
		Integer temp = fromIdMapToChildId.get(fromId);
		if (temp != null) {
			return temp;
		} else {
			return View.NO_ID;
		}
	}

	public int getMaskByScreen(int screen) {
		int[] lockArray;
		int type=StaticVar.self().mType;
		if (type == StaticVar.TYPE_G68||type == StaticVar.TYPE_GBOX_NEW) {
			int[] lock = { R.id.navigation_video, R.id.navigation_gamecenter, R.id.navigation_myapp };
			lockArray = lock;
		} else if(type == StaticVar.TYPE_TABLET_G58){
			int[] lock = { R.id.navigation_gamecenter,R.id.navigation_mygame,R.id.navigation_search,  R.id.navigation_myapp };
			lockArray = lock;
		} else if(type == StaticVar.TYPE_TABLET_Q9){
			int[] lock = { R.id.navigation_gamecenter,R.id.navigation_pc_game,R.id.navigation_mygame,  R.id.navigation_myapp };
			lockArray = lock;
		} else if(type == StaticVar.TYPE_TABLET_Q9_EN){
			int[] lock = { R.id.navigation_gamecenter,R.id.navigation_mygame,  R.id.navigation_myapp };
			lockArray = lock;
		} else if(type == StaticVar.TYPE_TABLET_XD){
			int[] lock = {R.id.navigation_netgame,  R.id.navigation_gamecenter,R.id.navigation_pc_game,R.id.navigation_mygame,  R.id.navigation_myapp };
			lockArray = lock;
		} else if(type == StaticVar.TYPE_TABLET_ANDROID7){
			int[] lock = { R.id.navigation_gamecenter,R.id.navigation_pc_game,R.id.navigation_mygame,  R.id.navigation_myapp };
			lockArray = lock;
		} else if(type == StaticVar.TYPE_TABLET_XD_EN){
			int[] lock = { R.id.navigation_gamecenter,R.id.navigation_mygame,  R.id.navigation_myapp };
			lockArray = lock;
		}else {
			int[] lock = { R.id.navigation_video, R.id.navigation_gamecenter, R.id.navigation_mygame, R.id.navigation_myapp, R.id.navigation_search };
			lockArray = lock;
		}
		for (int index = 0; index < lockArray.length; index++) {
			if (lockArray[index] == screen) {
				return ParentLock.getMask(index);
			}
		}
		return ParentLock.MASK_NO_PASSWORD;
	}
	
	

	public void switchService(final int fromId) {
		if (DEBUG)
			XLog.i("switchService");
		createLayoutAtMain(fromId, R.layout.category_service);

	}
	public void switchPCGame(final int fromId) {
		if (DEBUG)
			XLog.i("switchService");
		View layout=createLayoutAtMain(fromId, R.layout.category_pc_game);
		//addImageShadow((ImageView)layout.findViewById(R.id.settings_pc_dest1),R.drawable.game_pc01);
	}

	private void addImageShadow(ImageView view,int mid) {
		if(view!=null){
			int h=StaticVar.self().mReflectHeight;
			Bitmap newBitmap=ImageHelper.drawableIdToBitmap(view.getResources(), mid);
			Bitmap bm=ImageHelper.createReflectionImageWithOrigin(newBitmap, h);
			view.setImageBitmap(bm);
			ViewGroup.LayoutParams lps=view.getLayoutParams();
			lps.height=lps.height+h;
			view.setLayoutParams(lps);
		}
	}

}
