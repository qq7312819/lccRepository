package com.fcapp.beautyimage.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.fcapp.beautyimage.R;
import com.fcapp.beautyimage.appdata.Constant;
import com.fcapp.beautyimage.model.RequestModel;

@TargetApi(8)
public class NetUtil {
	
	private final static String Loggerin ="api";
	private final static String password ="fightclubapi2013";
	
	private static SharedPreferences sp;
	private static Editor edit;
	public static boolean isWifi(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkINfo = cm.getActiveNetworkInfo();
		if (networkINfo != null && networkINfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}
	
	public static Object head(RequestModel model){
		try {
			
			String popupHost = model.host;
			String host ;
			if(TextUtils.isEmpty(popupHost)){
				host = model.context.getString(R.string.app_host);
			}else{
				host = popupHost;
			}
			model.host = host;
			String requestUrl = model.host.concat(model.context.getString(model.requestUrl));
			if(model.eTag.equals(model.context.getString(R.string.no_need_eTag))){
				Object obj = null;
				if(model.requestMethod.equals(Constant.GET)){
					obj = get(model);
				}else{
					obj = post(model);
				}
				return obj;
			}
//			Authenticator.setDefault(new Authenticator() {
//			        protected PasswordAuthentication getPasswordAuthentication() {
//			            return new PasswordAuthentication (Loggerin, password.toCharArray());
//			        }
//			    });
			Logger.i("NetUtil", "执行完了身份验证的代码");
			URL url = new URL(requestUrl);
			Logger.e("head请求发送的URL是：", url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("HEAD");
			conn.setRequestProperty(Constant.ETAG, model.eTag);
			conn.setRequestProperty("", model.eTag);
			
			conn.setRequestProperty("Authorization", "Basic " +Base64.encodeToString("api:fightclubapi2013".getBytes(), Base64.NO_WRAP));
			
			String eTagFromServer = conn.getHeaderField(Constant.ETAG);
			
				if(eTagFromServer == null){
//					Logger.d("NetUtil Header请求返回的", "eTagFromServer == null");
//					return null;
				}
			
			
//			Logger.e("NetUtil  head请求返回的数据:", eTagFromServer+"   本地的是："+model.eTag);
			Object obj = null;
			if(model.eTag.equals(eTagFromServer)){
				//从本地获取
//				Logger.e("head 请求", "本地加载");
				String result = FileUtil.getFileContent(model.jsonName);
				
				if(Constant.FILE_NOT_FOUND.equals(result)){
					//从网络获取//如果本地被删掉
//					Logger.e("head 请求", "本地json文件被删掉,从网络加载");
					if(model.requestMethod.equals(Constant.GET)){
						obj = get(model);
					}else{
						obj = post(model);
					}
					//保存etag
					sp = model.context.getSharedPreferences("eTagConfig", model.context.MODE_PRIVATE);
					edit = sp.edit();
					edit.putString(model.jsonName, eTagFromServer);
					edit.commit();
				}else{
					obj = model.jsonParser.parseJSON(result);
				}
				
				return obj;
			}else{
				//保存etag
				sp = model.context.getSharedPreferences("eTagConfig", model.context.MODE_PRIVATE);
				edit = sp.edit();
				edit.putString(model.jsonName, eTagFromServer);
				edit.commit();
				//从网络获取
				Logger.e("head 请求", "从网络加载");
				if(model.requestMethod.equals(Constant.GET)){
					obj = get(model);
				}else{
					obj = post(model);
				}
				return obj;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static Object post(RequestModel model){
		
//		Authenticator.setDefault(new Authenticator() {
//	        protected PasswordAuthentication getPasswordAuthentication() {
//	            return new PasswordAuthentication (Loggerin, password.toCharArray());
//	        }
//	    });
		DefaultHttpClient client = new DefaultHttpClient();
		String url = model.host.concat(model.context.getString(model.requestUrl));
		Logger.e("NetUtil里面的post的URL",url);
		HttpPost post = new HttpPost(url);
		post.setHeader("Authorization", "Basic " + Base64.encodeToString((Loggerin+":"+password).getBytes(), Base64.NO_WRAP));
		HttpParams params = new BasicHttpParams();
		params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 8000);
        HttpConnectionParams.setSoTimeout(params, 8000);
		post.setParams(params);
		
		Object obj = null;
		try {
            if (model.requestDataMap != null) {
            	HashMap<String, String> map = model.requestDataMap;
                List<BasicNameValuePair> pairList = new ArrayList<BasicNameValuePair>();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    BasicNameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue());
                    pairList.add(pair);
                }
                HttpEntity entity = new UrlEncodedFormEntity(pairList, "UTF-8");
                post.setEntity(entity);
            }

            HttpResponse response = client.execute(post);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    String result = EntityUtils.toString(response.getEntity(), "UTF-8");
                    Logger.e(NetUtil.class.getSimpleName()+"1", result);
                    try {
                        obj = model.jsonParser.parseJSON(result);
                    } catch (Exception e) {
                        Log.e(NetUtil.class.getSimpleName()+"2", e.getLocalizedMessage(), e);
                    }
                return obj;
            }
        } catch (ClientProtocolException e) {
            Log.e(NetUtil.class.getSimpleName()+"5", e.getLocalizedMessage(), e);
        } catch (IOException e) {
            Log.e(NetUtil.class.getSimpleName()+"6", e.getLocalizedMessage(), e);
        }
        return null;
		
	}
	
	
	/**
	 * 调用的时候要判断是否为空
	 * @param model
	 * @return
	 */
	
	 public static Object get(RequestModel model) {
//		 Authenticator.setDefault(new Authenticator() {
//		        protected PasswordAuthentication getPasswordAuthentication() {
//		            return new PasswordAuthentication (Loggerin, password.toCharArray());
//		        }
//		    });
	        DefaultHttpClient client = new DefaultHttpClient();
	        String url = model.host.concat(model.context.getString(model.requestUrl));
	        StringBuffer  sb = new StringBuffer();
	        sb.append(url);
	        String afterSign = addGetParams(model.requestDataMap);
	        sb.append(afterSign);
	        Logger.e("get请求方法", "最后的请求地址是 == "+sb.toString());
	        String u = sb.toString();
	        u = u.replace(" ", "%20");
	        HttpGet get = new HttpGet(u);
	        get.setHeader("Authorization", "Basic " + Base64.encodeToString((Loggerin+":"+password).getBytes(), Base64.NO_WRAP));
	        HttpParams params = new BasicHttpParams();
			params = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(params, 8000);   
	        HttpConnectionParams.setSoTimeout(params, 8000);
	        get.setParams(params);
	        Object obj = null;
	        try {
	            HttpResponse response = client.execute(get);
	            
	            if(response == null){
	            	Logger.e("get", "response == null");
	            }
	            
	            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	                String result = EntityUtils.toString(response.getEntity(), "UTF-8");
	                Logger.i(NetUtil.class.getSimpleName()+"get3", result);
	                try {
	                    obj = model.jsonParser.parseJSON(result);
	                } catch (JSONException e) {
	                    Log.e(NetUtil.class.getSimpleName()+"4", e.getLocalizedMessage(), e);
	                }
	                return obj;
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return null;
	    }
	 
	 
	 private static String addGetParams(Map<String, String> map){
		 int j = 0;
		 List<BasicNameValuePair> pairList = new ArrayList<BasicNameValuePair>();
	        for (Map.Entry<String, String> entry : map.entrySet()) {
	        	if(j==0){
	        		j++;
	        		BasicNameValuePair pair = new BasicNameValuePair("?"+entry.getKey(), entry.getValue());
	                pairList.add(pair);
	        	}else{
	        		BasicNameValuePair pair = new BasicNameValuePair("&"+entry.getKey(), entry.getValue());
	                pairList.add(pair);
	        	}
	        }
	        
	        StringBuffer sb1 = new StringBuffer();
	        for(int i=0;i<pairList.size();i++){
	        	sb1.append(pairList.get(i));
	        }
	        return sb1.toString();
		}
	 
	 /**
	  * 判断网络是否可用
	  * @param context
	  * @return
	  */
	 public static boolean hasNetwork(Context context) {
	        ConnectivityManager con = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo workinfo = con.getActiveNetworkInfo();
	        if (workinfo == null || !workinfo.isAvailable()) {
	            Toast.makeText(context, R.string.net_error, 0).show();
	            return false;
	        }
	        return true;
	    }
	 
	 public static String convertStreamToString(InputStream is) {      
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is));      
	        StringBuilder sb = new StringBuilder();      
	       
	        String line = null;      
	        try {      
	            while ((line = reader.readLine()) != null) {  
	                sb.append(line + "\n");      
	            }      
	        } catch (IOException e) {      
	            e.printStackTrace();      
	        } finally {      
	            try {      
	                is.close();      
	            } catch (IOException e) {      
	               e.printStackTrace();      
	            }      
	        }      
	        return sb.toString();      
	    }
	 
//	 private void showSetNetWorkDiaLogger() {
//			AlertDiaLogger.Builder builder = new Builder(this);
//			builder.setTitle("设置网络");
//			builder.setMessage("网络连接错误,请检查网络设置");
////			builder.setPositiveButton("设置网络", new OnClickListener() {
////				
////				public void onClick(DiaLoggerInterface diaLogger, int which) {
////					//cmp=com.android.settings/.WirelessSettings
////					Intent intent = new Intent();
////					intent.setClassName("com.android.settings","com.android.settings.WirelessSettings");
////					startActivity(intent);
////					
////				}
////			});
//			builder.setNegativeButton("确定",  new OnClickListener() {
//				
//				public void onClick(DiaLoggerInterface diaLogger, int which) {
//					
//				}
//			});
//			builder.create().show();
//		}
	 
	 /**
	     * 判断手机网络是否可用
	     * @return
	     */
	    public static boolean isNetWorkAvailable(Context context){
	    	boolean result = false;
	    	ConnectivityManager cm  = (ConnectivityManager) context.getSystemService("connectivity");
	    	NetworkInfo netinfo = cm.getActiveNetworkInfo();
	    	if(netinfo!=null){
	    		result =	netinfo.isConnected();
	    	}
	    	return result;
	    }
	    
	   
}
