package com.line.alermapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper{
	
	private final String sql = "create table mute ("
			+ "_id varchar(32) primary key autoincrement,"
			+ "mute varchar(32),"
			+ "start_hour integer,"
			+ "start_minute integer,"
			+ "end_hour integer,"
			+ "end_minute integer,"
			+ "repeat_days varchar(255)"
			+ ");";
	
	public DatabaseOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	
	public DatabaseOpenHelper(Context context){
		this(context,"alerm.db3",null,1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
