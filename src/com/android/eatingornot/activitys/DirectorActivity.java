/**
 * 
 */
package com.android.eatingornot.activitys;



import java.util.ArrayList;

import ccb.java.android.utils.LogWorker;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * @author Administrator
 *
 */
public class DirectorActivity extends Activity {
	
	ViewPager mDirectorsPaper;
	ArrayList<View> mDirectorViews;
	
	int[] mDirectorImager;
	
	LayoutInflater mInflater;
	
	@TargetApi(Build.VERSION_CODES.ECLAIR)
	public static void displayMyself(Activity from){
		Intent intent = new Intent(from,DirectorActivity.class);
		from.startActivity(intent);
        from.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}
	
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.director);
	        mInflater = getLayoutInflater();
	        
	        mDirectorsPaper = (ViewPager) findViewById(R.id.view_papers_directors);
	        mDirectorsPaper.setAdapter(new DirectAdater());
	        mDirectorsPaper.setOnPageChangeListener(new OnPageChangeListener() {
				@Override
				public void onPageSelected(int arg0) {
					if (arg0 == mDirectorImager.length - 1){
						EattingOrNotMainActivity.displayMyself(DirectorActivity.this,false);
						finish();
					}
				}
				
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					
				}
				
				@Override
				public void onPageScrollStateChanged(int arg0) {
					
				}
			});
	    }
		
		class DirectAdater extends PagerAdapter{
			
			public DirectAdater(){
				mDirectorImager = new int[]{R.drawable.direc1,
						R.drawable.direc2,
						R.drawable.direc3,
						-1};
				mDirectorViews = new ArrayList<View>();
				
				for (int i = 0 ; i < mDirectorImager.length; i++){
					View v = mInflater.inflate(R.layout.director_paper_item, null);
					mDirectorViews.add(v);
				}
			}
			@Override
			public int getCount() {
//				LogWorker.e("views:" + mDirectorImager.length);
				return mDirectorImager.length;
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				
				return arg0 == arg1;
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
//				LogWorker.e("destroyItem at :" + position);
				if (position < mDirectorViews.size()){
					((ViewPager)container).removeView(mDirectorViews.get(position));
				}
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
//				LogWorker.e("instantiateItem at :" + position);
				View v = mDirectorViews.get(position);
				ImageView iv = (ImageView) v.findViewById(R.id.director_img);
				if (mDirectorImager[position] != -1){
					iv.setBackgroundResource(mDirectorImager[position]);
				}
				((ViewPager)container).addView(v);
				return v;
			}
			
		}
}
