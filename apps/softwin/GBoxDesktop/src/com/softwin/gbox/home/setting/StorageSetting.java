package com.softwin.gbox.home.setting;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.softwin.gbox.home.R;
import com.softwin.gbox.home.StaticVar;
import com.xin.util.XLog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class StorageSetting extends Activity{
	private FileSpace mFileSpace;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_storage);
		
		if(!StaticVar.self().mShowOperationTip){
			View v=findViewById(R.id.storage_tip);
			v.setVisibility(View.GONE);
		}
		String internalPath="/mnt/sdcard";
		String externalPath="/mnt/external_sd";
		StorageManager sm=(StorageManager) getSystemService(Context.STORAGE_SERVICE);
		StorageVolume[]  list=sm.getVolumeList();
		for(StorageVolume volume: list){
			if(volume.isPrimary()){
				internalPath=volume.getPath();
			}else if(volume.isRemovable()){
				externalPath=volume.getPath();
			}
		}
		Log.i("xin","inter="+internalPath+",exter="+externalPath);
		//TODO
		SeekBar seekBarSdcard =(SeekBar) findViewById(R.id.seekBarSdcard);
		seekBarSdcard.setEnabled(false);
		TextView tvAvaSdcardCount = (TextView) findViewById(R.id.tvAvaSdcardCount);
		TextView tvTotalSdcardCount = (TextView) findViewById(R.id.tvTotalSdcardCount);
		mFileSpace=new FileSpace();
		long sdFree=mFileSpace.getFree(internalPath);
		long sdTotal=mFileSpace.getTotal(internalPath);
		int sdTotalInt=(int)(sdTotal/1024);
		int sdUsedInt=(int) ((sdTotal-sdFree)/1024);
		seekBarSdcard.setMax(sdTotalInt);
		seekBarSdcard.setProgress(sdUsedInt);
		tvAvaSdcardCount.setText(formatSize(sdFree));
		if ((sdTotal > 3l*1024*1024*1024) && (sdTotal < 8l*1024*1024*1024))
			tvTotalSdcardCount.setText(formatSize(8l*1024*1024*1024));
		else if ((sdTotal > 8l*1024*1024*1024) && (sdTotal < 16l*1024*1024*1024))
			tvTotalSdcardCount.setText(formatSize(16l*1024*1024*1024));
		else if ((sdTotal > 16l*1024*1024*1024) && (sdTotal < 32l*1024*1024*1024))
			tvTotalSdcardCount.setText(formatSize(32l*1024*1024*1024));
		else if ((sdTotal > 32l*1024*1024*1024) && (sdTotal < 64l*1024*1024*1024))
			tvTotalSdcardCount.setText(formatSize(64l*1024*1024*1024));
		else if ((sdTotal > 64l*1024*1024*1024) && (sdTotal < 128l*1024*1024*1024))
			tvTotalSdcardCount.setText(formatSize(128l*1024*1024*1024));
		else
			tvTotalSdcardCount.setText(formatSize(sdTotal));
		
		//TODO
		SeekBar seekBarSystem =(SeekBar) findViewById(R.id.seekBarSystem);
		seekBarSystem.setEnabled(false);
		TextView tvTotalSystemCount=(TextView) findViewById(R.id.tvTotalSystemCount);
		TextView tvAvaSystemCount=(TextView) findViewById(R.id.tvAvaSystemCount);
		long tfFree=mFileSpace.getFree(externalPath);
		long tfTotal=mFileSpace.getTotal(externalPath);
		int tfTotalInt=(int)(tfTotal/1024);
		int tfUsedInt=(int) ((tfTotal-tfFree)/1024);
		tvAvaSystemCount.setText(formatSize(tfFree));
		tvTotalSystemCount.setText(formatSize(tfTotal));
		seekBarSystem.setMax(tfTotalInt);
		seekBarSystem.setProgress(tfUsedInt);
	}

    class FileSpace{
    	HashMap<String, Long> mTotals=new HashMap<String, Long>();
    	HashMap<String, Long> mFrees=new HashMap<String, Long>();
    	public FileSpace() {
			// TODO Auto-generated constructor stub
		}
    	public long getTotal() {
			long data=getTotal("/data");
			long cache=getTotal("/cache");
			long system=getTotal("/system");
			long total=data+cache+system;
			return total;
		}
		public long getTotal(String path){
    		if(mTotals.containsKey(path)){
    			return mTotals.get(path);
    		}else{
    			File file=new File(path);
    			long totalValue=0;
    			long freeValue=0;
    			if(file.exists()){
    				freeValue=file.getFreeSpace();
    				totalValue=file.getTotalSpace();
    			}
    			mFrees.put(path, freeValue);
    			mTotals.put(path, totalValue);
    			return totalValue;
    		}
    		
    	}
    	public long getFree(String path){
    		if(mTotals.containsKey(path)){
    			return mTotals.get(path);
    		}else{
    			File file=new File(path);
    			long totalValue=0;
    			long freeValue=0;
    			if(file.exists()){
    				freeValue=file.getFreeSpace();
    				totalValue=file.getTotalSpace();
    			}
    			mFrees.put(path, freeValue);
    			mTotals.put(path, totalValue);
    			return freeValue;
    		}
    		
    	}
    }
	void showLog(File file) {
		if(file.exists()){
			long avSize=file.getFreeSpace();
			long toSize=file.getTotalSpace();
			try {
				XLog.i(file.getCanonicalPath()+" free="+formatSize(avSize));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			XLog.i(file.getPath()+" total="+formatSize(toSize));
		}else{
			XLog.e(file.getAbsolutePath()+" is not exist!");
		}
		
	}
	private String formatSize(long size) {
        return Formatter.formatFileSize(this, size);
    }
}
