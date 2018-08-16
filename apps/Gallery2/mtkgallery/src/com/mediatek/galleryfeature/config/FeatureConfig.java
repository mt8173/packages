package com.mediatek.galleryfeature.config;

import android.content.Context;

import android.os.Environment;
import android.os.SystemProperties;

import com.mediatek.galleryframework.util.MtkLog;

import java.io.File;

/**
 * Get featureOption from FeatureConfig.
 */
public class FeatureConfig {
    private static final String TAG = "MtkGallery2/FeatureConfig";

    /// M: [FEATURE.ADD] <Global PQ > @{
    public static final String GLOBAL_PQ_PROPERTY = "ro.globalpq.support";
    /// @}
    public static final boolean IS_TABLET = SystemProperties.get("ro.build.characteristics")
            .equals("tablet");
    public static final boolean SUPPORT_PQ = (new File(Environment.getExternalStorageDirectory(),
            "SUPPORT_PQ")).exists();

    /// M: [FEATURE.ADD] <Global PQ > @{
    public static final boolean IS_GLOBALPQ_SUPPORT = SystemProperties.get(GLOBAL_PQ_PROPERTY)
            .equals("1");
    /// @}

    // Picture quality enhancement feature avails Camera ISP hardware
    // to improve image quality displayed on the screen.
    public static final boolean SUPPORT_PICTURE_QUALITY_ENHANCE = true;
}