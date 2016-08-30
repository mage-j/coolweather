package com.coolweather.app.model;

public class County {
	private int id;
	private String countyName;
	private String countyCode;
	private int cityId;
	
	public void setId(int id){
		this.id = id;
	}
	
	public int getId(){
		return id;
	}
	
	public void setCityName(String cityName){
		this.countyName = cityName;
	}
	
	public String getCityName(){
		return countyName;
	}
	
	public void setCityCode(String cityCode){
		this.countyCode = cityCode;
	}
	
	public String getCityCode(){
		return countyCode;
	}
	
	public void setProvinceId(int provinceId){
		this.cityId = provinceId;
	}
	
	public int getProvinceId(){
		return cityId;
	}
	
}
