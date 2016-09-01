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
	
	public void setCountyName(String cityName){
		this.countyName = cityName;
	}
	
	public String getCountyName(){
		return countyName;
	}
	
	public void setCountyCode(String cityCode){
		this.countyCode = cityCode;
	}
	
	public String getCountyCode(){
		return countyCode;
	}
	
	public void setCityId(int provinceId){
		this.cityId = provinceId;
	}
	
	public int getCityId(){
		return cityId;
	}
	
}
