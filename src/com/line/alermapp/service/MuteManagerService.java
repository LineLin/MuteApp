package com.line.alermapp.service;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

public class MuteManagerService extends IntentService{
	
	private Calendar calendar;
	
	private Timer timer;
	
	private final long period = 1000 * 3600 * 24l;
	
	public MuteManagerService() {
		super("MuteService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		timer = new Timer();
		boolean[] repeatDaysChoice = intent.getBooleanArrayExtra("repeatDaysChoice");
		int startHour = intent.getIntExtra("startHour",-1);
		int endHour = intent.getIntExtra("endHour",-1);
		int startMinute = intent.getIntExtra("startMinute",-1);
		int endMinute = intent.getIntExtra("endMinute",-1);
		boolean mute = intent.getBooleanExtra("mute",false);
		boolean repeat = false;
		
		for(int i = 0; i < repeatDaysChoice.length ; i++){
			if(repeatDaysChoice[i]){
				repeat = true;
				return ;
			}
		}
		
		Intent muteService = new Intent(this,MuteService.class);
		muteService.putExtra("repeatDaysChoice",repeatDaysChoice);
		muteService.putExtra("mute",mute);
		muteService.putExtra("repeat",repeat);
		
		/*设置服务开始的时间*/
			
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY,startHour);
		calendar.set(Calendar.MINUTE,startMinute);
		calendar.set(Calendar.SECOND,0);
		
		
		//设置定时任务开启的延迟时间
		long delay = calendar.getTimeInMillis() - System.currentTimeMillis();
		delay = delay >= 0 ? delay : 0;
		
		if(repeat){
			timer.schedule(new MyTimerTask(muteService,this),delay,period);
		}else{
			timer.schedule(new MyTimerTask(muteService,this),delay);
		}
		
		
		//只有是静音服务的时候才需要在服务结束的时候开启声音
		if(mute){
			
			muteService = new Intent(this,MuteService.class);
			muteService.putExtra("repeatDaysChoice",repeatDaysChoice);
			muteService.putExtra("mute",!mute);
			muteService.putExtra("repeat",repeat);
			
			/*设置关闭服务的时间*/
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.set(Calendar.HOUR_OF_DAY,endHour);
			calendar.set(Calendar.MINUTE,endMinute);
			calendar.set(Calendar.SECOND,0);
			
			//设置定时任务关闭的延迟时间
			delay = calendar.getTimeInMillis() - System.currentTimeMillis();
			delay = delay >= 0 ? delay : 0;
			
			if(repeat){
				timer.schedule(new MyTimerTask(muteService,this),delay,period);
			}else{
				timer.schedule(new MyTimerTask(muteService,this),delay);
			}
		}
		
	}

	@Override
	public void onCreate(){
		super.onCreate();
		calendar = Calendar.getInstance();
	}
	
	class MyTimerTask extends TimerTask{
		
		private Intent intent;
		
		private Context context;
		
		public MyTimerTask(Intent intent,Context context){
			this.intent = intent;
			this.context = context;
		}
		
		@Override
		public void run(){
			System.out.println("开始广播-------");
			context.startService(intent);
		}
	}
}
