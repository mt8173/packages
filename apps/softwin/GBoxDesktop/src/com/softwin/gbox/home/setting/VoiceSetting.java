package com.softwin.gbox.home.setting;

import com.softwin.gbox.home.R;
import com.softwin.gbox.home.StaticVar;
import com.xin.util.XLog;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class VoiceSetting extends Activity implements View.OnClickListener, OnSeekBarChangeListener{
	private AudioManager mAudioManager;
	private ImageButton mAdd;
	private ImageButton mSub;
	private SeekBar mSeekBar;
	private int progress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voice);
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);    
		int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);    
		progress = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		mSub=(ImageButton) findViewById(R.id.imgBtnVoiceDec);
		mAdd=(ImageButton) findViewById(R.id.imgBtnVoiceInc);
		mSeekBar = (SeekBar) findViewById(R.id.volum_seek);
		mSub.setOnClickListener(this);
		mAdd.setOnClickListener(this);
		mSeekBar.setMax(maxVolume);
		mSeekBar.setProgress(progress);
		mSeekBar.setOnSeekBarChangeListener(this);
		if(!StaticVar.self().mShowOperationTip){
			View v=findViewById(R.id.voice_tip);
			v.setVisibility(View.GONE);
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgBtnVoiceDec:
			updateVoice(false);
			break;
		case R.id.imgBtnVoiceInc:
			updateVoice(true);
			break;

		default:
			break;
		}
	}
	private void updateVoice(boolean isAdd){
		if(isAdd){
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE,    
	                AudioManager.FLAG_PLAY_SOUND); 
		}else{
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER,    
	                AudioManager.FLAG_PLAY_SOUND); 
		}
		refreshSeekBar();
	}
	private void refreshSeekBar(){
		progress = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		mSeekBar.setProgress(progress);
	}
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		XLog.i("set progress="+progress+",user="+fromUser);
		if(fromUser){
			this.progress=progress;
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_PLAY_SOUND);
		}
	}
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		
	}
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		XLog.i("onKeyDown "+keyCode);
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			updateVoice(false);
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			updateVoice(true);
			return true;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			refreshSeekBar();
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			refreshSeekBar();
			return true;
		default:
			break;
		}
		return super.onKeyUp(keyCode, event);
	}


}
