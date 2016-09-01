package com.coolweather.app.util;

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
		
}
