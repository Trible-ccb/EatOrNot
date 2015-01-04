/**
 * 
 */
package com.android.eatingornot.activitys;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ccb.android.net.framkwork.HttpLoader;
import ccb.android.net.framkwork.HttpLoader.CacheHelper;
import ccb.android.net.framkwork.ImageLoader;
import ccb.android.net.framkwork.HttpLoader.Task;
import ccb.android.net.framkwork.HttpLoader.dataParser;
import ccb.android.net.framkwork.HttpLoader.downLoadListener;
import ccb.android.net.framkwork.ImageLoader.ImageLoaderListener;
import ccb.android.net.framkwork.SimpleAsynHttpDowload;
import ccb.java.android.utils.FileUtils;
import ccb.java.android.utils.LogWorker;
import ccb.java.android.utils.StorageManager;

import com.android.eatingornot.datamodel.Food;
import com.android.eatingornot.datapraser.HttpDataPraser;
import com.android.eatingornot.httpparams.AllFoodOfDiseaseParams;
import com.android.eatingornot.httpparams.HotFoodParams;
import com.android.eatingornot.service.DieseaseWithFoodService;
import com.android.eatingornot.service.FoodService;
import com.android.eatingornot.widget.CustomNaviBar;
import com.android.eatingornot.widget.ImageViewWithText;
import com.android.eatingornot.widget.ResizeLayout;
import com.android.eatingornot.widget.ResizeLayout.OnResizeListner;
import com.umeng.analytics.MobclickAgent;

import android.R.integer;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.util.LruCache;
import android.support.v4.view.ViewPager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
/**
 * @author陈垂波
 *
 */
public class SeachFoodsActivity extends Activity {
	TextView titleName,textHotFood;
	Button btnBack,btnDefault;
	Button btnSearch;
//	EditText edtContent;
	AutoCompleteTextView actvContent;
	ProgressDialog dialog;
	ResizeLayout mResizeLayout;
	LinearLayout dotsLayout;
	LinearLayout searchBarBg;
	ImageButton overLayout;
	ViewPager viewPapers;
	ViewPaperAdater viewPaperAdater;
	
	CustomNaviBar mNaviBar;
	
	ArrayList<Food> mHotFoods;
	static HashMap<String, ArrayList<Food>> mHotFoodsOfAllDisease;
	ArrayList<View> mGirdViewList;
	
	static  HashMap<String, Integer> CurrentPageIdx;
	int gridViews;
	int imgsPerPage;
	int maxPageNum = 5;
	boolean hadDownLoad;

	InputMethodManager inputMethodManager;
	ACTVAdater mACTVAdater;

	private static Food curSelFood;
	String sickName;
	static String curShowedDisease;
	ArrayList<Food> mAllFoods;
	private Task hotFoodTask;
	protected DownLoadFlg downloadFlg;
	public static String SICK_NAME = "sick_name";
	
	public static Food getCurFood(){
		if (curSelFood == null){
			curSelFood = new Food();
		}
		return curSelFood;
	}
	public static void setCurFood(Food f){
		
		curSelFood = f;
	}
	private void initViewsPerPage(){
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		LogWorker.i("heightPixels:" + mDisplayMetrics.heightPixels);
		if (mDisplayMetrics.heightPixels > 1000){
			imgsPerPage = 12;
		}else if (mDisplayMetrics.heightPixels > 800){
			imgsPerPage = 12;
		} else if (mDisplayMetrics.heightPixels > 600){
			imgsPerPage = 9;
		} else {
			imgsPerPage = 6;
		}
	}
	
