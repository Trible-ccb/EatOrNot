package com.android.eatingornot.httpparams;

import com.android.eatingornot.activitys.EaterApplication;
import com.android.eatingornot.datamodel.WeiBoUserData;

import ccb.android.net.framkwork.BaseHttpParams;

public class BangdinglParams extends BaseHttpParams {

	public BangdinglParams(String uid,WeiBoUserData wud) {
		super(EaterApplication.HOST_URL + "bindweibom");
		addParams("clientAgent", "Android");
		addParams("type", "bangding");
		
		addParams("weiboID", wud.getWeiboId());
		addParams("weiboName", wud.getName());
		addParams("weiboImgUrl", wud.getProfile_image_url());
		addParams("weiboToken", wud.getAccessToken());
		addParams("weiboExpiresIn", wud.getLifeTime());
		
		addParams("uid", uid);
	}
	

}
