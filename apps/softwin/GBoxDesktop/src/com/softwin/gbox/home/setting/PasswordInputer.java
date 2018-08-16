package com.softwin.gbox.home.setting;

import com.softwin.gbox.home.R;
import com.xin.util.XLog;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class PasswordInputer extends Fragment implements View.OnClickListener{
	public interface CheckListener{
		boolean onCheck(String password);
		void onCancel();
	}
	private View mContainer;
	private View mNumberView[] = new View[10];
	private TextView mPasswordView;
	private Button mGreenView;
	private Button mRedView;
	private String mInput="";
	private int mInputLength=6;
	private CheckListener mListener;
	private String mTitle;
	private boolean isAutoCheck=true;
	private boolean isPasswordShow=false;
	public PasswordInputer(CheckListener listener){
		mListener=listener;
	}
	public PasswordInputer(CheckListener listener,String title){
		mListener=listener;
		mTitle=title;
		isAutoCheck=false;
		isPasswordShow=true;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.mContainer = inflater.inflate(R.layout.ui_password, container, false);
		String pkg=getActivity().getPackageName();
		mPasswordView=(TextView) mContainer.findViewById(R.id.textView_password);
		mGreenView=(Button) mContainer.findViewById(R.id.textView_green);
		mRedView=(Button) mContainer.findViewById(R.id.textView_red);
		mGreenView.setOnClickListener(this);
		mRedView.setOnClickListener(this);
		if(mTitle!=null){
			TextView titleView=(TextView) mContainer.findViewById(R.id.textView_title);
			titleView.setText(mTitle);
		}
		for(int i=0;i<mNumberView.length;i++){
			int id=getResources().getIdentifier("textView_"+i, "id", pkg);
			mNumberView[i]=mContainer.findViewById(id);
			mNumberView[i].setOnClickListener(this);
		}
		return mContainer;
	}
	@Override
	public void onClick(View v) {
		v.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.ui_password_scale));
		switch (v.getId()) {
		case R.id.textView_0:
			inputNumber("0");
			break;
		case R.id.textView_1:
			inputNumber("1");
			break;
		case R.id.textView_2:
			inputNumber("2");
			break;
		case R.id.textView_3:
			inputNumber("3");
			break;
		case R.id.textView_4:
			inputNumber("4");
			break;
		case R.id.textView_5:
			inputNumber("5");
			break;
		case R.id.textView_6:
			inputNumber("6");
			break;
		case R.id.textView_7:
			inputNumber("7");
			break;
		case R.id.textView_8:
			inputNumber("8");
			break;
		case R.id.textView_9:
			inputNumber("9");
			break;
		case R.id.textView_green:
			inputOK();
			break;
		case R.id.textView_red:
			inputBack();
			break;
		default:
			break;
		}
	}
	private void inputBack() {
		int len=mInput.length();
		if(len>0){
			refreshPaswordInput(mInput.substring(0, len-1));
		}
		
	}
	private void inputOK() {
		boolean result=mListener.onCheck(mInput);
		if(result){
			return ;
		}else{
			refreshPaswordInput("");
		}
	}
	private void inputNumber(String number) {
		String input=mInput.concat(number);
		int length=input.length();
		if(length>=mInputLength){
			if(isAutoCheck){
				boolean result=mListener.onCheck(input);
				if(result){
					return ;
				}else{
					refreshPaswordInput("");
				}
			}else{
				refreshPaswordInput(input.substring(0, mInputLength));
			}
		}else{
			refreshPaswordInput(input);
		}
		
		
		
	}
	private void refreshPaswordInput(String input){
		String showString="";
		int length=input.length();
		for(int i=0;i<length;i++){
			if(isPasswordShow){
				char ch=input.charAt(i);
				if(i<mInputLength-1){
					showString=showString.concat(ch+" ");
				}else{
					showString=showString.concat(String.valueOf(ch));
				}
			}else{
				if(i<mInputLength-1){
					showString=showString.concat("* ");
				}else{
					showString=showString.concat("*");
				}
			}
		}
		for(int i=length;i<mInputLength;i++){
			if(i<mInputLength-1){
				showString=showString.concat("_ ");
			}else{
				showString=showString.concat("_");
			}
		}
		XLog.i("show len="+length+",str="+showString);
		mInput=input;
		mPasswordView.setText(showString);
	}
}
