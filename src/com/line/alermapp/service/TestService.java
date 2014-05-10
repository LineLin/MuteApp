package com.line.alermapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TestService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent,int i,int j){
		System.out.println("(())()()()()()(");
		return j;
	}

}
