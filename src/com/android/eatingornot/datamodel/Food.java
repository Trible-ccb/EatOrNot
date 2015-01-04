package com.android.eatingornot.datamodel;

public class Food {

	String foodImgURL;
	String foodName;	
	int foodID;
	int canEatInt;
	
	String relatedDisease;
	boolean isSelected;

	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	public String getRelatedDisease() {
		return relatedDisease;
	}
	public void setRelatedDisease(String relatedDisease) {
		this.relatedDisease = relatedDisease;
	}
	public int getCanEatInt() {
		return canEatInt;
	}
	public void setCanEatInt(int canEatInt) {
		this.canEatInt = canEatInt;
	}
	public String getFoodName() {
		return foodName;
	}
	public void setFoodName(String foodName) {
		this.foodName = foodName;
	}

	public int getFoodImgResId() {
		return foodID;
	}
	public void setFoodImgResId(int foodImgResId) {
		this.foodID = foodImgResId;
	}
	public String getFoodImgURL() {
		return foodImgURL;
	}
	public void setFoodImgURL(String foodImgPath) {
		this.foodImgURL = foodImgPath;
	}
	
}
