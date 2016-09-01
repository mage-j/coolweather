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
	
	private boolean isFromWeatherActivity;//�����жϸû�ǲ�����Ϊ�������Ϣ��ʾ�����ѡ�������ť���򿪵�
	
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	private ProgressDialog progressDialog;//��ʾ��
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;//�����ַ����б�������
	private CoolWeatherDB coolWeatherDB;//�������࣬���������ݿ�����ӺͲ�ѯ����
	private List<String> dataList = new ArrayList<String>();
	
	private List<Province> provinceList;//���ڴ�Ŷ�ȡ����Province����
	private List<City> cityList;
	private List<County> countyList;
	
	private Province selectedProvince;//���ڴ�����һ�ε����ʡ������
	private City selectedCity;
	
	private int currentLevel;//��ǰѡ�еļ���
	
	@Override
	protected void onCreate(Bundle savedInstanceState){//��onCreate()�н��г�ʼ������
		super.onCreate(savedInstanceState);
		isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if(prefs.getBoolean("city_selected", false) && !isFromWeatherActivity){//�Ѿ�ѡ������в��Ҳ��Ǵ�WeatherActivityת�������ĲŻ�ִ��if��ֱ������WeatherActivity
			Intent intent = new Intent(this,WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);//�ر��Դ���title
		setContentView(R.layout.choose_area);//���ز���
		//ʵ�����ؼ�
		listView = (ListView)findViewById(R.id.list_view);
		titleText = (TextView)findViewById(R.id.title_text);
		//�������������б�
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);//��ǰdataList�ǿյģ�֮������֮�����ˢ�º���ˢ���б���
		listView.setAdapter(adapter);//�����������б�
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		//�����б���
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int index, long arg3) {
				if(currentLevel == LEVEL_PROVINCE){//�����ǰ�ȼ���ʡ���Ļ�
					selectedProvince = provinceList.get(index);//��ȡ�����ʡ
					queryCities();//��ѯ�������м�����
				}else if(currentLevel == LEVEL_CITY){
					selectedCity = cityList.get(index);
					queryCounties();
				}else if(currentLevel == LEVEL_COUNTY){//�����������ؼ��ĵ������Ϳ���������Ϣ��ʾ�������intent���ݵ�����صĴ��Ź�ȥ
					String countyCode = countyList.get(index).getCountyCode();//��ȡ���ؼ�����
					Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
					intent.putExtra("county_code", countyCode);
					startActivity(intent);
					finish();
				}
			}
			
		});
		//��ѯ����ʡ������
		queryProvinces();
	}

	//��ѯȫ������ʡ�����û�еĻ��ʹӷ�������ȡʡ����Ϣ�����ݿ�
	private void queryProvinces() {
		provinceList = coolWeatherDB.loadProvinces();//��ȡʡ������
		if(provinceList.size() > 0){//��������ݵĻ�
			dataList.clear();//���String�б������
			for(Province province : provinceList){//���������ݿ��ȡ����ʡ�����ݣ�����Щʡ�����ַ���String�б���
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();//�����б�
			listView.setSelection(0);
			titleText.setText("�й�");
			currentLevel = LEVEL_PROVINCE;//��ǰ��ʾ����ʡ���б����Եȼ�Ϊʡ��
		}else{//���û������
			queryFromServer(null,"province");//�����������ʡ������
		}
	}

	//��ѯѡ��ʡ�������У����û�еĻ��ʹӷ�������ȡ��ʡ���е���Ϣ�����ݿ�
	private void queryCities() {
		cityList = coolWeatherDB.loadCities(selectedProvince.getId());//��ȡ�м�����,��Ҫ��id,��ΪCity������һ������provinceId
		if(cityList.size() > 0){//��������ݵĻ�
			dataList.clear();//���String�б������
			for(City city : cityList){//���������ݿ��ȡ�����м����ݣ�����Щ�е����ַ���String�б���
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();//�����б�
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		}else{//���û������
			queryFromServer(selectedProvince.getProvinceCode(),"city");//���������ȡ�м����ݣ���Ҫ��url�����ʡ�ı�ţ�����Ҫ��һ������
		}
	}

	//��ѯѡ���е������أ����û�еĻ��ʹӷ�������ȡ�������ص���Ϣ�����ݿ�
	private void queryCounties() {
		countyList = coolWeatherDB.loadCounties(selectedCity.getId());
		if(countyList.size() > 0){//��������ݵĻ�
			dataList.clear();//���String�б������
			for(County county : countyList){//���������ݿ��ȡ�����м����ݣ�����Щ�е����ַ���String�б���
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();//�����б�
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		}else{//���û������
			queryFromServer(selectedCity.getCityCode(),"county");//���������ȡ�ؼ����ݣ���Ҫ��url������еı�ţ�����Ҫ��һ������
		}
	}
	
	private void queryFromServer(final String code,final String type) {
		String address;
		if(!TextUtils.isEmpty(code)){//���code�ǲ�Ϊ�յ�˵������Ĳ���ʡ�����ݣ���URL��Ҫ���ϱ��code
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		}else{//���code�ǿյ�˵���������ʡ������
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();//���ú�������ʾ������,��ʾ�û����ڼ���
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener(){//����HttpUtil���sendHttpRequest������ȡ��������ݣ�����ֵ�ڻص�������,���÷���ֵû�о�������������ֱ�ӱ��������ݿ���

			@Override
			public void onFinish(String response) {
				boolean result = false;
				if("province".equals(type)){//����������ʡ������
					result = Utility.handleProvincesResponse(coolWeatherDB, response);//�����ݽ������������������ݿ���
				}else if("city".equals(type)){
					result = Utility.handleCitiesResponse(coolWeatherDB, response,selectedProvince.getId());//�����ݽ������������������ݿ���
				}else if("county".equals(type)){
					result = Utility.handleCountiesResponse(coolWeatherDB, response,selectedCity.getId());//�����ݽ������������������ݿ���
				}
				if(result){//������ݻ�ȡ�ͱ���ɹ������¶�ȡ����ʱ��Ϊ���ݿ������������Ի�����ݿ��л�ȡ������
					//��Ϊ���߳��еĲ�����ı�UI������Ҫ��runOnUiThread�����ó���ص����̹߳���
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							closeProgressDialog();//���ݻ�ȡ�ͱ���������رյ����ġ����ڼ���...����ʾ��
							if("province".equals(type)){//����������ʡ������
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
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��",Toast.LENGTH_SHORT).show();
					}
					
				});
			}
			
		});
	}

	private void showProgressDialog() {
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);//��������ʵ��
			progressDialog.setMessage("���ڼ���...");//����������ı�����
			progressDialog.setCanceledOnTouchOutside(false);//��������Ϊ����ȡ��
		}
		progressDialog.show();//��ʾ����
	}

	private void closeProgressDialog() {
		if(progressDialog != null){
			progressDialog.dismiss();//�رյ���
		}
	}
	
	//��дonBackPressed()������������Back������Ϊ
	@Override
	public void onBackPressed(){
		if(currentLevel == LEVEL_COUNTY){
			queryCities();
		}else if(currentLevel == LEVEL_CITY){
			queryProvinces();
		}else{
			if(isFromWeatherActivity){//����Ǵ�WeatherActivityת�������ģ����back��Ӧ�ûص�WeatherActivity
				Intent intent = new Intent(this,WeatherActivity.class);
				startActivity(intent);
			}
			finish();
		}
	}
	
}