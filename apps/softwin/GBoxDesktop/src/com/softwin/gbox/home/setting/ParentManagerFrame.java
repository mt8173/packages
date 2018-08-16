package com.softwin.gbox.home.setting;

import com.softwin.gbox.home.R;
import com.softwin.gbox.home.StaticVar;
import com.xin.util.XLog;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class ParentManagerFrame extends PreferenceFragment implements OnPreferenceChangeListener {
	private ParentLock mLock;
	private Preference passwordInput;
	private int[] mArray = { R.string.parent_lock_vc_label, R.string.parent_lock_gc_label, R.string.parent_lock_mg_label, R.string.parent_lock_ma_label,
			R.string.parent_lock_gd_label };

	public ParentManagerFrame(ParentLock mLock, InputMethodManager mIMM2) {
		this.mLock = mLock;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		addPreferencesFromResource(R.xml.parent_manager);
		int[] array = StaticVar.self().mLockCheckBoxTitle;
		if (array != null) {
			this.mArray = array;
		}
		for (int i = 0; i < mArray.length; i++) {
			initCheckBox(i, mArray[i]);
		}

		passwordInput = (Preference) findPreference("password_input");
		// passwordInput.setOnPreferenceChangeListener(this);
	}

	private void findCheckBox(int index) {
		CheckBoxPreference checkbox0 = (CheckBoxPreference) findPreference(getCheckBoxKey(index));
		if (checkbox0 != null) {
			checkbox0.setChecked(mLock.isLock(ParentLock.getMask(index)));
			checkbox0.setOnPreferenceChangeListener(this);
		}
	}

	private void initCheckBox(int index, int title) {
		CheckBoxPreference temp = new CheckBoxPreference(getActivity());
		temp.setKey(getCheckBoxKey(index));
		temp.setChecked(mLock.isLock(ParentLock.getMask(index)));
		temp.setOnPreferenceChangeListener(this);
		temp.setOrder(index);
		temp.setTitle(title);
		getPreferenceScreen().addPreference(temp);
	}

	private String getCheckBoxKey(int index) {
		return "checkbox" + index;
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		String key = preference.getKey();
		if (key != null && key.startsWith("checkbox")) {
			for (int i = 0; i < mArray.length; i++) {
				if (key.equals(getCheckBoxKey(i))) {
					boolean isChecked = newValue == Boolean.TRUE ? true : false;
					int flag = 1 << i;
					mLock.updateLock(isChecked ? flag : 0, flag);
					return true;
				}
			}
		} else if (preference == passwordInput) {
			String text = newValue.toString();
			XLog.i("new value=" + text);
			if (text == null || text.trim().equals("")) {
				Toast.makeText(getActivity(), R.string.parent_password_null, Toast.LENGTH_SHORT).show();
			} else if (text.length() < 5) {
				Toast.makeText(getActivity(), R.string.parent_password_length, Toast.LENGTH_SHORT).show();
			} else {
				mLock.updatePassword(text);
				Toast.makeText(getActivity(), R.string.parent_password_success, Toast.LENGTH_SHORT).show();
			}

		}
		return false;
	}
}
