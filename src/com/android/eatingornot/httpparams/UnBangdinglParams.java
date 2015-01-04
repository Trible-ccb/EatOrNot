package com.android.eatingornot.httpparams;

import com.android.eatingornot.activitys.EaterApplication;

import ccb.android.net.framkwork.BaseHttpParams;

public class UnBangdinglParams extends BaseHttpParams {

	public UnBangdinglParams(String weiboID,String uid) {
		super(EaterApplication.HOST_URL + "debindweibo");
		addParams("clientAgent", "Android");
		addParams("type", "unbangding");
		addParams("weiboID", weiboID);
		addParams("uid", uid);
	}
	

}
