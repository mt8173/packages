package com.softwin.gbox.home;

import com.xin.util.XLog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.WindowManager;

public class GameCenterPage extends ViewGroup{
    private static final boolean DEBUG = false;
    private int PADDING_TOP=160;
    private int PADDING_LEFT=100;

    private int ITEM_FIRST_WIDTH=318;
    private int ITEM_FIRST_HEIGHT=426;
    private int ITEM_SECOND_WIDTH=426;
    private int ITEM_SECOND_HEIGHT=211;
    private int ITEM_MIN_WIDTH=211;
    private int ITEM_MIN_HEIGHT=211; 
    private int SPACE_V=ITEM_FIRST_HEIGHT-2*ITEM_MIN_HEIGHT;
    private int SPACE_H=SPACE_V;
    private int LOOP_EACH_WIDTH=(ITEM_FIRST_WIDTH+ITEM_MIN_WIDTH*2+3*SPACE_H);
    private int SHADOW_HEIGHT=100;
    private final int LOOP_INDEX=4;
    public int count =0;
	private int type=NO_ID;
	private final boolean hasShadow = true;
	private int oldChildCount=-1;
	public GameCenterPage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public GameCenterPage(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GameCenterPage(Context context) {
		super(context);
		init();
	}
	private void init() {
		WindowManager wm=(WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		int mScreenWidth=wm.getDefaultDisplay().getHeight();
		if(mScreenWidth!=720){
			float scale=mScreenWidth*1.0f/720;
			XLog.i("mScreenWidth="+mScreenWidth+",scale="+scale);
			PADDING_TOP=(int) (PADDING_TOP*scale);
			PADDING_LEFT=(int) (PADDING_LEFT*scale);
			ITEM_MIN_WIDTH =(int) (ITEM_MIN_WIDTH*scale);
			ITEM_MIN_HEIGHT=ITEM_MIN_WIDTH;
			XLog.i("ITEM_MIN_WIDTH="+ITEM_MIN_WIDTH);
			ITEM_FIRST_WIDTH=(int) (ITEM_FIRST_WIDTH*scale);
			ITEM_FIRST_HEIGHT=(int) (ITEM_FIRST_HEIGHT*scale);
			ITEM_SECOND_WIDTH=(int) (ITEM_SECOND_WIDTH*scale);
			ITEM_SECOND_HEIGHT=ITEM_MIN_WIDTH;
			SPACE_V=ITEM_FIRST_HEIGHT-2*ITEM_MIN_HEIGHT;
			SPACE_H=SPACE_V;
			LOOP_EACH_WIDTH=(ITEM_FIRST_WIDTH+ITEM_MIN_WIDTH*2+3*SPACE_H);
			SHADOW_HEIGHT=(int) (SHADOW_HEIGHT*scale);
		}
	}
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize =  MeasureSpec.getSize(heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        if(DEBUG){
        	XLog.i("CellLayout.mode("+MeasureSpec.toString(widthMeasureSpec)+","+MeasureSpec.toString(heightMeasureSpec)+");");
        	XLog.i("CellLayout.setMeasuredDimension("+widthSpecSize+","+heightSpecSize+");");
        }
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if(DEBUG){
            	XLog.i("onMeasureChild"+((GameCenterItemView)child).getName()+"==="+child.getLayoutParams());
            }
            measureChild(child);
        }
        if(widthSpecMode ==MeasureSpec.EXACTLY){
        	setMeasuredDimension(widthSpecSize, heightSpecSize);
        }else if(widthSpecMode == MeasureSpec.UNSPECIFIED){
        	LayoutParams lp0=null;
        	for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if(lp0==null||(lp0.x+lp0.width<lp.x+lp.width)){
                	lp0=lp;
                }
            }
        	if(lp0!=null){
        		int width=lp0.x+lp0.width;
            	setMeasuredDimension(width+PADDING_LEFT*2, heightSpecSize);
        	}else{
        		setMeasuredDimension(PADDING_LEFT*2, heightSpecSize);
        	}
        	
        	
        }
    }
    
    public void measureChild(View child) {
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY);
        int childheightMeasureSpec = MeasureSpec.makeMeasureSpec(lp.height,MeasureSpec.EXACTLY);
        child.measure(childWidthMeasureSpec, childheightMeasureSpec);
    }
    public void reset(int id) {
    	count=0;
    	type = id;
    	removeAllViews();
    }
    public int getType(){
    	return type;
    }
    public boolean getShadow(){
    	return hasShadow;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    	if(DEBUG){
    		XLog.i("onLayout("+changed+","+l+","+t+","+r+","+b+")");
    	}
    	int count = getChildCount();
    	if(!changed&&oldChildCount==count){
    		return;
    	}
    	oldChildCount=count;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                int childLeft = lp.x+PADDING_LEFT;
                int childTop = lp.y+PADDING_TOP;
                child.layout(childLeft, childTop, childLeft + lp.width, childTop + lp.height);
            }
        }
    }

	@Override
	protected LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams();
	}
	


    public  class LayoutParams extends ViewGroup.MarginLayoutParams {
    	public int index;
        // X coordinate of the view in the layout.
        @ViewDebug.ExportedProperty
        int x;
        // Y coordinate of the view in the layout.
        @ViewDebug.ExportedProperty
        int y;
        boolean isShadow;
        public LayoutParams() {
        	super(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        	index=count++;
        	if(DEBUG)XLog.i("new lp,"+count+" index="+index);
        	int temp1=index%LOOP_INDEX;
        	int temp2=index/LOOP_INDEX;
        	switch(temp1){
        	case 0:
        		this.x= temp2*LOOP_EACH_WIDTH;
        		this.y= 0;
        		this.width=ITEM_FIRST_WIDTH;
        		this.height=ITEM_FIRST_HEIGHT+(hasShadow?SHADOW_HEIGHT:0);
        		isShadow=hasShadow;
            	break;
        	case 1:
        		this.x=temp2*LOOP_EACH_WIDTH+SPACE_H+ITEM_FIRST_WIDTH;
        		this.y= 0;
        		this.width=ITEM_SECOND_WIDTH;
        		this.height=ITEM_SECOND_HEIGHT;
            	break;
        	case 2:
        		this.x=temp2*LOOP_EACH_WIDTH+SPACE_H+ITEM_FIRST_WIDTH;
        		this.y=ITEM_SECOND_HEIGHT+SPACE_V;
        		this.width=ITEM_MIN_WIDTH;
        		this.height=ITEM_MIN_HEIGHT+(hasShadow?SHADOW_HEIGHT:0);
        		isShadow=hasShadow;
            	break;
        	case 3:
        		this.x=temp2*LOOP_EACH_WIDTH+SPACE_H*2+ITEM_FIRST_WIDTH+ITEM_MIN_WIDTH;
        		this.y=ITEM_SECOND_HEIGHT+SPACE_V;
        		this.width=ITEM_MIN_WIDTH;
        		this.height=ITEM_MIN_HEIGHT+(hasShadow?SHADOW_HEIGHT:0);
        		isShadow=hasShadow;
        		break;
        	}
        	if(DEBUG)XLog.i("new lp,"+toString());
        	
        }
        public LayoutParams(int x, int y, int width, int height) {
            super(width, height);
            index=count++;
            this.x = x;
            this.y = y;
        }


        public String toString() {
            return "index("+index+")=(" + this.x + ", " + this.y +"--"+width+","+height +")";
        }
		public int getShadowHeight() {
			return SHADOW_HEIGHT;
		}


    }

}
