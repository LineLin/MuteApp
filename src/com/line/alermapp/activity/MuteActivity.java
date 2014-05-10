package com.line.alermapp.activity;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.line.alermapp.R;

public class MuteActivity extends Activity{
	
	private TextView startTime;
	
	private TextView endTime;
	
	private TextView repeatTip;
	
	private View saveBtn;
	
	private View cancerBtn;
	
	private View toggleBtn;
	
	private int startHour;
	
	private int endHour;
	
	private int startMinute;
	
	private int endMinute;
	
	private Calendar calendar;
	
	private boolean[] daysChoice;
	
	private boolean mute;
	
	@Override
	protected void onCreate(Bundle saveBtnState){
		super.onCreate(saveBtnState);
		setContentView(R.layout.activity_add);
		
		//设置默认值
		startHour = endHour = startMinute = endMinute = -1;
		
		mute = false;
		daysChoice = new boolean[]{false,false,false,false,false,false,false};
		startTime = (TextView) findViewById(R.id.nowTime);
		endTime = (TextView) findViewById(R.id.defaultEndTime);
		repeatTip = (TextView) findViewById(R.id.repeatTip);
		saveBtn = findViewById(R.id.btnSave);
		cancerBtn = findViewById(R.id.btnCancer);
		toggleBtn = findViewById(R.id.toggle);
		calendar = Calendar.getInstance();
		
		String nowTime = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
		startTime.setText(nowTime);
		endTime.setText(nowTime);
		repeatTip.setText("一律不");
		
		LinearLayout startTimeLayout = (LinearLayout) findViewById(R.id.startTime);
		startTimeLayout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view){
				new TimePickerDialog(MuteActivity.this,new TimePickerDialog.OnTimeSetListener(){
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						MuteActivity.this.startHour = hourOfDay;
						MuteActivity.this.startMinute = minute;
						startTime.setText(startHour + ":" + startMinute);
					}
				},calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true)
				.show();
			}
		});
		
		LinearLayout endTimeLayout = (LinearLayout) findViewById(R.id.endTime);
		endTimeLayout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view){
				new TimePickerDialog(MuteActivity.this,new TimePickerDialog.OnTimeSetListener(){
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						MuteActivity.this.endHour = hourOfDay;
						MuteActivity.this.endMinute = minute;
						endTime.setText(endHour + ":" + endMinute);
					}
				},calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true)
				.show();
			}
		});
		
		LinearLayout repeatLayout = (LinearLayout) findViewById(R.id.repeat);
		repeatLayout.setOnClickListener(new OnClickListener(){
			
			private String[] days = new String[]{"星期一","星期二","星期三","星期四","星期五","星期六","星期日"}; 
			private List<Integer> daysList = new ArrayList<Integer>();
			
			@Override
			public void onClick(View view){
				new AlertDialog.Builder(MuteActivity.this)
				.setTitle("重复")
				.setMultiChoiceItems(days, daysChoice,new OnMultiChoiceClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
							daysChoice[which] = isChecked;
					}
				})
				.setPositiveButton("确认",new DialogInterface.OnClickListener() {
					
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String result = new String();
						int choiceNum = 0;
						for(int i = 0; i<daysChoice.length;i++){
							if(daysChoice[i]){
								daysList.add((i+1)%7);
								choiceNum++;
								result += "周" + days[i].charAt(days[i].length()-1) + " ";
							}
						}
						if(choiceNum == daysChoice.length){
							result = "每天";
						}
						repeatTip.setText(result);
					}
				})
				.setNegativeButton("取消",null)
				.show();
			}
		});
		
		toggleBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view){
				System.out.println("静音 " + !mute);
				mute = !mute;
			}
		});
		
		saveBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view){
				System.out.println("保存设置-------");
				Intent intent = MuteActivity.this.getIntent();
				intent.putExtra("startHour",startHour);
				intent.putExtra("endHour",endHour);
				intent.putExtra("startMinute",startMinute);
				intent.putExtra("endMinute",endMinute);
				intent.putExtra("repeatDays",repeatTip.getText().toString());
				intent.putExtra("repeatDaysChoice",daysChoice);
				intent.putExtra("mute",mute);
				setResult(0,intent);
				MuteActivity.this.finish();
			}
		});
		
		cancerBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view){
				setResult(0,getIntent());
				MuteActivity.this.finish();
			}
		});

	}
	
	@Override
	public void finish(){
		setResult(0,getIntent());
		super.finish();
	}

}
