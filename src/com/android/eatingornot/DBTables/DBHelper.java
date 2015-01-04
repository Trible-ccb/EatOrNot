package com.android.eatingornot.DBTables;

import ccb.java.android.utils.LogWorker;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DBHelper extends SQLiteOpenHelper{
	public static final String APP_DB_NAME = "EattingOrNot.db";
	public static final Integer APP_DB_VERSION = 1;
		public DBHelper(Context c){
			this(c,APP_DB_VERSION);
		}
		public DBHelper(Context c , Integer version){
			this(c, APP_DB_NAME, null, version);
		}
		public DBHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
		}	
	
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			LogWorker.i("DBOpenHelper onCreate..");
			db.execSQL(FavorFoodOfDiseaseTB.createDiseaseWithFoodTableString());
		}
	
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			LogWorker.i("DBOpenHelper onUpgrade..");
			String delDB = "DROP TABLE IF EXISTS " + FavorFoodOfDiseaseTB.TABEL_NAME ;
			db.execSQL(delDB);
			onCreate(db);
		}
		
}
