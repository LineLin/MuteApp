package com.line.alermapp.service;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;

import com.line.alermapp.database.Mute;

public class MuteService extends Service{

	private AudioManager audioManager;
	
	private Calendar calendar;
	
	@Override
	public IBinder onBind(Intent intent){
		return null;
	}

	@Override
	public void onCreate(){
		super.onCreate();
		audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
		calendar = Calendar.getInstance();
	}
	
	@Override
	public int onStartCommand(Intent intent,int arg2,int flag) {
		
		
//		boolean mute = intent.getBooleanExtra("mute",false);
//		boolean repeat = intent.getBooleanExtra("repeat",false);
//		boolean[] repeatDaysChoice = intent.getBooleanArrayExtra("repeatDaysChoice");
//		int nowDayIndex = ((calendar.get(Calendar.DAY_OF_WEEK)-1) + 7) % 7 ;
		
		Mute mute = intent.getParcelableExtra("mute");
		
		try {
			PrintWriter reader = new PrintWriter(openFileOutput("log.log",MODE_APPEND));
			reader.println(mute.getStartTime() + "-->" + mute.getEndTime());
			reader.println("重复" + mute.getRepeatDaysDesc());
			reader.println("now : " + new Date());
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if(!mute.isRepeat() || mute.isMuteDay(calendar.get(Calendar.DAY_OF_WEEK))){	
			if(mute.isMute()){
				System.out.println(mute.hashCode());
				System.out.println("调整音量为0");
				audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//				audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,20,
//						AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
			}else{
				System.out.println(mute.hashCode());
				System.out.println("调整音量为100");
				audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//				audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
//						audioManager.getStreamMaxVolume(AudioManager.STREAM_RING),
//						AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
			}
		}
//		if(!repeat || repeatDaysChoice[nowDayIndex]){
//		if( !mute.isRepeat() || mute.isMuteDay(calendar.get(Calendar.DAY_OF_WEEK))){	
//			if(mute.isMute()){
//				System.out.println(mute.hashCode());
//				System.out.println("调整音量为0");
//				audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
////				audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,20,
////						AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
//			}else{
//				System.out.println(mute.hashCode());
//				System.out.println("调整音量为100");
//				audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
////				audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
////						audioManager.getStreamMaxVolume(AudioManager.STREAM_RING),
////						AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
//			}
//		}
		//尽量完成这次的intent请求，即时被中断
		return Service.START_REDELIVER_INTENT;
	}

}
