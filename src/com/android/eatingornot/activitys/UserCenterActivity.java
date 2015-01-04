package com.android.eatingornot.activitys;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import com.android.eatingornot.datamodel.UserInfo;
import com.android.eatingornot.datapraser.HttpDataPraser;
import com.android.eatingornot.httpparams.UnBangdinglParams;
import com.android.eatingornot.tool.Tool;
import com.android.eatingornot.weibo.WeiboEntity;
import com.umeng.analytics.MobclickAgent;

import ccb.android.net.framkwork.HttpLoader;
import ccb.android.net.framkwork.ImageLoader;
import ccb.android.net.framkwork.SimpleAsynHttpDowload;
import ccb.android.net.framkwork.HttpLoader.Task;
import ccb.android.net.framkwork.HttpLoader.dataParser;
import ccb.android.net.framkwork.HttpLoader.downLoadListener;
import ccb.android.net.framkwork.ImageLoader.ImageLoaderListener;
import ccb.java.android.utils.LogWorker;
import ccb.java.android.utils.StorageManager;
import ccb.java.android.utils.StringUtil;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserCenterActivity extends Activity {

	TextView tvUserName,tvWebSite,titleView;
	Button btnLoginOut,btnFeedback,btnAppraise,btnBack,btnBangding;
	ImageView ivUserHead;
	
	EaterApplication mApp;
	UserInfo userInfo;
	private ProgressDialog dialog;
	
	static Activity fromActivity;
	@SuppressLint("NewApi")
	public static void displayMyself(Activity from){
		fromActivity = from;
		Intent intent = new Intent(from,UserCenterActivity.class);
		from.startActivity(intent);
        from.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//        from.finish();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_center);
		MobclickAgent.onError(this);
		
		mApp = (EaterApplication) getApplication();
		userInfo = mApp.getUserInfo();
		initView();
		fetchUserHead();
		
	}

	private void initView(){
		titleView = (TextView) findViewById(R.id.title_text);
		titleView.setText(R.string.user_center);
		
		tvUserName = (TextView) findViewById(R.id.username);
		
		if (mApp.isWeiboLogin()){
			tvUserName.setText(userInfo.getWeiboName());
		} else {
			tvUserName.setText(userInfo.getUserName());
		}
		
		tvWebSite = (TextView) findViewById(R.id.web_site);
		
		btnAppraise = (Button) findViewById(R.id.btn_appraise);
		btnFeedback = (Button) findViewById(R.id.btn_feedcak);
		btnLoginOut = (Button) findViewById(R.id.btn_login_out);
		btnBack = (Button) findViewById(R.id.title_btn_left);
		btnBangding = (Button) findViewById(R.id.btn_bangding);
		if (!mApp.isLogin()){
			btnBangding.setVisibility(View.GONE);
		}
		if (mApp.isWeiboLogin){
			btnBangding.setText(getText(R.string.un_bangding_weibo));
		} else {
			btnBangding.setText(getText(R.string.bangding_weibo));
		}
		OnClickListener listener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_appraise:
					onAppraise();
					break;
				case R.id.btn_feedcak:
					onFeedback();
					break;
				case R.id.btn_login_out:
					onLoginOut();
					break;
				case R.id.title_btn_left:
					onBack();
					break;
				case R.id.btn_bangding:
					onBangding();
					break;
				default:
					break;
				}
				
			}

		};
		btnBangding.setOnClickListener(listener);
		btnBack.setVisibility(View.VISIBLE);
		btnBack.setOnClickListener(listener);
		btnAppraise.setOnClickListener(listener);
		btnFeedback.setOnClickListener(listener);
		btnLoginOut.setOnClickListener(listener);
		
		ivUserHead = (ImageView) findViewById(R.id.user_head);
		ivUserHead.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
