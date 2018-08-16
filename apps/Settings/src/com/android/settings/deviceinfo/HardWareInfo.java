/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.deviceinfo;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserHandle;
import android.support.v7.preference.Preference;
import java.util.ArrayList;
import java.util.HashMap;
import com.android.settings.R;
import com.android.settings.Utils;
import android.os.SystemProperties;
import android.util.Log;
import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.settings.SettingsPreferenceFragment;
import android.support.v7.preference.PreferenceCategory;
public class HardWareInfo extends SettingsPreferenceFragment {

    private static final String KEY_CPU_INFO="cpu_info";
    private static final String KEY_CPU_MODEL="cpu_model";
    private static final String KEY_CPU_CORES="cpu_cores";

    private static final String KEY_STORAGE_INFO="wisky_storage_info";
    private static final String KEY_INTERNAL_STORAGE="wisky_internal_storage";
    private static final String KEY_INTERNAL_ROM="wisky_internal_rom";

    private static final String KEY_CAMERA_INFO="camera_info";
    private static final String KEY_CAMERA_INFO_BACK="camera_info_back";
    private static final String KEY_CAMERA_INFO_FRONT="camera_info_front";


    private Preference mCpuModel;
    private Preference mCpuCores;

    private Preference mInternalStorage;
    private Preference mInternalRom;

    private PreferenceCategory mCameraInfo;
    private Preference mCameraInfoBack;
    private Preference mCamerInfoFront;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.device_info_hardware);
        initView();
        initData(getContext());
    }
    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.DEVICEINFO_STATUS;
    }
    private void initView()
    {
        mCpuModel = findPreference(KEY_CPU_MODEL);
        mCpuCores = findPreference(KEY_CPU_CORES);

        mInternalStorage = findPreference(KEY_INTERNAL_STORAGE);
        mInternalRom = findPreference(KEY_INTERNAL_ROM);

        mCameraInfo = (PreferenceCategory)findPreference(KEY_CAMERA_INFO);
        mCameraInfoBack = findPreference(KEY_CAMERA_INFO_BACK);
        mCamerInfoFront = findPreference(KEY_CAMERA_INFO_FRONT);
    }

    private void initData(Context context)
    {
        ArrayList<HashMap<String, String>> cpuInfo = (ArrayList<HashMap<String, String>>)HardWareInfoHelp.getCpuInfo();
        mCpuModel.setSummary(cpuInfo.get(0).get("model name"));
        mCpuCores.setSummary(cpuInfo.get(1).get("cpu cores"));

        ArrayList<String> storageInfo = (ArrayList<String>) HardWareInfoHelp.getStorageInfo(context);
        mInternalStorage.setSummary(storageInfo.get(0));
        mInternalRom.setSummary(storageInfo.get(1));

        ArrayList<String> cameraInfo = (ArrayList<String>) HardWareInfoHelp.getCameraInfo(context);
		if(cameraInfo != null){
			Log.i("HardWareInfo","cameraInfo.size()" + cameraInfo.size());
			if(cameraInfo.size() > 1){
				mCameraInfoBack.setSummary(cameraInfo.get(0));
				mCamerInfoFront.setSummary(cameraInfo.get(1));
			}
		}else{
			removePreference(KEY_CAMERA_INFO);
		}
    }
}
