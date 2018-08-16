package com.softwin.gbox.home.setting;

import com.softwin.gbox.home.R;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.Toast;

public class ParentPasswordSetting extends Activity {
	private ParentLock mLock;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_password_update);
		mLock=new ParentLock(this);
		if (savedInstanceState == null) {
			FragmentTransaction transaction=getFragmentManager().beginTransaction();
			transaction.add(R.id.pass_fragment, new PasswordInputer(listener1,getString(R.string.parent_modify_password_tip)));
			transaction.commit();
		}

	}
	PasswordInputer.CheckListener listener1=new PasswordInputer.CheckListener() {
		
		@Override
		public boolean onCheck(String password) {
			mLock.updatePassword(password);
			Toast.makeText(ParentPasswordSetting.this, R.string.parent_password_success,
					Toast.LENGTH_SHORT).show();
			finish();
			return true;
		}
		
		@Override
		public void onCancel() {
			
		}
	};
}
