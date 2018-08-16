package com.softwin.gbox.home;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Xml;

import com.xin.util.ApkLoader;
import com.xin.util.PathGame;
import com.xin.util.SimpleAppsModel;
import com.xin.util.XLog;

public class MyGameImport {
	public static final String XML_CONFIG_NAME="games";
	public static final String START_TAG="game";
	public static final String NAME="name";
	public static final String ICON="icon";
	public static final String APK="apk";
	public static final String PACKAGE="pkg";
	public static final String CLASS="cls";
	public static final String DATA="data";
	public static List<SimpleAppsModel> importGames(Context context,boolean force){
		List<SimpleAppsModel> list= importGamesByConfig(context,"Android");
		XLog.i("importGames(config),load "+list.size()+" apks");
		List<SimpleAppsModel> list2=importGamesByAuto(context,"AndroidGames");
		XLog.i("importGames(auto),load "+list2.size()+" apks");
		list.addAll(list2);
		return list;
	}	
	public static List<SimpleAppsModel> importNetGames(Context context){
		List<SimpleAppsModel> list= importGamesByConfig(context,"net_games");
		XLog.i("importNetGames(config),load "+list.size()+" apks");
		List<SimpleAppsModel> list2=importGamesByAuto(context,"net_auto");
		XLog.i("importNetGames(auto),load "+list2.size()+" apks");
		list.addAll(list2);
		return list;
	}
	public static List<SimpleAppsModel> importGamesByAuto(Context context,String dirGame){
		PackageManager pm=context.getPackageManager();
		ArrayList<SimpleAppsModel> apps=new ArrayList<SimpleAppsModel>();
		File folder=new File(StaticVar.self().mInsertPath,dirGame);
		if(folder.exists()&&folder.isDirectory()){
			File[] gameFolders=folder.listFiles();
			if(gameFolders!=null&&gameFolders.length>0){
				int index=AppDatabase.INDEX_LEVEL_GMME_AUTO;
				for(File gameFolder:gameFolders){
					try{
						SimpleAppsModel model=folderToModel(gameFolder, pm);
						if(model!=null){
							model.index=index++;
							model.canMove=false;
							apps.add(model);
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}
		return apps;
	}
	private static SimpleAppsModel folderToModel(File gameFolder,PackageManager pm){
		if(gameFolder.isDirectory()){
			File[] childs=gameFolder.listFiles();
			if(childs!=null&&childs.length>0){
				File apk=null;
				for(File item:childs){
					if(item.getName().endsWith(".apk")){
						if(apk==null){
							apk=item;
						}else{
							XLog.e("not have 2+ apks");
							apk=null;
							break;
						}
					}
				}
				ApplicationInfo aInfo=apk!=null?ApkLoader.getApplicationInfo(pm, apk.getPath()):null;
				if(aInfo!=null){
					SimpleAppsModel model=new SimpleAppsModel();
					model.title=(String) aInfo.loadLabel(pm);
					model.componentName=new ComponentName(aInfo.packageName, "null");
					
					model.icon=aInfo.loadIcon(pm);
					model.game=new PathGame();
					model.game.apkPath=apk.getPath();
					model.game.packagName=aInfo.packageName;
					File dataPath=null;
					for(File item:childs){
						if(item.getName().equals("Android")&&item.isDirectory()){
							dataPath=item;
							break;
						}
					}
					if(dataPath!=null){
						model.game.dataPath=dataPath.getPath();
					}
					return model;
				}
			}
		}else if(gameFolder.isFile()){
			if(gameFolder.getName().endsWith(".apk")){
				ApplicationInfo aInfo=ApkLoader.getApplicationInfo(pm, gameFolder.getPath());
				if(aInfo!=null){
					SimpleAppsModel model=new SimpleAppsModel();
					model.title=(String) aInfo.loadLabel(pm);
					model.componentName=new ComponentName(aInfo.packageName, "null");
					model.icon=aInfo.loadIcon(pm);
					model.game=new PathGame();
					model.game.apkPath=gameFolder.getPath();
					model.game.packagName=aInfo.packageName;
					return model;
				}
			}
		}
		return null;
	}
	public static List<SimpleAppsModel> importGamesByConfig(Context context,String dirName){
		ArrayList<SimpleAppsModel> apps=null;
		File file;
		Locale locale=context.getResources().getConfiguration().locale;
		String pathByLocale=dirName+"/"+XML_CONFIG_NAME+locale.toString()+".xml";
		File fileByLocale=new File(StaticVar.self().mInsertPath,pathByLocale);
		XLog.i("access "+fileByLocale.getPath());
		if(fileByLocale.exists()){
			file=fileByLocale;
			XLog.i("use "+fileByLocale.getPath());
		}else{
			String pathByLanguage=dirName+"/"+XML_CONFIG_NAME+locale.getLanguage()+".xml";
			File fileByLanguage=new File(StaticVar.self().mInsertPath,pathByLanguage);
			XLog.i("access "+fileByLanguage);
			if(fileByLanguage.exists()){
				file=fileByLanguage;
				XLog.i("use "+fileByLanguage);
			}else{
				file=new File(StaticVar.self().mInsertPath,dirName+"/"+XML_CONFIG_NAME+".xml");
				XLog.i("use "+file.getPath());
			}
		}
		if(file.exists()){
			InputStream input;
			try {
				input = new FileInputStream(file);
				apps=parserGame(context,input,file.getParentFile());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(apps==null){
			apps=new ArrayList<SimpleAppsModel>();
		}
		return apps;
	}
	public static ArrayList<SimpleAppsModel> parserGame(Context ctx,InputStream stream,File mRelativeFile) throws XmlPullParserException, IOException {
		XLog.i("parser start in " + System.currentTimeMillis());
		ArrayList<SimpleAppsModel> list = null;
		SimpleAppsModel game = null;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(stream, "UTF-8");
		int type = parser.getEventType();
		int index=AppDatabase.INDEX_LEVEL_GAME_CONFIG;
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_DOCUMENT:
				list = new ArrayList<SimpleAppsModel>();
				break;

			case XmlPullParser.START_TAG:
				String name = parser.getName();
				if (START_TAG.equals(name)) {
					game = new SimpleAppsModel();
					game.game=new PathGame();
					game.index=index++;
					game.canMove=false;
				}
				if (game != null) {
					if (NAME.equals(name)) {
						game.title = parser.nextText();
					}
					if (ICON.equals(name)) {
						game.game.iconPath=getPath(mRelativeFile,parser.nextText());
					}
					if (APK.equals(name)) {
						game.game.apkPath = getPath(mRelativeFile,parser.nextText());
					}
					if (PACKAGE.equals(name)) {
						game.game.packagName = parser.nextText();
						if(game.game.className!=null){
							game.componentName=new ComponentName(game.game.packagName, game.game.className);
						}
					}
					if (CLASS.equals(name)) {
						game.game.className = parser.nextText();
						if(game.game.packagName!=null){
							game.componentName=new ComponentName(game.game.packagName, game.game.className);
						}
					}
					if (DATA.equals(name)) {
						game.game.dataPath = getPath(mRelativeFile,parser.nextText());
					}

				}
				break;
			case XmlPullParser.END_TAG:
				if (START_TAG.equals(parser.getName())) {
					if(game.game.apkPath!=null&&new File(game.game.apkPath).exists()){
						list.add(game);
					}else if(game.game.packagName!=null&&ApkLoader.isInstalled(ctx.getPackageManager(), game.game.packagName)){
						list.add(game);
					}
					game = null;
				}
				break;
			}
			type = parser.next();
		}
		XLog.i( "parser finish in " + System.currentTimeMillis());
		XLog.i( "total:" + list.size());
		return list;
	}
	public static String getPath(File mRelativeFile,String path){
		if(path.startsWith("/")){
			return path;
		}else{
			if(mRelativeFile!=null){
				return new File(mRelativeFile,path).getAbsolutePath();
			}else{
				return new File(StaticVar.self().mInsertPath,path).getAbsolutePath();
			}
		}
	}
}
