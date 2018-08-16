package com.softwin.gbox.home.setting;

import com.softwin.gbox.home.R;
import com.xin.util.XLog;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class ParentManagerSettingOld extends Activity   {
	
	private ParentLock mLock;
	private InputMethodManager mIMM;
	private EditText mPasswordCheck;
	private RelativeLayout mManageLayout;
	private FrameLayout mPasswordLayout;
	private String mGobalPassword="www.softwincn.com";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLock=new ParentLock(this);
		mIMM = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);  
		setContentView(R.layout.activity_parent_manager_old);
		mManageLayout=(RelativeLayout) findViewById(R.id.parent_manager_layout);
		mPasswordLayout=(FrameLayout) findViewById(R.id.parent_password_layout);
		mPasswordCheck=(EditText) mPasswordLayout.findViewById(R.id.parent_password);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.parent_manager_frame, new ParentManagerFrame(mLock,mIMM)).commit();
		}
		final String password=mLock.getPassword();
		if(password==null||password.equals("")){
			enterMangerLayout();
		}else{
			//XLog.i("password="+password);
			mPasswordCheck.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					String string=s.toString();
					XLog.i("onTextChanged s="+s);
					if(password.equals(string)||mGobalPassword.equals(string)){
						XLog.i("onTextChanged ok");
						if (mIMM.isActive()) {
							mIMM.hideSoftInputFromWindow(getCurrentFocus()  
			                        .getWindowToken(),  
			                        InputMethodManager.HIDE_NOT_ALWAYS);  
						}
						enterMangerLayout();
					}
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
				}
				
				@Override
				public void afterTextChanged(Editable s) {
				}
			});
		}
		
	}
	private void enterMangerLayout() {
		mManageLayout.setVisibility(View.VISIBLE);
		mPasswordLayout.setVisibility(View.GONE);

	}



}
