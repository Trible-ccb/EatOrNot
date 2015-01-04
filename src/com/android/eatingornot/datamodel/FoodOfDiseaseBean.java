package com.android.eatingornot.datamodel;

import java.util.ArrayList;


public class FoodOfDiseaseBean {

	public FoodOfDiseaseBean(){
		
	}
	Integer id;//web 中食物+病的表示页面
    String diseaseName;
    String foodName;
    String canEat;
    Integer canEatInt;
    String reason;
    String recommendEatAmount;
    String recommend_food_mix;
    String replaceFood;
    String infoSource;
    String eatAction;
    String eatSkill;
    String recommend_food;
    ArrayList<Food> Recommend_foods; 
	public ArrayList<Food> getRecommend_foods() {
		return Recommend_foods;
	}
	public void setRecommend_foods(ArrayList<Food> recommend_foods) {
		Recommend_foods = recommend_foods;
	}
	public String getRecommend_food() {
		return recommend_food;
	}
	public void setRecommend_food(String recommend_food) {
		this.recommend_food = recommend_food;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getDiseaseName() {
		return diseaseName;
	}
	public void setDiseaseName(String diseaseName) {
		diseaseName = diseaseName;
	}
	public String getFoodName() {
		return foodName;
	}
	public void setFoodName(String foodName) {
		foodName = foodName;
	}
	public String getCanEat() {
		return canEat;
	}
	public void setCanEat(String canEat) {
		canEat = canEat;
	}
	public Integer getCanEatInt() {
		return canEatInt;
	}
	public void setCanEatInt(Integer canEatInt) {
		canEatInt = canEatInt;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		reason = reason;
	}
	public String getRecommendEatAmount() {
		return recommendEatAmount;
	}
	public void setRecommendEatAmount(String recommendEatAmount) {
		recommendEatAmount = recommendEatAmount;
	}
	public String getRecommend_food_mix() {
		return recommend_food_mix;
	}
	public void setRecommend_food_mix(String recommend_food_mix) {
		this.recommend_food_mix = recommend_food_mix;
	}
	public String getReplaceFood() {
		return replaceFood;
	}
	public void setReplaceFood(String replaceFood) {
		replaceFood = replaceFood;
	}
	public String getInfoSource() {
		return infoSource;
	}
	public void setInfoSource(String infoSource) {
		infoSource = infoSource;
	}
	public String getEatAction() {
		return eatAction;
	}
	public void setEatAction(String eatAction) {
		eatAction = eatAction;
	}
	public String getEatSkill() {
		return eatSkill;
	}
	public void setEatSkill(String eatSkill) {
		eatSkill = eatSkill;
	}
 
}
