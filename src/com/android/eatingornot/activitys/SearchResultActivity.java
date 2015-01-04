/**
 * 
 */
package com.android.eatingornot.activitys;

import java.io.InputStream;
import java.util.ArrayList;

import ccb.android.net.framkwork.HttpLoader;
import ccb.android.net.framkwork.HttpLoader.CacheHelper;
import ccb.android.net.framkwork.HttpLoader.Task;
import ccb.android.net.framkwork.HttpLoader.dataParser;
import ccb.android.net.framkwork.HttpLoader.downLoadListener;
import ccb.android.net.framkwork.ImageLoader;
import ccb.android.net.framkwork.ImageLoader.ImageLoaderListener;
import ccb.android.net.framkwork.SimpleAsynHttpDowload;
import ccb.java.android.utils.FileUtils;
import ccb.java.android.utils.LogWorker;
import ccb.java.android.utils.StorageManager;

import com.android.eatingornot.datamodel.Food;
import com.android.eatingornot.datapraser.HttpDataPraser;
import com.android.eatingornot.httpparams.SearchFoodOfDiseaseParams;
import com.android.eatingornot.tool.Tool;
import com.android.eatingornot.widget.CustomNaviBar;
import com.umeng.analytics.MobclickAgent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author chenchuibo
 *
 */
public class SearchResultActivity extends Activity {
	
	public static String SICK_NAME = "sick_name";
	public static String FOOD_NAME = "food_name";
	public static String NO_SEARCHING = "do_not_search";
	String foodName;
	TextView titTextView,nullRetTextView;
	Button btnBack;
	ListView mSearchRetListView;
	
	LayoutInflater mInflater;
	ListAdater mListAdater;
//	SearchController mSearchControl;
//	FoodService mFoodService;
//	DieseaseWithFoodService dieseaseWithFoodService;
	ArrayList<Food> mSearchResultFoods;
	private CustomNaviBar mNaviBar;
	private ProgressDialog dialog;
	
	@TargetApi(Build.VERSION_CODES.ECLAIR)
	public static void displayMyself(Activity from,String searchText){
		Intent intent = new Intent(from,SearchResultActivity.class);
		intent.putExtra(FOOD_NAME, searchText);
		from.startActivity(intent);
        from.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//        from.finish();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_result);
		MobclickAgent.onError(this);
		mInflater = getLayoutInflater();
		
//		dieseaseWithFoodService = DieseaseWithFoodService.getInstance(this);
//		mFoodService = FoodService.getInstance(this);
//		mSearchControl = new SearchController(this);
		
		mSearchResultFoods = new ArrayList<Food>();
		foodName = getIntent().getStringExtra(FOOD_NAME);
		initView();
		//后期需要异步处理
		
		
//		if (!foodName.startsWith(NO_SEARCHING)){
//			doSearch();
//		} else {
//			foodName = foodName.substring(foodName.indexOf(NO_SEARCHING) + NO_SEARCHING.length());
//			titTextView.setText(getText(R.string.tip_search) 
//					+ foodName);
//			hideListView();
//		}
//		doSearchFoods();
	}

