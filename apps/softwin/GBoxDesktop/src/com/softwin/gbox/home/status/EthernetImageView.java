package com.softwin.gbox.home.status;

import com.softwin.gbox.home.R;
import com.softwin.gbox.home.status.NetworkController.NetworkSignalChangedCallback;
import com.xin.util.XLog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class EthernetImageView extends ImageView implements NetworkSignalChangedCallback{
	private final static boolean DEBUG = false;
	public EthernetImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public EthernetImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EthernetImageView(Context context) {
		super(context);
	}
	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		StaticFactory.getNetworkController(getContext()).addNetworkSignalChangedCallback(this);;
	}
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		StaticFactory.getNetworkController(getContext()).removeNetworkSignalChangedCallback(this);
	}

	@Override
	public void onWifiSignalChanged(boolean enabled, int wifiLevel,
			String wifiSignalContentDescriptionId, String description) {
	}

	@Override
	public void onAirplaneModeChanged(boolean enabled) {

		
	}

	@Override
	public void onEthernetChanged(boolean enabled) {
		if(DEBUG)XLog.i("Ethernet enabled="+enabled);
		if(enabled){
			setVisibility(View.VISIBLE);
			setImageResource(R.drawable.ic_qs_ethernet);
		}else{
			setVisibility(View.GONE);
		}
		
	}

	
}
