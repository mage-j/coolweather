package com.coolweather.app.util;
//�����������ַ����ȡ�����ݺ�ͨ���ӿڰ����ݷ���
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
	public static void sendHttpRequest(final String address,final HttpCallbackListener listener){//�ڶ���������һ���ӿڣ������ص����񷵻صĽ��������������return����Ϊ�߳��в�����return�������ûص�����������
		new Thread(new Runnable(){

			@Override
			public void run() {
				HttpURLConnection connection = null;
				try{
					/*URL url = new URL(address);
					connection = (HttpURLConnection)url.openConnection();
					connection.setRequestMethod("GET");//��ʾ����һ����ȡ���ݵ�����
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
					HttpClient httpClient = new DefaultHttpClient();//��ȡHttpClient��ʵ��
					HttpGet httpGet = new HttpGet(address);//������Ϊ������վ��ȡ����,10.0.2.2��ģ������˵���ǵ��Ա�����ip��ַ
					HttpResponse httpResponse = httpClient.execute(httpGet);//ִ��httpGet��Ϊ������÷���ֵ
					if(httpResponse.getStatusLine().getStatusCode() == 200){//������������ص�״̬����200˵���������Ӧ���ɹ���
						HttpEntity entity = httpResponse.getEntity();//�ӷ��ص�httpResponse�л�ȡ��HttpEntity��entity��Ϊʵ��
						response = EntityUtils.toString(entity,"UTF-8");//����entity�����л�ȡ��String�����ý����ʽΪutg-8�������ı������
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
