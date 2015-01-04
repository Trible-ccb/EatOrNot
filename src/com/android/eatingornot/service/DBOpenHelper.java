package com.android.eatingornot.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper{

    private static final String name = "food.db";
    private static final int version = 1;

    public DBOpenHelper(Context context) {
          super(context, name, null, version);
    }

    public void close() {
        if (this != null) {
            this.close();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }




}
