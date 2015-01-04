package com.android.eatingornot.datamodel;

public class DiseaseWithFood {

    public DiseaseWithFood(Integer _id, String disease_name, String food_name,
            String can_eat, Integer can_eat_int, String reason,
            String recommend_eat_amount, String recommend_food_mix,
            String recommend_food) {
        super();
        this._id = _id;
        this.disease_name = disease_name;
        this.food_name = food_name;
        this.can_eat = can_eat;
        this.can_eat_int = can_eat_int;
        this.reason = reason;
        this.recommend_eat_amount = recommend_eat_amount;
        this.recommend_food_mix = recommend_food_mix;
        this.recommend_food = recommend_food;
    }
    Integer _id;
    String disease_name;
    String food_name;
    String can_eat;
    Integer can_eat_int;
    String reason;
    String recommend_eat_amount;
    String recommend_food_mix;
    String recommend_food;
    public Integer get_id() {
        return _id;
    }
    public void set_id(Integer _id) {
        this._id = _id;
    }
    public String getCan_eat() {
        return can_eat;
    }
    public void setCan_eat(String can_eat) {
        this.can_eat = can_eat;
    }
    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    public String getRecommend_food_mix() {
        return recommend_food_mix;
    }
    public void setRecommend_food_mix(String recommend_food_mix) {
        this.recommend_food_mix = recommend_food_mix;
    }
    public String getRecommend_food() {
        return recommend_food;
    }
    public void setRecommend_food(String recommend_food) {
        this.recommend_food = recommend_food;
    }
    public String getDisease_name() {
        return disease_name;
    }
    public void setDisease_name(String disease_name) {
        this.disease_name = disease_name;
    }
    public String getFood_name() {
        return food_name;
    }
    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }
    public Integer getCan_eat_int() {
        return can_eat_int;
    }
    public void setCan_eat_int(Integer can_eat_int) {
        this.can_eat_int = can_eat_int;
    }
    public String getRecommend_eat_amount() {
        return recommend_eat_amount;
    }
    public void setRecommend_eat_amount(String recommend_eat_amount) {
        this.recommend_eat_amount = recommend_eat_amount;
    }
}
