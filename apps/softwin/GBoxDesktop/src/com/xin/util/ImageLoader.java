package com.xin.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.ImageView;

public class ImageLoader {


	public static byte[] bitmapToByteArray(Bitmap icon) {
		if (icon == null)
			return null;
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		// 将Bitmap压缩成PNG编码，质量为100%存储
		icon.compress(Bitmap.CompressFormat.PNG, 100, os);
		return os.toByteArray();
	}

	public static boolean setAssetImage(AssetManager assets, ImageView view,
			String addr) {
		InputStream input = null;
		try {
			input = assets.open(addr);
			Bitmap bm = BitmapFactory.decodeStream(input);
			view.setImageBitmap(bm);
			view.setTag(addr);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	//view to bitmap
	public static Bitmap convertViewToBitmap(View v, int bitmapWidth,
			int bitmapHeight) {
		if(bitmapHeight<=0||bitmapWidth<=0){
			bitmapWidth=v.getWidth();
			bitmapHeight=v.getHeight();
		}
		XLog.i("create bitmap w="+bitmapWidth+",h="+bitmapHeight);
		Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight,
				Bitmap.Config.ARGB_8888);
		v.draw(new Canvas(bitmap));
		return bitmap;
	}

	public static Bitmap viewToBitmap(View view) {
		if (view.getWidth() == 0 || view.getHeight() == 0) {
			XLog.i("view not show");
			view.measure(0, 0);
			view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		}
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bm= view.getDrawingCache();
		return bm;
	}

	public static Bitmap convertViewToBitmap(View view){
		XLog.i("convertViewToBitmap "+view);
			view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
	        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
	        view.buildDrawingCache();
	        Bitmap bitmap = view.getDrawingCache();
	        return bitmap;
	}

	//save bitmap
	public static void saveBitmapToSd(Bitmap mBitmap, int id){
		String fileName=id+"-"+System.currentTimeMillis()/1000;
		fileName=id+"_new";
		File file = new File("/mnt/sdcard/" + fileName+ ".p");
		saveBitmapTo(mBitmap,file);
	}
	public static void saveBitmapTo(Bitmap mBitmap, File file) {
		if (mBitmap == null)
			return;
		if (file == null) {
			String fileName=""+new Random().nextInt(1000);
			file = new File("/mnt/sdcard/" + fileName+ ".p");
		}
		try {
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
		} catch (IOException e) {
			XLog.e("在保存图片时出错：" + e.toString());
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//handle bitmap
	public static Bitmap clipCenterBitmap(Bitmap bitmap, int w, int h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int x=Math.abs((width-w)/2);
		int y=Math.abs((height-h)/2);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, x, y, w, h);
		bitmap.recycle();
		return newbmp;
	}
	
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidht = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidht, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		bitmap.recycle();
		return newbmp;
	}
	public static Bitmap zoomBitmapEquidistantly(Bitmap bitmap, int w, int h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) w / width);
		float scaleHeight = ((float) h / height);
		if(scaleWidth>1&&scaleWidth>scaleHeight||scaleWidth<1&&scaleWidth<scaleHeight){
			matrix.postScale(scaleWidth, scaleWidth);
		}else{
			matrix.postScale(scaleHeight, scaleHeight);
		}
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		return newbmp;
	}
}
