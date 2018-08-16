package com.softwin.gbox.home.setting;

import com.xin.util.IntFlag;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

public class ParentLock extends IntFlag{
	private static final String PASSWORD_VALUE="softwin.gbox.password.value";
	private static final String PASSWORD_FLAG="softwin.gbox.password.flag";
	public static final int MASK_NO_PASSWORD=-1;

	private ContentResolver mContentResolver;
	public ParentLock(Context context){
		mContentResolver=context.getContentResolver();
	}
	public String getPassword(){
		return Settings.System.getString(mContentResolver, PASSWORD_VALUE);
	}
	public void updatePassword(String text){
		if(text!=null){
			Settings.System.putString(mContentResolver, PASSWORD_VALUE, text);
		}
	}
	public int getPasswordFlag(){
		return Settings.System.getInt(mContentResolver, PASSWORD_FLAG,0);
	}
	public void setPasswordFlag(int value){
		Settings.System.putInt(mContentResolver, PASSWORD_FLAG,value);
	}
	public boolean isLock(int mask){
		int storeFlag=getPasswordFlag();
		//XLog.i("get lock("+Integer.toHexString(storeFlag));
		setInitFlags(storeFlag); 
		return getFlags(mask)==mask;
	}
	/**
	 * 
	 * @param value: 0 or mask
	 * @param mask
	 * @return
	 */
	public void updateLock(int value,int mask){
		int storeFlag=getPasswordFlag();
		//XLog.i("update lock from "+Integer.toHexString(storeFlag)+" to "+Integer.toHexString(value));
		setInitFlags(storeFlag);
		if(setFlags(value, mask)!=0){
			setPasswordFlag(getCurrentFlags());
		}
	}
	
	public static final int getMask(int bit){
		if(bit>=0){
			return 1<<bit;
		}else{
			return MASK_NO_PASSWORD;
		}
		
	}

}
