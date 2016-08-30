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
	//数据库名
	public static final String DB_NAME = "cool_weather";
	//数据库版本
	public static final int VERSION = 1;
	
	private static CoolWeatherDB coolWeatherDB;//该类的声明，用于私有化构造方法
	private SQLiteDatabase db;//数据库的声明，实例化后就可以通过这个来操作数据库了
	
	//私有化的构造方法，只有通过该类的静态方法才能调用该构造方法来创造该类的实例，又因为是静态的所以可以保证全局范围内只有一个该类的实例
	private CoolWeatherDB(Context context){
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context,DB_NAME,null,VERSION);//创建数据库助手实例
		db = dbHelper.getWritableDatabase();//通过数据库助手获取数据库，如果数据库不存在就创建数据库，这是会调用数据库助手类的onCreate()方法来建表
	}
	//通过这个静态方法来创建和获取该类的实例，当synchronized用来修饰一个方法或者一个代码块的时候，能够保证在同一时刻最多只有一个线程执行该段代码
	public synchronized static CoolWeatherDB getInstance(Context context){
		if(coolWeatherDB == null){
			coolWeatherDB = new CoolWeatherDB(context);//创建该类的实例
		}
		return coolWeatherDB;//返回该类的实例
	}
	
	//将Province实例存储到数据库
	public void saveProvince(Province province){
		if(province != null){
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}
	
	//从数据库读取全国所有省份信息
	public List<Province> loadProvince(){
		List<Province> list = new ArrayList<Province>();//用来存放从数据库读取到的省份信息
		Cursor cursor = db.query("Province", null, null, null, null, null, null);//查询数据库，之后通过cursor获取数据即可
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
	
	//将City实例存储到数据库
	public void saveCity(City city){
		if(city != null){
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
		}
	}
	
	//从数据库读取某省下所有城市的信息
	public List<City> loadCity(int provinceId){
		List<City> list = new ArrayList<City>();//用来存放从数据库读取到的省份信息
		Cursor cursor = db.query("City", null, "province_id = ?", new String[] {String.valueOf(provinceId)}, null, null, null);//查询数据库，之后通过cursor获取数据即可
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
	
	//将County实例存储到数据库
	public void saveCounty(County county){
		if(county != null){
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCityName());
			values.put("county_code", county.getCityCode());
			values.put("city_id", county.getProvinceId());
			db.insert("County", null, values);
		}
	}
	
	//从数据库读取某城市下所有省份的信息
	public List<County> loadCounty(int cityId){
		List<County> list = new ArrayList<County>();//用来存放从数据库读取到的省份信息
		Cursor cursor = db.query("County", null, "city_id = ?", new String[] {String.valueOf(cityId)}, null, null, null);//查询数据库，之后通过cursor获取数据即可
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
