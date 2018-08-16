package com.softwin.gbox.home.status;

import com.softwin.gbox.home.R;
import com.xin.util.XLog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class UsbImageView extends ImageView implements UsbController.Callback{
	private final static boolean DEBUG = false;
	public UsbImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public UsbImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public UsbImageView(Context context) {
		super(context);
	}
	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		StaticFactory.getUsbController(getContext()).addCallback(this);;
	}
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		StaticFactory.getUsbController(getContext()).removeCallback(this);
	}

	@Override
	public void onUsbStateChanged(boolean isMounted) {
		if(DEBUG)XLog.i("UsbImageView mounted "+isMounted);
		if(isMounted){
			setVisibility(View.VISIBLE);
			setImageResource(R.drawable.ic_qs_usb);
		}else{
			setVisibility(View.GONE);
		}
		
	}



	
}
