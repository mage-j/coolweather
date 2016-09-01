package com.coolweather.app.activity;

import com.coolweather.app.R;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener {
	private Button switchCity;//返回选择城市按钮
	private Button refreshWeather;//刷新数据按钮
	private LinearLayout weatherInfoLayout;
	//用于显示城市名
	private TextView cityNameText;
	//用于显示发布时间，publish意为发布、出版
	private TextView publishText;
	//用于显示当前日期
	private TextView currentDateText;
	//用于显示天气描述信息
	private TextView weatherDespText;
	//用于显示气温1
	private TextView temp1Text;
	//用于显示气温2
	private TextView temp2Text;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		
		//初始化控件
		switchCity = (Button) findViewById(R.id.switch_city);
		switchCity.setOnClickListener(this);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		refreshWeather.setOnClickListener(this);
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		currentDateText = (TextView) findViewById(R.id.current_date);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		
		//从开启该活动的意图中获取县级代号
		String countyCode = getIntent().getStringExtra("county_code");
		if(!TextUtils.isEmpty(countyCode)){//如果县级代号有，就说明是点击了县的，要去查询天气
			publishText.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);//设置天气信息不可见
			cityNameText.setVisibility(View.INVISIBLE);//设置城市名不可见
			queryWeatherCode(countyCode);//通过县级代号获取天气代号
		}else{//如果没有县级代号说明不是第一次用了，本地是有天气信息的，所以可以直接获取
			showWeather();//从本地获取天气信息并显示出来
		}
	}

	//从SharedPreferences文件中获取本地的天气信息
	private void showWeather() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", ""));
		publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		temp1Text.setText(prefs.getString("temp1", ""));
		temp2Text.setText(prefs.getString("temp2", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);//设置天气信息可见
		cityNameText.setVisibility(View.VISIBLE);//设置城市名可见
	}

	//通过县级代号查询天气代号，然后通过天气代号查询天气信息
	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
		queryFromServer(address,"countyCode");
	}

	//通过天气代号查询天气信息
	protected void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		queryFromServer(address,"weatherCode");
	}
	
	//传入地址和类型向服务器查询天气代号或天气信息，查询到天气信息后会解析并保存，然后显示在UI上
	private void queryFromServer(final String address, final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener(){

			@Override
			public void onFinish(String response) {
				if("countyCode".equals(type)){//如果传来的是县级代号，说明服务器的返回值是天气代号
					if(!TextUtils.isEmpty(response)){//如果服务器有返回值,就开始解析数据
						String[] array = response.split("\\|");
						if((array != null) && (array.length == 2)){
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				}else if("weatherCode".equals(type)){//如果传来的是天气代号就说明服务器返回的数据是天气信息
					Utility.handleWeatherResponse(WeatherActivity.this, response);//解析数据，并把数据保存在本地
					runOnUiThread(new Runnable(){//回到主线程运行更改UI的程序

						@Override
						public void run() {
							showWeather();
						}
						
					});
				}
			}

			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable(){//回到主线程运行更改UI的程序

					@Override
					public void run() {
						publishText.setText("同步失败");
					}
					
				});
			}
			
		});
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.switch_city:
			Intent intent = new Intent(this,ChooseAreaActivity.class);//转跳到地区选择
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			break;
		default :
			break;
		}
	}
	
}