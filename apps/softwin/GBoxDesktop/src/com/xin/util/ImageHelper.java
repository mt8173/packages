package com.xin.util;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;


public class ImageHelper {
	public static Bitmap drawableIdToBitmap(Resources res,int id){
		return ((BitmapDrawable) res.getDrawable(id)).getBitmap(); 
	}
	//倒影
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap,int destHeight,int offset) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);
		int y=height-destHeight-offset;
		if(y<=0){
			y=0;
			destHeight=height;
		}
		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, y, width, destHeight, matrix, false);
		Bitmap destBp = Bitmap.createBitmap(width, destHeight, Config.ARGB_8888);
		Canvas canvas = new Canvas(destBp);
		canvas.drawBitmap(reflectionImage, 0, 0, null);
		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, 0, 0, destBp.getHeight(), 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, 0, destBp.getWidth(), destBp.getHeight(), paint);
		return destBp;
	}
	//倒影
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap,int destHeight) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);
		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height-destHeight, width, destHeight, matrix, false);
		Bitmap destBp = Bitmap.createBitmap(width, destHeight, Config.ARGB_8888);
		Canvas canvas = new Canvas(destBp);
		canvas.drawBitmap(reflectionImage, 0, 0, null);
		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, 0, 0, destBp.getHeight(), 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, 0, destBp.getWidth(), destBp.getHeight(), paint);
		return destBp;
	}
	//单色
	public static Bitmap getExtractBitmap(Bitmap bm,Integer color){
		Paint p = new Paint();
		if(color!=null){
			p.setColor(color);
		}else{
			p.setColor(Color.CYAN);
		}
		Bitmap bitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Config.ARGB_8888);  
        Canvas canvas = new Canvas(bitmap);  
        canvas.drawBitmap(bm.extractAlpha(), 0, 0, p);  
        return bitmap;
	}
	public static Bitmap getLayoutDropShadow(Bitmap originalBitmap){
		XLog.i("getLayoutDropShadow(o):"+originalBitmap.getWidth()+"-"+originalBitmap.getHeight());
		BlurMaskFilter blurFilter = new BlurMaskFilter(15, BlurMaskFilter.Blur.OUTER);
		Paint shadowPaint = new Paint();
		shadowPaint.setColor(Color.BLACK);
		shadowPaint.setMaskFilter(blurFilter);
		int[] offsetXY =new int[2];
		Bitmap shadowBitmap = originalBitmap.extractAlpha(shadowPaint,offsetXY);
		Bitmap shadowImage32 = shadowBitmap.copy(Bitmap.Config.ARGB_8888, true);
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
			shadowImage32.setPremultiplied(true);
		}
		Canvas c = new Canvas(shadowImage32);
		c.drawBitmap(originalBitmap, -offsetXY[0],-offsetXY[1], null);
		XLog.i("getLayoutDropShadow(d):"+shadowBitmap.getWidth()+"-"+shadowBitmap.getHeight());
		c.setBitmap(null);
		originalBitmap.recycle();
		shadowBitmap.recycle();
		return shadowImage32;
	}
	public static Bitmap getMyAppShadow(Bitmap originalBitmap){
		//XLog.i("getLayoutDropShadow(o):"+originalBitmap.getWidth()+"-"+originalBitmap.getHeight());
		BlurMaskFilter blurFilter = new BlurMaskFilter(30, BlurMaskFilter.Blur.OUTER);
		Paint shadowPaint = new Paint();
		shadowPaint.setColor(Color.RED);
		shadowPaint.setMaskFilter(blurFilter);
		int[] offsetXY =new int[2];
		Bitmap shadowBitmap = originalBitmap.extractAlpha(shadowPaint,offsetXY);
		Bitmap shadowImage32 = shadowBitmap.copy(Bitmap.Config.ARGB_8888, true);
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
			shadowImage32.setPremultiplied(true);
		}
		Canvas c = new Canvas(shadowImage32);
		c.drawBitmap(originalBitmap, -offsetXY[0],-offsetXY[1], null);
		//XLog.i("getLayoutDropShadow(d):"+shadowBitmap.getWidth()+"-"+shadowBitmap.getHeight());
		c.setBitmap(null);
		originalBitmap.recycle();
		shadowBitmap.recycle();
		return shadowImage32;
	}

		 
}