package com.android.eatingornot.httpparams;

import com.android.eatingornot.activitys.EaterApplication;

import ccb.android.net.framkwork.BaseHttpParams;

public class AddFavorParams extends BaseHttpParams {

	public AddFavorParams(String favorId,String foodName,String diseaseName,String uid) {
		super(EaterApplication.HOST_URL + "addcollectm");
		addParams("clientAgent", "Andoird");
		addParams("type", "addFavor");
		addParams("dataId", favorId);
		addParams("foodName", foodName);
		addParams("diseaseName", diseaseName);
		addParams("uid", uid);
	}

}
