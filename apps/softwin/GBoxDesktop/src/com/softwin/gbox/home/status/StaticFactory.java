package com.softwin.gbox.home.status;

import android.content.Context;

public class StaticFactory {
	//static network controller
	private static NetworkController mNetworkController;
	public static NetworkController getNetworkController(Context context){
		if(mNetworkController == null){
			mNetworkController = new NetworkController(context.getApplicationContext());
		}
		return mNetworkController;
	}
	
	//static usb controller
	private static UsbController mUsbController;
	public static UsbController getUsbController(Context context){
		if(mUsbController == null){
			mUsbController = new UsbController(context.getApplicationContext());
		}
		return mUsbController;
	}
}
