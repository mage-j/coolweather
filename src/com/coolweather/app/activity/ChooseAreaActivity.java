package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
	
	private boolean isFromWeatherActivity;//用于判断该活动是不是因为点击了信息显示界面的选择地区按钮而打开的
	
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	private ProgressDialog progressDialog;//提示框
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;//创建字符串列表适配器
	private CoolWeatherDB coolWeatherDB;//声明该类，用于在数据库中添加和查询数据
	private List<String> dataList = new ArrayList<String>();
	
	private List<Province> provinceList;//用于存放读取到的Province数据
	private List<City> cityList;
	private List<County> countyList;
	
	private Province selectedProvince;//用于存放最近一次点击的省的数据
	private City selectedCity;
	
	private int currentLevel;//当前选中的级别
	
	@Override
	protected void onCreate(Bundle savedInstanceState){//在onCreate()中进行初始化操作
		super.onCreate(savedInstanceState);
		isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if(prefs.getBoolean("city_selected", false) && !isFromWeatherActivity){//已经选择过城市并且不是从WeatherActivity转跳过来的才会执行if，直接跳到WeatherActivity
			Intent intent = new Intent(this,WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);//关闭自带的title
		setContentView(R.layout.choose_area);//加载布局
		//实例化控件
		listView = (ListView)findViewById(R.id.list_view);
		titleText = (TextView)findViewById(R.id.title_text);
		//设置适配器和列表
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);//当前dataList是空的，之后有了之后调用刷新函数刷新列表即可
		listView.setAdapter(adapter);//把适配器给列表
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		//设置列表点击
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int index, long arg3) {
				if(currentLevel == LEVEL_PROVINCE){//如果当前等级是省级的话
					selectedProvince = provinceList.get(index);//获取点击的省
					queryCities();//查询并加载市级数据
				}else if(currentLevel == LEVEL_CITY){
					selectedCity = cityList.get(index);
					queryCounties();
				}else if(currentLevel == LEVEL_COUNTY){//如果点击的是县级的地名，就开启天气信息显示活动，并用intent传递点击的县的代号过去
					String countyCode = countyList.get(index).getCountyCode();//获取到县级代号
					Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
					intent.putExtra("county_code", countyCode);
					startActivity(intent);
					finish();
				}
			}
			
		});
		//查询加载省级数据
		queryProvinces();
	}

	//查询全国所有省，如果没有的话就从服务器获取省的信息到数据库
	private void queryProvinces() {
		provinceList = coolWeatherDB.loadProvinces();//获取省级数据
		if(provinceList.size() > 0){//如果有数据的话
			dataList.clear();//清楚String列表的数据
			for(Province province : provinceList){//遍历从数据库读取到的省级数据，把这些省的名字放在String列表中
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();//更新列表
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;//当前显示的是省的列表，所以等级为省级
		}else{//如果没有数据
			queryFromServer(null,"province");//向服务器请求省的数据
		}
	}

	//查询选中省的所有市，如果没有的话就从服务器获取该省内市的信息到数据库
	private void queryCities() {
		cityList = coolWeatherDB.loadCities(selectedProvince.getId());//获取市级数据,需要的id,因为City类中有一条属性provinceId
		if(cityList.size() > 0){//如果有数据的话
			dataList.clear();//清楚String列表的数据
			for(City city : cityList){//遍历从数据库读取到的市级数据，把这些市的名字放在String列表中
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();//更新列表
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		}else{//如果没有数据
			queryFromServer(selectedProvince.getProvinceCode(),"city");//向服务器获取市级数据，需要在url中添加省的编号，所以要第一个参数
		}
	}

	//查询选中市的所有县，如果没有的话就从服务器获取该市内县的信息到数据库
	private void queryCounties() {
		countyList = coolWeatherDB.loadCounties(selectedCity.getId());
		if(countyList.size() > 0){//如果有数据的话
			dataList.clear();//清楚String列表的数据
			for(County county : countyList){//遍历从数据库读取到的市级数据，把这些市的名字放在String列表中
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();//更新列表
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		}else{//如果没有数据
			queryFromServer(selectedCity.getCityCode(),"county");//向服务器获取县级数据，需要在url中添加市的编号，所以要第一个参数
		}
	}
	
	private void queryFromServer(final String code,final String type) {
		String address;
		if(!TextUtils.isEmpty(code)){//如果code是不为空的说明请求的不是省的数据，在URL中要加上编号code
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		}else{//如果code是空的说明请求的是省的数据
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();//调用函数，显示弹出框,提示用户正在加载
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener(){//调用HttpUtil类的sendHttpRequest方法获取请求的数据，返回值在回调函数中,但该返回值没有经过解析，不能直接保存在数据库中

			@Override
			public void onFinish(String response) {
				boolean result = false;
				if("province".equals(type)){//如果请求的是省级数据
					result = Utility.handleProvincesResponse(coolWeatherDB, response);//把数据解析出来并保存在数据库中
				}else if("city".equals(type)){
					result = Utility.handleCitiesResponse(coolWeatherDB, response,selectedProvince.getId());//把数据解析出来并保存在数据库中
				}else if("county".equals(type)){
					result = Utility.handleCountiesResponse(coolWeatherDB, response,selectedCity.getId());//把数据解析出来并保存在数据库中
				}
				if(result){//如果数据获取和保存成功就重新读取，这时因为数据库中有数据所以会从数据库中获取到数据
					//因为该线程中的操作会改变UI，所以要用runOnUiThread方法让程序回到主线程工作
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							closeProgressDialog();//数据获取和保存结束，关闭弹出的“正在加载...”提示框
							if("province".equals(type)){//如果请求的是省级数据
								queryProvinces();
							}else if("city".equals(type)){
								queryCities();
							}else if("county".equals(type)){
								queryCounties();
							}
						}
						
					});
				}
			}

			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败",Toast.LENGTH_SHORT).show();
					}
					
				});
			}
			
		});
	}

	private void showProgressDialog() {
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);//创建弹窗实例
			progressDialog.setMessage("正在加载...");//给弹窗添加文本内容
			progressDialog.setCanceledOnTouchOutside(false);//设置属性为不可取消
		}
		progressDialog.show();//显示弹窗
	}

	private void closeProgressDialog() {
		if(progressDialog != null){
			progressDialog.dismiss();//关闭弹窗
		}
	}
	
	//重写onBackPressed()方法，来覆盖Back键的行为
	@Override
	public void onBackPressed(){
		if(currentLevel == LEVEL_COUNTY){
			queryCities();
		}else if(currentLevel == LEVEL_CITY){
			queryProvinces();
		}else{
			if(isFromWeatherActivity){//如果是从WeatherActivity转跳过来的，点击back；应该回到WeatherActivity
				Intent intent = new Intent(this,WeatherActivity.class);
				startActivity(intent);
			}
			finish();
		}
	}
	
}