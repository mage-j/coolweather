package com.coolweather.app.receiver;

import com.coolweather.app.service.AutoUpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoUpdateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context,AutoUpdateService.class);//重新回到LongRunningService.class，这样就形成了一个循环，一个一直运行的后台服务就完成了
		context.startService(i);
	}

}
