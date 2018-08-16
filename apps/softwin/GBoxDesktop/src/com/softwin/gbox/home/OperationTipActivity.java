package com.softwin.gbox.home;

import java.io.File;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.softwin.emulators2.C;
import com.softwin.emulators2.GameStartor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class OperationTipActivity extends Activity{
	public static boolean TIP_ENABLED=false;
	public static final String FIELD_FILE="file";
	public static final String FIELD_TYPE="type";
	private String filePath;
	private String type;
	private ShimmerTextView mContinueView;
	Shimmer shimmer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		filePath=getIntent().getStringExtra(FIELD_FILE);
		type=getIntent().getStringExtra(FIELD_TYPE);
		setContentView(R.layout.activity_operation_tip);
		findViewById(R.id.layout_tip_layout).setBackgroundResource(getBg());
		mContinueView=(ShimmerTextView) findViewById(R.id.layout_tip_continue);
		openAnimation(mContinueView);
	}
	public void openAnimation(ShimmerTextView target){
		if(shimmer==null){
			shimmer = new Shimmer();
            shimmer.setDuration(1500);
		}
		if(!shimmer.isAnimating()){
			shimmer.start(target);
		}
	}
    public void toggleAnimation(ShimmerTextView target) {
        if (shimmer != null && shimmer.isAnimating()) {
            shimmer.cancel();
        } else {
            shimmer = new Shimmer();
            shimmer.setDuration(1500);
            shimmer.start(target);
        }
    }
	private synchronized void exit(){
		try{
			GameStartor.launch(OperationTipActivity.this, new File(filePath), type);
			finish();
		}catch(Exception e){}
		
	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		exit();
		return true;
	}
	public boolean onGenericMotionEvent(MotionEvent event) {
		float rx = event.getAxisValue(MotionEvent.AXIS_Z) ;
		float ry = event.getAxisValue(MotionEvent.AXIS_RZ) ;
		if(rx!=0.0f||ry!=0.0f){
			exit();
		}
		return true;
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action=event.getAction();
		if(action==MotionEvent.ACTION_UP){
			exit();
		}else if(action==MotionEvent.ACTION_MOVE){
			exit();
		}else if(action==MotionEvent.ACTION_HOVER_MOVE){
			exit();
		}
		return true;
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		exit();
		return true;
	}
	public int getBg(){
		if(type!=null){
			if(type.equals(C.DIR_N64)){
				return R.drawable.start_tip_n64;
			}else if(type.equals(C.DIR_ARCADE)){
				return R.drawable.start_tip_arcade;
			}else if(type.equals(C.DIR_PSP)){
				return R.drawable.start_tip_psp;
			}
		}
		return R.drawable.start_tip_dc;
	}
	
	public static void startTip(Context context, File f, String type) {
		Intent intent=new Intent(Intent.ACTION_MAIN);
		intent.setClass(context, OperationTipActivity.class);
		intent.putExtra(FIELD_FILE, f.getPath());
		intent.putExtra(FIELD_TYPE, type);
		context.startActivity(intent);
	}
}
