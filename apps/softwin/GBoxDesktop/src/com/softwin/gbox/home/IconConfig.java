package com.softwin.gbox.home;

public class IconConfig {
	public static final int[] sMyApps={
		R.drawable.mygame_cell_bg1,
		R.drawable.mygame_cell_bg1_zoomin,
		R.drawable.mygame_cell_bg2,
		R.drawable.mygame_cell_bg2_zoomin,
		R.drawable.mygame_cell_bg3,
		R.drawable.mygame_cell_bg3_zoomin,
		R.drawable.mygame_cell_bg5,
		R.drawable.mygame_cell_bg5_zoomin,
		R.drawable.mygame_cell_bg4,
		R.drawable.mygame_cell_bg4_zoomin,

	};
	public static int getMyAppItemIcon(int x,int y){
		return sMyApps[(x*2+y)%sMyApps.length];
	}
}
