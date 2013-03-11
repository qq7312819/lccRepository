package com.fcapp.beautyimage.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class StreamTools {

	
	/**
	 * 把inputstream里面的内容读取出来,存放到一个byte[]返回
	 */
	
	
	public static byte[] getBytes(InputStream is) throws Exception{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len =0;
		while((len =is.read(buffer))!=-1){
			
			baos.write(buffer, 0, len);
			
		}
		is.close();
		baos.flush();
		return baos.toByteArray();
	}
}
