package com.softwin.gamepad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
			String isXBox=MainActivity.getMode(context);
			MainActivity.write_mode(isXBox);
			boolean isExchange=MainActivity.getExchange(context);
			MainActivity.write_exchange(isExchange);
		}
	}

}
