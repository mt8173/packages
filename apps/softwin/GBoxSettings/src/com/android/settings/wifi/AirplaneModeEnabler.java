package com.android.settings.wifi;

import android.content.Context;
import android.provider.Settings;

public class AirplaneModeEnabler {


    public static boolean isAirplaneModeOn(Context context) {
        return Settings.Global.getInt(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }


}
