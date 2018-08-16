package com.softwin.gamepad;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.widget.RadioButton;
import android.widget.RadioGroup;


public class MainActivity extends Activity {

	 public RadioGroup mRadioGroup1;
	 public RadioButton mRadio_ps3, mRadio_xbox, mRadio_cf;
	 public RadioGroup mRadioGroup2;
	 public RadioButton mRadio_exchange, mRadio_default;
	 //XD
	 public static final String sControlMode="/sys/devices/soc/soc:joystick@/mode";
	 public static final String sControlExchange="/sys/devices/soc/soc:joystick@/exchange";
	 //Q9
	 //public static final String sControlMode="/sys/devices/joystick.22/mode";
	 //public static final String sControlExchange="/sys/devices/joystick.22/exchange";
	 //for Q88PSV,G7A
	 //public static final String sControlMode="/sys/devices/platform/mx-adcjs.0/mode";
	 //public static final String sControlExchange="/sys/devices/platform/mx-adcjs.0/exchange";
	 public static String value="0";
	 
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	        
	        mRadioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
	        mRadio_ps3 = (RadioButton) findViewById(R.id.radio_ps3);
	        mRadio_xbox = (RadioButton) findViewById(R.id.radio_xbox);
	        mRadio_cf = (RadioButton) findViewById(R.id.radio_cf);
	        mRadioGroup1.setOnCheckedChangeListener(radiogpchange_mode);

	        String Mode = getMode(this);	        	
	        if("CF".equals(Mode)?true:false){
	        	mRadio_xbox.setChecked(false);
	        	mRadio_ps3.setChecked(false);
	        	mRadio_cf.setChecked(true);
	        	//System.out.println(">>>>>> CF");
	        }else if ("PS3".equals(Mode)?true:false){
	        	mRadio_xbox.setChecked(false);
	        	mRadio_ps3.setChecked(true);
	        	mRadio_cf.setChecked(false);
	        	//System.out.println(">>>>>> PS3");
	        }else{
	        	mRadio_xbox.setChecked(true);
	        	mRadio_ps3.setChecked(false);
	        	mRadio_cf.setChecked(false);
	        	//System.out.println(">>>>>> XBOX");
	        }
	        
	        	        
	        mRadioGroup2 = (RadioGroup) findViewById(R.id.radioGroup2);
	        mRadio_default = (RadioButton) findViewById(R.id.radio_default);
	        mRadio_exchange = (RadioButton) findViewById(R.id.radio_exchange);
	        mRadioGroup2.setOnCheckedChangeListener(radiogpchange_exchange);

	        if(getExchange(this)){
	        	mRadio_default.setChecked(true);
	        	mRadio_exchange.setChecked(false);
	        }else{
	        	mRadio_default.setChecked(false);
	        	mRadio_exchange.setChecked(true);
	        }
	        
	    }
	//PS3 = 0
    //XBOX = 1
    //CF = 2

    public static void write_mode(String Mode){
        File file=new File(sControlMode);
        String str="0";
    	if ("PS3".equals(Mode)?true:false)
    		str="0";
    	else if ("CF".equals(Mode)?true:false)
    		str="2";
    	else
    		str="1";
        write(file, str);
    }
    
    public static void write_exchange(boolean exchange){
        File file=new File(sControlExchange);
        String str=(exchange?"1":"0");
        write(file, str);
    }
    
    public static void write(File file,String str){
        if(str==null)return;
        byte[] buffer=str.getBytes();
        OutputStream output=null;
        try {
                output=new FileOutputStream(file);
                output.write(buffer, 0, buffer.length);
                output.flush();
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }finally{
                if(output!=null){
                        try {
                                output.close();
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                }
        }
    }
    
   
    public void store_mode(String Mode){ 
    	SharedPreferences mySharedPreferences= getSharedPreferences("mode", Activity.MODE_PRIVATE);  
    	SharedPreferences.Editor editor = mySharedPreferences.edit(); 
    	if("XBOX".equals(Mode)?true:false)
    		editor.putString("mode", "XBOX");
    	else if ("PS3".equals(Mode)?true:false)
    		editor.putString("mode", "PS3");
    	else if ("CF".equals(Mode)?true:false)
    		editor.putString("mode", "CF");
    	editor.commit();
    }
    
    public void store_exchange(boolean exchange){ 
    	SharedPreferences mySharedPreferences= getSharedPreferences("exchange", Activity.MODE_PRIVATE);  
    	SharedPreferences.Editor editor = mySharedPreferences.edit(); 
    	if(exchange)
    		editor.putString("exchange", "default");
    	else
    		editor.putString("exchange", "exchange");
    	editor.commit();
    }

    
    public static String getMode(Context ctx){ 
    	SharedPreferences mySharedPreferences= ctx.getSharedPreferences("mode", Activity.MODE_PRIVATE);  
    	String modeString= mySharedPreferences.getString("mode", null);
    	//return "PS3".equals(modeString)?false:true;
    	return modeString;
    }

    public static boolean getExchange(Context ctx){ 
    	SharedPreferences mySharedPreferences= ctx.getSharedPreferences("exchange", Activity.MODE_PRIVATE);  
    	String exchangeString= mySharedPreferences.getString("exchange", null);
    	return "exchange".equals(exchangeString)?false:true;
    }
    
    private RadioGroup.OnCheckedChangeListener radiogpchange_mode = new RadioGroup.OnCheckedChangeListener() {
    	  @Override
    	  public void onCheckedChanged(RadioGroup group, int checkedId) {  
    		  if (checkedId == mRadio_xbox.getId()) {
    			  System.out.println(">>>>>> xbox");
    			  write_mode("XBOX");
    			  store_mode("XBOX");
    		  } else if (checkedId == mRadio_ps3.getId()) {
    			  System.out.println(">>>>>> PS3");
    			  write_mode("PS3");
    			  store_mode("PS3");
    		  } else if (checkedId == mRadio_cf.getId()) {
    			  System.out.println(">>>>>> CF");
    			  write_mode("CF");
    			  store_mode("CF"); 
    		  }
    	  }
    }; 
    private RadioGroup.OnCheckedChangeListener radiogpchange_exchange = new RadioGroup.OnCheckedChangeListener() {
  	  @Override
  	  public void onCheckedChanged(RadioGroup group, int checkedId) {
  		  if (checkedId == mRadio_default.getId()) {
  			  System.out.println(">>>>>> default");
  			  write_exchange(true);
  			  store_exchange(true);
  		  } else if (checkedId == mRadio_exchange.getId()) {
  			  System.out.println(">>>>>> exchange");
  			  write_exchange(false);
			  store_exchange(false);
  		  }
  	  }
  };
    
    
}
