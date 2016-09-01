package com.coolweather.app.util;
//接口，用于回调返回结果，该接口用于辅助HttpUtil类
public interface HttpCallbackListener {
	void onFinish(String response);
	void onError(Exception e);
}
