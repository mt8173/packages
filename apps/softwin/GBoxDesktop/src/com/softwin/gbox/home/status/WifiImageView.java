package com.softwin.gbox.home.status;

import com.softwin.gbox.home.R;
import com.softwin.gbox.home.status.NetworkController.NetworkSignalChangedCallback;
import com.xin.util.XLog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class WifiImageView extends ImageView implements NetworkSignalChangedCallback{
	private final static boolean DEBUG = false;
	public WifiImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public WifiImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WifiImageView(Context context) {
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
		if(DEBUG)XLog.i("Wifi enabled="+enabled+",wifiLevel="+wifiLevel+",des="+description);
		if(enabled){
			setVisibility(View.VISIBLE);
			switch (wifiLevel) {
			case 0:
				setImageResource(R.drawable.ic_qs_wifi_0);
				break;
			case 1:
				setImageResource(R.drawable.ic_qs_wifi_1);
				break;
			case 2:
				setImageResource(R.drawable.ic_qs_wifi_2);
				break;
			case 3:
				setImageResource(R.drawable.ic_qs_wifi_3);
				break;
			default:
				setImageResource(R.drawable.ic_qs_wifi_4);
				break;
			}
		}else{
			setVisibility(View.GONE);
		}
	}

	@Override
	public void onAirplaneModeChanged(boolean enabled) {
		if(DEBUG)XLog.i("Wifi airplane="+enabled);
		
	}

	@Override
	public void onEthernetChanged(boolean enabled) {
		// TODO Auto-generated method stub
		
	}

	
}
