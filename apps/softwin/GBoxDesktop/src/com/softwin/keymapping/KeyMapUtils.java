package com.softwin.keymapping;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class KeyMapUtils {
	
	public static final String TAG = "keymap";
	public static final String ACTION_SERVICE = "softwin.intent.action.KEYMAP_SERVICE";

	public static final ComponentName startService(Context context) {
		Intent serviceIntent = new Intent(ACTION_SERVICE);
		serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		return context.startService(serviceIntent);
	}




}
