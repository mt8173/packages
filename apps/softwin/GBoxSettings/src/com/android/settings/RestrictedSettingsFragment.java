/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings;

import java.util.HashSet;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.UserManager;
import android.preference.CheckBoxPreference;
import android.preference.Preference;

/**
 * Base class for settings activities that should be pin protected when in restricted mode.
 * The constructor for this class will take the restriction key that this screen should be
 * locked by.  If {@link UserManager.hasRestrictionsPin()} and
 * {@link UserManager.hasUserRestriction(String)} returns true for the restriction key, then
 * the user will have to enter the restrictions pin before seeing the Settings screen.
 *
 * If this settings screen should be pin protected whenever
 * {@link UserManager.hasUserRestriction(String)} returns true, pass in
 * {@link RESTRICTIONS_PIN_SET} to the constructor instead of a restrictions key.
 */
public class RestrictedSettingsFragment extends SettingsPreferenceFragment {

    protected static final String RESTRICTIONS_PIN_SET = "restrictions_pin_set";

    private static final String EXTRA_PREFERENCE = "pref";
    private static final String EXTRA_CHECKBOX_STATE = "isChecked";
    // Should be unique across all settings screens that use this.
    private static final int REQUEST_PIN_CHALLENGE = 12309;

    private static final String KEY_CHALLENGE_SUCCEEDED = "chsc";
    private static final String KEY_CHALLENGE_REQUESTED = "chrq";
    private static final String KEY_RESUME_ACTION_BUNDLE = "rsmb";

    // If the restriction PIN is entered correctly.
    private boolean mChallengeSucceeded;
    private boolean mChallengeRequested;
    private Bundle mResumeActionBundle;

    private UserManager mUserManager;

    private final String mRestrictionKey;

    private final HashSet<Preference> mProtectedByRestictionsPrefs = new HashSet<Preference>();



    /**
     * @param restrictionKey The restriction key to check before pin protecting
     *            this settings page. Pass in {@link RESTRICTIONS_PIN_SET} if it should
     *            be PIN protected whenever a restrictions pin is set. Pass in
     *            null if it should never be PIN protected.
     */
    public RestrictedSettingsFragment(String restrictionKey) {
        mRestrictionKey = restrictionKey;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        mUserManager = (UserManager) getSystemService(Context.USER_SERVICE);

        if (icicle != null) {
            mChallengeSucceeded = icicle.getBoolean(KEY_CHALLENGE_SUCCEEDED, false);
            mChallengeRequested = icicle.getBoolean(KEY_CHALLENGE_REQUESTED, false);
            mResumeActionBundle = icicle.getBundle(KEY_RESUME_ACTION_BUNDLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(KEY_CHALLENGE_REQUESTED, mChallengeRequested);
        if (mResumeActionBundle != null) {
            outState.putBundle(KEY_RESUME_ACTION_BUNDLE, mResumeActionBundle);
        }
        if (getActivity().isChangingConfigurations()) {
            outState.putBoolean(KEY_CHALLENGE_SUCCEEDED, mChallengeSucceeded);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mChallengeSucceeded = false;

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PIN_CHALLENGE) {
            Bundle resumeActionBundle = mResumeActionBundle;
            mResumeActionBundle = null;
            mChallengeRequested = false;
            if (resultCode == Activity.RESULT_OK) {
                mChallengeSucceeded = true;
                String prefKey = resumeActionBundle == null ?
                        null : resumeActionBundle.getString(EXTRA_PREFERENCE);
                if (prefKey != null) {
                    Preference pref = findPreference(prefKey);
                    if (pref != null) {
                        // Make sure the checkbox state is the same as it was when we launched the
                        // pin challenge.
                        if (pref instanceof CheckBoxPreference
                                && resumeActionBundle.containsKey(EXTRA_CHECKBOX_STATE)) {
                            boolean isChecked =
                                    resumeActionBundle.getBoolean(EXTRA_CHECKBOX_STATE, false);
                            ((CheckBoxPreference)pref).setChecked(isChecked);
                        }
                        if (!onPreferenceTreeClick(getPreferenceScreen(), pref)) {
                            Intent prefIntent = pref.getIntent();
                            if (prefIntent != null) {
                                pref.getContext().startActivity(prefIntent);
                            }
                        }
                    }
                }
            } else if (!isDetached()) {
                finishFragment();
            }
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }





   protected boolean hasChallengeSucceeded() {
       return mChallengeSucceeded;
   }





    /**
     * Call this with any preferences that should require the PIN to be entered
     * before they are accessible.
     */
   protected void protectByRestrictions(Preference pref) {
       if (pref != null) {
           mProtectedByRestictionsPrefs.add(pref);
       }
   }

   protected void protectByRestrictions(String key) {
       Preference pref = findPreference(key);
       protectByRestrictions(pref);
   }
}
