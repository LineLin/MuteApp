package com.line.alermapp.service;

import java.util.Calendar;
import java.util.List;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;

import com.line.alermapp.database.Mute;

public class MuteService extends IntentService{

	private AudioManager audioManager;
	
	private Calendar calendar;
	
	public MuteService(){
		this("MuteService");
	}
	
	public MuteService(String name) {
		super(name);
	}
	
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
	protected void onHandleIntent(Intent intent) {
		
		
//		boolean mute = intent.getBooleanExtra("mute",false);
//		boolean repeat = intent.getBooleanExtra("repeat",false);
//		boolean[] repeatDaysChoice = intent.getBooleanArrayExtra("repeatDaysChoice");
//		int nowDayIndex = ((calendar.get(Calendar.DAY_OF_WEEK)-1) + 7) % 7 ;
		
		Mute mute = intent.getParcelableExtra("mute");
		setMuteTimerTask(mute);
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
	}
	
	public void setMuteTimerTask(Mute mute){
		System.out.println("被调度了");
		if( !mute.isRepeat() || mute.isMuteDay(calendar.get(Calendar.DAY_OF_WEEK))){	
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
	}
	
	public void setMuteTimerTask(List<Mute> mutes){
		for(Mute m : mutes){
			setMuteTimerTask(m);
		}
	}
}
