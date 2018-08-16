package com.softwin.gbox.home.test;

import com.softwin.gbox.home.R;
import com.xin.util.ImageHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class Test extends Activity{
	public static void startTest(Context context) {
		Intent intent=new Intent();
		intent.setClass(context, Test.class);
		context.startActivity(intent);

	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		test5();
	}
	void test1(){
		setContentView(R.layout.test_wifi_state);
	}
	void test2(){
		setContentView(new ImageEffect(this));
	}
	void test3(){
		ImageView myImageView=new ImageView(this);
		myImageView.setScaleType(ScaleType.CENTER);
		Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(  
                R.drawable.launcher_mainpush_default_data1)).getBitmap();  
		bitmap=ImageHelper.createReflectionImageWithOrigin(bitmap,200);
        myImageView.setImageBitmap(bitmap); 
        setContentView(myImageView);
	}
	void test4(){
		ImageView myImageView=new ImageView(this);
		myImageView.setScaleType(ScaleType.CENTER);
		Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(  
				R.drawable.setting_logo_account)).getBitmap();  
		bitmap=ImageHelper.getExtractBitmap(bitmap,Color.RED);
		myImageView.setImageBitmap(bitmap); 
		setContentView(myImageView);
	}
	void test5(){
		setContentView(R.layout.test_drop_shadow);
		ImageView myImageView=(ImageView) findViewById(R.id.test_img_shadow);
		Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(  
				R.drawable.cs_green_focus)).getBitmap();  
		Bitmap background=ImageHelper.getLayoutDropShadow(bitmap);
		myImageView.setImageBitmap(background);
	}

}
