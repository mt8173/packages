package com.android.settings.deviceinfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.android.settings.R;
import android.content.Context;
import android.content.res.Resources;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.Log;
import android.os.StatFs;
import android.os.SystemProperties;
import com.android.internal.util.MemInfoReader;
import android.text.format.Formatter;

public class HardWareInfoHelp
{

    public static List<HashMap<String, String>> getCpuInfo()
    {
        String str1 = "/proc/cpuinfo";
		//for mtk
		String strCore = "/sys/devices/system/cpu/present";
		//end
        ArrayList<HashMap<String, String>> cpuInfo = new ArrayList<HashMap<String, String>>();
        String[] arrayOfString;
        try
        {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr);
            String line = localBufferedReader.readLine();
			int count = 0;
            while (line != null)
            {
				Log.i("HardWareInfo","line:"+line);
                arrayOfString = line.split(":");
                if (arrayOfString.length >= 2)
                {
                    if("Hardware".equals(arrayOfString[0].trim()))
                    {
                        HashMap<String, String> model = new HashMap<String, String>();
                        //model.put("model name", arrayOfString[1].trim());
                        model.put("model name", "MT8176(2×Cortex-A72@2.1GHz&4×CortexA53@1.7GHz)");
                        cpuInfo.add(model);
						break; 
                    }
                    Log.i("HardWareInfo", arrayOfString[0].trim() + " : " + arrayOfString[1]);
                } else
                {
                    Log.i("HardWareInfo", arrayOfString[0].trim() + " : " + "null");
                }
                line = localBufferedReader.readLine();
				
            }
            localBufferedReader.close();
        } catch (IOException e)
        {
        }
		
		try
        {
            FileReader fr2 = new FileReader(strCore);
            BufferedReader localBufferedReader2 = new BufferedReader(fr2);
            String line2 = localBufferedReader2.readLine();
            while (line2 != null)
            {
                arrayOfString = line2.split("-");
                if (arrayOfString.length >= 2)
                {
                    HashMap<String, String> model2 = new HashMap<String, String>();
                    model2.put("cpu cores", "" +(Integer.parseInt(arrayOfString[1].trim())+1));
                    cpuInfo.add(model2);
					Log.i("HardWareInfo", arrayOfString[0].trim() + " : " + arrayOfString[1]);
					break;
                }
            }
            localBufferedReader2.close();
        } catch (IOException e)
        {
        }
		
        return cpuInfo;
    }

    public static List<String> getCameraInfo(Context context)
    {

        int cameraCount = Camera.getNumberOfCameras();
        Log.i("HardWareInfo","the cameraCount is : "+cameraCount);
        ArrayList<String> cameraInfo = null;
        if(cameraCount > 0)
        {
            cameraInfo = new ArrayList<String>();
        }

        Resources res = context.getResources();
        String[] pictureSizes = res.getStringArray(R.array.pref_camera_picturesize_entryvalues);
        String[] pictureSizesName = res.getStringArray(R.array.pref_camera_picturesize_entries);
        for(int i = 0; i < cameraCount; i ++)
        {
            try {
                Camera camera = Camera.open(i);
                Parameters parameters = camera.getParameters();
                List<Size> supportedPictureSizes = parameters.getSupportedPictureSizes();

                int supportedPictureSizesCount = supportedPictureSizes.size();
                Size maxSize = supportedPictureSizes.get(supportedPictureSizesCount-1);
                String maxSizeString = maxSize.width + "x" + maxSize.height;
                Log.i("HardWareInfo","maxSizeString = "+maxSizeString);

                for(int j = 0; j < pictureSizes.length; j ++)
                {
                    if(maxSizeString.equals(pictureSizes[j]))
                    {
                        cameraInfo.add(pictureSizesName[j]);
                        break;
                    }
                }
                camera.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //Log.i("HardWareInfo", cameraInfo.get(0) + " " + cameraInfo.get(1));
        return cameraInfo;
    }


    public static List<String> getStorageInfo(Context context)
    {
        ArrayList<String> storageInfo = new ArrayList<String>();
        MemInfoReader mMemInfoReader;
        StatFs statfs = new StatFs("/storage/sdcard0");
        mMemInfoReader = new MemInfoReader();
        mMemInfoReader.readMemInfo();
        long mRamSize = mMemInfoReader.getTotalSize();
        Log.i("xiao","ss:" + mRamSize);
        //String mRamSizeStr = Formatter.formatShortFileSize(context,mRamSize);
        long nTotalBlocks = statfs.getBlockCount();
        long nBlocSize = statfs.getBlockSize();
        long mFlashTotalSize = nTotalBlocks * nBlocSize;
        float result = ((float)mFlashTotalSize)/1024/1024/1024;
        String mFlashSizeStr = "";
        if (SystemProperties.get("ro.wisky.flashsize", "-1").equals("-1")){
            if(3<result && result< 5)        mFlashSizeStr = "8.0";
            if(5<result && result<8)        mFlashSizeStr = "10.0";
            if(8<result && result<12)        mFlashSizeStr = "16.0";
            if(12<result && result<20)        mFlashSizeStr = "20.0";
            if(result > 20)                 mFlashSizeStr = "32.0";
            if(result > 32)                 mFlashSizeStr = "64.0";
            if(result > 64)                 mFlashSizeStr = "128.0";
        } else {
            mFlashSizeStr += SystemProperties.get("ro.wisky.flashsize", "-1");
        }
        mFlashSizeStr += " GB";
        float ramSize = ((float)mRamSize)/1024/1024/1024;
        String mRamSizeStr = "";
        if (1 < ramSize && ramSize < 2)
            mRamSizeStr = "2.0 GB";
        else if (2 < ramSize && ramSize < 3)
            mRamSizeStr = "3.0 GB";
        else if (3 < ramSize && ramSize < 4)
            mRamSizeStr = "4.0 GB";
        else mRamSizeStr = Formatter.formatShortFileSize(context,mRamSize);
        storageInfo.add(mRamSizeStr);
        storageInfo.add(mFlashSizeStr);

        return storageInfo;
    }



}