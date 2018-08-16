package com.softwin.gbox.settings.time;

import com.softwin.gbox.settings.R;
import com.xin.util.XLog;

import android.app.Activity;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.TextView;

public class TimeSettings extends Activity implements TimeUpdateListener{
	private ProgressBar mProgressBar;
	private TextClock mTextClock;
	private TextView mShowState;
	private Button mBackButton;
	private int result;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_time);
		mProgressBar = (ProgressBar) findViewById(R.id.time_setting_grogress);
		mTextClock = (TextClock) findViewById(R.id.time_setting_result);
		mShowState = (TextView) findViewById(R.id.time_setting_state);
		mBackButton = (Button) findViewById(R.id.time_setting_btn);
		updateTime();
	}
	public void updateTime(){
		handler.sendEmptyMessage(1);
		SNTPGuide test=new SNTPGuide(this, this);
		test.request();
	}
	Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			result=msg.what;
			Drawable drawable=mProgressBar.getIndeterminateDrawable();
			Animatable an=null;
			if(drawable instanceof Animatable){
				an=(Animatable) drawable;
			}
			switch (msg.what) {
			case 1:
				if(an!=null&&!an.isRunning()){
					an.start();
				}
				mShowState.setVisibility(View.VISIBLE);
				mShowState.setText(R.string.time_auto_check);
				mTextClock.setVisibility(View.GONE);
				mBackButton.setText(R.string.start_bluetooth_exit_btn);
				break;
			case 2:
				if(an!=null&&an.isRunning()){
					an.stop();
				}
				mShowState.setVisibility(View.GONE);
				mTextClock.setVisibility(View.VISIBLE);
				mBackButton.setText(R.string.start_bluetooth_exit_btn);
				break;
			case 3:
				if(an!=null&&an.isRunning()){
					an.stop();
				}
				mShowState.setVisibility(View.VISIBLE);
				mShowState.setText(R.string.time_update_fail);
				mTextClock.setVisibility(View.GONE);
				mBackButton.setText(R.string.time_retry);
				break;

			default:
				break;
			}
		};
	};

	@Override
	public void onSuccess(long time) {
		handler.sendEmptyMessage(2);
		
	}

	@Override
	public void onFail() {
		handler.sendEmptyMessage(3);
		
	}
	
	public void onCancal(View view){
		if(result==3){
			updateTime();
		}else{
			finish();
		}
		
	}
}
