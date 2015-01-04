package com.android.eatingornot.data;

import java.util.ArrayList;
import android.content.Context;
import ccb.java.android.utils.LogWorker;

import com.android.eatingornot.datamodel.Food;

public class ParseRecommendFood {


    private ArrayList<Food> mReommendFood;

    public ParseRecommendFood(String recommendFood, Context context) {
        mReommendFood = new ArrayList<Food>();
        if (recommendFood == null)return;
        recommendFood = recommendFood.replaceAll(";", ",");
        recommendFood = recommendFood.replaceAll("£»", ",");
        recommendFood = recommendFood.replaceAll("£¬", ",");
        String[] sourceStrArray = recommendFood.split(",");
        if (sourceStrArray.equals("") || sourceStrArray.length == 1 && sourceStrArray[0].equalsIgnoreCase("ÎÞ")){
        	return;
        }
//        FoodService foodService = FoodService.getInstance(context);
        for (int i = 0; i < sourceStrArray.length; i++) {
            String foodName = sourceStrArray[i];
//            Food food = foodService.getFoodByName(foodName, context);
            Food food = new Food();
            food.setFoodName(foodName);
            LogWorker.i("rec food @=" + i + " is " + foodName);
            mReommendFood.add(food);
        }
    }

    public ArrayList<Food> getReommendFood() {
        return mReommendFood;
    }

}
