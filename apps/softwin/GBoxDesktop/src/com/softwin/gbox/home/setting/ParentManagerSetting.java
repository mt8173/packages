package com.softwin.gbox.home.setting;

import com.softwin.gbox.home.R;
import com.softwin.gbox.home.setting.PasswordInputer.CheckListener;
import com.xin.util.XLog;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ParentManagerSetting extends Activity implements CheckListener   {
	
	private ParentLock mLock;
	private InputMethodManager mIMM;
	private RelativeLayout mManageLayout;
	private RelativeLayout mPasswordLayout;
	//private String mGobalPassword="www.softwincn.com";
	private String mGobalPassword="075500";
	private String mPassword="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLock=new ParentLock(this);
		mIMM = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);  
		setContentView(R.layout.activity_parent_manager);
		mManageLayout=(RelativeLayout) findViewById(R.id.parent_manager_layout);
		mPasswordLayout=(RelativeLayout) findViewById(R.id.parent_password_layout);
		if (savedInstanceState == null) {
			FragmentTransaction transaction=getFragmentManager().beginTransaction();
			transaction.add(R.id.parent_password_frame, new PasswordInputer(this));
			transaction.add(R.id.parent_manager_frame, new ParentManagerFrame(mLock,mIMM));
			transaction.commit();
		}
		mPassword=mLock.getPassword();
		if(mPassword==null||mPassword.equals("")){
			enterMangerLayout();
		}else{

		}
		
	}
	private void enterMangerLayout() {
		mManageLayout.setVisibility(View.VISIBLE);
		mPasswordLayout.setVisibility(View.GONE);

	}
	@Override
	public boolean onCheck(String password) {
		boolean result=(mPassword.equals(password)||mGobalPassword.equals(password));
		if(result){
			XLog.i("onTextChanged ok");
			enterMangerLayout();
		}else{
			Toast.makeText(this, R.string.parent_password_check_fail, Toast.LENGTH_SHORT).show();
		}
		return result;
		
	}
	@Override
	public void onCancel() {
		// TODO Auto-generated method stub
		
	}



}
