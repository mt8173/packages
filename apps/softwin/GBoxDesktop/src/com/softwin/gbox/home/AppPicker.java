package com.softwin.gbox.home;

import java.util.Collections;
import java.util.List;

import com.xin.util.SimpleAppsModel;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AppPicker extends ListActivity{
	private AppListAdapter mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mAdapter = new AppListAdapter(this);
        if (mAdapter.getCount() <= 0) {
        	Toast.makeText(this, "no apps!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            setListAdapter(mAdapter);
        }

	}
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	SimpleAppsModel app = mAdapter.getItem(position);
        //insert
    	Intent intent=new Intent(BoxModel.ACTION_ADD);
    	intent.putExtra(BoxModel.FIELD_PACKAGE, app.componentName.getPackageName());
    	intent.putExtra(BoxModel.FIELD_RECORD, RecordCellLayout.class.getName());
    	sendBroadcast(intent);
    	finish();
    }

    public class AppListAdapter extends ArrayAdapter<SimpleAppsModel> {
        private final List<SimpleAppsModel> mPackageInfoList;
        private final LayoutInflater mInflater;

        public AppListAdapter(Context context) {
            super(context, 0);
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mPackageInfoList=SimpleAppsModel.loadApplications(context);
            Collections.sort(mPackageInfoList, new SimpleAppsModel.NameSort());
            addAll(mPackageInfoList);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	AppViewHolder holder;
        	if(convertView==null){
        		holder=new AppViewHolder();
        		convertView=mInflater.inflate(R.layout.item_app_picker, null);
        		holder.mIcon=(ImageView) convertView.findViewById(R.id.app_picker_icon);
        		holder.mLabel=(TextView) convertView.findViewById(R.id.app_picker_label);
        		convertView.setTag(holder);
        	}else{
        		holder=(AppViewHolder) convertView.getTag();
        	}
        	SimpleAppsModel app=getItem(position);
        	holder.mIcon.setImageDrawable(app.icon);
        	holder.mLabel.setText(app.title);
            return convertView;
        }
    }
    private static class AppViewHolder{
    	private ImageView mIcon;
    	private TextView mLabel;
    }

	
	
	public static SimpleAppsModel create(Context context){
		SimpleAppsModel model=new SimpleAppsModel();
		model.componentName=new ComponentName(context, AppPicker.class);
		model.icon=null;
		model.title=null;
		return model;
	}
	public static SimpleAppsModel create(){
		SimpleAppsModel model=new SimpleAppsModel();
		model.componentName=new ComponentName(StaticVar.self().mPackageName, AppPicker.class.getName());
		model.icon=null;
		model.title=null;
		return model;
	}
}
