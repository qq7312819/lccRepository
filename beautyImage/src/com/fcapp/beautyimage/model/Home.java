package com.fcapp.beautyimage.model;

import java.io.Serializable;

public class Home implements Serializable{

	private String id;
	private String name;
	private String type;
	private MyImage cover_img;
	private long updated_at;
	private String img_local_dir;
	private String title;
//	private String mark;
	
	private String stack_id;
	private String text;
	private int view_count;
	private int like_count;
	private MyImage file;
	private boolean isCollect;
	
	public boolean isCollect() {
		return isCollect;
	}
	public void setCollect(boolean isCollect) {
		this.isCollect = isCollect;
	}
	public String getStack_id() {
		return stack_id;
	}
	public void setStack_id(String stack_id) {
		this.stack_id = stack_id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public int getView_count() {
		return view_count;
	}
	public void setView_count(int view_count) {
		this.view_count = view_count;
	}
	public int getLike_count() {
		return like_count;
	}
	public void setLike_count(int like_count) {
		this.like_count = like_count;
	}
	public MyImage getFile() {
		return file;
	}
	public void setFile(MyImage file) {
		this.file = file;
	}
//	public String getMark() {
//		return mark;
//	}
//	public void setMark(String mark) {
//		this.mark = mark;
//	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getImg_local_dir() {
		return img_local_dir;
	}
	public void setImg_local_dir(String img_local_dir) {
		this.img_local_dir = img_local_dir;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public MyImage getCover_img() {
		return cover_img;
	}
	public void setCover_img(MyImage cover_img) {
		this.cover_img = cover_img;
	}
	public long getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(long updated_at) {
		this.updated_at = updated_at;
	}
	@Override
	public String toString() {
		return "Home [id=" + id + ", name=" + name + ", type=" + type
				+ ", cover_img=" + cover_img + ", updated_at=" + updated_at
				+ ", img_local_dir=" + img_local_dir + ", title=" + title
				+ ", stack_id=" + stack_id + ", text=" + text + ", view_count="
				+ view_count + ", like_count=" + like_count + ", file=" + file
				+ "]";
	}
	
	
	
}
