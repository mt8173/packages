package com.xin.util;

import android.content.res.Resources;
import android.view.View;

public class ViewPrint {
	public static String getViewClassAndId(View view) {
		if (view != null) {
			StringBuilder out = new StringBuilder(128);
			out.append(view.getClass().getSimpleName());
			out.append('[');
			Resources r = view.getResources();
			int id = view.getId();
			if (id != View.NO_ID) {
				try{
					String typename = r.getResourceTypeName(id);
					String entryname = r.getResourceEntryName(id);
					out.append(typename);
					out.append("=");
					out.append(entryname);
				}catch(Exception e){
					out.append("self-define");
				}
			}
			out.append(']');
			return out.toString();
		}
		return null;
	}

	public static String getIdString(Resources r, int id) {
		if (id != View.NO_ID) {
			StringBuilder out = new StringBuilder(128);
			String typename = r.getResourceTypeName(id);
			String entryname = r.getResourceEntryName(id);
			out.append("R.");
			out.append(typename);
			out.append(".");
			out.append(entryname);
			return out.toString();
		}else{
			return "NO_ID";
		}
	}
}
