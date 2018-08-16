package com.softwin.gbox.settings.time;

public interface TimeUpdateListener {
	void onSuccess(long time);
	void onFail();
}
