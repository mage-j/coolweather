package com.coolweather.app.util;
//�ӿڣ����ڻص����ؽ�����ýӿ����ڸ���HttpUtil��
public interface HttpCallbackListener {
	void onFinish(String response);
	void onError(Exception e);
}
