package com.mediatek.gallery3d.adapter;

import android.app.Activity;
import android.content.Context;

import com.android.gallery3d.app.GalleryAppImpl;
import com.mediatek.galleryframework.base.ThumbType;

import java.util.ArrayList;

public class PhotoPlayFacade {
    private final static String TAG = "MtkGallery2/PhotoPlayFacade";


    public static void initialize(GalleryAppImpl context, int microThumbnailSize,
            int thumbnailSize) {
        ThumbType.MICRO.setTargetSize(microThumbnailSize);
        ThumbType.MIDDLE.setTargetSize(thumbnailSize);
    }

    private PhotoPlayFacade() {
    }
}
