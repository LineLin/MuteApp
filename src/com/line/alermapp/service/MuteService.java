package com.line.alermapp.service;

import java.util.Calendar;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.widget.Toast;

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
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		
		boolean mute = intent.getBooleanExtra("mute",false);
		boolean repeat = intent.getBooleanExtra("repeat",false);
		boolean[] repeatDaysChoice = intent.getBooleanArrayExtra("repeatDaysChoice");
		int nowDayIndex = ((calendar.get(Calendar.DAY_OF_WEEK)-1) + 7) % 7 ;
		
		if(!repeat || repeatDaysChoice[nowDayIndex]){
			
			if(mute){
				System.out.println("调整音量为0");
				audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//				audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,20,
//						AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
			}else{
				System.out.println("调整音量为100");
				audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//				audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
//						audioManager.getStreamMaxVolume(AudioManager.STREAM_RING),
//						AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
}
