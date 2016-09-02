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
				//��ʱִ�е�����
				updateWeather();//���±��ص�������Ϣ�������Ļ��û��´δ�����ӱ��ض�ȡ����Ϣ�ͻ������µ�
			}
		}).start();
		//���ö�ʱ
		AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);//�ͻ�ȡnotificationʵ��һ��ֱ����getSystemService()��ȡ��AlarmManager��ʵ��,֮��Ҫͨ�����ʵ������ʱ����
		int anHour = 8*60*60*1000;
		long triggerAtTime = SystemClock.elapsedRealtime() + anHour;//��ǰʱ�䣨���������ڵ�ʱ��ms����+һ��Сʱ
		Intent i = new Intent(this,AutoUpdateReceiver.class);//���ö�ʱִ�е�intent
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);//��intent����ΪpendingIntent
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);//���ö�ʱ����
		return super.onStartCommand(intent, flags, startId);
	}

	//���±��ص�������Ϣ�������Ļ��û��´δ�����ӱ��ض�ȡ����Ϣ�ͻ������µ�
	protected void updateWeather() {
		//�ȴӱ��ػ�ȡ����������������
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String weatherCode = prefs.getString("weather_code", "");
		//��װ��ַ�����ʺ���������Ϣ
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		//��ȡ������Ϣ�����ڻص������е��ý����ͱ���ĺ���
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