//				okOrCancelDialog(getText(R.string.user_img_is_not_open).toString(),
//						getText(R.string.sweat_tip).toString(), new DialogInterface.OnClickListener() {
//							
//							@Override
//							public void onClick(DialogInterface dialog, int which) {
//								dialog.dismiss();
//								
//							}
//						}, null);
				changeUserImg();
			}
		});
		mLoader = new ImageLoader(this);
		mLoader.setListener(new ImageLoaderListener() {
			
			@Override
			public void imgDidLoad(String imgUrl) {
				if (imgUrl != null){
					showUserHead(Tool.getScaleImageByScaleOfWinWidth(
							UserCenterActivity.this, ImageLoader.getImageCacheByUrl(imgUrl), 0.3f));
				}
			}
		});
		
	}
	ImageLoader mLoader;
	private void fetchUserHead(){
		Bitmap bmHead = ImageLoader.getImageCacheByUrl(mApp.getUserInfo().getUserID());
		if (bmHead == null){
			bmHead = BitmapFactory.decodeResource(getResources(), R.drawable.touxiang);
		}
		showUserHead(Tool.getScaleImageByScaleOfWinWidth(
				this, bmHead, 0.3f));
		LogWorker.i("userImgUrl:" + userInfo.getUserImgUrl());
		String url = userInfo.getUserImgUrl();
		if (mApp.isWeiboLogin() && !mApp.isLogin()){
			url = userInfo.getWeiboImgUrl();
			mLoader.fetch(url);
		}
	}
	private void showUserHead(Bitmap bm){
		
		ivUserHead.setImageBitmap( 
				Tool.getRoundedCornerBitmap(
						this, bm, 5, bm.getWidth(), bm.getHeight(), false, false, false, false));
	}
	private AlertDialog adialog;
	File sdcardTempFile;
	int crop = 96;
	private void changeUserImg(){
		
		 sdcardTempFile = new File(StorageManager.instance().getImgCacheFullPath(mApp.getUserInfo().getUserID() + "tmp"));
		if (adialog == null) {
			adialog = new AlertDialog.Builder(this).setItems(new String[] { "照一张", "从相册选一张" }, 
			new DialogInterface.OnClickListener() {
			@Override
								public void onClick(DialogInterface dialog, int which) {
									if (which == 0) {
										String status = Environment.getExternalStorageState();
										if (status.equals(Environment.MEDIA_MOUNTED)) {
											Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
											intent.putExtra("output", Uri.fromFile(sdcardTempFile));
											startActivityForResult(intent, 101);  
										 } else {
											 Toast.makeText(UserCenterActivity.this, "没有SD卡", Toast.LENGTH_LONG).show(); 
										 }
										
									} else {
										Intent intent = new Intent("android.intent.action.PICK");
										intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
										intent.putExtra("output", Uri.fromFile(sdcardTempFile));
										intent.putExtra("crop", "true");
										intent.putExtra("aspectX", 1);// 裁剪框比例
										intent.putExtra("aspectY", 1);
										intent.putExtra("outputX", crop);// 输出图片大小
										intent.putExtra("outputY", crop);
										startActivityForResult(intent, 100);
									}
								}
							}).create();
						}
						if (!adialog.isShowing()) {
							adialog.show();
						}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Bitmap bmp = null;
		if (resultCode == RESULT_OK) {
			if (requestCode == 101){
				//剪裁图片
				LogWorker.i("剪裁图片..");
				Intent intent1 = new Intent("com.android.camera.action.CROP");  
		        intent1.setDataAndType(Uri.fromFile(sdcardTempFile), "image/*");
		        intent1.putExtra("crop", "true");  
		        // aspectX aspectY 是宽高的比例  
		        intent1.putExtra("aspectX", 1);  
		        intent1.putExtra("aspectY", 1);  
		        // outputX outputY 是裁剪图片宽高  
		        intent1.putExtra("outputX", crop);  
		        intent1.putExtra("outputY", crop);  
		        intent1.putExtra("return-data", true);  
		        startActivityForResult(intent1, 99);  
			} else{
				if (requestCode == 99) {  
		            Bundle extras = intent.getExtras();  
		            if (extras != null) {  
		                bmp = extras.getParcelable("data");  
		            }  
		  
				} else if (requestCode == 100){
					bmp = BitmapFactory.decodeFile(sdcardTempFile.getAbsolutePath());
				}
				Bitmap ret = Tool.getScaleImageByScaleOfWinWidth(this, bmp, 0.3f);
				StorageManager.instance().setImageCache(mApp.getUserInfo().getUserID(), ret);
				showUserHead(ret);
			}


		} else {
			LogWorker.showToast(UserCenterActivity.this, "返回照片失败");
		}
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
		if ( !isFinishing() ){
			error_dlg.show();
		}
	}
	protected void okOrCancelDialog(String msg,String tit,
			DialogInterface.OnClickListener forOk,DialogInterface.OnClickListener forCancel) {
		  AlertDialog.Builder builder = new Builder(this);
		  builder.setMessage(msg);

		  builder.setTitle(tit);
		  if (forOk != null){
			  builder.setPositiveButton(getText(R.string.ok), forOk);  
		  }

		  if (forCancel != null){
			  builder.setNegativeButton(R.string.cancel, forCancel);
		  }
		  builder.create().show();
	}
	private void onBangding() {
		// TODO Auto-generated method stub
		LogWorker.i("onBangding..");
		WeiboEntity weiboEntity = new WeiboEntity(this,UserCenterActivity.class);
		if (btnBangding.getText().toString().equals(getText(R.string.un_bangding_weibo))){
			LogWorker.i("un_bangding_weibo..");
			okOrCancelDialog(getText(R.string.want_to_un_bangding_weibo).toString(),
					getText(R.string.sweat_tip).toString(), 
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
		    				unBangding(mApp.getUserInfo().getWeiboId(),
		    						mApp.getUserInfo().getUserID());
							
						}
					},
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							
						}
					});
		} else {
			LogWorker.i("bangding_weibo..");
			weiboEntity.bangding(mApp.getUserInfo().getUserID());
		}
	}
	UserInfo uInfo;
	public void unBangding(String weiboID,String uid){
		final EaterApplication mApp = (EaterApplication) getApplication();
		Task unBangdingTask = HttpLoader.getInstance(this).getTask(
				new  UnBangdinglParams(weiboID, uid));

		unBangdingTask.setDataParser(new dataParser() {			
			@Override
			public boolean onDataParser(Task t, InputStream is) {
				if (is == null)return false;
//				bMsg = HttpDataPraser.getFavorRetMsg(is);
				uInfo = HttpDataPraser.getUserInfo(is);
				return true;
			}
		});
		unBangdingTask.setDownLoadDoneListener(new downLoadListener() {
			
			@Override
			public void onDownLoadDone(int taskId, String result) {
				stopDialog();
				if (result.equals(HttpLoader.DOWLOAD_SUCCESS)){
					if (uInfo.getErrorMsg() == 0){
						mApp.setUserInfo(uInfo);
				        Intent intent = new Intent();
				        intent.setClass(UserCenterActivity.this, UserCenterActivity.class);
				        startActivity(intent);
				        finish();
					} else {
						Toast.makeText(UserCenterActivity.this,  getText(R.string.un_bangding_weibo_failed), Toast.LENGTH_SHORT
						).show();
						mApp.setWeiboLogin(true);
					}
				} else {
					Toast.makeText(UserCenterActivity.this,  getText(R.string.un_bangding_weibo_failed), Toast.LENGTH_SHORT
					).show();
					mApp.setWeiboLogin(true);
				}

			}
			
		});
		
		//执行注册

		final SimpleAsynHttpDowload loader = new SimpleAsynHttpDowload(
				HttpLoader.getInstance(this));
		showDialog(getText(R.string.sweat_tip).toString(),
				getText(R.string.is_doing).toString(), new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				loader.cancel(true);
			}
		});
		loader.execute(unBangdingTask);
	}
	protected void onLoginOut() {
		LogWorker.i("onLoginOut");
		
		okOrCancelDialog(getText(R.string.tip_for_login_out).toString(),getText(R.string.sweat_tip).toString(),
				new DialogInterface.OnClickListener() {

			   @Override
				   public void onClick(DialogInterface dialog, int which) {
				    UserLoger.upload(UserCenterActivity.this, getText(R.string.UserLoginOut).toString(), getText(R.string.login_out).toString());
				    dialog.dismiss();
				    mApp.setLoginOut(true);
				    mApp.getUserInfo().setPWD(StringUtil.decodeString(mApp.getUserInfo().getPWD()));
					EattingOrNotMainActivity.displayMyself(UserCenterActivity.this,true);
				   }
			  },
			  new DialogInterface.OnClickListener() {

				   @Override
				   public void onClick(DialogInterface dialog, int which) {
				    dialog.dismiss();
				   }
				  });
		
	}

	protected void onFeedback() {
		LogWorker.i("onFeedback");
		FeedbackActivity.displayMyself(this);
		
	}

	protected void onAppraise() {
		LogWorker.i("onAppraise");
		UserLoger.upload(this, getText(R.string.appraise_at_market).toString(), getText(R.string.action_appraise).toString());
		Uri uri = Uri.parse("market://search?q=pname:com.android.eatingornot.activitys"); 
		Intent it = new Intent(Intent.ACTION_VIEW, uri); 
		startActivity(it);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		stopDialog();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		MobclickAgent.onResume(this);
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@SuppressLint("NewApi")
	private void onBack(){
//		Intent intent = new Intent(this,fromActivity.getClass());
//		startActivity(intent);
		finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
       
	}
	@SuppressLint("NewApi")
	@Override
	public void onBackPressed() {
		onBack();
		super.onBackPressed();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		MobclickAgent.onPause(this);
		super.onPause();
	}
}
