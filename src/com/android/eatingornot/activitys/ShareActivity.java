/*
 * Copyright 2011 Sina.
 *
 * Licensed under the Apache License and Weibo License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.open.weibo.com
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.eatingornot.activitys;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import ccb.android.net.framkwork.HttpLoader;
import ccb.android.net.framkwork.SimpleAsynHttpDowload;
import ccb.android.net.framkwork.HttpLoader.Task;
import ccb.android.net.framkwork.HttpLoader.dataParser;
import ccb.android.net.framkwork.HttpLoader.downLoadListener;
import ccb.java.android.utils.LogWorker;
import ccb.java.android.utils.StorageManager;

import com.android.eatingornot.datamodel.TokenInfo;
import com.android.eatingornot.datamodel.UserInfo;
import com.android.eatingornot.datapraser.HttpDataPraser;
import com.android.eatingornot.httpparams.UnBangdinglParams;
import com.android.eatingornot.tool.Tool;
import com.android.eatingornot.weibo.WeiBoData;
import com.android.eatingornot.weibo.WeiboEntity;
import com.weibo.net.AccessToken;
import com.weibo.net.AsyncWeiboRunner;
import com.weibo.net.AsyncWeiboRunner.RequestListener;
import com.weibo.net.Utility;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;
import com.weibo.net.WeiboParameters;


@SuppressLint("ResourceAsColor")
public class ShareActivity extends Activity implements OnClickListener, RequestListener {
//    private TextView mTextNum;
    private Button mSend;
    private EditText mEdit;
//    private FrameLayout mPiclayout;

    private EaterApplication mApp;
    private Button mConnectWeibo;
    private TextView mStateText;
    private String mPicPath = "";
    private String mContent = "";
    private String mAccessToken = "";
    private String mTokenSecret = "";

    public static String shareContent;
    public static final String EXTRA_WEIBO_CONTENT = "com.weibo.android.content";
    public static final String EXTRA_PIC_URI = "com.weibo.android.pic.uri";
    public static final String EXTRA_ACCESS_TOKEN = "com.weibo.android.accesstoken";
    public static final String EXTRA_TOKEN_SECRET = "com.weibo.android.token.secret";

    public static final int WEIBO_MAX_LENGTH = 140;

    @SuppressLint("ResourceAsColor")
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.share_view);
        mApp = (EaterApplication) getApplication();
        
        Intent in = this.getIntent();
        mPicPath = in.getStringExtra(EXTRA_PIC_URI);
        LogWorker.i("mPicPath = " + mPicPath);
//        mPicPath = "";
        mContent = in.getStringExtra(EXTRA_WEIBO_CONTENT);
        mAccessToken = in.getStringExtra(EXTRA_ACCESS_TOKEN);
        mTokenSecret = in.getStringExtra(EXTRA_TOKEN_SECRET);

        mConnectWeibo = (Button) findViewById(R.id.connect_weibo);
        mConnectWeibo.setOnClickListener(this);
        mStateText = (TextView) findViewById(R.id.weibo_state);
        Button close = (Button) this.findViewById(R.id.btnClose);
        close.setOnClickListener(this);
        mSend = (Button) this.findViewById(R.id.btnSend);
        mSend.setOnClickListener(this);
//        LinearLayout total = (LinearLayout) this.findViewById(R.id.ll_text_limit_unit);
//        total.setOnClickListener(this);
//        mTextNum = (TextView) this.findViewById(R.id.tv_text_limit);
//        ImageView picture = (ImageView) this.findViewById(R.id.ivDelPic);
//        picture.setOnClickListener(this);

        mEdit = (EditText) this.findViewById(R.id.etEdit);
        mEdit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @SuppressLint("ResourceAsColor")
			public void onTextChanged(CharSequence s, int start, int before, int count) {
                String mText = mEdit.getText().toString();
                int len = mText.length();
                
                if (len > WEIBO_MAX_LENGTH){
                	mEdit.setText(mText.substring(0, WEIBO_MAX_LENGTH));
                	LogWorker.showToast(ShareActivity.this, getText(R.string.already_max_words));
                }
            }
        });
        refreshToken();
        if (!TextUtils.isEmpty(mPicPath)){
            Bitmap bmp = Tool.getScaleImageByScaleOfWinWidth(ShareActivity.this, new File(mPicPath).getPath(), 0.4f);
           	mEdit.setCompoundDrawablesWithIntrinsicBounds(null, null, null, new BitmapDrawable(bmp));
        }
        mEdit.setText(shareContent);
        
    }
    
	@Override
	protected void onResume() {
        reflashData();
		super.onResume();
	}
	TokenInfo tokenInfo;
	private void refreshToken(){
//		Task refreshTokenTask = HttpLoader.getInstance(this).getTask(p)
		if ( !HttpLoader.getInstance(this).isConnectInternet() ){
			return;
		}
		RefreshTokenAsyTask task = new RefreshTokenAsyTask();
		task.execute("");
	}
	class RefreshTokenAsyTask extends AsyncTask<String, Integer, String>{

		@Override
		protected String doInBackground(String... params) {
	        WeiboEntity en = new WeiboEntity(ShareActivity.this, null);
	        try {
	        	tokenInfo = HttpDataPraser.getTokenInfo(en.showTokenInfo());
			} catch (WeiboException e) {
				
				e.printStackTrace();
			}
			if (tokenInfo == null){
				mApp.setWeiboLogin(false);
			} else {
				mApp.refreshTokenInfo(tokenInfo);
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			reflashData();
			super.onPostExecute(result);
		}
		
	}
    public static String getShareContent() {
		return shareContent;
	}

	public static void setShareContent(String shareContent) {
		ShareActivity.shareContent = shareContent;
	}

	@Override
    public void onClick(View v) {
        int viewId = v.getId();
        setShareContent(mEdit.getText().toString());
        if (viewId == R.id.btnClose) {
            finish();
        } else if ( viewId == R.id.btnSend ) {
        	if ( !HttpLoader.getInstance(this).isConnectInternet() ){
        		Toast.makeText(this, this.getString(R.string.network_is_not_ok), Toast.LENGTH_LONG).show();
        		return;
        	}
    		LogWorker.i("mApp.isWeiboLogin = " + mApp.isWeiboLogin()
    				+ "mApp.isTokenValid=" + mApp.isTokenValid()); 
        	if (!mApp.isWeiboLogin() || !mApp.isTokenValid()){
        		Toast.makeText(this, this.getString(R.string.please_login), Toast.LENGTH_LONG).show();
        		doAuthorize();
        		return;
        	}
            Weibo weibo = Weibo.getInstance();
            if (!TextUtils.isEmpty((String) (weibo.getAccessToken().getToken()))) {
			    this.mContent = mEdit.getText().toString();
			    mSend.setText(getText(R.string.action_share) + "..");

			    if (!TextUtils.isEmpty(mPicPath)) {
//                        upload(weibo, WeiBoData.weibo_appkey, this.mPicPath, shareContent, "", "");
			    	new Upload().execute("");

			    } else {
			        // Just update a text weibo!
//                        update(weibo, WeiBoData.weibo_appkey, shareContent, "", "");
			    	new Update().execute("");
			    }
			   
			} else {
			    Toast.makeText(this, this.getString(R.string.please_login), Toast.LENGTH_LONG).show();
			}
			mSend.setText(getText(R.string.action_share));
        }else if (viewId == R.id.connect_weibo) {
        	connectToWeibo();
        } 
//        else if (viewId == R.id.ivDelPic) {
//            Dialog dialog = new AlertDialog.Builder(this).setTitle(R.string.attention)
//                    .setMessage(R.string.del_pic)
//                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            mPiclayout.setVisibility(View.GONE);
//                        }
//                    }).setNegativeButton(R.string.cancel, null).create();
//            dialog.show();
//        }
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
						mApp.unBangdingWeiboData();
						reflashData();
						doAuthorize();
					} else {
						Toast.makeText(ShareActivity.this,  getText(R.string.un_bangding_weibo_failed), Toast.LENGTH_SHORT
						).show();
						mApp.setWeiboLogin(true);
					}
				} else {
					Toast.makeText(ShareActivity.this,  getText(R.string.un_bangding_weibo_failed), Toast.LENGTH_SHORT
					).show();
					mApp.setWeiboLogin(true);
				}

			}
			
		});
		
		//执行注册

		final SimpleAsynHttpDowload loader = new SimpleAsynHttpDowload(HttpLoader.getInstance(this));
		showDialog(getText(R.string.sweat_tip).toString(),
				getText(R.string.is_doing).toString(), new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				loader.cancel(true);
			}
		});
		loader.execute(unBangdingTask);
	}
	
	private void reflashData(){
        LogWorker.i(mApp.getUserInfo().toString());
		LogWorker.i("isWeiboLogin=" + mApp.isWeiboLogin() + " isLogin=" + mApp.isLogin());
		if ( mApp.isLogin() ){
			if (!mApp.isTokenValid() || !mApp.isWeiboLogin()){
				//需要绑定
				mConnectWeibo.setText(R.string.weibo_logging);
				mStateText.setText(getText(R.string.bangding_weibo).toString() + "到" + mApp.getUserInfo().getUserName());
				mStateText.setTag(0);
			} else {
				//重新绑定
				mConnectWeibo.setText(R.string.change_weibo_user);
				mStateText.setText("" +mApp.getUserInfo().getWeiboName());
				mStateText.setTag(1);
			}
		} else {
			if (!mApp.isTokenValid() || !mApp.isWeiboLogin() ){
				//需要登录
				LogWorker.i("weibo login...");
				mConnectWeibo.setText(R.string.weibo_logging);
				mStateText.setText("");
				mStateText.setTag(2);
			} else {
				//切换登录
				LogWorker.i("weibo loginout...");
				mConnectWeibo.setText(R.string.change_weibo_user);
				mStateText.setText(""+mApp.getUserInfo().getWeiboName());
				mStateText.setTag(3);
			}
		}
	}
	ProgressDialog dialog;
	private  void showDialog(String title,String msg,DialogInterface.OnCancelListener cancel){
		if (dialog != null){

			dialog = null;
		}
		dialog = ProgressDialog.show(this, title, msg);
		if ( cancel != null){
			dialog.setCancelable(true);
		}

		dialog.setOnCancelListener(cancel);
		dialog.show();
	}

	private void stopDialog(){
		if (dialog != null && dialog.isShowing()){
			dialog.dismiss();
		}

	}
	protected void okOrCancelDialog(String msg,String tit,
			DialogInterface.OnClickListener forOk,DialogInterface.OnClickListener forCancel) {
		  AlertDialog.Builder builder = new Builder(this);
		  builder.setMessage(msg);

		  builder.setTitle(tit);
		  if (forCancel != null){
			  builder.setNegativeButton(R.string.cancel, forCancel);
		  }
		  if (forOk != null){
			  builder.setPositiveButton(getText(R.string.ok), forOk);  
		  }

		  builder.create().show();
	}
    private String upload(Weibo weibo, String source, String file, String status, String lon,
            String lat) throws WeiboException {
        WeiboParameters bundle = new WeiboParameters();
        bundle.add("source", source);
        bundle.add("pic", file);
        bundle.add("status", status);
        if (!TextUtils.isEmpty(lon)) {
            bundle.add("lon", lon);
        }
        if (!TextUtils.isEmpty(lat)) {
            bundle.add("lat", lat);
        }
        String rlt = "";
        String url = Weibo.SERVER + "statuses/upload.json";
        AsyncWeiboRunner weiboRunner = new AsyncWeiboRunner(weibo);
        weiboRunner.request(this, url, bundle, Utility.HTTPMETHOD_POST, this);

        return rlt;
    }

    private String update(Weibo weibo, String source, String status, String lon, String lat)
            throws MalformedURLException, IOException, WeiboException {
        WeiboParameters bundle = new WeiboParameters();
        LogWorker.i("source key = " + source + " token = " + weibo.getAccessToken().getToken() + "expiresIn = " + weibo.getAccessToken().getExpiresIn() + " status = " + status);
        bundle.add("source", source);
        bundle.add("status", status);
        if (!TextUtils.isEmpty(lon)) {
            bundle.add("lon", lon);
        }
        if (!TextUtils.isEmpty(lat)) {
            bundle.add("lat", lat);
        }
        String rlt = "";
        String url = Weibo.SERVER + "statuses/update.json";
        AsyncWeiboRunner weiboRunner = new AsyncWeiboRunner(weibo);
        weiboRunner.request(this, url, bundle, Utility.HTTPMETHOD_POST, this);
        return rlt;
    }
    class Upload extends AsyncTask< String, String, String>{
		@Override
		protected void onPreExecute() {
        	showDialog("", "", null);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				upload(Weibo.getInstance(), WeiBoData.weibo_appkey, 
						ShareActivity.this.mPicPath, getShareContent(), "", "");
			} catch (WeiboException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			
			super.onPostExecute(result);
		}
    	
    }
    class Update extends AsyncTask< String, String, String>{
		@Override
		protected void onPreExecute() {
        	showDialog("", "", null);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				update(Weibo.getInstance(), WeiBoData.weibo_appkey, getShareContent(), "", "");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			
			super.onPostExecute(result);
		}
    	
    }
	@Override
	public void onStartRun() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
            }
        });
	}
    @Override
    public void onComplete(String response) {
    	 stopDialog();
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(ShareActivity.this, R.string.send_sucess, Toast.LENGTH_LONG).show();
            }
        });

        this.finish();
    }

    @Override
    public void onIOException(final IOException e) {
    	 stopDialog();
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(
                        ShareActivity.this,
                        "分享失败。", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onError(final WeiboException e) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(
                        ShareActivity.this,
                        String.format(getText(R.string.send_failed) + ":%s", e.getMessage()), Toast.LENGTH_LONG).show();
            }
        });
//        21327	：	token过期
//        21332	：	access_token 无效
//        21501	：	access_token 无效
//        20017	：	你刚刚已经发送过相似内容了哦，先休息一会吧
//        20019	：	不要太贪心哦，发一次就够啦
//        10006	：	缺少 source参数(appkey)
        stopDialog();
        LogWorker.i("msg code = " + e.getStatusCode());
        switch (e.getStatusCode()) {
		case 10006:
	        doAuthorize();
			break;
		case 21501:
	        doAuthorize();
			break;
		case 21327:
	        doAuthorize();
			break;
		case 21332:
			doAuthorize();
			break;
		default:
			break;
		}

    }
    public void connectToWeibo(){
     	
		WeiboEntity entity = new WeiboEntity(this,ShareActivity.class);
		int t = (Integer) mStateText.getTag();
		switch (t) {
		case 0:
			//需要绑定
			LogWorker.i("bangding...");
			entity.bangding(mApp.getUserInfo().getUserID());
			reflashData();
			break;
		case 1:
			//重新绑定
			LogWorker.i("unbangding...");
			okOrCancelDialog(getText(R.string.want_to_un_bangding_weibo).toString(),
					getText(R.string.sweat_tip).toString(),
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
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

			break;
		case 2:
			//需要登录
			LogWorker.i("weibo login...");

			entity.login();
			reflashData();
			break;
		case 3:
			//切换登录
			LogWorker.i("weibo loginout...");
			okOrCancelDialog(getText(R.string.want_to_login_out_weibo).toString(),
					getText(R.string.sweat_tip).toString(),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mApp.setWeiboLogin(false);
							mApp.setLoginOut(true);
							reflashData();
							doAuthorize();
						}
					},
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							
						}
					});
			break;
		default:
			break;
		}
    }
    private void doAuthorize(){
        WeiboEntity entity = new WeiboEntity(this,ShareActivity.class);
        if (mApp.isLogin()){
			entity.bangding(mApp.getUserInfo().getUserID());
        } else {
			entity.login();
        }
    }



}
