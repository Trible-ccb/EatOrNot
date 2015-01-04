package com.android.eatingornot.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;

import com.android.eatingornot.datamodel.DiseaseWithFood;
import com.android.eatingornot.datamodel.Food;

public class DieseaseWithFoodService {
    private DBOpenHelper openHelper;
    SQLiteDatabase db;
    private static DieseaseWithFoodService mInstance;
    private DieseaseWithFoodService(Context context) {
        this.openHelper = new DBOpenHelper(context);
        if (openHelper.getWritableDatabase().isOpen()) {
            openHelper.getWritableDatabase().close();
        }
        db = openHelper.getWritableDatabase();
    }

    public static DieseaseWithFoodService getInstance(Context context) {
        if(null == mInstance) {
            mInstance = new DieseaseWithFoodService(context);
        }
        return mInstance;
    }
    public void closeDataBase() {
        if (null != openHelper) {
            openHelper.close();
        }
        if (null != db && db.isOpen()) {
            db.close();
        }
    }

    /**
     * 保存食物搭配信息
     *
     * @param adapter
     *            要保存的食物信息
     */
    public void save(DiseaseWithFood adapter) {
        String sql = "INSERT INTO disease_with_food (_id, "
                + "disease_name,food_name,can_eat,reason,recommend_food_mix,recommend_food) "
                + "VALUES (?,?,?,?,?,?,?)";
        Object[] bindArgs = { null, adapter.getDisease_name(),
                adapter.getFood_name(), adapter.getCan_eat(),
                adapter.getReason(), adapter.getRecommend_food_mix(),
                adapter.getRecommend_food() };
        db.execSQL(sql, bindArgs);
    }

    /**
     * 根据id查找食物搭配信息
     *
     * @param id  查找的id
     * @return    食物搭配信息
     */
    public DiseaseWithFood find(Integer id) {
        String sql = "SELECT * " + "FROM disease_with_food WHERE _id=?";
        String[] selectionArgs = { id + "" };
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        if (cursor.moveToFirst())
            return new DiseaseWithFood(cursor.getInt(0), cursor.getString(1),
                    cursor.getString(2), cursor.getString(3),
                    cursor.getInt(4), cursor.getString(5),
                    cursor.getString(6), cursor.getString(7),cursor.getString(8));
        return null;
    }

    /**
     * 更新食物搭配信息
     *
     * @param adapter  食物搭配信息
     */
    public void update(DiseaseWithFood adapter) {
        String sql = "UPDATE disease_with_food "
                + "SET disease_name =?, food_name=?,"
                + "can_eat=?,can_eat_int=?,reason=? recommend_food_mix=?,recommend_food=? WHERE _id=?";
        Object[] bindArgs = { adapter.getDisease_name(),
                adapter.getFood_name(), adapter.getCan_eat(),adapter.getCan_eat_int(),
                adapter.getReason(), adapter.getRecommend_food_mix(),
                adapter.getRecommend_food() };
        db.execSQL(sql, bindArgs);
    }

    /**
     * 删除一组食物搭配信息
     *
     * @param id  食物搭配信息的id
     */
    public void delete(Integer id) {
        String sql = "DELETE FROM disease_with_food WHERE _id=?";
        Object[] bindArgs = { id };
        db.execSQL(sql, bindArgs);
    }

    /**
     * 获取食物搭配信息的数目
     *
     * @return
     */
    public long getCount() {
        String sql = "SELECT count(*) FROM disease_with_food";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        return cursor.getLong(0);
    }

    /**
     * 根据病名和食物名查找食物搭配信息
     *
     * @param disease_name
     *            病名
     * @param food_name
     *            食物名
     * @return 食物搭配信息
     */
    public DiseaseWithFood getScrollDataByname(String disease_name,
            String food_name) {
        String sql = "SELECT * "
                + "FROM disease_with_food WHERE disease_name = ? and food_name = ?";
        String[] selectionArgs = { disease_name + "", food_name + "" };
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        List<DiseaseWithFood> contacts = new ArrayList<DiseaseWithFood>();
        DiseaseWithFood contact = null;
        while (cursor.moveToNext()) {
            contact = new DiseaseWithFood(cursor.getInt(0),
                    cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getInt(4),
                    cursor.getString(5), cursor.getString(6),
                    cursor.getString(7), cursor.getString(8));
            contacts.add(contact);
        }
        return contact;
    }

    //根据病名查找相关热门食物
    public ArrayList<Food> getHotFoodsBySickName(String disease_name,Context context) {
        String sql = "SELECT * "
                + "FROM disease_with_food inner join food on disease_with_food.food_name=food.name " +
                		"WHERE disease_name = ? ORDER BY food.search_time DESC";
        String[] selectionArgs = { disease_name + ""};
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        ArrayList<Food> contacts = new ArrayList<Food>();
        while (cursor.moveToNext()) {
            String food_name = cursor.getString(cursor.getColumnIndex("food_name"));
            String image_pinyin = cursor.getString(cursor.getColumnIndex("image_pinyin"));
            Resources resources = context.getResources();
            int resid = resources.getIdentifier(context.getPackageName()
                    + ":drawable/" + image_pinyin, "drawable", null);
            if (resid <= 0) {
                resid = resources.getIdentifier(context.getPackageName()
                        + ":drawable/" + "not_exist_image_symbol", "drawable",
                        null);
            }
            Drawable d = context.getResources().getDrawable(resid);
            Food f = new Food();
            f.setFoodImgResId(resid);
//            f.setFoodImg(d);
            f.setFoodName(food_name);
            contacts.add(f);
        }
        return contacts;
    }
    
    //根据病名,食物模糊名查找相关食物
    public ArrayList<Food> searchFoodsByNameRelativeSickName(String foodname,String disease_name,Context context) {
        String sql = "SELECT food_name,image_pinyin "
                + "FROM disease_with_food inner join food on disease_with_food.food_name=food.name " +
                		"WHERE disease_name = ? and food.name like '%" + foodname + "%'";
        String[] selectionArgs = { disease_name + ""};
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        ArrayList<Food> contacts = new ArrayList<Food>();
        while (cursor.moveToNext()) {
            String food_name = cursor.getString(cursor.getColumnIndex("food_name"));
            String image_pinyin = cursor.getString(cursor.getColumnIndex("image_pinyin"));
            Resources resources = context.getResources();
            int resid = resources.getIdentifier(context.getPackageName()
                    + ":drawable/" + image_pinyin, "drawable", null);
            if (resid <= 0) {
                resid = resources.getIdentifier(context.getPackageName()
                        + ":drawable/" + "not_exist_image_symbol", "drawable",
                        null);
            }
            Drawable d = context.getResources().getDrawable(resid);
            Food f = new Food();
            f.setFoodImgResId(resid);
//            f.setFoodImg(d);
            f.setFoodName(food_name);
            contacts.add(f);
        }
        return contacts;
    }
    /**
     * 根据病名和食物名查找宜忌信息
     *
     * @param disease_name
     *            病名
     * @param food_name
     *            食物名
     * @return 食物宜忌信息
     */
    public int getCanEat(String disease_name,
            String food_name) {
        String sql = "SELECT can_eat_int "
                + "FROM disease_with_food WHERE disease_name = ? and food_name = ?";
        String[] selectionArgs = { disease_name + "", food_name + "" };
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            return cursor.getInt(0);
        }
        return 2;
    }
}
