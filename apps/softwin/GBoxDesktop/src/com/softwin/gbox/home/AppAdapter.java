package com.softwin.gbox.home;

import java.util.List;

import com.xin.util.ImageHelper;
import com.xin.util.SimpleAppsModel;
import com.xin.util.XLog;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class AppAdapter extends BaseAdapter{
	private Context mContext;
	private List<SimpleAppsModel> mItems;
	public AppAdapter(Context context, List<SimpleAppsModel> apps) {
		this.mContext=context;
		this.mItems=apps;
	}

	@Override
	public int getCount() {
		return (mItems.size()-1)/2+1;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	//0 1 2 3 4
	//0 2 4 6 8
	//1 3 5 7 9
	public SimpleAppsModel getApp1(int position){
		return mItems.get(position*2);
	}
	public SimpleAppsModel getApp2(int position){
		int index=position*2+1;
		if(index<mItems.size()){
			return mItems.get(index);
		}
		return null;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=View.inflate(mContext, R.layout.item_myapp_two_with_shadow, null);
			holder.app1=(AppItemView) convertView.findViewById(R.id.item_app1);
			holder.app2=(AppItemView) convertView.findViewById(R.id.item_app2);
			holder.shadow=(ImageView) convertView.findViewById(R.id.item_app_shadow);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		XLog.i("AppAdapter:"+position);
		SimpleAppsModel app1=getApp1(position);
		SimpleAppsModel app2=getApp2(position);
		ColorConfig config=getColorConfig(position);
		holder.app1.setText(app1.title);
		holder.app1.setIcon(app1.icon);
		holder.app1.setBackgroundResource(config.getBg());
		if(app2!=null){
			holder.app2.setVisibility(View.VISIBLE);
			holder.app2.setText(app2.title);
			holder.app2.setIcon(app2.icon);
			holder.app2.setBackgroundResource(config.getBg());
			holder.shadow.setImageBitmap(config.getShadow());
		}else{
			holder.app2.setVisibility(View.GONE);
			holder.shadow.setVisibility(View.GONE);
		}

		return convertView;
	}
	public static class ViewHolder{
		AppItemView app1;
		AppItemView app2;
		ImageView shadow;
	}
	public ColorConfig getColorConfig(int position){
		return mColorConfig[position%mColorConfig.length];
	}
	ColorConfig[] mColorConfig={
			new ColorConfig(R.drawable.cs_red),
			new ColorConfig(R.drawable.cs_blue),
			new ColorConfig(R.drawable.cs_green),
			new ColorConfig(R.drawable.cs_iblue)
	};
	public class ColorConfig{
		private int bg;
		private Bitmap shadow;
		public ColorConfig(int bg){
			this.bg=bg;
		}
		public Bitmap getShadow(){
			if(shadow==null){
				Bitmap bp=ImageHelper.drawableIdToBitmap(mContext.getResources(), bg);
				shadow=ImageHelper.createReflectionImageWithOrigin(bp, 100);
			}
			return shadow;
		}
		public int getBg(){
			return bg;
		}
	}

}
