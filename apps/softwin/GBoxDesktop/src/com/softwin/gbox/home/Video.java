package com.softwin.gbox.home;

import java.io.File;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class Video extends Activity {
	private VideoView video1;
	MediaController mediaco;
	private static Toast toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IntentFilter filter=new IntentFilter(Intent.ACTION_SCREEN_OFF);
		setContentView(R.layout.activity_mp4);
		File file = findFile();
		if (file == null) {
			showToast(getApplicationContext(),"没有视频向导文件!");
			Video.this.finish();
			this.registerReceiver(mReceiver, filter);
			return;
		}
		video1 = (VideoView) findViewById(R.id.mp4_videoview);
		video1.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer arg0) {
				finish();
			}
		});
		mediaco = new MediaController(this);
		video1.setVideoPath(file.getPath());
		video1.setMediaController(mediaco);
		mediaco.setMediaPlayer(video1);
		video1.start();
		
		this.registerReceiver(mReceiver, filter);
	}
	
	BroadcastReceiver mReceiver=new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if(Intent.ACTION_SCREEN_OFF.equals(arg1.getAction())){
				Video.this.finish();
			}
		}
	};
	
	@Override
	protected void onDestroy() {
		this.unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	public File findFile() {
		String fileName = "softwin.mp4";
		File file = new File("/mnt/external_sd", fileName);
		if (file.exists() && file.canRead()) {
			return file;
		}
		file = new File("/sdcard", fileName);
		if (file.exists() && file.canRead()) {
			return file;
		}
		file = new File("/system/media", fileName);
		if (file.exists() && file.canRead()) {
			return file;
		}
		return null;
	}
	
    public static void showToast(Context context, 
            String content) {
            if (toast == null) {
                toast = Toast.makeText(context,
                             content, 
                             Toast.LENGTH_SHORT);
            } else {
                toast.setText(content);
            }
            toast.show();
        }
}
