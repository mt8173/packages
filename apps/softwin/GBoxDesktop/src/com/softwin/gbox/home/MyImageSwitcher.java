package com.softwin.gbox.home;

import java.util.Arrays;

import com.xin.util.ViewPrint;
import com.xin.util.XLog;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

public class MyImageSwitcher extends ImageSwitcher implements
		ImageSwitcher.ViewFactory, View.OnTouchListener {
	private int[] mImageArray;
	private int mIndex = 0;

	public MyImageSwitcher(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.MyImageSwitcher, 0, 0);
		int id = a.getResourceId(R.styleable.MyImageSwitcher_imageArray, 0);
		a.recycle();
		//XLog.i("MyImageSwitcher " + ViewPrint.getIdString(getResources(), id));

		a = getResources().obtainTypedArray(id);
		mImageArray = new int[a.length()];
		for (int i = 0; i < mImageArray.length; i++) {
			mImageArray[i] = a.getResourceId(i, 0);
		}
		a.recycle();

		//XLog.i("MyImageSwitcher " + Arrays.toString(mImageArray));
		init(context);
	}

	private void init(Context context) {
		setFactory(this);
		setOnTouchListener(this);
		//setBackgroundColor(Color.RED);
	}

	@Override
	public View makeView() {
		ImageView view = new ImageView(getContext());
		view.setImageResource(getNextImage(true));
		view.setScaleType(ScaleType.FIT_START);
		return view;
	}

	private synchronized int getNextImage(boolean b) {
		if (mIndex >= mImageArray.length) {
			mIndex = 0;
		}
		if (mIndex < 0) {
			mIndex = mImageArray.length - 1;
		}
		int id = mImageArray[mIndex];
		//XLog.i("MyImageSwitcher " + ViewPrint.getIdString(getResources(), id));
		if (b) {
			mIndex++;
		} else {
			mIndex--;
		}
		return id;
	}

	private float downX;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		//XLog.i("MyImageSwitcher " + event );
		//XLog.i(ViewPrint.getViewClassAndId(v));
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			downX = event.getX();
			break;
		}
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP: {
			float lastX = event.getX();
			//XLog.i("lastX="+lastX+"   downX="+downX);
			if (lastX < downX) {
				//XLog.i("left in");
				this.setInAnimation(AnimationUtils.loadAnimation(
						getContext(), R.anim.img_switch_in));
				this.setOutAnimation(AnimationUtils.loadAnimation(
						getContext(), R.anim.img_switch_out));
				this.setImageResource(getNextImage(true));
			} else {
				//XLog.i("right in");
				this.setInAnimation(AnimationUtils.loadAnimation(
						getContext(), R.anim.img_switch_in));
				this.setOutAnimation(AnimationUtils.loadAnimation(
						getContext(), R.anim.img_switch_out));
				this.setImageResource(getNextImage(false));
			}
		}

			break;
		}

		return true;
	}

}
