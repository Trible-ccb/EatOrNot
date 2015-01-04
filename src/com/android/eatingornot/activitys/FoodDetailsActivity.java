/**
 * 
 */
package com.android.eatingornot.activitys;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import ccb.android.net.framkwork.HttpLoader;
import ccb.android.net.framkwork.ImageLoader;
import ccb.android.net.framkwork.HttpLoader.CacheHelper;
import ccb.android.net.framkwork.HttpLoader.Task;
import ccb.android.net.framkwork.HttpLoader.dataParser;
import ccb.android.net.framkwork.HttpLoader.downLoadListener;
import ccb.android.net.framkwork.ImageLoader.ImageLoaderListener;
import ccb.android.net.framkwork.SimpleAsynHttpDowload;
import ccb.java.android.utils.FileUtils;
import ccb.java.android.utils.LogWorker;
import ccb.java.android.utils.StorageManager;

import com.android.eatingornot.data.FoodMix;
import com.android.eatingornot.data.ParseFoodMix;
import com.android.eatingornot.data.ParseRecommendFood;
import com.android.eatingornot.datamodel.FoodOfDiseaseBean;
import com.android.eatingornot.datamodel.RequestRetMsg;
import com.android.eatingornot.datamodel.Food;
import com.android.eatingornot.datapraser.HttpDataPraser;
import com.android.eatingornot.httpparams.AddFavorParams;
import com.android.eatingornot.httpparams.CheckFavorParams;
import com.android.eatingornot.httpparams.DelFavorParams;
import com.android.eatingornot.httpparams.FoodDetailParams;
import com.android.eatingornot.httpparams.SearchFoodOfDiseaseParams;
import com.android.eatingornot.service.DieseaseWithFoodService;
import com.android.eatingornot.tool.Tool;
import com.android.eatingornot.weibo.WeiBoData;
import com.android.eatingornot.weibo.WeiboEntity;
import com.android.eatingornot.widget.CustomNaviBar;
import com.android.eatingornot.widget.ImageViewWithText;
import com.umeng.analytics.MobclickAgent;
import com.weibo.net.AccessToken;
import com.weibo.net.Oauth2AccessTokenHeader;
import com.weibo.net.OldShareActivity;
import com.weibo.net.Utility;
import com.weibo.net.Weibo;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author 陈垂波
 *
 */
@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.ECLAIR)
public class FoodDetailsActivity extends Activity {

	ImageView foodView;
	TextView foodDespView;
	TextView titleView;
	Button tvShare,btnHome;
	Button btnBack,btnFavor;
	ViewPager viewPaper;
	
	ImageView foodMixIsFitImg;
	TextView titAdvise,contentAdvise,titEatAmount,contentEatAmount,
	titEatSkill,contentEatSkill,titEatAction,contentEatAction,titInfoSource,
	contentInfoSource,titDetailReason,contentDetailReason,titFoodMacth,
	contentFoodMatchTV,titRecFood,contentRecFoodTV,foodMixIsFitReason;
	
	LinearLayout contentFoodMacth,contentRecFood;
	
	Food curFood;
	String sickName;
	String foodName;
	String titleText;
	
    DieseaseWithFoodService mDieseaseWithFoodService;
//    DiseaseWithFood mDiseaseWithFood;
    FoodOfDiseaseBean mFoodOfDiseaseBean;
    
    ArrayList<FoodMix> mArrayFoodMix;
    ArrayList<Food>    mArrayFood;
    
    static int openDetailActivities = 0;
    static Activity fromActivity;
    int mFoodMatchsNumber = 0;
    int mFoodNumber = 0;
	float recFoodItemWidthPersentBySceneWidth = 0.25f;
	
	LinearLayout hsvLayout;
	LinearLayout footMatchlayout;
	LayoutInflater mInflater;
	Animation ainmToLeft,animToRight;
	EaterApplication mApp;
	static ArrayList<Activity> mDetailsActivity;
	private ParseFoodMix mParseFoodMix;
	private ParseRecommendFood mParseRecommendFood;
	private CustomNaviBar mNaviBar;
	
	public static String SICK_NAME = "sick_name";
	public static String FOOD_NAME = "food_name";
	private static boolean WANT_SHARE = false;
	public static enum FoodKind{
		eFit,
		eUnfit,
		eCanEat;
	}
	@TargetApi(Build.VERSION_CODES.ECLAIR)
	public static void displayMyself(Activity from){
		fromActivity = from;
		Intent intent = new Intent(from,FoodDetailsActivity.class);
		from.startActivity(intent);
        from.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//		if (fromActivity.getClass().getSimpleName().equals(SeachFoodsActivity.class.getSimpleName())){
//	        from.finish();
//		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.food_details);
		MobclickAgent.onError(this);
		UserLoger.upload(this, getText(R.string.FoodDetail).toString(), getText(R.string.action_setup).toString());
		mInflater = getLayoutInflater();
		if (mDetailsActivity == null){
			mDetailsActivity = new ArrayList<Activity>();
		}
		curFood = SeachFoodsActivity.getCurFood();
		sickName = curFood.getRelatedDisease();
		foodName = curFood.getFoodName();
		mApp = (EaterApplication)getApplication();
		titleText = sickName + "+" + foodName;
		initView();
        checkWantedShare();
		ainmToLeft = AnimationUtils.loadAnimation(this, R.anim.right_to_left);
		animToRight = AnimationUtils.loadAnimation(this, R.anim.left_to_right);
		doFoodDetailTask();
	}
	private void initView(){
		
		mNaviBar = new CustomNaviBar(this, 0);
		
		tvShare = (Button) findViewById(R.id.share_to_weibo);
		titleView = (TextView) findViewById(R.id.title_text);
		btnBack = (Button) findViewById(R.id.title_btn_left);
		btnFavor = (Button) findViewById(R.id.btnFavor);
		btnHome = (Button) findViewById(R.id.title_btn_right);
		btnHome.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.back_to_home, 0);
		
