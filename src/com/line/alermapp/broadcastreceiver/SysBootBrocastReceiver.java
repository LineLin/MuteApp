package com.line.alermapp.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SysBootBrocastReceiver extends BroadcastReceiver{
	
	@Override
	public void onReceive(Context context,Intent arg){
		Intent intent = new Intent();
		intent.setAction("com.line.alermapp.RESUME_MUTE_MANAGER");
		context.startService(intent);
	}
}
