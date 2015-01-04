package com.android.eatingornot.httpparams;

import com.android.eatingornot.activitys.EaterApplication;

import ccb.android.net.framkwork.BaseHttpParams;

public class GetFavorsParams extends BaseHttpParams {

	public GetFavorsParams(String uId) {
		super(EaterApplication.HOST_URL + "listcollectm");
		addParams("clientAgent", "Andoird");
		addParams("type", "getFavors");
		addParams("uid", uId);
	}

}
