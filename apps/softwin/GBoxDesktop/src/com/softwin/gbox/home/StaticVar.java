package com.softwin.gbox.home;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

public class StaticVar {
	private static StaticVar instance = null;

	private StaticVar() {
	}
	public static void init(Context context){
		instance = new StaticVar();
		String zh=context.getResources().getConfiguration().locale.getLanguage();
		if(zh!=null&&!zh.equals("zh")){
			if(TYPE_TABLET_XD==instance.mType){
				instance.mType=TYPE_TABLET_XD_EN;
			}else if(TYPE_TABLET_Q9==instance.mType){
				instance.mType=TYPE_TABLET_Q9_EN;
			}
		}
		instance.loadConfig();
	}
	public static StaticVar self() {
		if (instance == null) {
			instance = new StaticVar();
			instance.loadConfig();
		}
		return instance;
	}

	public static final int TYPE_GBOX = 1;
	public static final int TYPE_TABLET_Q88 = 2;
	public static final int TYPE_G68 = 3;
	public static final int TYPE_TABLET_G58 = 4;
	public static final int TYPE_GBOX_NEW = 5;
	public static final int TYPE_TABLET_Q9 = 6;
	public static final int TYPE_TABLET_Q9_EN = 7;
	public static final int TYPE_TABLET_XD = 8;
	public static final int TYPE_TABLET_XD_EN = 9;
	public static final int TYPE_APK = 10;
	public static final int TYPE_TABLET_ANDROID7 = 11;
	public int mType=TYPE_TABLET_ANDROID7;
			
	private void loadConfig() {
		switch (mType) {
		case TYPE_GBOX:
			mInsertPath = "/games";
			break;
		case TYPE_TABLET_Q88:
			mSettingLayoutId=R.layout.category_settings_tablet;
			mInsertPath = "/mnt/external_sd";
			break;
		case TYPE_G68:
			mSettingLayoutId=R.layout.category_settings_no_bt;
			mLauncherLayoutId=R.layout.activity_launcher_g68;
			mInsertPath = "/mnt/sdcard";
			isSingleEnumlator =true;
			int[] mArray={R.string.parent_lock_vc_label,R.string.parent_lock_gc_label,R.string.parent_lock_ma_label};
			mLockCheckBoxTitle=mArray;
			break;
		case TYPE_GBOX_NEW:
			mInsertPath = "/mnt/external_sd";
			isSingleEnumlator =true;
			break;
		case TYPE_TABLET_G58:
			mSettingLayoutId=R.layout.category_settings_tablet;
			mLauncherLayoutId=R.layout.activity_launcher_g58;
			mInsertPath = "/mnt/internal_sd/roms";
			int[] tempG58={R.string.parent_lock_roms,R.string.parent_lock_android,R.string.parent_lock_gd_label,R.string.parent_lock_apps};
			mLockCheckBoxTitle=tempG58;
			break;
		case TYPE_TABLET_Q9:
		{
			mSettingLayoutId=R.layout.category_settings_tablet;
			mLauncherLayoutId=R.layout.activity_launcher_g9;
			mInsertPath = "/mnt/sdcard/roms";
			int[] tempG9={R.string.parent_lock_roms,R.string.parent_lock_pc,R.string.parent_lock_android,R.string.parent_lock_app};
			mLockCheckBoxTitle=tempG9;
			canMove = true;
		}
			break;
		case TYPE_TABLET_Q9_EN:
			mSettingLayoutId=R.layout.category_settings_tablet;
			mLauncherLayoutId=R.layout.activity_launcher_g9_en;
			mInsertPath = "/mnt/sdcard/roms";
			int[] tempG9EN={R.string.parent_lock_roms,R.string.parent_lock_android,R.string.parent_lock_app};
			mLockCheckBoxTitle=tempG9EN;
			canMove = true;
			break;
		case TYPE_TABLET_XD:
			mSettingLayoutId=R.layout.category_settings_tablet;
			mLauncherLayoutId=R.layout.activity_launcher_xd;
			mInsertPath = "/mnt/sdcard/roms";
			int[] tempXD={R.string.parent_lock_netgame,R.string.parent_lock_roms,R.string.parent_lock_pc,R.string.parent_lock_android,R.string.parent_lock_app};
			mLockCheckBoxTitle=tempXD;
			canMove = true;
			break;
		case TYPE_TABLET_ANDROID7:
			mSettingLayoutId=R.layout.category_settings_tablet_android7;
			mLauncherLayoutId=R.layout.activity_launcher_android7;
			
			mInsertPath = "/mnt/sdcard/roms";
			int[] temp7={R.string.parent_lock_roms,R.string.parent_lock_pc,R.string.parent_lock_android,R.string.parent_lock_app};
			mLockCheckBoxTitle=temp7;
			canMove = true;
			break;
		case TYPE_TABLET_XD_EN:
			mSettingLayoutId=R.layout.category_settings_tablet;
			mLauncherLayoutId=R.layout.activity_launcher_xd_en;
			mInsertPath = "/mnt/sdcard/roms";
			int[] tempXDEN={R.string.parent_lock_roms,R.string.parent_lock_android,R.string.parent_lock_app};
			mLockCheckBoxTitle=tempXDEN;
			canMove = true;
			break;
		case TYPE_APK:
			mInsertPath = "/mnt/sdcard/roms";
			mLauncherLayoutId=R.layout.activity_launcher_apk;
			break;
		default:
			mInsertPath = "/mnt/external_sd";
			break;
		}
	}
	public void expand(Context context){
		Configuration config=context.getResources().getConfiguration();
		Log.i("xin", "config="+config);
		String zh=config.locale.getLanguage();
		boolean isZh=(zh!=null&&zh.equals("zh"));
		if(mType==TYPE_TABLET_ANDROID7){
			if(!isZh){
				mLauncherLayoutId=R.layout.activity_launcher_android7_en;
			}
		}
	}

