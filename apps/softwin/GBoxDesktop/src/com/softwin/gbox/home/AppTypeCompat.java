package com.softwin.gbox.home;

import java.util.ArrayList;

public class AppTypeCompat {

	private ArrayList<String> sVideoPackage = new ArrayList<String>();
	private ArrayList<String> sGameDownloadPackage = new ArrayList<String>();
	private ArrayList<String> sHidePackage = new ArrayList<String>();
	private ArrayList<String> sMyGamePackage = new ArrayList<String>();
	private ArrayList<String> sGameCenterPackage = new ArrayList<String>();

	public AppTypeCompat() {
		switch (StaticVar.self().mType) {
		case StaticVar.TYPE_TABLET_G58:
			initG58();
			break;
		case StaticVar.TYPE_GBOX_NEW:
			initGBoxNew();
			break;
		default:
			initGBox();
			break;
		}
	}

	public ArrayList<String> getPackageNames(int type) {
		switch (type) {
		case AppDatabase.TYPE_VIDIA:
			return sVideoPackage;
		case AppDatabase.TYPE_DOWNLOAD:
			return sGameDownloadPackage;
		case AppDatabase.TYPE_HIDE:
			return sHidePackage;
		case AppDatabase.TYPE_MYGAME:
			return sMyGamePackage;
		case AppDatabase.TYPE_GAME_CENTER:
			return sGameCenterPackage;
		}
		return new ArrayList<String>();
	}

	private void initThreeMenu() {
		sVideoPackage.add("com.sohutv.tv");
		sVideoPackage.add("com.togic.livevideo");
		sVideoPackage.add("com.youku.tv");
		sVideoPackage.add("com.youku.tv.c");
		sVideoPackage.add("com.pplive.androidpad");
		sVideoPackage.add("com.qiyi.video");
		sVideoPackage.add("com.baidu.tv.app");
		sVideoPackage.add("com.elinkway.tvlive2");
		sVideoPackage.add("com.moretv.android");
		sVideoPackage.add("com.luxtone.tuzi3");
		sVideoPackage.add("com.tudou.tv");
		sVideoPackage.add("com.tudou.tv.c");
		sVideoPackage.add("com.xunlei.tvcloud");
		sVideoPackage.add("hdpfans.com");
		sVideoPackage.add("net.myvst.v2");
		sVideoPackage.add("cn.box.cloudbox");
		sVideoPackage.add("com.letv.tv");
		sVideoPackage.add("com.slanissue.tv.erge");
		sVideoPackage.add("com.dianlv.tv");
		sVideoPackage.add("com.molitv.android");
		sVideoPackage.add("com.google.android.youtube");
		sVideoPackage.add("org.xbmc.xbmc");

		sHidePackage.add("com.android.settings");
		sHidePackage.add("com.sohu.inputmethod.sogouoem");
		sHidePackage.add(Launcher.GUIDE_PACKGAE);
		sHidePackage.add("com.google.android.gms");
		sGameCenterPackage.add("com.dygame.gamezone2");
		sGameCenterPackage.add("com.app.xjiajia");
		sGameCenterPackage.add("com.jiajia.club_main");
		sGameCenterPackage.add("com.exampl.dancer");

		sGameCenterPackage.add("com.trans.yoga");
		sGameCenterPackage.add("com.trans.pingpang2");
		sGameCenterPackage.add("com.trans.beatpenguin");
		sGameCenterPackage.add("com.trans.dancingbeats");
		sGameCenterPackage.add("com.trans.sprint");
		sGameCenterPackage.add("com.trans.tennis");
		sGameCenterPackage.add("com.trans.hurdling");
	}

	private void initG58() {
		sGameDownloadPackage.add("com.putaolab.ptgame");
		sGameDownloadPackage.add("com.xiaoji.tvbox");
		sGameDownloadPackage.add("com.xiaoji.emulator");
		sGameDownloadPackage.add("cn.vszone.tv.gamebox");
		sGameDownloadPackage.add("com.shafa.market");
		sGameDownloadPackage.add("com.bitgames.android.tv");
		sGameDownloadPackage.add("com.tvgamecenter");
		sGameDownloadPackage.add("cn.gloud.client");
		sGameDownloadPackage.add("com.android.vending");

		sHidePackage.add("com.softwin.emulators2");
		sHidePackage.add("com.android.settings");
		sHidePackage.add("com.sohu.inputmethod.sogouoem");
		sHidePackage.add("com.google.android.gms");
		sHidePackage.add("com.sohu.inputmethod.sogou");

		sGameCenterPackage.add("com.dygame.gamezone2");
		sGameCenterPackage.add("com.app.xjiajia");
		sGameCenterPackage.add("com.jiajia.club_main");

	}