		foodView = (ImageView) findViewById(R.id.bg_image);
		foodDespView = (TextView) findViewById(R.id.img_dscp);

		contentFoodMacth = (LinearLayout) findViewById(R.id.rec_foods_macth)
		.findViewById(R.id.content_view);


		View lv = mInflater.inflate(R.layout.food_match_item, null);
		footMatchlayout = (LinearLayout)lv.findViewById(R.id.food_match_layout); 
		contentFoodMacth.addView(footMatchlayout);
		
		//food match layout childs
		btnPre = (ImageButton)footMatchlayout.findViewById(R.id.btn_prev);
		btnNext = (ImageButton)footMatchlayout.findViewById(R.id.btn_next);
		food1 = (ImageView)footMatchlayout.findViewById(R.id.food_1).findViewById(R.id.bg_image);
		food1Text = (TextView)footMatchlayout.findViewById(R.id.food_1).findViewById(R.id.img_dscp);
		food2 = (ImageView)footMatchlayout.findViewById(R.id.food_2).findViewById(R.id.bg_image);
		food2Text = (TextView)footMatchlayout.findViewById(R.id.food_2).findViewById(R.id.img_dscp);
		foodMixIsFitImg = (ImageView) findViewById(R.id.is_fit_img);
		foodMixIsFitReason = (TextView) findViewById(R.id.is_fit_reason);
		
		contentFoodMatchTV = (TextView)findViewById(R.id.rec_foods_macth).findViewById(R.id.content);
		
		contentRecFoodTV = (TextView)findViewById(R.id.rec_foods).findViewById(R.id.content);
		contentRecFood = (LinearLayout) findViewById(R.id.rec_foods).findViewById(R.id.content_view);
		contentRecFoodTV.setText(getText(R.string.none).toString());
		hsvLayout = new LinearLayout(this);
		
        //能吃不能吃
		titAdvise = (TextView) findViewById(R.id.advise)
		.findViewById(R.id.content_title);
		contentAdvise = (TextView) findViewById(R.id.advise)
		.findViewById(R.id.content);
		 //能吃不能吃 详细原因
		titDetailReason = (TextView) findViewById(R.id.details_reason)
		.findViewById(R.id.content_title);
		contentDetailReason = (TextView) findViewById(R.id.details_reason)
		.findViewById(R.id.content);
		 //食物搭配禁忌与食物推荐
		titFoodMacth = (TextView) findViewById(R.id.rec_foods_macth)
		.findViewById(R.id.content_title);
		findViewById(R.id.rec_foods_macth)
		.findViewById(R.id.content).setVisibility(View.GONE);
		titRecFood = (TextView) findViewById(R.id.rec_foods)
		.findViewById(R.id.content_title);
		findViewById(R.id.rec_foods)
		.findViewById(R.id.content).setVisibility(View.GONE);
		 //摄入量
		titEatAmount = (TextView) findViewById(R.id.eat_amount)
		.findViewById(R.id.content_title);
		contentEatAmount = (TextView) findViewById(R.id.eat_amount)
		.findViewById(R.id.content);
		titEatAmount.setText(R.string.eat_amount);
		
		 //食用禁忌
		titEatAction = (TextView) findViewById(R.id.eat_action)
		.findViewById(R.id.content_title);
		contentEatAction = (TextView) findViewById(R.id.eat_action)
		.findViewById(R.id.content);
		titEatAction.setText(R.string.eat_action);
		 //食用技巧
		titEatSkill = (TextView) findViewById(R.id.eat_skill)
		.findViewById(R.id.content_title);
		contentEatSkill = (TextView) findViewById(R.id.eat_skill)
		.findViewById(R.id.content);
		titEatSkill.setText(R.string.eat_skill);
		 //信息来源
		titInfoSource = (TextView) findViewById(R.id.info_source)
		.findViewById(R.id.content_title);
		contentInfoSource = (TextView) findViewById(R.id.info_source)
		.findViewById(R.id.content);
		titInfoSource.setText(R.string.info_source);
		titInfoSource.setVisibility(View.GONE);
		
		titAdvise.setText(getText(R.string.eating_or_not));
		titDetailReason.setText(getText(R.string.details_reason));
		titFoodMacth.setText(R.string.foods_macth_fitornot);
		titRecFood.setText(R.string.rec_foods);
		
