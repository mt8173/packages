package com.softwin.keymapping;

import android.content.Context;
import android.content.Intent;

public class GuideUtils {
	public static final String ACTION_SERVICE = "xin.guide.start";
	public static final String ACTION_CONNECT_PC = "xin.connect.pc";

	
	public static final void start(Context context) {
		Intent intent = new Intent(ACTION_SERVICE);
		context.startService(intent);
	}

	
	public static final void connectPc(Context context) {
		Intent intent = new Intent(ACTION_CONNECT_PC);
		context.sendBroadcast(intent);
	}
	
}
