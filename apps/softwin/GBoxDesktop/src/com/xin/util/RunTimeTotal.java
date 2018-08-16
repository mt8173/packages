package com.xin.util;

public class RunTimeTotal {
	private String tag;
	private long start;
	private long next1;
	private long next2;

	public RunTimeTotal(String tag) {
		super();
		this.tag = tag;
		start();
	}
	private void start(){
		this.start=System.currentTimeMillis();
		this.next1=this.start;
	}
	public void next(){
		this.next2=System.currentTimeMillis();
		XLog.i(tag+" next:"+(this.next2-this.next1)+"ms.");
		this.next1=this.next2;
	}
	public void next(Object index){
		this.next2=System.currentTimeMillis();
		XLog.i(tag+"("+index+") next:"+(this.next2-this.next1)+"ms.");
		this.next1=this.next2;
	}
	public void total(){
		XLog.i(tag+" total:"+(System.currentTimeMillis()-this.start)+"ms.");
	}
}
