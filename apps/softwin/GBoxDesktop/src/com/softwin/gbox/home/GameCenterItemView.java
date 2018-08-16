package com.softwin.gbox.home;

import java.io.File;

import com.xin.util.BaseHandler;
import com.xin.util.ImageHelper;
import com.xin.util.ImageLoader;
import com.xin.util.ViewPrint;
import com.xin.util.XLog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.ViewParent;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class GameCenterItemView extends FrameLayout implements
		OnFocusChangeListener, View.OnClickListener, View.OnLongClickListener {
	private View mBgView;
	private TextView mTitleView;
	private ImageView mIconView;
	private PathGameBean mGame;
	private ImageView mShadowView;
	private GameCenterContainer.ChildItemState state= GameCenterContainer.ChildItemState.NOT_LOAD;
	public GameCenterItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttribute(context, attrs);
		initLayout();
	}

	public GameCenterItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAttribute(context, attrs);
		initLayout();
	}

	public GameCenterItemView(Context context) {
		super(context);
		initLayout();
	}

	private void initAttribute(Context context, AttributeSet attrs) {

	}

	private void initLayout() {
		inflate(getContext(), R.layout.view_gc, this);
		setFocusable(true);
		setFocusableInTouchMode(true);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mBgView = findViewById(R.id.item_gc_group);
		mTitleView = (TextView) findViewById(R.id.item_gc_text);
		mIconView = (ImageView) findViewById(R.id.item_gc_image);
		mShadowView = (ImageView) findViewById(R.id.item_gc_shadow);
		setOnFocusChangeListener(this);
		setOnClickListener(this);
		setOnLongClickListener(this);
		FocusHelper.add(this);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			ViewParent vp = getParent();
			if (vp != null) {
				vp.bringChildToFront(this);
			}
			mIconView
					.setBackgroundResource(R.drawable.launcher_game_detail_logo_focus);
			startAnimation(AnimationUtils.loadAnimation(getContext(),
					R.anim.setting_item_big));
			//printChildIndex();
		} else {
			mIconView.setBackground(null);
			startAnimation(AnimationUtils.loadAnimation(getContext(),
					R.anim.setting_item_small));
		}
	}
	void printChildIndex(){
		ViewGroup layout=(ViewGroup)getParent();
		int count=layout.getChildCount();
		XLog.i("print child index,count="+count);
		for(int i=0;i<count;i++){
			View v=layout.getChildAt(i);
			if(v instanceof GameCenterItemView){
				GameCenterItemView av=(GameCenterItemView) v;
				XLog.i("index="+i+",tag="+av.mGame.label);
			}
			
		}
		XLog.i("--end");
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		XLog.i("GameCenterItemView.onKeyDown "+KeyEvent.keyCodeToString(keyCode));
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
		XLog.i("GameCenterItemView.onOverScrolled scrollX="+scrollX);
		super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		XLog.i("GameCenterItemView.onKeyUp "+KeyEvent.keyCodeToString(keyCode));
		// TODO Auto-generated method stub
		return super.onKeyUp(keyCode, event);
	}

    @Override
    public View focusSearch(int direction) {
    	XLog.v("GameCenterItemView.focusSearch "+direction);
    	View v = super.focusSearch(direction);
    	if(StaticVar.self().isSingleEnumlator){
    		return v;
    	}
    	if(direction==FOCUS_UP){
    		XLog.v("FOCUS_UP "+ViewPrint.getViewClassAndId(v));
    		if(!(v instanceof GameCenterItemView)){
    			if(v==null){
    				v=this;
    			}else if(v.getId()!=R.id.navigation_gamecenter){
    				while(v.getParent()!=null){
        				v = (View) v.getParent();
        				if(v.getId()==android.R.id.content){
        					break;
        				}
        				XLog.d("parent="+ViewPrint.getViewClassAndId(v));
        			}
    				v= v.findViewById(R.id.navigation_gamecenter);
        		}
    		}
    	}else if(direction == FOCUS_DOWN){
    		if(v!=null&& (v instanceof GameCenterItemView)){
    			//pass
    		}else{
    			XLog.v("FOCUS_DOWN "+ViewPrint.getViewClassAndId(v));
    			v=getContainer(this);
    			v=getSelectMenuView(v);
    		}
    	}else if(direction == FOCUS_LEFT){
    		XLog.i("FOCUS_LEFT:"+v);
    		if(v==null){
    			v=this;
    		}else if(v instanceof GameCenterItemView){
    			//pass
    		}else{
    			v=this;
    		}
    	}else if(direction == FOCUS_RIGHT){
    		XLog.i("FOCUS_RIGHT:"+v);
    		if(v==null){
    			v=this;
    		}else if(v instanceof GameCenterItemView){
    			//pass
    		}else{
    			v=this;
    		}
    	}
    	return v;
    }
    public View getContainer(View v){
    	while(v!=null){
    		if(v instanceof View){
    			v = (View) v.getParent();
    			if(v.getId()==R.id.category_game_center){
    				return v;
    			}
    		}else{
    			return null;
    		}
		}
    	return null;
    }
    public View getSelectMenuView(View container){
    	if(container==null)return null;
    	RadioGroup rg=(RadioGroup) container.findViewById(R.id.nav_game_center);
    	int id=rg.getCheckedRadioButtonId();
    	if(id==NO_ID){
    		id=R.id.gc_nav_arcade;
    	}
    	return rg.findViewById(id);
    }
     
	public void setText(String title) {
		if (mTitleView != null) {
			mTitleView.setText(title);
		}
	}

	public void setIcon(Drawable drawable) {
		if (mBgView != null) {
			mBgView.setBackground(drawable);
		}
	}

	public void setTag(PathGameBean tag) {
		this.mGame = tag;
		if (mTitleView != null) {
			mTitleView.setText(tag.label);
		}
		
	}
	public void loadImageBeforeAddView(){
		if(state==GameCenterContainer.ChildItemState.NOT_LOAD){
			state=GameCenterContainer.ChildItemState.LOADING;
			synchronized (this) {
				if (mBgView != null) {
					Bitmap b = mGame.getBg(getContext());
					if(b!=null){
						mBgView.setBackground(new BitmapDrawable(b));
					}else{
						mBgView.setBackgroundResource(R.drawable.common_bg);
					}
				}
				if(mShadowView.getVisibility()==View.VISIBLE){
					new ReflectionImageLoader().execute();
				}
				state=GameCenterContainer.ChildItemState.LOADED;
			}
		}
	}
	public void loadImageAsyc(BaseHandler handler){
		if(state==GameCenterContainer.ChildItemState.NOT_LOAD){
			state=GameCenterContainer.ChildItemState.LOADING;
			synchronized (this) {
				Bitmap b = mGame.getBg(getContext());
				if (mBgView != null) {
					if(b!=null){
						handler.setBackground(mBgView, new BitmapDrawable(b));
						if(isFocused()){
							handler.post(new Runnable() {
								public void run() {
									invalidate();
								}
							});
						}
					}else{
					}
				}
				if(mShadowView.getVisibility()==View.VISIBLE){
					if(b!=null){
						Bitmap bitmap=ImageHelper.createReflectionImageWithOrigin(b, 100,35);
						handler.setImage(mShadowView, bitmap);
					}
				}
				state=GameCenterContainer.ChildItemState.LOADED;
			}
		}
	}

	public void setLayoutParams(GameCenterPage.LayoutParams params) {
		super.setLayoutParams(params);
		if(params.isShadow){
			LinearLayout.LayoutParams lps=(android.widget.LinearLayout.LayoutParams) mShadowView.getLayoutParams();
			if(lps!=null){
				lps.height=params.getShadowHeight();
				mShadowView.setLayoutParams(lps);
			}
			mShadowView.setVisibility(View.VISIBLE);
		}else{
			mShadowView.setVisibility(View.GONE);
		}
	}
	public String getName(){
		return mGame.label;
	}

	@Override
	public void onClick(View v) {
		if (mGame != null) {
			try {
				mGame.startGame(getContext());
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getContext(), "game is not exist!",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	@Override
	public boolean onLongClick(View v) {
		return true;
	}



	public class ReflectionImageLoader extends AsyncTask<Object, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Object... params) {
			Bitmap bt = convertViewToBitmap2(mBgView);
			if (bt != null) {
				return ImageHelper.createReflectionImageWithOrigin(bt, 100,35);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result == null){
				return;
			}else{
				mShadowView.setImageBitmap(result);
			}
		}
	}

	public Bitmap convertViewToBitmap(View view) {
		GameCenterPage.LayoutParams lp = (GameCenterPage.LayoutParams) getLayoutParams();
		view.measure(
				MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(lp.height - lp.getShadowHeight(),
						MeasureSpec.EXACTLY));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		return bitmap;
	}

	public Bitmap convertViewToBitmap2(View view) {
		Drawable d = view.getBackground();
		if (d instanceof BitmapDrawable) {
			return ((BitmapDrawable) d).getBitmap();
		} else {
			//return ImageUtil.drawableToBitmap(d);
			return null;
			
		}

	}

	public Bitmap convertViewToBitmap3(View view) {
		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
				Bitmap.Config.ARGB_8888);
		// 利用bitmap生成画布
		Canvas canvas = new Canvas(bitmap);
		// 把view中的内容绘制在画布上
		view.draw(canvas);

		return bitmap;
	}

	/**
	 * save view as a bitmap
	 */
	private Bitmap saveViewBitmap(View view) {
		// get current view bitmap
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache(true);
		Bitmap bitmap = view.getDrawingCache(true);

		Bitmap bmp = duplicateBitmap(bitmap);
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
		// clear the cache
		view.setDrawingCacheEnabled(false);
		return bmp;
	}

	public static Bitmap duplicateBitmap(Bitmap bmpSrc) {
		if (null == bmpSrc) {
			return null;
		}

		int bmpSrcWidth = bmpSrc.getWidth();
		int bmpSrcHeight = bmpSrc.getHeight();

		Bitmap bmpDest = Bitmap.createBitmap(bmpSrcWidth, bmpSrcHeight,
				Config.ARGB_8888);
		if (null != bmpDest) {
			Canvas canvas = new Canvas(bmpDest);
			final Rect rect = new Rect(0, 0, bmpSrcWidth, bmpSrcHeight);

			canvas.drawBitmap(bmpSrc, rect, rect, null);
		}

		return bmpDest;
	}

	private void saveImageToSD(Bitmap zoomBitmap, String name) {
		File sd = Environment.getExternalStorageDirectory();
		File flagFile = new File(sd, "png_out");
		flagFile.mkdirs();
		if (flagFile.exists()) {
			File destFile = new File(flagFile, name + ".p");
			XLog.w("output png: " + destFile);
			ImageLoader.saveBitmapTo(zoomBitmap, destFile);
		}

	}

}