	public String mInsertPath = "/games";// "/mnt/external_sd"
	public boolean isSingleEnumlator=false;
	public boolean canMove = false;
	public int mSettingLayoutId=R.layout.category_settings;
	public int mLauncherLayoutId=R.layout.activity_launcher_gbox;
	public int mLockCheckBoxTitle[]=null;
	public int mReflectHeight = 100;
	public int mCellWidth;
	public int mCellHeight;
	public int mCellWidthGap;
	public int mCellHeightGap;

	public int mCellMarginTop;
	public int mCellMarginLeft;
	public int mCellMarginRight;
	public int mCellMarginBottom;

	public int mAppPaddingTop;
	public int mAppPaddingLeft;

	public boolean mShowOperationTip;

	public String mPackageName;

	public void loadVar(Context ctx) {
		this.mPackageName = ctx.getPackageName();
		Resources res = ctx.getResources();
		mCellWidth = res.getDimensionPixelSize(R.dimen.app_cell_width);
		mCellHeight = res.getDimensionPixelSize(R.dimen.app_cell_height);
		mCellWidthGap = res.getDimensionPixelSize(R.dimen.app_cell_height_gap);
		mCellHeightGap = res.getDimensionPixelSize(R.dimen.app_cell_height_gap);

		mCellMarginTop = res.getDimensionPixelSize(R.dimen.app_cell_margin_top);
		mCellMarginLeft = res.getDimensionPixelSize(R.dimen.app_cell_margin_left);
		mCellMarginRight = res.getDimensionPixelSize(R.dimen.app_cell_margin_right);
		mCellMarginBottom = res.getDimensionPixelSize(R.dimen.app_cell_margin_bottom);

		mReflectHeight = res.getDimensionPixelSize(R.dimen.app_reflect_height);

		mAppPaddingTop = res.getDimensionPixelSize(R.dimen.app_padding_top);
		mAppPaddingLeft = res.getDimensionPixelSize(R.dimen.app_padding_left);
		mShowOperationTip = res.getBoolean(R.bool.global_show_operation_tip);
	}

}