	@TargetApi(Build.VERSION_CODES.ECLAIR)
	public static void displayMyself(Activity from){
		Intent intent = new Intent(from,SeachFoodsActivity.class);
		from.startActivity(intent);
        from.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//        from.finish();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MobclickAgent.onError(this);
		UserLoger.upload(this, getText(R.string.SearchFood).toString(), getText(R.string.action_setup).toString());
		LogWorker.i("onCreate");
		
		inputMethodManager =
	        (InputMethodManager)getSystemService(
	                Context.INPUT_METHOD_SERVICE);
		mInflater = getLayoutInflater();
		setContentView(R.layout.search_foods);
		initViewsPerPage();
		
		sickName = EattingOrNotMainActivity.getSickName();
		
		if (mHotFoodsOfAllDisease == null ){
			mHotFoodsOfAllDisease = new HashMap<String, ArrayList<Food>>();
		}
		if (CurrentPageIdx == null){
			CurrentPageIdx = new HashMap<String, Integer>();
		}
		mHotFoods = new ArrayList<Food>();
		//异步处理
		LogWorker.i("getAllFoodForACTV s");
//		mOriginData = mSearchControl.getAllFoodForACTV();
//		new getACTVDataTask().execute("");
		doDownloadALLFood();
		LogWorker.i("getAllFoodForACTV e");
		initView();

	}
	
	@TargetApi(Build.VERSION_CODES.CUPCAKE)
	private void initView(){
		LogWorker.i("initView start.");
		onCreateDialog(1);
		mNaviBar = new CustomNaviBar(this, 0);
		
		titleName = (TextView) findViewById(R.id.title_text);
		btnBack = (Button) findViewById(R.id.title_btn_left);
		btnDefault = (Button) findViewById(R.id.title_btn_right);
		btnSearch = (Button) findViewById(R.id.btn_search);
//		edtContent = (EditText) findViewById(R.id.edit_text);
		textHotFood = (TextView)findViewById(R.id.text_hot_food);
		actvContent = (AutoCompleteTextView) findViewById(R.id.auto_cpl_text_view);
		dotsLayout = (LinearLayout) findViewById(R.id.dots);
		viewPapers = (ViewPager) findViewById(R.id.view_papers);
		
		overLayout = (ImageButton) findViewById(R.id.over_button);
		searchBarBg = (LinearLayout) findViewById(R.id.search_bar_bg);
		
		mResizeLayout = (ResizeLayout) findViewById(R.id.resize_layout);
		mResizeLayout.setOnResizeListner(new OnResizeListner() {
			
			@Override
			public void onResizeSmall() {
				//说明输入法显示
				LogWorker.i("onResizeSmall");
			}
			
			@Override
			public void onResizeBigger() {
				LogWorker.i("onResizeBigger");
				//说明输入法隐藏
				onLight();
			}
		});
		OnClickListener lstner = new OnClickListener() {			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.title_btn_left:
					onBack();
					break;
				case R.id.title_btn_right:
					onDefault();
					break;
				case R.id.btn_search:
					onSearchButton();
					break;
				case R.id.over_button:
					onLight();
					break;
				default:
					break;
				}
				
			}
		};
		btnBack.setOnClickListener(lstner);
		btnBack.setVisibility(View.VISIBLE);
		btnDefault.setOnClickListener(lstner);
		btnDefault.setVisibility(View.VISIBLE);
		btnSearch.setOnClickListener(lstner);
		titleName.setText(sickName);
		OnPageChangeListener pageListner = new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				CurrentPageIdx.put(sickName, (Integer)arg0);
				changDot(arg0);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		};
		viewPapers.setOnPageChangeListener(pageListner);
		mACTVAdater = new ACTVAdater();
		actvContent.setAdapter(mACTVAdater);
