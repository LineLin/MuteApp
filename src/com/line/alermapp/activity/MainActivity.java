package com.line.alermapp.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.line.alermapp.database.DatabaseOpenHelper;
import com.line.alermapp.database.Mute;
import com.line.alermapp.database.MuteDao;
import com.line.alermapp.service.MuteManagerService;

public class MainActivity extends Activity implements OnItemLongClickListener,
		OnItemClickListener{
	
	private ListView listView;
	
	private List<Mute> mutes;
	
	private List<Map<String,Object>> data;
	
	private SimpleAdapter adapter;
	
	private MuteDao muteDao;
	
	private DatabaseOpenHelper databaseOpenHelp;
	
	public static final int ADD_REQUEST_CODE = 1;
	
	public static final int UPDATE_REQUEST_CODE = 2;
	
	public static final int OK_RESULT_CODE = 1;
	
	public static final int FAIL_RESULT_CODE = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button addService = (Button) findViewById(R.id.setting);
		databaseOpenHelp = new DatabaseOpenHelper(this,1);
		muteDao = new MuteDao(databaseOpenHelp.getWritableDatabase());
		
		
		//设置列表项的适配器和监听器
		initData();
		listView = (ListView)findViewById(R.id.listView);
		adapter = new SimpleAdapter(this,data,R.layout.array_item,
				new String[]{"desc","tip","mute"},
				new int[]{R.id.desc,R.id.tip,R.id.state});
		listView.setAdapter(adapter);
		listView.setOnItemLongClickListener(this);
		listView.setOnItemClickListener(this);
		
		//为listView注册上下文菜单
//		this.registerForContextMenu(listView);
		
		//注册监听器
		addService.setOnClickListener(new OnClickListener(){				
			@Override
			public void onClick(View source){
				Intent intent = new Intent(MainActivity.this,MuteActivity.class);
				startActivityForResult(intent,MainActivity.ADD_REQUEST_CODE);
			}
		});
		
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,final int position,
			long id) {

		new AlertDialog.Builder(MainActivity.this)
				.setItems(new String[]{"删除"},new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							String muteId = (String)data.get(position).get("id");
							muteDao.deleteMute(muteId);
							data.remove(position);
							adapter.notifyDataSetChanged();
							for(String key :MuteManagerService.timers.keySet()){
								System.out.println("key : " + key);
							}
							Timer timer = MuteManagerService.timers.get(muteId);
							if(timer != null){
								timer.cancel();
								MuteManagerService.timers.remove(muteId);
							}
						}
				})
				.setNegativeButton("取消",null)
				.show();
		
		return false;
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		Intent intent = new Intent(this,MuteActivity.class);
		intent.putExtra("mute",mutes.get(position));
		startActivityForResult(intent,MainActivity.UPDATE_REQUEST_CODE);
	}
	
	@Override
	public void onActivityResult(int requestCode,int resultCode,Intent intent){
		Mute mute = intent.getParcelableExtra("mute");
		
		switch(requestCode){
		case MainActivity.ADD_REQUEST_CODE:
//			String repeatDay = intent.getStringExtra("repeatDays");
//			int startHour = intent.getIntExtra("startHour",-1);
//			int endHour = intent.getIntExtra("endHour",-1);
//			int startMinute = intent.getIntExtra("startMinute",-1);
//			int endMinute = intent.getIntExtra("endMinute",-1);
//			boolean mute = intent.getBooleanExtra("mute",false);
			
//			int startHour = mute.getStartHour();
//			int endHour = mute.getEndHour();
//			int startMinute = mute.getStartMinute();
//			int endMinute = mute.getEndMinute();
//			String startTime = startHour + ":" + startMinute;
//			String endTime = endHour + ":" + endMinute;
			
			System.out.println("开始-------");
//			if(startHour < endHour || (startHour == endHour && startMinute < endMinute)){
//				changeDate(repeatDay,startTime,endTime,mute);
			if(resultCode ==  OK_RESULT_CODE && mute != null && mute.checkTimeFormat()){
				addDate(mute);
				//启动服务管理Service设置服务
				Bundle bundle = intent.getExtras();
				
				Intent muteServiceIntent = new Intent();
				muteServiceIntent.setAction("com.line.alermapp.ADD_MUTE_MANAGER");
				muteServiceIntent.putExtras(bundle);
				startService(muteServiceIntent);
			}
			break;
		case MainActivity.UPDATE_REQUEST_CODE:
			if(resultCode == OK_RESULT_CODE){
				Timer timer = MuteManagerService.timers.get(mute.getId());
				if(timer !=  null){
					timer.cancel();
					MuteManagerService.timers.remove(mute.getId());
				}
				updateData(mute);
				//启动服务管理Service设置服务
				Bundle bundle = intent.getExtras();
				
				Intent muteServiceIntent = new Intent();
				muteServiceIntent.setAction("com.line.alermapp.ADD_MUTE_MANAGER");
				muteServiceIntent.putExtras(bundle);
				startService(muteServiceIntent);
			}
			break;
		}
		
	}
	
	private void addDate(Mute mute){
		
//		if("一律不".equals(repeatDay)){
//			repeatDay = "";
//		}
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("id",mute.getId());
		map.put("desc",mute.getStartTime() + "  -----  " + mute.getEndTime());
		map.put("tip",mute.getRepeatDaysDesc());
		map.put("mute",mute.isMute());
		data.add(map);
		adapter.notifyDataSetChanged();
		muteDao.insertMute(mute);
	}
	
	private void updateData(Mute mute){
		muteDao.updateMute(mute);
		int position = -1;
		for(int i = 0; i < mutes.size(); i++){
			if(mutes.get(i).getId().equals(mute.getId())){
				position = i;
			}
		}
		
		if(position == -1){
			return ;
		}
		
		Map<String,Object> map = data.get(position);
		map.put("desc",mute.getStartTime() + "  -----  " + mute.getEndTime());
		map.put("tip",mute.getRepeatDaysDesc());
		map.put("mute",mute.isMute());
		adapter.notifyDataSetChanged();
	}
	
	public void initData(){
		mutes = muteDao.getAllService();
		data = coverTo(mutes);
	}
	
	public List<Map<String,Object>> coverTo(List<Mute> mutes){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for(Mute mute : mutes){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("id",mute.getId());
			map.put("desc",mute.getStartTime() + "  -----  " + mute.getEndTime());
			map.put("tip",mute.getRepeatDaysDesc());
			map.put("mute",mute.isMute());
			list.add(map);
		}
		return list;
	}																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																							
//	@Override
//	public void onCreateContextMenu(ContextMenu menu, View v,
//			ContextMenuInfo menuInfo) {
//		super.onCreateContextMenu(menu, v, menuInfo);
//		menu.add(Menu.NONE,1,0,"s删除");
//	}


	
	@Override
	public void onDestroy(){
		super.onDestroy();
		muteDao.closeDataBase();
		databaseOpenHelp.close();
		
	}

}
