package com.line.alermapp.database;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class Mute implements Parcelable,Cloneable{
	
	public static final int FLAG_CONVER = 1;
	
	public static final int FLAG_NORMAL = 0;
	
	private static final String[] DAYS = new String[]{"星期一","星期二","星期三",
		"星期四","星期五","星期六","星期日"};
	
	private String id;

	private int startHour;
	
	private int startMinute;

	private int endHour;
	
	private int endMinute;
	
	private boolean[] repeatDays;
	
	private boolean mute;
	
	public  static final Parcelable.Creator<Mute> CREATOR = new Parcelable.Creator<Mute>() {

		@Override
		public Mute createFromParcel(Parcel source) {
			return new Mute(source);
		}

		@Override
		public Mute[] newArray(int size) {
			return new Mute[size];
		}
	};  
	
	public Mute(){
		
	}
	
	public Mute(Parcel source){
		this.id = source.readString();
		this.startHour = source.readInt();
		this.startMinute = source.readInt();
		this.endHour = source.readInt();
		this.endMinute = source.readInt();
		this.repeatDays = new boolean[7];
		source.readBooleanArray(this.repeatDays);
		boolean[]  temp = new boolean[1];
//		source.readBooleanArray(new boolean[]{this.mute});
		source.readBooleanArray(temp);
		this.mute = temp[0];
		
	}
	
	private Mute(String id,int startHour,int startMinute,int endHour,int endMinute
			,boolean[] repeatDays,boolean mute){
		this(startHour,startMinute,endHour,endMinute,repeatDays,mute);
		this.id = id;
	}
	
	public Mute(int startHour,int startMinute,int endHour,int endMinute
			,boolean[] repeatDays,boolean mute){
		this.id = UUID.randomUUID().toString().replace("-","");
		this.startHour = startHour;
		this.startMinute = startMinute;
		this.endHour = endHour;
		this.endMinute = endMinute;
		this.repeatDays = new boolean[7];
		setRepeatDays(repeatDays);
		this.mute = mute;
//		System.out.println("mute : " + mute + "         ");
	}
	
	public static String[] getDays() {
		return DAYS;
	}
	
	/**
	 * 由SQLite的数据库查询结果返回一个Mute的List
	 * @param cursor 数据库查询结果
	 * @return
	 */
	public static List<Mute> getMuteList(Cursor cursor){
		List<Mute> mutes = new ArrayList<Mute>();
		
		while(cursor.moveToNext()){
			String id = cursor.getString(cursor.getColumnIndex("id"));
			int startHour = cursor.getInt(cursor.getColumnIndex("start_hour"));
			int startMinute = cursor.getInt(cursor.getColumnIndex("start_minute"));
			int endHour = cursor.getInt(cursor.getColumnIndex("end_hour"));
			int endMinute = cursor.getInt(cursor.getColumnIndex("end_minute"));
			boolean mute = Boolean.valueOf(cursor.getString(cursor.getColumnIndex("mute")));
			String repeatDays = cursor.getString(cursor.getColumnIndex("repeat_days"));
			Mute obj = new Mute(id,startHour,startMinute,endHour,endMinute
					,stringToBooleanArray(repeatDays),mute);
			mutes.add(obj);
		}
		cursor.close();
		return mutes;
	}
	
	/**
	 * 将string转换成布尔数组，string中的布尔值必须是以,分割
	 * @param source
	 * @return
	 */
	public static boolean[] stringToBooleanArray(String source){
		String[] boolArray = source.split(",");
		boolean[] result = new boolean[boolArray.length];
		
		for(int i = 0; i < boolArray.length ; i++){
			result[i] = Boolean.valueOf(boolArray[i]);
		}
		
		return result;
	}
	
	public String getId() {
		return id;
	}
	
	public int getStartHour() {
		return startHour;
	}

	public void setStartHour(int startHour) {
		this.startHour = startHour;
	}

	public int getStartMinute() {
		return startMinute;
	}

	public void setStartMinute(int startMinute) {
		this.startMinute = startMinute;
	}

	public int getEndHour() {
		return endHour;
	}

	public void setEndHour(int endHour) {
		this.endHour = endHour;
	}

	public int getEndMinute() {
		return endMinute;
	}

	public void setEndMinute(int endMinute) {
		this.endMinute = endMinute;
	}

	public boolean[] getRepeatDays() {
		return repeatDays;
	}
	
	public void setRepeatDays(boolean[] repeatDays) {
		this.repeatDays = repeatDays;
	}
	
	public boolean isRepeat() {
		
		for(boolean choice : repeatDays){
			if(choice){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isMute() {
//		System.out.println("ismute() : " + mute + "         ");
		return mute;
	}

	public void setMute(boolean mute) {
		this.mute = mute;
	}
	
	/**
	 * 将服务的日期的选择转换为一个String数组表示，以，分割。方便以后数据库的存储
	 * @return
	 */
	public String getRepeatDaysString(){
		
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < repeatDays.length; i++){
				sb.append(String.valueOf(repeatDays[i]));
				sb.append(",");
		}
		
		return sb.toString();
	}
	
	/**
	 * 得到重复日期在文字上的描述
	 * @return
	 */
	public String getRepeatDaysDesc(){
		
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0 ; i < repeatDays.length; i++){
			
			if(repeatDays[i]){
				sb.append(DAYS[i].replace("星期","周") + " ");
			}
		}
	
		return sb.toString();
	}
	
	public boolean checkTimeFormat(){
		if(startHour < endHour || (startHour == endHour && startMinute < endMinute)){
			return true;
		}
		return false;
	}
	
	
	public String getStartTime(){
		return startHour + ":" + startMinute;
	}
	
	public String getEndTime(){
		return endHour + ":" + endMinute;
	}
	
	public boolean isMuteDay(int day){
		System.out.println("today is :" + day);
		return repeatDays[((day-2)+7)%7];
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeInt(startHour);
		dest.writeInt(startMinute);
		dest.writeInt(endHour);
		dest.writeInt(endMinute);
		dest.writeBooleanArray(repeatDays);
		dest.writeBooleanArray(new boolean[]{mute});
	}
	
	@Override
	public Mute clone(){
		try {
			return (Mute) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
