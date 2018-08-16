package com.xin.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ReflectHelper {
	public static boolean SHOW_EXCEPTION=false;
	public ReflectHelper() {
	}

	private Class<?> mClazz;
	private Object mObject;
	private Method mMethod;
	private Object[] mMethodArgs;
	private Field mField;
	

	public void setMethodArgs(Object...args) {
		this.mMethodArgs = args;
	}

	public void setClazz(Class<?> mClazz) {
		this.mClazz = mClazz;
	}

	public void setObject(Object mObject) {
		this.mObject = mObject;
		this.mClazz=this.mObject.getClass();
	}

	public void setMethod(Method mMethod) {
		this.mMethod = mMethod;
	}
	public void setMethod(Class<?> clazz,String methodString,Class<?>...parameterTypes){
		if(clazz!=null){
			setClazz(clazz);
		}
		if(mClazz==null){
			if(mObject!=null){
				setClazz(mObject.getClass());
			}
		}
		if(mClazz==null){
			tellNullValue("setClazz");
			return;
		}
		Method method=null;
		try {
			method=mClazz.getDeclaredMethod(methodString, parameterTypes);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		if(method!=null){
			setMethod(method);
		}
	}
	
	public void setField(Field mField) {
		this.mField = mField;
	}
	public void setField(Class<?> clazz,String fieldString){
		if(clazz!=null){
			setClazz(clazz);
		}
		Field field=null;
		try {
			field=mClazz.getDeclaredField(fieldString);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		if(field!=null){
			setField(field);
		}
	}


	public void clear(){
		mClazz=null;
		mObject=null;
		mMethod=null;
		mMethodArgs=null;
		mField=null;
	}
	/**
	 * call static method request:method,[parameter]
	 * call normal method request:object,method,[parameter]
	 * We should do setMethod before do callMethod.
	 * @return method return value;
	 */
	public Object callMethod() {
		if(mMethod==null){
			tellNullValue("setMethod");
			return null;
		}
		Object returnValue;
		try {
			int modifiers = mMethod.getModifiers();
			Object object = null;
			if(!Modifier.isStatic(modifiers)){
				if(mObject==null){
					tellNullValue("setObject");
					return null;
				}else{
					object= mObject;
				}
			}
			Class<?>[] parameterTypes=mMethod.getParameterTypes();
			//log("callMethod:"+parameterTypes);
			if(parameterTypes!=null&&parameterTypes.length>0){
				if(mMethodArgs==null){
					tellNullValue("setMethodArgs");
					return null;
				}
				returnValue = mMethod.invoke(object, mMethodArgs);
			}else{
				returnValue = mMethod.invoke(object);
			}
		} catch (Exception e) {
			e.printStackTrace();
			returnValue = null;
		}
		return returnValue;
	}
	
	
	
	/**
	 * get static field request:field
	 * get normal field request:object,field
	 * We should do setField before do getField.
	 * @return field value;
	 */
	public Object getField() {
		if(mField==null){
			tellNullValue("setField");
			return null;
		}
		Object returnValue;
		try {
			int modifiers = mField.getModifiers();
			Object object = null;
			if(!Modifier.isStatic(modifiers)){
				if(mObject==null){
					tellNullValue("setObject");
					return null;
				}else{
					object= mObject;
				}
			}
			
			returnValue = mField.get(object);
		} catch (Exception e) {
			e.printStackTrace();
			returnValue = null;
		}
		return returnValue;
	}
	
	
	private void tellNullValue(String method){
		String errorMsg="please call "+method+" first!";
		if(SHOW_EXCEPTION){
			throw new RuntimeException(errorMsg);
		}else{
			log(errorMsg);
		}
		
	}
	private void log(String message){
		//System.out.println("info:"+message);
		XLog.i(message);
	}
	public static Class<?> toClass(String classString){
		try {
			return Class.forName(classString);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
