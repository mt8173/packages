package com.softwin.gbox.home;

import java.io.File;
import java.util.ArrayList;

import com.softwin.emulators2.C;
import com.xin.util.ViewPrint;
import com.xin.util.XLog;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;

public class TabletNormalHelper implements OnCheckedChangeListener, OnItemSelectedListener, OnFocusChangeListener {
	private static final boolean DEBUG = false;
	private View mLoadView;
	private TextView mResultView;
	private TextView mPathView;
	private FrameLayout mContentLayout;
	private View layout;
	protected int currentId = View.NO_ID;
	private Context ctx;
	private String mRootPath;
	private int mFocusUpId;
	public TabletNormalHelper(View groupView,int focusUpId) {
		this.layout = groupView;
		this.mFocusUpId=focusUpId;
		ctx = layout.getContext();
		mLoadView =  layout.findViewById(R.id.loadcontent_doing);
		mResultView = (TextView) layout.findViewById(R.id.loadcontent_result);
		mContentLayout = (FrameLayout) layout.findViewById(R.id.tablet_game_content);
		mPathView=(TextView)layout.findViewById(R.id.tablet_game_path);
		Spinner spinner = (Spinner) layout.findViewById(R.id.tablet_game_select_where);
		String[] mStringArray = ctx.getResources().getStringArray(R.array.tablet_where_array);
		ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(ctx, R.layout.item_where_spinner, mStringArray);

		// 设置下拉列表风格
		mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(mAdapter);
		// 监听Item选中事件
		spinner.setOnItemSelectedListener(this);
		spinner.setNextFocusUpId(focusUpId);
		RadioGroup rg = (RadioGroup) layout.findViewById(R.id.tablet_game_nav_layout);
		rg.setOnCheckedChangeListener(this);
		for (int i = 0; i < rg.getChildCount(); i++) {
			View view = rg.getChildAt(i);
			view.setOnFocusChangeListener(this);
			view.setOnKeyListener(keyListener);
			if (i == 0) {
				((RadioButton) view).setChecked(true);
			}
		}

	}
    View.OnKeyListener keyListener=new OnKeyListener() {
		@Override
		public boolean onKey(View child, int keyCode, KeyEvent event) {
			if(keyCode == KeyEvent.KEYCODE_DPAD_UP){
				View v=child.focusSearch(View.FOCUS_UP);
				XLog.w("onKey "+ViewPrint.getViewClassAndId(v));
				while(v!=null){
					if(v.getId()==mFocusUpId){
						return false;
					}
					if(v instanceof GridItemView){
						return false;
					}
					if(v.getId()==android.R.id.content){
						v= v.findViewById(mFocusUpId);
						v.requestFocus();
    					return false;
    				}
					v=(View) v.getParent();
				}
	    	}
			return false;
		}
	};
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(hasFocus){
			if(DEBUG)XLog.i("focus "+ViewPrint.getViewClassAndId(v));
			v.startAnimation(AnimationUtils.loadAnimation(ctx, R.anim.nav_big));
			if(v instanceof RadioButton){
				((RadioButton) v).setChecked(true);
			}
		}else{
			v.startAnimation(AnimationUtils.loadAnimation(ctx, R.anim.nav_small));
		}
	}
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		XLog.i("TabletGameHelper.onCheckedChanged checkedId="+checkedId);
		switchFrameIfNeed(checkedId,mRootPath);
	}


	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		XLog.i("TabletGameHelper.onItemSelected position="+position+",id="+id);
		String[] mStringArray = ctx.getResources().getStringArray(R.array.tablet_where_array_value);
		String path=mStringArray[position];
		if(mRootPath!=null&&mRootPath.equals(path)){
			XLog.i("not load");
		}else{
			switchFrameIfNeed(currentId,path);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		XLog.i("TabletGameHelper.onNothingSelected");
		
	}

	private synchronized void switchFrameIfNeed(int checkedId,String path) {
		XLog.i("TabletGameHelper.switchFrameIfNeed checkedId="+checkedId+",path="+path);
		if(checkedId==View.NO_ID||TextUtils.isEmpty(path)){
			this.currentId=checkedId;
			this.mRootPath=path;
			return;
		}else if(checkedId==currentId&&path.equals(mRootPath)){
			return;
		}else{
			this.currentId=checkedId;
			this.mRootPath=path;
			switchFrameInner(checkedId, path);
		}
	}
	private void switchFrameInner(final int id,final String fatherPath){
		XLog.i("TabletGameHelper.switchFrameInner");
		mHandler.sendEmptyMessage(SHOW_LOAD);
		final String subPath=getSubPath(id);
		mPathView.setText("/"+subPath);
		new Thread(){
			public void run() {
				File file=new File(fatherPath,subPath);
				String type=getType(id);
				ArrayList<PathGameBean> beans=getGameBeans(file, type);
				
				if(beans.size()<=0){
					File hideFile=new File(fatherPath,getExtendPath(id));
					ArrayList<PathGameBean> hideBeans=getGameBeans(hideFile, type);
					beans.addAll(hideBeans);
				}
				
				LayoutInflater mInflater=LayoutInflater.from(ctx);
				View layout=mInflater.inflate(R.layout.category_game_center_tablet_content, null);
				GridCellLayout view=(GridCellLayout) layout.findViewById(R.id.list_game);
				view.setList(beans);
				view.setMenuId(mFocusUpId);
				if(view.load(id,TabletNormalHelper.this)){
					mHandler.obtainMessage(SHOW_CONTENT,id,id, layout).sendToTarget();
				}
			};
		}.start();
	}
	public static final int SHOW_LOAD=1;
	public static final int SHOW_CONTENT=2;
	Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SHOW_LOAD:
				mLoadView.setVisibility(View.VISIBLE);
				mResultView.setVisibility(View.GONE);
				mContentLayout.setVisibility(View.GONE);
				break;
			case SHOW_CONTENT:
				synchronized (TabletNormalHelper.this) {
					if(msg.arg1==currentId){
						mLoadView.setVisibility(View.GONE);
						mResultView.setVisibility(View.GONE);
						mContentLayout.setVisibility(View.VISIBLE);
						if(msg.obj instanceof View){
							View v=(View) msg.obj;
							mContentLayout.removeAllViews();
							mContentLayout.addView(v);
						}
					}
					
				}
				break;

			default:
				break;
			}
		};
	};
	private ArrayList<PathGameBean> getGameBeans(File file,String type){
		ArrayList<PathGameBean> beans=new ArrayList<PathGameBean>();
		if(file==null||!file.exists())return beans;
		File[] subFiles=file.listFiles();
		if(subFiles!=null){
			for(int i=0;i<subFiles.length;i++){
				PathGameBean bean=new PathGameBean(subFiles[i],type);
				if(bean.isValid()){
					beans.add(bean);
				}
			}
		}
		return beans;
	}
	
	public String getSubPath(int id){
		switch (id) {
		case R.id.tablet_game_arcade:
			return "roms/ARCADE";
		case R.id.tablet_game_gba:
			return "roms/GBA";
		case R.id.tablet_game_sfc:
			return "roms/SFC";
		case R.id.tablet_game_dc:
			return "roms/DC";
		case R.id.tablet_game_psp:
			return "roms/PSP";
		case R.id.tablet_game_ps1:
			return "roms/PS1";
		case R.id.tablet_game_md:
			return "roms/MD";
		case R.id.tablet_game_n64:
			return "roms/N64";
		default:
			return "roms/default";
		}
	}
	public String getExtendPath(int id){
		switch (id) {
		case R.id.tablet_game_arcade:
			return "roms/ARCADE/roms";
		case R.id.tablet_game_gba:
			return "roms/GBA/roms";
		case R.id.tablet_game_sfc:
			return "roms/SFC/roms";
		case R.id.tablet_game_dc:
			return "roms/DC/roms";
		case R.id.tablet_game_psp:
			return "roms/PSP/roms";
		case R.id.tablet_game_ps1:
			return "roms/PS1/roms";
		case R.id.tablet_game_md:
			return "roms/MD/roms";
		case R.id.tablet_game_n64:
			return "roms/N64/roms";
		default:
			return "roms/default/roms";
		}
	}
	public String getType(int id){
		switch (id) {
		case R.id.tablet_game_arcade:
			return C.DIR_ARCADE;
		case R.id.tablet_game_gba:
			return C.DIR_GBA;
		case R.id.tablet_game_sfc:
			return C.DIR_SNES;
		case R.id.tablet_game_dc:
			return C.DIR_DC;
		case R.id.tablet_game_psp:
			return C.DIR_PSP;
		case R.id.tablet_game_ps1:
			return C.DIR_PS1;
		case R.id.tablet_game_md:
			return C.DIR_MD;
		case R.id.tablet_game_n64:
			return C.DIR_N64;
		default:
			return C.DIR_N64;
		}
	}


}
