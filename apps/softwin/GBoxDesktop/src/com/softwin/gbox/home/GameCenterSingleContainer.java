package com.softwin.gbox.home;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.softwin.emulators2.C;
import com.xin.util.BaseHandler;
import com.xin.util.RunTimeTotal;
import com.xin.util.SimpleAppsModel;
import com.xin.util.ViewPrint;
import com.xin.util.XLog;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.ViewGroup;
public class GameCenterSingleContainer extends RelativeLayout implements ViewGroup.OnHierarchyChangeListener {
	private static final boolean DEBUG = false;
	private View loadView;
	private TextView resultView;
	private GameHandler mHandler;
	private LayoutInflater mInflater;
	private GameCenterHorizontalScrollView mScrollView;
	private int mScreenWidth;
	private GameCenterPage mPage;
	public GameCenterSingleContainer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public GameCenterSingleContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GameCenterSingleContainer(Context context) {
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

	private String mType;
	private int mFromId;
	public void loadItemImage(){
		mHandler.removeCallbacks(mPngLoad);
		mHandler.postDelayed(mPngLoad, 100);
	}


	@Override
	public void onChildViewAdded(View parent, View v) {
		if(DEBUG)XLog.i("onChildViewAdded "+ViewPrint.getViewClassAndId(v));
		int id=v.getId();
		if(id==R.id.game_center_scroll){
			mScrollView = (GameCenterHorizontalScrollView) v;
			mScrollView.setOnMoveLinstener(new GameCenterHorizontalScrollView.OnMoveLinstener() {
				public void move(int l, int t, int oldl, int oldt) {
					loadItemImage();
				}
			});
			mPage=(GameCenterPage) mScrollView.findViewById(R.id.game_center_page);
		}else if(id==R.id.loadcontent_doing){
			loadView = v;
			TextView tv=(TextView) loadView.findViewById(R.id.loadcontent_doing_text);
			tv.setText(R.string.gc_loading_text);
		}else if(id==R.id.loadcontent_result){
			resultView = (TextView) v;
		}
	}


	@Override
	public void onChildViewRemoved(View parent, View v) {
		if(DEBUG)XLog.i("onChildViewRemoved "+ViewPrint.getViewClassAndId(v));
		
	}

	public void syncAddView(GameCenterPage layout,GameCenterItemView itemView){
		mHandler.obtainMessage(GameHandler.WHAT_ADD_VIEW, new Object[]{layout,itemView}).sendToTarget();
	}
	public void loadContent() {
		mHandler.sendEmptyMessage(GameHandler.WHAT_LOAD);
		Runnable thread=new Runnable() {
			public void run() {
				RunTimeTotal test=new RunTimeTotal("createView");
				GameCenterPage page=mPage;
				if(page==null){
					page=new GameCenterPage(getContext());
					page.reset(mFromId);
					page.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT));
				}
				if(page!=null){
					List<PathGameBean> beans= getGameByType();
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
						itemView.setLayoutParams(page.generateDefaultLayoutParams());
						itemView.setTag(bean);
						if(i<=6){
							itemView.loadImageBeforeAddView();
						}
						syncAddView(page,itemView);
						if(i==count-1 || i == 4){
							mHandler.obtainMessage(GameHandler.WHAT_UI, page).sendToTarget();
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
	


	private List<PathGameBean> getGameByType() {
		String type=this.mType;
		String path;
		if(C.DIR_ARCADE.equals(type)){
			path= StaticVar.self().mInsertPath+"/ARCADE";
		}else if(C.DIR_N64.equals(type)){
			path= StaticVar.self().mInsertPath+"/N64";
		}else if(C.DIR_PS1.equals(type)){
			path= StaticVar.self().mInsertPath+"/PS1";
		}else if(C.DIR_PSP.equals(type)){
			path= StaticVar.self().mInsertPath+"/PSP";
		}else if(C.DIR_DC.equals(type)){
			path= StaticVar.self().mInsertPath+"/DC";
		}else{
			throw new RuntimeException("unknown type");
		}
		return GameCenterContainer.getAllGame(path,type);
	}

	public void setType(String type) {
		this.mType=type;
		
	}

	public void setFromId(int fromId) {
		this.mFromId=fromId;
		
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
				page.addView(child);
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

}
