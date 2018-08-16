package com.softwin.gbox.settings.time;

import java.util.Date;
import java.util.TimeZone;

import com.xin.util.XLog;
import android.content.Context;
import android.content.Intent;
import android.net.SntpClient;
import android.os.SystemClock;
import android.os.UserHandle;

public class SNTPGuide {
	protected final static String[] HostArray={
		"north-america.pool.ntp.org",
		"pool.ntp.org",
		"0.pool.ntp.org",
		"1.pool.ntp.org",
		"2.pool.ntp.org",
		"3.pool.ntp.org",
		"0.europe.pool.ntp.org",
		"0.asia.pool.ntp.org",
		"0.north-america.pool.ntp.org",
		"fr.pool.ntp.org"
	};
	private Context mContext;
	private SntpClient mClient;
	private TimeUpdateListener mListener;
	public SNTPGuide(Context context,TimeUpdateListener listener) {
		super();
		this.mContext = context;
		this.mListener = listener;
		this.mClient = new SntpClient();
	}
	public void request(){
		Runnable run = new Runnable() {
			@Override
			public void run() {
				requestSntpArray();
			}
		};
		new Thread(run).start();
	}
	private void requestSntpArray() {
		XLog.i("Reqest sntp array!!!");
        int tryCount = HostArray.length;
        for(int i = 0; i < tryCount; i++){
            boolean result=requestSntp(HostArray[i]);
            if(result){
            	return;
            }
        }
        if(mListener!=null){
        	mListener.onFail();
        }
    }
	private boolean requestSntp(String host){
		XLog.i("Request host:"+host);
		if(mClient.requestTime(host, 10000)) {
            long cachedNtp = mClient.getNtpTime();
            long cachedNtpTimestamp = SystemClock.elapsedRealtime();
            XLog.i("Success,sntp NtpTime = " + cachedNtp);
            setAndBroadcastNetworkSetTime(cachedNtp
               + (cachedNtpTimestamp - mClient.getNtpTimeReference()));
            return true;
        }else{
        	XLog.i("Read time fail,"+host);
        	return false;
        }
	}
    public static final String ACTION_NETWORK_SET_TIME = "android.intent.action.NETWORK_SET_TIME";
    private void setAndBroadcastNetworkSetTime(long time0) {
        XLog.i("setAndBroadcastNetworkSetTime: time=" + time0 + "ms");
        //TimeZone currentTimeZone=TimeZone.getTimeZone(arg0);
        long time =time0;
        Date date = new Date(time);
        String timeText = date.toLocaleString();
        XLog.i("print:"+timeText);
        SystemClock.setCurrentTimeMillis(time);
        if(mListener!=null){
        	mListener.onSuccess(time);
        }
        
        Intent intent = new Intent(ACTION_NETWORK_SET_TIME);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        intent.putExtra("time", time);
        mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
        
    }

}