		OnClickListener lstner = new OnClickListener() {			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.title_btn_left:
					onBack();
					break;
				case R.id.btn_prev:
					showPrevMath();
					break;
				case R.id.btn_next:
					showNextMatch();
				break;
				case R.id.btnFavor:
					onFavor();
				break;	
				case R.id.share_to_weibo:
					clickShareToWeibo();
					break;
				case R.id.title_btn_right:
					onHome();
					break;
				default:
					break;
				}
				
			}
		};
		tvShare.setOnClickListener(lstner);
		btnBack.setOnClickListener(lstner);
		btnBack.setVisibility(View.VISIBLE);
		btnFavor.setOnClickListener(lstner);
		btnFavor.setVisibility(View.VISIBLE);
		btnFavor.setTag(false);
		btnHome.setVisibility(View.VISIBLE);
		btnHome.setOnClickListener(lstner);
		
//		refreshFavorButton();
		
		contentAdvise.setVisibility(View.VISIBLE);
		contentDetailReason.setVisibility(View.VISIBLE);
		

		titleView.setText(titleText);
		showFoodView(true);

		btnNext.setOnClickListener(lstner);
		btnPre.setOnClickListener(lstner);
		btnPre.setVisibility(View.INVISIBLE);
		if (curFoodMatchIndex == mFoodMatchsNumber -1){
			btnNext.setVisibility(View.INVISIBLE);
		}
		showAndHideViewByFoodKind();
		
	}
	String spaceHeader = "";
	private String formatStringOfContent(String s){
		String str = s.trim()
		.replace("，", ", ").replace("。", ". ").replace("、", ", ")
		.replace("：", ": ").replace("(", "（").replace(")", "）")
		.replace("；", "; ").replace("‘", "' ").replace("’", "' ").replace("\n\n", "\n");
		if (TextUtils.isEmpty(str)){
			str = getText(R.string.none).toString();
		}
		return str;
	}
	private void showDataToViews(){
		
	if(null != mFoodOfDiseaseBean){
        mParseFoodMix = new ParseFoodMix(
        		mFoodOfDiseaseBean.getRecommend_food_mix(),
                this);
        mParseRecommendFood = new ParseRecommendFood(
        		mFoodOfDiseaseBean.getRecommend_food(),
                this);
        
        mArrayFood = mFoodOfDiseaseBean.getRecommend_foods();
        mFoodNumber = mArrayFood == null ? 0 : mArrayFood.size();
        LogWorker.i( "before remove mFoodNumber = " + mFoodNumber);
        for (int i = mFoodNumber - 1 ; i>=0 ; i--){
        	Food f = mArrayFood.get(i);
        	if (TextUtils.isEmpty(f.getFoodName())){
        		mArrayFood.remove(i);
        	}
        }
//        mFoodMatchsNumber = mArrayFoodMix.size();
        mFoodNumber = mArrayFood == null ? 0 : mArrayFood.size();
        LogWorker.i("mFoodMatchsNumber = " + mFoodMatchsNumber
        		+ "mFoodNumber = " + mFoodNumber);
        initFavorButton();
        
        
	      contentAdvise.setText(spaceHeader + mFoodOfDiseaseBean.getCanEat());
//	      Spanned htmlString = Html.fromHtml(mFoodOfDiseaseBean.getReason() +
//	      		"<br /><font color=\"red\">" + getString(R.string.rec_eat_amount) + "</font><br />"
//	      		+ mFoodOfDiseaseBean.getRecommendEatAmount());
	      contentDetailReason.setText(formatStringOfContent(spaceHeader + mFoodOfDiseaseBean.getReason()));
	      contentEatAmount.setText(formatStringOfContent(spaceHeader + mFoodOfDiseaseBean.getRecommendEatAmount()));
	      contentEatAction.setText(formatStringOfContent(spaceHeader + mFoodOfDiseaseBean.getEatAction()));
	      contentEatSkill.setText(formatStringOfContent(mFoodOfDiseaseBean.getEatSkill()));
	      String strForinfo = formatStringOfContent(mFoodOfDiseaseBean.getInfoSource());
	      contentInfoSource.setText(strForinfo);
	      contentInfoSource.setVisibility(View.GONE);
		}
	if (mFoodMatchsNumber == 0){
		contentFoodMatchTV.setVisibility(View.VISIBLE);
		footMatchlayout.setVisibility(View.GONE);
	} else {
		initFoodMathchView(0);
	}
	HorizontalScrollView hScrollView = new HorizontalScrollView(this);
	LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
	slp.setMargins(10, 10, 10, 10);
	hScrollView.setLayoutParams(slp);
	contentRecFood.addView(hScrollView);
	
	hsvLayout.setLayoutParams(slp);
	hScrollView.addView(hsvLayout);
	if ( mFoodNumber == 0){
		contentRecFoodTV.setVisibility(View.VISIBLE);
		hScrollView.setVisibility(View.GONE);
	}

		getRecFoodView();
	    
	    showFoodView(true);
	}
	ImageLoader recFoodImgLoader;
	HashMap<String, ImageViewWithText> recFoodsView = new HashMap<String, ImageViewWithText>();
	
	private void getRecFoodView(){
		recFoodImgLoader = new ImageLoader(this);
		recFoodImgLoader.setListener(new ImageLoaderListener() {
			
			@Override
			public void imgDidLoad(String imgUrl) {
				if (imgUrl != null){
					showRecFoodView(imgUrl);
				}
			}
		});
		OnClickListener recFoodListner = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LogWorker.i("onclick rec food id = " + v.getId());
				if (v.getId() < mArrayFood.size() ){
					Food food = mArrayFood.get(v.getId());
					LogWorker.i("onclick rec food is can eat:" + food.getCanEatInt());
			    	if ( food.getCanEatInt() == -1 || TextUtils.isEmpty(food.getFoodName()) ){
			    		//here can show sth tips
			    		LogWorker.i(titleText + "	" + titRecFood.getText().toString());
			    		LogWorker.writeToFile(titleText + "	" + titRecFood.getText().toString() + "\n");
			    		LogWorker.showToast(FoodDetailsActivity.this, "数据缺失问题已记录");
			    	} else {
			    		mArrayFood.get(v.getId()).setRelatedDisease(sickName);
			    		SeachFoodsActivity.setCurFood(mArrayFood.get(v.getId()));
			    		Intent intent = new Intent(FoodDetailsActivity.this,FoodDetailsActivity.class);
			    		startActivity(intent);
			            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			            openDetailActivities++;
			            mDetailsActivity.add(FoodDetailsActivity.this);
			    	}
				}

			}
		};
		Bitmap 	defaultImg = ((BitmapDrawable)getResources()
				.getDrawable(R.drawable.default_food)).getBitmap();
	    for ( int i = 0 ; i < mFoodNumber ; i++){
	    	Food food = mArrayFood.get(i);
//	    	if (TextUtils.isEmpty(food.getFoodName())){
//	    		continue;
//	    	}
	        View v = mInflater.inflate(R.layout.customs_image2, null);
	        LinearLayout.LayoutParams lpp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	        lpp.setMargins(5, 0, 5, 0);
	        v.setLayoutParams(lpp);
	        v.setId(i);
	        v.setOnClickListener(recFoodListner);
	    	
	    	String imgURL = StorageManager.instance().getImageURLByName(food.getFoodImgURL());
	    	if (EaterApplication.READ_HTTP_CACHE){
	    		imgURL = food.getFoodImgURL();
	    	}
	    	Bitmap bm = ImageLoader.getImageCacheByUrl(imgURL);
			ImageViewWithText imgWithText = new ImageViewWithText(FoodDetailsActivity.this);
			imgWithText.bgImg = (ImageView) v.findViewById(R.id.bg_image);
			imgWithText.imgDesp = (TextView)v.findViewById(R.id.img_dscp);
			imgWithText.imgFit = (ImageView) v.findViewById(R.id.img_fit);
			imgWithText.textLayout = (RelativeLayout) v.findViewById(R.id.img_text_bottom);
	    	if (bm == null){
	    		recFoodsView.put(imgURL, imgWithText);
	    		if (!EaterApplication.LOCAL_DEBUG){
	    			recFoodImgLoader.fetch(StorageManager.instance().getImageURLByName(food.getFoodImgURL()));
	    		} else {
	    			recFoodImgLoader.fetch(food.getFoodImgURL());
	    		}
	    		bm = defaultImg;
	    	}
		        	        
		    imgWithText.setImageAndText(
		        		bm, food.getFoodName(), recFoodItemWidthPersentBySceneWidth);
		    LogWorker.i("food indx = " + i + " is can eat:" + food.getCanEatInt());
			switch ( food.getCanEatInt() ) {
				case EaterApplication.JI:
					imgWithText.imgFit.setImageResource(R.drawable.ji);
					break;
					
				case EaterApplication.YI:
					imgWithText.imgFit.setImageResource(R.drawable.yi);
					break;
					
				case EaterApplication.KE:
					imgWithText.imgFit.setImageResource(R.drawable.ke);
					break;
				default:
					break;
			}
		    hsvLayout.addView(v);

	    }
	}
	private void showRecFoodView(String url){
		ImageViewWithText t = recFoodsView.get(url);
		Bitmap bm = ImageLoader.getImageCacheByUrl(url);
		String s = t.imgDesp.getText().toString();
		t.setImageAndText(bm, s, recFoodItemWidthPersentBySceneWidth);
	}
	private void initFavorButton(){
		
		if (mApp.isConnectInternet()){
			doCheckFavorTask();
		} else {
			refreshFavorButton();
		}
	}
	Task mFoodDetailTask;
	private ProgressDialog dialog;
	private void showDialog(String title,String msg,DialogInterface.OnCancelListener oncancelListner){
		if (dialog != null){
			dialog = null;
		}
		dialog = ProgressDialog.show(this, title, msg);
		dialog.setCancelable(true);
		dialog.setOnCancelListener(oncancelListner);
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
	private void prepareFoodDetailTask(){
		mFoodDetailTask = HttpLoader.getInstance(this)
		.getTask(new FoodDetailParams(foodName, sickName));
		LogWorker.i("mFoodDetailTask = " + mFoodDetailTask);
		final String fooddetailPath = StorageManager.instance().getAPPCachePath() 
		+ "/" +foodName + sickName +"detail.txt";
		if (EaterApplication.READ_HTTP_CACHE){
				mFoodDetailTask.setCacheHelper(new CacheHelper() {
				
				@Override
				public boolean onStoreDataInCache(Task t, InputStream is) {
					return FileUtils.storeInputStream(
							fooddetailPath, is,"utf-8");
				}
				
				@Override
				public InputStream getCache(Task t) {
					//
					InputStream is = null;
					try {
	//					is = FileUtils.openFileInputStream(fooddetailPath);
						is = getAssets().open("fooddetail.txt");
					} catch (Exception e) {
						LogWorker.i(e.getMessage());
						e.printStackTrace();
					}
					return is;
				}
			});
		}
		mFoodDetailTask.setDataParser(new dataParser() {
			
			@Override
			public boolean onDataParser(Task t, InputStream is) {
				//需将数据流解析存放到Bean
				if (is == null)return false;
				try{
					mFoodOfDiseaseBean = HttpDataPraser.getFoodDetailBean(is);
					return true;
				}catch (Exception e) {
					return false;
				}
			}
		});
		mFoodDetailTask.setDownLoadDoneListener( new downLoadListener() {
			
			@Override
			public void onDownLoadDone(int taskId, String result) {
				if (result == HttpLoader.DOWLOAD_SUCCESS){
					stopDialog();
//					showHotFoodViewPaper();
					showDataToViews();
					
				} else {
					showErrorDlg(getText(
							R.string.sweat_tip).toString(), getText(R.string.hot_food_download_failed).toString());
					
				}
				
			}
		});
	}
	private void doFoodDetailTask(){
		prepareFoodDetailTask();
		showDialog(getText(R.string.sweat_tip).toString(), getText(R.string.food_detail_loading).toString(),null);
		SimpleAsynHttpDowload simLoader = new SimpleAsynHttpDowload(HttpLoader.getInstance(this));
		simLoader.execute(mFoodDetailTask);
		
	}
   RequestRetMsg checkFavorRetMsg;
	private Task prepareCheckFavorTask(){
		Task checkFavorTask;

		LogWorker.i("prepareCheckFavorTask");
		if ( mFoodOfDiseaseBean == null){
			return null;
		}
		checkFavorTask = HttpLoader.getInstance(this).getTask(
				new CheckFavorParams("" + mFoodOfDiseaseBean.getId() , foodName, sickName,mApp.getUserInfo().getUserID()));
		checkFavorTask.setDataParser(new dataParser() {
			
			@Override
			public boolean onDataParser(Task t, InputStream is) {
				checkFavorRetMsg = HttpDataPraser.getFavorRetMsg(is);
				if (checkFavorRetMsg == null){
					return false;
				}
				return true;
			}
		});
		
		checkFavorTask.setDownLoadDoneListener(new downLoadListener() {
			
			@Override
			public void onDownLoadDone(int taskId, String result) {
				if ( result.equals(HttpLoader.DOWLOAD_SUCCESS)){
					stopDialog();
					if (checkFavorRetMsg != null && checkFavorRetMsg.isActionCode()){
//						((Boolean)btnFavor.getTag()).booleanValue();
						btnFavor.setTag(true);
					} else {
						btnFavor.setTag(false);
					}
				} else {
					btnFavor.setTag(false);
				}
				refreshFavorButton();
			}
		});
		return checkFavorTask;
	}
	Task addFavorTask;
	RequestRetMsg addFavorRetMsg;
	private void prepareAddFavorTask(){
		addFavorTask = HttpLoader.getInstance(this).getTask(
				new AddFavorParams("" + mFoodOfDiseaseBean.getId() , foodName, sickName,mApp.getUserInfo().getUserID()));
		addFavorTask.setDataParser(new dataParser() {
			
			@Override
			public boolean onDataParser(Task t, InputStream is) {
				addFavorRetMsg = HttpDataPraser.getFavorRetMsg(is);
				if (addFavorRetMsg == null){
					return false;
				}
				return true;
			}
		});
		
		addFavorTask.setDownLoadDoneListener(new downLoadListener() {
			
			@Override
			public void onDownLoadDone(int taskId, String result) {
				btnFavor.setClickable(true);
				stopDialog();
				if ( result.equals(HttpLoader.DOWLOAD_SUCCESS)){
					
					if (addFavorRetMsg.isActionCode()){
//						((Boolean)btnFavor.getTag()).booleanValue();
						
						btnFavor.setTag(true);
						Toast.makeText(FoodDetailsActivity.this, 
								getText(R.string.add_favor_success).toString(), Toast.LENGTH_SHORT).show();
					} else {
						btnFavor.setTag(false);
						Toast.makeText(FoodDetailsActivity.this, 
								getText(R.string.add_favor_failed).toString(), Toast.LENGTH_SHORT).show();
						
					}
				} else {
					btnFavor.setTag(false);
					showErrorDlg(getText(R.string.sweat_tip).toString(),
							getText(R.string.server_connect_failed).toString());
				}
				refreshFavorButton();
			}
		});
		
	}
	Task delFavorTask;
	RequestRetMsg delFavorRetMsg;
	private void prepareDelFavorTask(){
		delFavorTask = HttpLoader.getInstance(this).getTask(
				new DelFavorParams("" + mFoodOfDiseaseBean.getId() , foodName, sickName,mApp.getUserInfo().getUserID()));
		delFavorTask.setDataParser(new dataParser() {
			
			@Override
			public boolean onDataParser(Task t, InputStream is) {
				delFavorRetMsg = HttpDataPraser.getFavorRetMsg(is);
				if (delFavorRetMsg == null){
					return false;
				}
				return true;
			}
		});
		
		delFavorTask.setDownLoadDoneListener(new downLoadListener() {
			
			@Override
			public void onDownLoadDone(int taskId, String result) {
				btnFavor.setClickable(true);
				stopDialog();
				if ( result.equals(HttpLoader.DOWLOAD_SUCCESS)){
					if (delFavorRetMsg.isActionCode()){
						btnFavor.setTag(false);
						Toast.makeText(FoodDetailsActivity.this, 
								getText(R.string.delected_favor_suc).toString(), Toast.LENGTH_SHORT).show();
					} else {
						btnFavor.setTag(true);
						Toast.makeText(FoodDetailsActivity.this, 
								getText(R.string.delected_favor_failed).toString(), Toast.LENGTH_SHORT).show();
					}
				} else {
					btnFavor.setTag(true);
					showErrorDlg(getText(R.string.sweat_tip).toString(),
							getText(R.string.server_connect_failed).toString());
				}
				refreshFavorButton();
			}
		});
		
	}
	private void doCheckFavorTask(){
		if (!mApp.isWeiboLogin() && !mApp.isLogin()){
			LogWorker.i("mApp is not Login");
			btnFavor.setTag(false);
			refreshFavorButton();
			return;
		}
		Task checkFavorTask = prepareCheckFavorTask();
		SimpleAsynHttpDowload mLoader = new SimpleAsynHttpDowload(HttpLoader.getInstance(this));
		mLoader.execute(checkFavorTask);
	}
	private void doAddFavorTask(){
		prepareAddFavorTask();
		final SimpleAsynHttpDowload mLoader = new SimpleAsynHttpDowload(HttpLoader.getInstance(this));
		showDialog("", "", new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				btnFavor.setClickable(true);
				mLoader.cancel(true);
			}
		});
		btnFavor.setClickable(false);
		mLoader.execute(addFavorTask);
	}
	private void doDelFavorTask(){
		prepareDelFavorTask();
		final SimpleAsynHttpDowload mLoader = new SimpleAsynHttpDowload(HttpLoader.getInstance(this));
		showDialog("", "", new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				btnFavor.setClickable(true);
				mLoader.cancel(true);
			}
		});
		btnFavor.setClickable(false);
		mLoader.execute(delFavorTask);
	}
	private String getShareContent(){
		String s = getText(R.string.tip_for_share).toString();
		String s2 = getText(R.string.tip_for_share2).toString();
		StringBuffer arg = new StringBuffer("");
		switch (curFood.getCanEatInt()) {
		case 0:
			arg.append(sickName + "宜吃" + foodName);
			break;
		case 1:
			arg.append(sickName + "忌吃" + foodName);
			break;
		case 2:
			arg.append(sickName + "可以吃" + foodName);
			break;
		default:
			break;
		}
		
		return String.format(s, arg) + String.format(s2, "" + mFoodOfDiseaseBean.getId());
	}
	@SuppressLint("DefaultLocale")
	private String getShareImgPath(){
		NetworkInfo netInfo = HttpLoader.getInstance(this).getNetworkType();
		if ( netInfo != null && HttpLoader.getInstance(this).isConnectInternet() ){
			int nettype = netInfo.getType();
			int netsubtype = netInfo.getSubtype();
			if (nettype == ConnectivityManager.TYPE_WIFI
					|| netsubtype == TelephonyManager.NETWORK_TYPE_EVDO_0
					|| netsubtype == TelephonyManager.NETWORK_TYPE_EVDO_A
					|| netsubtype == TelephonyManager.NETWORK_TYPE_EVDO_B
					|| netsubtype == TelephonyManager.NETWORK_TYPE_HSDPA
					|| netsubtype == TelephonyManager.NETWORK_TYPE_UMTS){
				if ( hasBigImg){
					return StorageManager.instance().getImgCacheFullPath(bigFoodImgUrl);
				} 
				if ( hasSmImg ){
					return StorageManager.instance().getImgCacheFullPath(StorageManager.instance().getImageURLByName(curFood.getFoodImgURL()));
				}
				return null;
			} else {
//				if ( hasSmImg ){
//					return StorageManager.instance().getImgCacheFullPath(StorageManager.instance().getImageURLByName(curFood.getFoodImgURL()));
//				}
				return null;
			}
		} else {
			return null;
		}
	}
	private void checkWantedShare(){
		if (WANT_SHARE){
			Utility.setAuthorization(new Oauth2AccessTokenHeader());
	        AccessToken token = new AccessToken(mApp.getUserInfo().getWeiboToken(), WeiBoData.app_secret);
	        token.setExpiresIn(mApp.getUserInfo().getWeiboExpiresIn());
	        Weibo.getInstance().setAccessToken(token);
	        WeiboEntity entity = new WeiboEntity(this,FoodDetailsActivity.class);
	        entity.share(getShareContent(), getShareImgPath());
	        WANT_SHARE = false;
	        
		}
	}
	private void clickShareToWeibo(){
		LogWorker.i("clickShareToWeibo");

		Weibo mWeibo = Weibo.getInstance();
        Intent i = new Intent(this, ShareActivity.class);
        i.putExtra(ShareActivity.EXTRA_WEIBO_CONTENT, getShareContent());
        i.putExtra(ShareActivity.EXTRA_PIC_URI, getShareImgPath());
        ShareActivity.setShareContent(getShareContent());
        startActivity(i);

	}
	FoodOfDiseaseBean favorBean = new FoodOfDiseaseBean();
	private void onFavor(){
		if (!mApp.isConnectInternet()){
			showErrorDlg(getText(R.string.sweat_tip).toString()
					, getText(R.string.network_is_not_ok).toString());
			return;
		}
		if (!mApp.isLogin() && !mApp.isWeiboLogin()){
			UserLoginActivity.displayMyself(this);
			return;
		}
		if (mFoodOfDiseaseBean == null){
			showErrorDlg(getText(R.string.sweat_tip).toString()
					, getText(R.string.add_favor_failed).toString());
			return;
		}
		boolean curFavor = ((Boolean)btnFavor.getTag()).booleanValue();
		favorBean.setDiseaseName(mFoodOfDiseaseBean.getDiseaseName());
		favorBean.setFoodName(mFoodOfDiseaseBean.getFoodName());
		if (curFavor){
			//取消收藏
			LogWorker.i("del favor");
			doDelFavorTask();

		} else {
			//加入收藏
			LogWorker.i("add favor");
			doAddFavorTask();
		}
	}
	private void onHome(){
		LogWorker.i("mDetailsActivity.size()="  + mDetailsActivity.size());
			
        	for (int i = (mDetailsActivity.size() - 1) ; i >= 0; i--){
        		mDetailsActivity.get(i).finish();
        	}
        	finish();
        	mDetailsActivity.clear();
        	openDetailActivities = 0;
        	overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}
	
	private void refreshFavorButton(){
		LogWorker.i("reflashFavorButton");
		boolean curFavor = ((Boolean)btnFavor.getTag()).booleanValue();
		if (curFavor){
//			btnFavor.setBackgroundDrawable(getResources().getDrawable(R.drawable.favor_btn_seletor));
			btnFavor.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.not_collect_heart, 0);
		} else {
//			btnFavor.setBackgroundDrawable(getResources().getDrawable(R.drawable.unfavor_btn_seletor));
			btnFavor.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.collected_heart, 0);
		}
		btnFavor.setPadding(Tool.dip2px(FoodDetailsActivity.this, 12), 
				Tool.dip2px(FoodDetailsActivity.this, 2), 
				Tool.dip2px(FoodDetailsActivity.this, 12),
				0);
		
	}
	private void showAndHideViewByFoodKind(){
		int fitKind = SeachFoodsActivity.getCurFood().getCanEatInt();
		if (fitKind == 0){
			//宜吃
			findViewById(R.id.eat_action).setVisibility(View.VISIBLE);
			findViewById(R.id.eat_skill).setVisibility(View.VISIBLE);
			findViewById(R.id.eat_amount).setVisibility(View.VISIBLE);
			findViewById(R.id.rec_foods).setVisibility(View.GONE);
		} else if ( fitKind == 1){
			//忌吃
			findViewById(R.id.eat_action).setVisibility(View.GONE);
			findViewById(R.id.eat_skill).setVisibility(View.GONE);
			findViewById(R.id.eat_amount).setVisibility(View.GONE);
			findViewById(R.id.rec_foods).setVisibility(View.VISIBLE);
			titRecFood.setText(R.string.replace_foods);
		} else {
			//可吃
			findViewById(R.id.eat_action).setVisibility(View.VISIBLE);
			findViewById(R.id.eat_skill).setVisibility(View.VISIBLE);
			findViewById(R.id.eat_amount).setVisibility(View.VISIBLE);
			findViewById(R.id.rec_foods).setVisibility(View.VISIBLE);
			titRecFood.setText(R.string.rec_foods);
		}
	}
	float itemWidthPersentBySceneWidth = 0.2f;
	ImageButton btnPre,btnNext;
	ImageView food1,food2;
	TextView food1Text,food2Text;
	private void initFoodMathchView(int foodIndex){

		//the first food
		int foodPos = foodIndex;
		if ( mArrayFoodMix == null || mFoodMatchsNumber <= foodPos){
			LogWorker.i("get FoodMathch out of bound @:" 
					+ foodPos + " size is :" +mFoodMatchsNumber);
			return;
		}

        //xie
        FoodMix foodTuple = null;
        foodTuple = mArrayFoodMix.get(foodPos);
        
		ImageViewWithText imgWithText_1 = new ImageViewWithText(FoodDetailsActivity.this);
		imgWithText_1.bgImg = food1;
		imgWithText_1.imgDesp = food1Text;
		
            imgWithText_1.setImageAndText(
                    foodTuple.getFirstFood().getFoodImgResId(), 
                    foodTuple.getFirstFood().getFoodName(),
                    itemWidthPersentBySceneWidth);
        //xie
        
		//the second food
		ImageViewWithText imgWithText_2 = new ImageViewWithText(FoodDetailsActivity.this);
		imgWithText_2.bgImg = food2;
		imgWithText_2.imgDesp = food2Text;
        //xie
            imgWithText_2.setImageAndText(
                    foodTuple.getSecondFood().getFoodImgResId(), 
                    foodTuple.getSecondFood().getFoodName(),
                    itemWidthPersentBySceneWidth);
        //xie

            foodMixIsFitReason.setText(getString(R.string.fit_reason) + foodTuple.getmMixReason());
            if (foodTuple.getmCanMix() == 0){
            	foodMixIsFitImg.setImageResource(R.drawable.fit);
            } else if (foodTuple.getmCanMix() == 1){
            	foodMixIsFitImg.setImageResource(R.drawable.unfit);
            } else {
            	foodMixIsFitImg.setVisibility(View.INVISIBLE);
            }
	}
	int curFoodMatchIndex = 0;
	
	private void showNextMatch(){
		if (curFoodMatchIndex < mFoodMatchsNumber - 1){
			initFoodMathchView( ++curFoodMatchIndex);
			food1.startAnimation(animToRight);
			food2.startAnimation(animToRight);
			btnPre.setVisibility(View.VISIBLE);
			if (curFoodMatchIndex == mFoodMatchsNumber -1){
				btnNext.setVisibility(View.INVISIBLE);
			}
		}
	}
	private void showPrevMath(){
		if (curFoodMatchIndex > 0){
			initFoodMathchView( --curFoodMatchIndex);
			food1.startAnimation(ainmToLeft);
			food2.startAnimation(ainmToLeft);
			btnNext.setVisibility(View.VISIBLE);
			if (curFoodMatchIndex == 0){
				btnPre.setVisibility(View.INVISIBLE);
			}
		}
	}
	String bigFoodImgUrl ;
	boolean hasBigImg;
	boolean hasSmImg;
	private void showFoodView(boolean needDown){
		final float itemWidthPersentBySceneWidth = 1.0f;
		if (curFood == null)return;
		String imgName = curFood.getFoodImgURL();
		
		if ( !EaterApplication.READ_HTTP_CACHE ){
			bigFoodImgUrl = StorageManager.instance().getHDImgURLByName(imgName);
		} else {
			bigFoodImgUrl = imgName;
		}
		ImageLoader mImgLoader = new ImageLoader(this);

		Bitmap 	defaultImg = ((BitmapDrawable)getResources()
				.getDrawable(R.drawable.default_food)).getBitmap();
		Bitmap 	smallImg = ImageLoader.getImageCacheByUrl(StorageManager.instance().getImageURLByName(imgName));
		mImgLoader.setListener(new ImageLoaderListener() {			
			@Override
			public void imgDidLoad(String imgUrl) {
				if (imgUrl != null)
				showFoodView(false);
			}
		});
		Bitmap bm = ImageLoader.getImageCacheByUrl(bigFoodImgUrl);
		hasBigImg = true;
		if (bm == null){
			if (needDown){
				mImgLoader.fetch(bigFoodImgUrl);
				if ( smallImg != null){
					bm = smallImg;
					hasSmImg = true; 
				} else {
					bm = defaultImg;
					hasBigImg = false;
					hasSmImg = false;
				}
			} else {
				bm = ((BitmapDrawable)getResources()
						.getDrawable(R.drawable.img_fail)).getBitmap();
				hasBigImg = false;
				hasSmImg = false;
			}

		}
		final ImageViewWithText imgWithText = new ImageViewWithText(this);
		imgWithText.bgImg = foodView;
		imgWithText.imgDesp = foodDespView;
		//must set before setImageAndText(...);
//		imgWithText.imgDesp.setBackgroundResource(R.drawable.black_b);
//		imgWithText.imgDesp.setTextSize(
//				getResources().getDimension(R.dimen.TEXT_SIZE_FOR_IMGDESP));
		imgWithText.setImageAndText(
				bm, curFood.getFoodName(), itemWidthPersentBySceneWidth);
	}
	@Override
	public void onBackPressed() {
		onBack();
		super.onBackPressed();
	}
	@TargetApi(Build.VERSION_CODES.ECLAIR)
	private void onBack() {
		LogWorker.i("openDetailActivities = " + openDetailActivities + " from = " + fromActivity.getClass().getSimpleName());
		if (openDetailActivities == 0 && fromActivity.getClass().getSimpleName().equals(SeachFoodsActivity.class.getSimpleName())){
		} else {
			
		}
		
//		if (openDetailActivities != 0){
			openDetailActivities--;
			LogWorker.i("mDetailsActivity.size() = " + mDetailsActivity.size());
			if (mDetailsActivity.size() > 0){
				mDetailsActivity.remove(mDetailsActivity.size() - 1);
			}
			finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}
	@Override
	protected void onResume() {
		LogWorker.i(this.getClass().getSimpleName()+":onResume");
		mNaviBar.changeNavibar(0);
		MobclickAgent.onResume(this);
		initFavorButton();
		if (EattingOrNotMainActivity.getSickName() == null){
			EattingOrNotMainActivity.displayMyself(this,false);
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
