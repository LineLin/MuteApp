package com.line.alermapp.database;

import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MuteDao {
	
	private SQLiteDatabase db;
	
	private static final String QUERY_SQL = "select * from mute";
	
	private static final String INSERT_SQL = "insert into mute"
			+ "(id,start_hour,start_minute,end_hour,end_minute,repeat_days,mute)"
			+ "values"
			+ "(?,?,?,?,?,?,?)";
	
	private static final String UPDATE_SQL = "update mute "
			+ "set start_hour = ? , start_minute = ? , end_hour = ? , end_minute = ? "
			+ ", repeat_days = ? , mute = ? where id = ?";
	
	private static final String DELETE_SQL = "delete from mute where id = ?";
	
	public MuteDao(SQLiteDatabase db){
		this.db = db;
	}
	
	public List<Mute> getAllService(){
		Cursor cursor = db.rawQuery(QUERY_SQL,null);
		List<Mute> mutes = Mute.getMuteList(cursor);
//		db.close();
		return mutes;
	}
	
	public void insertMute(Mute mute){
		String[] parameter = new String[]{mute.getId(),String.valueOf(mute.getStartHour()),
				String.valueOf(mute.getStartMinute()),String.valueOf(mute.getEndHour()),
				String.valueOf(mute.getEndMinute()),mute.getRepeatDaysString(),
				String.valueOf(mute.isMute())};
		db.execSQL(INSERT_SQL,parameter);
	}
	
	public void deleteMute(String id){
		System.out.println(id);
		db.execSQL(DELETE_SQL,new String[]{id});
	}
	
	public void updateMute(Mute mute){
		String[] parameter = new String[]{String.valueOf(mute.getStartHour()),
				String.valueOf(mute.getStartMinute()),String.valueOf(mute.getEndHour()),
				String.valueOf(mute.getEndMinute()),mute.getRepeatDaysString(),
				String.valueOf(mute.isMute()),mute.getId()};
		db.execSQL(UPDATE_SQL,parameter);
	}
	
	public void closeDataBase(){
		db.close();
	}
	
}
