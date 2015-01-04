package com.android.eatingornot.DBTables;

import com.android.eatingornot.datamodel.FoodOfDiseaseBean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FavorFoodOfDieaseService {

	   private DBHelper openHelper;
	    SQLiteDatabase db;
	    private static FavorFoodOfDieaseService mInstance;
	    private FavorFoodOfDieaseService(Context context) {
	        this.openHelper = new DBHelper(context,DBHelper.APP_DB_VERSION + 4);
	        if (openHelper.getWritableDatabase().isOpen()) {
	            openHelper.getWritableDatabase().close();
	        }
	        db = openHelper.getWritableDatabase();
	    }

	    public static FavorFoodOfDieaseService getInstance(Context context) {
	        if(null == mInstance) {
	            mInstance = new FavorFoodOfDieaseService(context);
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
	    public boolean addFavor(FoodOfDiseaseBean adapter) {
//	        String sql = "INSERT INTO " + FavorFoodOfDiseaseTB.TABEL_NAME + " (id, "
//	                + "disease_name,food_name,can_eat,can_eat_int,recommend_eat_amount," +
//	                		"reason,recommend_food_mix," +
//	                		"recommend_food,info_source) "
//	                + "VALUES (?,?,?,?,?,?,?,?,?,?)";
//	        Object[] bindArgs = { null, adapter.getDisease_name(),
//	                adapter.getFood_name(), adapter.getCan_eat(),
//	                adapter.getCan_eat_int(),adapter.getRecommend_eat_amount(),
//	                adapter.getReason(), adapter.getRecommend_food_mix(),
//	                adapter.getRecommend_food(),adapter.getInfo_source() };
//	        db.execSQL(sql, bindArgs);
	        ContentValues values = new ContentValues();
	        values.putNull(FavorFoodOfDiseaseTB.KEY_ID);
	        values.put(FavorFoodOfDiseaseTB.KEY_DISEASE_NAME, adapter.getDiseaseName());
	        values.put(FavorFoodOfDiseaseTB.KEY_FOOD_NAME, adapter.getFoodName());
	        values.put(FavorFoodOfDiseaseTB.KEY_CAN_EAT, adapter.getCanEat());
	        values.put(FavorFoodOfDiseaseTB.KEY_CAN_EAT_INT, adapter.getCanEatInt());
	        values.put(FavorFoodOfDiseaseTB.KEY_REC_EAT_AMOUNT, adapter.getRecommendEatAmount());
	        values.put(FavorFoodOfDiseaseTB.KEY_REASON, adapter.getReason());
	        values.put(FavorFoodOfDiseaseTB.KEY_REC_FOOD_MIX, adapter.getRecommend_food_mix());
	        values.put(FavorFoodOfDiseaseTB.KEY_REC_FOODS, adapter.getReplaceFood());
	        values.put(FavorFoodOfDiseaseTB.KEY_INFO_SOURCE, adapter.getInfoSource());
	        values.put(FavorFoodOfDiseaseTB.KEY_EAT_ACTION, adapter.getEatAction());
	        values.put(FavorFoodOfDiseaseTB.KEY_EAT_SKILL, adapter.getEatSkill());
	        long idx = db.insert(FavorFoodOfDiseaseTB.TABEL_NAME, null, values);
	        if (idx == -1){
	        	return false;
	        } else {
	        	return true;
	        }
	    }

	    public boolean delFavor(FoodOfDiseaseBean adapter){
	    	String where = FavorFoodOfDiseaseTB.KEY_FOOD_NAME + "=? and "
	        + FavorFoodOfDiseaseTB.KEY_DISEASE_NAME + "=?";
	        String sql = "DELETE FROM " + FavorFoodOfDiseaseTB.TABEL_NAME + " WHERE "
	        + where;
	        String[] bindArgs = { adapter.getFoodName(),adapter.getDiseaseName() };
	        int delNum = db.delete(FavorFoodOfDiseaseTB.TABEL_NAME, where,bindArgs);
	        if (delNum == 0){
	        	return false;
	        } else {
	        	return true;
	        }
	    }
	    public boolean checkIsFavor(String foodName,String diseaseName){
	    	String[] cols = {FavorFoodOfDiseaseTB.KEY_ID};
	    	String where = FavorFoodOfDiseaseTB.KEY_FOOD_NAME + "=? and "
	        + FavorFoodOfDiseaseTB.KEY_DISEASE_NAME + "=?";
	        String[] bindArgs = { foodName,diseaseName };
	    	Cursor cur = db.query(FavorFoodOfDiseaseTB.TABEL_NAME,
	    			null, where, bindArgs, null, null, null);
	    	if (cur.getCount() > 0){
	    		return true;
	    	}
	    	return false;
	    }
	    /**
	     * 根据id查找食物搭配信息
	     *
	     * @param id  查找的id
	     * @return    食物搭配信息
	     */
	    public FoodOfDiseaseBean find(String foodName,String diseaseName) {

	    	String where = FavorFoodOfDiseaseTB.KEY_FOOD_NAME + "=? and "
	        + FavorFoodOfDiseaseTB.KEY_DISEASE_NAME + "=?";
	        String[] bindArgs = { foodName,diseaseName };
	        String sql = "SELECT * " + "FROM " + 
	        FavorFoodOfDiseaseTB.TABEL_NAME + " WHERE " + where;
	        Cursor cursor = db.rawQuery(sql, bindArgs);
	        FoodOfDiseaseBean bean = null;
	        if (cursor.moveToFirst()){
	        	bean = new FoodOfDiseaseBean();
	        	bean.setCanEat(cursor.getString(cursor.getColumnIndex(FavorFoodOfDiseaseTB.KEY_CAN_EAT)));
	        	bean.setCanEatInt(cursor.getInt(cursor.getColumnIndex(FavorFoodOfDiseaseTB.KEY_CAN_EAT_INT)));
	        	bean.setDiseaseName(cursor.getString(cursor.getColumnIndex(FavorFoodOfDiseaseTB.KEY_DISEASE_NAME)));
	        	bean.setEatAction(cursor.getString(cursor.getColumnIndex(FavorFoodOfDiseaseTB.KEY_EAT_ACTION)));
	        	bean.setEatSkill(cursor.getString(cursor.getColumnIndex(FavorFoodOfDiseaseTB.KEY_EAT_SKILL)));
	        	bean.setFoodName(cursor.getString(cursor.getColumnIndex(FavorFoodOfDiseaseTB.KEY_FOOD_NAME)));
	        	bean.setId(cursor.getInt(cursor.getColumnIndex(FavorFoodOfDiseaseTB.KEY_ID)));
	        	bean.setInfoSource(cursor.getString(cursor.getColumnIndex(FavorFoodOfDiseaseTB.KEY_INFO_SOURCE)));
	        	bean.setReason(cursor.getString(cursor.getColumnIndex(FavorFoodOfDiseaseTB.KEY_REASON)));
	        	bean.setRecommend_food_mix(cursor.getString(cursor.getColumnIndex(FavorFoodOfDiseaseTB.KEY_REC_FOOD_MIX)));
	        	bean.setRecommendEatAmount(cursor.getString(cursor.getColumnIndex(FavorFoodOfDiseaseTB.KEY_REC_EAT_AMOUNT)));
	        	bean.setReplaceFood(cursor.getString(cursor.getColumnIndex(FavorFoodOfDiseaseTB.KEY_REC_FOODS)));
	        }
	        return bean;
	    }

	    /**
	     * 更新食物搭配信息
	     *
	     * @param adapter  食物搭配信息
	     */
	    public void update(FoodOfDiseaseBean adapter) {
	        String sql = "UPDATE " + FavorFoodOfDiseaseTB.TABEL_NAME
	                + "SET disease_name =?, food_name=?,"
	                + "can_eat=?,can_eat_int=?,reason=? recommend_food_mix=?," +
	                		"recommend_food=?,info_source=? WHERE id=?";

	        Object[] bindArgs = { null, adapter.getDiseaseName(),
	                adapter.getFoodName(), adapter.getCanEat(),
	                adapter.getReason(), adapter.getRecommend_food_mix(),
	                adapter.getReplaceFood(),adapter.getInfoSource() };
	        db.execSQL(sql, bindArgs);
	    }

	    /**
	     * 删除一组食物搭配信息
	     *
	     * @param id  食物搭配信息的id
	     */
	    public void delete(Integer id) {
	        String sql = "DELETE FROM " + FavorFoodOfDiseaseTB.TABEL_NAME + " WHERE "
	        + FavorFoodOfDiseaseTB.KEY_ID + "=?";
	        Object[] bindArgs = { id };
	        db.execSQL(sql, bindArgs);
	    }

}
