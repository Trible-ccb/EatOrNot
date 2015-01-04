package com.android.eatingornot.httpparams;

import com.android.eatingornot.activitys.EaterApplication;

import ccb.android.net.framkwork.BaseHttpParams;

public class CheckFavorParams extends BaseHttpParams {

	public CheckFavorParams(String favorId,String foodName,String diseaseName,String uid) {
		super(EaterApplication.HOST_URL + "checkcollectm");
		addParams("clientAgent", "Andoird");
		addParams("type", "checkFavor");
		addParams("dataId", favorId);
		addParams("uid", uid);
		addParams("foodName", foodName);
		addParams("diseaseName", diseaseName);
	}

}
