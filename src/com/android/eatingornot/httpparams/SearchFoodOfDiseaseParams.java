package com.android.eatingornot.httpparams;

import com.android.eatingornot.activitys.EaterApplication;

import ccb.android.net.framkwork.BaseHttpParams;

public class SearchFoodOfDiseaseParams extends BaseHttpParams {

	public SearchFoodOfDiseaseParams(String diseaseName,String searchText) {
		super(EaterApplication.HOST_URL + "seachdfoodm");
		addParams("clientAgent", "Andoird");
		addParams("type", "searchFoodOfDisease");
		addParams("diseaseName", diseaseName);
		addParams("foodName", searchText);
	}

}
