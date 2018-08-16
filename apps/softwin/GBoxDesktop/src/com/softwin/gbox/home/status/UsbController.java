package com.softwin.gbox.home.status;

import java.io.File;
import java.util.ArrayList;

import com.xin.util.XLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class UsbController extends BroadcastReceiver {
	public static final boolean DEBUG = false;
	private final static String sUsbPath = "/mnt/usb_storage";
	private ArrayList<Callback> callbacks = new ArrayList<UsbController.Callback>();
	private Context mContext;
	private boolean isMounted=false;

	public UsbController(Context context) {
		mContext = context;
		IntentFilter filter = new IntentFilter();
		filter.addDataScheme("file");
		filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		filter.addAction(Intent.ACTION_MEDIA_EJECT);
		filter.addAction(Intent.ACTION_MEDIA_REMOVED);
		Intent intent=context.registerReceiver(this, filter);
		if(intent!=null)onReceive(context, intent);
	}



	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();
		String path = intent.getData().getPath();
		boolean oldMounted=isMounted;
		if(DEBUG)XLog.i("UsbController action=" + action + ",file=" + path);
		if(path==null)return;
		if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
			if(path.startsWith(sUsbPath))isMounted=true;
		} else {
			if(path.startsWith(sUsbPath)){
				File[] childUsbPaths=new File(sUsbPath).listFiles();
				if(childUsbPaths==null||childUsbPaths.length==0){
					isMounted=false;
				}else if(childUsbPaths.length==1){
					File oneChild=childUsbPaths[0];
					File mountChild=new File(path);
					isMounted=!oneChild.equals(mountChild);
				}else{
					isMounted=true;
				}
			}
		}
		if(oldMounted!=isMounted){
			refreshView();
		}
	}

	public void refreshView() {
		for (Callback callback : callbacks) {
			callback.onUsbStateChanged(isMounted);
		}
	}

	public void destroy() {
		if (mContext != null) {
			mContext.unregisterReceiver(this);
		}
	}

	public interface Callback {
		void onUsbStateChanged(boolean isMounted);
	}

	public void addCallback(Callback callback) {
		callbacks.add(callback);
		callback.onUsbStateChanged(isMounted);
	}

	public void removeCallback(Callback callback) {
		callbacks.remove(callback);
	}

}
