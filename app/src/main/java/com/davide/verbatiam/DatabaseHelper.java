package com.davide.verbatiam;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME="storage.db";
    public static final String COL_1="ID";
    public static final String COL_2="RESULT";
    public static final String COL_3="SCORE";
    public static final String COL_4="GREEN";
    public static final String COL_5="RED";
    public static final String COL_6="ULTIMATE";
    /*public static final String COL_7="G1";
    public static final String COL_8="G2";
    public static final String COL_9="G3";
    public static final String COL_10="R1";
    public static final String COL_11="R2";
    public static final String COL_12="R3";
    public static final String COL_13="U2";*/

    public DatabaseHelper( Context context) {

        super(context, DATABASE_NAME, null, 1);
        //SQLiteDatabase db= this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE storage(ID INTEGER PRIMARY KEY, RESULT LONG, SCORE INT, GREEN INT DEFAULT 1, RED INT, ULTIMATE INT /*G1 INT DEFAULT 1, G2 INT, G3 INT, R1 INT DEFAULT 1, R2 INT, R3 INT , U2 INT*/)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS storage");
        onCreate(db);
    }

    public boolean insertData(int id2,long result2, int score2, int green, int red, int ultimate)
    {
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COL_1,id2);
        contentValues.put(COL_2,result2);
        contentValues.put(COL_3,score2);
        contentValues.put(COL_4,green);
        contentValues.put(COL_5,red);
        contentValues.put(COL_6,ultimate);
        /*contentValues.put(COL_7="G1";
        contentValues.put(COL_8="G2";
        contentValues.put(COL_9="G3";
        contentValues.put(COL_10="R1";
        contentValues.put(COL_11="R2";
        contentValues.put(COL_12="R3";
        contentValues.put(COL_13="U2";*/
        long results = db.insert("storage",null, contentValues);
        if(results == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public Cursor selectData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM storage",null);
        return res;
    }

    public void updateData(long result2) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,result2);
        db.update("storage", contentValues, null, null);
    }

    public void updateScore(int score2) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_3,score2);
        db.update("storage", contentValues, "SCORE<"+score2, null);
    }

    public void updateGreen(int green2) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_4,green2);
        db.update("storage", contentValues, null, null);
    }

    public void updateRed(int red2) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_5,red2);
        db.update("storage", contentValues, null, null);
    }

    public void updateUltimate(int ultimate2) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_6,ultimate2);
        db.update("storage", contentValues, null, null);
    }

    public void deleteAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("storage",null,null);
        db.execSQL("delete from "+ "storage");
    }
}