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
	//传入数据库操作封装类CoolWeatherDB和从服务器得到的省级数据，该函数会把数据解析和存储起来
	public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,String response){
		if(!TextUtils.isEmpty(response)){
			//从服务器的到的省级数据格式为“代号|城市，代号|城市”，所以先用“，”分割
			String[] allProvinces = response.split(",");
			if((allProvinces != null) && (allProvinces.length > 0)){
				for(String p : allProvinces){//遍历用“，”分割出来的数据
					String[] array = p.split("\\|");//再用"|"分割，因为"|"是转义字符，所以要加\\
					//将解析出来得数据保存在Province中
					Province province = new Province();
					province.setProvinceName(array[1]);
					province.setProvinceCode(array[0]);
					//把数据保存在Province表中
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	//解析和处理服务器返回的市级数据
	public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId){
		if(!TextUtils.isEmpty(response)){
			//从服务器的到的省级数据格式为“代号|城市，代号|城市”，所以先用“，”分割
			String[] allCities = response.split(",");
			if((allCities != null) && (allCities.length > 0)){
				for(String c : allCities){//遍历用“，”分割出来的数据
					String[] array = c.split("\\|");//再用"|"分割，因为"|"是转义字符，所以要加\\
					//将解析出来得数据保存在City中
					City city = new City();
					city.setCityName(array[1]);
					city.setCityCode(array[0]);
					city.setProvinceId(provinceId);
					//把数据保存在City表中
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	//解析和处理服务器返回的市级数据
	public synchronized static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			//从服务器的到的省级数据格式为“代号|城市，代号|城市”，所以先用“，”分割
			String[] allCounties = response.split(",");
			if(allCounties != null && allCounties.length > 0){
				for(String c : allCounties){//遍历用“，”分割出来的数据
					String[] array = c.split("\\|");//再用"|"分割，因为"|"是转义字符，所以要加\\
					//将解析出来得数据保存在County中
					County county = new County();
					county.setCountyName(array[1]);
					county.setCountyCode(array[0]);
					county.setCityId(cityId);
					//把数据保存在County表中
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
		
	//解析服务器返回的JSON格式的天气数据，并将解析出的数据保存在本地
	public static void handleWeatherResponse(Context context,String response){
		try{
			//解析数据
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			//保存数据
			saveWeatherInfo(context , cityName , weatherCode , temp1 , temp2 , weatherDesp , publishTime);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);//用于判断是否保存有本地信息，有的话打开ChooseAreaAcitvity活动会直接跳到信息显示界面（只有第一次打开该软件是这个值为false）
		editor.putString("weather_code", weatherCode);//按下刷新天气信息按钮时从本地获取该数据（地区的天气代号），然后用该代号获取更新天气信息
		//保存天气信息
		editor.putString("city_name", cityName);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));//用sdf.format(new Date())获取到当前时间，格式为“yyyy年M月d日”
		editor.putString("weather_desp", weatherDesp);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.commit();
	}
	
}









