package com.android.eatingornot.datamodel;

public class Disease {
    public Disease(Integer _id, String name) {
        super();
        this._id = _id;
        this.name = name;
    }
    Integer _id;
    String name;
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
}
