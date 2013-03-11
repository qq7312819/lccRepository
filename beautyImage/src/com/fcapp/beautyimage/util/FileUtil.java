package com.fcapp.beautyimage.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import android.widget.Toast;

import com.fcapp.beautyimage.appdata.Constant;

public class FileUtil {
	
	
	public static boolean createFile(String destFileName) {
		File file = new File(destFileName);	// 根据指定的文件名创建File对象
		if (file.exists()) {					// 判断该文件是否存在
//			System.out.println("创建单个文件" + destFileName + "失败，目标文件已存在！");
			return false;				// 如果存在，则不需创建则返回fasle
		}
		if (destFileName.endsWith(File.separator)) {// 如果传入的文件名是以文件分隔符结尾的，则说明此File对象是个目录而不是文件
//			System.out.println("创建单个文件" + destFileName + "失败，目标文件不能为目录！");
			return false;// 因为不是文件所以不可能创建成功，则返回false
		}
		// 判断目标文件所在的目录是否存在
		if (!file.getParentFile().exists()) {
			// 如果目标文件所在的文件夹不存在，则创建父文件夹
//			System.out.println("创建" + file.getName() + "所在目录不存在，正在创建！它的父目录是："+file.getParentFile().getAbsolutePath());
			if (!file.getParentFile().mkdirs()) {// 判断父文件夹是否存在，如果存在则表示创建成功否则不成功
				System.out.println("创建目标文件所在的目录失败！");
				return false;
			}
		}
		// 创建目标文件
		try {
			if (file.createNewFile()) {// 调用createNewFile方法，判断创建指定文件是否成功
//				System.out.println("创建单个文件" + destFileName + "成功！");
				return true;
			} else {
//				System.out.println("创建单个文件" + destFileName + "失败！");
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
//			System.out.println("创建文件" + destFileName + "失败！" + e.getMessage());
			return false;
		}
	}
	
	
	public static boolean writeFile(String str,String filePath){
		boolean isExist = SystemInfo.existSDcard();
		if(!isExist){
			return false;
		}
		File file = new File(filePath);
		if(!file.exists()){
			createFile(filePath);
		}
		
		FileWriter fw = null;
		try
		{
			fw = new FileWriter(filePath , false);
			fw.write(str);//如果写入回车换行符，在window中需要 \r\n 来完成。
		}
		catch (IOException e)
		{
//			throw new RuntimeException("写入失败");
		}
		finally
		{
			try
			{
				if(fw!=null)
					fw.close();
			}
			catch (IOException e)
			{
				throw new RuntimeException("关闭失败");
			}
		}
		return false;
	}
	
	
	public static String getFileContent(String local_dir){
		
		try {
			File file = new File(local_dir);
			if(!file.exists()){
				return Constant.FILE_NOT_FOUND;
			}
			InputStream fis = new FileInputStream(file);
			
			byte[] b = StreamTools.getBytes(fis);
			String json = new String(b,"UTF-8");
			
			return json;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static boolean deleteFile(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				if (f != null) {
					if (f.isDirectory()) {
						deleteFile(f);
					}
//					System.out.println("删除文件");
					f.delete();
				}
			}
			return file.delete();
		} else {
			return file.delete();

		}
	}
}
