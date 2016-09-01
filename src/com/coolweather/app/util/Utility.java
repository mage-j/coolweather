package com.coolweather.app.util;

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
		
}