	private void initView(){
		mNaviBar = new CustomNaviBar(this, 0);
		
		titTextView = (TextView) findViewById(R.id.title_text);
		titTextView.setText(getText(R.string.tip_search) 
				+ foodName);
		
		nullRetTextView = (TextView) findViewById(R.id.seach_ret_null);
		
		btnBack = (Button)findViewById(R.id.title_btn_left);
		btnBack.setVisibility(View.VISIBLE);
		btnBack.setOnClickListener(new OnClickListener() {
			@TargetApi(Build.VERSION_CODES.ECLAIR)
			@Override
			public void onClick(View arg0) {
				onBack();
			}
		});
		mSearchRetListView = (ListView)findViewById(R.id.search_result_list);
		mSearchRetListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				mSearchResultFoods.get(arg2).setRelatedDisease(EattingOrNotMainActivity.SICK_NAME);
				SeachFoodsActivity.setCurFood(mSearchResultFoods.get(arg2));
				FoodDetailsActivity.displayMyself(
						SearchResultActivity.this);
			}
		});
		mListAdater = new ListAdater(this);
		mSearchRetListView.setAdapter(mListAdater);
	}
	Task searchTask;
	private void prepareSearchTask(){
		searchTask = HttpLoader.getInstance(this).getTask(
				new SearchFoodOfDiseaseParams(EattingOrNotMainActivity.SICK_NAME, foodName));
		final String fooddetailPath = StorageManager.instance().getAPPCachePath() 
		+ "/" +foodName + EattingOrNotMainActivity.SICK_NAME +"_ret_food.txt";
		if ( EaterApplication.READ_HTTP_CACHE ){
			searchTask.setCacheHelper(new CacheHelper() {
				
				@Override
				public boolean onStoreDataInCache(Task t, InputStream is) {
					// TODO Auto-generated method stub
					return FileUtils.storeInputStream(fooddetailPath, is, "utf-8");
				}
				
				@Override
				public InputStream getCache(Task t) {
					// TODO Auto-generated method stub
					InputStream is = null;
					try {
	//					is = FileUtils.openFileInputStream(fooddetailPath);
						is = getAssets().open("search_ret_foods.txt");
					} catch (Exception e) {
						LogWorker.i(e.getMessage());
						e.printStackTrace();
					}
					return is;
				}
			});
		}
		searchTask.setDataParser(new dataParser() {
			
			@Override
			public boolean onDataParser(Task t, InputStream is) {
				if (is == null)return false;
				try{
					mSearchResultFoods = (ArrayList<Food>) HttpDataPraser.getFoodList(is);
					if ( mSearchResultFoods == null || mSearchResultFoods.size() == 0){
						return false;
					}
					return true;
				} catch (Exception e) {
					return false;
				}
				
			}
		});
		searchTask.setDownLoadDoneListener(new downLoadListener() {
			
			@Override
			public void onDownLoadDone(int taskId, String result) {
				// TODO Auto-generated method stub
				if (result.equals(HttpLoader.DOWLOAD_SUCCESS)){
					stopDialog();
					mListAdater.notifyDataSetChanged();
				} else {
					hideListView();
					stopDialog();
				}
			}
		});
	}
	private void doSearchFoods(){
		prepareSearchTask();
		showDialog(getText(R.string.sweat_tip).toString(), getText(R.string.tip_for_searching).toString());
		SimpleAsynHttpDowload loader = new SimpleAsynHttpDowload(HttpLoader.getInstance(this));
		loader.execute(searchTask);
	}
	private void hideListView(){
		nullRetTextView.setVisibility(View.VISIBLE);
		mSearchRetListView.setVisibility(View.GONE);
	}
	private void doSearch(){
//		mCurFoods = mSearchControl.searchBySickMixFood(foodName);
//		mSearchResultFoods = mSearchControl.searchFoodsByNameRelativeSickName(foodName,EattingOrNotMainActivity.getSickName());
//		if (mSearchResultFoods == null || mSearchResultFoods.size() == 0){
//			hideListView();
//		} else {
//			mListAdater.notifyDataSetChanged();
//		}
		
	}
	class ListAdater extends BaseAdapter{

		float p = 0.3f;
		Context mContext;
		ImageLoader mLoader;
		Bitmap defaultBM ;
		public ListAdater(Context c){
			mContext = c;
			mLoader = new ImageLoader(c);
			mLoader.setListener(new ImageLoaderListener() {
				
				@Override
				public void imgDidLoad(String imgUrl) {
					notifyDataSetChanged();
				}
			});
			defaultBM = BitmapFactory.decodeResource(getResources(), R.drawable.default_food);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mSearchResultFoods.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return mSearchResultFoods.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			ViewHoder imgAndText;
			ImageView fit = null;
			Log.e("gridview ", "getView : " + position);
			String foodname = mSearchResultFoods.get(position).getFoodName();
			int flgFit = mSearchResultFoods.get(position).getCanEatInt();
			String name = mSearchResultFoods.get(position).getFoodImgURL();
			String imgUrl ;
			if ( !EaterApplication.READ_HTTP_CACHE ){
				imgUrl = StorageManager.instance().getImageURLByName(name);
			} else {
				imgUrl = name;
			}
			Bitmap bm = ImageLoader.getImageCacheByUrl(imgUrl);

			if (convertView == null){
				convertView = mInflater.inflate(R.layout.search_result_item, null);
				ImageView bgImg = (ImageView) convertView.findViewById(R.id.search_ret_item_img);
				fit = (ImageView) convertView.findViewById(R.id.search_ret_item_fit);
				TextView imgDesp = (TextView) convertView.findViewById(R.id.search_ret_item_name);
//				imgAndText = new ImageViewWithText(mContext);
//				imgAndText.bgImg = bgImg;
//				imgAndText.imgDesp = imgDesp;
//				imgAndText.imgFit = imgFit;
//				imgAndText.textLayout = rlt;
				imgAndText = new ViewHoder();
				
				imgAndText.bgImg = bgImg;
				imgAndText.imgDesp = imgDesp;
				convertView.setTag(imgAndText);
				if (bm == null){
					mLoader.fetch(imgUrl);
				}
			} else {
				imgAndText = (ViewHoder)convertView.getTag();
			}

			if (fit != null){
				switch ( flgFit ) {
				case EaterApplication.JI:
					fit.setImageResource(R.drawable.ji);
					break;
					
				case EaterApplication.YI:
					fit.setImageResource(R.drawable.yi);
					break;
					
				case EaterApplication.KE:
					fit.setImageResource(R.drawable.ke);
					break;
				default:
					break;
				}
			}
			if ( bm == null ){
				bm = defaultBM;
			}
			int foodPos = position;
			int resId = mSearchResultFoods.get(foodPos).getFoodImgResId();
			LogWorker.i("bm = " + bm);
			imgAndText.bgImg.setImageBitmap(Tool.getScaleImageByScaleOfWinWidth(mContext, bm, p));
			imgAndText.bgImg.setTag(resId);
			imgAndText.imgDesp.setText(foodname);
			return convertView;
		}
		class ViewHoder{
			ImageView bgImg;
			ImageView fit;
			TextView imgDesp;
		}
	}
	private  void showDialog(String title,String msg){
		if (dialog != null){
			dialog = null;
		}
		dialog = ProgressDialog.show(this, title, msg);
	}

	private void stopDialog(){
		if (dialog != null && dialog.isShowing()){
			dialog.dismiss();
		}

	}
	private void showErrorDlg(String title, String msg) {
		if (dialog != null && dialog.isShowing()) {
			stopDialog();
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		AlertDialog error_dlg = builder.create();
		error_dlg.setCancelable(true);
		error_dlg.setTitle(title);
		error_dlg.setMessage(msg);
		if ( !isFinishing() ){
			error_dlg.show();
		}
	}
	@TargetApi(Build.VERSION_CODES.ECLAIR)
	@Override
	public void onBackPressed() {
		onBack();
		super.onBackPressed();
	}
	@TargetApi(Build.VERSION_CODES.ECLAIR)
	private void onBack() {
//		showDialog("", getText(R.string.loading).toString());
//		SeachFoodsActivity.displayMyself(this);
		finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}
	@Override
	protected void onResume() {
		LogWorker.i(this.getClass().getSimpleName()+":onResume");
		mNaviBar.changeNavibar(0);
		MobclickAgent.onResume(this);
		if (EattingOrNotMainActivity.getSickName() == null){
			EattingOrNotMainActivity.displayMyself(this,false);
		} else {
			doSearchFoods();
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
		super.onStop();
	}
}
