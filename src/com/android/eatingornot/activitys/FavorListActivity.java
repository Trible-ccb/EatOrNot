package com.android.eatingornot.activitys;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ccb.android.net.framkwork.HttpLoader;
import ccb.android.net.framkwork.ImageLoader;
import ccb.android.net.framkwork.SimpleAsynHttpDowload;
import ccb.android.net.framkwork.HttpLoader.CacheHelper;
import ccb.android.net.framkwork.HttpLoader.Task;
import ccb.android.net.framkwork.HttpLoader.dataParser;
import ccb.android.net.framkwork.HttpLoader.downLoadListener;
import ccb.android.net.framkwork.ImageLoader.ImageLoaderListener;
import ccb.java.android.utils.FileUtils;
import ccb.java.android.utils.LogWorker;
import ccb.java.android.utils.StorageManager;

import com.android.eatingornot.datamodel.RequestRetMsg;
import com.android.eatingornot.datamodel.Food;
import com.android.eatingornot.datamodel.UserInfo;
import com.android.eatingornot.datamodel.WeiBoUserData;
import com.android.eatingornot.datapraser.HttpDataPraser;
import com.android.eatingornot.httpparams.DelFavorParams;
import com.android.eatingornot.httpparams.GetFavorsParams;
import com.android.eatingornot.httpparams.SearchFoodOfDiseaseParams;
import com.android.eatingornot.tool.Tool;
import com.android.eatingornot.weibo.WeiboEntity;
import com.android.eatingornot.widget.CustomNaviBar;
import com.umeng.analytics.MobclickAgent;
import com.weibo.net.Weibo;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class FavorListActivity extends Activity {

	Button btnBack,btnEdit;
	TextView titText;
	TextView tvFavorNum;
	LayoutInflater mInflater;
	ListView illListView;
	ListView favorList;
	TextView curIllText;
	
	static ArrayList<Food> mFavorFoods;
	static ArrayList<Food> mFavorFoodsTmp;
	FavorListAdater mFavorListAdater;
	CustomNaviBar mNaviBar;
	
	EaterApplication mApp;
	UserInfo userInfo;
	static Activity fromActivity;
	
	int selectedNumber;
	int illSet[];
	@TargetApi(Build.VERSION_CODES.ECLAIR)
	public static void displayMyself(Activity from){
		fromActivity = from;
		Intent intent = new Intent(from,FavorListActivity.class);
		from.startActivity(intent);
        from.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//        from.finish();
	}
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favor_list);
		MobclickAgent.onError(this);
		UserLoger.upload(this, getText(R.string.FavorList).toString(), getText(R.string.action_setup).toString());
		mInflater = getLayoutInflater();
		int illNumber = EattingOrNotMainActivity.diseaseSet.length + 1;
		illSet = new int[illNumber];
		for (int i = 0 ; i < illNumber ; i++){
			if ( i == illNumber - 1){
				illSet[i] = 0;
			} else {
				illSet[i] = EattingOrNotMainActivity.diseaseSet[i][0];
			}
		}
		mApp = (EaterApplication)getApplication();
		if (mFavorFoods == null){
			mFavorFoods = new ArrayList<Food>();
		}
		if (mFavorFoodsTmp == null){
			mFavorFoodsTmp = new ArrayList<Food>();
		}
		initView();
		checkWeibo();
	}

	private void initView(){
		titText = (TextView) findViewById(R.id.title_text);
		titText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		btnBack = (Button) findViewById(R.id.title_btn_left);
		btnEdit = (Button) findViewById(R.id.title_btn_right);
		illListView = (ListView) findViewById(R.id.ill_list);
		btnBack.setVisibility(View.VISIBLE);
		btnEdit.setVisibility(View.VISIBLE);
		btnEdit.setText(R.string.text_edit);
		titText.setBackgroundResource(R.drawable.title_btn_seletor);
		tvFavorNum = (TextView) findViewById(R.id.favor_number);
		curIllText = (TextView) findViewById(R.id.current_ill);
		mNaviBar = new CustomNaviBar(this,1);
		favorList = (ListView) findViewById(R.id.favor_list);
		mFavorListAdater = new FavorListAdater(this);
		favorList.setAdapter(mFavorListAdater);
		favorList.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				illListView.setVisibility(View.GONE);
				return false;
			}
		});
		favorList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				illListView.setVisibility(View.GONE);
				SeachFoodsActivity.setCurFood(mFavorFoods.get(arg2));
