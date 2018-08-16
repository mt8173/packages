package com.xin.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class BaseHandler extends Handler {
	private final static String TAG = BaseHandler.class.getName();
	private final static int SHOW_TOAST = 0x00;
	private static final int SET_TEXT = 0x01;
	private static final int SET_IMAGE = 0x02;
	private static final int SET_BG = 0x03;
	private static final int UPDATE_LIST = 0x04;
	private static final int SET_VISIBILITY = 0x05;
	public static final int WHAT_FIRST = 100;
	private Context context;

	public BaseHandler(Context context) {
		super();
		this.context = context;
	}

	@Override
	public void handleMessage(android.os.Message msg) {
		switch (msg.what) {
		case SHOW_TOAST:
			if (msg.obj instanceof CharSequence) {
				Toast.makeText(context, (CharSequence) msg.obj, Toast.LENGTH_SHORT).show();
			} else if (msg.obj instanceof Integer) {
				Toast.makeText(context, (Integer) msg.obj, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, msg.obj.toString(), Toast.LENGTH_SHORT).show();
			}
			break;
		case SET_TEXT:
			setText(msg.obj);
			break;
		case SET_IMAGE:
			setImage(msg.obj);
			break;
		case SET_BG:
			setBackground(msg.obj);
			break;
		case UPDATE_LIST:
			if (msg.obj != null && msg.obj instanceof BaseAdapter) {
				((BaseAdapter) (msg.obj)).notifyDataSetChanged();
			}
			break;
		case SET_VISIBILITY:
			setVisibility(msg.obj);
			break;
		default:
		}
	};

	private void setText(Object obj) {
		if (obj != null && obj instanceof Object[]) {
			Object[] para = (Object[]) obj;
			if (para.length == 2 && para[0] != null && para[1] != null && para[0] instanceof TextView) {
				TextView textView = (TextView) para[0];
				if (para[1] instanceof Integer) {
					textView.setText((Integer) para[1]);
				} else {
					textView.setText(para[1].toString());
				}
			} else {
				Log.e(TAG, "setText:all parameters not are null,para0 must be TextView");
			}
		} else {
			Log.e(TAG, "setText:object must be new Object[]{para1,para2}");
		}
	}

	private void setImage(Object obj) {
		if (obj != null && obj instanceof Object[]) {
			Object[] para = (Object[]) obj;
			if (para.length == 2 && para[0] != null && para[1] != null && para[0] instanceof ImageView) {
				ImageView view = (ImageView) para[0];
				if (para[1] instanceof Integer) {
					view.setImageResource((Integer) para[1]);
				} else if (para[1] instanceof Bitmap) {
					view.setImageBitmap((Bitmap) para[1]);
				} else if (para[1] instanceof Drawable) {
					view.setImageDrawable((Drawable) para[1]);
				} else if (para[1] instanceof Uri) {
					view.setImageURI((Uri) para[1]);
				} else {
					Log.e(TAG, "setImage:para2 must be Integer,Bitmap,Drawable and Uri");
				}
			} else {
				Log.e(TAG, "setImage:all parameters not are null,para0 must be ImageView");
			}
		} else {
			Log.e(TAG, "setImage:object must be new Object[]{para1,para2}");
		}

	}

	private void setBackground(Object obj) {
		if (obj != null && obj instanceof Object[]) {
			Object[] para = (Object[]) obj;
			if (para.length == 2 && para[0] != null && para[1] != null && para[0] instanceof View) {
				View view = (View) para[0];
				if (para[1] instanceof Integer) {
					view.setBackgroundResource((Integer) para[1]);
				} else if (para[1] instanceof Drawable) {
					view.setBackgroundDrawable((Drawable) para[1]);
				} else if (para[1] instanceof CharSequence) {
					try {
						int color = Integer.parseInt(para[1].toString());
						view.setBackgroundColor(color);
					} catch (Exception e) {
						view.setBackgroundColor(0xff000000);
					}
				} else {
					Log.e(TAG, "setBackground:para2 must be Integer,Drawable and Uri");
				}
			} else {
				Log.e(TAG, "setBackground:all parameters not are null,para0 must be View");
			}
		} else {
			Log.e(TAG, "setBackground:object must be new Object[]{para1,para2}");
		}

	}

	private void setVisibility(Object obj) {
		if (obj != null && obj instanceof Object[]) {
			Object[] para = (Object[]) obj;
			if (para.length == 2 && para[0] != null && para[1] != null && para[0] instanceof View && para[1] instanceof Integer) {
				View view = (View) para[0];
				int v = (Integer) para[1];
				if (v == View.GONE || v == View.VISIBLE || v == View.INVISIBLE) {
					view.setVisibility(v);
				} else {
					view.setVisibility(View.VISIBLE);
				}
			} else {
				Log.e(TAG, "setVisibility:all parameters not are null,para0 must be View,para1 must be Integer");
			}
		} else {
			Log.e(TAG, "setVisibility:object must be new Object[]{View,Integer}");
		}
	}

	public void showToast(Object obj) {
		if (obj == null) {
			obj = "null";
		}
		this.sendMessage(this.obtainMessage(SHOW_TOAST, obj));
	}

	public void setText(TextView view, Object obj) {
		if (view == null || obj == null) {
			Log.e(TAG, "all parameters not are null!");
		} else {
			this.sendMessage(this.obtainMessage(SET_TEXT, new Object[] { view, obj }));
		}
	}

	public void setImage(ImageView view, Object obj) {
		if (view == null || obj == null) {
			Log.e(TAG, "all parameters not are null!");
		} else {
			this.sendMessage(this.obtainMessage(SET_IMAGE, new Object[] { view, obj }));
		}
	}

	public void setBackground(View view, Object obj) {
		if (view == null || obj == null) {
			Log.e(TAG, "all parameters not are null!");
		} else {
			this.sendMessage(this.obtainMessage(SET_BG, new Object[] { view, obj }));
		}
	}

	public void setVisible(View view, int visible) {
		if (view == null) {
			Log.e(TAG, "all parameters not are null!");
		} else {
			this.sendMessage(this.obtainMessage(SET_VISIBILITY, new Object[] { view, visible }));
		}
	}

	public void showView(View view) {
		setVisible(view, View.VISIBLE);
	}

	public void hideView(View view) {
		setVisible(view, View.GONE);
	}

	public void update(BaseAdapter adapter) {
		if (adapter == null) {
			Log.e(TAG, "all parameters not are null!");
		} else {
			this.sendMessage(this.obtainMessage(UPDATE_LIST, adapter));
		}
	}

	public void update(AdapterView<ListAdapter> view) {
		ListAdapter adapter = view.getAdapter();
		if (adapter instanceof BaseAdapter) {
			update((BaseAdapter) adapter);
		}
	}
}
