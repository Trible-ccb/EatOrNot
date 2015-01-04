package com.android.eatingornot.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class ResizeLayout extends LinearLayout {

	public interface OnResizeListner{
		void onResizeBigger();
		void onResizeSmall();
	}
	OnResizeListner mOnResizeListner;
	public ResizeLayout(Context context) {
		
		super(context);
		
	}

	public ResizeLayout(Context context,AttributeSet as){
		super(context,as);
	}
	
	public OnResizeListner getOnResizeListner() {
		return mOnResizeListner;
	}


	public void setOnResizeListner(OnResizeListner mOnResizeListner) {
		this.mOnResizeListner = mOnResizeListner;
	}


	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		if ( mOnResizeListner != null){
			if (h < oldh){
				mOnResizeListner.onResizeSmall();
			} else {
				mOnResizeListner.onResizeBigger();
			}
		}

		
	}

	
}
