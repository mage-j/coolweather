package com.coolweather.app.util;
//传入服务器地址，获取到数据后通过接口把数据返回
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.Message;
import android.util.Log;

public class HttpUtil {
	public static void sendHttpRequest(final String address,final HttpCallbackListener listener){//第二个参数是一个接口，用来回调服务返回的结果，作用类似于return，因为线程中不能用return，所以用回调来返回数据
		new Thread(new Runnable(){

			@Override
			public void run() {
				HttpURLConnection connection = null;
				try{
					/*URL url = new URL(address);
					connection = (HttpURLConnection)url.openConnection();
					connection.setRequestMethod("GET");//表示这是一个获取数据的连接
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));//InputStream->InputStreamReader->BufferedReader
					StringBuilder response = new StringBuilder();
					String line;
					while((line = reader.readLine()) != null){
						response.append(line);
					}
					if(listener != null){
						listener.onFinish(response.toString());
					}*/
					String response = new String();
					HttpClient httpClient = new DefaultHttpClient();//获取HttpClient的实例
					HttpGet httpGet = new HttpGet(address);//设置行为，从网站获取数据,10.0.2.2对模拟器来说就是电脑本机的ip地址
					HttpResponse httpResponse = httpClient.execute(httpGet);//执行httpGet行为，并获得返回值
					if(httpResponse.getStatusLine().getStatusCode() == 200){//如果服务器返回的状态码是200说明请求和响应都成功了
						HttpEntity entity = httpResponse.getEntity();//从返回的httpResponse中获取到HttpEntity，entity意为实体
						response = EntityUtils.toString(entity,"UTF-8");//解析entity，从中获取到String，设置解码格式为utg-8放置中文变成乱码
					}
					if(listener != null){
						listener.onFinish(response);
					}
				}catch(Exception e){
					if(listener != null){
						listener.onError(e);
					}
				}finally{
					if(connection != null){
						connection.disconnect();
					}
				}
			}
			
		}).start();
	}
}
