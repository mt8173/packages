package com.softwin.gbox.home;

import java.util.ArrayList;

import com.xin.util.ImageHelper;
import com.xin.util.ReflectHelper;
import com.xin.util.SimpleAppsModel;
import com.xin.util.ViewPrint;
import com.xin.util.XLog;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewParent;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomShadowLayout extends FrameLayout implements OnFocusChangeListener,OnKeyListener{
	public static final boolean DEBUG_FOCUS = Launcher.DEBUG_FOCUS;
	private ImageView mOverlayView;
	private TextView mTitleView;
	private ImageView mIconView;
	private ImageView mShadowView;
	private FrameLayout mViewLayout;
	private int styleId=NO_ID;
	private int mFocusId=NO_ID;
	private int iconId=NO_ID;
	private int nameId=NO_ID;
	private boolean isShadow;
	private ReflectHelper mReflect;
	private int nextUpId ;
	private int nextDownId;
	private int nextRightId;
	private int nextLeftId;
	private String mPackageName;
	public CustomShadowLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttribute(context,attrs);
		initLayout();
	}

	public CustomShadowLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAttribute(context,attrs);
		initLayout();
	}
	public CustomShadowLayout(Context context) {
		super(context);
	}

	private void initAttribute(Context context, AttributeSet attrs) {
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CustomShadowLayout);
		styleId = array.getResourceId(R.styleable.CustomShadowLayout_style, NO_ID);
		iconId = array.getResourceId(R.styleable.CustomShadowLayout_icon, NO_ID);
		nameId = array.getResourceId(R.styleable.CustomShadowLayout_name, NO_ID);
		nextUpId = array.getResourceId(R.styleable.CustomShadowLayout_next_up_view, R.id.navigation_setting);
		nextDownId = array.getResourceId(R.styleable.CustomShadowLayout_next_down_view, View.NO_ID);
		nextLeftId = array.getResourceId(R.styleable.CustomShadowLayout_next_left_view, View.NO_ID);
		nextRightId = array.getResourceId(R.styleable.CustomShadowLayout_next_right_view, View.NO_ID);
		mPackageName = array.getString(R.styleable.CustomShadowLayout_packageName);
		isShadow = array.getBoolean(R.styleable.CustomShadowLayout_shadow, false);   
        array.recycle();
        XLog.i("packageName="+mPackageName +" | "+nextUpId+","+nextDownId+","+nextLeftId+","+nextRightId);
        XLog.i("           "+R.id.global_no_move);
	}
	private void initLayout() {
		if(isRectangle()){
			inflate(getContext(), R.layout.view_customshadow_rectangle, this);
		}else{
			inflate(getContext(), R.layout.view_customshadow_square, this);
		}
	}
	private boolean isRectangle() {
		// TODO Auto-generated method stub
		return styleId==R.drawable.cs_rect_red||styleId==R.drawable.cs_rect_yellow;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mOverlayView=(ImageView) findViewById(R.id.setting_item_overlay);
		mTitleView=(TextView) findViewById(R.id.setting_item_name);
		mIconView=(ImageView) findViewById(R.id.setting_item_icon);
		mShadowView=(ImageView) findViewById(R.id.setting_item_shadow);
		mViewLayout=(FrameLayout) findViewById(R.id.setting_item_layout);

		mViewLayout.setBackgroundResource(styleId);
		mViewLayout.setOnFocusChangeListener(this);
		mViewLayout.setOnKeyListener(this);
		mViewLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onEnter();
				
			}
		});
		if(nextDownId==R.id.global_no_move){
			mViewLayout.setNextFocusDownId(mViewLayout.getId());
		}else if(nextDownId!=View.NO_ID){
			mViewLayout.setNextFocusDownId(nextDownId);
		}
		
		if(nextRightId==R.id.global_no_move){
			XLog.i("nextDownId="+nextUpId+","+nextDownId+","+nextLeftId+","+nextRightId);
			mViewLayout.setNextFocusRightId(mViewLayout.getId());
		}else if(nextRightId!=View.NO_ID){
			mViewLayout.setNextFocusRightId(nextRightId);
		}
		
		if(nextLeftId==R.id.global_no_move){
			mViewLayout.setNextFocusLeftId(mViewLayout.getId());
		}else if(nextLeftId!=View.NO_ID){
			mViewLayout.setNextFocusLeftId(nextLeftId);
		} 
		if(mPackageName!=null){
			ArrayList<SimpleAppsModel> apps=SimpleAppsModel.loadLauncherByPackage(getContext(), mPackageName);
			if(apps!=null&&apps.size()>=1){
				SimpleAppsModel app=apps.get(0);
				mTitleView.setText(app.title);
				mIconView.setImageDrawable(app.icon);
			}
		}else{
			mTitleView.setText(nameId);
			mIconView.setBackgroundResource(iconId);
		}
		FocusHelper.add(mViewLayout);
		if(isShadow){
			mShadowView.setVisibility(View.VISIBLE);
			Bitmap bitmap=ImageHelper.drawableIdToBitmap(getResources(), styleId);
			bitmap=ImageHelper.createReflectionImageWithOrigin(bitmap, 100);
			mShadowView.setImageBitmap(bitmap);
		}
	}
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(DEBUG_FOCUS)XLog.i("CustomShadowLayout{"+ViewPrint.getViewClassAndId(v)+"}="+hasFocus);
		if(hasFocus){
			mOverlayView.setBackgroundResource(getOverlayId());
			mOverlayView.setVisibility(View.VISIBLE);
			mViewLayout.setBackground(null);
			ViewParent vp=getParent();
			if(vp!=null){
				vp.bringChildToFront(this);
			}
			RecordFocusView.self().setSelectView(nextUpId, this);
			startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.setting_item_big));
		}else{
			mOverlayView.setVisibility(View.INVISIBLE);
			mViewLayout.setBackgroundResource(styleId);
			startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.setting_item_small));
		}
	}
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	XLog.i("onKeyDown "+event);
    	if(keyCode == KeyEvent.KEYCODE_DPAD_UP){
    		checkFocus(this);
    	}
    	return super.onKeyDown(keyCode, event);
    }
    public void checkFocus(View layout){
    	View v=layout.focusSearch(FOCUS_UP);
		XLog.w("checkFocus "+ViewPrint.getViewClassAndId(v));
		if(v.getId()!=layout.getId()){
			if(v.getId()!=nextUpId){
				while(v.getParent()!=null){
    				v = (View) v.getParent();
    				if(v.getId()==android.R.id.content){
    					break;
    				}
    				XLog.d("parent="+ViewPrint.getViewClassAndId(v));
    			}
				v= v.findViewById(nextUpId);
				v.requestFocus();
    		}
		}
    }
    
	private int getOverlayId(){
		if(mFocusId==NO_ID){
			Resources res=getResources();
			String type=res.getResourceTypeName(styleId);
			String label=res.getResourceEntryName(styleId);
			mFocusId=res.getIdentifier(label+"_focus", type, getContext().getPackageName());
		}
		if(DEBUG_FOCUS)XLog.i("use overlay id="+mFocusId);
		return mFocusId;
	}
	
	public static class Item{
		public int id;
		public int focusId;

	}
	public void onEnter(){
		if(mPackageName!=null){
			ArrayList<SimpleAppsModel> apps=SimpleAppsModel.loadLauncherByPackage(getContext(), mPackageName);
			if(apps!=null&&apps.size()>=1){
				SimpleAppsModel app=apps.get(0);
				app.startActivity(getContext());
			}
			return;
		}
		if(mReflect==null){
			mReflect=new ReflectHelper();
			mReflect.setObject(getContext());
			mReflect.setMethod(null, "enterSetting", View.class);
			mReflect.setMethodArgs(this);
		}
		mReflect.callMethod();
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		XLog.i("onKey "+event);
		if(keyCode==KeyEvent.KEYCODE_DPAD_UP && event.getAction()==KeyEvent.ACTION_DOWN){
			checkFocus(v);
		}
		return false;
	}

}
