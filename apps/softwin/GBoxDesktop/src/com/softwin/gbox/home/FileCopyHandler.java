package com.softwin.gbox.home;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;

import com.xin.util.MD5Util2;
import com.xin.util.PathGame;
import com.xin.util.RunTimeTotal;
import com.xin.util.SimpleAppsModel;
import com.xin.util.XLog;

public class FileCopyHandler implements Runnable{
	public final String EXTERNAD_ANDROID_DATA = "/sdcard/Android";
	private SimpleAppsModel mApp;
	private PathGame mGame;
	private Context mContext;
	private Handler mHandler;
	private ProgressDialog mDialog;
	private Resources mRes;
	private long mTotal;
	private long mCurrentSize;

	public FileCopyHandler(Context context, SimpleAppsModel game) {
		this.mContext = context;
		mRes = context.getResources();
		this.mApp = game;
		this.mGame = game.game;
	}

	public void start() {
		mDialog = getProgressView();
		mHandler = new CopyHandler();
		XLog.i("build mDialog");
		new Thread(this).start();
	}

	public ProgressDialog getProgressView() {
		return ProgressDialog.show(mContext, "",
				mRes.getString(R.string.file_check));
	}

	public boolean checkDataExist() {
		File dataDir = new File(mGame.dataPath);
		File destDir = new File(EXTERNAD_ANDROID_DATA);
		return checkDirSame(dataDir, destDir);
	}

	private boolean checkDirSame(File sourceDir, File destDir) {
		if (sourceDir.exists() && destDir.exists()) {
			if (sourceDir.isDirectory()) {
				if (destDir.isDirectory()) {
					File[] files = sourceDir.listFiles();
					if (files == null) {
						return true;
					} else {
						for (File item : files) {
							File dest = new File(destDir, item.getName());
							if (!checkDirSame(item, dest)) {
								return false;
							}
						}
						return true;
					}
				} else {
					return false;
				}
			} else if (sourceDir.isFile()) {
				if (destDir.isFile()) {
					//return isSame(sourceDir,destDir);
					return sourceDir.length()==destDir.length();
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	public static boolean isSame(File sourceDir,File destDir){
		try {
			XLog.i("check md5");
			String md5a = MD5Util2.getFileMD5String(sourceDir);
			XLog.i("md5 path ="+sourceDir.getPath());
			XLog.i("md5 value="+md5a);
			String md5b = MD5Util2.getFileMD5String(destDir);
			XLog.i("md5 path ="+destDir.getPath());
			XLog.i("md5 value="+md5b);
			return md5a.equals(md5b);
		} catch (Exception e) {
			return false;
		}
	}

	public void copyAndroidData() {
		File sourceDir = new File(mGame.dataPath);
		File destDir = new File(EXTERNAD_ANDROID_DATA);
		mTotal = getDirLength(sourceDir);
		mCurrentSize = 0;
		copyDir(sourceDir,destDir);
	}
	public static long getDirLength(File file){
		if(file==null||!file.exists()){
			return 0l;
		}else if(file.isDirectory()){
			File[] childFiles=file.listFiles();
			if(childFiles==null){
				return 0l;
			}else{
				long len=0;
				for(File child:childFiles){
					len+=getDirLength(child);
				}
				return len;
			}
		}else if(file.isFile()){
			return file.length();
		}else{
			return 0l;
		}
	}
	
	public static String getPercentString(long progress,long total){
		float a=(float)progress/total;
		DecimalFormat decimalFormat=new DecimalFormat("#.0%");
		return decimalFormat.format(a);
	}

	
	public void copyDir(File srcFile, File destFile) {
		if (srcFile.exists()) {
			if (srcFile.isDirectory()) {
				if (!destFile.exists()) {
					XLog.d("create dir " + destFile);
					destFile.mkdirs();
					if (!destFile.exists())
						XLog.d("create dir fail!");

				}
				File[] files = srcFile.listFiles();
				if (files == null)
					return;
				for (File file : files) {
					File toFile = new File(destFile, file.getName());
					copyDir(file, toFile);
				}
			} else if (srcFile.isFile()) {
				if (!copyFile(srcFile, destFile)) {
					XLog.d("Copy " + srcFile.getPath() + " to "
							+ destFile.getPath() + " fail");
				}
			}
		}
	}

	public boolean copyFile(File srcFile, File destFile) {
		boolean result = false;
		try {
			InputStream in = new FileInputStream(srcFile);
			try {
				result = copyToFile(in, destFile);
			} finally {
				in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	public boolean copyToFile(InputStream inputStream, File destFile) {
		try {
			if (destFile.exists()) {
				destFile.delete();
			}
			FileOutputStream out = new FileOutputStream(destFile);
			try {
				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) >= 0) {
					mCurrentSize+=bytesRead;
					out.write(buffer, 0, bytesRead);
				}
			} finally {
				out.flush();
				try {
					out.getFD().sync();
				} catch (IOException e) {
				}
				out.close();
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	class CopyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				XLog.i("update progress");
				if(mDialog!=null){
					String size=Formatter.formatFileSize(mContext, mCurrentSize);
					String progress=getPercentString(mCurrentSize,mTotal);
					XLog.i("current="+mCurrentSize);
					XLog.i("  total="+ mTotal);
					String string=mRes.getString(R.string.file_copy_progress,size,progress);
					mDialog.setMessage(string);
					sendEmptyMessageDelayed(1, 500);
				}
				
				break;
			case 3:
				XLog.i("start copy");
				if(mDialog!=null){
					mDialog.setMessage(mContext.getString(R.string.file_copy_start));
				}
				break;
			case 4:
				XLog.i("close progress");
				if(mDialog!=null){
					XLog.i("----------------");
					XLog.i("current="+mCurrentSize);
					XLog.i("  total="+ mTotal);
					removeMessages(1);
					mDialog.cancel();
					mDialog=null;
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	}

	@Override
	public void run() {
		XLog.i("check at background thread");
		RunTimeTotal total = new RunTimeTotal("openApp");
		boolean result = checkDataExist();
		XLog.i("check finish,result="+result);
		total.next();
		if (result) {
			mHandler.sendEmptyMessage(4);
			mApp.startActivitySafely(mContext);
		} else {
			mHandler.sendEmptyMessage(3);
			mHandler.sendEmptyMessageDelayed(1, 500);
			copyAndroidData();
			mHandler.sendEmptyMessage(4);
			mApp.startActivitySafely(mContext);
		}
	}
}
