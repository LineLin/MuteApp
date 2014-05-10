package com.line.alermapp.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.line.alermapp.R;
import com.line.alermapp.database.DatabaseOpenHelper;
import com.line.alermapp.database.MuteDao;

public class MainActivity extends Activity implements OnItemClickListener{
	
	private ListView listView;
	
	private List<Map<String,Object>> data;
	
	private SimpleAdapter adapter;
	
	private MuteDao muteDao;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button addService = (Button) findViewById(R.id.setting);
		muteDao = new MuteDao(new DatabaseOpenHelper(this).getWritableDatabase());
		
		//设置列表项的适配器和监听器
		listView = (ListView)findViewById(R.id.listView);
		data = new ArrayList<Map<String,Object>>();
		adapter = new SimpleAdapter(this,data,R.layout.array_item,
				new String[]{"desc","tip","state"},
				new int[]{R.id.desc,R.id.tip,R.id.state});
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		
		//注册监听器
		addService.setOnClickListener(new OnClickListener(){				
			@Override
			public void onClick(View source){
				Intent intent = new Intent(MainActivity.this,MuteActivity.class);
				intent.putExtras(new Bundle());
				startActivityForResult(intent,0);
			}
		});
		
	}
	
	@Override
	public void onActivityResult(int requestCode,int resultCode,Intent intent){
		if(requestCode == 0 && resultCode == 0){
			
			String repeatDay = intent.getStringExtra("repeatDays");
			int startHour = intent.getIntExtra("startHour",-1);
			int endHour = intent.getIntExtra("endHour",-1);
			int startMinute = intent.getIntExtra("startMinute",-1);
			int endMinute = intent.getIntExtra("endMinute",-1);
			boolean mute = intent.getBooleanExtra("mute",false);
			
			String startTime = startHour + ":" + startMinute;
			String endTime = endHour + ":" + endMinute;
			
			System.out.println("开始-------");
			if(startHour < endHour || (startHour == endHour && startMinute < endMinute)){
				changeDate(repeatDay,startTime,endTime,mute);
				
				Bundle bundle = intent.getExtras();
				bundle.remove("repeatDays");
				
				Intent muteServiceIntent = new Intent();
				muteServiceIntent.setAction("com.line.alermapp.MUTE_MANAGER");
				muteServiceIntent.putExtras(bundle);
				startService(muteServiceIntent);
			}
		}
	}
	
	private void changeDate(String repeatDay,String startTime,String endTime,boolean mute){
		
		if("一律不".equals(repeatDay)){
			repeatDay = "";
		}
		System.out.println("mute  " + mute);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("desc",startTime + "  --->  " + endTime);
		map.put("tip",repeatDay);
		map.put("state",mute);
		data.add(map);
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
	}
	
}
