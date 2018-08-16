package com.xin.util;

public class IntFlag {
	public static final int FIRST_FLAG = 0x00000000;
	public static final int FIRST_FLAG_NOT = 0x00000001;
	public static final int FIRST_FLAG_MASK = 0x00000001;
	private int mFlags;
	public void setInitFlags(int flags){
		this.mFlags=flags;
	}
	public int getCurrentFlags(){
		return this.mFlags;
	}
	//Changed
	public int setFlags(int flags,int mask){
		int old = mFlags;
		mFlags = (mFlags & ~mask)|(flags & mask);
		int changed= old ^ mFlags;
		return changed;
	}

	public int getFlags(int mask){
		return mFlags&mask;
	}
}
