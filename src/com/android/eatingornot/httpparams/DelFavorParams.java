package com.android.eatingornot.httpparams;

import com.android.eatingornot.activitys.EaterApplication;

import ccb.android.net.framkwork.BaseHttpParams;

public class DelFavorParams extends BaseHttpParams {

	public DelFavorParams(String favorId,String foodNames,String diseaseNames,String uid) {
		super(EaterApplication.HOST_URL + "delcollectm");
		addParams("clientAgent", "Andoird");
		addParams("type", "delFavor");
		addParams("dataId", favorId);
		addParams("foodNames", foodNames);
		addParams("diseaseNames", diseaseNames);
		addParams("uid", uid);
		}

}
