package com.softwin.emulators2;

import java.io.File;
import com.xin.util.XLog;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.widget.Toast;

public class GameStartor {
	public static final String MAME_PACKAGE = "com.softwin.emulator.collection";
	public static final String MAME_CLASS = "com.softwin.emulator.collection.FileActivity$Arcade";
	
	public static final String GBA_PACKAGE = "game.emulator.gba";
	public static final String GBA_CLASS = "game.emulator.gba.EmulatorActivity";
	
	public static final String GENS_PACKAGE = "com.explusalpha.Snes9x";
	public static final String GENS_CLASS = "com.imagine.BaseActivity";
	
	public static final String N64_PACKAGE = "paulscode.android.mupen64plusae.softwin";
	public static final String N64_CLASS = "paulscode.android.mupen64plusae.MainActivity";
	
	
	public static final String NES_PACKAGE = "game.emulator.nes";
	public static final String NES_CLASS = "game.emulator.nes.EmulatorActivity";
	
	public static final String PS1_PACKAGE = "com.softwin.emulator.collection";
	public static final String PS1_CLASS = "com.softwin.emulator.collection.FileActivity$PS1";
	
	public static final String MD_PACKAGE = "game.emulator.gens";
	public static final String MD_CLASS = "game.emulator.gens.EmulatorActivity";
	
	public static final String SNES_PACKAGE = "com.softwin.emulator.collection";
	public static final String SNES_CLASS = "com.softwin.emulator.collection.FileActivity$SNES";
	
	public static Intent getIntentWithData(Context context,String packageString,String classString, File f,String type){
		if(packageString==null||classString==null){
			XLog.e("null!!! print packageString="+packageString+" classString="+classString);
			return null;
		}else{
			ComponentName name = new ComponentName(packageString, classString);
			try {
				context.getPackageManager().getActivityInfo(name, 0);
			} catch (NameNotFoundException e) {
				XLog.e("FileManager:"+ e.getMessage());
				Toast.makeText(context, "Please install "+type.toLowerCase()+" emulator!", Toast.LENGTH_SHORT).show();
				return null;
			}
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Uri uri = Uri.fromFile(f);
			intent.setData(uri);
			intent.setComponent(name);
			return intent;
		}
	}
	public static Intent getFbaArcadeIntent(Context context,File f){
		ComponentName cn = new ComponentName("com.joyemu.fbaapp.gpd", "com.joyemu.fba.FbaActivity");
		try {
			context.getPackageManager().getActivityInfo(cn, 0);
		} catch (NameNotFoundException e) {
			XLog.e("FileManager:"+ e.getMessage());
			Toast.makeText(context, "Please install fba emulator!", Toast.LENGTH_SHORT).show();
			return null;
		}
		Intent intent = new Intent();
		intent.setComponent(cn);
		intent.putExtra("romPath", f.getAbsolutePath());
		return intent;
	}
	public static Intent getPSPIntent(Context context,File f){
		ComponentName cn = new ComponentName("org.ppsspp.ppsspp.gpd", "org.ppsspp.ppsspp.PpssppActivity");
		try {
			context.getPackageManager().getActivityInfo(cn, 0);
		} catch (NameNotFoundException e) {
			XLog.e("FileManager:"+ e.getMessage());
			Toast.makeText(context, "Please install ppsspp emulator!", Toast.LENGTH_SHORT).show();
			return null;
		}
		Intent intent = new Intent();
		intent.setComponent(cn);
		intent.putExtra("romPath", f.getAbsolutePath());
		return intent;
	}
	
	public static Intent getIntentWithEmulator(Context context, File f,String type) {
		String packageString = null, classString = null;
		if(C.DIR_ARCADE.equals(type)){
			//packageString = MAME_PACKAGE;
			//classString = MAME_CLASS;
			//return getIntentWithData(context,packageString,classString,f,type);
			
			//use new arcade emulator(2014-6-5)
			return getFbaArcadeIntent(context,f);
		}else if(C.DIR_PSP.equals(type)){
			return getPSPIntent(context,f);
		}else if(C.DIR_GBA.equals(type)){
			packageString = GBA_PACKAGE;
			classString = GBA_CLASS;
			return getIntentWithData(context,packageString,classString,f,type);
		}else if(C.DIR_MD.equals(type)){
			packageString = MD_PACKAGE;
			classString = MD_CLASS;
			return getIntentWithData(context,packageString,classString,f,type);
		}else if(C.DIR_N64.equals(type)){
			packageString = N64_PACKAGE;
			classString = N64_CLASS;
			return getIntentWithData(context,packageString,classString,f,type);
		}else if(C.DIR_NES.equals(type)){
			packageString = NES_PACKAGE;
			classString = NES_CLASS;
			return getIntentWithData(context,packageString,classString,f,type);
		}else if(C.DIR_PS1.equals(type)){
			packageString = PS1_PACKAGE;
			classString = PS1_CLASS;
			return getIntentWithData(context,packageString,classString,f,type);
		}else if(C.DIR_SNES.equals(type)){
			packageString = SNES_PACKAGE;
			classString = SNES_CLASS;
			return getIntentWithData(context,packageString,classString,f,type);
		}else if(C.DIR_DC.equals(type)){
			packageString="com.reicast.emulator.gpd";
			classString="com.dc.app.MainActivity";
			return getIntentWithData(context,packageString,classString,f,type);
		}
		return null;
		
	}

	public static void launch(Context context, File f,String type){
		Intent intent=getIntentWithEmulator(context,f,type);
		if(intent!=null){
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
		
	}
	public static boolean isGame(String name, String type) {
		name=name.toLowerCase();
		if(C.DIR_ARCADE.equals(type)){
			if(name.equals("neogeo.zip")){
				return false;
			}else if(name.equals("pgm.zip")){
				return false;
			}else if(name.endsWith(".zip")){
				return true;
			}else{
				return false;
			}
		}else if(C.DIR_PSP.equals(type)){
		}else if(C.DIR_GBA.equals(type)){
		}else if(C.DIR_MD.equals(type)){
		}else if(C.DIR_N64.equals(type)){
		}else if(C.DIR_NES.equals(type)){
		}else if(C.DIR_PS1.equals(type)){
			if(name.endsWith(".img")){
				return true;
			}else{
				return false;
			}
		}else if(C.DIR_SNES.equals(type)){
		}
		return !name.endsWith(".p");
	}
}
