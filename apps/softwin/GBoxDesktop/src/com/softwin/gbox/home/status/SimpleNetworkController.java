package com.softwin.gbox.home.status;

import java.util.List;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.View;
import android.widget.ImageView;

public class SimpleNetworkController extends BroadcastReceiver {
	public static final int WIFI_LEVEL_COUNT = 4;
    public static final String ETHERNET_STATE_CHANGED_ACTION = "android.net.ethernet.ETHERNET_STATE_CHANGED";
    public static final String EXTRA_ETHERNET_STATE = "ethernet_state";
	public static final int ETHER_STATE_DISCONNECTED=0;
	public static final int ETHER_STATE_CONNECTING=1;
	public static final int ETHER_STATE_CONNECTED=2;
	
    private Context mContext;
    private ImageView mIconView;

	private boolean mWifiEnabled;

	private boolean mWifiConnected;

	private int mWifiLevel;

	private int mWifiRssi;

	private WifiManager mWifiManager;

	private String mWifiSsid;
	private int enableId;
	private int disableId;
	
	private ImageView ethernetView;
	private int enableId2;
	private int disableId2;


    public SimpleNetworkController(Context context) {
        mContext = context;
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        filter.addAction(ETHERNET_STATE_CHANGED_ACTION);
        context.registerReceiver(this, filter);
    }
    public void destroy() {
		if(mContext!=null){
			mContext.unregisterReceiver(this);
		}
		mIconView=null;
		ethernetView=null;

	}
    public void setWifiView(ImageView v,int enableId,int disableId) {
        mIconView=v;
        this.enableId=enableId;
        this.disableId=disableId;
    }
    public void setEthernetView(ImageView v,int enableId,int disableId) {
    	ethernetView=v;
    	this.enableId2=enableId;
    	this.disableId2=disableId;
    }




    public void onReceive(Context context, Intent intent) {
    	final String action = intent.getAction();
    	if (action.equals(WifiManager.RSSI_CHANGED_ACTION)
                || action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)
                || action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            updateWifiState(intent);
            refreshViews();
        }else{
        	updateEthIcons(intent.getIntExtra(EXTRA_ETHERNET_STATE,0));
        }
    }
	private void updateEthIcons(int state) {
		if(ethernetView==null)return;
		ethernetView.setVisibility(View.VISIBLE);
		switch(state)
		{
			case ETHER_STATE_CONNECTED:
				ethernetView.setImageResource(enableId2);
				break;
			default:
				ethernetView.setImageResource(disableId2);
				break;
		}
    }

    private void refreshViews() {
		if(mIconView==null)return;
		if(mWifiEnabled){
			mIconView.setVisibility(View.VISIBLE);
			if (mWifiConnected) {
				mIconView.setImageResource(enableId);
			}else{
				mIconView.setImageResource(disableId);
			}
		}else{
			mIconView.setVisibility(View.VISIBLE);
			mIconView.setImageResource(disableId);
		}
		
		
	}

	private void updateWifiState(Intent intent) {
        final String action = intent.getAction();
        if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            mWifiEnabled = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN) == WifiManager.WIFI_STATE_ENABLED;

        } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            final NetworkInfo networkInfo = (NetworkInfo)
                    intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            boolean wasConnected = mWifiConnected;
            mWifiConnected = networkInfo != null && networkInfo.isConnected();
            // If we just connected, grab the inintial signal strength and ssid
            if (mWifiConnected && !wasConnected) {
                // try getting it out of the intent first
                WifiInfo info = (WifiInfo) intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                if (info == null) {
                    info = mWifiManager.getConnectionInfo();
                }
                if (info != null) {
                    mWifiSsid = huntForSsid(info);
                } else {
                    mWifiSsid = null;
                }
            } else if (!mWifiConnected) {
                mWifiSsid = null;
            }
        } else if (action.equals(WifiManager.RSSI_CHANGED_ACTION)) {
        	mWifiRssi = intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, -200);
        	mWifiLevel = WifiManager.calculateSignalLevel(mWifiRssi, WIFI_LEVEL_COUNT);
        }

    }
    private String huntForSsid(WifiInfo info) {
        String ssid = info.getSSID();
        if (ssid != null) {
            return ssid;
        }
        // OK, it's not in the connectionInfo; we have to go hunting for it
        List<WifiConfiguration> networks = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration net : networks) {
            if (net.networkId == info.getNetworkId()) {
                return net.SSID;
            }
        }
        return null;
    }
	 



}
