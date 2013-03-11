package com.fcapp.beautyimage.model;

import java.io.Serializable;

public class MyImage implements Serializable{
	private String src;
	private int width;
	private int height;
	private String type;
	
	public String getType() {
		return type;
	}
	@Override
	public String toString() {
		return "MyImage [src=" + src + ", width=" + width + ", height="
				+ height + ", type=" + type + "]";
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
}