//				EattingOrNotMainActivity.SICK_NAME = mFavorFoods.get(arg2).getRelatedDisease();
				FoodDetailsActivity.displayMyself(
						FavorListActivity.this);
			}
		});
		

		illListView.setAdapter(new BaseIllAdater(this));
		illListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				TextView  t = (TextView) arg1.findViewById(R.id.ill_item);
				curIllText.setText(t.getText());
				filterFoodInTmp();
				favorListDataChange();
				illListView.setVisibility(View.GONE);
			}
		});
		illListView.setVisibility(View.GONE);
		OnClickListener listener = new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				switch (arg0.getId()) {
					case R.id.current_ill:
						if (illListView.getVisibility() == View.VISIBLE){
							illListView.setVisibility(View.GONE);
						} else {
							illListView.setVisibility(View.VISIBLE);
						}
					break;
					case R.id.title_btn_left:
						onBack();
						break;
						
					case R.id.title_btn_right:
						onEdit();
						break;
					case R.id.title_text:
						changeUser();
						break;
				default:
					break;
				}

			}
		};
		titText.setOnClickListener(listener);
		curIllText.setOnClickListener(listener);
		btnBack.setOnClickListener(listener);
		btnEdit.setOnClickListener(listener);
		
	}
	protected void changeUser() {
//		WeiboEntity weiboEntity = new WeiboEntity(this,FavorListActivity.class);
//		weiboEntity.login();
		UserCenterActivity.displayMyself(this);
		
	}

	private void checkWeibo(){

		userInfo = mApp.getUserInfo();
		LogWorker.i("uid = " + userInfo.getUserID() + " weiboId = " + userInfo.getWeiboId());
		if (!mApp.isLogin() && !mApp.isWeiboLogin()){
			Toast t = Toast.makeText(this, getText(R.string.please_logging), Toast.LENGTH_LONG);
			t.show();
			UserLoginActivity.displayMyself(fromActivity);
			finish();
		} else {
			Toast t = Toast.makeText(this, "had logined", Toast.LENGTH_SHORT);
			String name = userInfo.getUserName();
			if (TextUtils.isEmpty(name)){
				name = userInfo.getWeiboName();
			}
			titText.setText(name);
//			t.show();
			doSearchFavorFoods();
		}
	}
	boolean isEditing;
	private void onEdit(){

		isEditing = !isEditing;
		if ( isEditing ){
			btnEdit.setText(R.string.delete_selected);
			favorListDataChange();
		} else{
			doDelFavorTask();
		}
		illListView.setVisibility(View.GONE);
	}
	Task searchTask;
	private ProgressDialog dialog;
	
	private void prepareFavorTask(){
		LogWorker.i("prepareFavorTask..");
		searchTask = HttpLoader.getInstance(this).getTask(
				new GetFavorsParams(mApp.getUserInfo().getUserID()));
		final String favorFoodPath = StorageManager.instance().getAPPCachePath() 
		+ "/favor_foods.txt";
		if ( EaterApplication.READ_HTTP_CACHE ){
			searchTask.setCacheHelper(new CacheHelper() {
				
				@Override
				public boolean onStoreDataInCache(Task t, InputStream is) {
					return FileUtils.storeInputStream(favorFoodPath, is, "utf-8");
				}
				
				@Override
				public InputStream getCache(Task t) {
					InputStream is = null;
					try {
						is = getAssets().open("favorFoodsWithDisease.txt");
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
					mFavorFoods = (ArrayList<Food>) HttpDataPraser.getFoodList(is);
					filterFoodInTmp();
					if ( mFavorFoods == null){
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
					favorListDataChange();
				} else {
//					hideListView();
					stopDialog();
				}
				LogWorker.i("mFavorsFood size " + mFavorFoods.size());
			}
		});
	}

	@SuppressLint("NewApi")
	private void doSearchFavorFoods(){
		SimpleAsynHttpDowload favorLoader = new SimpleAsynHttpDowload(HttpLoader.getInstance(this));
		prepareFavorTask();
		LogWorker.i("doSearchFavorFoods..");
//		showDialog(getText(R.string.sweat_tip).toString(), getText(R.string.tip_for_searching).toString(),null);
//		favorLoader.execute(searchTask);
	}
	Task delecteFavorTask;
	RequestRetMsg delRetMsg;
	private void prepareDelecteTask(){

		String ids = "";
		String diseases = "";
		String foods = "";
		for (int i = 0 ; i < mFavorFoods.size() ; i++){
			Food f = mFavorFoods.get(i);
			if (f.isSelected() ){
				ids += f.getFoodImgResId();
				diseases += f.getRelatedDisease();
				foods += f.getFoodName();
				if ( i != mFavorFoods.size() - 1 ){
					ids += ";";
					diseases += ";";
					foods += ";";
				}
					
			}
		}
		delecteFavorTask = HttpLoader.getInstance(this).getTask(
				new DelFavorParams(ids,foods ,diseases,mApp.getUserInfo().getUserID()));
		final String oodPath = StorageManager.instance().getAPPCachePath() 
		+ "/avor_ret.txt";
		if ( EaterApplication.READ_HTTP_CACHE ){
			delecteFavorTask.setCacheHelper(new CacheHelper() {
				
				@Override
				public boolean onStoreDataInCache(Task t, InputStream is) {
					return FileUtils.storeInputStream(oodPath, is, "utf-8");
				}
				
				@Override
				public InputStream getCache(Task t) {
					InputStream is = null;
					try {
						is = getAssets().open("delRetMsg.txt");
					} catch (Exception e) {
						LogWorker.i(e.getMessage());
						e.printStackTrace();
					}
					return is;
				}
			});
		}
		delecteFavorTask.setDataParser(new dataParser() {
			
			@Override
			public boolean onDataParser(Task t, InputStream is) {
				if (is == null)return false;
				try{
					delRetMsg = HttpDataPraser.getFavorRetMsg(is);
					if (delRetMsg == null){
						return false;
					}
					return true;
				} catch (Exception e) {
					return false;
				}
				
			}
		});
		delecteFavorTask.setDownLoadDoneListener(new downLoadListener() {
			
			@Override
			public void onDownLoadDone(int taskId, String result) {
				if (result.equals(HttpLoader.DOWLOAD_SUCCESS)){
					stopDialog();
					if (delRetMsg.isActionCode()){
						for (int i = mFavorFoods.size() - 1 ; i >= 0 ; i--){
							if (mFavorFoods.get(i).isSelected()){
								mFavorFoods.remove(i);
							}
						}
						filterFoodInTmp();
					} else {
						showErrorDlg(getText(R.string.sweat_tip).toString(), getText(R.string.delected_favor_failed).toString());
					}
				} else {
//					hideListView();
					showErrorDlg(getText(R.string.sweat_tip).toString(), getText(R.string.server_connect_failed).toString());
				}
				btnEdit.setText(R.string.text_edit);
				favorListDataChange();
			}
		});
	}
	private void doDelFavorTask(){
		if (mFavorFoods == null || mFavorFoods.size() == 0 || selectedNumber == 0){
			btnEdit.setText(R.string.text_edit);
			favorListDataChange();
			return ;
		}
		prepareDelecteTask();
		
		final SimpleAsynHttpDowload loader = new SimpleAsynHttpDowload(HttpLoader.getInstance(this));
		showDialog(getText(R.string.sweat_tip).toString(), getText(R.string.delecting_favor).toString(), new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface arg0) {
				// TODO Auto-generated method stub
				loader.cancel(true);
			}
		});
		loader.execute(delecteFavorTask);
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
	private void showErrorDlg(String title, String msg) {
		if (dialog != null && dialog.isShowing()) {
			stopDialog();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		AlertDialog error_dlg = builder.create();
		error_dlg.setCancelable(true);
		error_dlg.setTitle(title);
		error_dlg.setMessage(msg);
		if (!isFinishing()){
			error_dlg.show();
		}

	}
	private void favorListDataChange(){
		tvFavorNum.setText("" + mFavorFoodsTmp.size());
		mFavorListAdater.notifyDataSetChanged();
	}
	private void filterFoodInTmp(){
		String filterStr = curIllText.getText().toString();
		mFavorFoodsTmp.clear();
		if (filterStr.equals(getText(R.string.all_disease))){
			mFavorFoodsTmp = (ArrayList<Food>) mFavorFoods.clone();
		} else {
				for (int i = 0 ; i < mFavorFoods.size(); i++){
					if (filterStr.equals(mFavorFoods.get(i).getRelatedDisease())){
					mFavorFoodsTmp.add(mFavorFoods.get(i));
					}
				}
		}
	}
	class FavorListAdater extends BaseAdapter{

		float p = 0.3f;
		Context mContext;
		ImageLoader mLoader;
		Bitmap defaultBM ;
		public OnCheckedChangeListener checkListner;
		public FavorListAdater(Context c){
			mContext = c;
			mLoader = new ImageLoader(c);
			filterFoodInTmp();
			mLoader.setListener(new ImageLoaderListener() {
				
				@Override
				public void imgDidLoad(String imgUrl) {
					favorListDataChange();
				}
			});
			defaultBM = BitmapFactory.decodeResource(getResources(), R.drawable.default_food);
			
			checkListner = new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					Food f = mFavorFoodsTmp.get(buttonView.getId());
					if (isChecked){
						selectedNumber++;
					} else {
						selectedNumber--;
						if (selectedNumber == 0){
							btnEdit.setText(R.string.text_edit);
						} else {
							btnEdit.setText(R.string.delete_selected);
						}
					}
					for (int i = 0 ; i < mFavorFoods.size() ; i++){
						Food of = mFavorFoods.get(i);
						if (f.getFoodName().equals(of.getFoodName()) 
								&& f.getRelatedDisease().equals(of.getRelatedDisease())){
							of.setSelected(isChecked);
							break;
						}
					}
				}
			};
		}
		@Override
		public int getCount() {
			return mFavorFoodsTmp.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mFavorFoodsTmp.get(arg0);
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
			String foodname = mFavorFoodsTmp.get(position).getFoodName();
			int flgFit = mFavorFoodsTmp.get(position).getCanEatInt();
			String pinyin = mFavorFoodsTmp.get(position).getFoodImgURL();
			String disease = mFavorFoodsTmp.get(position).getRelatedDisease();
			String imgUrl ;
			if ( !EaterApplication.READ_HTTP_CACHE ){
				imgUrl = StorageManager.instance().getImageURLByName(pinyin);
			} else {
				imgUrl = pinyin;
			}
			Bitmap bm = ImageLoader.getImageCacheByUrl(imgUrl);

			if (convertView == null){
				convertView = mInflater.inflate(R.layout.favor_list_item, null);
				ImageView bgImg = (ImageView) convertView.findViewById(R.id.search_ret_item_img);
				fit = (ImageView) convertView.findViewById(R.id.search_ret_item_fit);
				TextView imgDesp = (TextView) convertView.findViewById(R.id.search_ret_item_name);
				CheckBox box = (CheckBox) convertView.findViewById(R.id.checkBox1);
				
				imgAndText = new ViewHoder();
				imgAndText.mCheckBox = box;
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
				if ( flgFit == EaterApplication.YI){
					fit.setImageResource(R.drawable.yi);					
				} else if (flgFit == EaterApplication.JI){
					fit.setImageResource(R.drawable.ji);
				} else if (flgFit == EaterApplication.KE){
					fit.setImageResource(R.drawable.ke);
				} else {
					fit.setVisibility(View.INVISIBLE);
				}
			}
			if ( bm == null ){
				bm = defaultBM;
			}
			int foodPos = position;
			int resId = mFavorFoodsTmp.get(foodPos).getFoodImgResId();
			LogWorker.i("bm = " + bm);
			imgAndText.bgImg.setImageBitmap(Tool.getScaleImageByScaleOfWinWidth(mContext, bm, p));
			imgAndText.bgImg.setTag(resId);
			imgAndText.imgDesp.setText(disease + "+" + foodname);
			imgAndText.mCheckBox.setId(foodPos);
			imgAndText.mCheckBox.setOnCheckedChangeListener(checkListner);
			imgAndText.mCheckBox.setChecked(false);
			
			if (isEditing){
				imgAndText.mCheckBox.setVisibility(View.VISIBLE);
			} else {
				imgAndText.mCheckBox.setVisibility(View.GONE);
			}
			return convertView;
		}
		class ViewHoder{
			ImageView bgImg;
			ImageView fit;
			TextView imgDesp;
			CheckBox mCheckBox;
		}
	}
	class BaseIllAdater extends BaseAdapter{

		Context mContext;
		
		public BaseIllAdater(Context c){
			mContext = c;
		}
		@Override
		public int getCount() {
			return illSet.length;
		}

		@Override
		public Object getItem(int arg0) {
			return illSet[arg0];
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			TextView illText;
			if (arg1 == null){
				arg1 = mInflater.inflate(R.layout.ill_list_view_item, null);
				illText = (TextView) arg1.findViewById(R.id.ill_item);
				arg1.setTag(illText);
			} else {
				illText = (TextView) arg1.getTag();
			}
			if (illSet[arg0] != 0){
				illText.setText(mContext.getText(illSet[arg0]));
			} else {
				illText.setText(getText(R.string.all_disease));
			}
			return arg1;
		}

		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		stopDialog();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		MobclickAgent.onPause(this);
		super.onPause();
	}

	@SuppressLint("NewApi")
	private void onBack(){
//		Intent intent = new Intent(this,fromActivity.getClass());
//		startActivity(intent);
		finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        
//		EattingOrNotMainActivity.displayMyself(this, true);
	}
	@SuppressLint("NewApi")
	@Override
	public void onBackPressed() {
		onBack();
		super.onBackPressed();
	}
	
	@Override
	protected void onResume() {
		mNaviBar.changeNavibar(1);
//		doSearchFavorFoods();
		refreshData();
		MobclickAgent.onResume(this);
		super.onResume();
	}
	private void refreshData(){
		SimpleAsynHttpDowload refreshLoader = new SimpleAsynHttpDowload(HttpLoader.getInstance(this));
		tvFavorNum.setText(mFavorFoodsTmp.size() + "     正在刷新..");
		if (searchTask != null){
			refreshLoader.execute(searchTask);
		}
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	
}
