package com.softwin.gbox.settings;

import com.xin.util.XLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;

public class BootBroadcastReceiver extends BroadcastReceiver {
	public static boolean runing=false;
	@Override
	public void onReceive(Context context, Intent intent) {
		XLog.i( "receiver broadcast------> "+intent.getAction()+",run="+runing);
		if(!runing){
			runing=true;
			String flag=SystemProperties.get("ro.xin.guide", "");
			if(flag==null||flag.equals("")){
				SystemProperties.get("ro.xin.guide", "run-one");
				XLog.i( "start guide ");
				String bluetoothFeature=SystemProperties.get("ro.softwin.feature.bluetooth","1");
				if("0".equals(bluetoothFeature)){
					startWifiSetting(context,true);
				}else{
					startBluetoothSetting(context,true);
				}
				
			}
		}
		
		
	}
	public static void startBluetoothSetting(Context context,boolean isGuide){
		Intent intent=new Intent();
		intent.setAction("xin.guide.bluetooth");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if(isGuide){
			intent.putExtra("guide", true);
		}
		context.startActivity(intent);
	}
	public static void startWifiSetting(Context context,boolean isGuide){
		Intent intent=new Intent();
		intent.setAction("xin.guide.wifi");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if(isGuide){
			intent.putExtra("guide", true);
		}
		context.startActivity(intent);
	}

}