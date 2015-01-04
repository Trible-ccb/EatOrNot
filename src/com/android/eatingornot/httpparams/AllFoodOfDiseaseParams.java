package com.android.eatingornot.httpparams;

import com.android.eatingornot.activitys.EaterApplication;

import ccb.android.net.framkwork.BaseHttpParams;

public class AllFoodOfDiseaseParams extends BaseHttpParams {

	public AllFoodOfDiseaseParams(String diseaseName) {
		super(EaterApplication.HOST_URL + "alldfoodm");
		addParams("clientAgent", "Andoird");
		addParams("type", "AllFoodOfDisease");
		addParams("diseaseName", diseaseName);
	}

}