//		actvContent.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
		actvContent.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				
				onNight();
				
				return false;
			}
		});
		
		overLayout.setOnClickListener(lstner);
		initViewPaper();
		initDefaultButton();
		LogWorker.i("initView end.");
	}
	private void initDefaultButton(){
		//检查该病是不是默认的
		String n = StorageManager.instance().getDefaultDisease(this);
		boolean isDefault = true;
		if (n == null || n.equals("") || !n.equals(EattingOrNotMainActivity.SICK_NAME)){
			isDefault = false;
		}
		
		if (isDefault){
			btnDefault.setText(R.string.cancel_default_disease);
		} else {
			btnDefault.setText(R.string.set_default_disease);
		}
	}
	private void refreshDefaultButton(){
		initDefaultButton();
	}
	protected void okOrCancelForDefaultDialog(String msg) {
		  AlertDialog.Builder builder = new Builder(this);
		  builder.setMessage(msg);

		  builder.setTitle(getText(R.string.sweat_tip));

		  builder.setPositiveButton(getText(R.string.ok), new DialogInterface.OnClickListener() {

		   @Override
			   public void onClick(DialogInterface dialog, int which) {
				StorageManager.instance().setDefaultDisease(SeachFoodsActivity.this,EattingOrNotMainActivity.SICK_NAME);
				UserLoger.upload(SeachFoodsActivity.this, sickName, getText(R.string.set_default_disease).toString());
				refreshDefaultButton();
			   }
		  });

		  builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

		   @Override
		   public void onClick(DialogInterface dialog, int which) {
		    dialog.dismiss();
		   }
		  });

		  builder.create().show();
	}
	protected void onDefault() {
		LogWorker.i("onDefault");
		//do sth
		if (btnDefault.getText().toString().equals(getText(R.string.set_default_disease))){
			LogWorker.i("set_default_disease...");
			String d = StorageManager.instance().getDefaultDisease(this);
			if (d != null && !d.equals("")){
				okOrCancelForDefaultDialog(String.format(getText(R.string.tip_for_set_default_disease).toString(), d));
			} else {
				boolean f = StorageManager.instance().setDefaultDisease(SeachFoodsActivity.this,EattingOrNotMainActivity.SICK_NAME);
				if (f){
					UserLoger.upload(SeachFoodsActivity.this, sickName, getText(R.string.set_default_disease).toString());
					Toast.makeText(this, getText(R.string.set_success).toString(), Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(this, getText(R.string.set_failed).toString(), Toast.LENGTH_SHORT).show();
				}
				
			}

		} else {
			LogWorker.i("cancel_default_disease...");
			if (StorageManager.instance().setDefaultDisease(this,"")){
				UserLoger.upload(this, sickName, getText(R.string.cancel_default_disease).toString());
				Toast.makeText(this, getText(R.string.cancel_success).toString(), Toast.LENGTH_SHORT).show();
				
			} else {
				Toast.makeText(this, getText(R.string.cancel_failed).toString(), Toast.LENGTH_SHORT).show();
			}

		}
		
		refreshDefaultButton();
		
	}

	private  void showDialog(String title,String msg,DialogInterface.OnCancelListener cancel){
		if (dialog != null){
			dialog = null;
		}
		dialog = ProgressDialog.show(this, title, msg);
		dialog.setCancelable(true);
		dialog.setOnCancelListener(cancel);
	}

	private void stopDialog(){
		if (dialog != null && dialog.isShowing()){
			dialog.dismiss();
		}

	}

	AlertDialog error_dlg;
	private void showErrorDlg(String title, String msg) {
		stopDialog();
		if (error_dlg != null && error_dlg.isShowing()){
			
			error_dlg.dismiss();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		error_dlg = builder.create();
		error_dlg.setCancelable(true);
		error_dlg.setTitle(title);
		error_dlg.setMessage(msg);
		if (!isFinishing()){
			error_dlg.show();
		}

	}
	private void onNight(){ 
		LogWorker.i("onNight..");
		overLayout.setVisibility(View.VISIBLE);
		searchBarBg.setBackgroundResource(R.color.trans_gray);
		
		
	}
	@TargetApi(Build.VERSION_CODES.CUPCAKE)
	private void onLight(){
		LogWorker.i("onLight..");
		overLayout.setVisibility(View.GONE);
		searchBarBg.setBackgroundResource(R.color.transparent);
		inputMethodManager.hideSoftInputFromWindow(
				actvContent.getWindowToken(), 0); //隐藏
	}
	public enum DownLoadFlg{
		downloading,downloadFailed,downloadScs;
	}
	
	private void prepareHotFoodTask(String name){
		LogWorker.i("prepareHotFoodTask " + name);
		final String hotFoodPath = StorageManager.instance().getAPPCachePath() + "/" + name + "_hotFood.txt";

		hotFoodTask = 
			HttpLoader.getInstance(this)
			.getTask(new HotFoodParams(EattingOrNotMainActivity.SICK_NAME,maxPageNum * imgsPerPage));
		hotFoodTask.setStoreCachePath(hotFoodPath);
		if (EaterApplication.READ_HTTP_CACHE){
				hotFoodTask.setCacheHelper(new CacheHelper() {
				
				@Override
				public boolean onStoreDataInCache(Task t, InputStream is) {
					
					return FileUtils.storeInputStream(hotFoodPath, is, "utf-8");
				}
				
				@Override
				public InputStream getCache(Task t) {
					InputStream is = null;
					try {
						is = getAssets().open("hotfoods.txt");
					} catch (IOException e) {
						e.printStackTrace();
					}
					return is;
				}
			});
		}	
		hotFoodTask.setDownLoadDoneListener(new downLoadListener() {
			
			@Override
			public void onDownLoadDone(int id, String result) {
//				Toast.makeText(, result, Toast.LENGTH_LONG).show();

				if (result == HttpLoader.DOWLOAD_SUCCESS){
					hadDownLoad = true;
					downloadFlg = DownLoadFlg.downloadScs;
					stopDialog();
					showHotFoodViewPaper();
				} else {
					downloadFlg = DownLoadFlg.downloadFailed;
					showErrorDlg(getText(
							R.string.sweat_tip).toString(), getText(R.string.hot_food_download_failed).toString());
					
				}
				
			}
		});
		hotFoodTask.setDataParser(new dataParser() {
			
			@Override
			public boolean onDataParser(Task t, InputStream is) {
				
				if (is == null){
					LogWorker.i("is = null");
					return false;
					
				}
				try{
					mHotFoods = (ArrayList<Food>) HttpDataPraser.getFoodList(is);
					mHotFoodsOfAllDisease.put(sickName, mHotFoods);
					return true;
				} catch (Exception e) {
					return false;
				}
			}
		});
	}

	private void reflashHotFoods(){
		doDownloadHotFood(true);
	}
	private void doDownloadHotFood(boolean needNewData){
		if (!needNewData && mHotFoodsOfAllDisease.containsKey(sickName)){
			mHotFoods = mHotFoodsOfAllDisease.get(sickName);
			showHotFoodViewPaper();
			return ;
		}
		prepareHotFoodTask(sickName);
//		if (curShowedDisease != null && curShowedDisease.equals(EattingOrNotMainActivity.SICK_NAME))return;
		LogWorker.i("DownloadHotFood:" + sickName);

		downloadFlg = DownLoadFlg.downloading;
		final SimpleAsynHttpDowload hotFoodLoader = new SimpleAsynHttpDowload(HttpLoader.getInstance(this));
		showDialog(getResources().getText(R.string.sweat_tip).toString(),
				getResources().getText(R.string.hot_food_downloading).toString(),new OnCancelListener() {
					
					@Override
					public void onCancel(DialogInterface dialog) {
						hotFoodLoader.cancel(true);
					}
				});
		hotFoodLoader.execute(hotFoodTask);
		LogWorker.i("DownloadHotFood end");
	}
	Task allFoodTask;
	private void prepareALLFoodTask(String sickname){
		LogWorker.i("prepareALLFoodTask " + sickname);
		final String hotFoodPath = StorageManager.instance().getAPPCachePath() + "/allFood.txt";

		allFoodTask = 
			HttpLoader.getInstance(this)
			.getTask(new AllFoodOfDiseaseParams(EattingOrNotMainActivity.SICK_NAME));
		allFoodTask.setStoreCachePath(hotFoodPath);

		allFoodTask.setDownLoadDoneListener(new downLoadListener() {
			
			@Override
			public void onDownLoadDone(int id, String result) {

				if (result == HttpLoader.DOWLOAD_SUCCESS){
//					downloadFlg = DownLoadFlg.downloadScs;
//					stopDialog();
//					showHotFoodViewPaper();
//					Toast.makeText(SeachFoodsActivity.this, "all food done", Toast.LENGTH_LONG);
					mACTVAdater.notifyDataSetChanged();
				} else {
//					downloadFlg = DownLoadFlg.downloadFailed;
//					showErrorDlg(getText(
//							R.string.sweat_tip).toString(), getText(R.string.hot_food_download_failed).toString());
//					Toast.makeText(SeachFoodsActivity.this, "all food failed load", Toast.LENGTH_LONG);
				}
				
			}
		});
		allFoodTask.setDataParser(new dataParser() {
			
			@Override
			public boolean onDataParser(Task t, InputStream is) {
				
				if (is == null){
					LogWorker.i("is = null");
					return false;
					
				}
				try {
					mAllFoods = (ArrayList<Food>) HttpDataPraser.getFoodList(is);
					return true;
				} catch (Exception e) {
					return false;
				}
			}
		});
	}

	private void doDownloadALLFood(){
		prepareALLFoodTask(sickName);
		LogWorker.i("doDownloadALLFood");
		SimpleAsynHttpDowload hotFoodLoader = new SimpleAsynHttpDowload(HttpLoader.getInstance(this));
		hotFoodLoader.execute(allFoodTask);
		LogWorker.i("doDownloadALLFood end");
	}

	private void showHotFoodViewPaper(){
		LogWorker.i("showHotFoodViewPaper");
		initDotsAndGridView();
		viewPaperAdater.notifyDataSetChanged();
		Integer i = CurrentPageIdx.get(sickName);
		
		changDot(i == null ? 0 : i.intValue());
		viewPapers.setCurrentItem((i == null ? 0 : i.intValue()), true);
	}
	private void initViewPaper(){
		LogWorker.i("initViewPaper");
		viewPapers.setVisibility(View.VISIBLE);
		textHotFood.setVisibility(View.VISIBLE);
		initDotsAndGridView();
		viewPaperAdater = new ViewPaperAdater(this);
		viewPapers.setAdapter(viewPaperAdater);
	}
	protected void changDot(int id) {
			if (gridViews <= 0 )return ;
//			CurrentPageIdx = id;
			ImageView olddot = (ImageView) dotsLayout.getChildAt(curDot);
			olddot.setBackgroundDrawable(getResources().getDrawable(R.drawable.dot_default));
			curDot = id;
			ImageView dot = (ImageView) dotsLayout.getChildAt(curDot);
			dot.setBackgroundDrawable(getResources().getDrawable(R.drawable.dot_cur_min));	
	}
	private void onSearchButton(){

		String searchText = actvContent.getText().toString().trim();
		if (searchText == null ||searchText.equals("")  ){
			Toast.makeText(this, R.string.tip_for_input_null, Toast.LENGTH_SHORT).show();
//			return;
		} else {
			
			//goto searchResultActivity;
			onLight();
			SearchResultActivity.displayMyself(this, searchText);

		}
		
	}
	int curDot;
	private void initDotsAndGridView(){
		//
		if (mHotFoods == null ){
			LogWorker.i("mCurFoods is null");
			return;
		}
		gridViews = (mHotFoods.size() + imgsPerPage - 1)/ imgsPerPage;
		LinearLayout dots = (LinearLayout) findViewById(R.id.dots);
		dots.removeAllViews();
		// 
		mGirdViewList = new ArrayList<View>();
		for ( int i = 0 ; i < gridViews ; i++){
			ImageView dot = new ImageView(this);
	//		dot.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			dot.setBackgroundResource(R.drawable.dot_default);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.setMargins(10, 10, 10, 5);
			dot.setLayoutParams(lp);
			dots.addView(dot);
		
			View v = mInflater.inflate(R.layout.gallery_item, null);
			mGirdViewList.add(v);
		}
		LogWorker.i("gridViews = " + gridViews + " mGirdViewListSize" + mGirdViewList.size());
		curDot = 0;
		changDot(0);
	}
	private void toDetail(){
		FoodDetailsActivity.displayMyself(SeachFoodsActivity.this);
	}
	class ACTVAdater extends BaseAdapter implements Filterable{
		
		MyFilter mFilter;

		ArrayList<Food> mFilterResultData;
		OnClickListener mListener;
		final Object mLock = new Object();


		public ACTVAdater(){
			mFilterResultData = new ArrayList<Food>();
		}
		@Override
		public int getCount() {
			return mFilterResultData == null ? 0 : mFilterResultData.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mFilterResultData.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int p, View v, ViewGroup arg2) {
			ViewHoder hoder;
			if (v == null){
				v = mInflater.inflate(R.layout.auto_cpl_tv_item_rich, null);
				hoder = new ViewHoder();
				hoder.img = (ImageView) v.findViewById(R.id.fit_img);
				hoder.name = (TextView) v.findViewById(R.id.food_name);
				v.setTag(hoder);
			} else {
				hoder = (ViewHoder) v.getTag();
			}
			mListener = new OnClickListener() {
				
				@TargetApi(Build.VERSION_CODES.CUPCAKE)
				@Override
				public void onClick(View v) {
					LogWorker.i("onClick..");
					onLight();
					//根据食物名获取食物对象
					TextView tv = (TextView) v.findViewById(R.id.food_name);
					String name = tv.getText().toString();
					LogWorker.i("onClick..text = " + name);
//					curSelFood = mSearchControl.getFoodByName(name);
					mFilterResultData.get(p).setRelatedDisease(EattingOrNotMainActivity.SICK_NAME);
					setCurFood(mFilterResultData.get(p));
//					FoodDetailsActivity.displayMyself(SeachFoodsActivity.this);
					toDetail();
					
				}
			};
			String enterString = actvContent.getText().toString();
			String foodname = mFilterResultData.get(p).getFoodName();
			
			int flgFit = mFilterResultData.get(p).getCanEatInt();
			switch ( flgFit ) {
			case EaterApplication.JI:
				hoder.img.setImageResource(R.drawable.ji);
				break;
				
			case EaterApplication.YI:
				hoder.img.setImageResource(R.drawable.yi);
				break;
				
			case EaterApplication.KE:
				hoder.img.setImageResource(R.drawable.ke);
				break;
			default:
				break;
			}
			
			LogWorker.i("entering...: " + enterString);
			Spanned htmlString = Html.fromHtml(foodname.substring(0, foodname.indexOf(enterString)) 
					+ "<font color=\"red\">" + enterString + "</font>" 
					+ foodname.substring(foodname.indexOf(enterString)+enterString.length()));
			hoder.name.setText(htmlString);
			hoder.name.setTextColor(Color.GRAY);
			v.setOnClickListener(mListener);
			return v;
		}
		class ViewHoder{
			TextView name;
			ImageView img;
		}
		@Override
		public Filter getFilter() {
			if (mFilter == null){
				mFilter = new MyFilter();
			}
			return mFilter;
		}
		class MyFilter extends Filter{
			
			@Override
			public CharSequence convertResultToString(Object resultValue) {
				// TODO Auto-generated method stub
				return super.convertResultToString(resultValue);
			}

			@Override
			protected FilterResults performFiltering(CharSequence prefix) {
				// TODO Auto-generated method stub
				FilterResults results = new FilterResults();
				// 没有输入信息
				if (prefix == null || prefix.length() == 0) {
				synchronized (mLock) {
					ArrayList<Food> list;
					if (mAllFoods!=null){
						list = new ArrayList<Food>(
							mAllFoods);
					} else {
						list = new ArrayList<Food>();
					}
					results.values = list;
					results.count = list.size();
					return results;
				}
				} else {
				String prefixString = prefix.toString().toLowerCase();

				final int count = mAllFoods.size();
				LogWorker.i("all foods number= " + count);
				final ArrayList<Food> newValues = new ArrayList<Food>(count);
				for (int i = 0; i < count; i++) {
					final Food value = mAllFoods.get(i);
					if (value.getFoodName().contains(prefixString)) { // 源码 ,匹配开头
						newValues.add(value);//存放的是他在原数组的id值
						
					}
				}
				results.values = newValues;
				results.count = newValues.size();
				return results;
			}
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				//刷新信息
				mFilterResultData = (ArrayList<Food>) results.values;    
				           if (results.count > 0) {    
				               notifyDataSetChanged();    
				           } else {    
				               notifyDataSetInvalidated();    
				           }  
			}
				
			
		}
	}


	LayoutInflater mInflater;
	private class ViewPaperAdater extends PagerAdapter{
		float itemWidthPersentBySceneWidth = 0.25f;
		Context mContext;
		OnItemClickListener listener;
		HashMap<Integer, GridViewAdater> mGridViewAdaterMap ;
		Bitmap defaultImg;
		public ViewPaperAdater(Context c){
			mContext = c;
			mGridViewAdaterMap = new HashMap<Integer, SeachFoodsActivity.ViewPaperAdater.GridViewAdater>();

			defaultImg = ((BitmapDrawable)getResources()
					.getDrawable(R.drawable.default_food)).getBitmap();
			for (int arg1 = 0 ; arg1 < mGirdViewList.size(); arg1++){
				mGridViewAdaterMap.put((Integer)arg1, new GridViewAdater(mContext, arg1, null));
			}
		}
		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			LogWorker.i("view paper destroyItem arg1: " + arg1);
			if (arg1 < getCount())
			((ViewPager)arg0).removeView(mGirdViewList.get(arg1));
			
		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int getCount() {
			LogWorker.i("view papers = " + mGirdViewList.size());
			return mGirdViewList.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			LogWorker.i("view paper on instantiateItem arg1: " + arg1);
			View v = mGirdViewList.get(arg1);
			GridView gv = (GridView) v.findViewById(R.id.grid_view_imgs);
			GridViewAdater adt = mGridViewAdaterMap.get((Integer)arg1);
			if (adt == null){
				adt = new GridViewAdater(mContext, arg1, null);
				mGridViewAdaterMap.put((Integer)arg1, adt);
			}
			gv.setAdapter(adt);
			final int offSet = arg1;
			listener = new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View v,
						int arg2, long arg3) {

					LogWorker.i("click hot food at:" + arg2);
					Food f = mHotFoods.get(arg2 + offSet * imgsPerPage);
					f.setRelatedDisease(EattingOrNotMainActivity.SICK_NAME);
					setCurFood(f);
//					mFoodService.upDateFoodSearchTime(curSelFood.getFoodName());
					toDetail();
				}
			};
			gv.setOnItemClickListener(listener);
			((ViewPager)arg0).addView(v);
			LogWorker.i("view paper on instantiateItem arg1: " + arg1 + " end");
			return v;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
			
		}
//		int cacheMemory = 2 * 1024 * 1024;
		@TargetApi(Build.VERSION_CODES.CUPCAKE)
		public class GridViewAdater extends BaseAdapter{
			ImageLoader mImgLoader = new ImageLoader(SeachFoodsActivity.this);
			int curGridViewPos;
			HashMap<String, Bitmap> imgViewCache = new HashMap<String, Bitmap>();
			public GridViewAdater(Context c,int curPos,GridView view){
				curGridViewPos = curPos;
				imgViewCache = new HashMap<String, Bitmap>();
				mImgLoader.setListener(new ImageLoaderListener() {
					
					@Override
					public void imgDidLoad(String imgUrl) {
						if (imgUrl != null){
							
							stopDialog();
//							imgLruCache.put(imgUrl, ImageLoader.getImageCacheByUrl(imgUrl));
							notifyDataSetChanged();
						}

					}
				});
			}
			@Override
			public int getCount() {
				if ( (curGridViewPos + 1) * imgsPerPage < mHotFoods.size()){
					return imgsPerPage;
				}
				return mHotFoods.size() - curGridViewPos * imgsPerPage;
			}

			@Override
			public Object getItem(int position) {
				return mHotFoods.get(position).getFoodImgResId(); 
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ImageViewWithText imgAndText;
				LogWorker.i("grid view get view at:" + position);

				int foodPos = imgsPerPage * curGridViewPos + position;
				int resId = mHotFoods.get(foodPos).getFoodImgResId();
				int caneat = mHotFoods.get(foodPos).getCanEatInt();
				String imgName = mHotFoods.get(foodPos).getFoodImgURL();
				String imgUrl ;
				if ( !EaterApplication.READ_HTTP_CACHE ){
					imgUrl = StorageManager.instance().getImageURLByName(imgName);
				} else {
					imgUrl = imgName;
				}
				
				Bitmap bm = imgViewCache.get(imgUrl);
//				bm = imgLruCache.get(imgUrl);
				if (bm == null){
					bm = ImageLoader.getImageCacheByUrl(imgUrl);
				}
				if (convertView == null){
					convertView = mInflater.inflate(R.layout.customs_image, null);
					ImageView bgImg = (ImageView) convertView.findViewById(R.id.bg_image);
					TextView imgDesp = (TextView) convertView.findViewById(R.id.img_dscp);
					ImageView imgFit = (ImageView) convertView.findViewById(R.id.img_fit);
					RelativeLayout rlt = (RelativeLayout) convertView.findViewById(R.id.img_text_bottom);
					imgAndText = new ImageViewWithText(mContext);
					imgAndText.bgImg = bgImg;
					imgAndText.imgDesp = imgDesp;
					imgAndText.imgFit = imgFit;
					imgAndText.textLayout = rlt;
					if (bm == null){
						mImgLoader.setScaleByScreenWidthPercentage(itemWidthPersentBySceneWidth);
						mImgLoader.fetch(imgUrl);
						
					}
					convertView.setTag(imgAndText);
//					mapForFoodImg.put(imgUrl, imgAndText.bgImg);
				} else {
					imgAndText = (ImageViewWithText)convertView.getTag();
				}

				switch (caneat) {
				case EaterApplication.JI:
					imgAndText.imgFit.setImageResource(R.drawable.ji);
					break;
					
				case EaterApplication.YI:
					imgAndText.imgFit.setImageResource(R.drawable.yi);
					break;
					
				case EaterApplication.KE:
					imgAndText.imgFit.setImageResource(R.drawable.ke);
					break;
				default:
					break;
				}
				
//
				if ( bm == null ){
					bm = defaultImg;
				}
				if (bm != defaultImg && imgViewCache.size() <= imgsPerPage  && !imgViewCache.containsKey(imgUrl)){
					imgViewCache.put(imgUrl, bm);
				}
				imgAndText.setImageAndText(
						bm,
						mHotFoods.get(foodPos).getFoodName(),itemWidthPersentBySceneWidth);
				return convertView;
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.ECLAIR)
	private boolean onBack(){
		LogWorker.i("onBack...");
		if (overLayout.getVisibility() == View.VISIBLE){
			onLight();
			return false;
		}
		finish();
//		EattingOrNotMainActivity.displayMyself(SeachFoodsActivity.this,true);
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
		return true;
	}
	
	@TargetApi(Build.VERSION_CODES.ECLAIR)
	@Override
	public void onBackPressed() {

		if (onBack()){
			super.onBackPressed();
		}

	}

	
	@Override
	protected void onResume() {
		actvContent.setText("");
		onLight();
		mNaviBar.changeNavibar(0);
		MobclickAgent.onResume(this);
		LogWorker.i(this.getClass().getSimpleName()+":onResume");
		if (EattingOrNotMainActivity.getSickName() == null){
			EattingOrNotMainActivity.displayMyself(this,false);
		} else {
			if ( dialog == null || !dialog.isShowing()){
				doDownloadHotFood(false);
			}
			
		}
		super.onResume();

	}
	@Override
	protected void onDestroy() {
		LogWorker.i(this.getClass().getSimpleName()+":onDestroy");
		stopDialog();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		LogWorker.i(this.getClass().getSimpleName()+":onPause");
		MobclickAgent.onPause(this);
		super.onPause();
	}

	@Override
	protected void onStop() {
		LogWorker.i(this.getClass().getSimpleName()+":onStop");
		stopDialog();
		super.onStop();
	}
	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		LogWorker.i(this.getClass().getSimpleName()+":onLowMemory");
		super.onLowMemory();
	}
}
