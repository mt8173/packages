package com.softwin.gbox.home;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import com.xin.util.SimpleAppsModel;
import com.xin.util.XLog;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.widget.Toast;

public class BoxModel extends BroadcastReceiver {
	public static final String ACTION_ADD = "com.xin.package.add";
	public static final String ACTION_RM = "com.xin.package.remove";
	public static final String FIELD_PACKAGE = "package";
	public static final String FIELD_RECORD = "record";
	private final BoxApplication mApp;
	private Handler mHandler = new Handler();
	private PackageManager mPm;
	private Callback mCallback;

	interface Callback {
		public void onPackageAdd(String package_);

		public void onPackageRemove(String package_);

		public void forceReloadUI();

		public void forceReloadWorksapce();
	}

	BoxModel(BoxApplication app) {
		mApp = app;
		mPm = app.getPackageManager();
	}

	public void setCallback(Callback callback) {
		this.mCallback = callback;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();
		XLog.i("BoxModel receive action=" + action);
		if (Intent.ACTION_PACKAGE_CHANGED.equals(action) || Intent.ACTION_PACKAGE_REMOVED.equals(action) || Intent.ACTION_PACKAGE_ADDED.equals(action)) {
			final String packageName = intent.getData().getSchemeSpecificPart();
			final boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
			int op = PackageUpdatedTask.OP_NONE;
			if (packageName == null || packageName.length() == 0) {
				return;
			}
			if (Intent.ACTION_PACKAGE_CHANGED.equals(action)) {
				op = PackageUpdatedTask.OP_UPDATE;
			} else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
				if (!replacing) {
					op = PackageUpdatedTask.OP_REMOVE;
				}
			} else if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
				if (!replacing) {
					op = PackageUpdatedTask.OP_ADD;
				} else {
					op = PackageUpdatedTask.OP_UPDATE;
				}
				new DeleteAppSourceFile(packageName).start();
			}
			if (op != PackageUpdatedTask.OP_NONE) {
				enqueuePackageUpdated(new PackageUpdatedTask(op, new String[] { packageName }));
			}
		} else if (Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE.equals(action)) {
			// First, schedule to add these apps back in.
			String[] packages = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
			enqueuePackageUpdated(new PackageUpdatedTask(PackageUpdatedTask.OP_ADD, packages));
		} else if (Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE.equals(action)) {
			String[] packages = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
			enqueuePackageUpdated(new PackageUpdatedTask(PackageUpdatedTask.OP_UNAVAILABLE, packages));
		} else if (Intent.ACTION_LOCALE_CHANGED.equals(action)) {
			if (mCallback != null) {
				//mCallback.forceReloadUI();
			}
		} else if (Intent.ACTION_MEDIA_MOUNTED.equals(action) || Intent.ACTION_MEDIA_EJECT.equals(action)) {
			String path = intent.getData().getPath();
			if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
				XLog.i("BoxModel receive ACTION_MEDIA_MOUNTED path=" + path);
				if (StaticVar.self().mInsertPath.startsWith(path)) {
					updateWorkspace(true,100);
				}
			} else if (Intent.ACTION_MEDIA_EJECT.equals(action)) {
				XLog.i("BoxModel receive ACTION_MEDIA_EJECT path=" + path);
				if (StaticVar.self().mInsertPath.startsWith(path)) {
					updateWorkspace(false,100);
				}
			}
		}
	}
	
	void updateWorkspace(final boolean isMounted,int delay){
		if(delay<=0){
			if(isMounted){
				//AppType.self().reImportSdApp(mApp);
			}else{
				//AppType.self().removeNotExistApp(mApp);
			}
			if (mCallback != null) {
				mCallback.forceReloadWorksapce();
			}
		}else{
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					updateWorkspace(isMounted,0);
				}
			}, delay);
		}
		
	}

	void enqueuePackageUpdated(PackageUpdatedTask task) {
		mHandler.post(task);
	}

	class PackageUpdatedTask implements Runnable {
		int mOp;
		String[] mPackages;

		public static final int OP_NONE = 0;
		public static final int OP_ADD = 1;
		public static final int OP_UPDATE = 2;
		public static final int OP_REMOVE = 3;
		public static final int OP_UNAVAILABLE = 4;

		public PackageUpdatedTask(int op, String[] packages) {
			mOp = op;
			mPackages = packages;
		}

		public void run() {
			final String[] packages = mPackages;
			final int N = packages.length;
			switch (mOp) {
			case OP_ADD:
				for (int i = 0; i < N; i++) {
					XLog.i("BoxModel.addPackage " + packages[i]);
					addPackage(packages[i]);
				}
				break;
			case OP_UPDATE:
				for (int i = 0; i < N; i++) {
					XLog.i("BoxModel.updatePackage " + packages[i]);
				}
				break;
			case OP_REMOVE:
			case OP_UNAVAILABLE:
				for (int i = 0; i < N; i++) {
					XLog.i("BoxModel.removePackage " + packages[i]);
					rmPackage(packages[i]);
				}
				break;
			}
		}
	}

	public void addPackage(String packageName) {
		XLog.i("send addPackage in BoxModel");
		String pkg = mPm.getInstallerPackageName(packageName);
		if (pkg != null) {
			if(mApp.getDatabase().isType(pkg, AppDatabase.TYPE_DOWNLOAD)){
				mApp.getDatabase().insert(packageName,null,AppDatabase.TYPE_MYGAME,10,true);
			}
		}
		Intent intent = new Intent(ACTION_ADD);
		intent.putExtra(FIELD_PACKAGE, packageName);
		mApp.sendBroadcast(intent);
	}

	public void rmPackage(String packageName) {
		Intent intent = new Intent(ACTION_RM);
		intent.putExtra(FIELD_PACKAGE, packageName);
		mApp.sendBroadcast(intent);
	}
	
	class DeleteAppSourceFile extends Thread{
		private String pkg;
		public DeleteAppSourceFile(String pkg){
			this.pkg=pkg;
		}
		public DeleteAppSourceFile(){
			this.pkg="com.netease.ldxy.qihoo";
		}
		@Override
		public void run() {
			if(pkg==null){
				return;
			}
			XLog.i("delete reset apk, pkg="+pkg);
			try {
				List<SimpleAppsModel> list=MyGameImport.importNetGames(mApp);
				deleteFileFromList(list, pkg);
				List<SimpleAppsModel> _extends=MyGameImport.importGames(mApp, false);
				deleteFileFromList(_extends, pkg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private void deleteFileFromList(List<SimpleAppsModel> list,String pkg){
			if(list!=null){
				for(SimpleAppsModel model:list){
					String cpkg=model.game.packagName;
					if(pkg.equals(cpkg)){
						String path=model.game.apkPath;
						File file=new File(path);
						XLog.i("delete file:"+file);
						if(file.exists()){
							file.delete();
							XLog.i("delete success");
							String text=mApp.getString(R.string.delete_file_x, path);
							((Launcher)mCallback).showToastTip(text);
						}
						return;
					}
					
				}
				XLog.i("not found file");
			}
		}
	}

}