	private void initGBox() {
		sVideoPackage.add("com.sohutv.tv");
		sVideoPackage.add("com.togic.livevideo");
		sVideoPackage.add("com.youku.tv");
		sVideoPackage.add("com.youku.tv.c");
		sVideoPackage.add("com.pplive.androidpad");
		sVideoPackage.add("com.qiyi.video");
		sVideoPackage.add("com.baidu.tv.app");
		sVideoPackage.add("com.elinkway.tvlive2");
		sVideoPackage.add("com.moretv.android");
		sVideoPackage.add("com.luxtone.tuzi3");
		sVideoPackage.add("com.tudou.tv");
		sVideoPackage.add("com.tudou.tv.c");
		sVideoPackage.add("com.xunlei.tvcloud");
		sVideoPackage.add("hdpfans.com");
		sVideoPackage.add("net.myvst.v2");
		sVideoPackage.add("cn.box.cloudbox");
		sVideoPackage.add("com.letv.tv");
		sVideoPackage.add("com.slanissue.tv.erge");
		sVideoPackage.add("com.dianlv.tv");
		sVideoPackage.add("com.molitv.android");
		sVideoPackage.add("com.google.android.youtube");
		sVideoPackage.add("org.xbmc.xbmc");
		sGameDownloadPackage.add("com.putaolab.ptgame");
		sGameDownloadPackage.add("com.xiaoji.tvbox");
		sGameDownloadPackage.add("cn.vszone.tv.gamebox");
		sGameDownloadPackage.add("com.shafa.market");
		sGameDownloadPackage.add("com.bitgames.android.tv");
		sGameDownloadPackage.add("com.tvgamecenter");
		sGameDownloadPackage.add("cn.gloud.client");
		sGameDownloadPackage.add("com.android.vending");
		sHidePackage.add("com.android.settings");
		sHidePackage.add("com.sohu.inputmethod.sogouoem");
		sHidePackage.add(Launcher.GUIDE_PACKGAE);
		sHidePackage.add("com.google.android.gms");
		sGameCenterPackage.add("com.dygame.gamezone2");
		sGameCenterPackage.add("com.app.xjiajia");
		sGameCenterPackage.add("com.jiajia.club_main");
		sGameCenterPackage.add("com.exampl.dancer");

		sGameCenterPackage.add("com.trans.yoga");
		sGameCenterPackage.add("com.trans.pingpang2");
		sGameCenterPackage.add("com.trans.beatpenguin");
		sGameCenterPackage.add("com.trans.dancingbeats");
		sGameCenterPackage.add("com.trans.sprint");
		sGameCenterPackage.add("com.trans.tennis");
		sGameCenterPackage.add("com.trans.hurdling");
	}

	private void initGBoxNew() {
		sVideoPackage.add("com.sohutv.tv");
		sVideoPackage.add("com.togic.livevideo");
		sVideoPackage.add("com.youku.tv");
		sVideoPackage.add("com.youku.tv.c");
		sVideoPackage.add("com.pplive.androidpad");
		sVideoPackage.add("com.qiyi.video");
		sVideoPackage.add("com.baidu.tv.app");
		sVideoPackage.add("com.elinkway.tvlive2");
		sVideoPackage.add("com.moretv.android");
		sVideoPackage.add("com.luxtone.tuzi3");
		sVideoPackage.add("com.tudou.tv");
		sVideoPackage.add("com.tudou.tv.c");
		sVideoPackage.add("com.xunlei.tvcloud");
		sVideoPackage.add("hdpfans.com");
		sVideoPackage.add("net.myvst.v2");
		sVideoPackage.add("cn.box.cloudbox");
		sVideoPackage.add("com.letv.tv");
		sVideoPackage.add("com.slanissue.tv.erge");
		sVideoPackage.add("com.dianlv.tv");
		sVideoPackage.add("com.molitv.android");
		sVideoPackage.add("com.google.android.youtube");
		sVideoPackage.add("org.xbmc.xbmc");
		sGameDownloadPackage.add("com.putaolab.ptgame");
		sGameDownloadPackage.add("com.xiaoji.tvbox");
		sGameDownloadPackage.add("cn.vszone.tv.gamebox");
		sGameDownloadPackage.add("com.shafa.market");
		sGameDownloadPackage.add("com.bitgames.android.tv");
		sGameDownloadPackage.add("com.tvgamecenter");
		sGameDownloadPackage.add("com.android.vending");
		sHidePackage.add("com.android.settings");
		sHidePackage.add("com.sohu.inputmethod.sogouoem");
		sHidePackage.add("cn.gloud.client");
		sHidePackage.add("com.google.android.gms");
		sGameCenterPackage.add("com.dygame.gamezone2");
		sGameCenterPackage.add("com.app.xjiajia");
		sGameCenterPackage.add("com.jiajia.club_main");
		sGameCenterPackage.add("com.exampl.dancer");

		sGameCenterPackage.add("com.trans.yoga");
		sGameCenterPackage.add("com.trans.pingpang2");
		sGameCenterPackage.add("com.trans.beatpenguin");
		sGameCenterPackage.add("com.trans.dancingbeats");
		sGameCenterPackage.add("com.trans.sprint");
		sGameCenterPackage.add("com.trans.tennis");
		sGameCenterPackage.add("com.trans.hurdling");
	}

}
