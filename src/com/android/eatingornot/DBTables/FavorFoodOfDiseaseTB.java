package com.android.eatingornot.DBTables;

public class FavorFoodOfDiseaseTB {

	public static final String TABEL_NAME = "favor_food_with_disease";
	public static final String KEY_ID = "id";
	public static final String KEY_DISEASE_NAME = "disease_name";
	public static final String KEY_FOOD_NAME = "food_name";
	public static final String KEY_CAN_EAT = "can_eat";
	public static final String KEY_CAN_EAT_INT = "can_eat_int";
	public static final String KEY_REASON = "reason";
	public static final String KEY_REC_EAT_AMOUNT = "recommend_eat_amount";
	public static final String KEY_REC_FOOD_MIX = "recommend_food_mix";
	public static final String KEY_REC_FOODS = "recommend_food";
	public static final String KEY_EAT_ACTION = "eat_action";
	public static final String KEY_EAT_SKILL = "eat_skill";
	public static final String KEY_INFO_SOURCE = "info_source";
	
    public static String createDiseaseWithFoodTableString() {
        return "CREATE TABLE IF NOT EXISTS " + TABEL_NAME + "("
                + KEY_ID + " integer primary key autoincrement,"
                + KEY_DISEASE_NAME +" text,"
                + KEY_FOOD_NAME+" text,"
                + KEY_CAN_EAT+" text,"
                + KEY_CAN_EAT_INT+" integer,"
                + KEY_REASON+" text,"
                + KEY_REC_EAT_AMOUNT+" text,"
                + KEY_REC_FOOD_MIX+" text,"
                + KEY_REC_FOODS+" text,"
                + KEY_EAT_SKILL+" text,"
                + KEY_EAT_ACTION+" text,"
                + KEY_INFO_SOURCE+" text)";
    }
}
