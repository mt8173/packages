package com.softwin.gbox.home.setting;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.softwin.gbox.home.R;
import com.softwin.gbox.home.StaticVar;
import com.xin.util.XLog;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class SystemInfoSetting extends Activity{
	private static final String FILENAME_PROC_VERSION = "/proc/version";
	private TextView mModelView,mAndroidVersionView,mSystemVersionView,mKernelVersionView,mVersionIdView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		String CPU = "MT8176";
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_systeminfo);
		mModelView=(TextView) findViewById(R.id.setting_systeminfo_model);
		mAndroidVersionView=(TextView) findViewById(R.id.setting_systeminfo_androidversion);
		mSystemVersionView=(TextView) findViewById(R.id.setting_systeminfo_hard_version);
		mKernelVersionView=(TextView) findViewById(R.id.setting_systeminfo_kernel_version);
		mVersionIdView=(TextView) findViewById(R.id.setting_systeminfo_version);
		mModelView.setText(Build.MODEL);
		mAndroidVersionView.setText(Build.VERSION.RELEASE);
		mSystemVersionView.setText(CPU);
		mKernelVersionView.setText(getFormattedKernelVersion());
		mVersionIdView.setText(Build.DISPLAY);
		if(!StaticVar.self().mShowOperationTip){
			View v=findViewById(R.id.systeminfo_tip);
			v.setVisibility(View.GONE);
		}
		
	}
    public static String getFormattedKernelVersion() {
        try {
            return formatKernelVersion(readLine(FILENAME_PROC_VERSION));

        } catch (IOException e) {
            XLog.e(
                "IO Exception when getting kernel version for Device Info screen",
                e);

            return "Unavailable";
        }
    }
    private static String readLine(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename), 256);
        try {
            return reader.readLine();
        } finally {
            reader.close();
        }
    }


    public static String formatKernelVersion(String rawKernelVersion) {
        // Example (see tests for more):
        // Linux version 3.0.31-g6fb96c9 (android-build@xxx.xxx.xxx.xxx.com) \
        //     (gcc version 4.6.x-xxx 20120106 (prerelease) (GCC) ) #1 SMP PREEMPT \
        //     Thu Jun 28 11:02:39 PDT 2012

        final String PROC_VERSION_REGEX =
            "Linux version (\\S+) " + /* group 1: "3.0.31-g6fb96c9" */
            "\\((\\S+?)\\) " +        /* group 2: "x@y.com" (kernel builder) */
            "(?:\\(gcc.+? \\)) " +    /* ignore: GCC version information */
            "(#\\d+) " +              /* group 3: "#1" */
            "(?:.*?)?" +              /* ignore: optional SMP, PREEMPT, and any CONFIG_FLAGS */
            "((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)"; /* group 4: "Thu Jun 28 11:02:39 PDT 2012" */

        Matcher m = Pattern.compile(PROC_VERSION_REGEX).matcher(rawKernelVersion);
        if (!m.matches()) {
        	XLog.e("Regex did not match on /proc/version: " + rawKernelVersion);
            return "Unavailable";
        } else if (m.groupCount() < 4) {
        	XLog.e( "Regex match on /proc/version only returned " + m.groupCount()
                    + " groups");
            return "Unavailable";
        }
        return m.group(1) + "\n" +                 // 3.0.31-g6fb96c9
            m.group(2) + " " + m.group(3) + "\n" + // x@y.com #1
            m.group(4);                            // Thu Jun 28 11:02:39 PDT 2012
    }

}
