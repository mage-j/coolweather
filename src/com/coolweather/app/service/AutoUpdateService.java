package com.coolweather.app.service;

import java.util.Date;

import com.coolweather.app.receiver.AutoUpdateReceiver;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public int onStartCommand(Intent intent,int flags,int startId){
		new Thread(new Runnable(){
			@Override
			public void run() {
				//定时执行的任务
				updateWeather();//更新本地的天气信息，这样的话用户下次打开软件从本地读取的信息就会是最新的
			}
		}).start();
		//设置定时
		AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);//和获取notification实例一样直接用getSystemService()获取到AlarmManager的实例,之后要通过这个实例来定时任务
		int anHour = 8*60*60*1000;
		long triggerAtTime = SystemClock.elapsedRealtime() + anHour;//当前时间（开机到现在的时间ms数）+一个小时
		Intent i = new Intent(this,AutoUpdateReceiver.class);//设置定时执行的intent
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);//把intent设置为pendingIntent
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);//设置定时任务
		return super.onStartCommand(intent, flags, startId);
	}

	//更新本地的天气信息，这样的话用户下次打开软件从本地读取的信息就会是最新的
	protected void updateWeather() {
		//先从本地获取到地区的天气代号
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String weatherCode = prefs.getString("weather_code", "");
		//组装地址，访问后获得天气信息
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		//获取天气信息，并在回调函数中调用解析和保存的函数
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener(){

			@Override
			public void onFinish(String response) {
				Utility.handleWeatherResponse(AutoUpdateService.this, response);
			}

			@Override
			public void onError(Exception e) {
				e.printStackTrace();
			}
			
		});
	}
	
}
