package com.softwin.gbox.settings.time;

import com.xin.util.XLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class BackgroundTimeUpdater extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
			// There is connectivity
			final ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			final NetworkInfo netInfo = connManager.getActiveNetworkInfo();
			if (netInfo != null) {
				// Verify that it's a WIFI connection
				if (netInfo.getState() == NetworkInfo.State.CONNECTED
						&& (netInfo.getType() == ConnectivityManager.TYPE_WIFI || netInfo.getType() == ConnectivityManager.TYPE_ETHERNET)) {
					XLog.i("auto update time at background...");
					SNTPGuide sntpGuide = new SNTPGuide(context, null);
					sntpGuide.request();
				}
			}
		}
	}

}
