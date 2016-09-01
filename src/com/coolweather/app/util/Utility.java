package com.coolweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

public class Utility {
	//�������ݿ������װ��CoolWeatherDB�ʹӷ������õ���ʡ�����ݣ��ú���������ݽ����ʹ洢����
	public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,String response){
		if(!TextUtils.isEmpty(response)){
			//�ӷ������ĵ���ʡ�����ݸ�ʽΪ������|���У�����|���С����������á������ָ�
			String[] allProvinces = response.split(",");
			if((allProvinces != null) && (allProvinces.length > 0)){
				for(String p : allProvinces){//�����á������ָ����������
					String[] array = p.split("\\|");//����"|"�ָ��Ϊ"|"��ת���ַ�������Ҫ��\\
					//���������������ݱ�����Province��
					Province province = new Province();
					province.setProvinceName(array[1]);
					province.setProvinceCode(array[0]);
					//�����ݱ�����Province����
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	//�����ʹ�����������ص��м�����
	public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId){
		if(!TextUtils.isEmpty(response)){
			//�ӷ������ĵ���ʡ�����ݸ�ʽΪ������|���У�����|���С����������á������ָ�
			String[] allCities = response.split(",");
			if((allCities != null) && (allCities.length > 0)){
				for(String c : allCities){//�����á������ָ����������
					String[] array = c.split("\\|");//����"|"�ָ��Ϊ"|"��ת���ַ�������Ҫ��\\
					//���������������ݱ�����City��
					City city = new City();
					city.setCityName(array[1]);
					city.setCityCode(array[0]);
					city.setProvinceId(provinceId);
					//�����ݱ�����City����
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	//�����ʹ�����������ص��м�����
	public synchronized static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			//�ӷ������ĵ���ʡ�����ݸ�ʽΪ������|���У�����|���С����������á������ָ�
			String[] allCounties = response.split(",");
			if(allCounties != null && allCounties.length > 0){
				for(String c : allCounties){//�����á������ָ����������
					String[] array = c.split("\\|");//����"|"�ָ��Ϊ"|"��ת���ַ�������Ҫ��\\
					//���������������ݱ�����County��
					County county = new County();
					county.setCountyName(array[1]);
					county.setCountyCode(array[0]);
					county.setCityId(cityId);
					//�����ݱ�����County����
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
		
	//�������������ص�JSON��ʽ���������ݣ����������������ݱ����ڱ���
	public static void handleWeatherResponse(Context context,String response){
		try{
			//��������
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			//��������
			saveWeatherInfo(context , cityName , weatherCode , temp1 , temp2 , weatherDesp , publishTime);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy��M��d��",Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);//�����ж��Ƿ񱣴��б�����Ϣ���еĻ���ChooseAreaAcitvity���ֱ��������Ϣ��ʾ���棨ֻ�е�һ�δ򿪸���������ֵΪfalse��
		editor.putString("weather_code", weatherCode);//����ˢ��������Ϣ��ťʱ�ӱ��ػ�ȡ�����ݣ��������������ţ���Ȼ���øô��Ż�ȡ����������Ϣ
		//����������Ϣ
		editor.putString("city_name", cityName);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));//��sdf.format(new Date())��ȡ����ǰʱ�䣬��ʽΪ��yyyy��M��d�ա�
		editor.putString("weather_desp", weatherDesp);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.commit();
	}
	
}









