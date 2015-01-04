package com.android.eatingornot.service;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;

import com.android.eatingornot.datamodel.Food;
import com.android.eatingornot.datamodel.FoodDBEntity;

public class FoodService {
    private DBOpenHelper openHelper;
    SQLiteDatabase db;
    private static FoodService mInstance;

    private FoodService(Context context) {
        this.openHelper = new DBOpenHelper(context);
        if (openHelper.getWritableDatabase().isOpen()) {
            openHelper.getWritableDatabase().close();
        }
        db = openHelper.getWritableDatabase();
    }

    public static FoodService getInstance(Context context) {
        if (null == mInstance) {
            return mInstance = new FoodService(context);
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
     * 按照食物名查找食物
     *
     * @param food_name
     *            要查找的食物名
     * @return
     */
    public FoodDBEntity find(String food_name) {
        String sql = "SELECT * " + "FROM food WHERE name=?";
        String[] selectionArgs = { food_name };
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        if (cursor.moveToFirst())
            return new FoodDBEntity(cursor.getInt(0), cursor.getString(1),
                    cursor.getInt(2), cursor.getString(3));
        return null;
    }

    /**
     * 返回食物总数
     *
     * @return
     */
    public long getCount() {
        String sql = "SELECT count(*) FROM food";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        return cursor.getLong(0);
    }

    /**
     * 根据提供的食物名进行模糊搜索
     *
     * @param name
     *            要搜索的食物名
     * @return 符合要求食物的列表
     */
    public ArrayList<Food> getScrollDataByname(String name, Context context) {
        String sql = "SELECT * " + "FROM food WHERE name like '%" + name + "%'";
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<Food> contacts = new ArrayList<Food>();
        while (cursor.moveToNext()) {
            String food_name = cursor.getString(1);
            String image_pinyin = cursor.getString(3);
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
     * 根据提供的食物名进行精确搜索
     *
     * @param name
     *            要搜索的食物名
     * @return 符合要求食物的列表
     */
    public Food getFoodByName(String name, Context context) {
        String sql = "SELECT * " + "FROM food WHERE name = '" + name + "'";
        Cursor cursor = db.rawQuery(sql, null);
        Resources resources = context.getResources();
        Food f = new Food();
        int resid = -1;
        if (cursor.moveToNext()) {
            String food_name = cursor.getString(1);
            String image_pinyin = cursor.getString(3);
            resid = resources.getIdentifier(context.getPackageName()
                    + ":drawable/" + image_pinyin, "drawable", null);
            if (resid <= 0) {
                resid = resources.getIdentifier(context.getPackageName()
                        + ":drawable/" + "not_exist_image_symbol", "drawable",
                        null);
            }
        } else {
            resid = resources
                    .getIdentifier(context.getPackageName() + ":drawable/"
                            + "not_exist_image_symbol", "drawable", null);
        }
        Drawable d = context.getResources().getDrawable(resid);
        f.setFoodImgResId(resid);
//        f.setFoodImg(d);
        f.setFoodName(name);
        return f;
    }

    /**
     * 根据提供的食物名进行模糊搜索，返回食物名数组
     *
     * @param name
     *            要搜索的食物名
     * @return 符合要求食物名的列表
     */
    public ArrayList<String> getFoodArrayByName(String name) {
        String sql = "SELECT * " + "FROM food WHERE name like '%" + name + "%'";
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<String> foodArray = new ArrayList<String>();
        if (cursor.moveToNext()) {
            String food_name = cursor.getString(1);
            foodArray.add(food_name);
        }
        return foodArray;
    }
    //返回所有食物名字列表
    public ArrayList<String> getAllFoodArray() {
        String sql = "SELECT * " + "FROM food";
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<String> foodArray = new ArrayList<String>();
        while (cursor.moveToNext()) {
            String food_name = cursor.getString(1);
            foodArray.add(food_name);
        }
        return foodArray;
    }
    /**
     * 添加搜索的热门食物,搜索次数加1
     *
     * @param name
     *            要搜索的食物名
     * @return 符合要求食物的列表
     */
    public boolean upDateFoodSearchTime(String name) {
        FoodDBEntity foodDBEntity = find(name);
        if (null == foodDBEntity) {
            return false;
        }
        int search_time = foodDBEntity.getSearch_time();
        String sql = "UPDATE food " + "SET search_time = ? WHERE name = ?";
        Object[] bindArgs = { search_time + 1, name };
        db.execSQL(sql, bindArgs);
        return true;
    }

    /**
     * 列出热门食物
     *
     * @param returnCount
     *            要返回食物数量
     * @param context
     *            应用场景
     * @return 符合要求食物的列表
     */
    public ArrayList<Food> getScrollDataBySearchTime(int returnCount,
            Context context) {
        String sql = "SELECT * " + "FROM food ORDER BY search_time DESC limit " + returnCount;
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<Food> contacts = new ArrayList<Food>();
        int count = returnCount;
        while (cursor.moveToNext() && (count--) > 0) {
            String food_name = cursor.getString(1);
            String image_pinyin = cursor.getString(3);
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
}
