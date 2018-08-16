package com.android.settings.wifi;

import com.softwin.gbox.settings.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {
	private Button button;
	private boolean isGuide=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isGuide=getIntent().getBooleanExtra("guide", isGuide);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new WifiSettings()).commit();
		}
		button = (Button) findViewById(R.id.btnSkip);
		if(isGuide){
			button.setText(R.string.start_bluetooth_next_btn);
		}else{
			button.setText(R.string.start_bluetooth_exit_btn);
		}
		button.setOnClickListener(this);
	}
	@Override
	public void onClick(View arg0) {
		if(isGuide){
			startHome(true);
			finish();
		}else{
			finish();
		}
	}
	public void startHome(boolean isGuide){
		Intent intent=new Intent();
		intent.setAction("xin.guide.welcome");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if(isGuide){
			intent.putExtra("guide", true);
		}
		startActivity(intent);
	}




}
