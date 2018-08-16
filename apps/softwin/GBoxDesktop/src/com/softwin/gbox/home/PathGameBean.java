package com.softwin.gbox.home;

import java.io.File;
import java.util.Locale;
import com.softwin.emulators2.GameStartor;
import com.xin.util.SimpleAppsModel;
import com.xin.util.UriImage;
import com.xin.util.XLog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

public class PathGameBean implements Comparable<PathGameBean>{
	File folder;
	String label;
	String head;
	String type;
	boolean valid = false;
	SimpleAppsModel model=null;
	File icon;
	public PathGameBean(File folder){
		this.folder=folder;
		if(folder.isDirectory()){
			String name=folder.getName();
			int index_=name.indexOf("-");
			if(index_>0){
				head=name.substring(0,index_);
				label=name.substring(index_+1);
				valid = true;
			}else{
				head=name;
				label=name;
				valid = true;
			}
		}
	}
	public PathGameBean(File gameFile,String type){
		this.folder=gameFile;
		this.type=type;
		if(folder.isDirectory()){
			File[] files=folder.listFiles();
			if(files==null||files.length<=0){
				valid=false;
			}else{
				String name=folder.getName();
				int index_=name.indexOf(".");
				if(index_>0){
					label=name.substring(0,index_);
				}else{
					label=name;
				}
				for(File f:files){
					if(isGame(f)){
						valid=true;
					}else if(isIcon(f)){
						icon=f;
					}
				}
			}
		}else{
			if(isGame(folder)){
				String name=folder.getName();
				int index_=name.indexOf(".");
				if(index_>0){
					label=name.substring(0,index_);
					valid = true;
					//兼容
					File ff=folder.getParentFile();
					if(ff!=null&&ff.getName().equals("roms")){
						File fff=ff.getParentFile();
						if(fff!=null){
							File icon=new File(fff,"screenshots/"+label+".p");
							if(icon.exists()){
								this.icon=icon;
							}
						}
					}
				}else{
					valid = false;
				}
			}
			
		}
	}
	public PathGameBean(SimpleAppsModel app) {
		model=app;
		label=app.title;
	}
	public boolean isValid() {
		return valid;
	}
	public Bitmap getDirectBg() {
		File file=new File(folder,"ic.p");
		if(file.exists())
		try{
			Bitmap d=BitmapFactory.decodeFile(file.getPath());
			return d;
		}catch(Exception e){
			
		}
		return null;
	}
	public Bitmap getBg(Context context) {
		File file=null;
		if(model!=null){
			file=new File(StaticVar.self().mInsertPath,model.componentName.getPackageName()+".p");
			XLog.d("getBg "+file.getPath());
		}else{
			file=new File(folder,"ic.p");
		}
		
		if(file.exists()){
			Uri uri=Uri.fromFile(file);
			UriImage img=new UriImage(context, uri);
			float scale=0.85f;
			int w=(int) (img.getWidth()*scale);
			int h=(int) (img.getHeight()*scale);
			return img.getResizedImage(w, h, 1024*1024);
		}
		return null;
	}
	public void setIcon(ImageView v){
		if(icon!=null){
			Uri uri=Uri.fromFile(icon);
			UriImage img=new UriImage(v.getContext(), uri);
			int size=90;
			Bitmap bm=img.getResizedImage(size, size, 1024*1024);
			v.setImageBitmap(bm);
		}else{
			v.setImageResource(R.drawable.game_icon_default);
		}
	}
	@Override
	public int compareTo(PathGameBean another) {
		return head.compareToIgnoreCase(another.head);
	}
	public void setType(String type) {
		// TODO Auto-generated method stub
		this.type=type;
	}
	public void startGame(Context context){
		if(model!=null){
			model.startActivitySafely(context);
		}else{
			startEmulator(context);
		}
	}
	public void startEmulator(Context context){
		if(folder.isDirectory()){
			File[] files=folder.listFiles();
			if(files==null||files.length<=0)return;
			for(File f:files){
				if(isGame(f)){
					startEmulatorByFile(context,f);
					break;
				}
			}
		}else{
			if(isGame(folder)){
				startEmulatorByFile(context,folder);
			}
		}
		
	}
	private void startEmulatorByFile(Context context,File f){
		XLog.i("open rom file["+f+"] type="+type);
		if(OperationTipActivity.TIP_ENABLED){
			OperationTipActivity.startTip(context,f,type);
		}else{
			GameStartor.launch(context, f, type);
		}
	}
	private boolean isGame(File f){
		String name=f.getName();
		return GameStartor.isGame(name,type);
	}
	private boolean isIcon(File f){
		String name=f.getName();
		name=name.toLowerCase(Locale.getDefault());
		return name.endsWith(".p");
	}
}
