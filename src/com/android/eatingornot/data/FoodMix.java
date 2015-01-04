package com.android.eatingornot.data;

import com.android.eatingornot.datamodel.Food;

public class FoodMix {
    public int getmCanMix() {
		return mCanMix;
	}
	public void setmCanMix(int mCanMix) {
		this.mCanMix = mCanMix;
	}
	public String getmMixReason() {
		return mMixReason;
	}
	public void setmMixReason(String mMixReason) {
		this.mMixReason = mMixReason;
	}
	private int mCanMix;
    private String  mMixReason;
    public FoodMix(Food firstFood, Food secondFood) {
        super();
        this.firstFood = firstFood;
        this.secondFood = secondFood;
    }
    private Food firstFood;
    private Food secondFood;
    public Food getFirstFood() {
        return firstFood;
    }
    public void setFirstFood(Food firstFood) {
        this.firstFood = firstFood;
    }
    public Food getSecondFood() {
        return secondFood;
    }
    public void setSecondFood(Food secondFood) {
        this.secondFood = secondFood;
    }

}
