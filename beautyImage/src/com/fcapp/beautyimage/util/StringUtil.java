package com.fcapp.beautyimage.util;

import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

public class StringUtil {
	
	
	public final static String MD5(String s) {
	     try {
	      byte[] btInput = s.getBytes();
	      MessageDigest mdInst = MessageDigest.getInstance("MD5");
	      mdInst.update(btInput);
	      byte[] md = mdInst.digest();
	      StringBuffer sb = new StringBuffer();
	      for (int i = 0; i < md.length; i++) {
	       int val = (md[i]) & 0xff;
	       if (val < 16)
	        sb.append("0");
	       sb.append(Integer.toHexString(val));
	     
	      }
	      return sb.toString();
	     } catch (Exception e) {
	      return null;
	     }
	 }
	
	public static boolean checkEmail(String email){
		Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
		 Matcher matcher = pattern.matcher(email);
		 return matcher.matches();
	}
	
	/**
	 * true if str is null or zero length
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str){
		return TextUtils.isEmpty(str);
	}
}
