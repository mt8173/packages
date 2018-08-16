package com.softwin.gbox.home;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;

import com.softwin.emulators2.C;
import com.xin.util.BaseHandler;
import com.xin.util.RunTimeTotal;
import com.xin.util.SimpleAppsModel;
import com.xin.util.ViewPrint;
import com.xin.util.XLog;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.ViewGroup;
public class GameCenterContainer extends RelativeLayout implements View.OnFocusChangeListener,ViewGroup.OnHierarchyChangeListener, OnCheckedChangeListener {
	private static final boolean DEBUG = false;
	private int currentId=NO_ID;
	private View loadView;
	private TextView resultView;
	private GameHandler mHandler;
	private LayoutInflater mInflater;
	private GameCenterHorizontalScrollView mScrollView;
	private int mScreenWidth;
	private WeakHashMap<Integer, GameCenterPage> mCachedViewMap=new WeakHashMap<Integer, GameCenterPage>();
	enum ChildItemState{
		NOT_LOAD,
		LOADING,
		LOADED
	}
	public GameCenterContainer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public GameCenterContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GameCenterContainer(Context context) {
		super(context);
		init();
	}

	private void init() {
		mHandler=new GameHandler(getContext());
		mInflater = LayoutInflater.from(getContext());
		setOnHierarchyChangeListener(this);
		WindowManager wm=(WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		mScreenWidth=wm.getDefaultDisplay().getWidth();
	}
	public void showContentLayout(GameCenterPage view){
		mScrollView.removeAllViews();
		view.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT));
		mScrollView.addView(view);
	}
	public GameCenterPage getCachedLayout(int id) {
		GameCenterPage destView = mCachedViewMap.get(id);
		if(destView!=null){
			if(DEBUG)XLog.i("getCachedLayout["+id+"]="+ViewPrint.getViewClassAndId(destView));
			return destView;
		}
		return null;
		
	}
	public void saveLayout(int id,GameCenterPage layout){
		mCachedViewMap.put(id, layout);
	}
	class GameHandler extends BaseHandler{
		public static final int WHAT_LOAD=WHAT_FIRST+1;
		public static final int WHAT_RESULT_TEXT=WHAT_FIRST+2;
		public static final int WHAT_ADD_VIEW=WHAT_FIRST+3;
		protected static final int WHAT_UI = WHAT_FIRST+4;
		public GameHandler(Context context) {
			super(context);
		}
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_LOAD:
				loadView.setVisibility(View.VISIBLE);
				resultView.setVisibility(View.GONE);
				mScrollView.setVisibility(View.GONE);
				break;
			case WHAT_RESULT_TEXT:
				loadView.setVisibility(View.GONE);
				resultView.setVisibility(View.VISIBLE);
				mScrollView.setVisibility(View.GONE);
				resultView.setText(msg.arg1);
				break;
			case WHAT_ADD_VIEW:
				Object[] objArray=(Object[]) msg.obj;
				GameCenterPage page=(GameCenterPage) objArray[0];
				GameCenterItemView child=(GameCenterItemView) objArray[1];
				if(page.getType()==currentId){
					if(DEBUG)XLog.i("will add view<< "+child.getName());
					page.addView(child);
				}else{
					if(DEBUG)XLog.i("found id diff,value="+child.getName());
				}
				break;
			case WHAT_UI:
				loadView.setVisibility(View.GONE);
				resultView.setVisibility(View.GONE);
				mScrollView.setVisibility(View.VISIBLE);
				GameCenterPage ui=(GameCenterPage) msg.obj;
				showContentLayout(ui);
				break;
			default:
				super.handleMessage(msg);
				break;
			}
			
		}
	}
	Runnable mPngLoad=new Runnable() {
		
		@Override
		public void run() {
			if(DEBUG)XLog.i("start load png");
			GameCenterPage pageView = (GameCenterPage) mScrollView.getChildAt(0);
			if(pageView==null){
				XLog.i("pageView is null !");
			}else{
				int baseX=mScrollView.getLeft()+mScrollView.getScrollX();
				XLog.i("png "+mScrollView.getLeft()+","+mScrollView.getScrollX());
				int count=pageView.getChildCount();
				for(int i=0;i<count;i++){
					GameCenterItemView item=(GameCenterItemView) pageView.getChildAt(i);
					if(item!=null){
						int left=item.getLeft();
						int right=item.getRight();
						if(right>=baseX-20&&left<=baseX+mScreenWidth){
							item.loadImageAsyc(mHandler);
						}
					}
				}
			}
		}
	};
	public void loadItemImage(){
		mHandler.removeCallbacks(mPngLoad);
		mHandler.postDelayed(mPngLoad, 100);
	}


	@Override
	public void onChildViewAdded(View parent, View v) {
		if(DEBUG)XLog.i("onChildViewAdded "+ViewPrint.getViewClassAndId(v));
		int id=v.getId();
		if(id==R.id.nav_game_center){
			RadioGroup mNavigationLayout=(RadioGroup) v;
			mNavigationLayout.setOnCheckedChangeListener(this);
			for(int i=0;i<mNavigationLayout.getChildCount();i++){
				View view=mNavigationLayout.getChildAt(i);
				view.setOnFocusChangeListener(this);
				view.setOnKeyListener(keyListener);
				if(i == 0){
					((RadioButton)view).setChecked(true);
				}
			}
		}else if(id==R.id.game_center_scroll){
			mScrollView = (GameCenterHorizontalScrollView) v;
			mScrollView.setOnMoveLinstener(new GameCenterHorizontalScrollView.OnMoveLinstener() {
				public void move(int l, int t, int oldl, int oldt) {
					loadItemImage();
				}
			});
		}else if(id==R.id.loadcontent_doing){
			loadView = v;
			TextView tv=(TextView) loadView.findViewById(R.id.loadcontent_doing_text);
			tv.setText(R.string.gc_loading_text);
		}else if(id==R.id.loadcontent_result){
			resultView = (TextView) v;
		}
	}
    View.OnKeyListener keyListener=new OnKeyListener() {
		@Override
		public boolean onKey(View child, int keyCode, KeyEvent event) {
			if(keyCode == KeyEvent.KEYCODE_DPAD_UP){
				View v=child.focusSearch(FOCUS_UP);
				XLog.w("onKey "+ViewPrint.getViewClassAndId(v));
				if(!(v instanceof GameCenterItemView)){
					if(v.getId()!=R.id.navigation_gamecenter){
						while(v.getParent()!=null){
		    				v = (View) v.getParent();
		    				if(v.getId()==android.R.id.content){
		    					break;
		    				}
		    				XLog.d("parent="+ViewPrint.getViewClassAndId(v));
		    			}
						v= v.findViewById(R.id.navigation_gamecenter);
						v.requestFocus();
		    		}
				}
	    	}
			return false;
		}
	};

	@Override
	public void onChildViewRemoved(View parent, View v) {
		if(DEBUG)XLog.i("onChildViewRemoved "+ViewPrint.getViewClassAndId(v));
		
	}
	private void selectFrame(final int id){
		GameCenterPage layout=getCachedLayout(id);
		if(layout!=null){
			if(DEBUG)XLog.i("childcount="+layout.getChildCount());
			showContentLayout(layout);
		}else{
			switchFrame(id,null);
		}
	}
	public void syncAddView(GameCenterPage layout,GameCenterItemView itemView){
		mHandler.obtainMessage(GameHandler.WHAT_ADD_VIEW, new Object[]{layout,itemView}).sendToTarget();
	}
	private void switchFrame(final int id,final GameCenterPage layout) {
		mHandler.sendEmptyMessage(GameHandler.WHAT_LOAD);
		Runnable thread=new Runnable() {
			public void run() {
				RunTimeTotal test=new RunTimeTotal("createView");
				GameCenterPage page=layout;
				if(page==null){
					page=new GameCenterPage(getContext());
					page.reset(id);
					page.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT));
					//saveLayout(id,page);
				}
				if(page!=null){
					List<PathGameBean> beans= getGameByType(id);
					test.next();
					if(beans==null||beans.size()<=0){
						mHandler.obtainMessage(GameHandler.WHAT_RESULT_TEXT, R.string.result_game_null, 0).sendToTarget();
						return;
					}
					int count = beans.size();
					XLog.i("load game center num="+count);
					for(int i=0;i<count;i++){
						PathGameBean bean=beans.get(i);
						GameCenterItemView itemView=(GameCenterItemView) mInflater.inflate(R.layout.item_gc, null);
						//add set next focus
						//setNextFocusId(itemView, i, count);
						itemView.setLayoutParams(page.generateDefaultLayoutParams());
						itemView.setTag(bean);
						if(i<=6){
							itemView.loadImageBeforeAddView();
						}
						if(currentId==id){
							syncAddView(page,itemView);
							if(i==count-1 || i == 4){
								mHandler.obtainMessage(GameHandler.WHAT_UI, page).sendToTarget();
							}
						}else{
							break;
						}
						test.next(bean.label);
					}
					test.next();
				}
				test.total();
			}
		};
		new Thread(thread).start();
		
	}
	
	void setNextFocusId(View view,int index,int count){
		int startId=R.bool.global_show_operation_tip;
		int viewId=startId+index;
		view.setId(viewId);
		int nextFocusLeftId=View.NO_ID;
		int nextFocusRightId=View.NO_ID;
		switch(index%4){
		case 1:
			nextFocusLeftId=index!=0?viewId-1:viewId;
			nextFocusRightId=index+3<count?viewId+3:viewId;
			break;
		case 2:
			nextFocusLeftId=viewId-2;
			nextFocusRightId=index!=count-1?viewId+1:viewId;
			break;
		default:
			nextFocusLeftId=index!=0?viewId-1:viewId;
			nextFocusRightId=index!=count-1?viewId+1:viewId;
		}
		view.setNextFocusLeftId(nextFocusLeftId);
		view.setNextFocusRightId(nextFocusRightId);
	}

	protected List<PathGameBean> getGameByType(int id) {
		String path=null;
		String type=null;
		switch (id) {
		case R.id.gc_nav_arcade:
			path= StaticVar.self().mInsertPath+"/ARCADE";
			type=C.DIR_ARCADE;
			break;
		case R.id.gc_nav_2:
			path = StaticVar.self().mInsertPath+"/N64";
			type=C.DIR_N64;
			break;
		case R.id.gc_nav_3:
			path = StaticVar.self().mInsertPath+"/PS1";
			type=C.DIR_PS1;
			break;
		case R.id.gc_nav_4:
			path = StaticVar.self().mInsertPath+"/PSP";
			type=C.DIR_PSP;
			break;
		case R.id.gc_nav_dc:
			path = StaticVar.self().mInsertPath+"/DC";
			type=C.DIR_DC;
			break;
		case R.id.gc_nav_5:
			return getGame5();
		default:
			break;
		}
		return getAllGame(path,type);
	}
	public List<PathGameBean> getGame5(){
		ArrayList<PathGameBean> result=new ArrayList<PathGameBean>();
		BoxApplication  ba=(BoxApplication)((Activity)getContext()).getApplication();
		List<SimpleAppsModel> apps = ba.getDatabase().getAppByType(AppDatabase.TYPE_GAME_CENTER);
		for(SimpleAppsModel app:apps){
			result.add(new PathGameBean(app));
		}
		return result;
	}
	public static List<PathGameBean> getAllGame(String path,String type){
		XLog.i("load "+path);
		if(path==null)return null;
		ArrayList<PathGameBean> list=new ArrayList<PathGameBean>();
		File file=new File(path);
		if(file.exists()){
			File[] subFiles=file.listFiles();
			if(subFiles!=null){
				for(int i=0;i<subFiles.length;i++){
					PathGameBean bean=new PathGameBean(subFiles[i]);
					if(bean.isValid()){
						bean.setType(type);
						list.add(bean);
					}
				}
			}
		}
		XLog.i("getAllGame,count="+list.size());
		Collections.sort(list);
		return list;
	}
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(hasFocus){
			if(DEBUG)XLog.i("focus "+ViewPrint.getViewClassAndId(v));
			v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.nav_big));
			if(v instanceof RadioButton){
				((RadioButton) v).setChecked(true);
			}
		}else{
			v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.nav_small));
		}
		
	}
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if(currentId!=checkedId){
			currentId = checkedId;
			selectFrame(checkedId);
		}
		
		
	}
}
