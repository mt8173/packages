package com.xin.util;

import java.io.File;

public class PathGame {
	public String packagName;
	public String className;
	public String apkPath;
	public String iconPath;
	public String dataPath;
	public boolean existData() {
		if(dataPath!=null){
			File file=new File(dataPath);
			return file.exists()&&file.isDirectory()&&file.length()>0;
		}
		return false;
	}
}
