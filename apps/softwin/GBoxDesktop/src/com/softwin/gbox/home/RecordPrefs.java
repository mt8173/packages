package com.softwin.gbox.home;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

public class RecordPrefs {
	public final static String TEXT_SHOW="package-show";
	private Context mContext;
	private SharedPreferences mShowPrefs;
	private static RecordPrefs sKeyMapPrefs;
	public static RecordPrefs getSingleInstance(Context context){
		if(sKeyMapPrefs==null){
			sKeyMapPrefs=new RecordPrefs(context);
		}
		return sKeyMapPrefs;
	}
	private RecordPrefs(Context context){
		this.mContext=context;
		mShowPrefs=mContext.getSharedPreferences(TEXT_SHOW, Context.MODE_PRIVATE);
	}
	public RecordPrefs(Context context,String str){
		this.mContext=context;
		mShowPrefs=mContext.getSharedPreferences(TEXT_SHOW+"-"+str, Context.MODE_PRIVATE);
	}
	public synchronized boolean isExist(String package_){
		return mShowPrefs.contains(package_);
	}
	public synchronized void add(String package_){
		mShowPrefs.edit().putBoolean(package_, true).commit();
	}
	public synchronized void remove(String package_){
		mShowPrefs.edit().remove(package_).commit();
	}
	public synchronized ArrayList<String> getAllList(){
		Map<String,?> map= mShowPrefs.getAll();
		ArrayList<String> list=new ArrayList<String>();
		for(String key:map.keySet()){
			Object obj=map.get(key);
			if(obj instanceof Boolean){
				boolean b=(Boolean)obj;
				if(b)list.add(key);
			}
		}
		return list;
	}

	private synchronized String readString(String fileName){
		File fileDir=mContext.getFilesDir();
		if(!new File(fileDir,fileName).exists()){
			return null;
		}
		FileInputStream input=null;
		try {
			input = mContext.openFileInput(fileName);
			byte[] b = new byte[2048];
			int count=input.read(b);
			return new String(b, 0, count);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally{
			if(input!=null){
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	private synchronized void writeString(String fileName,String string){
		FileOutputStream fout=null;
        try {
            fout = mContext.openFileOutput(fileName, 0);
            byte[] bytes = string.getBytes();
            fout.write(bytes);
            fout.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
        	if(fout!=null){
        		try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
	}
}
