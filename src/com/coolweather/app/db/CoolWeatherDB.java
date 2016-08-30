package com.coolweather.app.db;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {
	//���ݿ���
	public static final String DB_NAME = "cool_weather";
	//���ݿ�汾
	public static final int VERSION = 1;
	
	private static CoolWeatherDB coolWeatherDB;//���������������˽�л����췽��
	private SQLiteDatabase db;//���ݿ��������ʵ������Ϳ���ͨ��������������ݿ���
	
	//˽�л��Ĺ��췽����ֻ��ͨ������ľ�̬�������ܵ��øù��췽������������ʵ��������Ϊ�Ǿ�̬�����Կ��Ա�֤ȫ�ַ�Χ��ֻ��һ�������ʵ��
	private CoolWeatherDB(Context context){
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context,DB_NAME,null,VERSION);//�������ݿ�����ʵ��
		db = dbHelper.getWritableDatabase();//ͨ�����ݿ����ֻ�ȡ���ݿ⣬������ݿⲻ���ھʹ������ݿ⣬���ǻ�������ݿ��������onCreate()����������
	}
	//ͨ�������̬�����������ͻ�ȡ�����ʵ������synchronized��������һ����������һ��������ʱ���ܹ���֤��ͬһʱ�����ֻ��һ���߳�ִ�иöδ���
	public synchronized static CoolWeatherDB getInstance(Context context){
		if(coolWeatherDB == null){
			coolWeatherDB = new CoolWeatherDB(context);//���������ʵ��
		}
		return coolWeatherDB;//���ظ����ʵ��
	}
	
	//��Provinceʵ���洢�����ݿ�
	public void saveProvince(Province province){
		if(province != null){
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}
	
	//�����ݿ��ȡȫ������ʡ����Ϣ
	public List<Province> loadProvince(){
		List<Province> list = new ArrayList<Province>();//������Ŵ����ݿ��ȡ����ʡ����Ϣ
		Cursor cursor = db.query("Province", null, null, null, null, null, null);//��ѯ���ݿ⣬֮��ͨ��cursor��ȡ���ݼ���
		if(cursor.moveToFirst()){
			do{
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			}while(cursor.moveToNext());
		}
		if(cursor != null){
			cursor.close();
		}
		return list;
	}
	
	//��Cityʵ���洢�����ݿ�
	public void saveCity(City city){
		if(city != null){
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
		}
	}
	
	//�����ݿ��ȡĳʡ�����г��е���Ϣ
	public List<City> loadCity(int provinceId){
		List<City> list = new ArrayList<City>();//������Ŵ����ݿ��ȡ����ʡ����Ϣ
		Cursor cursor = db.query("City", null, "province_id = ?", new String[] {String.valueOf(provinceId)}, null, null, null);//��ѯ���ݿ⣬֮��ͨ��cursor��ȡ���ݼ���
		if(cursor.moveToFirst()){
			do{
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				list.add(city);
			}while(cursor.moveToNext());
		}
		if(cursor != null){
			cursor.close();
		}
		return list;
	}
	
	//��Countyʵ���洢�����ݿ�
	public void saveCounty(County county){
		if(county != null){
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCityName());
			values.put("county_code", county.getCityCode());
			values.put("city_id", county.getProvinceId());
			db.insert("County", null, values);
		}
	}
	
	//�����ݿ��ȡĳ����������ʡ�ݵ���Ϣ
	public List<County> loadCounty(int cityId){
		List<County> list = new ArrayList<County>();//������Ŵ����ݿ��ȡ����ʡ����Ϣ
		Cursor cursor = db.query("County", null, "city_id = ?", new String[] {String.valueOf(cityId)}, null, null, null);//��ѯ���ݿ⣬֮��ͨ��cursor��ȡ���ݼ���
		if(cursor.moveToFirst()){
			do{
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCityName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCityCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setProvinceId(cityId);
				list.add(county);
			}while(cursor.moveToNext());
		}
		if(cursor != null){
			cursor.close();
		}
		return list;
	}
		
}
