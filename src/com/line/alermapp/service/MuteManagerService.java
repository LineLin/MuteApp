package com.line.alermapp.service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.line.alermapp.database.DatabaseOpenHelper;
import com.line.alermapp.database.Mute;
import com.line.alermapp.database.MuteDao;

public class MuteManagerService extends IntentService{
	
	private Calendar calendar;
	
	public static Map<String,Timer> timers;
	
	private final long period = 1000 * 3600 * 24l; //重复周期
	
	private MuteDao muteDao;
	
	private DatabaseOpenHelper dbHelper;
	
	static{
		timers = new HashMap<String,Timer>();
	}
	
	public MuteManagerService() {
		super("MuteService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		System.out.println("test");
		
		if(intent.getAction().equals("com.line.alermapp.ADD_MUTE_MANAGER")){
			Mute mute = intent.getParcelableExtra("mute");
			setTimerTask(mute);
			return ;
		}
		System.out.println("test2");
		if(intent.getAction().equals("com.line.alermapp.RESUME_MUTE_MANAGER")){
			List<Mute> mutes = muteDao.getAllService();
			setTimerTask(mutes);
			return ;
		}
		
//		boolean[] repeatDaysChoice = intent.getBooleanArrayExtra("repeatDaysChoice");
//		int startHour = intent.getIntExtra("startHour",-1);
//		int endHour = intent.getIntExtra("endHour",-1);
//		int startMinute = intent.getIntExtra("startMinute",-1);
//		int endMinute = intent.getIntExtra("endMinute",-1);
//		boolean mute = intent.getBooleanExtra("mute",false);
//		boolean repeat = false;
//		
//		for(int i = 0; i < repeatDaysChoice.length ; i++){
//			if(repeatDaysChoice[i]){
//				repeat = true;
//				return ;
//			}
//		}
//		
		
		
	}
	
	private void setTimerTask(Mute mute){
		
		Timer timer = new Timer();
		timers.put(mute.getId(),timer);
		
		Intent muteService = new Intent(this,MuteService.class);
//		muteService.putExtra("repeatDaysChoice",repeatDaysChoice);
//		muteService.putExtra("mute",mute);
//		muteService.putExtra("repeat",repeat);
		
		muteService.putExtra("mute",mute);
		/*设置服务开始的时间*/
			
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY,mute.getStartHour());
		calendar.set(Calendar.MINUTE,mute.getStartMinute());
		calendar.set(Calendar.SECOND,0);
		
		
		//设置定时任务开启的延迟时间
		long delay = calendar.getTimeInMillis() - System.currentTimeMillis();
		delay = delay >= 0 ? delay : 0;
		
		if(mute.isRepeat()){
			timer.schedule(new MyTimerTask(muteService,this),delay,period);
		}else{
			timer.schedule(new MyTimerTask(muteService,this),delay);
		}
		
		
		//只有是静音服务的时候才需要在服务结束的时候开启声音
		if(mute.isMute()){
			
			muteService = new Intent(this,MuteService.class);
//			muteService.putExtra("repeatDaysChoice",repeatDaysChoice);
//			muteService.putExtra("mute",!mute);
//			muteService.putExtra("repeat",repeat);
			Mute obj = mute.clone();
			obj.setMute(false);
			muteService.putExtra("mute",obj);
			
			/*设置关闭服务的时间*/
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.set(Calendar.HOUR_OF_DAY,mute.getEndHour());
			calendar.set(Calendar.MINUTE,mute.getEndMinute());
			calendar.set(Calendar.SECOND,0);
			
			//设置定时任务关闭的延迟时间
			delay = calendar.getTimeInMillis() - System.currentTimeMillis();
			delay = delay >= 0 ? delay : 0;
			
			if(mute.isRepeat()){
				timer.schedule(new MyTimerTask(muteService,this),delay,period);
			}else{
				timer.schedule(new MyTimerTask(muteService,this),delay);
			}
		}
		
	}
	
	private void setTimerTask(List<Mute> mutes){
		
		for(Mute mute : mutes){
			setTimerTask(mute);
		}
		
	}

	@Override
	public void onCreate(){
		super.onCreate();
		calendar = Calendar.getInstance();
		dbHelper = new DatabaseOpenHelper(this,1);
		muteDao = new MuteDao(dbHelper.getWritableDatabase());
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		muteDao.closeDataBase();
		dbHelper.close();
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
			context.startService(intent);
		}
	}
}
