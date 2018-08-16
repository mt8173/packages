package com.softwin.gbox.home.setting;

import java.io.File;

import com.softwin.gbox.home.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class GuideSetting extends Activity implements OnClickListener{
	private ImageButton imgBtnAniA;
	private ImageButton imgBtnAniB;
	private Button mButton;
	private boolean isGuide=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		isGuide=getIntent().getBooleanExtra("guide", isGuide);
		setContentView(R.layout.activity_guider);
		mButton=(Button) findViewById(R.id.btnSkip);
		if(isGuide){
			mButton.setText(R.string.start_skip);
		}else{
			mButton.setText(R.string.start_exit);
		}
		
		imgBtnAniA=(ImageButton) findViewById(R.id.imgBtnAniA);
		imgBtnAniB=(ImageButton) findViewById(R.id.imgBtnAniB);
		imgBtnAniA.setOnClickListener(this);
		imgBtnAniB.setOnClickListener(this);
		mButton.setOnClickListener(this);
		

	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgBtnAniA:
			openVideo("/system/media/mv.mp4");
			break;
		case R.id.imgBtnAniB:
			openVideo("/system/media/mv2.mp4");
			break;
		case R.id.btnSkip:
			finish();
			break;

		default:
			break;
		}
		
	}
	private void openVideo(String string) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(Intent.ACTION_VIEW);
		//intent.setClass(this, Video.class);
	    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    intent.putExtra("oneshot", 0);
	    intent.putExtra("configchange", 0);
	    Uri uri = Uri.fromFile(new File(string ));
	    intent.setDataAndType(uri, "video/*");
	    startActivity(intent);
	}
}
