package com.softwin.gbox.home;

import java.util.List;

import com.softwin.gbox.home.status.StaticFactory;
import com.xin.util.RunTimeTotal;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class BoxApplication extends Application {
	
	private BoxModel mModel;
	private AppDatabase mDatabase;
	@Override
	public void onCreate() {
		super.onCreate();
		StaticVar.self().expand(this);
        mModel = new BoxModel(this);
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        registerReceiver(mModel, filter);
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
        filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        //filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        registerReceiver(mModel, filter);
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addDataScheme("file");
        registerReceiver(mModel, filter);
        
        mDatabase=new AppDatabase(this);
		PackageManager manager = this.getPackageManager();
		RunTimeTotal runtimeTest=new RunTimeTotal("preinstall");
		List<PackageInfo> packageInfos=manager.getInstalledPackages(0);
        for(PackageInfo info:packageInfos){
        	String pkg=manager.getInstallerPackageName(info.packageName);
        	if(pkg!=null){
        		if(mDatabase.isType(pkg, AppDatabase.TYPE_DOWNLOAD)){
        			if(!mDatabase.existApp(info.packageName, null)){
        				mDatabase.insert(info.packageName, null, AppDatabase.TYPE_MYGAME, AppDatabase.INDEX_LEVEL_GMME_AUTO, true);
        			}
        			
        		}
        	}
        }
        runtimeTest.next();
        
        StaticFactory.getNetworkController(this);
        StaticFactory.getUsbController(this);
        startPreInstall(this);
	}

	public void setCallback(BoxModel.Callback callback) {
		if(mModel!=null){
			mModel.setCallback(callback);
		}
		
	}
	
	public void startPreInstall(Context context) {
    	Intent serviceIntent = new Intent();
		serviceIntent.setAction("softwin.intent.action.preinstall");
		serviceIntent.putExtra("force", false);
		context.startService(serviceIntent);
	}
	
	public AppDatabase getDatabase(){
		return mDatabase;
	}



}
