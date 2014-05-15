package com.line.alermapp.activity;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.line.alermapp.R;
import com.line.alermapp.database.Mute;
import com.line.alermapp.util.FormatUtils;

public class MuteActivity extends Activity{
	
	private TextView startTime;
	
	private TextView endTime;
	
	private TextView repeatTip;
	
	private View saveBtn;
	
	private View cancerBtn;
	
	private ToggleButton toggleBtn;
	
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
		startTime = (TextView) findViewById(R.id.nowTime);
		endTime = (TextView) findViewById(R.id.defaultEndTime);
		repeatTip = (TextView) findViewById(R.id.repeatTip);
		saveBtn = findViewById(R.id.btnSave);
		cancerBtn = findViewById(R.id.btnCancer);
		toggleBtn = (ToggleButton)findViewById(R.id.toggle);
		calendar = Calendar.getInstance();
		
		final Mute updateObject = getIntent().getParcelableExtra("mute");
		
		// 如果是修改服务的话，先重置好各变量的值
		if(updateObject != null){
			startTime.setText(updateObject.getStartTime());
			endTime.setText(updateObject.getEndTime());
			repeatTip.setText(updateObject.getRepeatDaysDesc());
			startHour = updateObject.getStartHour();
			endHour = updateObject.getEndHour();
			startMinute = updateObject.getStartMinute();
			endMinute = updateObject.getEndMinute();
			daysChoice = updateObject.getRepeatDays();
			mute = updateObject.isMute();
			toggleBtn.setChecked(mute);
		}else{
			startHour = endHour = startMinute = endMinute = -1;
			mute = false;
			daysChoice = new boolean[]{false,false,false,false,false,false,false};
			String nowTime = FormatUtils.formatInt(calendar.get(Calendar.HOUR_OF_DAY), "00") + ":" 
								+ FormatUtils.formatInt(calendar.get(Calendar.MINUTE), "00");
			startTime.setText(nowTime);
			endTime.setText(nowTime);
			repeatTip.setText("一律不");
		}
		
		LinearLayout startTimeLayout = (LinearLayout) findViewById(R.id.startTime);
		startTimeLayout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view){
				new TimePickerDialog(MuteActivity.this,new TimePickerDialog.OnTimeSetListener(){
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						startHour = hourOfDay;
						startMinute = minute;
						startTime.setText(FormatUtils.formatInt(startHour,"00") + ":" + FormatUtils.formatInt(startMinute,"00"));
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
						endHour = hourOfDay;
						endMinute = minute;
						endTime.setText(FormatUtils.formatInt(endHour,"00") + ":" + FormatUtils.formatInt(endMinute,"00"));
					}
				},calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true)
				.show();
			}
		});
		
		LinearLayout repeatLayout = (LinearLayout) findViewById(R.id.repeat);
		repeatLayout.setOnClickListener(new OnClickListener(){
			
			private String[] days = Mute.getDays();
			
			@Override
			public void onClick(View view){
				new AlertDialog.Builder(MuteActivity.this)
				.setTitle("重复")
				.setMultiChoiceItems(days,daysChoice,new OnMultiChoiceClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
							daysChoice[which] = isChecked;
					}
				})
				.setNegativeButton("确认",new DialogInterface.OnClickListener() {
					
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String result = new String();
						int choiceNum = 0;
						for(int i = 0; i<daysChoice.length;i++){
							if(daysChoice[i]){
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
				.setPositiveButton("取消",null)
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
//				System.out.println("保存设置-------");
//				Intent intent = MuteActivity.this.getIntent();
//				intent.putExtra("startHour",startHour);
//				intent.putExtra("endHour",endHour);
//				intent.putExtra("startMinute",startMinute);
//				intent.putExtra("endMinute",endMinute);
//				intent.putExtra("repeatDays",repeatTip.getText().toString());
//				intent.putExtra("repeatDaysChoice",daysChoice);
//				intent.putExtra("mute",mute);
				if(startHour < endHour || (startHour == endHour && startMinute < endMinute)){
					if(updateObject == null){
						Mute obj = new Mute(startHour,startMinute,endHour,endMinute,
								daysChoice,mute);
						System.out.println("保存设置-------");
						Intent intent = MuteActivity.this.getIntent();
						intent.putExtra("mute",obj);
						setResult(MainActivity.OK_RESULT_CODE,intent);
						MuteActivity.this.finish();
					}else{
						System.out.println("->>>helo");
						updateObject.setStartHour(startHour);
						updateObject.setEndHour(endHour);
						updateObject.setEndMinute(endMinute);
						updateObject.setStartMinute(startMinute);
						updateObject.setRepeatDays(daysChoice);
						updateObject.setMute(mute);
						setResult(MainActivity.OK_RESULT_CODE,MuteActivity.this.getIntent());
						MuteActivity.this.finish();
					}
				}else{
					Toast.makeText(MuteActivity.this,"选择的时间段错误", 
							Toast.LENGTH_SHORT).show();
				}
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
    public void onBackPressed() {
		System.out.println("dsasd");
        setResult(MainActivity.FAIL_RESULT_CODE,getIntent());
        super.onBackPressed();
    }

}
