package com.softwin.gbox.home;

import java.util.WeakHashMap;

import android.content.res.Resources;
import android.graphics.Bitmap;

import com.xin.util.ImageHelper;

public class CachedShadow {
	private static WeakHashMap<Integer, Bitmap> sBitmap =new WeakHashMap<Integer, Bitmap>();

	public static Bitmap getShadow(int drawableId,Resources res){
		Bitmap bp=sBitmap.get(drawableId);
		if(bp!=null)return bp;
		Bitmap newBitmap=ImageHelper.drawableIdToBitmap(res, drawableId);
		Bitmap bm=ImageHelper.createReflectionImageWithOrigin(newBitmap, StaticVar.self().mReflectHeight);
		sBitmap.put(drawableId, bm);
		return bm;
	}
}
