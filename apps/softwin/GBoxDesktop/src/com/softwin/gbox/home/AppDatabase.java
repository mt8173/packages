package com.softwin.gbox.home;

import java.util.ArrayList;
import java.util.Iterator;

import com.xin.util.SimpleAppsModel;
import com.xin.util.XLog;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppDatabase {
	public static final int INDEX_FOREMOST= -3000;
	public static final int INDEX_LEVEL_GAME_CONFIG= -2000;
	public static final int INDEX_LEVEL_GMME_AUTO= -1000;
	public static final int INDEX_APP_START= 1;
	public static final int TYPE_HIDE = -1;
	public static final int TYPE_MYAPP = 0;
	public static final int TYPE_MYGAME = 1;
	public static final int TYPE_VIDIA = 2;
	public static final int TYPE_DOWNLOAD = 3;
	public static final int TYPE_BODY = 4;
	public static final int TYPE_GAME_CENTER = 5;
	public static final int TYPE_NET_GAME = 6;
	public static final String TABLET = "application";
	public static final String FIELD_PACKAGE = "t_package_name";
	public static final String FIELD_CLASS = "t_class_name";
	public static final String FIELD_TYPE = "t_type";
	public static final String FIELD_INDEX = "t_index";
	public static final String FIELD_MOVE = "t_can_move";
	private DatabaseHelper mHelper;
	private Context mContext;
	public AppDatabase(Context context) {
		this.mContext=context;
		mHelper=new DatabaseHelper(context);
	}
	public static int mapToType(String className){
		if(className!=null){
			if(className.equals(MyAppCellLayout.class.getSimpleName())){
				return TYPE_MYAPP;
			}else if(className.equals(MyGameCellLayout2.class.getSimpleName())){
				return TYPE_MYGAME;
			}else if(className.equals(VideoCellLayout.class.getSimpleName())){
				return TYPE_VIDIA;
			}else if(className.equals(MyAppCellLayout.class.getSimpleName())){
				return TYPE_GAME_CENTER;
			}else if(className.equals(GameDownloadCellLayout.class.getSimpleName())){
				return TYPE_DOWNLOAD;
			}else if(className.equals(BodyCellLayout.class.getSimpleName())){
				return TYPE_BODY;
			}else if(className.equals(GameCenterItemView.class.getSimpleName())){
				return TYPE_GAME_CENTER;
			}else if(className.equals(GameCenterItemView.class.getSimpleName())){
				return TYPE_NET_GAME;
			}
		}
		return TYPE_HIDE;
	}
	public ArrayList<SimpleAppsModel> getAppByType(int type){
		ArrayList<SimpleAppsModel> apps=new ArrayList<SimpleAppsModel>();
		PackageManager pm= mContext.getPackageManager();
		SQLiteDatabase db=mHelper.getReadableDatabase();
		Cursor c= db.rawQuery("select * from "+TABLET+" where "+FIELD_TYPE+" = ? order by "+FIELD_INDEX, new String[]{Integer.toString(type)});
		while(c!=null&&c.moveToNext()){
			String package_ = c.getString(c.getColumnIndex(FIELD_PACKAGE));
			int index = c.getInt(c.getColumnIndex(FIELD_INDEX));
			boolean move = c.getInt(c.getColumnIndex(FIELD_MOVE))!=0;
			//XLog.i(package_+" \t"+index);
			ArrayList<SimpleAppsModel> items=SimpleAppsModel.loadLauncherByPackage(pm, package_);
			if(items==null||items.size()==0){
				XLog.e("AppDatabase: "+package_ +" is not launcher or not exist");
			}else if(items.size()==1){
				SimpleAppsModel item=items.get(0);
				item.index=index;
				item.canMove=move;
				apps.add(item);
			}else{
				XLog.w("AppDatabase: "+package_+" have 2+ launcher");
				XLog.w("AppDatabase: "+items);
				for(SimpleAppsModel item:items){
					item.index=index;
					item.canMove=move;
				}
				apps.addAll(items);
			}
			
		}
		db.close();
		//printIndex(apps);
		return apps;
	}
	public void printIndex(ArrayList<SimpleAppsModel> apps){
		if(apps==null)return;
		for(int i=0;i<apps.size();i++){
			SimpleAppsModel model=apps.get(i);
			XLog.i(model.title+" \t"+model.index);
		}
	}
	
	public ArrayList<SimpleAppsModel> getDefaultApp(){
		ArrayList<SimpleAppsModel> temps=SimpleAppsModel.loadApplications(mContext);
		SQLiteDatabase db=mHelper.getReadableDatabase();
		Cursor c= db.rawQuery("select * from "+TABLET+" order by "+FIELD_INDEX, null);
		while(c!=null&&c.moveToNext()){
			String package_ = c.getString(c.getColumnIndex(FIELD_PACKAGE));
			int index = c.getInt(c.getColumnIndex(FIELD_INDEX));
			boolean move = c.getInt(c.getColumnIndex(FIELD_MOVE))!=0;
			int type = c.getInt(c.getColumnIndex(FIELD_TYPE));
			if(type==TYPE_MYAPP){
				Iterator<SimpleAppsModel> it=temps.iterator();
				while(it.hasNext()){
					SimpleAppsModel sam=it.next();
					if(package_.equals(sam.componentName.getPackageName())){
						sam.index=index;
						sam.canMove=move;
					}
				}
			}else{
				Iterator<SimpleAppsModel> it=temps.iterator();
				while(it.hasNext()){
					SimpleAppsModel sam=it.next();
					if(package_.equals(sam.componentName.getPackageName())){
						it.remove();
					}
				}
			}
			
		}
		db.close();
		//printIndex(temps);
		return temps;
	}
	
	public boolean isType(String pkg,int type){
		SQLiteDatabase db=mHelper.getReadableDatabase();
		Cursor c= db.rawQuery("select * from "+TABLET+" where "+FIELD_PACKAGE+" = ? and "+FIELD_TYPE+" = ?", new String[]{pkg,Integer.toString(type)});
		return c!=null&&c.getCount()>0;
	}
	public boolean existApp(String pkg,String clazz){
		SQLiteDatabase db=mHelper.getReadableDatabase();
		Cursor c= null;
		if(clazz!=null){
			c= db.rawQuery("select * from "+TABLET+" where "+FIELD_PACKAGE+" = ? and "+FIELD_CLASS+" = ?", new String[]{pkg,clazz});
		}else{
			c= db.rawQuery("select * from "+TABLET+" where "+FIELD_PACKAGE+" = ?",  new String[]{pkg});
		}
		return c!=null&&c.getCount()>0;
	}
	public void insert(String package_,String class_,int type,int index,boolean canMove){
		SQLiteDatabase db=mHelper.getWritableDatabase();
		ContentValues values=new ContentValues();
		mHelper.insertInner(db, values, package_, class_, type, index, canMove);
		db.close();
	}
	
	public void delete(String pkg){
		SQLiteDatabase db=mHelper.getWritableDatabase();
		db.delete(TABLET, FIELD_PACKAGE+" = ?", new String[]{pkg});
	}

	class DatabaseHelper extends SQLiteOpenHelper {
		public static final String DATABASE_NAME = "xin.db";
		private static final int DATABASE_VERSION = 1;  
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			String NAME_TABLE_CREATE = "create table "+TABLET+"(" 
			        + "_id INTEGER PRIMARY KEY AUTOINCREMENT," 
					+ FIELD_PACKAGE+" TEXT,"
			        + FIELD_INDEX+" INTEGER DEFAULT 0,"
			        + FIELD_TYPE+" INTEGER DEFAULT 0,"  
			        + FIELD_MOVE+" bit DEFAULT 1,"  
			        + FIELD_CLASS+" TEXT,"
			        + "UNIQUE("+FIELD_PACKAGE+","+FIELD_CLASS+") );";  
			db.execSQL(NAME_TABLE_CREATE); 
			initData(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
			db.execSQL("drop table "+TABLET);
			onCreate(db);
		}
		
		private void initData(SQLiteDatabase db) {
			switch(StaticVar.self().mType){
			case StaticVar.TYPE_TABLET_Q9:
				initG9(db);
				break;
			case StaticVar.TYPE_TABLET_Q9_EN:
				initG9(db);
				break;
			case StaticVar.TYPE_TABLET_XD:
			case StaticVar.TYPE_TABLET_ANDROID7:
				initG9(db);
				break;
			case StaticVar.TYPE_TABLET_XD_EN:
				initG9(db);
				break;
			default:
				initOld(db);
				break;
			}
		}


		private void addHide(SQLiteDatabase db,ContentValues values,String package_){
			insertInner(db,values,package_,null,TYPE_HIDE,0,false);
		}
		private void insertInner(SQLiteDatabase db,ContentValues values,String package_,String class_,int type,int index,boolean canMove) {
			values.clear();
			values.put(FIELD_PACKAGE, package_);
			if(class_!=null){
				values.put(FIELD_CLASS, class_);
			}
			values.put(FIELD_TYPE, type);
			values.put(FIELD_INDEX, index);
			values.put(FIELD_MOVE, canMove);
			db.insert(TABLET, null, values);
		}

		private void initG9(SQLiteDatabase db) {
			ContentValues values=new ContentValues();
			addHide(db,values,"com.softwin.gbox.gamecenter");
			//from emulator 
			addHide(db,values,"com.xiaoji.emulator");
			addHide(db,values,"cn.vszone.ko.tv.arena");
			addHide(db,values,"org.ppsspp.ppsspp");
			//pc game
			addHide(db,values,"cn.gloud.client");
			//hide app
			addHide(db,values,"com.sohu.inputmethod.sogou");
			addHide(db,values,"com.android.settings");
			addHide(db,values,"com.google.android.gms");
			addHide(db,values,"com.softwin.gamepad");
			addHide(db,values,"com.softwin.gbox.settings");
			//android game
			int i=INDEX_FOREMOST;
			insertInner(db,values,"com.qihoo.gameunion",null,TYPE_MYGAME,i++,false);
			insertInner(db,values,"com.putaolab.mobile",null,TYPE_MYGAME,i++,false);
			insertInner(db,values,"com.openpad.devicemanagementservice",null,TYPE_MYGAME,i++,false);
			insertInner(db,values,"com.diguayouxi",null,TYPE_MYGAME,i++,false);
			insertInner(db,values,"com.xiyuegame.tvgame",null,TYPE_MYGAME,i++,false);
			insertInner(db,values,"com.muzhiwan.market",null,TYPE_MYGAME,i++,false);
			insertInner(db,values,"umido.ugamestore",null,TYPE_MYGAME,i++,false);
		}

		
		private void initOld(SQLiteDatabase db) {
			ContentValues values=new ContentValues();
			addHide(db,values,"com.softwin.gbox.gamecenter");
			AppTypeCompat appType=new AppTypeCompat();
			insertAppType(db,values,appType,TYPE_HIDE);
			insertAppType(db,values,appType,TYPE_MYGAME);
			insertAppType(db,values,appType,TYPE_VIDIA);
			insertAppType(db,values,appType,TYPE_DOWNLOAD);
			insertAppType(db,values,appType,TYPE_BODY);
			insertAppType(db,values,appType,TYPE_GAME_CENTER);
		}

		private void insertAppType(SQLiteDatabase db, ContentValues values,
				AppTypeCompat appType, int type) {
			ArrayList<String> list=appType.getPackageNames(type);
			for(String pkg:list){
				insertInner(db, values, pkg, null, type, INDEX_APP_START, true);
			}
		}
		
		

	}
	
	
}
