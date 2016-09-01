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
	private Button switchCity;//����ѡ����а�ť
	private Button refreshWeather;//ˢ�����ݰ�ť
	private LinearLayout weatherInfoLayout;
	//������ʾ������
	private TextView cityNameText;
	//������ʾ����ʱ�䣬publish��Ϊ����������
	private TextView publishText;
	//������ʾ��ǰ����
	private TextView currentDateText;
	//������ʾ����������Ϣ
	private TextView weatherDespText;
	//������ʾ����1
	private TextView temp1Text;
	//������ʾ����2
	private TextView temp2Text;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		
		//��ʼ���ؼ�
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
		
		//�ӿ����û����ͼ�л�ȡ�ؼ�����
		String countyCode = getIntent().getStringExtra("county_code");
		if(!TextUtils.isEmpty(countyCode)){//����ؼ������У���˵���ǵ�����صģ�Ҫȥ��ѯ����
			publishText.setText("ͬ����...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);//����������Ϣ���ɼ�
			cityNameText.setVisibility(View.INVISIBLE);//���ó��������ɼ�
			queryWeatherCode(countyCode);//ͨ���ؼ����Ż�ȡ��������
		}else{//���û���ؼ�����˵�����ǵ�һ�����ˣ���������������Ϣ�ģ����Կ���ֱ�ӻ�ȡ
			showWeather();//�ӱ��ػ�ȡ������Ϣ����ʾ����
		}
	}

	//��SharedPreferences�ļ��л�ȡ���ص�������Ϣ
	private void showWeather() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", ""));
		publishText.setText("����" + prefs.getString("publish_time", "") + "����");
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		temp1Text.setText(prefs.getString("temp1", ""));
		temp2Text.setText(prefs.getString("temp2", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);//����������Ϣ�ɼ�
		cityNameText.setVisibility(View.VISIBLE);//���ó������ɼ�
	}

	//ͨ���ؼ����Ų�ѯ�������ţ�Ȼ��ͨ���������Ų�ѯ������Ϣ
	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
		queryFromServer(address,"countyCode");
	}

	//ͨ���������Ų�ѯ������Ϣ
	protected void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		queryFromServer(address,"weatherCode");
	}
	
	//�����ַ���������������ѯ�������Ż�������Ϣ����ѯ��������Ϣ�����������棬Ȼ����ʾ��UI��
	private void queryFromServer(final String address, final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener(){

			@Override
			public void onFinish(String response) {
				if("countyCode".equals(type)){//������������ؼ����ţ�˵���������ķ���ֵ����������
					if(!TextUtils.isEmpty(response)){//����������з���ֵ,�Ϳ�ʼ��������
						String[] array = response.split("\\|");
						if((array != null) && (array.length == 2)){
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				}else if("weatherCode".equals(type)){//������������������ž�˵�����������ص�������������Ϣ
					Utility.handleWeatherResponse(WeatherActivity.this, response);//�������ݣ��������ݱ����ڱ���
					runOnUiThread(new Runnable(){//�ص����߳����и���UI�ĳ���

						@Override
						public void run() {
							showWeather();
						}
						
					});
				}
			}

			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable(){//�ص����߳����и���UI�ĳ���

					@Override
					public void run() {
						publishText.setText("ͬ��ʧ��");
					}
					
				});
			}
			
		});
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.switch_city:
			Intent intent = new Intent(this,ChooseAreaActivity.class);//ת��������ѡ��
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