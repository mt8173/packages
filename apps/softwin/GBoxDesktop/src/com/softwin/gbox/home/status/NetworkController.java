package com.softwin.gbox.home.status;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.xin.util.XLog;

public class NetworkController extends BroadcastReceiver {
	public static final int WIFI_LEVEL_COUNT = 5;
	public static final boolean DEBUG = false;
	private String mContentDescriptionWifi;
	// wifi
	private final WifiManager mWifiManager;
	private boolean mWifiEnabled, mWifiConnected;
	private int mWifiRssi, mWifiLevel;
	private String mWifiSsid;
	// eth
	private boolean mEthConnected;
	private boolean mAirplaneMode = false;
	private Locale mLocale = null;
	// our ui
	private Context mContext;
	private ArrayList<NetworkSignalChangedCallback> mSignalsChangedCallbacks = new ArrayList<NetworkSignalChangedCallback>();
	private boolean mHasMobileDataFeature;
	boolean mDataAndWifiStacked = false;
	public static final String ETHERNET_STATE_CHANGED_ACTION = "android.net.ethernet.ETHERNET_STATE_CHANGED";
	public static final String EXTRA_ETHERNET_STATE = "ethernet_state";
	public static final int ETHER_STATE_DISCONNECTED = 0;
	public static final int ETHER_STATE_CONNECTING = 1;
	public static final int ETHER_STATE_CONNECTED = 2;


	public interface NetworkSignalChangedCallback {
		void onWifiSignalChanged(boolean enabled, int wifiLevel,
				String wifiSignalContentDescriptionId, String description);
		void onAirplaneModeChanged(boolean enabled);
		void onEthernetChanged(boolean enabled);
	}

	/**
	 * Construct this controller object and register for updates.
	 */
	public NetworkController(Context context) {
		mContext = context;
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		// broadcasts
		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
		filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		filter.addAction(ETHERNET_STATE_CHANGED_ACTION);
		context.registerReceiver(this, filter);

		updateAirplaneMode();
		refreshLocale();
	}




	public void addNetworkSignalChangedCallback(NetworkSignalChangedCallback cb) {
		mSignalsChangedCallbacks.add(cb);
		notifySignalsChangedCallbacks(cb);
	}
	public void removeNetworkSignalChangedCallback(NetworkSignalChangedCallback cb) {
		mSignalsChangedCallbacks.remove(cb);
	}


	void notifySignalsChangedCallbacks(NetworkSignalChangedCallback cb) {
		// only show wifi in the cluster if connected or if wifi-only
		boolean wifiEnabled = mWifiEnabled
				&& (mWifiConnected || !mHasMobileDataFeature);
		String wifiDesc = wifiEnabled ? mWifiSsid : null;
		cb.onWifiSignalChanged(wifiEnabled, mWifiLevel,
				mContentDescriptionWifi, wifiDesc);
		cb.onEthernetChanged(mEthConnected);
		cb.onAirplaneModeChanged(mAirplaneMode);
	}

	public void setStackedMode(boolean stacked) {
		mDataAndWifiStacked = true;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();
		if(DEBUG){
			XLog.i("NetworkController: action="+action);
		}
		if (action.equals(WifiManager.RSSI_CHANGED_ACTION)
				|| action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)
				|| action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
			updateWifiState(intent);
			refreshViews();
		} else if (action.equals(Intent.ACTION_CONFIGURATION_CHANGED)) {
			refreshLocale();
			refreshViews();
		} else if (action.equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)) {
			refreshLocale();
			updateAirplaneMode();
			refreshViews();
		} else if (action.equals(ETHERNET_STATE_CHANGED_ACTION)) {
			updateEthIcons(intent.getIntExtra(EXTRA_ETHERNET_STATE, 0));
			refreshViews();
		}else if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
			//updateWifiState(intent);
			//refreshViews();
		}
	}

	private void updateAirplaneMode() {
		mAirplaneMode = (Settings.Global.getInt(mContext.getContentResolver(),
				Settings.Global.AIRPLANE_MODE_ON, 0) == 1);
	}

	private void refreshLocale() {
		mLocale = mContext.getResources().getConfiguration().locale;
	}

	private void updateWifiState(Intent intent) {
		final String action = intent.getAction();
		if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
			mWifiEnabled = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
					WifiManager.WIFI_STATE_UNKNOWN) == WifiManager.WIFI_STATE_ENABLED;
			if(!mWifiEnabled)mWifiLevel = 0;
		} else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
			final NetworkInfo networkInfo = (NetworkInfo) intent
					.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			boolean wasConnected = mWifiConnected;
			mWifiConnected = networkInfo != null && networkInfo.isConnected();
			if(DEBUG)XLog.i("wasConnected="+wasConnected+",mWifiConnected="+mWifiConnected+",networkInfo = "+networkInfo);
			// If we just connected, grab the inintial signal strength and ssid
			if (mWifiConnected && !wasConnected) {
				// try getting it out of the intent first
				WifiInfo info = (WifiInfo) intent
						.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
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
				mWifiLevel = 0;
			}
		} else if (action.equals(WifiManager.RSSI_CHANGED_ACTION)) {
			mWifiRssi = intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, -200);
			mWifiLevel = WifiManager.calculateSignalLevel(mWifiRssi,
					WIFI_LEVEL_COUNT);
		}
	}

	private void updateEthIcons(int state) {
		if(DEBUG)XLog.i("NetworkController ethernet statue? "+state);
		switch (state) {
		case ETHER_STATE_CONNECTED:
			// mEthIconId = R.drawable.stat_sys_eth_connected;
			mEthConnected = true;
			break;
		default:
			mEthConnected = false;
			break;
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

	void refreshViews() {
		// update QS
		for (NetworkSignalChangedCallback cb : mSignalsChangedCallbacks) {
			notifySignalsChangedCallbacks(cb);
		}

	}


}
