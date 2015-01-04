package com.android.eatingornot.data;

import java.util.ArrayList;
import android.content.Context;
import com.android.eatingornot.datamodel.Food;
import com.android.eatingornot.service.FoodService;

public class ParseFoodMix {

    private String  mFirstFoodName;
    private String  mSecondFoodName;
    private int mCanMix;
    private String  mMixReason;
    private ArrayList<FoodMix> mFoodMixArray;

    public ParseFoodMix(String foodMix, Context context) {
        mFoodMixArray = new ArrayList<FoodMix>();
        if (foodMix == null)return;
        foodMix = foodMix.replaceAll("£¨", ",");
        String[] sourceStrArray = foodMix.split(",");
        FoodService foodService = FoodService.getInstance(context);
        mFirstFoodName = sourceStrArray[0];
        mSecondFoodName = sourceStrArray[1];
        mCanMix = getCanMixFromString(sourceStrArray[2]);
        mMixReason = sourceStrArray[3];
        Food firstFood = foodService
                .getFoodByName(mFirstFoodName, context);
        Food secondFood = foodService.getFoodByName(mSecondFoodName,
                context);
        FoodMix foodMix2 = new FoodMix(firstFood, secondFood);
        foodMix2.setmCanMix(mCanMix);
        foodMix2.setmMixReason(mMixReason);
        mFoodMixArray.add(foodMix2);
    }

    public ArrayList<FoodMix> getReommendFoodMix() {
        return mFoodMixArray;
    }

    private int getCanMixFromString(String str) {
        if(null == str || "".equals(str) || "Œﬁ".equals(str)) {
            return 2;
        } else if ("“À".equals(str)) {
            return 0;
        } else if ("º…".equals(str)) {
            return 1;
        } else {
            return 2;
        }
    }

    public int getmCanMix() {
        return mCanMix;
    }

    public String getmMixReason() {
        return mMixReason;
    }
}
