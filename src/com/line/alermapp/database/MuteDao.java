package com.line.alermapp.database;

import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MuteDao {
	
	private static SQLiteDatabase db;
	
	private static final String QUERY_SQL = "select * from mute";
	
	private static final String INSERT_SQL = "insert into mute"
			+ "(start_hour,start_minute,end_hour,end_minute,repeat,mute)"
			+ "values"
			+ "(?,?,?,?,?,?)";
	
	private static final String UPDATE_SQL = "update mute "
			+ "set start_hour = ? , start_minute = ? , end_hour = ? , end_minute = ? "
			+ ", repeat = ? where _id = ?";
	
	public MuteDao(SQLiteDatabase db){
		MuteDao.db = db;
	}
	
	public List<Mute> getAllService(){
		db.beginTransaction();
		Cursor cursor = db.rawQuery(QUERY_SQL,null);
		return Mute.getMuteList(cursor);
	}
	
	public void insertMute(Mute mute){
		
	}
	
	public void deleteMute(Mute mute){
		
	}
}
