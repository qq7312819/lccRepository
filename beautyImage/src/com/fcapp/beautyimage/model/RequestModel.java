package com.fcapp.beautyimage.model;

import java.util.HashMap;
import java.util.TreeMap;

import android.content.Context;

import com.fcapp.beautyimage.parser.BaseParser;

public class RequestModel {
	public int requestUrl;
	public Context context;
	public HashMap<String, String> requestDataMap;
	public BaseParser<?> jsonParser;
	public String requestMethod;
	public int mark;		//标注是哪个对象返回的数据
	public String eTag;
	public String jsonName;
	public String host;
	
}
