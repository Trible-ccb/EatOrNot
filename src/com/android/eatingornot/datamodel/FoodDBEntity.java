package com.android.eatingornot.datamodel;

public class FoodDBEntity {
    public FoodDBEntity(Integer _id, String name, Integer search_time,
            String image_url) {
        super();
        this._id = _id;
        this.name = name;
        this.search_time = search_time;
        this.image_url = image_url;
    }
    Integer _id;
    String name;
    Integer search_time;
    String image_url;
    public Integer get_id() {
        return _id;
    }
    public void set_id(Integer _id) {
        this._id = _id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getImage_url() {
        return image_url;
    }
    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
    public Integer getSearch_time() {
        return search_time;
    }
    public void setSearch_time(Integer search_time) {
        this.search_time = search_time;
    }
}
