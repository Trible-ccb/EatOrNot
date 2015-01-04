package com.android.eatingornot.widget;


import com.android.eatingornot.tool.Tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TableLayout.LayoutParams;

public class ImageViewWithText {

	int imgWidthIndp;
	Context mContext;
	public ImageView bgImg;
	public TextView imgDesp;
	public ImageView imgFit;
	public RelativeLayout textLayout;

	public ImageViewWithText(Context c){
		mContext = c;
	}
	public void setImageAndText(int resId,String s,float itemWidthPersentBySceneWidth){
		bgImg.setTag(resId);
		Bitmap visualBmp =Tool.getScaleImageByScaleOfWinWidth(
				mContext, resId, itemWidthPersentBySceneWidth) ;
		setImgAndText(visualBmp,s);
	}
	public void setImageAndText(Bitmap bm,String s,float itemWidthPersentBySceneWidth){
//		bgImg.setTag(resId);
		Bitmap visualBmp =Tool.getScaleImageByScaleOfWinWidth(
				mContext, bm, itemWidthPersentBySceneWidth) ;
		setImgAndText(visualBmp,s);
	}
	public void setImageAndText(Drawable d,String s,float itemWidthPersentBySceneWidth){
//		bgImg.setTag(resId);
		Bitmap bm = ((BitmapDrawable)d).getBitmap();
		Bitmap visualBmp =Tool.getScaleImageByScaleOfWinWidth(
				mContext, bm, itemWidthPersentBySceneWidth) ;
		setImgAndText(visualBmp,s);
	}
	public void setImgAndText(Bitmap bm,String s){
		imgDesp.setText(s);
		if (bm == null)return;
		bgImg.setImageBitmap(bm);
		int visualBmpW = bm.getWidth();
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				visualBmpW, LayoutParams.WRAP_CONTENT);
		lp.setMargins(
				0, -(int)(imgDesp.getTextSize() * 1.5f), 0, 0);
//		imgDesp.setLayoutParams(lp);

	}
}
